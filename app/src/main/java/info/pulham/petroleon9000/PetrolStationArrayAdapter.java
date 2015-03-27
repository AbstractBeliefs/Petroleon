package info.pulham.petroleon9000;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by gareth on 26/03/15.
 */
public class PetrolStationArrayAdapter extends ArrayAdapter<PetrolStation> {
    Context context;
    int layoutResourceID;
    List<PetrolStation> entries = null;

    public PetrolStationArrayAdapter(Context context, int layoutResourceId, List<PetrolStation> entries) {
        super(context, layoutResourceId, entries);
        this.layoutResourceID = layoutResourceId;
        this.context = context;
        this.entries = entries;
    }

    @Override
    public View getView(int position, View row, ViewGroup parent){
        if (row == null){
            LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
            row = inflater.inflate(layoutResourceID, parent, false);
        }

        TextView stationName = (TextView) row.findViewById(R.id.station_name);
        TextView petrol = (TextView) row.findViewById(R.id.petrol_price);
        TextView diesel = (TextView) row.findViewById(R.id.diesel_price);
        TextView lpg = (TextView) row.findViewById(R.id.lpg_price);

        PetrolStation currentStation = entries.get(position);

        stationName.setText(currentStation.name);
        petrol.setText(Integer.toString(currentStation.petrol));
        diesel.setText(Integer.toString(currentStation.diesel));
        lpg.setText(Integer.toString(currentStation.lpg));

        return row;
    }
}
