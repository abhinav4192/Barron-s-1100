package fightingpit.barrons1100;

/**
 * Created by AG on 08-Oct-15.
 */

import android.support.v13.app.FragmentPagerAdapter;
import android.app.Fragment;
import android.app.FragmentManager;

public class TabsPagerAdapter extends FragmentPagerAdapter {

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

    @Override
    public int getItemPosition(Object object)
    {
        return POSITION_NONE;
    }


}