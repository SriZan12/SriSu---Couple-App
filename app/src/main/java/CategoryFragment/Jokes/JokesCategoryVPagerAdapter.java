package CategoryFragment.Jokes;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import CategoryFragment.Jokes.JokesCategoryFragments.AnyFragment;
import CategoryFragment.Jokes.JokesCategoryFragments.ChristmasFragment;
import CategoryFragment.Jokes.JokesCategoryFragments.DarkFragment;
import CategoryFragment.Jokes.JokesCategoryFragments.MiscFragment;
import CategoryFragment.Jokes.JokesCategoryFragments.ProgrammingFragment;
import CategoryFragment.Jokes.JokesCategoryFragments.PunFragment;
import CategoryFragment.Jokes.JokesCategoryFragments.SpookyFragment;

public class JokesCategoryVPagerAdapter extends FragmentPagerAdapter {

    int tabCount;

    public JokesCategoryVPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        this.tabCount = behavior;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position){

            case 0:
                return new AnyFragment();
            case 1:
                return new ProgrammingFragment();
            case 2:
                return new DarkFragment();
            case 3:
                return new MiscFragment();
            case 4:
                return new PunFragment();
            case 5:
                return new SpookyFragment();
            case 6:
                return new ChristmasFragment();

        }

        return null;
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
