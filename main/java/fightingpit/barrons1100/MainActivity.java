package fightingpit.barrons1100;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import fightingpit.barrons1100.util.IabHelper;
import fightingpit.barrons1100.util.IabResult;
import fightingpit.barrons1100.util.Inventory;
import fightingpit.barrons1100.util.Purchase;

public class MainActivity extends Activity {

    @Bind(R.id.tab_nav_pager) ViewPager mViewPager;
    @Bind(R.id.adView) AdView mAdView;
    TextView aReminderTimeView;
    private TabsPagerAdapter mAdapter;
    private MenuItem aExpandButton;
    private MenuItem mResetButton;
    public Context context;
    boolean mUpdateView =false; // Boolean to control the update of view in cached Tabs
    IabHelper mHelper;  // Billing Helper


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initializeAppSettings();

        mAdapter = new TabsPagerAdapter(getFragmentManager(),this);
        mViewPager.setAdapter(mAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setUpdateView(true);
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
                handleRating();


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        context = getBaseContext();
        SharedPreferences aSharedPref = getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        String aIsAppPurchased = aSharedPref.getString("is_app_purchased", "");
        if(aIsAppPurchased.equals("")){
            // App is running for first time, check if app has been purchased or not.
            checkAppPurchase(true);
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
                /**
                 * App is not running for first time, (just to be sure)check if app has been purchased or not.
                 * If user cancels order, this will reset the shared pref, thus making quiz option unavailable.
                 */
                checkAppPurchase(false);
            }
        }
    }

    void checkAppPurchase(final Boolean iWithToast){
        try {
            // Setup Billing Helper
            mHelper = new IabHelper(this, getResources().getString(R.string.base64EncodedPublicKey));
            mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                public void onIabSetupFinished(IabResult result) {
                    if (!result.isSuccess()) {
                        Log.d("ABG", getResources().getString(R.string.prob_inApp_billing) + result);
                        if(iWithToast){
                            Toast.makeText(getBaseContext(),
                                    getResources().getString(R.string.cannot_fetch_premium_status),
                                    Toast.LENGTH_LONG).show();
                        }
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
            if(iWithToast) {
                Toast.makeText(getBaseContext(),
                        getResources().getString(R.string.cannot_fetch_premium_status),
                        Toast.LENGTH_LONG).show();
            }
            // Exception Caught. Show ads.
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
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
                Log.d("ABG", "Purchase Query Success");
                SharedPreferences aSharedPref = getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
                SharedPreferences.Editor aEditor = aSharedPref.edit();

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

        if(id == R.id.action_popup_menu){
            handlePopupMenu();
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
        Log.d("ABG", "OnActivityResultCalled. RequestCode:" + String.valueOf(requestCode) + " : resultCode:" + String.valueOf(resultCode));
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
                mWordListFromDb = aDBHelper.resetWordList("a");
            } else if(aFilerPref.matches("[A-Z]")){
                mWordListFromDb = aDBHelper.resetWordListByAlphabet(aFilerPref, "a");
            }else if (aFilerPref.matches("[0123456789]{1,2}")){
                mWordListFromDb = aDBHelper.resetWordListBySet(String.valueOf(Integer.parseInt(aFilerPref)), "a");
            }
            aDBHelper.close();
            for (GenericContainer aWordInfo : mWordListFromDb) {
                aDBHelper.updateProgress(aWordInfo.getWord(), getResources().getInteger(R.integer.max_progress_val));
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            if(dialog != null && dialog.isShowing()){
                dialog.dismiss();
            }
            // Set Tab name as empty, so that all tabs get refreshed.
            mAdapter.setTabName("");
            mAdapter.notifyDataSetChanged();

        }
    }

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener
            = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase)
        {
            if (result.isFailure()) {
                Log.d("ABG", "Error purchasing: " + result);
                Toast.makeText(getBaseContext(), "Error while purchasing.", Toast.LENGTH_LONG).show();
            }
            else if (purchase.getSku().equalsIgnoreCase("premium")) {
                Toast.makeText(getBaseContext(), "Premium version activated.", Toast.LENGTH_LONG).show();
                SharedPreferences aSharedPref = getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
                SharedPreferences.Editor aEditor = aSharedPref.edit();
                aEditor.putString("is_app_purchased", "y");
                aEditor.commit();
                Intent i = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage(getPackageName());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }else{
                Log.d("ABG", "Error purchasing: " + result);
                Toast.makeText(getBaseContext(), "Error while purchasing.", Toast.LENGTH_LONG).show();
            }
        }
    };

    public boolean getUpdateView() {
        return mUpdateView;
    }

    public void setUpdateView(boolean iToUpdateView) {
        this.mUpdateView = iToUpdateView;
    }

    public void updateTabs(String iClassName){
        mAdapter.setTabName(iClassName);
        mAdapter.notifyDataSetChanged();
    }


    public void initializeAppSettings() {
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

        // If App is running for first time, set Reminder Preference
        String aReminderPref = aSharedPref.getString("reminder_pref", "");
        if(aReminderPref.equalsIgnoreCase("")){
            aEditor.putString("reminder_pref", "y");
            aEditor.commit();
        }

        // If App is running for first time, set Reminder Time
        String aReminderTime = aSharedPref.getString("reminder_time", "");
        if(aReminderTime.equalsIgnoreCase("")){
            setReminderTime("10:30 AM", 10, 30);
        }

        // If App is running for first time, set Number of words mastered.
        Integer aNumberOfWordsMastered = aSharedPref.getInt("number_words_mastered", 0);
        if(aNumberOfWordsMastered == 0){
            aEditor.putInt("number_words_mastered", 0);
            aEditor.commit();
        }

        // If App is running for first time, set ask for rating
        String aAskForRating = aSharedPref.getString("ask_rating", "");
        if(aAskForRating.equalsIgnoreCase("")){
            aEditor.putString("ask_rating", "y");
            aEditor.commit();
        }
    }

    void handleRating(){

        final SharedPreferences aSharedPref = getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        final SharedPreferences.Editor aEditor = aSharedPref.edit();
        String aAskForRating = aSharedPref.getString("ask_rating", "");
        if(aAskForRating.equalsIgnoreCase("y")){
            Integer aNumberOfWordsMastered = aSharedPref.getInt("number_words_mastered", 0);
            if(aNumberOfWordsMastered > 25){

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                View aRateDialogView = LayoutInflater.from(getBaseContext()).inflate(R.layout.dialog_rate_app,null);
                builder.setView(aRateDialogView);

                final AlertDialog alert = builder.create();
                alert.show();

                Button aYes = (Button) aRateDialogView.findViewById(R.id.bt_dra_yes);
                aYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alert.dismiss();
                        aEditor.putInt("number_words_mastered", 0);
                        aEditor.commit();
                        rateApplication();
                    }
                });

                Button aLater = (Button) aRateDialogView.findViewById(R.id.bt_dra_later);
                aLater.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alert.dismiss();
                        aEditor.putInt("number_words_mastered", 0);
                        aEditor.commit();
                    }
                });

                Button aNever = (Button) aRateDialogView.findViewById(R.id.bt_dra_never);
                aNever.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alert.dismiss();
                        aEditor.putInt("number_words_mastered", 0);
                        aEditor.putString("ask_rating", "n");
                        aEditor.commit();
                    }
                });

            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    void handlePopupMenu(){

        // Inflate a new popup.
        final PopupWindow popup = new PopupWindow(getBaseContext());
        final View aPopUpView = getLayoutInflater().inflate(R.layout.popup_reminder, null);

        // Get Resources from View
        TextView aRateApp = (TextView) aPopUpView.findViewById(R.id.tv_pr_rate_app);
        Switch aReminderToggle = (Switch) aPopUpView.findViewById(R.id.sw_pr_reminder);
        final LinearLayout aLayoutReminderTime = (LinearLayout) aPopUpView.findViewById(R.id.ll_pr_reminder_time);
        aReminderTimeView = (TextView) aPopUpView.findViewById(R.id.tv_pr_reminder_time_view);

        final SharedPreferences aSharedPref = getSharedPreferences("AppSettings", Context.MODE_PRIVATE);

        // Show Proper status of Switch.
        String aReminderPreference = aSharedPref.getString("reminder_pref", "");
        if("y".equalsIgnoreCase(aReminderPreference)){
            aReminderToggle.setChecked(true);
            aLayoutReminderTime.setVisibility(View.VISIBLE);
        }else{
            aReminderToggle.setChecked(false);
            aLayoutReminderTime.setVisibility(View.GONE);
        }

        // Set proper reminder time.
        String aReminderTime = aSharedPref.getString("reminder_time", "");
        aReminderTimeView.setText(aReminderTime);

        // Change Reminder Time
        aReminderTimeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getFragmentManager(), "timePicker");
            }
        });

        // Display the popup, anchored to Menu button.
        popup.setContentView(aPopUpView);
        popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setOutsideTouchable(true);
        popup.setFocusable(true);
        popup.setBackgroundDrawable(new ColorDrawable(0xFFF2F2F2));
        popup.setElevation(12);
        View aMenuView = findViewById(R.id.action_popup_menu);
        popup.showAsDropDown(aMenuView);

        // User enables, or disables reminder
        aReminderToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor aEditor = aSharedPref.edit();
                if(isChecked){
                    aLayoutReminderTime.setVisibility(View.VISIBLE);
                    aEditor.putString("reminder_pref", "y");
                    aEditor.commit();
                }else{
                    aLayoutReminderTime.setVisibility(View.GONE);
                    aEditor.putString("reminder_pref", "n");
                    aEditor.commit();
                    unsetReminder();
                }
            }
        });

        // User wants to rate application.
        aRateApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
                rateApplication();
            }
        });

        TextView aHelp = (TextView) aPopUpView.findViewById(R.id.tv_pr_help);
        aHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivityContext());
                View aRateDialogView = LayoutInflater.from(getBaseContext()).inflate(R.layout.help_layout, null);
                builder.setView(aRateDialogView);
                final AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    Context getActivityContext()
    {
        return this;
    }

    void rateApplication(){
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
        }
    }


    // Time-picker for Reminder.
    static public class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
            String aTimeFormat = new String();
            if(hourOfDay<12){
                String aHour = String.valueOf(hourOfDay);
                if(aHour.length()<2){
                    aHour = "0" + aHour;
                }
                String aMinute = String.valueOf(minute);
                if(aMinute.length() <2){
                    aMinute = "0" + aMinute;
                }
                aTimeFormat = aHour + ":" + aMinute + " AM";
            }else{
                String aHour = String.valueOf(hourOfDay-12);
                if(aHour.length()<2){
                    aHour = "0" + aHour;
                }
                String aMinute = String.valueOf(minute);
                if(aMinute.length() <2){
                    aMinute = "0" + aMinute;
                }
                aTimeFormat = aHour + ":" + aMinute + " PM";
            }
            ((MainActivity) getActivity()).setReminderTime(aTimeFormat, hourOfDay, minute);
        }
    }

    // Disables reminder notifications.
    void unsetReminder(){
        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent myIntent = new Intent(this , NotificationService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, myIntent, 0);
        alarmMgr.cancel(pendingIntent);
    }

    // Set Reminder Time and Notification
    void setReminderTime(String iText, int iHourOfDay, int iMinute){

        // Set Shared preference for future use.
        final SharedPreferences aSharedPref = getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        SharedPreferences.Editor aEditor = aSharedPref.edit();
        aEditor.putString("reminder_time", iText);
        aEditor.commit();

        // Set time in popup menu.
        if(aReminderTimeView!=null){
            aReminderTimeView.setText(iText);
        }

        // Set Actual repeating notification reminder.
        Intent myIntent = new Intent(this , NotificationService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, myIntent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, iHourOfDay);
        calendar.set(Calendar.MINUTE, iMinute);

        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmMgr.cancel(pendingIntent);
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);
    }
}