package com.watsonlogic.malenah.malenah3;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.maps.model.Marker;

import java.io.Serializable;
import java.util.ArrayList;

public class RowItem implements Parcelable,Serializable {
    private String category;
    private long id;
    private String iconURL;
    private String firstName;
    private String lastName;
    private String designation;
    private ArrayList<String> specialty;
    private String organization;
    private String buildingNumber;
    private String street;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private String notes;
    private double latitude;
    private double longitude;
    private double distance;
    private boolean favourited;
    private Marker mapMarker;

    public RowItem(String category, long id, String iconURL,
                   String firstName, String lastName, String designation, ArrayList<String> specialty, String organization,
                   String buildingNumber, String street, String city, String state, String country, String postalCode,
                   String notes, double latitude, double longitude, double distance, boolean favourited){
        this.category=category;
        this.id=id;
        this.iconURL=iconURL;
        this.firstName=firstName;
        this.lastName=lastName;
        this.designation=designation;
        this.specialty=specialty;
        this.organization=organization;
        this.buildingNumber=buildingNumber;
        this.street=street;
        this.city=city;
        this.state=state;
        this.country=country;
        this.postalCode=postalCode;
        this.notes=notes;
        this.latitude=latitude;
        this.longitude=longitude;
        this.distance=distance;
        this.favourited=favourited;
    }


    protected RowItem(Parcel in) {
        category = in.readString();
        id = in.readLong();
        iconURL = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        designation = in.readString();
        specialty = (ArrayList<String>) in.readSerializable();
        organization = in.readString();
        buildingNumber = in.readString();
        street = in.readString();
        city = in.readString();
        state = in.readString();
        country = in.readString();
        postalCode = in.readString();
        notes = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        distance = in.readDouble();
        favourited = (in.readInt()==0)?false:true;
    }

    public static final Creator<RowItem> CREATOR = new Creator<RowItem>() {
        @Override
        public RowItem createFromParcel(Parcel in) {
            return new RowItem(in);
        }

        @Override
        public RowItem[] newArray(int size) {
            return new RowItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(category);
        dest.writeLong(id);
        dest.writeString(iconURL);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(designation);
        dest.writeSerializable(specialty);
        dest.writeString(organization);
        dest.writeString(buildingNumber);
        dest.writeString(street);
        dest.writeString(city);
        dest.writeString(state);
        dest.writeString(country);
        dest.writeString(postalCode);
        dest.writeString(notes);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeDouble(distance);
        dest.writeInt(favourited?1:0);
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIconURL() {
        return iconURL;
    }

    public void setIconURL(String iconURL) {
        this.iconURL = iconURL;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public ArrayList<String> getSpecialty() {
        return specialty;
    }

    public void setSpecialty(ArrayList<String> specialty) {
        this.specialty = specialty;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getBuildingNumber() {
        return buildingNumber;
    }

    public void setBuildingNumber(String buildingNumber) {
        this.buildingNumber = buildingNumber;
    }


    public String getStreet() {
        return street;
    }

    public void setStreet(String streetName) {
        this.street = streetName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public boolean isFavourited() {
        return favourited;
    }

    public void setFavourited(boolean favourited) {
        this.favourited = favourited;
    }

    public Marker getMapMarker() {
        return mapMarker;
    }

    public void setMapMarker(Marker mapMarker) {
        this.mapMarker = mapMarker;
    }
}
