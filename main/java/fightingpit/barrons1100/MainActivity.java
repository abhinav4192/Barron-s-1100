package fightingpit.barrons1100;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity {

    private ViewPager mViewPager;
    private TabsPagerAdapter mAdapter;
    MenuItem aExpandButton;
    MenuItem mResetButton;
    private final Integer mMaxProgress = 3;
    public Context context;

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
        mAdapter = new TabsPagerAdapter(getFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.tab_nav_pager);
        mViewPager.setAdapter(mAdapter);

        // Specify that tabs should be displayed in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setHomeButtonEnabled(false);

        // Tab Navigation Select.
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                final ActionBar.Tab aTab = tab;
                if(aExpandButton!=null){
                    if(tab.getPosition()==0){
                        aExpandButton.setVisible(true);
                    }else{
                        aExpandButton.setVisible(false);
                    }
                }
                if(mResetButton!=null){
                    if(!(tab.getPosition()==0)){
                        mResetButton.setVisible(true);
                    }else{
                        mResetButton.setVisible(false);
                    }
                }
                mViewPager.setCurrentItem(tab.getPosition());
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
                if(mResetButton!=null){
                    if(!(position==0)){
                        mResetButton.setVisible(true);
                    }else{
                        mResetButton.setVisible(false);
                    }
                }
                final Integer aPos = position;
                if(aPos==0){
                    updateTabs("WordListFragment");
                }else if(aPos==2){
                    updateTabs("QuizFragment");
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

        AdView mAdView = (AdView) findViewById(R.id.adView);
        if(true){
            // If user has purchased the app.
            // Hide advertisement.
            mAdView.setVisibility(View.GONE);
            // Make Layout Full Screen
            int sizeInDP = 8;
            int marginInDp = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, sizeInDP, getResources()
                            .getDisplayMetrics());
            RelativeLayout.LayoutParams aCVParams = new RelativeLayout.LayoutParams(CardView.LayoutParams.MATCH_PARENT,
                    CardView.LayoutParams.MATCH_PARENT);
            aCVParams.setMargins(0, 0, 0, marginInDp);
            mViewPager.setLayoutParams(aCVParams);
        }else{
            // Loading the Advertisement.
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }
        context = getBaseContext();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);

        // Setting Proper Icon for Expand Contract List.
        aExpandButton = menu.getItem(0);
        mResetButton = menu.getItem(1);
        mResetButton.setVisible(false);
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
            mAdapter.setTabName("");
            mAdapter.notifyDataSetChanged();
        }
        if(id==R.id.action_reset){
            // Getting Desired Words
            SharedPreferences aSharedPref = this.getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
            String aFilerPref = aSharedPref.getString("filter_pref", "");
            String mSetSelectorText = new String();
            if(aFilerPref.equalsIgnoreCase("all") || aFilerPref.equalsIgnoreCase("")){
                mSetSelectorText = "All Words";
            } else if(aFilerPref.matches("[A-Z]")){
                mSetSelectorText = "Alphabet " + aFilerPref;
            }else if (aFilerPref.matches("[0123456789]{1,2}")){
                mSetSelectorText = "Set " + aFilerPref;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("This will reset progress for \"" + mSetSelectorText + "\". Do you want to continue?");
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    new ResetProgress().execute(null, null, null);
                    dialog.dismiss();
                }

            });
            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Do nothing
                    dialog.dismiss();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Check which request we're responding to
        if (requestCode == 100) {
            // Settings Activity finished.
            mAdapter.setTabName("");
            mAdapter.notifyDataSetChanged();
        }
    }

    public void updateTabs(String iClassName){
        mAdapter.setTabName(iClassName);
        mAdapter.notifyDataSetChanged();
    }

    private class ResetProgress extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog = new ProgressDialog(MainActivity.this);
        @Override
        protected void onPreExecute() {
            dialog.setMessage("Resetting progress. This might take long time. Please be patient.");
            dialog.show();
            super.onPreExecute();
        }
        protected Void doInBackground(Void... args) {
            SharedPreferences aSharedPref = context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
            String aFilerPref = aSharedPref.getString("filter_pref", "");

            List<GenericContainer> mWordListFromDb = new ArrayList<>();
            final DatabaseHelper aDBHelper = new DatabaseHelper(context);
            if(aFilerPref.equalsIgnoreCase("all") || aFilerPref.equalsIgnoreCase("")){
                mWordListFromDb = aDBHelper.getWordList("a");
            } else if(aFilerPref.matches("[A-Z]")){
                mWordListFromDb = aDBHelper.getWordListByAlphabet(aFilerPref,"a");
            }else if (aFilerPref.matches("[0123456789]{1,2}")){
                mWordListFromDb = aDBHelper.getWordListBySet(String.valueOf(Integer.parseInt(aFilerPref)), "a");
            }
            aDBHelper.close();
            for (GenericContainer aWordInfo : mWordListFromDb) {
                aDBHelper.updateProgress(aWordInfo.getWord(), mMaxProgress);
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            if(dialog != null && dialog.isShowing()){
                dialog.dismiss();
            }
            mAdapter.setTabName("");
            mAdapter.notifyDataSetChanged();

        }
    }
}