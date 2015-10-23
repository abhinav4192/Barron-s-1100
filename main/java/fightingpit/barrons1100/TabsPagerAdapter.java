package fightingpit.barrons1100;

/**
 * Created by AG on 08-Oct-15.
 */

import android.support.v13.app.FragmentPagerAdapter;
import android.app.Fragment;
import android.app.FragmentManager;
import android.util.Log;

public class TabsPagerAdapter extends FragmentPagerAdapter {
    private String mTabName = new String();

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                return new WordListFragment();
            case 1:
                return new FlashCardsFragment();
            case 2:
                return new QuizFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 3;
    }

//    @Override
//    public int getItemPosition(Object object)
//    {
//        position;
//        return POSITION_NONE;
//    }


    @Override
    public int getItemPosition(Object object) {

        Log.d("ABG", object.getClass().getSimpleName());
        if(object.getClass().getSimpleName().equals(mTabName)){
            return POSITION_UNCHANGED;
        }
        else {
            return POSITION_NONE;
        }
    }

    public void setTabName(String iTabName){
        mTabName = iTabName;
    }
}