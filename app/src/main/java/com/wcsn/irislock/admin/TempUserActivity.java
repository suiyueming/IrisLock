package com.wcsn.irislock.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ImaginationUnlimited.library.app.BaseActivity;
import com.ImaginationUnlimited.library.app.mvp.BaseMVPActivity;
import com.ImaginationUnlimited.library.app.mvp.IUI;
import com.ImaginationUnlimited.library.utils.log.Logger;
import com.ImaginationUnlimited.library.utils.network.NetworkUtils;
import com.ImaginationUnlimited.library.utils.toast.ToastUtils;
import com.ImaginationUnlimited.library.utils.view.ViewFinder;
import com.wcsn.irislock.R;
import com.wcsn.irislock.admin.bean.TempUserInfo;
import com.wcsn.irislock.admin.bean.UserInfo;
import com.wcsn.irislock.app.App;

import java.util.Calendar;

import cn.qqtheme.framework.picker.DatePicker;
import cn.qqtheme.framework.picker.TimePicker;

/**
 * Created by suiyue on 2016/7/8 0008.
 */
public class TempUserActivity extends BaseMVPActivity<TempUserPresenter> implements ITempUserUI{


    private RadioGroup mChooseSexGroup;
    private EditText mNameEdit;
    private CheckBox mNameCheck;
    private EditText mPhoneEdit;
    private CheckBox mPhoneCheck;
    private Button mRegisterButton;

    private TextView mStartDateText;
    private TextView mStopDateText;

    private TextView mStartTimeText;
    private TextView mStopTimeText;

    private RelativeLayout mValidityLayout;

    private RelativeLayout mRegisterLayout;
    private LinearLayout mWaitLayout;

    private TextView mEnterCount;
    private CheckBox mCheckBox;

    private CheckBox monCheck;
    private CheckBox tusCheck;
    private CheckBox wedCheck;
    private CheckBox thuCheck;
    private CheckBox friCheck;
    private CheckBox satCheck;
    private CheckBox sunCheck;

    private UserInfo mUserInfo = new UserInfo();

    private int[] week = new int[7];

    private TempUserInfo mTempUserInfo = new TempUserInfo();

    private String sex = "male";

    private ImageView mBackView;
    private ImageView mAddUserView;

    private View mShadow;


    public static void launch(BaseActivity activity) {
        Intent intent = new Intent(activity, TempUserActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreateExecute(Bundle savedInstanceState) {

        setContentView(R.layout.activity_add_temp_user);
        ViewFinder finder = new ViewFinder(this);

        mBackView = finder.find(R.id.back);
        mBackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mShadow = finder.find(R.id.shadow);

        mAddUserView = finder.find(R.id.addUser);
        mAddUserView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdminActivity.chooseUserType(v, getOwnerActivity(), mShadow);
            }
        });


        mNameEdit = finder.find(R.id.nameEdit);
        mNameCheck = finder.find(R.id.nameCheck);
        mPhoneEdit = finder.find(R.id.phoneEdit);
        mPhoneCheck = finder.find(R.id.phoneCheck);

