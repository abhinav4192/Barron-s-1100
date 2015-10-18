package fightingpit.barrons1100;

/**
 * Created by AG on 08-Oct-15.
 */

import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class FlashCardsFragment extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.flash_cards_fragment, container, false);


        getFragmentManager()
                .beginTransaction()
                .add(R.id.fl_fcf_cardView_holder, new FlashCardFrontFragment())
                .commit();

        Button aButton = (Button) rootView.findViewById(R.id.bt_fcf_show_meaning);
        aButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flipCard();
            }
        });



        return rootView;
    }


    public void flipCard() {
//        if (mShowingBack) {
//            getFragmentManager().popBackStack();
//            return;
//        }
//
//        // Flip to the back.
//
//        mShowingBack = true;

        // Create and commit a new fragment transaction that adds the fragment for the back of
        // the card, uses custom animations, and is part of the fragment manager's back stack.

        getFragmentManager()
                .beginTransaction()

                        // Replace the default fragment animations with animator resources representing
                        // rotations when switching to the back of the card, as well as animator
                        // resources representing rotations when flipping back to the front (e.g. when
                        // the system Back button is pressed).
                .setCustomAnimations(
                        R.animator.card_flip_right_in, R.animator.card_flip_right_out,
                        R.animator.card_flip_left_in, R.animator.card_flip_left_out)

                        // Replace any fragments currently in the container view with a fragment
                        // representing the next page (indicated by the just-incremented currentPage
                        // variable).
                .replace(R.id.fl_fcf_cardView_holder, new FlashCardBackFragment())

                        // Add this transaction to the back stack, allowing users to press Back
                        // to get to the front of the card.
                .addToBackStack(null)

                        // Commit the transaction.
                .commit();
    }

    public void testMEthod(){
        Log.d("ABG", "test MEthod Called");
        flipCard();
    }
}

