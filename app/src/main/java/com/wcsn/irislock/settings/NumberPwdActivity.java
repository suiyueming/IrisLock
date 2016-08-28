package com.wcsn.irislock.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ImaginationUnlimited.GifKeyboard.gifkeycommon.sp.SPModel;
import com.ImaginationUnlimited.library.app.BaseActivity;
import com.ImaginationUnlimited.library.utils.app.StringUtils;
import com.ImaginationUnlimited.library.utils.toast.ToastUtils;
import com.ImaginationUnlimited.library.utils.view.ViewFinder;
import com.wcsn.irislock.R;
import com.wcsn.irislock.home.MainActivity;

/**
 * Created by suiyue on 2016/8/17 0017.
 */
public class NumberPwdActivity extends BaseActivity {

    private static final String IS_CHANGE_MODEL = "changeModel";

    private TextView mHintText;
    private EditText mFirstEdit;
    private EditText mSecondEdit;
    private EditText mThirdEdit;
    private EditText mFourthEdit;

    private ImageView mBack;

    private int mChangeModel;


    public static void launch(BaseActivity activity){
        launch(activity, 0);
    }

    public static void launch(BaseActivity activity, int changeModel){
        Intent intent = new Intent(activity,NumberPwdActivity.class);
        intent.putExtra(IS_CHANGE_MODEL, changeModel);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_pwd);

        ViewFinder finder = new ViewFinder(this);

        mHintText = finder.find(R.id.numberPwdHint);

        mFirstEdit = finder.find(R.id.first);
        mSecondEdit = finder.find(R.id.second);
        mThirdEdit = finder.find(R.id.third);
        mFourthEdit = finder.find(R.id.fourth);

        mFirstEdit.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        mSecondEdit.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        mThirdEdit.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        mFourthEdit.setInputType(EditorInfo.TYPE_CLASS_NUMBER);

        mFirstEdit.addTextChangedListener(tw);
        mSecondEdit.addTextChangedListener(tw);
        mThirdEdit.addTextChangedListener(tw);
        mFourthEdit.addTextChangedListener(tw);

        mChangeModel = getIntent().getIntExtra(IS_CHANGE_MODEL, 0);


        if (mChangeModel != 2) {
            if (SPModel.getPwdType() == SPModel.PWD_TYPE_NUM) {
                mHintText.setText(getResources().getString(R.string.inputPwdOld));
            } else if (SPModel.getPwdType() == SPModel.PWD_TYPE_IMAGE) {
                ImagePwdActivity.launch(this, 1);
            }
        }

        mBack = finder.find(R.id.back);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SPModel.getPwdType() == SPModel.PWD_TYPE_NONE) {
                    SPModel.putPwd(null);
                }
                finish();
            }
        });

        mFirstEdit.setFocusable(true);



        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

    }


    TextWatcher tw = new TextWatcher(){
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,int after){
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count){
        }
        @Override
        public void afterTextChanged(Editable s){
            if(s.toString().length() == 1) {
                if(mFirstEdit.isFocused()) {
                    mFirstEdit.clearFocus();
                    mSecondEdit.requestFocus();

                } else if(mSecondEdit.isFocused()) {
                    mSecondEdit.clearFocus();
                    mThirdEdit.requestFocus();
                } else if(mThirdEdit.isFocused()) {
                    mThirdEdit.clearFocus();
                    mFourthEdit.requestFocus();
                }
                if (isFinish()) {
                    String pwd = mFirstEdit.getText().toString() + mSecondEdit.getText().toString()
                            +mThirdEdit.getText().toString() + mFourthEdit.getText().toString();
                    if (mChangeModel == 2) {
                        if (pwd.equals(SPModel.getPwd())) {
                            MainActivity.launch(NumberPwdActivity.this);
                        } else {
                            mHintText.setText("密码错误，请重新输入");
                        }

                    } else if (mHintText.getText().toString()
                            .equals(getResources().getString(R.string.inputPwd))) {


                        SPModel.putPwd(pwd);

                        mHintText.setText(getResources().getString(R.string.inputPwdAgain));
                        mFourthEdit.clearFocus();
                        mFirstEdit.requestFocus();
                        mFirstEdit.setText("");
                        mSecondEdit.setText("");
                        mThirdEdit.setText("");
                        mFourthEdit.setText("");
                    } else if (mHintText.getText().toString()
                            .equals(getResources().getString(R.string.inputPwdAgain))){
                        if (pwd.equals(SPModel.getPwd())) {
                            SPModel.putPwdType(SPModel.PWD_TYPE_NUM);
                            ToastUtils.toastShort("密码设置成功");
                            finish();

                        } else {
                            SPModel.putPwdType(SPModel.PWD_TYPE_NONE);
                            ToastUtils.toastShort("两次密码不一致，请重新输入");
                            mHintText.setText(getResources().getString(R.string.inputPwd));
                            mFourthEdit.clearFocus();
                            mFirstEdit.requestFocus();
                            mFirstEdit.setText("");
                            mSecondEdit.setText("");
                            mThirdEdit.setText("");
                            mFourthEdit.setText("");
                        }
                    } else {
                        if (pwd.equals(SPModel.getPwd()) && mChangeModel==0) {
                            mHintText.setText(getResources().getString(R.string.inputPwd));
                            mFourthEdit.clearFocus();
                            mFirstEdit.requestFocus();
                            mFirstEdit.setText("");
                            mSecondEdit.setText("");
                            mThirdEdit.setText("");
                            mFourthEdit.setText("");
                        } else if (pwd.equals(SPModel.getPwd()) && mChangeModel==1) {

                            SPModel.putPwdType(SPModel.PWD_TYPE_NONE);
//                            ImagePwdActivity.launch(getOwnerActivity());
                            finish();

                        }else {
                            ToastUtils.toastShort("密码错误，请重新输入");
                            mFourthEdit.clearFocus();
                            mFirstEdit.requestFocus();
                            mFirstEdit.setText("");
                            mSecondEdit.setText("");
                            mThirdEdit.setText("");
                            mFourthEdit.setText("");

                        }
                    }

                }
            }
        }
    };

    public boolean isFinish() {
        if (!StringUtils.isNullOrEmpty(mFirstEdit.getText().toString())
                && !StringUtils.isNullOrEmpty(mSecondEdit.getText().toString())
                && !StringUtils.isNullOrEmpty(mThirdEdit.getText().toString())
                && !StringUtils.isNullOrEmpty(mFourthEdit.getText().toString())) {
            return true;
        }
        return false;
    }



}
