package info.pulham.petroleon9000;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

public class PetrolStation implements Parcelable {
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

    // Parcelableisation ...?
    @Override
    public int describeContents(){
        return 0;
    }

    @Override public void writeToParcel(Parcel pc, int flags){
        pc.writeString(this.id);
        pc.writeString(this.name);

        pc.writeDouble(this.location.getLatitude());
        pc.writeDouble(this.location.getLongitude());

        pc.writeInt(this.petrol);
        pc.writeInt(this.diesel);
        pc.writeInt(this.lpg);
    }

    public static final Parcelable.Creator<PetrolStation> CREATOR = new Parcelable.Creator<PetrolStation>() {
        public PetrolStation createFromParcel(Parcel pc){
            return new PetrolStation(pc);
        }

        public PetrolStation[] newArray(int size){
            return new PetrolStation[size];
        }
    };

    public PetrolStation(Parcel pc){
        this.id = pc.readString();
        this.name = pc.readString();

        this.location = new Location(this.name);
        this.location.setLatitude(pc.readDouble());
        this.location.setLongitude(pc.readDouble());

        this.petrol = pc.readInt();
        this.diesel = pc.readInt();
        this.lpg = pc.readInt();
    }
}
