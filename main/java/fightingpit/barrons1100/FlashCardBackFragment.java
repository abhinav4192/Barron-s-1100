package fightingpit.barrons1100;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class FlashCardBackFragment extends Fragment {
    private TextView mWord;
    private TextView mMeaning;
    private ImageView mFav;


    public static FlashCardFrontFragment newInstance() {
        FlashCardFrontFragment fragment = new FlashCardFrontFragment();
        return fragment;
    }

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

        mWord = (TextView) rootView.findViewById(R.id.tv_fcb_word);
        mMeaning = (TextView) rootView.findViewById(R.id.tv_fcb_meaning);
        mFav = (ImageView) rootView.findViewById(R.id.iv_fcb_fav);

        Bundle bundle = getArguments();
        final String aWord = bundle.getString("Word");
        final String aMeaning = bundle.getString("Meaning");

        mWord.setText(aWord);
        mMeaning.setText(aMeaning);

        final DatabaseHelper aDBHelper = new DatabaseHelper(getActivity().getBaseContext());
        // Set appropriate Favourite Image
        if(aDBHelper.isFavourite(aWord)){
            mFav.setImageResource(R.drawable.ic_star_black_24dp);
        }else{
            mFav.setImageResource(R.drawable.ic_star_outline_black_24dp);
        }

        mFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(aDBHelper.isFavourite(aWord)){
                    mFav.setImageResource(R.drawable.ic_star_outline_black_24dp);
                    aDBHelper.updateFavourite(aWord,false);

                }else{
                    mFav.setImageResource(R.drawable.ic_star_black_24dp);
                    aDBHelper.updateFavourite(aWord,true);
                }
                ((MainActivity) getActivity()).updateTabs("FlashCardsFragment");
            }
        });
        aDBHelper.close();
        return rootView;
    }
}

