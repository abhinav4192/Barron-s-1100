package fightingpit.barrons1100;

/**
 * Created by AG on 08-Oct-15.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
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

        TextView mNoWord = (TextView) rootView.findViewById(R.id.tv_wlf_no_word);
        CardView mCardView = (CardView) rootView.findViewById(R.id.cv_wlf_cardView);
        mCardView.setVisibility(View.GONE);

        SharedPreferences aSharedPref = getActivity().getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        String aFilerPref = aSharedPref.getString("filter_pref", "");
        String aFavPref = aSharedPref.getString("fav_pref", "");

        List<GenericContainer> mWordListNotOrdered = new ArrayList<>();
        DatabaseHelper aDBHelper = new DatabaseHelper(getActivity());
        if(aFilerPref.equalsIgnoreCase("all") || aFilerPref.equalsIgnoreCase("")){
            mWordListNotOrdered = aDBHelper.getWordList(aFavPref);
        } else if(aFilerPref.matches("[A-Z]")){
            mWordListNotOrdered = aDBHelper.getWordListByAlphabet(aFilerPref,aFavPref);
        }else if (aFilerPref.matches("[0123456789]{1,2}")){
            mWordListNotOrdered = aDBHelper.getWordListBySet(String.valueOf(Integer.parseInt(aFilerPref)), aFavPref);
        }
        aDBHelper.close();
        if(mWordList != null){
            mWordList.clear();
        }

        if(mMeaningList != null){
            mMeaningList.clear();
        }

        List<Integer> aOrderList= getIndex();
        for(Integer index:aOrderList){
            mWordList.add(mWordListNotOrdered.get(index));
        }
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

        if(mWordList.size()==0){
            mCardView.setVisibility(View.VISIBLE);
        }
       return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences aSharedPref = getActivity().getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        String aListViewPref = aSharedPref.getString("list_view_pref", "");
        if(aListViewPref.equalsIgnoreCase("expanded")){
            for(int i=0;i<mAdapterExpandableWordList.getGroupCount();i++){
                expandMeaning(i);
            }
        }else{
            for(int i=0;i<mAdapterExpandableWordList.getGroupCount();i++){
                hideMeaning(i);
            }
        }
    }

    public static void hideMeaning(int iPosition){
        mExpandableListView.collapseGroup(iPosition);
    }

    public static void expandMeaning(int iPosition){
        mExpandableListView.expandGroup(iPosition);
    }


    public static void updateFavourite(int iPosition, String iWord, boolean iIsFavourite){
        mWordList.get(iPosition).setFavourite(iIsFavourite);
        mAdapterExpandableWordList.notifyDataSetChanged();
        DatabaseHelper aDBHelper = new DatabaseHelper(context);
        aDBHelper.updateFavourite(iWord, iIsFavourite);
        aDBHelper.close();
        ((MainActivity) context).updateTabs("WordListFragment");
    }

    private List<Integer> getIndex()
    {
        SharedPreferences aSharedPref = getActivity().getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        String aIndexValue = aSharedPref.getString("IndexValue", "");
        String aSortPref = aSharedPref.getString("sort_pref", "");
        String aFavPref = aSharedPref.getString("fav_pref", "");
        String aFilterPref = aSharedPref.getString("filter_pref", "");
        Integer aWordCount = aSharedPref.getInt("word_list_count", 0);
        String aWasShuffeled = aSharedPref.getString("was_list_shuffled", "");

        DatabaseHelper aDBHelper = new DatabaseHelper(context);
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
        aDBHelper.close();
        if(aIndexList.size() >0 && aIndexList.size() == aWordCount && aWasShuffeled.equalsIgnoreCase(aSortPref)){
            aIndexList.clear();
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
        }else{
            SharedPreferences.Editor aEditor = aSharedPref.edit();
            if(aSortPref.equals("shuffle")){
                Collections.shuffle(aIndexList);
               aEditor.putString("was_list_shuffled","shuffle");
            }else{
               aEditor.putString("was_list_shuffled","alpha");
            }

            String aOrder = new String();
            for(int i=0;i<aIndexList.size();i++)
            {
                aOrder= aOrder + (aIndexList.get(i)).toString() + " ";
            }
            aEditor.putString("IndexValue", aOrder);
            aEditor.putInt("word_list_count",aIndexList.size());
            aEditor.apply();
        }
        return aIndexList;
    }
}
