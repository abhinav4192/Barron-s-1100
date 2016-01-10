package fightingpit.barrons1100;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FlashCardFrontFragment extends Fragment {

    public FlashCardFrontFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.flash_card_front, container, false);

        TextView aTv = (TextView) rootView.findViewById(R.id.tv_front_word);
        Bundle bundle = getArguments();
        String aWord = bundle.getString("Word");
        aTv.setText(aWord);
        return rootView;
    }

}
