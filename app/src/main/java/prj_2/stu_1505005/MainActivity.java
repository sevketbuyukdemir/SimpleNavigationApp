package prj_2.stu_1505005;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import java.util.List;
import prj_2.stu_1505005.AsyncTasks.DirectionsRetriever;

/**
 * This activity has 2 EditTexts and 2 Buttons for getting starting and destination address from
 * user.
 * Also get required permissions from user.
 * Also control internet connection and enforce user to open connection
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final Context context = this;
    private final int MY_PERMISSIONS_ACCESS_COARSE_LOCATION = 0;
    private final int MY_PERMISSIONS_ACCESS_FINE_LOCATION = 1;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_NETWORK_STATE = 2;
    private EditText edt_starting_address;
    private EditText edt_destination_address;
    private Button navigation_button;
    private Button clear_addresses_button;
    private String string_starting_address = "";
    private String string_destination_address = "";
    private Address starting_address;
    private Address destination_address;
    private ProgressDialog progress_dialog_address_find;
    private boolean user_click_network_error_dialog = false;

    /**
     * Control for internet connection when activity on start
     */
    @Override
    protected void onStart() {
        super.onStart();
        if(!networkIsOn()){
            showErrorDialog(getString(R.string.error_message_connection_fail));
        }
    }

    /**
     * onCreate function call init() function and then start enterProgramme() thread.
     * @param savedInstanceState Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        new enterProgramme().start();
    }

    /**
     * Initiate screen components and progressDialog for waiting thread processes
     */
    private void init(){
        progress_dialog_address_find = new ProgressDialog(context);
        // Bind screen objects by id
        edt_starting_address = findViewById(R.id.edt_starting_address);
        edt_destination_address = findViewById(R.id.edt_destination_address);
        navigation_button = findViewById(R.id.navigation_button);
        clear_addresses_button = findViewById(R.id.clear_addresses_button);
        // Set Click Listeners to buttons
        navigation_button.setOnClickListener(this);
        clear_addresses_button.setOnClickListener(this);
    }

    /**
     * Called when a view has been clicked.
     * Start internetEnforceThread() when navigation_button has been clicked
     * Clear addresses edit text when clear_addresses_button has been clicked
     * @param v View
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(navigation_button.getId() == id) {
            try {
                new internetEnforceThread().start();
            } catch (Exception e){
                showErrorDialog(e.getMessage());
            }
        } else if(clear_addresses_button.getId() == id) {
            edt_starting_address.setText("");
            edt_destination_address.setText("");
        }
    }

    /**
     * This thread enforce user to open internet connection for using app.
     * If internet connection is opened this thread run getAddressesThread()
     */
    private class internetEnforceThread extends Thread{
        @Override
        public void run() {
            try{
                if(networkIsOn()){
                    try {
                        string_starting_address = edt_starting_address.getText().toString();
                        string_destination_address = edt_destination_address.getText().toString();
                    } catch (Exception e) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                showErrorDialog(e.getMessage());
                            }
                        });
                    }
                    if(string_starting_address.equals("") && string_destination_address.equals("")){
                        runOnUiThread(new Runnable() {
                            public void run() {
                                showErrorDialog(getString(R.string.error_message_strings_null));
                            }
                        });
                    } else {
                        new getAddressesThread(string_starting_address, string_destination_address).start();
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            // Control for only one dialog creation
                            if(!user_click_network_error_dialog) {
                                user_click_network_error_dialog = true;
                                AlertDialog.Builder adb = new AlertDialog.Builder(context);
                                adb.setTitle(R.string.show_error_dialog_title);
                                adb.setMessage(getString(R.string.error_message_connection_fail));
                                adb.setPositiveButton(R.string.show_error_dialog_positive_button_message, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        user_click_network_error_dialog = false;
                                    }
                                });
                                adb.show();
                            }
                        }
                    });
                    Thread.sleep(2000);
                }
            }catch(InterruptedException e){
                e.printStackTrace();
            }finally {
                if (!networkIsOn()) {
                    new internetEnforceThread().start();
                }
            }
        }
    }

    /**
     * This thread get addresses with getAddressFromString() function,
     * and then execute DirectionsRetriever AsyncTask.
     */
    private class getAddressesThread extends Thread{
        getAddressesThread(String string_starting, String string_destination){
            string_starting_address = string_starting;
            string_destination_address = string_destination;
            runOnUiThread(new Runnable() {
                public void run() {
                    progress_dialog_address_find.setMessage(getString(R.string.progress_dialog_address_find));
                    progress_dialog_address_find.show();
                }
            });
        }
        @Override
        public void run() {
            try{
                starting_address = getAddressFromString(string_starting_address);
                destination_address = getAddressFromString(string_destination_address);
                Thread.sleep(2000);
            }catch(InterruptedException e){
                e.printStackTrace();
            }finally {
                if(string_starting_address.equals("") && string_destination_address.equals("")) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            showErrorDialog(getString(R.string.error_message_addresses_null));
                        }
                    });
                } else {
                    if(starting_address == null && destination_address == null){
                        new getAddressesThread(string_starting_address, string_destination_address).start();
                    } else {
                        // Directions retriever
                        Activity activity = MainActivity.this;
                        new DirectionsRetriever(activity,
                                context,
                                starting_address.getLatitude(),
                                starting_address.getLongitude(),
                                destination_address.getLatitude(),
                                destination_address.getLongitude()).execute();
                    }
                }
                runOnUiThread(new Runnable() {
                    public void run() {
                        progress_dialog_address_find.dismiss();
                    }
                });
            }
        }
    }

    /**
     * Getting address with Geocoder object with getFromLocationName() function
     * @param stringAddress String
     * @return addressList List<Address>
     */
    public Address getAddressFromString(String stringAddress){
        Geocoder coder = new Geocoder(context);
        List<Address> addressList;
        Address address = null;
        try {
            addressList = coder.getFromLocationName(stringAddress,5);
            if (addressList == null) {
                return null;
            }
            address = addressList.get(0);
            return address;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Request permissions with requestAccessLocationPermissions() function if permissions are not
     * granted don't enter programme
     */
    private class enterProgramme extends Thread{
        @Override
        public void run() {
            try{
                requestAccessLocationPermissions();
                Thread.sleep(2000);
            }catch(InterruptedException e){
                e.printStackTrace();
            }finally {
                if (!(ContextCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED)
                && !(ContextCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED)) {
                    new enterProgramme().start();
                }
            }
        }
    }

    /**
     * Request location permissions for using GoogleMap
     */
    private void requestAccessLocationPermissions(){
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[] {Manifest.permission.ACCESS_COARSE_LOCATION},
                MY_PERMISSIONS_ACCESS_COARSE_LOCATION);
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                MY_PERMISSIONS_ACCESS_FINE_LOCATION);
    }

    /**
     * Control for internet connection with ConnectivityManager object and
     * if internet connection is available return true
     * else return false
     * @return networkIsOn boolean
     */
    boolean networkIsOn(){
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null &&
                connectivityManager.getActiveNetworkInfo().isAvailable() &&
                connectivityManager.getActiveNetworkInfo().isConnected();
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