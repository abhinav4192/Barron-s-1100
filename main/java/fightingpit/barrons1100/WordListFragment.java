package fightingpit.barrons1100;

/**
 * Created by AG on 08-Oct-15.
 */

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;

public class WordListFragment extends Fragment {

    private static ExpandableListView mExpandableListView;
    private static AdapterExpandableWordList mAdapterExpandableWordList;
    private static List<GenericContainer> mWordList;
    private static Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        context = container.getContext();
        View rootView = inflater.inflate(R.layout.word_list_fragment, container, false);


        // Getting all Words from DB.
        DatabaseHelper aDBHelper = new DatabaseHelper(getActivity());
        mWordList = aDBHelper.getWordList();

        // Creating the Meaning List
        ArrayList<String> childItem = new ArrayList<>();
        for(GenericContainer aWordInfo:mWordList){
            childItem.add(aWordInfo.getMeaning());
        }


        // Putting data in List View.
        mExpandableListView = (ExpandableListView) rootView.findViewById(android.R.id.list);
        mAdapterExpandableWordList = new AdapterExpandableWordList(context,mWordList, childItem);
        mAdapterExpandableWordList
                .setInflater(
                        (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE),
                        getActivity());
        mExpandableListView.setAdapter(mAdapterExpandableWordList);


        return rootView;
    }



//    @Override
//    public boolean onChildClick(ExpandableListView parent, View v,
//                                int wordPosition, int meaningPosition, long id) {
//        Toast.makeText(MainActivity.this, "Clicked On Child",
//                Toast.LENGTH_SHORT).show();
//        return true;
//    }

    public static void hideMeaning(int iPosition){
        mExpandableListView.collapseGroup(iPosition);
    }

    public static void updateFavourite(int iPosition, String iWord, boolean iIsFavourite){
        mWordList.get(iPosition).setFavourite(iIsFavourite);
        mAdapterExpandableWordList.notifyDataSetChanged();
        DatabaseHelper aDBHelper = new DatabaseHelper(context);
        aDBHelper.updateFavourite(iWord,iIsFavourite);
    }
}
