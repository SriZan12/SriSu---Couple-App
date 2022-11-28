package CategoryFragment.Entertainment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import CategoryFragment.Entertainment.Movie.MovieFragment;
import CategoryFragment.Entertainment.Music.MusicFragment;

public class EntertainmentVPagerAdapter extends FragmentPagerAdapter {

    int tabCount;

    public EntertainmentVPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        this.tabCount = behavior;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                return new MovieFragment();
            case 1:
                return new MusicFragment();
        }

        return null;
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
