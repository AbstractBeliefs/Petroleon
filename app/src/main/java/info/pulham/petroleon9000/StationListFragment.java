package info.pulham.petroleon9000;

import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class StationListFragment extends Fragment {
    private static final String DEBUGTAG = "StationListFragment";

    public StationListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_station_list, container, false);
    }

    public void updateStations(List<PetrolStation> stationList){
        Log.d(DEBUGTAG, "Got a bunch of new stations: " + stationList);

        PetrolStationArrayAdapter adapter = new PetrolStationArrayAdapter(getActivity(), R.layout.petrol_station_list_row, stationList);
        ListView stationListView = (ListView) getView().findViewById(R.id.station_list);
        stationListView.setAdapter(adapter);
    }
}
