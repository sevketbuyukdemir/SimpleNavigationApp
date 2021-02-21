package prj_2.stu_1505005.AsyncTasks;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import androidx.appcompat.app.AlertDialog;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import prj_2.stu_1505005.NavigationActivity;
import prj_2.stu_1505005.R;

/**
 * This class retrieve route information between starting and destination coordinates with jSoup API.
 * DirectionsRetriever class extends AsyncTask class because this process is a heavy task.
 */
public class DirectionsRetriever extends AsyncTask<Void, Integer, JSONObject> {
    private static final String DIRECTIONS_LOG_TAG = "DIRECTIONS";
    private JSONObject jsonObject;
    @SuppressLint("StaticFieldLeak")
    private Activity activity;
    // Context is necessary because this activity direct user to Navigation activity
    @SuppressLint("StaticFieldLeak")
    private Context context;
    // ProgressDialog to show the user the progress this dialog have to run on UI thread
    private ProgressDialog progressDialog;
    // Starting address coordinates
    double starting_address_latitude;
    double starting_address_longitude;
    // Destination address coordinates
    double destination_address_latitude;
    double destination_address_longitude;

    /**
     * Constructor
     * @param context Context
     * @param starting_address_latitude double
     * @param starting_address_longitude double
     * @param destination_address_latitude double
     * @param destination_address_longitude double
     */
    public DirectionsRetriever(Activity activity,
                               Context context,
                               double starting_address_latitude,
                               double starting_address_longitude,
                               double destination_address_latitude,
                               double destination_address_longitude){
        this.activity = activity;
        this.context = context;
        this.starting_address_latitude = starting_address_latitude;
        this.starting_address_longitude = starting_address_longitude;
        this.destination_address_latitude = destination_address_latitude;
        this.destination_address_longitude = destination_address_longitude;
    }

