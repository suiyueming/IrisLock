package com.wcsn.irislock.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ImaginationUnlimited.GifKeyboard.gifkeycommon.sp.SPModel;
import com.ImaginationUnlimited.library.app.BaseActivity;
import com.ImaginationUnlimited.library.utils.log.Logger;
import com.ImaginationUnlimited.library.utils.toast.ToastUtils;
import com.ImaginationUnlimited.library.utils.view.ViewFinder;
import com.wcsn.irislock.R;
import com.wcsn.irislock.home.MainActivity;
import com.wcsn.irislock.settings.listener.OnPatternChangedListener;
import com.wcsn.irislock.settings.view.ImageLockView;

/**
 * Created by suiyue on 2016/8/17 0017.
 */
public class ImagePwdActivity extends BaseActivity implements OnPatternChangedListener{

    private TextView mHintText;
    private ImageView mBack;

    private ImageLockView mImageLockView;

    private static final String IS_CHANGE_MODEL = "changeModel";

    private int mChangeModel;

    public static void launch(BaseActivity activity){
        launch(activity, 0);
    }


    /**
     * 0：设置
     * 1：更改密码
     * 2：校对密码
     * @param activity
     * @param changeModel
     */
    public static void launch(BaseActivity activity, int changeModel){
        Intent intent = new Intent(activity,ImagePwdActivity.class);
        intent.putExtra(IS_CHANGE_MODEL, changeModel);
        activity.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_pwd);

        ViewFinder finder = new ViewFinder(this);

        mHintText = finder.find(R.id.numberPwdHint);
        mImageLockView = finder.find(R.id.imageLockView);
        mImageLockView.setOnPatternChangedListener(this);

        mBack = finder.find(R.id.back);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mChangeModel = getIntent().getIntExtra(IS_CHANGE_MODEL, 0);

        if (mChangeModel != 2) {
            if (SPModel.getPwdType() == SPModel.PWD_TYPE_IMAGE) {
                mHintText.setText(getResources().getString(R.string.inputPwdOld));
            } else if (SPModel.getPwdType() == SPModel.PWD_TYPE_NUM) {
                NumberPwdActivity.launch(this, 1);
            }
        }





    }

    @Override
    public void patternChanged(String password) {
        Logger.e(getClass().getSimpleName(), password);

        if (mChangeModel == 2) {
            if (password.equals(SPModel.getPwd())) {
                MainActivity.launch(ImagePwdActivity.this);
            } else {
                mHintText.setText("密码错误，请重新输入");
            }

        } else {
            if (mHintText.getText().toString()
                    .equals(getResources().getString(R.string.inputPwd))) {

                SPModel.putPwd(password);

                mHintText.setText(getResources().getString(R.string.inputPwdAgain));
                mImageLockView.reset();

            } else if (mHintText.getText().toString()
                    .equals(getResources().getString(R.string.inputPwdAgain))){

                if (password.equals(SPModel.getPwd())) {
                    SPModel.putPwdType(SPModel.PWD_TYPE_IMAGE);
                    ToastUtils.toastShort("密码设置成功");
                    finish();
                } else {
                    SPModel.putPwdType(SPModel.PWD_TYPE_NONE);
                    ToastUtils.toastShort("两次密码不一致，请重新输入");
                    mHintText.setText(getResources().getString(R.string.inputPwd));
                    mImageLockView.reset();
                }
            } else {
                if (password.equals(SPModel.getPwd()) && mChangeModel==0) {
                    mHintText.setText(getResources().getString(R.string.inputPwd));
                    mImageLockView.reset();
                } else if (password.equals(SPModel.getPwd()) && mChangeModel==1) {
                    SPModel.putPwdType(SPModel.PWD_TYPE_NONE);
                    finish();

                }else {
                    ToastUtils.toastShort("密码错误，请重新输入");
                    mImageLockView.reset();
                }
            }
        }


    }

    @Override
    public void patternStart(boolean start) {


    }

}
