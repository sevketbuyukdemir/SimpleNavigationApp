package prj_2.stu_1505005.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import prj_2.stu_1505005.MainActivity;
import prj_2.stu_1505005.R;
import prj_2.stu_1505005.ViewAdapters.StepsRecyclerViewAdapter;

/**
 * In this fragment there is a ListView which is going to be filled with the data which is received
 * from Google Directions API.
 */
public class DirectionFragment extends Fragment {
    // Context is necessary for UI element constructors
    private Context context;
    // This HashMap contains data which is retrieved from Directions API
    private HashMap<String, String> directionHashMap;
    // This TextViews represents total route information on top of screen
    TextView first_object_of_legs_distance_text;
    TextView first_object_of_legs_duration_text;
    TextView first_object_of_legs_start_address;
    TextView first_object_of_legs_end_address;
    TextView first_object_of_legs_steps_title_text;
    // This RecyclerView provides a list view for represent steps' information
    RecyclerView steps_recycler_view;

    /**
     * This function binds screen layout to fragment class.
     * Override from base Fragment class.
     * @param inflater LayoutInflater
     * @param container ViewGroup
     * @param savedInstanceState Bundle
     * @return inflater LayoutInflater
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_direction, container, false);
    }

    /**
     * This function trigger after UI screen and fragment class bind.
     * @param view View
     * @param savedInstanceState Bundle
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Binding UI elements with fragment UI objects with ID
        first_object_of_legs_distance_text = view.findViewById(R.id.first_object_of_legs_distance_text);
        first_object_of_legs_duration_text = view.findViewById(R.id.first_object_of_legs_duration_text);
        first_object_of_legs_start_address = view.findViewById(R.id.first_object_of_legs_start_address);
        first_object_of_legs_end_address = view.findViewById(R.id.first_object_of_legs_end_address);
        first_object_of_legs_steps_title_text = view.findViewById(R.id.first_object_of_legs_steps_title_text);
        steps_recycler_view = view.findViewById(R.id.steps_recycler_view);
        // Initiate directionsInformation HashMap
        HashMap<Integer, ArrayList<String>> directionsInformation = new HashMap<Integer, ArrayList<String>>();
        // bundle contains all information for represent route
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            // Assign information directionsInformation from bundle
            directionHashMap = (HashMap<String, String>)bundle.getSerializable("directionHashMap");
            // Set total route information to TextViews
            first_object_of_legs_distance_text.setText(directionHashMap.get("first_object_of_legs_distance_text"));
            first_object_of_legs_duration_text.setText(directionHashMap.get("first_object_of_legs_duration_text"));
            first_object_of_legs_start_address.setText(directionHashMap.get("first_object_of_legs_start_address"));
            first_object_of_legs_end_address.setText(directionHashMap.get("first_object_of_legs_end_address"));
            first_object_of_legs_steps_title_text.setText(directionHashMap.get("first_object_of_legs_steps_count"));
            // Get steps count
            int first_object_of_legs_steps_count = Integer.valueOf(directionHashMap.get("first_object_of_legs_steps_count"));
            // Assign steps information as array list to hash map with for loop
            for (int i = 0; i < first_object_of_legs_steps_count; i++) {
                // Assign Distance of step to a string variable
                String current_step_distance_text = directionHashMap.get(i + "_current_step_distance_text");
                // Assign Duration of step to a string variable
                String current_step_duration_text = directionHashMap.get(i + "_current_step_duration_text");
                // Assign start location coordinates of step to a string variable
                String current_step_start_location_latitude = directionHashMap.get(i + "_current_step_start_location_latitude");
                String current_step_start_location_longitude = directionHashMap.get(i + "_current_step_start_location_longitude");
                String current_step_starting_text = current_step_start_location_latitude + ", " + current_step_start_location_longitude;
                // Assign end location coordinates of step to a string variable
                String current_step_end_location_latitude = directionHashMap.get(i + "_current_step_end_location_latitude");
                String current_step_end_location_longitude = directionHashMap.get(i + "_current_step_end_location_longitude");
                String current_step_destination_text = current_step_end_location_latitude + ", " + current_step_end_location_longitude;
                // Assign maneuver of step if exist to a string variable
                String current_step_maneuver = directionHashMap.get(i +"_current_step_maneuver");
                // Preparing an ArrayList with string variables of the above
                ArrayList<String> current_step_info = new ArrayList<>();
                current_step_info.add(current_step_distance_text); // 0
                current_step_info.add(current_step_duration_text); // 1
                current_step_info.add(current_step_starting_text); // 2
                current_step_info.add(current_step_destination_text); // 3
                current_step_info.add(current_step_maneuver); // 4
                // Put current_step_info ArrayList to directionsInformation HashMap
                directionsInformation.put(i, current_step_info);
            }
        } else {
            // The user is sent to the MainActivity because the bundle is null.
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
        }
        // Set context and directionsInformation to recycler view adapter constructor
        StepsRecyclerViewAdapter stepsRecyclerViewAdapter = new StepsRecyclerViewAdapter(context, directionsInformation);
        // RecyclerView needs layout manager, i choose linear layout as vertical
        LinearLayoutManager llm = new LinearLayoutManager(context);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        steps_recycler_view.setLayoutManager(llm);
        // Set adapter to recycler view to display step items
        steps_recycler_view.setAdapter(stepsRecyclerViewAdapter);
    }

    /**
     * Override for get context information about host activity.
     * Called when a Fragment is first attached to a host Activity.
     * @param context Context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }
}