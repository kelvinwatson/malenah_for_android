package com.watsonlogic.malenah.malenah3;

import android.os.Parcel;
import android.os.Parcelable;

public class RowItem implements Parcelable{
    private String category;
    private int id;
    private String iconURL;
    private String name;
    private String designation;
    private String specialty;
    private String description;
    private int buildingNumber = -1;
    private int streetNumber;
    private String streetName;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private String notes;
    private double latitude;
    private double longitude;
    private double distance;
    private boolean favourited;

    public RowItem(String category, int id, String iconURL,
                   String name, String designation, String specialty, String description,
                   int buildingNumber, int streetNumber, String streetName,
                   String city, String state, String country, String postalCode,
                   String notes, double latitude, double longitude, double distance, boolean favourited){
        this.category=category;
        this.id=id;
        this.iconURL=iconURL;
        this.name=name;
        this.designation=designation;
        this.specialty=specialty;
        this.description=description;
        this.buildingNumber=buildingNumber;
        this.streetNumber=streetNumber;
        this.streetName=streetName;
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
        id = in.readInt();
        iconURL = in.readString();
        name = in.readString();
        designation = in.readString();
        specialty = in.readString();
        description = in.readString();
        buildingNumber = in.readInt();
        streetNumber = in.readInt();
        streetName = in.readString();
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
        dest.writeInt(id);
        dest.writeString(iconURL);
        dest.writeString(name);
        dest.writeString(designation);
        dest.writeString(specialty);
        dest.writeString(description);
        dest.writeInt(buildingNumber);
        dest.writeInt(streetNumber);
        dest.writeString(streetName);
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIconURL() {
        return iconURL;
    }

    public void setIconURL(String iconURL) {
        this.iconURL = iconURL;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getBuildingNumber() {
        return buildingNumber;
    }

    public void setBuildingNumber(int buildingNumber) {
        this.buildingNumber = buildingNumber;
    }

    public int getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(int streetNumber) {
        this.streetNumber = streetNumber;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
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
}
