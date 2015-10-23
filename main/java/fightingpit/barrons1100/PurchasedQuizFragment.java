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


public class PurchasedQuizFragment extends Fragment {

    private TextView mWordSelectionTextView;
    private ProgressBar mProgressBar;
    private TextView mWord;
    private ImageView mFavImage;
    private RadioGroup mRadioGroup;
    private RadioButton mOption1;
    private RadioButton mOption2;
    private RadioButton mOption3;
    private RadioButton mOption4;
    private Button mCheck;
    private Button mNextWord;
    private CardView mMainCardView;
    private CardView mNoWordCardView;
    private TextView mNoWordText;

    private final String mNoWords = "No word match the selected criteria. Change criteria.";
    private final String mAllMastered = "All words mastered in selected criteria. Reset progress or change criteria.";
    private List<GenericContainer> mWordList;
    private List<GenericContainer> mWordListFromDb;
    private final Integer mMaxProgress = 3;
    private String mSetSelectorText;
    private Integer mRandomIndex =0;
    private Random mRandomGenerator;
    private List<GenericContainer> mAllWordsListFromDb;



    // TODO: Rename and change types and number of parameters
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

        mWordSelectionTextView = (TextView) rootView.findViewById(R.id.tv_qpf_word_selection);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.pb_qpf_set_progress);
        mWord = (TextView) rootView.findViewById(R.id.tv_qpf_word);
        mFavImage = (ImageView) rootView.findViewById(R.id.iv_qpf_fav);
        mRadioGroup = (RadioGroup) rootView.findViewById(R.id.rg_qpf_radio_group);
        mOption1 = (RadioButton) rootView.findViewById(R.id.rb_qpf_option1);
        mOption2 = (RadioButton) rootView.findViewById(R.id.rb_qpf_option2);
        mOption3 = (RadioButton) rootView.findViewById(R.id.rb_qpf_option3);
        mOption4 = (RadioButton) rootView.findViewById(R.id.rb_qpf_option4);
        mCheck = (Button) rootView.findViewById(R.id.bt_qpf_check);
        mNextWord = (Button) rootView.findViewById(R.id.bt_qpf_next);
        mNextWord.setVisibility(View.GONE);
        mMainCardView = (CardView) rootView.findViewById(R.id.cv_qpf_cardView);
        mNoWordCardView = (CardView) rootView.findViewById(R.id.cv_qpf_noWordView);
        mNoWordCardView.setVisibility(View.GONE);
        mNoWordText = (TextView) rootView.findViewById(R.id.tv_qpf_no_word);
        mRandomGenerator = new Random();

        mCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCheckClicked();
            }
        });
        mNextWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNextWordClicked();
            }
        });
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
            mProgressBar.setMax(mWordListFromDb.size() * mMaxProgress);
            mProgressBar.setProgress((mWordListFromDb.size() * mMaxProgress) - mWordList.size());
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
            mNoWordText.setText(mNoWords);
            mNoWordCardView.setVisibility(View.VISIBLE);
        }else{
            mMainCardView.setVisibility(View.GONE);
            mCheck.setVisibility(View.GONE);
            mNextWord.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.GONE);
            mNoWordText.setText(mAllMastered);
            mNoWordCardView.setVisibility(View.VISIBLE);
        }
    }

    public void onCheckClicked(){
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
                aDBHelper.updateProgress(aWord, mMaxProgress);
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
                for(int i=0; i< mMaxProgress;i++){
                    mWordList.add(aTempWordInfo);
                }
                mNextWord.setVisibility(View.VISIBLE);
            }
            ((MainActivity) getActivity()).updateTabs("QuizFragment");
        }
        mProgressBar.setMax(mWordListFromDb.size() * mMaxProgress);
        mProgressBar.setProgress((mWordListFromDb.size() * mMaxProgress) - mWordList.size());

    }

    public void onNextWordClicked(){

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
}
