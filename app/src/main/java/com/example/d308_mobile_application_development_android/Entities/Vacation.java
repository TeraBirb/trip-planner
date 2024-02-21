package com.example.d308_mobile_application_development_android.Entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "vacations")
public class Vacation {

    @PrimaryKey(autoGenerate = true)
    private int vacationID;

    private String vacationTitle;
    private String accommodationName;
    private String startDate; // requires type conversion
    private String endDate; // requires type conversion

    public Vacation(int vacationID, String vacationTitle, String accommodationName, String startDate, String endDate) {
        this.vacationID = vacationID;
        this.vacationTitle = vacationTitle;
        this.accommodationName = accommodationName;
        this.startDate = startDate;
        this.endDate = endDate;
    }


    public int getVacationID() {
        return vacationID;
    }

    public String getVacationTitle() {
        return vacationTitle;
    }

    public void setVacationTitle(String vacationTitle) {
        this.vacationTitle = vacationTitle;
    }

    public String getAccommodationName() {
        return accommodationName;
    }

    public void setAccommodationName(String accommodationName) {
        this.accommodationName = accommodationName;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
