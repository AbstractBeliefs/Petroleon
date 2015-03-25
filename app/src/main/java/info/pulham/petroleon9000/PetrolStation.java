package info.pulham.petroleon9000;

import android.location.Location;

public class PetrolStation {
    public String id;
    public String name;
    public Location location;

    public int petrol;
    public int diesel;
    public int lpg;

    public PetrolStation(String id, String name, double latitude, double longitude, int petrol, int diesel, int lpg){
        this.id = id;
        this.name = name;
        this.location = new Location(name);
        this.location.setLatitude(latitude);
        this.location.setLongitude(longitude);

        this.petrol = petrol;
        this.diesel = diesel;
        this.lpg = lpg;
    }
}
