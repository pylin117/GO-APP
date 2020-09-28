package com.go.go;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import static com.go.go.Search_fragment.getBitmapFromURL;

public class History_record_Activity extends AppCompatActivity {
    private DatabaseReference mDatabase ;
    private TableLayout atts;
    private ArrayList<HashMap<String, String>> history_data= new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String, String>> infoToFavourite = new ArrayList<HashMap<String, String>>();
    private FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
    BottomNavigationView bottomNavigationView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_record);
        atts = findViewById(R.id.showTheHistory);
        //取得我的最愛資料
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<HashMap<String, String>> temp = (ArrayList<HashMap<String, String>>) dataSnapshot.child(currentFirebaseUser.getUid()).child("我的最愛").getValue();
                if(temp!=null){
                    for(int i=0;i<temp.size();i++){
                        if(infoToFavourite.contains(temp.get(i))==false) {
                            infoToFavourite.add(temp.get(i));
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        bottomNavigationView=findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_search:
                        Intent searchIntent = new Intent();
                        searchIntent.setClass(History_record_Activity.this, HomeActivity.class);
                        searchIntent.putExtra("id", 1);
                        startActivity(searchIntent);
                        break;
                    case R.id.action_recommend:
                        Intent recommendIntent = new Intent();
                        recommendIntent.setClass(History_record_Activity.this, HomeActivity.class);
                        recommendIntent.putExtra("id", 2);
                        startActivity(recommendIntent);
                        break;
                    case R.id.action_tour:
                        Intent tourIntent = new Intent();
                        tourIntent.setClass(History_record_Activity.this, HomeActivity.class);
                        tourIntent.putExtra("id", 3);
                        startActivity(tourIntent);
                        break;
                    case R.id.action_more:
                        Intent moreIntent = new Intent();
                        moreIntent.setClass(History_record_Activity.this, HomeActivity.class);
                        moreIntent.putExtra("id", 4);
                        startActivity(moreIntent);
                        break;
                }
                return false;
            }
        });
    }
   @Override
    protected void onStart() {
        super.onStart();
        //從Firebase抓資料
        mDatabase = FirebaseDatabase.getInstance().getReference(currentFirebaseUser.getUid()).child("歷史紀錄");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<HashMap<String, String>> temp = (ArrayList<HashMap<String, String>>) dataSnapshot.getValue();
                if(temp!=null){
                    for(int i=0;i<temp.size();i++){
                        if(history_data.contains(temp.get(i))==false) {
                            history_data.add(temp.get(i));
                        }
                        display_atts(history_data);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });
    }
    private DatabaseReference mDatabases;
    private Integer countFavourite;
    public void setDataTo(HashMap<String, String> info_favourite){
        countFavourite=infoToFavourite.size();
        mDatabases = FirebaseDatabase.getInstance().getReference(currentFirebaseUser.getUid()).child("我的最愛").child(String.valueOf(countFavourite));
        mDatabases.setValue(info_favourite);
    }

    public TableRow createTableRow_textView(String att1, String att2, int num){
        TableRow textRow = new TableRow(this);
        TextView str1 = new TextView(this);
        TextView str2 = new TextView(this);
        str1.setText(" " + att1);
        str2.setText(" " + att2);
        final float scale = this.getResources().getDisplayMetrics().density;
        TableRow.LayoutParams params = new TableRow.LayoutParams((int)(194 * scale + 0.5f), (int)(40 * scale + 0.5f));
        str1.setLayoutParams(params);
        str2.setLayoutParams(params);
        str1.setTextColor(Color.BLACK);
        str2.setTextColor(Color.BLACK);
        params = new TableRow.LayoutParams((int)(194 * scale + 0.5f), (int)(40 * scale + 0.5f));
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
        TableRow buttonRow = new TableRow(this);
        final Button b1 = new Button(this);
        final Button b2 = new Button(this);
        final float scale = this.getResources().getDisplayMetrics().density;
        TableRow.LayoutParams params = new TableRow.LayoutParams((int)(194 * scale + 0.5f), (int)(90 * scale + 0.5f));
        b1.setLayoutParams(params);
        b2.setLayoutParams(params);
        new Thread(new Runnable(){
            @Override
            public void run() {
                final Bitmap att1_mBitmap=getBitmapFromURL(att1.get("圖片"));
                if(num==2){Bitmap att2_mBitmap=getBitmapFromURL(att2.get("圖片"));
                    final BitmapDrawable att2_bdrawable = new BitmapDrawable(getResources(),att2_mBitmap);
                    History_record_Activity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            b2.setBackground(att2_bdrawable);
                        }
                    });
                }
                final BitmapDrawable att1_bdrawable = new BitmapDrawable(getResources(),att1_mBitmap);
               History_record_Activity.this.runOnUiThread(new Runnable() {
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
                new AlertDialog.Builder(History_record_Activity.this,R.style.AlertDialog)
                        .setMessage(att1.get("景點名稱")+"\n"+att1.get("地址")+"\n"+att1.get("營業時間"))
                        .setPositiveButton("加入我的最愛", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(currentFirebaseUser.getEmail()!=null){
                                    setDataTo(att1);
                                    Toast.makeText(History_record_Activity.this,"已添加",Toast.LENGTH_SHORT).show();;
                                }
                                else{
                                    Toast.makeText(History_record_Activity.this,"請加入會員",Toast.LENGTH_SHORT).show();;
                                }
                            }
                        })
                        .setNeutralButton("取消",null)
                        .setNegativeButton("加入行程", null)
                        .show();
            }
        });
        b2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d("DETAIL", "詳細資料");
                new AlertDialog.Builder(History_record_Activity.this,R.style.AlertDialog)
                        .setMessage(att2.get("景點名稱")+"\n"+att2.get("地址")+"\n"+att2.get("營業時間"))
                        .setPositiveButton("加入我的最愛", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(currentFirebaseUser.getEmail()!=null){
                                    setDataTo(att2);
                                    Toast.makeText(History_record_Activity.this,"已添加",Toast.LENGTH_SHORT).show();;
                                }
                                else{
                                    Toast.makeText(History_record_Activity.this,"請加入會員",Toast.LENGTH_SHORT).show();;
                                }
                            }
                        })
                        .setNeutralButton("取消",null)
                        .setNegativeButton("加入行程", null)
                        .show();
            }
        });
        return buttonRow;
    }
    public void display_atts(ArrayList<HashMap<String, String>> attList){
        atts.removeAllViews();
        for (int i = 0; i < attList.size(); i+=2){
            if(i + 1 < attList.size()){
                createTableRow_textView(attList.get(i).get("景點名稱"), attList.get(i + 1).get("景點名稱"), 2);
                createTableRow_Button(attList.get(i), attList.get(i + 1),2);
            }
            else{
                createTableRow_textView(attList.get(i).get("景點名稱"), "", 1);
                createTableRow_Button(attList.get(i), null,1);
            }
        }
    }



}
