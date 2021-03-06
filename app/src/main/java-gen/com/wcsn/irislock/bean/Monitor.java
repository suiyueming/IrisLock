package com.wcsn.irislock.bean;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "MONITOR".
 */
public class Monitor {

    private Long id;
    /** Not-null value. */
    private String name;
    /** Not-null value. */
    private String userType;
    private boolean isOut;
    /** Not-null value. */
    private String week;
    /** Not-null value. */
    private String image;
    /** Not-null value. */
    private String time;

    public Monitor() {
    }

    public Monitor(Long id) {
        this.id = id;
    }

    public Monitor(Long id, String name, String userType, boolean isOut, String week, String image, String time) {
        this.id = id;
        this.name = name;
        this.userType = userType;
        this.isOut = isOut;
        this.week = week;
        this.image = image;
        this.time = time;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /** Not-null value. */
    public String getName() {
        return name;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setName(String name) {
        this.name = name;
    }

    /** Not-null value. */
    public String getUserType() {
        return userType;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setUserType(String userType) {
        this.userType = userType;
    }

    public boolean getIsOut() {
        return isOut;
    }

    public void setIsOut(boolean isOut) {
        this.isOut = isOut;
    }

    /** Not-null value. */
    public String getWeek() {
        return week;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setWeek(String week) {
        this.week = week;
    }

    /** Not-null value. */
    public String getImage() {
        return image;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setImage(String image) {
        this.image = image;
    }

    /** Not-null value. */
    public String getTime() {
        return time;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setTime(String time) {
        this.time = time;
    }

}
