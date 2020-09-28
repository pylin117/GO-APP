package com.go.go;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class More_fragment extends Fragment{
    Button msignOutButton;
    FirebaseAuth mAuth;
    ListView moreMenu;
    ListAdapter menuAdapter;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.more, container, false);
        mAuth = FirebaseAuth.getInstance();
        String[] listArray={"歷史紀錄","我的最愛"};
        moreMenu=view.findViewById(R.id.otherAction_menu);
        menuAdapter=new ArrayAdapter<String>(getActivity(), R.layout.list_item, listArray );
        moreMenu.setAdapter(menuAdapter);
        moreMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        Intent toHistory = new Intent();
                        toHistory.setClass(view.getContext(), History_record_Activity.class);
                        startActivity(toHistory);
                        break;
                    case 1:
                        Intent toFavourite = new Intent();
                        toFavourite.setClass(view.getContext(),My_favourite_Activity.class);
                        startActivity(toFavourite);
                        break;
                    default:
                        break;
                }
            }
        });
        msignOutButton = view.findViewById(R.id.signOut_button);
        msignOutButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                if( FirebaseAuth.getInstance().getCurrentUser() == null){
                    Toast.makeText( getActivity()  , "已登出 " , Toast.LENGTH_LONG).show();
                    Intent intent = new Intent();
                    intent.setClass( getActivity() ,Login_Activity.class);
                    startActivity(intent);
                }
            }
        });
        return view;
    }

}
