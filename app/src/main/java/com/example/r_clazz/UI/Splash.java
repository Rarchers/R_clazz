package com.example.r_clazz.UI;

import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.r_clazz.R;
import com.example.r_clazz.Receiver.AdminReceiver;
import java.util.ArrayList;


public class Splash extends AppCompatActivity {
    String TAG = "Splash";
    private ComponentName componentName;
    String mPackName = "com.example.r_clazz";
    AlertDialog mPermissionDialog;
    ArrayList mPermissionList = new ArrayList();
    private final int mRequestCode = 100;
    String[] permissions = new String[]{"android.permission.ACCESS_WIFI_STATE", "android.permission.CHANGE_WIFI_STATE", "android.permission.ACCESS_NETWORK_STATE", "android.permission.CHANGE_NETWORK_STATE", "android.permission.INTERNET"};
    private DevicePolicyManager policyManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.policyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        this.componentName = new ComponentName(this, AdminReceiver.class);
        setContentView(R.layout.activity_main);
        initPermission();
    }

    private void initPermission() {
        if (!this.policyManager.isAdminActive(this.componentName)) {
            activeManage();
        }
        this.mPermissionList.clear();
        for (String permission : this.permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != 0) {
                String str = this.TAG;
                String stringBuilder = "onRequestPermissionsResult: " +
                        permission;
                Log.d(str, stringBuilder);
                Toast.makeText(this, permission, Toast.LENGTH_SHORT).show();
                this.mPermissionList.add(permission);
            }
        }
        String str2;
        str2 = this.TAG;
        String stringBuilder2 = "initPermission: " +
                this.mPermissionList.size();
        Log.d(str2, stringBuilder2);
        if (this.mPermissionList.size() > 0) {
            ActivityCompat.requestPermissions(this, this.permissions, 100);
        } else {
            init();
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean hasPermissionDismiss = false;
        if (100 == requestCode) {
            for (int i : grantResults) {
                if (i == -1) {
                    hasPermissionDismiss = true;
                    break;
                }
            }
        }
        if (hasPermissionDismiss) {
            showPermissionDialog();
        } else {
            init();
        }
    }

    private void showPermissionDialog() {
        if (this.mPermissionDialog == null) {
            this.mPermissionDialog = new AlertDialog.Builder(this)
                    .setMessage("已禁用权限，请手动授予")
                    .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("package:");
                            stringBuilder.append(mPackName);
                            startActivity(new Intent("android.settings.APPLICATION_DETAILS_SETTINGS", Uri.parse(stringBuilder.toString())));
                        }
                    })
            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).create();
        }
        this.mPermissionDialog.show();
    }

    private void cancelPermissionDialog() {
        this.mPermissionDialog.cancel();
    }

    private void init() {
        if (this.policyManager.isAdminActive(this.componentName)) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        Toast.makeText(getApplicationContext(), "你还有一个权限尚未同意", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void activeManage() {
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,componentName);
        intent.putExtra("android.app.extra.ADD_EXPLANATION", "激活后才能使用锁屏功能哦");
        startActivity(intent);
    }
}
