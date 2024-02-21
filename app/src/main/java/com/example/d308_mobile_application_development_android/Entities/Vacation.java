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
    private Long startDate; // requires type conversion
    private Long endDate; // requires type conversion

    public Vacation(int vacationID, String vacationTitle, String accommodationName, Long startDate, Long endDate) {
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

    public Long getStartDate() {
        return startDate;
    }

    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }

    public Long getEndDate() {
        return endDate;
    }

    public void setEndDate(Long endDate) {
        this.endDate = endDate;
    }
}