        final Calendar c = Calendar.getInstance();
        mStartDateText = finder.find(R.id.startDate);
        mStartDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePicker datePicker = new DatePicker(getOwnerActivity());
                datePicker.setLabel("", "", "");
                datePicker.setTitleText("开始时间");
                datePicker.setCancelText("返回");
                datePicker.setSubmitText("下一步");
                datePicker.setRange(c.get(Calendar.YEAR), c.get(Calendar.YEAR) + 50);
                datePicker.setSelectedItem(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
                datePicker.setOnDatePickListener(new DatePicker.OnYearMonthDayPickListener() {
                    @Override
                    public void onDatePicked(String year, String month, String day) {
                        mStartDateText.setText(year + "-" + month + "-" + day);
                    }
                });
                datePicker.show();
            }
        });
        mStopDateText = finder.find(R.id.stopDate);
        mStopDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePicker datePicker = new DatePicker(getOwnerActivity());
                datePicker.setLabel("", "", "");
                datePicker.setTitleText("结束时间");
                datePicker.setCancelText("返回");
                datePicker.setSubmitText("下一步");
                datePicker.setRange(c.get(Calendar.YEAR), c.get(Calendar.YEAR) + 50);
                datePicker.setSelectedItem(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
                datePicker.setOnDatePickListener(new DatePicker.OnYearMonthDayPickListener() {
                    @Override
                    public void onDatePicked(String year, String month, String day) {
                        mStopDateText.setText(year + "-" + month + "-" + day);
                    }
                });
                datePicker.show();
            }
        });

        mStartTimeText = finder.find(R.id.startTime);
        mStartTimeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePicker timePicker = new TimePicker(getOwnerActivity(), TimePicker.HOUR_OF_DAY, false);
                timePicker.setTitleText("开始时间");
                timePicker.setCancelText("返回");
                timePicker.setSubmitText("下一步");
                timePicker.setLabel("", "");
                timePicker.setOnTimePickListener(new TimePicker.OnTimePickListener() {
                    @Override
                    public void onTimePicked(String hour, String minute) {
                        if (hour.length() == 1) {
                            hour = "0" + hour;
                        }
                        mStartTimeText.setText(hour);
                    }
                });
                timePicker.show();
            }
        });
        mStopTimeText = finder.find(R.id.stopTime);
        mStopTimeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePicker timePicker = new TimePicker(getOwnerActivity(), TimePicker.HOUR_OF_DAY,false);
                timePicker.setTitleText("结束时间");
                timePicker.setCancelText("返回");
                timePicker.setSubmitText("下一步");
                timePicker.setLabel("", "");
                timePicker.setOnTimePickListener(new TimePicker.OnTimePickListener() {
                    @Override
                    public void onTimePicked(String hour, String minute) {
                        if (hour.length() == 1) {
                            hour = "0" + hour;
                        }
                        Logger.e("hour = " + hour);
                        mStopTimeText.setText(hour);
                    }
                });
                timePicker.show();
            }
        });

        mChooseSexGroup = finder.find(R.id.chooseSex);
        mChooseSexGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.maleRadio) {
                    sex = "male";
                } else {
                    sex = "female";
                }
            }
        });

        mValidityLayout = finder.find(R.id.validityLayout);

        mRegisterButton = finder.find(R.id.register);
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkUtils.getWifiSSID(getBaseContext()).startsWith(App.mWifiName)) {


                    mTempUserInfo.setName(mNameEdit.getText().toString());
                    mUserInfo.setPhone(mPhoneEdit.getText().toString());
                    mUserInfo.setSex(sex);
                    mTempUserInfo.setStartDate(mStartDateText.getText().toString());
                    mTempUserInfo.setStopDate(mStopDateText.getText().toString());
                    mTempUserInfo.setStartTime(mStartTimeText.getText().toString());
                    mTempUserInfo.setStopTime(mStopTimeText.getText().toString());
                    mTempUserInfo.setUserInfo(mUserInfo);
                    StringBuilder sb = new StringBuilder("");
                    for (int i=0; i<week.length; i++) {
                        sb.append(week[i]);
                    }
                    mTempUserInfo.setWeek(sb.toString());
                    getPresenter().registerTemp(mTempUserInfo);
                } else {
                    ToastUtils.toastShort("请连接设备WiFi");
                }


            }
        });

        mRegisterLayout = finder.find(R.id.registerLayout);
        mWaitLayout = finder.find(R.id.waitLayout);

        mEnterCount = finder.find(R.id.enterCount);
        mCheckBox = finder.find(R.id.checkbox);

        monCheck = finder.find(R.id.monCheck);
        monCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Logger.e("monClick");
                week[0] = isChecked ? 1 : 0;
            }
        });

        tusCheck = finder.find(R.id.tusCheck);
        tusCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                week[1] = isChecked ? 1 : 0;
            }
        });
        wedCheck = finder.find(R.id.wedCheck);
        wedCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                week[2] = isChecked ? 1 : 0;
            }
        });
        thuCheck = finder.find(R.id.thuCheck);
        thuCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                week[3] = isChecked ? 1 : 0;
            }
        });
        friCheck = finder.find(R.id.friCheck);
        friCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                week[4] = isChecked ? 1 : 0;
            }
        });
        satCheck = finder.find(R.id.satCheck);
        satCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                week[5] = isChecked ? 1 : 0;
            }
        });
        sunCheck = finder.find(R.id.sunCheck);
        sunCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                week[6] = isChecked ? 1 : 0;
            }
        });



        getPresenter().bindWatcher();

    }

    @Override
    protected TempUserPresenter createPresenter() {
        return new TempUserPresenter();
    }

    @Override
    protected IUI getUI() {
        return this;
    }

    @Override
    public EditText getNameEdit() {
        return mNameEdit;
    }

    @Override
    public EditText getPhoneEdit() {
        return mPhoneEdit;
    }

    @Override
    public void changeNameCheck(boolean isChecked) {
        mNameCheck.setChecked(isChecked);
    }

    @Override
    public void changePhoneCheck(boolean isChecked) {
        mPhoneCheck.setChecked(isChecked);
    }

    @Override
    public boolean isWholeInfo() {
        if (mPhoneCheck.isChecked() && mNameCheck.isChecked()) {
            return true;
        }
        return false;
    }

    @Override
    public Button getRegisterButton() {
        return mRegisterButton;
    }

    @Override
    public RelativeLayout getRegisterLayout() {
        return mRegisterLayout;
    }

    @Override
    public LinearLayout getWaitRegisterLayout() {
        return mWaitLayout;
    }

    @Override
    public TextView getText() {
        return mEnterCount;
    }

    @Override
    public CheckBox getCheck() {
        return mCheckBox;
    }

}
