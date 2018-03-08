package com.chenz.daynightdemo;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import java.util.List;

/**
 * description: <一句话功能简述>
 *
 * @author Chenz
 * @date 2018/3/7 0007
 */
public class MainActivity extends AppCompatActivity {
    private int      permisionCode    = 300;
    private int      permisionReqCode = 400;
    private String[] permission       = {
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private CheckBox checkBox;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkBox = findViewById(R.id.ckb);
        makeDirAndCheckPermision();
    }

    /**
     * 申请权限并创建下载目录
     */
    private void makeDirAndCheckPermision() {
//        if (!AndPermission.hasPermission(MainActivity.this, permission)) {
        AndPermission.with(this)
                .requestCode(permisionCode)
                .permission(permission)
                .rationale(new RationaleListener() {
                    @Override
                    public void showRequestPermissionRationale(int requestCode, Rationale
                            rationale) {
                        // 此对话框可以自定义，调用rationale.resume()就可以继续申请。
                        AndPermission.rationaleDialog(MainActivity.this, rationale).show();
                    }
                })
                .callback(listener)
                .start();
//        }
    }

    private PermissionListener listener = new PermissionListener() {

        @Override
        public void onSucceed(int requestCode, @NonNull List<String> grantedPermissions) {
            // 权限申请成功回调。

            // 这里的requestCode就是申请时设置的requestCode。
            if (requestCode == permisionCode) {
                // TODO ...

                final SharedPreferences sp = getPreferences(MODE_PRIVATE);
                boolean isChecked = sp.getBoolean("isChecked", false);
                Log.e("getBoolean", isChecked + "");
                checkBox.setChecked(isChecked);
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putBoolean("isChecked", isChecked);
                        Log.e("putBoolean", isChecked + "");
                        editor.apply();
                        AppCompatDelegate.setDefaultNightMode(
                                isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate
                                        .MODE_NIGHT_NO);
                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                });
            }
        }

        @Override
        public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
            // 权限申请失败回调。
            if (requestCode == permisionCode) {
                // TODO ...
                if (!AndPermission.hasPermission(MainActivity.this, deniedPermissions)) {
                    // 是否有不再提示并拒绝的权限。
                    if (AndPermission.hasAlwaysDeniedPermission(MainActivity.this,
                            deniedPermissions)) {
                        // 第一种：用AndPermission默认的提示语。
                        AndPermission.defaultSettingDialog(MainActivity.this, permisionReqCode)
                                .show();
                    } else {
                        AndPermission.defaultSettingDialog(MainActivity.this, permisionReqCode)
                                .show();
                    }
                }
            }
        }
    };
}
