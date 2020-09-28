package com.go.go;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.google.firebase.auth.FirebaseAuth.getInstance;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 999;
    private Button loginButton;
    private Button registerButton;
    private Button visitorButton;
    private FirebaseAuth mAuth;
    private String commandStr;
    private LocationManager locationManager;
    private boolean getService = false;
    private String address;
    private JSONArray allCounty;
    private String regionCode = "";
    private TextView regionText;
    private TextView temperatureText;
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("==============================");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        loginButton = findViewById(R.id.login);
        registerButton = findViewById(R.id.register);
        visitorButton = findViewById(R.id.visitor);
        regionText = findViewById(R.id.regionText);
        temperatureText = findViewById(R.id.temperature);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        try {
            getCountyCode();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mAuth = getInstance();

        loginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this , Login_Activity.class);
                startActivity(intent);
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this , Register_Activity.class);
                startActivity(intent);
            }
        });
        visitorButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mAuth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(MainActivity.this,"匿名登入成功 ",Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(MainActivity.this,"匿名登入失敗",Toast.LENGTH_LONG).show();
                        }
                    }
                });
                Intent intent = new Intent();
                intent.setClass(MainActivity.this ,HomeActivity.class);
                startActivity(intent);
            }
        });


        commandStr = LocationManager.GPS_PROVIDER;
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("Report", "== in android 6.0, getting permission");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSION_ACCESS_FINE_LOCATION);
            return;
        }

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            //如果GPS或網路定位開啟，呼叫locationServiceInitial()更新位置
            locationServiceInitial();
        } else {
            Toast.makeText(this, "請開啟定位服務", Toast.LENGTH_LONG).show();
            getService = true; //確認開啟定位服務
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)); //開啟設定頁面
            locationServiceInitial();
        }
        try {
            getCountyRegion(address);
            getWeather();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //定位
    private LocationManager lms;

    private void locationServiceInitial() {
        lms = (LocationManager) getSystemService(LOCATION_SERVICE); //取得系統定位服務
        //由Criteria物件判斷提供最準確的資訊
        Criteria criteria = new Criteria();  //資訊提供者選取標準
        commandStr = lms.getBestProvider(criteria, true);    //選擇精準度最高的提供者
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("Report", "== in android 6.0, getting permission");
            return;
        }
        Location location = lms.getLastKnownLocation(commandStr);

        address = getAddressByLocation(location);
        System.out.println(address);

        lms.requestLocationUpdates(commandStr, 1000, 1, locationListener);
    }
    //取得地址
    private String getAddressByLocation(Location location) {
        String returnAddress = "";
        try {
            if (location != null) {
                Double longitude = location.getLongitude();        //取得經度
                Double latitude = location.getLatitude();        //取得緯度
                Geocoder gc = new Geocoder(this, Locale.TRADITIONAL_CHINESE);        //地區:台灣
                //自經緯度取得地址
                List<Address> lstAddress = gc.getFromLocation(latitude, longitude, 1);
                //=lstAddress.get(0).getAddressLine(0);
                if (!Geocoder.isPresent()) { //Since: API Level 9
                    returnAddress = "Sorry! Geocoder service not Present.";
                }
                returnAddress = lstAddress.get(0).getAddressLine(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnAddress;
    }
    public LocationListener locationListener = new LocationListener() {
        //當座標改變時觸發此函數，如果Provider傳進相同的座標，它就不會被觸發
        @Override
        public void onLocationChanged(Location location) {
            String address;
            address = getAddressByLocation(location);
            System.out.println(address);
            try {
                getCountyRegion(address);
                getWeather();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        //Provider的轉態在可用、暫時不可用和無服務三個狀態直接切換時觸發此函數
        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }
        //Provider被enable時觸發此函數，比如GPS被打開
        @Override
        public void onProviderEnabled(String s) {

        }
        //Provider被disable時觸發此函數，比如GPS被關閉
        @Override
        public void onProviderDisabled(String s) {

        }
    };

    public void getCountyCode() throws JSONException, IOException {

        URL url = new URL("https://works.ioa.tw/weather/api/all.json");
        HttpURLConnection connection = null;
        connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        InputStream in = connection.getInputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        StringBuilder response = new StringBuilder();

        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        allCounty = new JSONArray(response.toString());
    }

    public void getWeather() throws JSONException, IOException {
        URL url = new URL("https://works.ioa.tw/weather/api/weathers/"+ String.valueOf(regionCode) +".json");
        HttpURLConnection connection = null;
        connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        InputStream in = connection.getInputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        StringBuilder response = new StringBuilder();

        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        Gson gson = new Gson();
        JSONObject weather = new JSONObject(response.toString());
        String temperature = String.valueOf(weather.getInt("temperature")) + "°C " + weather.getString("desc");
        temperatureText.setText(temperature);
    }

    public void getCountyRegion(String address) throws JSONException {
        String county = "", region = "", countCode = "";
        int countyIndex = 0, regionIndex = 0;
        for (int i = 0; i < allCounty.length(); i++){
            String temp = (String) allCounty.getJSONObject(i).get("name");
            if(address.contains(temp)) {
                county = temp;
                countCode = (String) allCounty.getJSONObject(i).get("id");
                countyIndex = i;
            }
        }
        JSONArray towns = (JSONArray) allCounty.getJSONObject(countyIndex).get("towns");
        for (int i = 0; i < towns.length(); i++){
            String  temp = (String) towns.getJSONObject(i).get("name");
            if(address.contains(temp)){
                region = temp;
                regionCode = (String) towns.getJSONObject(i).get("id");
                regionIndex = i;
            }
        }
        regionText.setText(region);
    }
}

