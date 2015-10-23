package fightingpit.barrons1100;

/**
 * Created by AG on 08-Oct-15.
 */

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class QuizFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.quiz_main_fragment, container, false);
        getFragmentManager()
                .beginTransaction()
                .add(R.id.fl_qmf_fragment_holder, new PurchasedQuizFragment())
                .commit();
        return rootView;
    }
}

