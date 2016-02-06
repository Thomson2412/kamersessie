package com.thomson2412.kamersessie.dataObject;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Thomas on 10-10-2015.
 */
public class PartyData implements Parcelable{
    public final String partyId;
    public final String partyname;
    public final String partyAdim;
    public final String partyCreated;

    public PartyData(String pId, String pname, String pA, String pC){
        partyId = pId;
        partyname = pname;
        partyAdim = pA;
        partyCreated = pC;
    }

    public String getPartyId(){
        return partyId;
    }

    public String getPartyname(){
        return partyname;
    }

    public String getPartyAdim(){
        return partyAdim;
    }

    public String getPartyCreated(){
        return partyCreated;
    }

    // Parcelling part
    public PartyData(Parcel in) {
        String[] data = new String[4];
        in.readStringArray(data);
        partyId = data[0];
        partyname = data[1];
        partyAdim = data[2];
        partyCreated = data[3];
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{this.partyId,
                this.partyname,
                this.partyAdim,
                this.partyCreated});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public PartyData createFromParcel(Parcel in) {
            return new PartyData(in);
        }

        public SessionData[] newArray(int size) {
            return new SessionData[size];
        }

    };
}
