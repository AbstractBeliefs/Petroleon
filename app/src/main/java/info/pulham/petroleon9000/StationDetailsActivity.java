package info.pulham.petroleon9000;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;

// Final class to implement.
public class StationDetailsActivity extends Activity {
    private static final String DEBUGTAG = "DetailsActivity";
    PetrolStation station;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_details);

        Intent inboundIntent = getIntent();
        this.station = inboundIntent.getParcelableExtra("station");

        // Set up the initial values
        TextView name = (TextView) findViewById(R.id.station_name);
        TextView vicinity = (TextView) findViewById(R.id.station_vicinity);
        name.setText(this.station.name);
        vicinity.setText(this.station.vicinity);

        EditText petrol = (EditText) findViewById(R.id.petrol_edit);
        EditText diesel = (EditText) findViewById(R.id.diesel_edit);
        EditText lpg = (EditText) findViewById(R.id.lpg_edit);

        petrol.setText(Integer.toString(this.station.petrol));
        diesel.setText(Integer.toString(this.station.diesel));
        lpg.setText(Integer.toString(this.station.lpg));
    }

    public void startNavigation(View parent) {
        Uri gmmIntentUri = Uri.parse("google.navigation:q="+this.station.name+", "+this.station.vicinity);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    public void saveStation(View parent){
        EditText petrol = (EditText) findViewById(R.id.petrol_edit);
        EditText diesel = (EditText) findViewById(R.id.diesel_edit);
        EditText lpg = (EditText) findViewById(R.id.lpg_edit);

        if (petrol.getText().toString().equals("")){
            petrol.setText("0");
        }
        if (diesel.getText().toString().equals("")){
            diesel.setText("0");
        }
        if (lpg.getText().toString().equals("")){
            lpg.setText("0");
        }

        this.station.petrol = Integer.parseInt(petrol.getText().toString());
        this.station.diesel = Integer.parseInt(diesel.getText().toString());
        this.station.lpg = Integer.parseInt(lpg.getText().toString());

        if (connected()) {
            new saveStationTask().execute(this.station);
        } else {
            Context context = getApplicationContext();
            Toast toast = Toast.makeText(context, "No connection to save with!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private class saveStationTask extends AsyncTask<PetrolStation, Void, Boolean>{
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

        @Override
        protected Boolean doInBackground(PetrolStation... stations){
            PetrolStation station = stations[0];
            try {
                String url = String.format("http://apps.pulham.info/updatestation?id=%s&name=%s&vicinity=%s&lat=%f&lon=%f&petrol=%d&diesel=%d&lpg=%d",
                        station.id,
                        URLEncoder.encode(station.name, "UTF-8"),
                        URLEncoder.encode(station.vicinity, "UTF-8"),
                        station.location.getLatitude(),
                        station.location.getLongitude(),
                        station.petrol,
                        station.diesel,
                        station.lpg
                );

                HttpClient client = new DefaultHttpClient();
                HttpResponse response = client.execute(new HttpGet(url));
                InputStream responseStream = response.getEntity().getContent();

                String responseJSON;
                if (responseStream != null){
                    responseJSON = readStream(responseStream);
                } else {
                    responseJSON = "{\"result\": \"bad\", \"reason\": \"parsing\"}";
                }
                JSONObject jsonResponse = new JSONObject(responseJSON);
                if (jsonResponse.getString("result").equals("ok")){
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                Log.e(DEBUGTAG, e.getMessage());
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result){
            if (result){
                Context context = getApplicationContext();
                Toast toast = Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT);
                toast.show();
            }
            else {
                Context context = getApplicationContext();
                Toast toast = Toast.makeText(context, "Couldn't save.", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    public boolean connected(){
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }
}
