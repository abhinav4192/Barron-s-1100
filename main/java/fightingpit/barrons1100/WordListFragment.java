package fightingpit.barrons1100;

/**
 * Created by AG on 08-Oct-15.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;


import java.util.ArrayList;
import java.util.List;

public class WordListFragment extends Fragment {

    private static ExpandableListView mExpandableListView;
    private static AdapterExpandableWordList mAdapterExpandableWordList;
    private static List<GenericContainer> mWordList = new ArrayList<>();
    ArrayList<String> mMeaningList = new ArrayList<>();
    private static Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        context = container.getContext();
        View rootView = inflater.inflate(R.layout.word_list_fragment, container, false);
        Log.d("ABG", "OnCreateView");


        DatabaseHelper aDBHelper = new DatabaseHelper(getActivity());
        List<GenericContainer> mWordListNotOrdered = aDBHelper.getWordListByAlphabet("b");
        if(mWordList != null){
            mWordList.clear();
        }

        List<Integer> aOrderList= getIndex();
        for(Integer index:aOrderList){
            mWordList.add(mWordListNotOrdered.get(index));
        }
        Log.d("ABG", "ListSize2:" + String.valueOf(mWordList.size()));
        // Creating the Meaning List
        for(GenericContainer aWordInfo:mWordList){
            mMeaningList.add(aWordInfo.getMeaning());
        }


        // Putting data in List View.
        mExpandableListView = (ExpandableListView) rootView.findViewById(android.R.id.list);
        mAdapterExpandableWordList = new AdapterExpandableWordList(context,mWordList, mMeaningList);
        mAdapterExpandableWordList
                .setInflater(
                        (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE),
                        getActivity());
        mExpandableListView.setAdapter(mAdapterExpandableWordList);


        return rootView;
    }

    public static void hideMeaning(int iPosition){
        mExpandableListView.collapseGroup(iPosition);
    }

    public static void updateFavourite(int iPosition, String iWord, boolean iIsFavourite){
        mWordList.get(iPosition).setFavourite(iIsFavourite);
        mAdapterExpandableWordList.notifyDataSetChanged();
        DatabaseHelper aDBHelper = new DatabaseHelper(context);
        aDBHelper.updateFavourite(iWord, iIsFavourite);
    }

    private List<Integer> getIndex()
    {
        SharedPreferences aSharedPref = getActivity().getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        String aIndexValue = aSharedPref.getString("IndexValue", "");

        List<Integer> aIndexList = new ArrayList<>();
        if(aIndexValue != "")
        {
            String[] aIndexStringArray;
            aIndexStringArray = aIndexValue.split(" ");
            int i=0;
            while(i < aIndexStringArray.length ) {
                aIndexList.add(Integer.parseInt(aIndexStringArray[i]));
                i++;
            }
        }
        return aIndexList;

    }
}
