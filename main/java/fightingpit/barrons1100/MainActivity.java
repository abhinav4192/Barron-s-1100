package fightingpit.barrons1100;

import java.util.ArrayList;
import java.util.List;

import android.app.ExpandableListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class MainActivity extends ExpandableListActivity implements
        OnChildClickListener {

    private ExpandableListView mExpandableListView;
    AdapterExpandableWordList mAdapterExpandableWordList;
    List<GenericContainer> mWordList;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Getting all Words from DB.
        DatabaseHelper aDBHelper = new DatabaseHelper(this);
        mWordList = aDBHelper.getWordList();

        // Creating the Meaning List
        ArrayList<String> childItem = new ArrayList<>();
        for(GenericContainer aWordInfo:mWordList){
            childItem.add(aWordInfo.getMeaning());
        }


        // Putting data in List View.
        mExpandableListView = (ExpandableListView) findViewById(android.R.id.list);
        mAdapterExpandableWordList = new AdapterExpandableWordList(this,mWordList, childItem);
        mAdapterExpandableWordList
                .setInflater(
                        (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE),
                        this);
        mExpandableListView.setAdapter(mAdapterExpandableWordList);


        // Loading the Advertisement.
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public boolean onChildClick(ExpandableListView parent, View v,
//                                int wordPosition, int meaningPosition, long id) {
//        Toast.makeText(MainActivity.this, "Clicked On Child",
//                Toast.LENGTH_SHORT).show();
//        return true;
//    }

    public void hideMeaning(int iPosition){
        mExpandableListView.collapseGroup(iPosition);
    }

    public void updateFavourite(int iPosition, String iWord, boolean iIsFavourite){
        mWordList.get(iPosition).setFavourite(iIsFavourite);
        mAdapterExpandableWordList.notifyDataSetChanged();
        DatabaseHelper aDBHelper = new DatabaseHelper(this);
        aDBHelper.updateFavourite(iWord,iIsFavourite);
    }



}