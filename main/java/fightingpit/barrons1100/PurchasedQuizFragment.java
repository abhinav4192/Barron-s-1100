package fightingpit.barrons1100;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class PurchasedQuizFragment extends Fragment {

    @Bind(R.id.tv_qpf_word_selection) TextView mWordSelectionTextView;
    @Bind(R.id.pb_qpf_set_progress) ProgressBar mProgressBar;
    @Bind(R.id.tv_qpf_word) TextView mWord;
    @Bind(R.id.iv_qpf_fav) ImageView mFavImage;
    @Bind(R.id.rg_qpf_radio_group) RadioGroup mRadioGroup;
    @Bind(R.id.rb_qpf_option1) RadioButton mOption1;
    @Bind(R.id.rb_qpf_option2) RadioButton mOption2;
    @Bind(R.id.rb_qpf_option3) RadioButton mOption3;
    @Bind(R.id.rb_qpf_option4) RadioButton mOption4;
    @Bind(R.id.bt_qpf_check) Button mCheck;
    @Bind(R.id.bt_qpf_next) Button mNextWord;
    @Bind(R.id.cv_qpf_cardView) CardView mMainCardView;
    @Bind(R.id.cv_qpf_noWordView) CardView mNoWordCardView;
    @Bind(R.id.tv_qpf_no_word) TextView mNoWordText;

    private List<GenericContainer> mWordList;
    private List<GenericContainer> mWordListFromDb;
    private String mSetSelectorText;
    private Integer mRandomIndex =0;
    private Random mRandomGenerator;
    private List<GenericContainer> mAllWordsListFromDb;

    public static PurchasedQuizFragment newInstance() {
        PurchasedQuizFragment fragment = new PurchasedQuizFragment();
        return fragment;
    }

    public PurchasedQuizFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.quiz_paid_fragment, container, false);
        ButterKnife.bind(this, rootView);

        mNextWord.setVisibility(View.GONE);
        mNoWordCardView.setVisibility(View.GONE);
        mRandomGenerator = new Random();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Getting Desired WordList
        SharedPreferences aSharedPref = getActivity().getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        String aFilerPref = aSharedPref.getString("filter_pref", "");
        String aFavPref = aSharedPref.getString("fav_pref", "");

        DatabaseHelper aDBHelper = new DatabaseHelper(getActivity());
        mWordListFromDb = new ArrayList<>();
        mAllWordsListFromDb = aDBHelper.getWordList("a");
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
        populateRadioGroup();

    }

    public void populateRadioGroup(){
        if(mWordList.size()>0){
            // Populate Word and Meanings in Radio Group
            mProgressBar.setMax(mWordListFromDb.size() * getResources().getInteger(R.integer.max_progress_val));
            mProgressBar.setProgress((mWordListFromDb.size() * getResources().getInteger(R.integer.max_progress_val)) - mWordList.size());
            mRandomIndex = mRandomGenerator.nextInt(mWordList.size());
            final String aWord = mWordList.get(mRandomIndex).getWord();
            mWord.setText(aWord);

            // Generating Options
            ArrayList<String> aOptionsList = new ArrayList<>();
            aOptionsList.add(mWordList.get(mRandomIndex).getMeaning());
            Integer aAddOptionCounter=0;
            while(aAddOptionCounter<3){
                boolean toAddMeaning = true;
                String aMeaningItem = mAllWordsListFromDb
                        .get(mRandomGenerator.nextInt(mAllWordsListFromDb.size()))
                        .getMeaning();
                for(int i=0; i< aOptionsList.size();i++){
                    if(aMeaningItem.equals(aOptionsList.get(i))){
                        toAddMeaning=false;
                        break;
                    }
                }
                if(toAddMeaning){
                    aOptionsList.add(aMeaningItem);
                    aAddOptionCounter++;
                }
            }
            // Populating Options
            Collections.shuffle(aOptionsList);
            mOption1.setTextColor(getResources().getColor(R.color.colorBlack));
            mOption2.setTextColor(getResources().getColor(R.color.colorBlack));
            mOption3.setTextColor(getResources().getColor(R.color.colorBlack));
            mOption4.setTextColor(getResources().getColor(R.color.colorBlack));
            mOption1.setTypeface(null, Typeface.NORMAL);
            mOption2.setTypeface(null, Typeface.NORMAL);
            mOption3.setTypeface(null, Typeface.NORMAL);
            mOption4.setTypeface(null, Typeface.NORMAL);
            mRadioGroup.clearCheck();
            mOption1.setText(aOptionsList.get(0));
            mOption2.setText(aOptionsList.get(1));
            mOption3.setText(aOptionsList.get(2));
            mOption4.setText(aOptionsList.get(3));

            final DatabaseHelper aDBHelper = new DatabaseHelper(getActivity().getBaseContext());
            // Set appropriate Favourite Image
            if(aDBHelper.isFavourite(aWord)){
                mFavImage.setImageResource(R.drawable.ic_star_black_24dp);
            }else{
                mFavImage.setImageResource(R.drawable.ic_star_outline_black_24dp);
            }
            mFavImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (aDBHelper.isFavourite(aWord)) {
                        mFavImage.setImageResource(R.drawable.ic_star_outline_black_24dp);
                        aDBHelper.updateFavourite(aWord, false);

                    } else {
                        mFavImage.setImageResource(R.drawable.ic_star_black_24dp);
                        aDBHelper.updateFavourite(aWord, true);
                    }
                    ((MainActivity) getActivity()).updateTabs("QuizFragment");
                }
            });
            aDBHelper.close();
            mCheck.setVisibility(View.VISIBLE);
        } else if(mWordListFromDb.size()==0){
            mMainCardView.setVisibility(View.GONE);
            mCheck.setVisibility(View.GONE);
            mNextWord.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.GONE);
            mNoWordText.setText(R.string.no_words);
            mNoWordCardView.setVisibility(View.VISIBLE);
        }else{
            mMainCardView.setVisibility(View.GONE);
            mCheck.setVisibility(View.GONE);
            mNextWord.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.GONE);
            mNoWordText.setText(R.string.all_words_mastered);
            mNoWordCardView.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.bt_qpf_check) void onCheckClicked() {
        int id = mRadioGroup.getCheckedRadioButtonId();
        if (id == -1){
            Toast.makeText(getActivity().getBaseContext(),"No option selected",Toast.LENGTH_LONG).show();
        }
        else{
            mCheck.setVisibility(View.GONE);
            boolean isCorrect = false;
            String aMeaning = mWordList.get(mRandomIndex).getMeaning();
            final String aWord = mWordList.get(mRandomIndex).getWord();
            if (id == R.id.rb_qpf_option1){
                if(mOption1.getText().equals(aMeaning)){
                    mOption1.setTypeface(null, Typeface.BOLD);
                    mOption1.setTextColor(getResources().getColor(R.color.colorGreenDark));
                    isCorrect =true;
                }else{
                    mOption1.setTextColor(getResources().getColor(R.color.colorRedDark));
                }
            }else if (id == R.id.rb_qpf_option2){
                if(mOption2.getText().equals(aMeaning)){
                    mOption2.setTypeface(null, Typeface.BOLD);
                    mOption2.setTextColor(getResources().getColor(R.color.colorGreenDark));
                    isCorrect =true;
                }else{
                    mOption2.setTextColor(getResources().getColor(R.color.colorRedDark));
                }
            }else if (id == R.id.rb_qpf_option3){
                if(mOption3.getText().equals(aMeaning)){
                    mOption3.setTypeface(null, Typeface.BOLD);
                    mOption3.setTextColor(getResources().getColor(R.color.colorGreenDark));
                    isCorrect =true;
                }else{
                    mOption3.setTextColor(getResources().getColor(R.color.colorRedDark));
                }
            }else if(id == R.id.rb_qpf_option4){
                if(mOption4.getText().equals(aMeaning)){
                    mOption4.setTypeface(null, Typeface.BOLD);
                    mOption4.setTextColor(getResources().getColor(R.color.colorGreenDark));
                    isCorrect =true;
                }else{
                    mOption4.setTextColor(getResources().getColor(R.color.colorRedDark));
                }
            }
            if(isCorrect){
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

                if(aWordNewProgress==0){
                    // It means Word has been mastered

                    SharedPreferences aSharedPref = getActivity().getBaseContext().getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
                    SharedPreferences.Editor aEditor = aSharedPref.edit();

                    // Update Progress for rating app
                    Integer aNumberOfWordsMastered = aSharedPref.getInt("number_words_mastered", 0);
                    aNumberOfWordsMastered += 1;
                    aEditor.putInt("number_words_mastered", aNumberOfWordsMastered);
                    aEditor.commit();

                    final Animation aOut = AnimationUtils.loadAnimation(getActivity().getBaseContext(), android.R.anim.fade_out);
                    final Animation aIn = AnimationUtils.loadAnimation(getActivity().getBaseContext(),android.R.anim.fade_in);
                    aIn.setDuration(700);

                    mWordSelectionTextView.startAnimation(aOut);
                    aOut.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            mWordSelectionTextView.setBackgroundResource(R.drawable.cust_rounded_orange);
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
                            mWordSelectionTextView.setBackgroundResource(R.drawable.cust_rounder_grey);
                            mWordSelectionTextView.setText(mSetSelectorText);
                            mNextWord.setVisibility(View.VISIBLE);
                        }
                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                }else{
                    mNextWord.setVisibility(View.VISIBLE);
                }
            }else{
                if(mOption1.getText().equals(aMeaning)){
                    mOption1.setTextColor(getResources().getColor(R.color.colorGreenDark));
                    mOption1.setTypeface(null, Typeface.BOLD);
                }else if(mOption2.getText().equals(aMeaning)) {
                    mOption2.setTextColor(getResources().getColor(R.color.colorGreenDark));
                    mOption2.setTypeface(null, Typeface.BOLD);
                }else if(mOption3.getText().equals(aMeaning)) {
                    mOption3.setTextColor(getResources().getColor(R.color.colorGreenDark));
                    mOption3.setTypeface(null, Typeface.BOLD);
                }else if(mOption4.getText().equals(aMeaning)) {
                    mOption4.setTextColor(getResources().getColor(R.color.colorGreenDark));
                    mOption4.setTypeface(null, Typeface.BOLD);
                }
                // Update Progress in DB
                DatabaseHelper aDBHelper = new DatabaseHelper(getActivity().getBaseContext());
                aDBHelper.updateProgress(aWord, getResources().getInteger(R.integer.max_progress_val));
                aDBHelper.close();

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
                for(int i=0; i< getResources().getInteger(R.integer.max_progress_val);i++){
                    mWordList.add(aTempWordInfo);
                }
                mNextWord.setVisibility(View.VISIBLE);
            }
            ((MainActivity) getActivity()).updateTabs("QuizFragment");
        }
        mProgressBar.setMax(mWordListFromDb.size() * getResources().getInteger(R.integer.max_progress_val));
        mProgressBar.setProgress((mWordListFromDb.size() * getResources().getInteger(R.integer.max_progress_val)) - mWordList.size());

    }

    @OnClick(R.id.bt_qpf_next) void onNextWordClicked() {

        final Animation aOut = AnimationUtils.loadAnimation(getActivity().getBaseContext(), android.R.anim.fade_out);
        final Animation aIn = AnimationUtils.loadAnimation(getActivity().getBaseContext(),android.R.anim.fade_in);
        aOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }
            @Override
            public void onAnimationEnd(Animation animation) {
                populateRadioGroup();
                mMainCardView.startAnimation(aIn);
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
                mNextWord.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mMainCardView.startAnimation(aOut);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
