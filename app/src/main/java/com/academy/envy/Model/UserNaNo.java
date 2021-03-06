package com.academy.envy.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class UserNaNo implements Comparable  {
    private String number;
    private String userName;
    private String uID;
    private int state;

    public UserNaNo(String number, String userName, String uID, int state) {
        this.number = number;
        this.userName = userName;
        this.state = state;
        this.uID = uID;
    }



//    protected UserNaNo(Parcel in) {
//        number = in.readString();
//        userName = in.readString();
//        state = in.readInt();
//    }

//    public static final Creator<UserNaNo> CREATOR = new Creator<UserNaNo>() {
//        @Override
//        public UserNaNo createFromParcel(Parcel in) {
//            return new UserNaNo(in);
//        }
//
//        @Override
//        public UserNaNo[] newArray(int size) {
//            return new UserNaNo[size];
//        }
//    };

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getuID() { return uID; }


    public int getState() { return state; }

    public void setState(int state) { this.state = state; }

    @Override
    public int compareTo(Object o) {
        int st = ((UserNaNo)o).getState();
        return st;
    }

//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel parcel, int i) {
//        parcel.writeString(number);
//        parcel.writeString(userName);
//        parcel.writeInt(state);
//    }
}
