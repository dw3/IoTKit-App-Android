package com.cylan.jiafeigou.n.view.login;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cylan.jiafeigou.R;
import com.cylan.jiafeigou.n.mvp.contract.login.RstPwdContract;
import com.cylan.jiafeigou.utils.ViewUtils;
import com.superlog.SLog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */
public class ResetPwdFragment extends android.support.v4.app.Fragment implements RstPwdContract.View {


    @BindView(R.id.cb_show_pwd)
    CheckBox cbShowPwd;
    @BindView(R.id.iv_new_clear_pwd)
    ImageView ivRstClearPwd;
    @BindView(R.id.tv_new_pwd_submit)
    TextView tvRstPwdSubmit;
    @BindView(R.id.et_new_pwd_input)
    EditText etRstPwdInput;

    private RstPwdContract.Presenter presenter;

    public ResetPwdFragment() {
        // Required empty public constructor
    }

    public static ResetPwdFragment newInstance(Bundle bundle) {
        ResetPwdFragment fragment = new ResetPwdFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public android.view.View onCreateView(LayoutInflater inflater, ViewGroup container,
                                          Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        android.view.View view = inflater.inflate(R.layout.fragment_set_new_pwd, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    /**
     * 密码变化
     *
     * @param s
     * @param start
     * @param before
     * @param count
     */
    @OnTextChanged(R.id.et_new_pwd_input)
    public void onPwdChange(CharSequence s, int start, int before, int count) {
        boolean flag = TextUtils.isEmpty(s);
        ivRstClearPwd.setVisibility(flag ? android.view.View.INVISIBLE : android.view.View.VISIBLE);
        final boolean submitEnable = !flag && s.length() >= 6;
        tvRstPwdSubmit.setEnabled(submitEnable);
    }


    @OnCheckedChanged(R.id.cb_show_pwd)
    public void onShowPwd(CompoundButton buttonView, boolean isChecked) {
        ViewUtils.showPwd(etRstPwdInput, isChecked);
        etRstPwdInput.setSelection(etRstPwdInput.length());
    }


    @OnClick({R.id.tv_new_pwd_submit, R.id.iv_new_clear_pwd})
    public void onClick(android.view.View view) {
        switch (view.getId()) {
            case R.id.tv_new_pwd_submit:
                if (isResumed() && getActivity() != null) {
                    ViewUtils.enableEditTextCursor(etRstPwdInput, false);
                }
                final String account =
                        getArguments() != null
                                ? getArguments().getString(LoginFragment.KEY_TEMP_ACCOUNT)
                                : "";
                SLog.d(account);
                if (presenter != null && !TextUtils.isEmpty(account)) {
                    presenter.executeSubmitNewPwd(account, etRstPwdInput.getText().toString());
                    Toast.makeText(getActivity(), "已提交", Toast.LENGTH_SHORT).show();
                    ViewUtils.enableEditTextCursor(etRstPwdInput, false);
                    ivRstClearPwd.setVisibility(View.INVISIBLE);
                } else {

                }
                break;
            case R.id.iv_new_clear_pwd:
                etRstPwdInput.setText("");
                break;
        }
    }


    @Override
    public void submitResult(int ret) {
        if (ret == 1) {
            Toast.makeText(getActivity(), "修改密码成功", Toast.LENGTH_SHORT).show();
//            ActivityUtils.justPop(getActivity());
            Toast.makeText(getActivity(), "yes?", Toast.LENGTH_SHORT).show();
            getActivity().getSupportFragmentManager().popBackStack();
        } else if (ret == 0) {
            Toast.makeText(getActivity(), "密码修改失败", Toast.LENGTH_SHORT).show();
            ViewUtils.enableEditTextCursor(etRstPwdInput, true);
        }
    }

    @Override
    public void setPresenter(RstPwdContract.Presenter presenter) {
        this.presenter = presenter;
    }
}
