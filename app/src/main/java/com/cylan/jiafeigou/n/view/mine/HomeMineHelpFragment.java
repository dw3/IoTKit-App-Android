package com.cylan.jiafeigou.n.view.mine;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.cylan.jiafeigou.R;
import com.cylan.jiafeigou.misc.JConstant;
import com.cylan.jiafeigou.n.base.BaseApplication;
import com.cylan.jiafeigou.n.base.IBaseFragment;
import com.cylan.jiafeigou.support.OptionsImpl;
import com.cylan.jiafeigou.support.badge.Badge;
import com.cylan.jiafeigou.support.badge.TreeHelper;
import com.cylan.jiafeigou.support.badge.TreeNode;
import com.cylan.jiafeigou.utils.ContextUtils;
import com.cylan.jiafeigou.utils.NetUtils;
import com.cylan.jiafeigou.utils.ViewUtils;
import com.cylan.jiafeigou.widget.CustomToolbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 创建者     谢坤
 * 创建时间   2016/8/8 11:26
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
@Badge(parentTag = "HomeMineFragment")
public class HomeMineHelpFragment extends IBaseFragment {

    @BindView(R.id.wv_mine_help)
    WebView mWvHelp;
    @BindView(R.id.custom_toolbar)
    CustomToolbar customToolbar;
    @BindView(R.id.v_progress)
    ProgressBar vProgress;


    public static HomeMineHelpFragment newInstance(Bundle bundle) {
        HomeMineHelpFragment fragment = new HomeMineHelpFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine_help, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle.containsKey(JConstant.KEY_SHOW_SUGGESTION)) {
            customToolbar.setToolbarRightTitle("");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        showWebView();
    }

    @Override
    public void onResume() {
        super.onResume();
        TreeHelper helper = BaseApplication.getAppComponent().getTreeHelper();
        TreeNode node = helper.findTreeNodeByName(this.getClass().getSimpleName());
        customToolbar.showToolbarRightHint(node != null && node.getNodeCount() > 0);
    }

    /**
     * 当进度条加载完成的时候显示该webView
     */
    private void showWebView() {
        final String packageNameSuffix = ContextUtils.getContext().getPackageName().replace("com.cylan.jiafeigou", "")
                .replace(".", "");
        String agreementUrl = getString(R.string.help_url, OptionsImpl.getServer().split(":")[0],
                packageNameSuffix.length() == 0 ? "" : "_" + packageNameSuffix);
        if (agreementUrl.contains("–")) {
            agreementUrl = agreementUrl.replace("–", "-");
        }
        final String url = agreementUrl;
        if (getView() != null) getView().post(() -> initWebView(url));
    }

    @OnClick({R.id.tv_toolbar_icon, R.id.tv_toolbar_right})
    public void onClick(View view) {
        switch (view.getId()) {
            //点击退回home_mine的fragment
            case R.id.tv_toolbar_icon:
                getActivity().getSupportFragmentManager().popBackStack();
                break;
            //点击进入意见反馈的页面
            case R.id.tv_toolbar_right:
                if (getView() != null)
                    ViewUtils.deBounceClick(getView().findViewById(R.id.tv_toolbar_right));
                startActivity(new Intent(getActivity(), FeedbackActivity.class));
                break;
        }
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView(String url) {
        mWvHelp.getSettings().setJavaScriptEnabled(true);
        mWvHelp.getSettings().setDefaultTextEncodingName("utf-8");
        mWvHelp.getSettings().setAllowFileAccess(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWvHelp.getSettings().setMixedContentMode(
                    WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        }
        // 设置 缓存模式
        if (!NetUtils.isNetworkAvailable(ContextUtils.getContext())) {
            mWvHelp.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        } else {
            mWvHelp.getSettings().setCacheMode(
                    WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
        // webView.getSettings().setBlockNetworkImage(true);// 把图片加载放在最后来加载渲染
        mWvHelp.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        // 支持多窗口
        mWvHelp.getSettings().setSupportMultipleWindows(true);
        // 开启 DOM storage API 功能
        mWvHelp.getSettings().setDomStorageEnabled(true);
        // 开启 Application Caches 功能
        String cacheDirPath = getContext().getApplicationContext().getDir("cache", Context.MODE_PRIVATE).getPath();
        mWvHelp.getSettings().setAppCachePath(cacheDirPath);
        mWvHelp.getSettings().setAppCacheMaxSize(1024 * 1024 * 8);
        mWvHelp.getSettings().setDatabaseEnabled(true);
        mWvHelp.getSettings().setAppCacheEnabled(true);
        onLoad(url);
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("SetJavaScriptEnabled")
    public void onLoad(String url) {
        try {
            mWvHelp.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    vProgress.setProgress(newProgress);
                    if (newProgress == 0) vProgress.setVisibility(View.VISIBLE);
                    if (newProgress == 100) vProgress.setVisibility(View.INVISIBLE);
                }
            });
            mWvHelp.setWebViewClient(new WebViewClient() {
                @Override
                public void onLoadResource(WebView view, String url) {
                    super.onLoadResource(view, url);
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView webview,
                                                        String url) {
                    webview.loadUrl(url);
                    return true;
                }

                @Override
                public void onReceivedSslError(WebView view,
                                               SslErrorHandler handler, SslError error) {
                    handler.proceed();  //接受所有证书
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                }

                @Override
                public void onReceivedError(WebView view, int errorCode,
                                            String description, String failingUrl) {

                }
            });
            mWvHelp.loadUrl(url);
        } catch (Exception e) {
            return;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWvHelp.removeAllViews();
        mWvHelp.destroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
