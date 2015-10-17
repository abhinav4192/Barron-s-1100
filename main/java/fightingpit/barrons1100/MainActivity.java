package fightingpit.barrons1100;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;



public class MainActivity extends FragmentActivity {

    private ViewPager mViewPager;
    private TabsPagerAdapter mAdapter;
    MenuItem aExpandButton;

    // Tab titles
    private String[] tabs = { "Word List", "Flash Cards", "Quiz" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences aSharedPref = getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        SharedPreferences.Editor aEditor = aSharedPref.edit();

        // If App is running for first time, set list Preference
        String aListViewPref = aSharedPref.getString("list_view_pref", "");
        if(aListViewPref.equalsIgnoreCase("")){
            aEditor.putString("list_view_pref", "contracted");
            aEditor.commit();
        }

        // If App is running for first time, set fav Preference
        String aFavPref = aSharedPref.getString("fav_pref", "");
        if(aFavPref.equalsIgnoreCase("")){
            aEditor.putString("fav_pref", "a");
            aEditor.commit();
        }

        // If App is running for first time, set Filter Preference
        String aFilerPref = aSharedPref.getString("filter_pref", "");
        if(aFilerPref.equalsIgnoreCase("")){
            aEditor.putString("filter_pref", "All");
            aEditor.commit();
        }


        // If App is running for first time, set Sort Preference
        String aSortPref = aSharedPref.getString("sort_pref", "");
        if(aSortPref.equalsIgnoreCase("")){
            aEditor.putString("sort_pref", "alpha");
            aEditor.commit();
        }

        final ActionBar actionBar = getActionBar();

        // Initilization
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.tab_nav_pager);
        mViewPager.setAdapter(mAdapter);

        // Specify that tabs should be displayed in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setHomeButtonEnabled(false);

        // Tab Navigation Select.
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                if(aExpandButton!=null){
                    if(tab.getPosition()==0){
                        aExpandButton.setVisible(true);
                    }else{
                        aExpandButton.setVisible(false);
                    }
                }
                mViewPager.setCurrentItem(tab.getPosition());
                // show the given tab
            }

            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
                // hide the given tab
            }

            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
                // probably ignore this event
            }
        };

        // Adding Tabs
        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name)
                    .setTabListener(tabListener));
        }


        // Tab Navigation Swipe
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
                actionBar.setSelectedNavigationItem(position);
                if(aExpandButton!=null){
                    if(position==0){
                        aExpandButton.setVisible(true);
                    }else{
                        aExpandButton.setVisible(false);
                    }
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

        // Loading the Advertisement.
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);

        // Setting Proper Icon for Expand Contract List.
        aExpandButton = menu.getItem(0);
        SharedPreferences aSharedPref = getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        String aListViewPref = aSharedPref.getString("list_view_pref", "");
        if (aListViewPref.equalsIgnoreCase("expanded")){
            aExpandButton.setIcon(R.drawable.ic_contract_list);
        } else if (aListViewPref.equalsIgnoreCase("contracted")){
            aExpandButton.setIcon(R.drawable.ic_expand_list);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent i = new Intent(this,SettingsActivity.class);
            startActivityForResult(i, 100);
            return true;
        }

        if(id==R.id.action_exp_cont){
            SharedPreferences aSharedPref = getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
            SharedPreferences.Editor aEditor = aSharedPref.edit();
            String aListViewPref = aSharedPref.getString("list_view_pref", "");
            if (aListViewPref.equalsIgnoreCase("expanded")){
                item.setIcon(R.drawable.ic_expand_list);
                aEditor.putString("list_view_pref", "contracted");
            } else if (aListViewPref.equalsIgnoreCase("contracted")){
                item.setIcon(R.drawable.ic_contract_list);
                aEditor.putString("list_view_pref", "expanded");
            }
            aEditor.commit();
            mAdapter.notifyDataSetChanged();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Check which request we're responding to
        if (requestCode == 100) {
            // Settings Activity finished.
            mAdapter.notifyDataSetChanged();
        }
    }

}