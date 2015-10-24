package fightingpit.barrons1100;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
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
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;

import fightingpit.barrons1100.util.IabHelper;
import fightingpit.barrons1100.util.IabResult;
import fightingpit.barrons1100.util.Inventory;
import fightingpit.barrons1100.util.Purchase;

public class MainActivity extends FragmentActivity {

    private ViewPager mViewPager;
    private TabsPagerAdapter mAdapter;
    MenuItem aExpandButton;
    MenuItem mResetButton;
    private final Integer mMaxProgress = 3;
    public Context context;
    private AdView mAdView;

    // Billing Helper
    IabHelper mHelper;

    // Tab titles
    private String[] tabs = { "Word List", "Flash Cards", "Quiz" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAdView = (AdView) findViewById(R.id.adView);

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
        context = getBaseContext();

        String aIsAppPurchased = aSharedPref.getString("is_app_purchased", "");
        if(aIsAppPurchased.equals("")){
            // App is running for first time, check if app has been purchased or not.
            try {
                // Setup Billing Helper
                mHelper = new IabHelper(this, getResources().getString(R.string.base64EncodedPublicKey));
                mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                    public void onIabSetupFinished(IabResult result) {
                        if (!result.isSuccess()) {
                            Log.d("ABG", "Problem setting up In-app Billing: " + result);
                            Toast.makeText(getBaseContext(),
                                    "Cannot fetch premium status.Connect to Internet and start Application again",
                                    Toast.LENGTH_LONG).show();

                            // Error. Show advertisements.
                            AdRequest adRequest = new AdRequest.Builder().build();
                            mAdView.loadAd(adRequest);
                        } else {
                            Log.d("ABG", "Helper Setup Complete");
                            mHelper.queryInventoryAsync(mGotInventoryListener);
                        }
                    }
                });
            }catch (Exception e){
                Log.d("ABG", "Exception caught while Setting up Helper:" + e);
                Toast.makeText(getBaseContext(),
                        "Cannot fetch premium status.Connect to Internet and start Application again",
                        Toast.LENGTH_LONG).show();

                // Exception Caught. Show ads.
                AdRequest adRequest = new AdRequest.Builder().build();
                mAdView.loadAd(adRequest);

            }
        }else{
            // Purchase Status already known.
            if("y".equals(aIsAppPurchased)){
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
        }
    }

    IabHelper.QueryInventoryFinishedListener mGotInventoryListener
            = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result,
                                             Inventory inventory) {

            if (result.isFailure()) {
                Log.d("ABG", "Purchase Query Failure:" + result);
                    // Loading the Advertisement.
                    AdRequest adRequest = new AdRequest.Builder().build();
                    mAdView.loadAd(adRequest);
            }
            else {
                SharedPreferences aSharedPref = getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
                SharedPreferences.Editor aEditor = aSharedPref.edit();
                String aIsAppPurchased = aSharedPref.getString("is_app_purchased", "");

                // does the user have the premium upgrade?
                boolean mIsPremium = inventory.hasPurchase("premium");
                if(mIsPremium){
                    Log.d("ABG", "Is Purchased");
                    aEditor.putString("is_app_purchased", "y");
                    aEditor.commit();
                    // User has purchased the app.
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
                    Log.d("ABG", "Not Purchased");
                    aEditor.putString("is_app_purchased", "n");
                    aEditor.commit();

                    // Loading the Advertisement.
                    AdRequest adRequest = new AdRequest.Builder().build();
                    mAdView.loadAd(adRequest);
                }
            }
        }
    };


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
            aExpandButton.setIcon(R.drawable.ic_contract_white);
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
                item.setIcon(R.drawable.ic_contract_white);
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
        Log.d("ABG","OnActivityResultCalled. RequestCode:" + String.valueOf(requestCode) + " : resultCode:" + String.valueOf(resultCode));
        // Check which request we're responding to
        if (requestCode == 100) {
            // Settings Activity finished.
            mAdapter.setTabName("");
            mAdapter.notifyDataSetChanged();
        }
        if(requestCode==10001){
            Fragment fragment = getFragmentManager().findFragmentByTag("BuyFragment");
            ((BuyAppFragment) fragment).onActivityResult(requestCode,resultCode,data);
        }
    }

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener
            = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase)
        {
            if (result.isFailure()) {
                Log.d("ABG", "Error purchasing: " + result);
                Toast.makeText(getBaseContext(), "Exception", Toast.LENGTH_LONG).show();
                return;
            }
            else if (purchase.getSku().equalsIgnoreCase("premium")) {
                Log.d("ABG", "Purchase Done 1");
                Toast.makeText(getBaseContext(), "Purchase Done", Toast.LENGTH_LONG).show();
                SharedPreferences aSharedPref = getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
                SharedPreferences.Editor aEditor = aSharedPref.edit();
                aEditor.putString("is_app_purchased", "y");
                aEditor.commit();
                Intent i = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage(getPackageName());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                return;
            }else{
                Log.d("ABG", "Purchase Done 2");
                Toast.makeText(getBaseContext(), "Purchase Done", Toast.LENGTH_LONG).show();
                SharedPreferences aSharedPref = getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
                SharedPreferences.Editor aEditor = aSharedPref.edit();
                aEditor.putString("is_app_purchased", "y");
                aEditor.commit();
                Intent i = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage(getPackageName());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                return;
            }
        }
    };

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