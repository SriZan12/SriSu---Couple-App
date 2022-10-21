package com.example.srisu;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import CategoryFragment.Activity.ActivitiesFragment;
import CategoryFragment.Facts.FactsFragment;
import CategoryFragment.Jokes.JokesFragment;
import CategoryFragment.Memes.MemesFragment;
import CategoryFragment.Bored.BoredFragment;
import CategoryFragment.LoveStories.StoriesFragment;
import CategoryFragment.Movie.MovieFragment;

public class CategoryVPagerAdapter extends FragmentPagerAdapter {

    int tabCount;

    public CategoryVPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        this.tabCount = behavior;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new StoriesFragment();
            case 1:
                return  new BoredFragment();
            case 2:
                return new JokesFragment();
            case 3:
                return new ActivitiesFragment();
            case 4:
                return new MemesFragment();
            case 5:
                return new MovieFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
