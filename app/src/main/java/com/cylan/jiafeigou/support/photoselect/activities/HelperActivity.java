package com.cylan.jiafeigou.support.photoselect.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.cylan.jiafeigou.support.photoselect.helpers.Constants;

/**
 * Created by darshan on 26/9/16.
 */
public class HelperActivity extends AppCompatActivity {
    protected View view;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(getResources().getDrawable(android.R.color.white));
    }

    private final int maxLines = 4;
    private final String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

    protected void checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            permissionGranted();

        } else {
            ActivityCompat.requestPermissions(this, permissions, Constants.PERMISSION_REQUEST_CODE);
        }
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//            showRequestPermissionRationale();

        } else {
            showAppPermissionSettings();
        }
    }

    private void showRequestPermissionRationale() {
//        Snackbar snackbar = Snackbar.make(
//                view,
//                getString(R.string.permission_info),
//                Snackbar.LENGTH_INDEFINITE)
//                .setAction(getString(R.string.OK), new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        ActivityCompat.requestPermissions(
//                                HelperActivity.this,
//                                permissions,
//                                Constants.PERMISSION_REQUEST_CODE);
//                    }
//                });

        /*((TextView) snackbar.getView()
                .findViewById(android.support.design.R.id.snackbar_text)).setMaxLines(maxLines);*/
//        snackbar.show();
    }

    private void showAppPermissionSettings() {
//        Snackbar snackbar = Snackbar.make(
//                view,
//                getString(R.string.permission_force),
//                Snackbar.LENGTH_INDEFINITE)
//                .setAction(getString(R.string.SETTINGS), new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Uri uri = Uri.fromParts(
//                                "package",
////                                HelperActivity.this.getPackageName(),
//                                ContextUtils.getContext().getPackageName(),
//                                null);
//
//                        Intent intent = new Intent();
//                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//                        intent.setData(uri);
//                        startActivityForResult(intent, Constants.PERMISSION_REQUEST_CODE);
//                    }
//                });
//
//        /*((TextView) snackbar.getView()
//                .findViewById(android.support.design.R.id.snackbar_text)).setMaxLines(maxLines);*/
//        snackbar.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != Constants.PERMISSION_REQUEST_CODE
                || grantResults.length == 0
                || grantResults[0] == PackageManager.PERMISSION_DENIED) {
            permissionDenied();

        } else {
            permissionGranted();
        }
    }

    protected void permissionGranted() {
    }

    private void permissionDenied() {
        hideViews();
        requestPermission();
    }

    protected void hideViews() {
    }

    protected void setView(View view) {
        this.view = view;
    }
}
