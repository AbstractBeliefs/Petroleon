package info.pulham.petroleon9000;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class HomeActivity extends Activity {
    private static final String DEBUGTAG = "HomeActivity";

    // GPS Handling
    private String GPS = LocationManager.GPS_PROVIDER;
    private Location lastKnownLocation = new Location(GPS);
    private LocationManager GPSManager; // Can't get fulfill this until oncreate
    private LocationListener GPSListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            // Save our location and stop getting updates
            lastKnownLocation = location;
            GPSManager.removeUpdates(this);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {}
        public void onProviderEnabled(String provider) {}
        public void onProviderDisabled(String provider) {}
    };
    private Handler GPSUpdateHandler = new Handler();

    // Fragment Handling
    FragmentManager fragMan = getFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Get the Location spun up first, then set it to run every 5 mins thereafter.
        GPSManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        lastKnownLocation = GPSManager.getLastKnownLocation(GPS);
        GPSManager.requestLocationUpdates(GPS, 0,0, GPSListener);
        GPSUpdateHandler.postDelayed(LocationUpdater, 5 * 60 * 1000);

        // Set up the distance spinner
        Spinner distanceSpinner = (Spinner) findViewById(R.id.distance_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.distance_values,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        distanceSpinner.setAdapter(adapter);

        // Setup the list fragment
        FragmentTransaction transaction = fragMan.beginTransaction();
        StationListFragment listFrag = new StationListFragment();
        transaction.add(R.id.view_container, listFrag);
        transaction.commit();
    }

    public void searchButtonClicked(View view){
        Log.d(DEBUGTAG, "Search button clicked");

        Spinner distanceSpinner = (Spinner) findViewById(R.id.distance_spinner);
        String radiusText = distanceSpinner.getSelectedItem().toString();

        int radius = Integer.parseInt(radiusText.replace(" KM", "")) * 1000;
        double lat = lastKnownLocation.getLatitude();
        double lon = lastKnownLocation.getLongitude();
        String url = String.format(
                "http://apps.pulham.info/getstations?lat=%f&lon=%f&radius=%d",
                lat, lon, radius
        );
        new getStationsTask().execute(url);
    }

    private class getStationsTask extends AsyncTask<String, Void, List<PetrolStation>> {
        // Helper function to read a stream and return it as a String
        private String readStream(InputStream stream){
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String output = "";
            String currentLine;
            try {
                while ((currentLine = reader.readLine()) != null) {
                    output += currentLine;
                }
            } catch (Exception e) {
                Log.e(DEBUGTAG, "readStream: " + e.getMessage());
            }
            return output;
        }

        // Core background task for getting and parsing the API
        @Override
        protected List<PetrolStation> doInBackground(String... urls) {
            List<PetrolStation> petrolStations = new ArrayList<>();
            try {
                HttpClient client = new DefaultHttpClient();
                HttpResponse response = client.execute(new HttpGet(urls[0]));
                InputStream responseStream = response.getEntity().getContent();

                String stationJSON;
                if (responseStream != null){
                    stationJSON = readStream(responseStream);
                } else {
                    stationJSON = "{\"result\": \"bad\", \"reason\": \"parsing\"}";
                }

                JSONObject jsonResponse = new JSONObject(stationJSON);
                if (!jsonResponse.getString("result").equals("ok")){
                    throw new NoSuchFieldError("Bad result, not going to be a stations field.");
                }
                JSONArray stationsJsonArray = jsonResponse.getJSONArray("stations");
                for (Integer i = 0; i < stationsJsonArray.length(); i++){
                    petrolStations.add(
                            new PetrolStation(
                                    stationsJsonArray.getJSONObject(i).getString("id"),
                                    stationsJsonArray.getJSONObject(i).getString("name"),
                                    stationsJsonArray.getJSONObject(i).getDouble("latitude"),
                                    stationsJsonArray.getJSONObject(i).getDouble("longitude"),
                                    stationsJsonArray.getJSONObject(i).getInt("petrol"),
                                    stationsJsonArray.getJSONObject(i).getInt("diesel"),
                                    stationsJsonArray.getJSONObject(i).getInt("lpg")
                            )
                    );
                }
                return petrolStations;
            } catch (Exception exception) {
                Log.e(DEBUGTAG, "Background API get: " + exception.getMessage());
            }
            return petrolStations;
        }

        // Apply the retrieved API results
        @Override
        protected void  onPostExecute(List<PetrolStation> results){
            StationListFragment listFrag = (StationListFragment) fragMan.findFragmentById(R.id.view_container);
            listFrag.updateStations(results);
        }
    }
    private Runnable LocationUpdater = new Runnable(){
        @Override
        public void run() {
            GPSManager.requestLocationUpdates(GPS, 0,0, GPSListener);
            GPSUpdateHandler.postDelayed(LocationUpdater, 5 * 60 * 1000);   // Rerun every 5 mins
        }
    };
}
