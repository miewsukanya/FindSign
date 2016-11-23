package miewsukanya.com.findsign;

/**
 * Created by Sukanya Boonpun on 23/11/2559.
 */

public class DataModel {
    private int SignID;
    private String SignName;
    private Double Latitude, Longitude;

    public DataModel(int signID, String signName, double latitude, double longitude) {
    }

    public int getSignID() {
        return SignID;
    }

    public void setSignID(int signID) {
        SignID = signID;
    }

    public String getSignName() {
        return SignName;
    }

    public void setSignName(String signName) {
        SignName = signName;
    }

    public Double getLatitude() {
        return Latitude;
    }

    public void setLatitude(Double latitude) {
        Latitude = latitude;
    }

    public Double getLongitude() {
        return Longitude;
    }

    public void setLongitude(Double longitude) {
        Longitude = longitude;
    }
}
