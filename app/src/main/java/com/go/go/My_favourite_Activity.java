package com.go.go;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

import java.util.ArrayList;
import java.util.HashMap;

public class My_favourite_Activity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private  ArrayList<HashMap<String, String>> infoToFavourite = new  ArrayList<HashMap<String, String>>();
    BottomNavigationView bottomNavigationView;
     TextView showThePage;
    private TableLayout showPage;
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_favourite);
        showThePage=findViewById(R.id.my_favourite_page);
        showPage=findViewById(R.id.showTheHistory);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<HashMap<String, String>> temp = (ArrayList<HashMap<String, String>>) dataSnapshot.child(currentUser.getUid()).child("我的最愛").getValue();
                if(temp!=null){
                    for(int i=0;i<temp.size();i++){
                        if(infoToFavourite.contains(temp.get(i))==false) {
                            infoToFavourite.add(temp.get(i));
                        }
                    }
                    Display_TheLike(infoToFavourite);
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
                        searchIntent.setClass(My_favourite_Activity.this, HomeActivity.class);
                        searchIntent.putExtra("id", 1);
                        startActivity(searchIntent);
                        break;
                    case R.id.action_recommend:
                        Intent recommendIntent = new Intent();
                        recommendIntent.setClass(My_favourite_Activity.this, HomeActivity.class);
                        recommendIntent.putExtra("id", 2);
                        startActivity(recommendIntent);
                        break;
                    case R.id.action_tour:
                        Intent tourIntent = new Intent();
                        tourIntent.setClass(My_favourite_Activity.this, HomeActivity.class);
                        tourIntent.putExtra("id", 3);
                        startActivity(tourIntent);
                        break;
                    case R.id.action_more:
                        Intent moreIntent = new Intent();
                        moreIntent.setClass(My_favourite_Activity.this, HomeActivity.class);
                        moreIntent.putExtra("id", 4);
                        startActivity(moreIntent);
                        break;
                }
                return false;
            }
        });
    }
    public TableRow create_TableRow_view(final HashMap<String, String>information){
        String theInfo=information.get("景點名稱")+"\n"+information.get("地址")+"\n"+information.get("營業時間")+"\n";
        final TableRow theView=new TableRow(this);
        final CheckBox heartCheck= (CheckBox) ((LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.checkbox_style,null);
        Drawable drawable=this.getDrawable(R.drawable.checked_selector);
        drawable.setBounds(0,0,100,100);
        heartCheck.setCompoundDrawables(null,null,drawable,null);
        heartCheck.setText(theInfo);
        heartCheck.setTextAlignment	(View.TEXT_ALIGNMENT_VIEW_START);
        heartCheck.setTextSize(14);
        heartCheck.setTextColor(Color.BLACK);
        heartCheck.setBackgroundColor(Color.WHITE);
        heartCheck.setChecked(true);
        final float scale = this.getResources().getDisplayMetrics().density;
        TableRow.LayoutParams params = new TableRow.LayoutParams((int)(415 * scale + 0.5f), (int)(70 * scale + 0.5f));
        heartCheck.setLayoutParams(params);
        params = new TableRow.LayoutParams((int)(415 * scale + 0.5f), (int)(70 * scale + 0.5f));
        theView.setLayoutParams(params);
        theView.addView(heartCheck);
        showPage.addView(theView);
        heartCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked==true){
                    if(infoToFavourite.contains(information)!=true){
                        infoToFavourite.add(information);
                        DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference(currentUser.getUid());
                        dataRef.child("我的最愛").setValue(infoToFavourite);
                        Toast.makeText(My_favourite_Activity.this, "已添加我的最愛",Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    new AlertDialog.Builder(My_favourite_Activity.this,R.style.AlertDialog)
                            .setMessage("確定是否取消我的最愛?")
                            .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference(currentUser.getUid());
                                    if(infoToFavourite.size()==1){
                                        dataRef.child("我的最愛").removeValue();
                                        showPage.removeAllViews();
                                    }
                                    else{
                                        infoToFavourite.remove(information);
                                        dataRef.child("我的最愛").setValue(infoToFavourite);
                                    }
                                    Toast.makeText(My_favourite_Activity.this, "已取消我的最愛",Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNeutralButton("否",new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    heartCheck.setChecked(true);
                                }
                            })
                            .show();
                }
            }
        });
        return theView;
    }
    public void Display_TheLike(ArrayList<HashMap<String,String>> temp){
        showPage.removeAllViews();
        for(int i=0;i<temp.size();i++){
            create_TableRow_view(temp.get(i));
        }
    }
}