    /**
     * Initiate and prepare ProgressDialog
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        activity.runOnUiThread(new Runnable() {
            public void run() {
                progressDialog = new ProgressDialog(context);
                progressDialog.setTitle(context.getString(R.string.directions_retriever_progress_dialog_title));
                progressDialog.setMessage(context.getString(R.string.directions_retriever_progress_dialog_message));
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setCancelable(false);
                progressDialog.setMax(100);
                progressDialog.show();
            }
        });
    }

    /**
     * Update progress information of ProgressDialog
     * @param values Integer
     */
    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        activity.runOnUiThread(new Runnable() {
            public void run() {
                progressDialog.setProgress(values[0]);
            }
        });
    }

    /**
     * Process jSoup task and then return retrieved information as JSONObject
     * @param params Void
     * @return jsonObject JSONObject
     */
    @Override
    protected JSONObject doInBackground(Void... params) {
        try {
            String directions_api_request = prepareDirectionsAPIURL(true, true,
                    starting_address_latitude,
                    starting_address_longitude,
                    destination_address_latitude,
                    destination_address_longitude);

            String response = Jsoup.connect(directions_api_request)
                    .ignoreContentType(true) // To Get JSON DATA
                    .get()
                    .text();

            jsonObject = new JSONObject(response);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * After jSoup task prepare information direct user to NavigationActivity
     * @param jsonObject JSONObject
     */
    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        super.onPostExecute(jsonObject);
        Intent intent = new Intent(context, NavigationActivity.class);
        intent.putExtra("starting_address_latitude", starting_address_latitude);
        intent.putExtra("starting_address_longitude", starting_address_longitude);
        intent.putExtra("destination_address_latitude", destination_address_latitude);
        intent.putExtra("destination_address_longitude", destination_address_longitude);
        intent.putExtra("directionHashMap", prepareResultHashMap(jsonObject));
        context.startActivity(intent);
        activity.runOnUiThread(new Runnable() {
            public void run() {
                progressDialog.dismiss();
            }
        });
    }

    /**
     * This function return a request URL as string for Directions API
     * You can specify URL features with isHttps and isJSON parameters
     * isHttps : true -> https://....
     * isHttps : false -> http://....
     * isJSON : true -> Response format will be JSONObject format
     * isJSON : false -> Response format will be XML format
     *
     * @param isHttps boolean
     * @param isJSON boolean
     * @param origin_latitude double -> starting address latitude
     * @param origin_longitude double -> starting address longitude
     * @param destination_latitude double -> destination address latitude
     * @param destination_longitude double -> destination address longitude
     * @return request String
     */
    protected String prepareDirectionsAPIURL(boolean isHttps,
                                             boolean isJSON,
                                             double origin_latitude,
                                             double origin_longitude,
                                             double destination_latitude,
                                             double destination_longitude) {
        String https_base = "https://maps.googleapis.com/maps/api/directions/";
        String http_base = "http://maps.googleapis.com/maps/api/directions/";
        String json = "json?";
        String xml = "xml?";
        String mode = "&mode=driving";
        String origin = "&origin=";
        String origin_address = origin_latitude + "," + origin_longitude;
        String destination = "&destination=";
        String destination_address = destination_latitude + "," + destination_longitude;
        String api_key = "&key=";
        String my_api_key = "AIzaSyAkTbubZf4u1-yVl9ti5CYwIikqNdDo_H0";

        String request = null;

        if(isHttps) {
            if(isJSON){
                request = https_base + json + mode + origin + origin_address + destination + destination_address + api_key + my_api_key;
            } else {
                request = https_base + xml + mode + origin + origin_address + destination + destination_address + api_key + my_api_key;
            }
        } else {
            if(isJSON){
                request = http_base + json + mode + origin + origin_address + destination + destination_address + api_key + my_api_key;
            } else {
                request = http_base + xml + mode + origin + origin_address + destination + destination_address + api_key + my_api_key;
            }
        }

        return request;
    }

    /**
     * This function get directions steps information from JSONObject response of Directions API
     * and assign this information to HashMap. Finally return this HashMap.
     *
     * hashMap keys (value example):
     * first_object_of_legs_distance_text (7.7 km)
     * first_object_of_legs_distance_value (7713.0)
     * first_object_of_legs_duration_text (11 mins)
     * first_object_of_legs_duration_value (670.0)
     * first_object_of_legs_end_address (Yeni Sahra, Demokrasi Cd No:10/4, 34746 Ataşehir/İstanbul, Turkey)
     * first_object_of_legs_end_location_latitude (40.9889582)
     * first_object_of_legs_end_location_longitude (29.0862987)
     * first_object_of_legs_start_address (Girne, Çiftlikli Sokağı No:32, 34852 Maltepe/İstanbul, Turkey)
     * first_object_of_legs_start_location_latitude (40.9396905)
     * first_object_of_legs_start_location_longitude (29.1378592)
     * first_object_of_legs_steps_count // This integer value contains steps count (cast to integer from string)
     * (row_number_of_step)_current_step_distance_text (41 m)
     * (row_number_of_step)_current_step_distance_value (108.0)
     * (row_number_of_step)_current_step_duration_text (4 mins)
     * (row_number_of_step)_current_step_duration_value (84.0)
     * (row_number_of_step)_current_step_start_location_latitude (40.9375835)
     * (row_number_of_step)_current_step_start_location_longitude (29.1281916)
     * (row_number_of_step)_current_step_end_location_latitude (40.9864661)
     * (row_number_of_step)_current_step_end_location_longitude (29.1390159)
     * (row_number_of_step)_current_step_maneuver (turn-left) (if exist i can not
     * find any clear documentation)
     *
     * @param jsonObject JSONObject Directions API response
     * @return HashMap<String, String> hashMap This HashMap created for assign step information
     * of direction
     */
    protected HashMap<String, String> prepareResultHashMap(JSONObject jsonObject){
        try {
            // This HashMap created for assign step information
            HashMap<String, String> hashMap = new HashMap<>();
            // This json array contains routes information
            JSONArray routes = jsonObject.getJSONArray("routes");
            // This json object contains first route
            JSONObject first_object_of_routes = routes.getJSONObject(0);
            // This json array contains different legs of route
            JSONArray legs = first_object_of_routes.getJSONArray("legs");
            // This json array contains first leg
            JSONObject first_object_of_legs = legs.getJSONObject(0);
            // This json object contains distance information of first leg
            JSONObject first_object_of_legs_distance = first_object_of_legs.getJSONObject("distance");
            String first_object_of_legs_distance_text = first_object_of_legs_distance.getString("text");
            String first_object_of_legs_distance_value = String.valueOf(first_object_of_legs_distance.getDouble("value"));
            hashMap.put("first_object_of_legs_distance_text", first_object_of_legs_distance_text);
            hashMap.put("first_object_of_legs_distance_value", first_object_of_legs_distance_value);
            // This json object contains duration information of first leg
            JSONObject first_object_of_legs_duration = first_object_of_legs.getJSONObject("duration");
            String first_object_of_legs_duration_text = first_object_of_legs_duration.getString("text");
            String first_object_of_legs_duration_value = String.valueOf(first_object_of_legs_duration.getDouble("value"));
            hashMap.put("first_object_of_legs_duration_text", first_object_of_legs_duration_text);
            hashMap.put("first_object_of_legs_duration_value", first_object_of_legs_duration_value);
            // This string contains destination address information of first leg
            String first_object_of_legs_end_address = first_object_of_legs.getString("end_address");
            hashMap.put("first_object_of_legs_end_address", first_object_of_legs_end_address);
            // This json object contains latitude and longitude information of destination address
            JSONObject first_object_of_legs_end_location = first_object_of_legs.getJSONObject("end_location");
            String first_object_of_legs_end_location_latitude = String.valueOf(first_object_of_legs_end_location.getDouble("lat"));
            String first_object_of_legs_end_location_longitude = String.valueOf(first_object_of_legs_end_location.getDouble("lng"));
            hashMap.put("first_object_of_legs_end_location_latitude", first_object_of_legs_end_location_latitude);
            hashMap.put("first_object_of_legs_end_location_longitude", first_object_of_legs_end_location_longitude);
            // This string contains starting address information of first leg
            String first_object_of_legs_start_address = first_object_of_legs.getString("start_address");
            hashMap.put("first_object_of_legs_start_address", first_object_of_legs_start_address);
            // This json object contains latitude and longitude information of starting address
            JSONObject first_object_of_legs_start_location = first_object_of_legs.getJSONObject("start_location");
            String first_object_of_legs_start_location_latitude = String.valueOf(first_object_of_legs_start_location.getDouble("lat"));
            String first_object_of_legs_start_location_longitude = String.valueOf(first_object_of_legs_start_location.getDouble("lng"));
            hashMap.put("first_object_of_legs_start_location_latitude", first_object_of_legs_start_location_latitude);
            hashMap.put("first_object_of_legs_start_location_longitude", first_object_of_legs_start_location_longitude);
            // This json array contains steps of first leg
            JSONArray first_object_of_legs_steps = first_object_of_legs.getJSONArray("steps");
            // This integer value contains steps count
            int first_object_of_legs_steps_count_value = first_object_of_legs_steps.length();
            String first_object_of_legs_steps_count = String.valueOf(first_object_of_legs_steps_count_value);
            hashMap.put("first_object_of_legs_steps_count", first_object_of_legs_steps_count);
            // Assign steps information one by one
            for (int i = 0; i < first_object_of_legs_steps.length(); i++) {
                // This json array contains current step information of steps
                JSONObject current_step = first_object_of_legs_steps.getJSONObject(i);
                // This json object contains distance information of current step
                JSONObject current_step_distance = current_step.getJSONObject("distance");
                String current_step_distance_text = current_step_distance.getString("text");
                String current_step_distance_value = String.valueOf(current_step_distance.getDouble("value"));
                hashMap.put(i + "_current_step_distance_text", current_step_distance_text);
                hashMap.put(i + "_current_step_distance_value", current_step_distance_value);
                // This json object contains duration information of current step
                JSONObject current_step_duration = current_step.getJSONObject("duration");
                String current_step_duration_text = current_step_duration.getString("text");
                String current_step_duration_value = String.valueOf(current_step_duration.getDouble("value"));
                hashMap.put(i + "_current_step_duration_text", current_step_duration_text);
                hashMap.put(i + "_current_step_duration_value", current_step_duration_value);
                // This json object contains latitude and longitude information of start location of current step
                JSONObject current_step_start_location = current_step.getJSONObject("start_location");
                String current_step_start_location_latitude = String.valueOf(current_step_start_location.getDouble("lat"));
                String current_step_start_location_longitude = String.valueOf(current_step_start_location.getDouble("lng"));
                hashMap.put(i + "_current_step_start_location_latitude", current_step_start_location_latitude);
                hashMap.put(i +"_current_step_start_location_longitude", current_step_start_location_longitude);
                // This json object contains latitude and longitude information of destination location of current step
                JSONObject current_step_end_location = current_step.getJSONObject("end_location");
                String current_step_end_location_latitude = String.valueOf(current_step_end_location.getDouble("lat"));
                String current_step_end_location_longitude = String.valueOf(current_step_end_location.getDouble("lng"));
                hashMap.put(i + "_current_step_end_location_latitude", current_step_end_location_latitude);
                hashMap.put(i +"_current_step_end_location_longitude", current_step_end_location_longitude);
                String current_step_maneuver = "maneuver";
                if(first_object_of_legs_start_location.has("maneuver")){
                    current_step_maneuver = current_step.getString("maneuver");
                    hashMap.put(i +"_current_step_maneuver", current_step_maneuver);
                } else {
                    hashMap.put(i +"_current_step_maneuver", "maneuver not found");
                }

            }
            return hashMap;
        } catch (Exception e) {
            showErrorDialog(e.getMessage());
        }
        return null;
    }

    /**
     * This function show error message with AlertDialog
     * Enter message as parameter
     * @param errorMessage String
     */
    private void showErrorDialog(String errorMessage) {
        AlertDialog.Builder adb = new AlertDialog.Builder(context);
        adb.setTitle(DIRECTIONS_LOG_TAG);
        adb.setMessage(errorMessage);
        adb.setPositiveButton(R.string.show_error_dialog_positive_button_message, null);
        adb.show();
    }

    /**
     * This function print HashMaps with for loop
     * @param hashMap HashMap<String, String>
     */
    private void printHashMap(HashMap<String, String> hashMap){
        List<String> keys = new ArrayList<>(hashMap.keySet());
        for (int i = 0; i < keys.size(); i++) {
            String key = (String) keys.get(i);
            System.out.println(key + " : " + hashMap.get(key));
        }
    }
}