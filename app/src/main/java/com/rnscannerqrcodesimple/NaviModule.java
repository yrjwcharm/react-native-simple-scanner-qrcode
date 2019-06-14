package com.rnscannerqrcodesimple;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.qw.soul.permission.SoulPermission;
import com.qw.soul.permission.bean.Permission;
import com.qw.soul.permission.bean.Permissions;
import com.qw.soul.permission.callbcak.CheckRequestPermissionsListener;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.common.Constant;

/**
 * Created by hurong_pc111 on 2019/6/14.
 */

public class NaviModule extends ReactContextBaseJavaModule {
    private Promise mPromise;
    private final ActivityEventListener activityEventListener=new BaseActivityEventListener(){
        @Override
        public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
            super.onActivityResult(activity, requestCode, resultCode, data);
            try{
                if(requestCode==200&&resultCode == Activity.RESULT_OK){
                    if (data != null) {
                        String content = data.getStringExtra(Constant.CODED_CONTENT);
                        mPromise.resolve(content);
                    }
                }else{
                    mPromise.resolve("");
                }


            }catch (Exception e){
                Log.d("333",e.getMessage());
                mPromise.resolve("");
            }

        }
    };
    public NaviModule(ReactApplicationContext reactContext) {
        super(reactContext);
        reactContext.addActivityEventListener(activityEventListener);
    }

    @Override
    public String getName() {
        return "NaviModule";
    }
    @ReactMethod
    public void startQrcodeScanner(final Promise promise){
        SoulPermission.getInstance().checkAndRequestPermissions(
                Permissions.build(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE),
                //if you want do noting or no need all the callbacks you may use SimplePermissionsAdapter instead
                new CheckRequestPermissionsListener() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onAllPermissionOk(Permission[] allPermissions) {
                        Activity currentActivity = getCurrentActivity();
                        if (currentActivity == null) {
                            promise.reject("", "Activity don't exist");
                            return;
                        }

                        // Store the promise to resolve/reject when picker returns data
                        mPromise = promise;

                        try {
                            Intent intent = new Intent(currentActivity, CaptureActivity.class);
                            currentActivity.startActivityForResult(intent, 200);
                        } catch (Exception e) {
                            mPromise.reject("", e.getMessage());
                            mPromise = null;
                        }
                    }

                    @Override
                    public void onPermissionDenied(Permission[] refusedPermissions) {

                    }
                });
    }
}
