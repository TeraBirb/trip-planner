package com.example.d308_mobile_application_development_android.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.example.d308_mobile_application_development_android.Database.Repository;
import com.example.d308_mobile_application_development_android.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ExcursionDetails extends AppCompatActivity {

    int excursionID;
    int vacationID;
    String excursionTitle;
    String excursionDate;
    EditText editTitle;
    EditText editDate;
    Repository repository;
    DatePickerDialog.OnDateSetListener dateListener;
    Calendar myCalendar;
    SimpleDateFormat sdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excursion_details);

        repository = new Repository(getApplication());
        excursionID = getIntent().getIntExtra("id", -1);
        excursionTitle = getIntent().getStringExtra("name");
        excursionDate = getIntent().getStringExtra("date");
        vacationID = getIntent().getIntExtra("vacID", -1);

        editTitle = findViewById(R.id.editTextExcursionTitle);
        editDate = findViewById(R.id.editTextExcursionDate);

        editTitle.setText(excursionTitle);
        editDate.setText(excursionDate);

        sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);
    }
}