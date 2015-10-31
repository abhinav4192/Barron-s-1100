package fightingpit.barrons1100;

/**
 * Created by AG on 08-Oct-15.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class FlashCardsFragment extends Fragment{

    private Button mCorrect;
    private Button mWrong;
    private Button mShowMeaning;
    private TextView mWordSelectionTextView;
    private List<GenericContainer> mWordList;
    private Random mRandomGenerator;
    private Integer mRandomIndex =0;
    private ProgressBar mProgressBar;
    private List<GenericContainer> mWordListFromDb;
    private final Integer mMaxProgress = 3;
    private String mSetSelectorText;
    private final String mNoWords = "No word match the selected criteria. Change criteria.";
    private final String mAllMastered = "All words mastered in selected criteria. Reset progress or change criteria.";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.flash_cards_fragment, container, false);

        mCorrect = (Button) rootView.findViewById(R.id.bt_fcf_correct);
        mCorrect.setVisibility(View.GONE);
        mCorrect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnCorrectClicked();
            }
        });


        mWrong = (Button) rootView.findViewById(R.id.bt_fcf_wrong);
        mWrong.setVisibility(View.GONE);
        mWrong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnWrongClicked();
            }
        });

        mShowMeaning = (Button) rootView.findViewById(R.id.bt_fcf_show_meaning);
        mShowMeaning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onShowMeaningClicked();

            }
        });

        mWordSelectionTextView = (TextView) rootView.findViewById(R.id.tv_fcf_word_selection);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.pb_fcf_set_progress);
        mSetSelectorText = new String();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Getting Desired WordList
        SharedPreferences aSharedPref = getActivity().getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        String aFilerPref = aSharedPref.getString("filter_pref", "");
        String aFavPref = aSharedPref.getString("fav_pref", "");

        mWordListFromDb = new ArrayList<>();
        DatabaseHelper aDBHelper = new DatabaseHelper(getActivity());
        if(aFilerPref.equalsIgnoreCase("all") || aFilerPref.equalsIgnoreCase("")){
            mWordListFromDb = aDBHelper.getWordList(aFavPref);
            mSetSelectorText = "Learning: All Words";
        } else if(aFilerPref.matches("[A-Z]")){
            mWordListFromDb = aDBHelper.getWordListByAlphabet(aFilerPref,aFavPref);
            mSetSelectorText = "Learning: Alphabet " + aFilerPref;
        }else if (aFilerPref.matches("[0123456789]{1,2}")){
            mWordListFromDb = aDBHelper.getWordListBySet(String.valueOf(Integer.parseInt(aFilerPref)), aFavPref);
            mSetSelectorText = "Learning: Set " + aFilerPref;
        }
        aDBHelper.close();
        mWordSelectionTextView.setText(mSetSelectorText);

        mWordList = new ArrayList<>();
        if(mWordListFromDb.size()>0){
            for(GenericContainer aWordDetails: mWordListFromDb){
                for(int i=0;i<aWordDetails.getProgress();i++){
                    mWordList.add(aWordDetails);
                }
            }
        }
        mRandomGenerator = new Random();
        if(mWordList.size()>0){
            mRandomIndex = mRandomGenerator.nextInt(mWordList.size());
            Fragment aFrontFragment = new FlashCardFrontFragment();
            Bundle bundle = new Bundle();
            bundle.putString("Word", mWordList.get(mRandomIndex).getWord());
            aFrontFragment.setArguments(bundle);
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.fl_fcf_cardView_holder, aFrontFragment)
                    .commit();

            mProgressBar.setMax(mWordListFromDb.size() * mMaxProgress);
            mProgressBar.setProgress((mWordListFromDb.size() * mMaxProgress) - mWordList.size());
        } else if(mWordListFromDb.size()==0){
            mShowMeaning.setVisibility(View.GONE);
            Fragment aBlankFragment = new BlankFragment();
            Bundle bundle = new Bundle();
            bundle.putString("Text", mNoWords);
            aBlankFragment.setArguments(bundle);
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.fl_fcf_cardView_holder,aBlankFragment)
                    .commit();
            mProgressBar.setVisibility(View.GONE);
        }else{
            mShowMeaning.setVisibility(View.GONE);
            Fragment aBlankFragment = new BlankFragment();
            Bundle bundle = new Bundle();
            bundle.putString("Text", mAllMastered);
            aBlankFragment.setArguments(bundle);
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.fl_fcf_cardView_holder,aBlankFragment)
                    .commit();
            mProgressBar.setVisibility(View.GONE);
        }
    }

    public void onShowMeaningClicked(){
        mShowMeaning.setVisibility(View.GONE);
        mCorrect.setVisibility(View.VISIBLE);
        mWrong.setVisibility(View.VISIBLE);

        Fragment aBackFragment = new FlashCardBackFragment();
        Bundle bundle = new Bundle();
        bundle.putString("Word", mWordList.get(mRandomIndex).getWord());
        bundle.putString("Meaning", mWordList.get(mRandomIndex).getMeaning());
        aBackFragment.setArguments(bundle);

        getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.animator.card_flip_right_in, R.animator.card_flip_right_out,
                        R.animator.card_flip_left_in, R.animator.card_flip_left_out)
                .replace(R.id.fl_fcf_cardView_holder, aBackFragment)
                .addToBackStack(null)
                .commit();
    }

    public void OnCorrectClicked(){
        mCorrect.setVisibility(View.GONE);
        mWrong.setVisibility(View.GONE);
        final String aWord = mWordList.get(mRandomIndex).getWord();

        // Remove Word From Current List
        for (Iterator<GenericContainer> iter = mWordList.listIterator(); iter.hasNext(); ) {
            GenericContainer aWordCurrent = iter.next();
            if (aWordCurrent.getWord().equalsIgnoreCase(aWord)) {
                iter.remove();
                break;
            }
        }

        // Update Progress in DB
        DatabaseHelper aDBHelper = new DatabaseHelper(getActivity().getBaseContext());
        Integer aWordNewProgress = aDBHelper.getProgress(aWord) - 1;
        aDBHelper.updateProgress(aWord, aWordNewProgress);
        aDBHelper.close();
        ((MainActivity) getActivity()).updateTabs("FlashCardsFragment");

        if(aWordNewProgress!=0){

            flipCardToFront(true);


        }else{
            // It means Word has been mastered
            final Animation aOut = AnimationUtils.loadAnimation(getActivity().getBaseContext(),android.R.anim.fade_out);
            final Animation aIn = AnimationUtils.loadAnimation(getActivity().getBaseContext(),android.R.anim.fade_in);
            aIn.setDuration(700);

            mWordSelectionTextView.startAnimation(aOut);
            aOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }
                @Override
                public void onAnimationEnd(Animation animation) {
                    mWordSelectionTextView.setBackgroundResource(R.color.colorOrangeDark);
                    mWordSelectionTextView.setText("Mastered Word: " + aWord);
                    mWordSelectionTextView.startAnimation(aIn);
                }
                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            aIn.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mWordSelectionTextView.setBackgroundResource(R.color.colorGreyDark);
                    mWordSelectionTextView.setText(mSetSelectorText);
                    flipCardToFront(true);

                }
                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });


        }
    }

    public void OnWrongClicked(){
        mCorrect.setVisibility(View.GONE);
        mWrong.setVisibility(View.GONE);
        final String aWord = mWordList.get(mRandomIndex).getWord();

        // Update Progress in DB
        DatabaseHelper aDBHelper = new DatabaseHelper(getActivity().getBaseContext());
        aDBHelper.updateProgress(aWord, mMaxProgress);
        aDBHelper.close();
        ((MainActivity) getActivity()).updateTabs("FlashCardsFragment");

        GenericContainer aTempWordInfo = new GenericContainer();
        // Remove Word From Current List
        for (Iterator<GenericContainer> iter = mWordList.listIterator(); iter.hasNext(); ) {
            GenericContainer aWordCurrent = iter.next();
            if (aWordCurrent.getWord().equalsIgnoreCase(aWord)) {
                aTempWordInfo =aWordCurrent;
                iter.remove();
            }
        }

        // Add word maximum times in current list
        for(int i=0; i< mMaxProgress;i++){
            mWordList.add(aTempWordInfo);
        }

        // Show next word.
        flipCardToFront(false);
    }

    public void flipCardToFront(boolean iFlipCardDirection){

        if(mWordList.size()>0){
            mRandomIndex = mRandomGenerator.nextInt(mWordList.size());
            mWordSelectionTextView.setText(mSetSelectorText);
            mShowMeaning.setVisibility(View.VISIBLE);

            // Set Progress for Criteria
            mProgressBar.setMax(mWordListFromDb.size() * mMaxProgress);
            mProgressBar.setProgress((mWordListFromDb.size() * mMaxProgress) - mWordList.size());

            Fragment aFrontFragment = new FlashCardFrontFragment();
            Bundle bundle = new Bundle();
            bundle.putString("Word", mWordList.get(mRandomIndex).getWord());
            aFrontFragment.setArguments(bundle);

            if(iFlipCardDirection){
                // Correct. Flip right to left.
                getFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(
                                R.animator.card_flip_right_in, R.animator.card_flip_right_out,
                                R.animator.card_flip_left_in, R.animator.card_flip_left_out)
                        .replace(R.id.fl_fcf_cardView_holder, aFrontFragment)
                        .addToBackStack(null)
                        .commit();

            }else{
                // Wrong. Flip left to right.
                getFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(
                                R.animator.card_flip_left_in, R.animator.card_flip_left_out,
                                R.animator.card_flip_right_in, R.animator.card_flip_right_out
                                )
                        .replace(R.id.fl_fcf_cardView_holder, aFrontFragment)
                        .addToBackStack(null)
                        .commit();
            }

        }else{
            mShowMeaning.setVisibility(View.GONE);
            Fragment aBlankFragment = new BlankFragment();
            Bundle bundle = new Bundle();
            bundle.putString("Text", mAllMastered);
            aBlankFragment.setArguments(bundle);
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fl_fcf_cardView_holder,aBlankFragment)
                    .commit();
            mProgressBar.setMax(1);
            mProgressBar.setProgress(1);
        }
    }
}