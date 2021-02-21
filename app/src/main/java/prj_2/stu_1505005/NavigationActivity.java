package prj_2.stu_1505005;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.HashMap;
import prj_2.stu_1505005.Fragments.DirectionFragment;
import prj_2.stu_1505005.Fragments.NavigationFragment;
import prj_2.stu_1505005.ViewAdapters.ViewPagerAdapter;

/**
 * This activity have one view pager and one tab layout.
 * ViewPager have two fragment which are navigationFragment and directionFragment
 * TabLayout provide an interface for switch fragments in ViewPager
 *
 * FRAGMENTS :
 * NavigationFragment : This fragment has full screen Google Map View. According to user Starting
 * And Destination Points, Map will have 2 different markers, a route polyline between those markers
 * and user current location also.
 * DirectionFragment : In this fragment there is a ListView which is going to be filled with
 * the data which is received from Google Directions API.
 *
 */
public class NavigationActivity extends AppCompatActivity {
    TabLayout navigation_tab_layout;
    ViewPager navigation_viewpager;
    ViewPagerAdapter navigationViewPagerAdapter;
    ArrayList<String> titleList;
    ArrayList<Fragment> fragmentsList;
    NavigationFragment navigationFragment;
    DirectionFragment directionFragment;
    private Double starting_address_latitude;
    private Double starting_address_longitude;
    private Double destination_address_latitude;
    private Double destination_address_longitude;

    /**
     * Bind ViewPager and TabLayout objects with UI by id.
     * Also create fragment objects and add fragments to ViewPager objects
     * Also create ViewPager adapter object and set to ViewPager
     * Bind TabLayout object and ViewPager object
     */
    private void init(){
        // Changing status bar color for more beautiful view
        Window window = NavigationActivity.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(NavigationActivity.this, R.color.navigation_layout_tab_layout_background_color));
        // init fragments
        navigation_tab_layout = findViewById(R.id.navigation_tab_layout);
        navigation_viewpager = findViewById(R.id.navigation_viewpager);
        titleList = new ArrayList<>();
        titleList.add(getString(R.string.navigation_fragment_title));
        titleList.add(getString(R.string.direction_fragment_title));
        navigationFragment = new NavigationFragment();
        directionFragment = new DirectionFragment();
        fragmentsList = new ArrayList<>();
        fragmentsList.add(navigationFragment);
        fragmentsList.add(directionFragment);
        navigationViewPagerAdapter = new ViewPagerAdapter(titleList, fragmentsList,
                getSupportFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        navigation_viewpager.setAdapter(navigationViewPagerAdapter);
        navigation_tab_layout.setupWithViewPager(navigation_viewpager);
    }

    /**
     * OnCreate function call init() function and getExtras() then put this extras to fragments
     * as arguments
     * @param savedInstanceState Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        init();
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            starting_address_latitude = extras.getDouble("starting_address_latitude");
            starting_address_longitude = extras.getDouble("starting_address_longitude");
            destination_address_latitude = extras.getDouble("destination_address_latitude");
            destination_address_longitude = extras.getDouble("destination_address_longitude");
            HashMap<String, String> directionHashMap = (HashMap<String, String>)extras.getSerializable("directionHashMap");
            Bundle bundle = new Bundle();
            bundle.putDouble("starting_address_latitude", starting_address_latitude);
            bundle.putDouble("starting_address_longitude", starting_address_longitude);
            bundle.putDouble("destination_address_latitude", destination_address_latitude);
            bundle.putDouble("destination_address_longitude", destination_address_longitude);
            bundle.putSerializable("directionHashMap", directionHashMap);
            navigationFragment.setArguments(bundle);
            directionFragment.setArguments(bundle);
        }
        // Display map view (navigation fragment) first
        navigation_viewpager.setCurrentItem(0);
    }
}