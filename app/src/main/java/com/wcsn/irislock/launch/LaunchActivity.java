package com.wcsn.irislock.launch;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.ImaginationUnlimited.GifKeyboard.gifkeycommon.sp.SPModel;
import com.ImaginationUnlimited.library.app.BaseActivity;
import com.ImaginationUnlimited.library.utils.app.StringUtils;
import com.ImaginationUnlimited.library.utils.log.Logger;
import com.ImaginationUnlimited.library.utils.view.ViewFinder;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.vilyever.socketclient.SocketClient;
import com.vilyever.socketclient.helper.SocketClientAddress;
import com.vilyever.socketclient.helper.SocketClientDelegate;
import com.vilyever.socketclient.helper.SocketPacket;
import com.vilyever.socketclient.helper.SocketResponsePacket;
import com.wcsn.irislock.R;
import com.wcsn.irislock.home.MainActivity;
import com.wcsn.irislock.login.AdminOrVisitorActivity;
import com.wcsn.irislock.settings.ImagePwdActivity;
import com.wcsn.irislock.settings.NumberPwdActivity;
import com.wcsn.irislock.utils.SocketUtils;
import com.wcsn.irislock.utils.image.ImageLoaderFactory;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by suiyue on 2016/6/7 0007.
 */
public class LaunchActivity extends BaseActivity {

    private static final int STANDING_TIME = 2000;
    private Subscription mSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        ViewFinder finder = new ViewFinder(LaunchActivity.this);
        SimpleDraweeView draweeView = finder.find(R.id.launchLogo);
        GenericDraweeHierarchy hierarchy = GenericDraweeHierarchyBuilder.newInstance(getResources())
                .setActualImageScaleType(ScalingUtils.ScaleType.FIT_XY)
                .build();
        draweeView.setHierarchy(hierarchy);
        ImageLoaderFactory.getLoader(draweeView).showImage(draweeView, "res:///" + R.drawable.bg_launch, null);


        if (StringUtils.isNullOrEmpty(SPModel.getDeviceId())) {
//            new SocketThread().run();
            SocketUtils.getDeviceId();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        mSubscription = Observable.timer(STANDING_TIME, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        if (SPModel.getAdmin()) {
                            AdminOrVisitorActivity.launch(LaunchActivity.this);
                        } else if (StringUtils.isNullOrEmpty(SPModel.getPwd())){
                            MainActivity.launch(LaunchActivity.this);
                        } else {
                            if (SPModel.getPwdType() == SPModel.PWD_TYPE_IMAGE) {
                                ImagePwdActivity.launch(LaunchActivity.this, 2);
                            } else {
                                NumberPwdActivity.launch(LaunchActivity.this, 2);
                            }
                        }

                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    class SocketThread extends Thread {

        @Override
        public void run() {

            Logger.e("SocketThread");

            SocketClient socketClient = new SocketClient(new SocketClientAddress(SPModel.getSocketIp(), SPModel.getSocketPort(), 5*1000));

            socketClient.setCharsetName("UTF-8");
            socketClient.getSocketPacketHelper().setSendHeaderData(null); // 设置发送消息时自动在消息头部添加的信息，远程端收到此信息后表示一条消息开始，用于解决粘包分包问题，若为null则不添加头部信息
            socketClient.getSocketPacketHelper().setSendTrailerString(null);

            socketClient.registerSocketClientDelegate(new SocketClientDelegate() {
                @Override
                public void onConnected(SocketClient socketClient) {
                    Logger.e("Socket 已连接");
                    SocketPacket packet = socketClient.sendString("FF");
                    packet = socketClient.sendString("EXIT");
                }

                @Override
                public void onDisconnected(SocketClient socketClient) {

                    Logger.e("Socket 连接断开");

                }

                @Override
                public void onResponse(SocketClient socketClient, @NonNull SocketResponsePacket socketResponsePacket) {
                    String message = socketResponsePacket.getMessage();
                    if (message == null) {
                        Logger.e("message = null");
                    } else if (message.equals("success")){
                        Logger.e("message = success");

                        socketClient.disconnect();
                    } else {
                        Logger.e("message = " + message);
                        SPModel.putDevieceId(message);
                        JPushInterface.setAlias(getOwnerActivity(), message, new TagAliasCallback() {
                            @Override
                            public void gotResult(int i, String s, Set<String> set) {
                                if (i == 0) {
                                    Logger.e("别名设置成功");
                                }
                            }
                        });
                    }
                }
            });

            socketClient.connect();
        }
    }
}
