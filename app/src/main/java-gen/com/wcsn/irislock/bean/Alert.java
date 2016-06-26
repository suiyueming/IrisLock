package com.wcsn.irislock.bean;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "ALERT".
 */
public class Alert {

    private Long id;
    private int type;
    /** Not-null value. */
    private String info;
    /** Not-null value. */
    private String time;
    /** Not-null value. */
    private String week;
    private String image;

    public Alert() {
    }

    public Alert(Long id) {
        this.id = id;
    }

    public Alert(Long id, int type, String info, String time, String week, String image) {
        this.id = id;
        this.type = type;
        this.info = info;
        this.time = time;
        this.week = week;
        this.image = image;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    /** Not-null value. */
    public String getInfo() {
        return info;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setInfo(String info) {
        this.info = info;
    }

    /** Not-null value. */
    public String getTime() {
        return time;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setTime(String time) {
        this.time = time;
    }

    /** Not-null value. */
    public String getWeek() {
        return week;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setWeek(String week) {
        this.week = week;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}