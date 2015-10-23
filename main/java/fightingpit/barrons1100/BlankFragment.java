package fightingpit.barrons1100;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class BlankFragment extends Fragment {

    // TODO: Rename and change types and number of parameters
    public static BlankFragment newInstance() {
        BlankFragment fragment = new BlankFragment();
        return fragment;
    }

    public BlankFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.no_word_fragment, container, false);
        TextView aTv = (TextView) rootView.findViewById(R.id.tv_nwf_word);
        Bundle bundle = getArguments();
        String aWord = bundle.getString("Text");
        aTv.setText(aWord);
        return rootView;
    }


}
