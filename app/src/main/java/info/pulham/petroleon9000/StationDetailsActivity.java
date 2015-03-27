package info.pulham.petroleon9000;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

// Final class to implement.
public class StationDetailsActivity extends Activity {
    PetrolStation station;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_details);

        Intent inboundIntent = getIntent();
        this.station = inboundIntent.getParcelableExtra("station");

        // Set up the initial values
        TextView name = (TextView) findViewById(R.id.station_name);
        name.setText(this.station.name);

        EditText petrol = (EditText) findViewById(R.id.petrol_edit);
        EditText diesel = (EditText) findViewById(R.id.diesel_edit);
        EditText lpg = (EditText) findViewById(R.id.lpg_edit);

        petrol.setText(Integer.toString(this.station.petrol));
        diesel.setText(Integer.toString(this.station.diesel));
        lpg.setText(Integer.toString(this.station.lpg));
    }
}
