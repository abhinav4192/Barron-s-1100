package fightingpit.barrons1100;

/**
 * Created by AG on 08-Oct-15.
 */

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class QuizFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.quiz_main_fragment, container, false);
        ButterKnife.bind(this, rootView);

        SharedPreferences aSharedPref = getActivity().getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        String aIsAppPurchased = aSharedPref.getString("is_app_purchased", "");
        if("y".equals(aIsAppPurchased)){
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.fl_qmf_fragment_holder, new PurchasedQuizFragment())
                    .commit();
        }else{
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.fl_qmf_fragment_holder, new BuyAppFragment(),"BuyFragment")
                    .commit();
        }
        return rootView;
    }
}

