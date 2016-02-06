package com.thomson2412.kamersessie.dataObject;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Thomas on 10-10-2015.
 */
public class SessionData implements Parcelable {
    private final String sessionname;
    private final String sessionCreator;
    private final String location;
    private final String partyname;
    private final String startTime;

    public SessionData(String sN, String sC, String l, String pN, String sT) {
        sessionname = sN;
        sessionCreator = sC;
        location = l;
        partyname = pN;
        startTime = sT;
    }

    public String getSessionname(){
        return sessionname;
    }
    public String getSessionCreator() {
        return sessionCreator;
    }

    public String getPartyname() {
        return partyname;
    }

    public String getLocation() {
        return location;
    }

    public String getStartTime() {
        return startTime;
    }

    // Parcelling part
    public SessionData(Parcel in) {
        String[] data = new String[5];
        in.readStringArray(data);
        sessionname = data[0];
        sessionCreator = data[1];
        location = data[2];
        partyname = data[3];
        startTime = data[4];
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{this.sessionname,
                this.sessionCreator,
                this.location,
                this.partyname,
                this.startTime});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public SessionData createFromParcel(Parcel in) {
            return new SessionData(in);
        }

        public SessionData[] newArray(int size) {
            return new SessionData[size];
        }

    };
}
