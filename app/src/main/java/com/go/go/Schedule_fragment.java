package com.go.go;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class Schedule_fragment extends Fragment{
    private DatabaseReference Schedule_Database;
    //private DatabaseReference myRef;
    private Button timebutton ;
    private Button add_button ;
    private ListView sch_listview ;
    private Spinner schedule_spinner;
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private ArrayList<HashMap<String, String>> schedule_item= new ArrayList<HashMap<String, String>>();
    private List<String> list_spinner = new ArrayList<String>();
    private List<String> list_spinner_copy = new ArrayList<String>();
    private ArrayAdapter<String> spinner_Adapter;
    private ArrayAdapter<String> listview_Adapter;
    private View view;
    private List<String> listview_data = new ArrayList<String>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        list_spinner.clear();
        view = inflater.inflate(R.layout.schedule, container, false);
        add_button = (Button) view.findViewById(R.id.addbutton);
        timebutton = (Button) view.findViewById(R.id.time_button);
        schedule_spinner = (Spinner) view.findViewById(R.id.schedule_spinner);
        Schedule_Database = FirebaseDatabase.getInstance().getReference();
        sch_listview = (ListView) view.findViewById(R.id.schedule_list_view);

        listview_Adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,listview_data);
        sch_listview.setAdapter(listview_Adapter);
        //讀取我的最愛
        Schedule_Database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<HashMap<String, String>> temp = (ArrayList<HashMap<String, String>>) dataSnapshot.child(currentUser.getUid()).child("我的景點").getValue();
                if(temp!=null){
                    list_spinner.clear();
                    for(int i=0;i<temp.size();i++){
                        if(schedule_item.contains(temp.get(i))==false) {
                            schedule_item.add(temp.get(i));
                        }
                    }
                    list_spinner.add("請選擇");
                    tostringarray(schedule_item);

                }
                else{
                    list_spinner.add("無加入景點");
                }

                spinner_Adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, list_spinner);
                schedule_spinner.setAdapter(spinner_Adapter);
                setSpinner();
                //copylist("copy");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
        //監聽
        timebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);
                new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if(minute <10){
                            timebutton.setText(hourOfDay + ":0" + minute);
                        }
                        else{
                            timebutton.setText(hourOfDay + ":" + minute);
                        }
                    }
                }, hour, minute, false).show();
            }
        });
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String data = String.valueOf(timebutton.getText());
                if(data.equals("選擇時間")){
                    Toast.makeText(getActivity(), "時間未選擇!", Toast.LENGTH_LONG).show();
                }
                else{
                    //Toast.makeText(getActivity(), schedule_spinner.getSelectedItem().toString(), Toast.LENGTH_LONG).show();
                    if (schedule_spinner.getSelectedItem().toString().equals("請選擇"))
                    {
                        Toast.makeText(getActivity(), "景點未選擇!", Toast.LENGTH_LONG).show();
                    }
                    else{
                        data = data + " ------ " + schedule_spinner.getSelectedItem().toString();
                        listview_data.add(data);
                        //sort(data);
                        Log.d("listview_data:", String.valueOf(listview_data));
                        listview_Adapter.notifyDataSetChanged();
                        //listview_Adapter.notifyDataSetChanged();
                        //spinner_Adapter.remove(schedule_spinner.getSelectedItem().toString());
                    }

                }

            }
        });

        return view;
    }

    private void sort(String data){
        int hourdata  = Integer.parseInt(data.substring(0,2));
        int  mindata = Integer.parseInt(data.substring(3,5));
        List<String> listview_temp = listview_data;
        int min;
        int hour;
        for(int i = 0;i<listview_temp.size();i++){
            hour = Integer.parseInt(listview_data.get(i).substring(0,2));
            min = Integer.parseInt(listview_data.get(i).substring(2,4));
            if(hour > hourdata){
                Log.d("listview_data1:", "hour > hourdata");
                listview_temp.add(data);
            }
            else if(min > mindata ){
                Log.d("listview_data1:", "min > mindata");
                listview_temp.add(data);
            }
            else{
                Log.d("listview_data1:", "other");
                listview_temp.add(listview_data.get(i));
            }
        }
        listview_data = listview_temp;

    }


    private void setSpinner(){
        schedule_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id){
                //Toast.makeText(getActivity(), "您選擇"+ select_number, Toast.LENGTH_LONG).show();
                //select_ItemId = String.valueOf(adapterView.getSelectedItemId());

            }
            public void onNothingSelected(AdapterView<?>  arg0) {
                //Toast.makeText(getActivity(), "您沒有選擇任何項目", Toast.LENGTH_LONG).show();
            }
        });
    }
    //取得景點名稱

    public  void tostringarray(final ArrayList<HashMap<String,String>>arrayLists){
        int lenlists = arrayLists.size();
        Log.d("tostringarray:", String.valueOf(lenlists));
        for(int i = 0;i<lenlists;i++){
            list_spinner.add(arrayLists.get(i).get("景點名稱"));
        }
        Log.d("tostringarray:", String.valueOf(list_spinner));

    }
    private void writeData_schedule(String theKey, HashMap<String, String> attData,ArrayList<HashMap<String, String>>arrayLists){
        //存入歷史紀錄、我的最愛
        Schedule_Database = FirebaseDatabase.getInstance().getReference(currentUser.getUid()).child(theKey).child(String.valueOf(arrayLists.size()-1));
        Schedule_Database.setValue(attData);
    }

}
