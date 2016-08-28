package com.wcsn.irislock.settings;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ImaginationUnlimited.library.app.BaseActivity;
import com.ImaginationUnlimited.library.app.mvp.BaseMVPActivity;
import com.ImaginationUnlimited.library.app.mvp.IUI;
import com.ImaginationUnlimited.library.utils.log.Logger;
import com.ImaginationUnlimited.library.utils.toast.ToastUtils;
import com.ImaginationUnlimited.library.utils.view.ViewFinder;
import com.github.pwittchen.networkevents.library.BusWrapper;
import com.github.pwittchen.networkevents.library.NetworkEvents;
import com.github.pwittchen.networkevents.library.event.WifiSignalStrengthChanged;
import com.nineoldandroids.animation.ObjectAnimator;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.wcsn.irislock.R;
import com.wcsn.irislock.utils.SocketUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suiyue on 2016/8/16 0016.
 */
public class SettingsActivity extends BaseMVPActivity<SettingsPresenter> implements ISettingsUI {

    private BusWrapper busWrapper;
    private NetworkEvents networkEvents;

    private RelativeLayout mNetworkLayout;
    private RelativeLayout mPasswordLayout;
    private RelativeLayout mSoundLayout;
    private RelativeLayout mAboutLayout;

    private View mShadow;

    private ImageView mBack;

    private ListView mWiFiList;

    private ImageView mWiFiArrow;

    public static void launch(BaseActivity activity){
        Intent intent = new Intent(activity,SettingsActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreateExecute(Bundle savedInstanceState) {
        setContentView(R.layout.activity_settings);

        ViewFinder finder = new ViewFinder(this);

        mNetworkLayout = finder.find(R.id.wifiSetting);

        mWiFiArrow = finder.find(R.id.wifiArrow);

        mNetworkLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWiFiList.getVisibility() == View.GONE) {
                    rotateAnimRun(mWiFiArrow, 0, 90f);
                    mWiFiList.setVisibility(View.VISIBLE);
                } else {
                    rotateAnimRun(mWiFiArrow, 90f, 0);
                    mWiFiList.setVisibility(View.GONE);
                }
            }
        });
        mPasswordLayout = finder.find(R.id.passwordSetting);
        mPasswordLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShadow.setVisibility(View.VISIBLE);
                choosePwdWay(v, getOwnerActivity());
            }
        });

        mSoundLayout = finder.find(R.id.soundSetting);
        mAboutLayout = finder.find(R.id.about);
        mAboutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AboutActivity.launch(getOwnerActivity());
            }
        });

        mShadow = finder.find(R.id.shadow);

        mBack = finder.find(R.id.back);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mWiFiList = finder.find(R.id.WiFiList);
        busWrapper = getOttoBusWrapper(new Bus());
        networkEvents = new NetworkEvents(getApplicationContext(), busWrapper).enableInternetCheck()
                .enableWifiScan();

    }

    public void rotateAnimRun(View view, float agreeStart, float agreeStop) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(view, "rotation", agreeStart, agreeStop);
        anim.start();
    }

    @NonNull
    private BusWrapper getOttoBusWrapper(final Bus bus) {
        return new BusWrapper() {
            @Override public void register(Object object) {
                bus.register(object);
            }

            @Override public void unregister(Object object) {
                bus.unregister(object);
            }

            @Override public void post(Object event) {
                bus.post(event);
            }
        };
    }




    @Subscribe
    @SuppressWarnings("unused") public void onEvent(WifiSignalStrengthChanged event) {
        final List<String> wifiScanResults = new ArrayList<>();

        for (ScanResult scanResult : event.getWifiScanResults()) {
            wifiScanResults.add(scanResult.SSID);
        }

        int itemLayoutId = android.R.layout.simple_list_item_1;
        mWiFiList.setAdapter(new ArrayAdapter<>(this, itemLayoutId, wifiScanResults));

        mWiFiList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ToastUtils.toastShort(wifiScanResults.get(position));

                final String ssid = wifiScanResults.get(position);

                final Dialog dialog = new Dialog(getOwnerActivity(), R.style.MyDialogStyle);
                View contentView = LayoutInflater.from(getOwnerActivity()).inflate(R.layout.dialog_input_wifi_pwd,null);
                dialog.setContentView(contentView);
                dialog.setCanceledOnTouchOutside(true);

                ViewFinder finder = new ViewFinder(contentView);

                final EditText editText = finder.find(R.id.inputEdit);
                Button ok = finder.find(R.id.ok);
                Button cancel = finder.find(R.id.cancel);

                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String pwd = editText.getText().toString();
                        Logger.e(getClass().getSimpleName(), pwd);
                        SocketUtils.setWiFi(ssid, pwd);
                        dialog.dismiss();
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();

            }
        });
    }



    @Override
    protected SettingsPresenter createPresenter() {
        return new SettingsPresenter();
    }

    @Override
    protected IUI getUI() {
        return this;
    }

    @Override
    protected void onResume() {
        super.onResume();
        busWrapper.register(this);
        networkEvents.register();
    }

    @Override
    protected void onPause() {
        super.onPause();
        busWrapper.unregister(this);
        networkEvents.unregister();
    }

    public void choosePwdWay(View view, final Activity context) {
        View contentView = LayoutInflater.from(context).inflate(
                R.layout.popup_password_way, null);
        ViewFinder finder = new ViewFinder(contentView);

        TextView numberPwd = finder.find(R.id.numberPwd);
        TextView imagePwd = finder.find(R.id.imagePwd);
        TextView cancel = finder.find(R.id.cancel);

        final PopupWindow popupWindow = new PopupWindow(contentView,
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);


        numberPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShadow.setVisibility(View.GONE);
                NumberPwdActivity.launch(getOwnerActivity(),0);
                popupWindow.dismiss();
            }
        });

        imagePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShadow.setVisibility(View.GONE);
                ImagePwdActivity.launch(getOwnerActivity(),0);
                popupWindow.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mShadow.setVisibility(View.GONE);
                popupWindow.dismiss();

            }
        });
        popupWindow.setTouchable(true);
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        popupWindow.showAtLocation(view, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mShadow.setVisibility(View.GONE);
            }
        });
    }
}
