package fightingpit.barrons1100;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class FlashCardBackFragment extends Fragment {
    @Bind(R.id.tv_fcb_word) TextView mWord;
    @Bind(R.id.tv_fcb_meaning) TextView mMeaning;
    @Bind(R.id.iv_fcb_fav) ImageView mFav;
    @Bind(R.id.tv_fcb_sen) TextView mSentence;


    public FlashCardBackFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.flash_card_back, container, false);
        ButterKnife.bind(this, rootView);

        Bundle bundle = getArguments();
        final String aWord = bundle.getString("Word");
        final String aMeaning = bundle.getString("Meaning");
        final String aSentence = bundle.getString("Sentence");

        mWord.setText(aWord);
        mMeaning.setText(aMeaning);
        mSentence.setText(aSentence);

        final DatabaseHelper aDBHelper = new DatabaseHelper(getActivity().getBaseContext());
        // Set appropriate Favourite Image
        if(aDBHelper.isFavourite(aWord)){
            mFav.setImageResource(R.drawable.ic_star_black_24dp);
        }else{
            mFav.setImageResource(R.drawable.ic_star_outline_black_24dp);
        }
        aDBHelper.close();
        return rootView;
    }

    @OnClick(R.id.iv_fcb_fav) void onFavouriteClicked(){
        final DatabaseHelper aDBHelper = new DatabaseHelper(getActivity().getBaseContext());
        if(aDBHelper.isFavourite(mWord.getText().toString())){
            mFav.setImageResource(R.drawable.ic_star_outline_black_24dp);
            aDBHelper.updateFavourite(mWord.getText().toString(),false);

        }else{
            mFav.setImageResource(R.drawable.ic_star_black_24dp);
            aDBHelper.updateFavourite(mWord.getText().toString(),true);
        }
        ((MainActivity) getActivity()).updateTabs("FlashCardsFragment");
        aDBHelper.close();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}

