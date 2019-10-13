package com.example.feedback;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.astuetz.PagerSlidingTabStrip;

public class Activity_Record_Voice extends AppCompatActivity {

    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    int indexOfProject;
    int indexOfStudent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__record__voice);
        pager = (ViewPager)findViewById(R.id.pager);
        pager.setAdapter(new MyAdapter(getSupportFragmentManager()));
        tabs = (PagerSlidingTabStrip)findViewById(R.id.tabs);
        tabs.setViewPager(pager);

        Intent intent = getIntent();
        indexOfProject = Integer.parseInt(intent.getStringExtra("indexOfProject"));
        indexOfStudent = Integer.parseInt(intent.getStringExtra("indexOfStudent"));
    }

    // set two get method. it is convenient for acquire two index in the fragment
    public int getIndexOfProject(){
        return indexOfProject;
    }

    public int getIndexOfStudent(){
        return indexOfStudent;
    }

    public class MyAdapter extends FragmentPagerAdapter {
        private String[] titles = { getString(R.string.tab_title_record),
                getString(R.string.tab_title_saved_recordings) };

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position){
                case 0:{
                    return RecordFragement.newInstance(position);
                }
                case 1:{
                    return FileViewerFragment.newInstance(position);
                }
            }
            return null;
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }

    public Activity_Record_Voice() {
    }
}
