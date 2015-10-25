package fightingpit.barrons1100;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;


import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends Activity implements AdapterView.OnItemSelectedListener {

    private ImageView mSortAlphabetically;
    private ImageView mShuffle;
    private Spinner mFilerSelector;
    private boolean isFirstTime = true;

    private ImageView mAllButton;
    private ImageView mFavButton;
    private ImageView mUnmarkedButton;
    private Button mDone;

    private String aSortPref = new String();
    private String aFavPref = new String();
    private String aFilterPref = new String();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SharedPreferences aSharedPref = getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        aSortPref = aSharedPref.getString("sort_pref", "");
        aFavPref = aSharedPref.getString("fav_pref","");
        aFilterPref = aSharedPref.getString("filter_pref","");

        mSortAlphabetically = (ImageView) findViewById(R.id.iv_as_aplha);
        mShuffle = (ImageView) findViewById(R.id.iv_as_shuffle);

        // Set Proper Image for Sort
        if(aSortPref.equalsIgnoreCase("alpha")){
            mSortAlphabetically.setImageResource(R.drawable.ic_atoz_green);

        } else if(aSortPref.equalsIgnoreCase("shuffle")){
            mShuffle.setImageResource(R.drawable.ic_shuffle_icon_green);
        }

        mSortAlphabetically.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aSortPref = "alpha";
                mSortAlphabetically.setImageResource(R.drawable.ic_atoz_green);
                mShuffle.setImageResource(R.drawable.ic_shuffle_icon_grey);

            }
        });


        mShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aSortPref = "shuffle";
                mSortAlphabetically.setImageResource(R.drawable.ic_atoz_grey);
                mShuffle.setImageResource(R.drawable.ic_shuffle_icon_green);
            }
        });

        mFilerSelector = (Spinner) findViewById(R.id.sp_as_filter_selector);
        populateFilterSelector();
        mFilerSelector.setOnItemSelectedListener(this);

        mAllButton = (ImageView) findViewById(R.id.iv_as_fav_all);
        mFavButton = (ImageView) findViewById(R.id.iv_as_fav_marked);
        mUnmarkedButton = (ImageView) findViewById(R.id.iv_as_fav_unmarked);

        // Set Proper Image for Favourite Pref
        if(aFavPref.equalsIgnoreCase("a")){
            mAllButton.setImageResource(R.drawable.ic_circle_outline_green);
        }else if(aFavPref.equalsIgnoreCase("m")){
            mFavButton.setImageResource(R.drawable.ic_star_solid_green);
        }else{
            mUnmarkedButton.setImageResource(R.drawable.ic_star_outline_green);
        }
        mAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aFavPref = "a";
                mAllButton.setImageResource(R.drawable.ic_circle_outline_green);
                mFavButton.setImageResource(R.drawable.ic_star_solid_grey);
                mUnmarkedButton.setImageResource(R.drawable.ic_star_outline_grey);
            }
        });
        mFavButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aFavPref = "m";
                mAllButton.setImageResource(R.drawable.ic_circle_outline_grey);
                mFavButton.setImageResource(R.drawable.ic_star_solid_green);
                mUnmarkedButton.setImageResource(R.drawable.ic_star_outline_grey);
            }
        });

        mUnmarkedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aFavPref = "u";
                mAllButton.setImageResource(R.drawable.ic_circle_outline_grey);
                mFavButton.setImageResource(R.drawable.ic_star_solid_grey);
                mUnmarkedButton.setImageResource(R.drawable.ic_star_outline_green);
            }
        });

        mDone = (Button) findViewById(R.id.bt_as_done);
        mDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences inSharedPref = getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
                SharedPreferences.Editor aEditor = inSharedPref.edit();
                aEditor.putString("sort_pref",aSortPref);
                aEditor.putString("fav_pref",aFavPref);
                aEditor.putString("filter_pref",aFilterPref);
                aEditor.apply();
                finish();
            }
        });
    }

    // Method to set Shared Preferences for Select Word Criteria.
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        if(isFirstTime){
            isFirstTime = false;
        }else {

            if(parent.getItemAtPosition(pos).toString().equals("All")){
                aFilterPref = "All";
            }
            if(parent.getItemAtPosition(pos).toString().matches("(Alphabet )[A-Z]")){
                aFilterPref = parent.getItemAtPosition(pos).toString().substring(9);
            }
            if(parent.getItemAtPosition(pos).toString().matches("(Set )[0123456789]{2}")){
                aFilterPref = String.valueOf(Integer.parseInt(parent.getItemAtPosition(pos).toString().substring(4)));
            }
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    private void populateFilterSelector(){
        List<String> aSelectionList = new ArrayList<>();
        aSelectionList.add("All");
        aSelectionList.add("Alphabet A");
        aSelectionList.add("Alphabet B");
        aSelectionList.add("Alphabet C");
        aSelectionList.add("Alphabet D");
        aSelectionList.add("Alphabet E");
        aSelectionList.add("Alphabet F");
        aSelectionList.add("Alphabet G");
        aSelectionList.add("Alphabet H");
        aSelectionList.add("Alphabet I");
        aSelectionList.add("Alphabet J");
        aSelectionList.add("Alphabet L");
        aSelectionList.add("Alphabet M");
        aSelectionList.add("Alphabet N");
        aSelectionList.add("Alphabet O");
        aSelectionList.add("Alphabet P");
        aSelectionList.add("Alphabet Q");
        aSelectionList.add("Alphabet R");
        aSelectionList.add("Alphabet S");
        aSelectionList.add("Alphabet T");
        aSelectionList.add("Alphabet U");
        aSelectionList.add("Alphabet V");
        aSelectionList.add("Alphabet W");
        aSelectionList.add("Alphabet Y");
        aSelectionList.add("Alphabet Z");
        aSelectionList.add("Set 01");
        aSelectionList.add("Set 02");
        aSelectionList.add("Set 03");
        aSelectionList.add("Set 04");
        aSelectionList.add("Set 05");
        aSelectionList.add("Set 06");
        aSelectionList.add("Set 07");
        aSelectionList.add("Set 08");
        aSelectionList.add("Set 09");
        aSelectionList.add("Set 10");
        aSelectionList.add("Set 11");
        aSelectionList.add("Set 12");
        aSelectionList.add("Set 13");
        aSelectionList.add("Set 14");
        aSelectionList.add("Set 15");
        aSelectionList.add("Set 16");
        aSelectionList.add("Set 17");
        aSelectionList.add("Set 18");
        aSelectionList.add("Set 19");
        aSelectionList.add("Set 20");

        // Populate Spinner
        ArrayAdapter<String> mFilterAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, aSelectionList);
        mFilterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mFilerSelector.setAdapter(mFilterAdapter);

        // Set Selected Position, based on current Filer Preference
        SharedPreferences aSharedPref = getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        String aFilerPref = aSharedPref.getString("filter_pref", "");
        if(aFilerPref.equals("All")){
            mFilerSelector.setSelection(0);
        }else if(aFilerPref.matches("[A-Z]")){
            for(int i=0;i< aSelectionList.size();i++){
                if(aSelectionList.get(i).equals("Alphabet " + aFilerPref)){
                    mFilerSelector.setSelection(i);
                    break;
                }
            }
        }else if(aFilerPref.matches("[0123456789]{1,2}")){
            if(aFilerPref.length()==1){
                aFilerPref = "0" + aFilerPref;
            }
            for(int i=0;i< aSelectionList.size();i++){
                if(aSelectionList.get(i).equals("Set " + aFilerPref)){
                    mFilerSelector.setSelection(i);
                    break;
                }
            }
        }
    }
}