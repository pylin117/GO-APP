package com.go.go;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;


public class HomeActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    ViewPager viewPager;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView=findViewById(R.id.bottom_navigation);
        viewPager=findViewById(R.id.viewPager);
        List<Fragment> fragments=new ArrayList<>();
        fragments.add(new Search_fragment());
        fragments.add(new Recommend_fragment());
        fragments.add(new Schedule_fragment());
        fragments.add(new More_fragment());
        FragmentAdatper adapter=new FragmentAdatper(fragments,getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()){
                    case R.id.action_search:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.action_recommend:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.action_tour:
                        viewPager.setCurrentItem(2);
                        break;
                    case R.id.action_more:
                        viewPager.setCurrentItem(3);
                        break;
                }
                return false;
            }

        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                bottomNavigationView.getMenu().getItem(viewPager.getCurrentItem()).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if(state==2){
                    bottomNavigationView.getMenu().getItem(viewPager.getCurrentItem()).setChecked(true);
                }
            }
        });
    }
    @Override
    protected void onResume(){
        int id =getIntent().getIntExtra("id",0);
        id=id-1;
        switch (id){
            case 0:
                viewPager.setCurrentItem(0);
                bottomNavigationView.getMenu().getItem(0).setChecked(true);
                break;
            case 1:
                viewPager.setCurrentItem(1);
                bottomNavigationView.getMenu().getItem(1).setChecked(true);
                break;
            case 2:
                viewPager.setCurrentItem(2);
                bottomNavigationView.getMenu().getItem(2).setChecked(true);
                break;
            case 3:
                viewPager.setCurrentItem(3);
                bottomNavigationView.getMenu().getItem(3).setChecked(true);
                break;
        }
        super.onResume();
    }

}
