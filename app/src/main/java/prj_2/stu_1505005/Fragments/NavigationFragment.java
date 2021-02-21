package prj_2.stu_1505005.Fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import java.util.HashMap;
import prj_2.stu_1505005.MainActivity;
import prj_2.stu_1505005.R;

/**
 * This fragment has full screen Google Map View. According to user Starting And Destination Points,
 * Map will have 2 different markers, a route polyline between those markers and user current
 * location also.
 */
public class NavigationFragment extends Fragment {
    private GoogleMap mMap;
    private Context context;
    private Double starting_address_latitude = -1d;
    private Double starting_address_longitude = -1d;
    private Double destination_address_latitude = -1d;
    private Double destination_address_longitude = -1d;
    private HashMap<String, String> directionHashMap;
    private LatLng bounds[];

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_navigation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = this.getArguments();
        /**
         * if (bundle != null) get addresses and route information from bundle
         * Else send user to MainActivity
         */
        if (bundle != null) {
            // Assigning address coordinates to global double variables
            starting_address_latitude = bundle.getDouble("starting_address_latitude");
            starting_address_longitude = bundle.getDouble("starting_address_longitude");
            destination_address_latitude = bundle.getDouble("destination_address_latitude");
            destination_address_longitude = bundle.getDouble("destination_address_longitude");

            // Getting HashMap which contain route information and assigning global HashMap
            directionHashMap = (HashMap<String, String>)bundle.getSerializable("directionHashMap");

            // Getting route's step count and assigning to local integer variable
            int first_object_of_legs_steps_count = Integer.valueOf(directionHashMap.get("first_object_of_legs_steps_count"));

            // Creating global LatLng array in memory
            bounds = new LatLng[first_object_of_legs_steps_count*2];

            // Creating local LatLng arrays for every steps' starting and ending points
            LatLng starting_bounds[] = new LatLng[first_object_of_legs_steps_count];
            LatLng ending_bounds[] = new LatLng[first_object_of_legs_steps_count];

            // Fill starting_bounds array with starting coordinates of route
            for (int i = 0; i < first_object_of_legs_steps_count; i++) {
                double step_starting_latitude = Double.valueOf(directionHashMap.get(i+"_current_step_start_location_latitude"));
                double step_starting_longitude = Double.valueOf(directionHashMap.get(i+"_current_step_start_location_longitude"));
                starting_bounds[i] = new LatLng(step_starting_latitude, step_starting_longitude);
            }

            // Fill ending_bounds array with ending coordinates of route
            for (int i = 0; i < first_object_of_legs_steps_count; i++) {
                double step_ending_latitude = Double.valueOf(directionHashMap.get(i+"_current_step_end_location_latitude"));
                double step_ending_longitude = Double.valueOf(directionHashMap.get(i+"_current_step_end_location_longitude"));
                ending_bounds[i] = new LatLng(step_ending_latitude, step_ending_longitude);
            }

            // Combining starting_bounds and ending_bounds and assigning to global bounds array
            int bounds_index = 0;
            for (int index = 0; index < first_object_of_legs_steps_count; index++) {
                bounds[bounds_index] = starting_bounds[index];
                bounds[bounds_index+1] = ending_bounds[index];
                bounds_index += 2;
            }
        } else {
            Intent intent_price_display = new Intent(context, MainActivity.class);
            startActivity(intent_price_display);
        }

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            /**
             * if "if (bundle == null)" in onViewCreated function, points coordinates will "-1d".
             * if points coordinates will "-1d", show error dialog.
             * Else add markers and draw route.
             */
            if(starting_address_latitude == -1d && starting_address_longitude == -1d &&
                    destination_address_latitude == -1d && destination_address_longitude == -1d) {
                showErrorDialog(getString(R.string.error_message_bundle_error));
            } else {
                // Prepare starting LatLng object with starting point coordinates
                LatLng starting_latitude_longitude = new LatLng(starting_address_latitude, starting_address_longitude);

                // Prepare destination LatLng object with destination point coordinates
                LatLng destination_latitude_longitude = new LatLng(destination_address_latitude, destination_address_longitude);

                // Add marker to starting point
                mMap.addMarker(new MarkerOptions().position(starting_latitude_longitude).title("starting_latitude_longitude"));

                // Add marker to destination point
                mMap.addMarker(new MarkerOptions().position(destination_latitude_longitude).title("destination_latitude_longitude"));

                // Zoom and animate camera to starting point of route
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(starting_latitude_longitude, 15));

                // Prepare PolylineOptions for draw route
                PolylineOptions polylineOptions = new PolylineOptions()
                        .add(bounds)
                        .width(13.0f)
                        .color(Color.rgb(255,0,0));

                // Adding route drawing to map
                mMap.addPolyline(polylineOptions);
            }

            // SHOW USER LOCATION
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            // Control for location permissions
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            // Set location to GoogleMap object
            mMap.setMyLocationEnabled(true);
        }
    };

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

    /**
     * This function show error message with AlertDialog
     * Enter message as parameter
     * @param errorMessage String
     */
    private void showErrorDialog(String errorMessage) {
        AlertDialog.Builder adb = new AlertDialog.Builder(context);
        adb.setTitle(R.string.show_error_dialog_title);
        adb.setMessage(errorMessage);
        adb.setPositiveButton(R.string.show_error_dialog_positive_button_message, null);
        adb.show();
    }
}