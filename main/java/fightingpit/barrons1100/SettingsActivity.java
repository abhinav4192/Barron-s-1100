package fightingpit.barrons1100;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SettingsActivity extends Activity {

    private Button mSortAlphabetically;
    private Button mShuffle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mSortAlphabetically = (Button) findViewById(R.id.bt_as_sort_alphabetically);
        mSortAlphabetically.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences aSharedPref = getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
                SharedPreferences.Editor aEditor = aSharedPref.edit();
                aEditor.putString("sort_pref", "alpha");
                aEditor.commit();
                reCalculateOrderList();
                finish();
            }
        });

        mShuffle = (Button) findViewById(R.id.bt_as_shuffle);
        mShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences aSharedPref = getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
                SharedPreferences.Editor aEditor = aSharedPref.edit();
                aEditor.putString("sort_pref", "shuffle");
                aEditor.commit();
                reCalculateOrderList();
                finish();
            }
        });
    }

    public void reCalculateOrderList(){

        // Get Size from DB based on preferences.
        DatabaseHelper aDBHelper = new DatabaseHelper(this);
        List<GenericContainer> mWordListNotOrdered = aDBHelper.getWordListByAlphabet("b");
        List<Integer> aIndexList = new ArrayList<>();
        for(int i=0;i<mWordListNotOrdered.size();i++)
        {
            aIndexList.add(i);
        }

        SharedPreferences aSharedPref = getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        SharedPreferences.Editor aEditor = aSharedPref.edit();

        String aSortPref = aSharedPref.getString("sort_pref", "");
        if(aSortPref.equals("shuffle")){
            Log.d("ABG", "Shuffel");
            Collections.shuffle(aIndexList);
        }

        String aOrder = new String();
        for(int i=0;i<aIndexList.size();i++)
        {
            aOrder= aOrder + (aIndexList.get(i)).toString() + " ";
        }
        Log.d("ABG", "ORDER:" + aOrder);
        aEditor.putString("IndexValue", aOrder);
        aEditor.apply();
        finish();
    }
}
