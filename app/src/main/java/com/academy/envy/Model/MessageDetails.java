package com.academy.envy.Model;

//import com.google.firebase.Timestamp;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;


//import java.sql.Timestamp;

//import com.google.type.Date;

public class MessageDetails {
    private String senderID;
    private String senderName;
    private String msg;



    @ServerTimestamp
    private Date dateSent;

    public MessageDetails(String senderID, String senderName, String msg) {
        this.senderID = senderID;
        this.senderName = senderName;
        this.msg = msg;
    }

    public MessageDetails() {
    }

    public Date getDateSent() { return dateSent; }

    public void setDateSent(Date dateSent) { this.dateSent = dateSent; }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
