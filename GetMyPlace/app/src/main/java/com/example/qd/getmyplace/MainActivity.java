package com.example.qd.getmyplace;

import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

public class MainActivity extends AppCompatActivity {
    private PermissionsChecker mPermissionsChecker; // 权限检测器
    private final int RESULT_CODE_LOCATION = 0x001;
    private Button btn_getPlace;
    //定位权限,获取app内常用权限
    String[] permsLocation = {"android.permission.READ_PHONE_STATE"
            , "android.permission.ACCESS_COARSE_LOCATION"
            , "android.permission.ACCESS_FINE_LOCATION"
            , "android.permission.READ_EXTERNAL_STORAGE"
            , "android.permission.WRITE_EXTERNAL_STORAGE"};
    LocationClient mLocClient;
    private TextView tv_place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_getPlace = findViewById(R.id.btn_getPlace);
        tv_place = findViewById(R.id.tv_place);
        mPermissionsChecker = new PermissionsChecker(MainActivity.this);
        mLocClient = new LocationClient(getApplicationContext());
        mLocClient.registerLocationListener(listener);

        btn_getPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPermissionsChecker.lacksPermissions(permsLocation)) {
                    ActivityCompat.requestPermissions(MainActivity.this, permsLocation, RESULT_CODE_LOCATION);
                } else {
                    //获取位置
                    setLocation();
                }
            }
        });
    }

    //获取位置
    private void setLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setIsNeedAddress(true);//是否要地址
        option.setOpenGps(true);
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);//可选，默认0，即仅定位一次，设置发起连续定位请求的间隔需要大于等于1000ms才是有效的
        mLocClient.setLocOption(option);
        mLocClient.start();
    }

    /***
     * 定位结果回调，在此方法中处理定位结果
     */
    BDAbstractLocationListener listener = new BDAbstractLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {

            Log.i("bdmap", "定位类型:" + bdLocation.getLocTypeDescription() + "\n"
                    + "纬度:" + bdLocation.getLatitude() + "\n"
                    + "经度:" + bdLocation.getLongitude() + "\n"
                    + "详细地址:" + bdLocation.getAddrStr() + "\n"
                    + "卫星数目" + bdLocation.getSatelliteNumber());
            //设置我的位置
            tv_place.setText(bdLocation.getAddrStr());
        }
    };

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults) {
        switch (permsRequestCode) {
            case RESULT_CODE_LOCATION:
                boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (cameraAccepted) {
                    //权限通过后继续获取位置
                    setLocation();
                } else {
                    //用户授权拒绝之后，友情提示一下
                    Toast.makeText(MainActivity.this, "请开启应用定位权限", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
