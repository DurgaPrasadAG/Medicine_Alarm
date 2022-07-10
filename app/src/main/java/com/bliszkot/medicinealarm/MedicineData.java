package com.bliszkot.medicinealarm;

public class MedicineData {
    String medicineName;
    String date;
    String time;
    String requestCode;


    public MedicineData(String medicineName, String date, String time, String requestCode) {
        this.medicineName = medicineName;
        this.date = date;
        this.time = time;
        this.requestCode = requestCode;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public String getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(String requestCode) {
        this.requestCode = requestCode;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
