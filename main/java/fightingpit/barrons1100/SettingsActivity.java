package fightingpit.barrons1100;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SettingsActivity extends Activity implements AdapterView.OnItemSelectedListener {

    private ImageView mSortAlphabetically;
    private ImageView mShuffle;
    private Spinner aFilerSelector;
    private boolean isFirstTime = true;

    private Button mAllButton;
    private Button mFavButton;
    private Button mUnmarkedButton;
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

        // Set Default Image
        if(aSortPref.equalsIgnoreCase("alpha")){
            mSortAlphabetically.setImageResource(R.drawable.ic_alpha_icon_green);

        } else if(aSortPref.equalsIgnoreCase("shuffle")){
            TextView aShuffleTextView = (TextView) findViewById(R.id.tv_as_shuffle);
            aShuffleTextView.setText("Shuffle Again");
            mShuffle.setImageResource(R.drawable.ic_shuffle_icon_green);
        }

        mSortAlphabetically.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aSortPref = "alpha";
                mSortAlphabetically.setImageResource(R.drawable.ic_alpha_icon_green);
                mShuffle.setImageResource(R.drawable.ic_shuffle_icon_grey);

            }
        });


        mShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aSortPref = "shuffle";
                mSortAlphabetically.setImageResource(R.drawable.ic_alpha_icon_grey);
                mShuffle.setImageResource(R.drawable.ic_shuffle_icon_green);
            }
        });

        aFilerSelector = (Spinner) findViewById(R.id.sp_as_filter_selector);
        populateFilterSelector();
        aFilerSelector.setOnItemSelectedListener(this);

        mAllButton = (Button) findViewById(R.id.bt_as_all);
        mAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aFavPref = "a";
            }
        });


        mFavButton = (Button) findViewById(R.id.bt_as_fav);
        mFavButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aFavPref = "m";
            }
        });

        mUnmarkedButton = (Button) findViewById(R.id.bt_as_unmarked);
        mUnmarkedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aFavPref = "u";
            }
        });

        mDone = (Button) findViewById(R.id.bt_as_done);
        mDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reCalculateOrderList();
            }
        });
    }

    public void reCalculateOrderList(){

        DatabaseHelper aDBHelper = new DatabaseHelper(this);
        List<Integer> aIndexList = new ArrayList<>();
        if(aFilterPref.equalsIgnoreCase("All")){
            Integer maxIndex= aDBHelper.getWordListCount(aFavPref);
            for(int i=0;i< maxIndex ;i++)
            {
                aIndexList.add(i);
            }

        }
        else if(aFilterPref.matches("[A-Z]")){
            Integer maxIndex= aDBHelper.getCountByAlphabet(aFilterPref, aFavPref);
            for(int i=0;i<maxIndex;i++)
            {
                aIndexList.add(i);
            }
        }else if (aFilterPref.matches("[0123456789]{1,2}")){
            Integer maxIndex= aDBHelper.getCountBySetNumber(aFilterPref, aFavPref);
            for(int i=0;i<maxIndex;i++)
            {
                aIndexList.add(i);
            }
        }


        if(aIndexList.size() > 0 ){
            SharedPreferences aSharedPref = getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
            SharedPreferences.Editor aEditor = aSharedPref.edit();
            if(aSortPref.equals("shuffle")){
                Collections.shuffle(aIndexList);
            }

            String aOrder = new String();
            for(int i=0;i<aIndexList.size();i++)
            {
                aOrder= aOrder + (aIndexList.get(i)).toString() + " ";
            }
            aEditor.putString("IndexValue", aOrder);
            aEditor.putString("sort_pref",aSortPref);
            aEditor.putString("fav_pref",aFavPref);
            aEditor.putString("filter_pref",aFilterPref);
            aEditor.apply();
            finish();
        }else
        {
            Toast.makeText(this,"No word match the provided criteria", Toast.LENGTH_LONG).show();
        }
    }

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
        aFilerSelector.setAdapter(mFilterAdapter);

        // Set Selected Position, based on current Filer Preference
        SharedPreferences aSharedPref = getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        String aFilerPref = aSharedPref.getString("filter_pref", "");
        if(aFilerPref.equals("All")){
            aFilerSelector.setSelection(0);
        }else if(aFilerPref.matches("[A-Z]")){
            for(int i=0;i< aSelectionList.size();i++){
                if(aSelectionList.get(i).equals("Alphabet " + aFilerPref)){
                    aFilerSelector.setSelection(i);
                    break;
                }
            }
        }else if(aFilerPref.matches("[0123456789]{1,2}")){
            if(aFilerPref.length()==1){
                aFilerPref = "0" + aFilerPref;
            }
            for(int i=0;i< aSelectionList.size();i++){
                if(aSelectionList.get(i).equals("Set " + aFilerPref)){
                    aFilerSelector.setSelection(i);
                    break;
                }
            }
        }
    }
}
