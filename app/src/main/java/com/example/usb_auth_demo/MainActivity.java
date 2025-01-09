package com.example.usb_auth_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.usb_auth_demo.databinding.ActivityMainBinding;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'usb_auth_demo' library on application startup.
    static {
        System.loadLibrary("usb_auth_demo");
    }

    private ActivityMainBinding binding;

    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private final String TAG = "usb_auth_demo";

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)){
                synchronized (this) {
                    UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)){
                        if(device != null){
                            Log.i(TAG,"设备授权成功");
                            Toast.makeText(context,"设备授权成功",Toast.LENGTH_LONG).show();
                        }
                    }else{
                        Log.d(TAG, "设备拒绝授权: " + device);
                        Toast.makeText(context,"设备拒绝授权",Toast.LENGTH_LONG).show();
                    }
                }
            }else if(UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)){
                Toast.makeText(context,"USB已插入",Toast.LENGTH_LONG).show();
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                Toast.makeText(context,"USB已拔出",Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Example of a call to a native method
        TextView tv = binding.sampleText;
        tv.setText(stringFromJNI());

        Button btnSave = findViewById(R.id.btnSaveToUDisk);
        btnSave.setOnClickListener(view -> {
            int[] arr =getIntArray();
            System.out.printf("===> %d %d \n",arr[0],arr[1]);
            byte[] _tmpBytes = getByteArray();
            System.out.printf("===> %d %d \n",_tmpBytes[0],_tmpBytes[1]);
        });

        //获取USB管理服务
        UsbManager mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        //获取USB设备列表
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();

        for (UsbDevice dev: deviceList.values()){
            String usbInfo = "DeviceID: "+dev.getDeviceId()+"\n"+
                    "VendorId: "+dev.getVendorId()+"\n"+
                    "ProductId: "+dev.getProductId()+"\n"+
                    "Manufacturer: "+dev.getManufacturerName()+"\n"+
                    "Product: "+dev.getProductName()+"\n"+
                    "Version: "+dev.getVersion()+"\n";
            tv.setText(usbInfo);
            System.out.println("==========================");
            System.out.println(usbInfo);
            System.out.println("==========================");

            //注册USB授权广播
            //Android14 API34 及或更高使用 FLAG_IMMUTABLE
            //Android14 API33 及或更低使用 FLAG_MUTABLE
            PendingIntent mPermissionIntent = PendingIntent.getBroadcast(this, 0,
                    new Intent(ACTION_USB_PERMISSION),
                    Build.VERSION.SDK_INT>=34? PendingIntent.FLAG_IMMUTABLE:PendingIntent.FLAG_MUTABLE);
            IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
            filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);//USB插入
            filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);//USB拔出
            if (Build.VERSION.SDK_INT>=33){
                registerReceiver(mUsbReceiver, filter,Context.RECEIVER_EXPORTED);
            }else{
                registerReceiver(mUsbReceiver, filter);
            }

            if (mUsbManager.hasPermission(dev)){
                Toast.makeText(this,"已拥有USB设备权限",Toast.LENGTH_LONG).show();
            }else{
                //请求USB设备权限
                mUsbManager.requestPermission(dev, mPermissionIntent);
            }


            //如果是USB配件，可以通信，如是U盘不行
            /*UsbInterface intf = dev.getInterface(0);
            UsbEndpoint endpoint = intf.getEndpoint(0);
            UsbDeviceConnection connection = mUsbManager.openDevice(dev);
            connection.claimInterface(intf, forceClaim);
            connection.bulkTransfer(endpoint, bytes, bytes.length, TIMEOUT);*/
        }


    }

    /**
     * A native method that is implemented by the 'usb_auth_demo' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
    public native int[] getIntArray();

    public native byte[] getByteArray();
}