package fightingpit.barrons1100;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fightingpit.barrons1100.util.IabHelper;
import fightingpit.barrons1100.util.IabResult;
import fightingpit.barrons1100.util.Inventory;
import fightingpit.barrons1100.util.Purchase;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BuyAppFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BuyAppFragment extends Fragment {

    private Button mBuyButton;
    private Button mCheckPurchase;
    private Button mConsumePurchase;
        // The helper object
    IabHelper mHelper;
    String TAG = "ABG";
    static final String SKU_PREMIUM = "premium";

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

        mHelper = new IabHelper(getActivity(), getResources().getString(R.string.base64EncodedPublicKey));
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    Log.d(TAG, "Problem setting up In-app Billing: " + result);
                    Toast.makeText(getActivity(),"Problem setting up In-app Billing: " + result, Toast.LENGTH_LONG).show();
                } else {
                    Log.d(TAG, "Helper Setup Complete");
                    Toast.makeText(getActivity(), "Helper Setup Complete", Toast.LENGTH_LONG).show();
                }
                // Hooray, IAB is fully set up!
            }
        });
        mBuyButton = (Button) rootView.findViewById(R.id.bt_fba_buy);
        mCheckPurchase = (Button) rootView.findViewById(R.id.bt_fba_check);
        mConsumePurchase = (Button) rootView.findViewById(R.id.bt_fba_consume);






        mBuyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                List additionalSkuList = new ArrayList();
//                additionalSkuList.add("premium");

//                mHelper.flagEndAsync();
//                mHelper.queryInventoryAsync(true, additionalSkuList, mQueryFinishedListener);

                mHelper.flagEndAsync();
                mHelper.launchPurchaseFlow(getActivity(), "premium", 10001,
                        mPurchaseFinishedListener, "myToken");

//                Intent i = getActivity().getBaseContext().getPackageManager()
//                        .getLaunchIntentForPackage(getActivity().getPackageName());
//                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(i);



            }
        });

        mCheckPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHelper.flagEndAsync();
                mHelper.queryInventoryAsync(mGotInventoryListener);
            }
        });

        mConsumePurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHelper.flagEndAsync();
                mHelper.queryInventoryAsync(mGotInventoryListenerConsumer);
            }
        });
        return rootView;
    }

//    IabHelper.QueryInventoryFinishedListener
//            mQueryFinishedListener = new IabHelper.QueryInventoryFinishedListener() {
//        public void onQueryInventoryFinished(IabResult result, Inventory inventory)
//        {
//            if (result.isFailure()) {
//                Log.d(TAG,"ERROR"+ result);
//                Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
//
//                // handle error
//                return;
//            }else{
//                Log.d(TAG, "Inv:" + inventory.toString());
//                try{
//                Log.d(TAG,"Price"+ inventory.getSkuDetails("premium").getPrice());
//                Toast.makeText(getActivity(), inventory.getSkuDetails("premium").getPrice(), Toast.LENGTH_LONG).show();}
//                catch (Exception e){
//                    Log.d(TAG, "Exception" + e);
//                    Toast.makeText(getActivity(), "Exception", Toast.LENGTH_LONG).show();
//                }
//            }
//        }
//    };

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener
            = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase)
        {
            if (result.isFailure()) {
                Log.d(TAG, "Error purchasing: " + result);
                Toast.makeText(getActivity(), "Exception", Toast.LENGTH_LONG).show();
                return;
            }
            else if (purchase.getSku().equalsIgnoreCase("premium")) {
                Log.d(TAG, "Purchase Done 1");
                Toast.makeText(getActivity(), "Purchase Done", Toast.LENGTH_LONG).show();
                SharedPreferences aSharedPref = getActivity().getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
                SharedPreferences.Editor aEditor = aSharedPref.edit();
                aEditor.putString("is_app_purchased", "y");
                aEditor.commit();
                Intent i = getActivity().getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage(getActivity().getPackageName());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                return;
            }
        }
    };

    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener =
            new IabHelper.OnConsumeFinishedListener() {
                public void onConsumeFinished(Purchase purchase, IabResult result) {
                    if (result.isSuccess()) {
                        Toast.makeText(getActivity(), "Consume Done", Toast.LENGTH_LONG).show();
                        Toast.makeText(getActivity(), "Purchase Done", Toast.LENGTH_LONG).show();
                        SharedPreferences aSharedPref = getActivity().getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
                        SharedPreferences.Editor aEditor = aSharedPref.edit();
                        aEditor.putString("is_app_purchased", "n");
                        aEditor.commit();
                        Intent i = getActivity().getBaseContext().getPackageManager()
                                .getLaunchIntentForPackage(getActivity().getPackageName());
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    }
                    else {
                        Toast.makeText(getActivity(), "Failed Consumed", Toast.LENGTH_LONG).show();
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
        Log.d(TAG,"REsult");
        if (!mHelper.handleActivityResult(requestCode,
                resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    IabHelper.QueryInventoryFinishedListener mGotInventoryListener
            = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result,
                                             Inventory inventory) {

            if (result.isFailure()) {
                Log.d(TAG, "Query Failure" + result);
            }
            else {
                // does the user have the premium upgrade?
                boolean mIsPremium = inventory.hasPurchase("premium");
                if(mIsPremium){
                    Log.d(TAG, "Is Purchased");
                    Toast.makeText(getActivity(), "Is purchased", Toast.LENGTH_LONG).show();
                }else{
                    Log.d(TAG, "Not Purchased");
                    Toast.makeText(getActivity(), "Not Purchased", Toast.LENGTH_LONG).show();
                }
                // update UI accordingly
            }
        }
    };


    IabHelper.QueryInventoryFinishedListener mGotInventoryListenerConsumer
            = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result,
                                             Inventory inventory) {

            if (result.isFailure()) {
                Log.d(TAG, "Query Failure" + result);
            }
            else {
                if(inventory.hasDetails("premium")){
                    mHelper.consumeAsync(inventory.getPurchase("premium"),
                            mConsumeFinishedListener);
                }
            }
        }
    };


}
