package CategoryFragment.Jokes;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.srisu.CategoryVPagerAdapter;
import com.example.srisu.R;
import com.example.srisu.databinding.FragmentJokesBinding;
import com.google.android.material.tabs.TabLayout;


public class JokesFragment extends Fragment {

   FragmentJokesBinding jokesBinding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        jokesBinding =  FragmentJokesBinding.inflate(inflater, container, false);
        int tabCount = jokesBinding.JokesCategoryTab.getTabCount();

        JokesCategoryVPagerAdapter categoryVPagerAdapter = new JokesCategoryVPagerAdapter(getChildFragmentManager(), tabCount);
        jokesBinding.categoryVPager.setAdapter(categoryVPagerAdapter);

        jokesBinding.JokesCategoryTab.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                jokesBinding.categoryVPager.setCurrentItem(tab.getPosition());
                jokesBinding.JokesCategoryTab.getTabAt(tab.getPosition()).select();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        jokesBinding.categoryVPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(jokesBinding.JokesCategoryTab));
        // This will change the page according to the tabLayout.

        return jokesBinding.getRoot();
    }
}