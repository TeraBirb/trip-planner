package com.example.d308_mobile_application_development_android.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.d308_mobile_application_development_android.Database.Repository;
import com.example.d308_mobile_application_development_android.Entities.Excursion;
import com.example.d308_mobile_application_development_android.Entities.Vacation;
import com.example.d308_mobile_application_development_android.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class VacationDetails extends AppCompatActivity {

    int vacationID;
    String vacationTitle;
    String accomomdationName;

    String startDate;

    String endDate;

    EditText editTitle;
    EditText editAccommodation;
    TextView editStartDate;
    TextView editEndDate;

    Repository repository;
    DatePickerDialog.OnDateSetListener startDateListener;
    DatePickerDialog.OnDateSetListener endDateListener;
    Calendar myCalendarStart;
    Calendar myCalendarEnd;

    SimpleDateFormat sdf;

    Vacation currentVacation;
    int numExursions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacation_details);

        String myFormat = "MM/dd/yy";
        sdf = new SimpleDateFormat(myFormat, Locale.US);
        myCalendarStart = Calendar.getInstance();
        myCalendarEnd = Calendar.getInstance();

        vacationTitle = getIntent().getStringExtra("name");
        editTitle = findViewById(R.id.editTextVacationTitle);
        editTitle.setText(vacationTitle);

        accomomdationName = getIntent().getStringExtra("staying at");
        editAccommodation = findViewById(R.id.editTextStayingAt);
        editAccommodation.setText(accomomdationName);

        vacationID = getIntent().getIntExtra("id", -1);

        // BE SUSPICIOSO !!!!!!!!!!!!!!!!!!!!!!!

        startDate = getIntent().getStringExtra("start date");
        editStartDate = findViewById(R.id.editTextStartDate);
        editStartDate.setText(startDate);

        endDate = getIntent().getStringExtra("end date");
        editEndDate = findViewById(R.id.editTextEndDate);
        editEndDate.setText(endDate);

        setUpStartDatePickerListener();
        setUpEndDatePickerListener();


//        RecyclerView recyclerView = findViewById(R.id.recyclerViewExcursions);
        repository = new Repository(getApplication());


        Button buttonAddExcursion = findViewById(R.id.buttonAddExcursion);
        buttonAddExcursion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VacationDetails.this, ExcursionDetails.class);
                startActivity(intent);
            }
        });

        Button buttonSaveVacation = findViewById(R.id.buttonSaveVacation);
        buttonSaveVacation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vacation vacation;

                String title = editTitle.getText().toString();
                String accommodation = editAccommodation.getText().toString();
                String startDateString = editStartDate.getText().toString();
                String endDateString = editEndDate.getText().toString();

                if (title.equals("") || startDateString.equals("") || endDateString.equals("")) {
                    Toast.makeText(VacationDetails.this, "Title, Start Date, and End Date are required.", Toast.LENGTH_LONG).show();
                    return;
                }

                // New vacation
                if (vacationID == -1) {
                    if (repository.getAllVacations().size() == 0)
                        vacationID = 1;
                    else
                        vacationID = repository.getAllVacations().get(repository.getAllVacations().size() - 1).getVacationID() + 1;
                    vacation = new Vacation(vacationID, title, accommodation, startDateString, endDateString);
                    repository.insert(vacation);
                }
                // Existing vacation
                else {
                    vacation = new Vacation(vacationID, title, accommodation, startDateString, endDateString);
                    repository.update(vacation);
                }

                Intent intent = new Intent(VacationDetails.this, VacationList.class);
                startActivity(intent);
            }
        });

        Button buttonDeleteVacation = findViewById(R.id.buttonDeleteVacation);
        buttonDeleteVacation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Vacation vac : repository.getAllVacations()) {
                    if (vac.getVacationID() == vacationID) currentVacation = vac;
                }

                numExursions = 0;
                for (Excursion excursion : repository.getAllExcursions()) {
                    if (excursion.getVacationID() == vacationID) ++numExursions;
                }

                if (numExursions == 0) {
                    repository.delete(currentVacation);
                    Toast.makeText(VacationDetails.this, currentVacation.getVacationTitle() + " was deleted.", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(VacationDetails.this, "Please delete this vacation's excursions first.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void setUpStartDatePickerListener() {
        startDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendarStart.set(Calendar.YEAR, year);
                myCalendarStart.set(Calendar.MONTH, month);
                myCalendarStart.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateStartDateLabel();
            }
        };

        editStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(startDateListener, myCalendarStart);
            }
        });
    }

    private void setUpEndDatePickerListener() {
        endDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendarEnd.set(Calendar.YEAR, year);
                myCalendarEnd.set(Calendar.MONTH, month);
                myCalendarEnd.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateEndDateLabel();
            }
        };

        editEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(endDateListener, myCalendarEnd);
            }
        });
    }

    private void showDatePickerDialog(DatePickerDialog.OnDateSetListener dateListener, Calendar calendar) {
        new DatePickerDialog(VacationDetails.this, dateListener,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateStartDateLabel() {
        editStartDate.setText(sdf.format(myCalendarStart.getTime()));
    }

    private void updateEndDateLabel() {
        editEndDate.setText(sdf.format(myCalendarEnd.getTime()));
    }
}