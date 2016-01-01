package fightingpit.barrons1100;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import fightingpit.barrons1100.util.IabHelper;
import fightingpit.barrons1100.util.IabResult;
import fightingpit.barrons1100.util.Purchase;

public class BuyAppFragment extends Fragment {

    @Bind(R.id.bt_fba_buy) Button mBuyButton;
    IabHelper mHelper;
    String TAG = "ABG";

    public static BuyAppFragment newInstance() {
        BuyAppFragment fragment = new BuyAppFragment();
        return fragment;
    }

    public BuyAppFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_buy_app, container, false);
        ButterKnife.bind(this, rootView);

        TextView aBuyText = (TextView) rootView.findViewById(R.id.tv_fba_word);
        aBuyText.setText(Html.fromHtml("Upgrade to premium version.<br>★ Enable Quiz<br>★ Disable Advertisements"));

        try{
            mHelper = new IabHelper(getActivity(), getResources().getString(R.string.base64EncodedPublicKey));
            mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                public void onIabSetupFinished(IabResult result) {
                    if (!result.isSuccess()) {
                        // Oh noes, there was a problem.
                        Log.d(TAG, "Problem setting up In-app Billing: " + result);
                    } else {
                        Log.d(TAG, "Helper Setup Complete");
                    }
                }
            });
        }catch (Exception e){
            Log.d(TAG, "Exception Caught while Setting up helper :" + e);
        }


        mBuyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try{
                mHelper.flagEndAsync();
                mHelper.launchPurchaseFlow(getActivity(), "premium", 10001,
                        mPurchaseFinishedListener, "myToken");
                }catch (Exception e){
                    Log.d(TAG, "Exception Caught :" + e);
                    Toast.makeText(getActivity().getBaseContext(),"There was some problem while connecting to internet."+
                            " Activate internet, restart application and try again.", Toast.LENGTH_LONG).show();
                }

            }
        });
        return rootView;
    }

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener
            = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase)
        {
            try {
                if (result.isFailure()) {
                    Log.d(TAG, "Error purchasing: " + result);
                    return;
                } else if (purchase.getSku().equalsIgnoreCase("premium")) {
                    Log.d(TAG, "Purchase Finished");
                    SharedPreferences aSharedPref = getActivity().getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
                    SharedPreferences.Editor aEditor = aSharedPref.edit();
                    aEditor.putString("is_app_purchased", "y");
                    aEditor.commit();
                    Toast.makeText(getActivity(), "Premium Version Activated.", Toast.LENGTH_LONG).show();
                    Intent i = getActivity().getBaseContext().getPackageManager()
                            .getLaunchIntentForPackage(getActivity().getPackageName());
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    return;
                }
        }catch (Exception e){
                Log.d(TAG, "Exception Caught :" + e);
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null) mHelper.dispose();
        mHelper = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                    Intent data)
    {
        if (!mHelper.handleActivityResult(requestCode,
                resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
