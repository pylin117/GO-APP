package com.go.go;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.R.layout.simple_spinner_dropdown_item;

public class Search_fragment extends Fragment{
    private DatabaseReference mDatabase ;
    private ArrayList<HashMap<String, String>> infoToFavourite = new ArrayList<HashMap<String, String>>();
    private HashMap<String, ArrayList<HashMap<String, String>>> attractions = new HashMap<String, ArrayList<HashMap<String, String>>>();
    private String[] county = new String[] {"請選擇","基隆","台北新北","桃園","新竹","苗栗","台中","彰化","雲林","嘉義","台南","高雄","屏東","台東","花蓮","宜蘭","南投","澎湖","金門","馬祖"};
    private ArrayAdapter<String> countyAdapter;
    private Spinner county_spinner;
    private TableLayout atts;
    private FirebaseUser currentUser =FirebaseAuth.getInstance().getCurrentUser();
    private ArrayList<HashMap<String, String>> history_data= new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String, String>> schedule_data= new ArrayList<HashMap<String, String>>();
    private JSONArray allCounty;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search, container, false);
        county_spinner = (Spinner) view.findViewById(R.id.spinner);
        try {
            Field popup = Spinner.class.getDeclaredField("mPopup");
            popup.setAccessible(true);
            // Get private mPopup member variable and try cast to ListPopupWindow
            android.widget.ListPopupWindow popupWindow = (android.widget.ListPopupWindow) popup.get(county_spinner);
            // Set popupWindow height to 500px
            popupWindow.setHeight(1000);
        }
        catch (NoClassDefFoundError | ClassCastException | NoSuchFieldException | IllegalAccessException e) {
        }
        atts = (TableLayout) view.findViewById(R.id.atts);
        //將各區名稱加入至下拉式選單
        countyAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, county);
        county_spinner.setAdapter(countyAdapter);
        try {
            getCountyCode();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        getData();
        getUserData("歷史紀錄",history_data);
        getUserData("我的最愛",infoToFavourite);
        getUserData("我的景點",schedule_data);
        setSelectedListener();

        return view;
    }
    private void setSelectedListener() {
        county_spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            public void onItemSelected(AdapterView adapterView, View view, int position, long id){
                //Toast.makeText(getActivity(), "您選擇"+adapterView.getSelectedItem().toString(), Toast.LENGTH_LONG).show();
                String choice_att = adapterView.getSelectedItem().toString();
                if(attractions.containsKey(choice_att)) {
                    try {
                        display_atts(attractions.get(choice_att));
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            public void onNothingSelected(AdapterView arg0) {
                //Toast.makeText(getActivity(), "您沒有選擇任何項目", Toast.LENGTH_LONG).show();
            }
        });
    }
    public void getData(){
        //從Firebase抓資料
        mDatabase = FirebaseDatabase.getInstance().getReference();
        ValueEventListener postListener = new ValueEventListener () {
            @Override
            public void onDataChange ( DataSnapshot dataSnapshot ) {
                // Get Post object and use the values to update the UI
                HashMap<String, HashMap<String, ArrayList<HashMap<String, String>>>> temp = (HashMap<String, HashMap<String, ArrayList<HashMap<String, String>>>>) dataSnapshot.getValue();
                attractions = temp.get("景點");
               // System.out.println(attractions);
            }
            @Override
            public void onCancelled ( DatabaseError databaseError ) {
                // Getting Post failed, log a message
            }
        };
        mDatabase.addValueEventListener ( postListener );
    }
    //history and my favorite
    public void getUserData(String theKey, final ArrayList<HashMap<String,String>>arrayLists){
        mDatabase = FirebaseDatabase.getInstance().getReference(currentUser.getUid()).child(theKey);
        ValueEventListener postListener = new ValueEventListener () {
            @Override
            public void onDataChange ( DataSnapshot dataSnapshot ) {
                // Get Post object and use the values to update the UI
                ArrayList<HashMap<String, String>> temp = (ArrayList<HashMap<String, String>>) dataSnapshot.getValue();
                if(temp!=null){
                    for(int i=0;i<temp.size();i++){
                        if(arrayLists.contains(temp.get(i))==false) {
                            arrayLists.add(temp.get(i));
                        }
                    }
                }
            }
            @Override
            public void onCancelled ( DatabaseError databaseError ) {
                // Getting Post failed, log a message
            }
        };
        mDatabase.addValueEventListener ( postListener );
    }
    public TableRow createTableRow_textView(String att1, String att2, int num, float textSize){
        TableRow textRow = new TableRow(getActivity());
        TextView str1 = new TextView(getActivity());
        TextView str2 = new TextView(getActivity());

        str1.setText(att1);
        str2.setText(att2);
        str1.setTextSize(textSize);
        str2.setTextSize(textSize);


        final float scale = getContext().getResources().getDisplayMetrics().density;
        TableRow.LayoutParams params = new TableRow.LayoutParams((int)(194 * scale + 0.5f), (int)(30 * scale + 0.5f));
        str1.setLayoutParams(params);
        str2.setLayoutParams(params);
        str1.setTextColor(Color.BLACK);
        str2.setTextColor(Color.BLACK);

        params = new TableRow.LayoutParams((int)(194 * scale + 0.5f), (int)(30 * scale + 0.5f));
        textRow.setLayoutParams(params);

        if (num == 2) {
            textRow.addView(str1);
            textRow.addView(str2);

        }
        else
            textRow.addView(str1);

        atts.addView(textRow);
        return textRow;
    }
    public TableRow createTableRow_Button(final HashMap<String,String> att1, final HashMap<String,String> att2, final int num){
        TableRow buttonRow = new TableRow(getActivity());
        final Button b1 = new Button(getActivity());
        final Button b2 = new Button(getActivity());

        final float scale = getContext().getResources().getDisplayMetrics().density;
        TableRow.LayoutParams params = new TableRow.LayoutParams((int)(194 * scale + 0.5f), (int)(90 * scale + 0.5f));
        b1.setLayoutParams(params);
        b2.setLayoutParams(params);

        new Thread(new Runnable(){
            @Override
            public void run() {
                final Bitmap att1_mBitmap=getBitmapFromURL(att1.get("圖片"));
                if(num==2){Bitmap att2_mBitmap=getBitmapFromURL(att2.get("圖片"));
                    final BitmapDrawable att2_bdrawable = new BitmapDrawable(getResources(),att2_mBitmap);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            b2.setBackground(att2_bdrawable);
                        }
                    });
                }
                final BitmapDrawable att1_bdrawable = new BitmapDrawable(getResources(),att1_mBitmap);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        b1.setBackground(att1_bdrawable);
                    }
                });
            }
        }).start();

        params = new TableRow.LayoutParams((int)(194 * scale + 0.5f), (int)(90 * scale + 0.5f));
        buttonRow.setLayoutParams(params);
        if (num == 2) {
            buttonRow.addView(b1);
            buttonRow.addView(b2);
        }
        else
            buttonRow.addView(b1);
        atts.addView(buttonRow);
        b1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d("DETAIL", "詳細資料");
                new AlertDialog.Builder(getActivity(),R.style.AlertDialog)
                        .setMessage(att1.get("景點名稱")+"\n"+att1.get("地址")+"\n"+att1.get("營業時間"))
                        .setPositiveButton("加入我的最愛", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(currentUser.getEmail()!=null){
                                    infoToFavourite.add(att1);
                                    writeData("我的最愛", att1,infoToFavourite);
                                    Toast.makeText(getContext(),"已添加",Toast.LENGTH_SHORT).show();;
                                }
                                else{
                                    Toast.makeText(getContext(),"請加入會員",Toast.LENGTH_SHORT).show();;
                                }
                            }
                        })
                        .setNeutralButton("取消",null)
                        .setNegativeButton("加入行程", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(currentUser.getEmail()!=null){
                                    schedule_data.add(att1);
                                    writeData("我的景點", att1,schedule_data);
                                    Toast.makeText(getContext(),"已添加",Toast.LENGTH_SHORT).show();;
                                }
                                else{
                                    Toast.makeText(getContext(),"請加入會員",Toast.LENGTH_SHORT).show();;
                                }
                            }
                        })
                        .show();
                if(history_data.contains(att1)==false){
                    history_data.add(att1);
                    writeData("歷史紀錄", history_data.get(history_data.size()-1),history_data);
                }
            }
        });
        b2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d("DETAIL", "詳細資料");
                new AlertDialog.Builder(getActivity(),R.style.AlertDialog)
                        .setMessage(att2.get("景點名稱")+"\n"+att2.get("地址")+"\n"+att2.get("營業時間"))
                        .setPositiveButton("加入我的最愛", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(currentUser.getEmail()!=null){
                                    infoToFavourite.add(att2);
                                    writeData("我的最愛",att2,infoToFavourite);
                                    Toast.makeText(getContext(),"已添加",Toast.LENGTH_SHORT).show();;
                                }
                                else{
                                    Toast.makeText(getContext(),"請加入會員",Toast.LENGTH_SHORT).show();;
                                }
                            }
                        })
                        .setNeutralButton("取消",null)
                        .setNegativeButton("加入行程", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(currentUser.getEmail()!=null){
                                    schedule_data.add(att2);
                                    writeData("我的景點", att2,schedule_data);
                                    Toast.makeText(getContext(),"已添加",Toast.LENGTH_SHORT).show();;
                                }
                                else{
                                    Toast.makeText(getContext(),"請加入會員",Toast.LENGTH_SHORT).show();;
                                }
                            }
                        })
                        .show();
                if(history_data.contains(att2)==false){
                    history_data.add(att2);
                    writeData("歷史紀錄",history_data.get(history_data.size()-1),history_data);
                }
            }
        });
        return buttonRow;
    }
    public void display_atts(ArrayList<HashMap<String, String>> attList) throws IOException, JSONException {
        atts.removeAllViews();
        for (int i = 0; i < attList.size(); i+=2){
            if(i + 1 < attList.size()){
                String regionCode1 = getRegionCode(attList.get(i).get("所在縣市").substring(0, 2), attList.get(i).get("區域"));
                String regionCode2 = getRegionCode(attList.get(i + 1).get("所在縣市").substring(0, 2), attList.get(i + 1).get("區域"));
                String temp1 = getWeather(regionCode1);
                String temp2 = getWeather(regionCode2);

                createTableRow_Button(attList.get(i), attList.get(i + 1),2);
                createTableRow_textView(attList.get(i).get("景點名稱"), attList.get(i + 1).get("景點名稱"),2, 24);
                createTableRow_textView(temp1, temp2, 2, 16);
            }
            else{
                String regionCode1 = getRegionCode(attList.get(i).get("所在縣市").substring(0, 2), attList.get(i).get("區域"));
                String temp1 = getWeather(regionCode1);

                createTableRow_Button(attList.get(i), null,1);
                createTableRow_textView(attList.get(i).get("景點名稱"), "", 1, 24);
                createTableRow_textView(temp1, "", 1, 16);
            }
        }
    }
    public void writeData(String theKey, HashMap<String, String> attData,ArrayList<HashMap<String, String>>arrayLists){
        //存入歷史紀錄、我的最愛
        mDatabase = FirebaseDatabase.getInstance().getReference(currentUser.getUid()).child(theKey).child(String.valueOf(arrayLists.size()-1));
        mDatabase.setValue(attData);
    }

    //URL轉Bitmap
    public static Bitmap getBitmapFromURL(String src){
        try {
            URL url = new URL(src);
            HttpURLConnection conn=(HttpURLConnection)url.openConnection();
            conn.connect();

            InputStream input=conn.getInputStream();
            Bitmap mbitmap= BitmapFactory.decodeStream(input);
            return mbitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
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

    public String getRegionCode(String county, String region) throws JSONException {
        String regionCode = "";
        for (int i = 0; i < allCounty.length(); i++) {
            if (county.equals(allCounty.getJSONObject(i).getString("name"))){
                JSONArray temp = (JSONArray) allCounty.getJSONObject(i).get("towns");
                for (int j = 0; j < temp.length(); j++){

                    if (region.equals(temp.getJSONObject(j).getString("name"))) {

                        regionCode = temp.getJSONObject(j).getString("id");
                        break;
                    }
                }
                break;
            }
        }
        return regionCode;
    }

    public String getWeather(String regionCode) throws JSONException, IOException {
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
        JSONObject weather = new JSONObject(response.toString());
        String temperature = String.valueOf(weather.getInt("temperature")) + "°C " + weather.getString("desc");
        return temperature;
    }
}
