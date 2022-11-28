package CategoryFragment.Entertainment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.srisu.CategoryVPagerAdapter;
import com.example.srisu.MainActivity;
import com.example.srisu.R;
import com.example.srisu.databinding.FragmentEntertainmentBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import ChatsSection.EditProfileActivity;
import SignInSection.AddPartnerActivity;


public class EntertainmentFragment extends Fragment {

    FragmentEntertainmentBinding entertainmentBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        entertainmentBinding =  FragmentEntertainmentBinding.inflate(inflater, container, false);

        int tabCount = entertainmentBinding.categoryTab.getTabCount();

        EntertainmentVPagerAdapter entertainmentVPagerAdapter = new EntertainmentVPagerAdapter(getChildFragmentManager(), tabCount);
        entertainmentBinding.EntertainmentVPager.setAdapter(entertainmentVPagerAdapter);

        entertainmentBinding.categoryTab.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                entertainmentBinding.EntertainmentVPager.setCurrentItem(tab.getPosition());
                entertainmentBinding.categoryTab.getTabAt(tab.getPosition()).select();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        entertainmentBinding.EntertainmentVPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(entertainmentBinding.categoryTab));

        return entertainmentBinding.getRoot();
    }
}