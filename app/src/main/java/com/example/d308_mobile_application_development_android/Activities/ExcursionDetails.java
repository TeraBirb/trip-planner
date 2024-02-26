package com.example.d308_mobile_application_development_android.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.d308_mobile_application_development_android.Database.Repository;
import com.example.d308_mobile_application_development_android.Entities.Excursion;
import com.example.d308_mobile_application_development_android.Entities.Vacation;
import com.example.d308_mobile_application_development_android.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ExcursionDetails extends AppCompatActivity {

    int excursionID;

    Excursion currentExcursion;
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

        // Set up data to populate if clicked on an existing excursion
        // If new excursion, edit fields will be blank
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
        myCalendar = Calendar.getInstance();
        setUpDatePickerListener();

        // Set up Vacation ID list to match with corresponding Excursions
        ArrayList<Vacation> vacationArrayList = new ArrayList<>();
        vacationArrayList.addAll(repository.getAllVacations());
        ArrayList<Integer> vacationIdList = new ArrayList<>();
        for (Vacation vacation : vacationArrayList) {
            vacationIdList.add(vacation.getVacationID());
        }

        // Save (add or update) Excursion Button

        Button buttonSaveExcursion = findViewById(R.id.buttonSaveExcursion);
        buttonSaveExcursion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Excursion excursion;

                // adding a new excursion
                if (excursionID == -1) {
                    // if there are no excursions yet in the database
                    if (repository.getAllExcursions().size() == 0) {
                        excursionID = 1;
                    }
                    else {
                        int lastExcursionIndex = repository.getAllExcursions().size() - 1;
                        excursionID = repository.getAllExcursions().get(lastExcursionIndex).getExcursionID() + 1;
                    }
                    excursion = new Excursion(excursionID, editTitle.getText().toString(), editDate.getText().toString(), vacationID);
                    repository.insert(excursion);
                    Toast.makeText(ExcursionDetails.this, editTitle.getText().toString() + " was added.", Toast.LENGTH_LONG).show();
                }
                // updating existing excursion
                else {
                    excursion = new Excursion(excursionID, editTitle.getText().toString(), editDate.getText().toString(), vacationID);
                    repository.update(excursion);
                    Toast.makeText(ExcursionDetails.this, editTitle.getText().toString() + " was updated.", Toast.LENGTH_LONG).show();
                }
                finish();
            }
        });

        // Delete Excursion Button

        Button buttonDeleteExcursion = findViewById(R.id.buttonDeleteExcursion);
        buttonDeleteExcursion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Excursion e : repository.getAllExcursions()) {
                    if (e.getExcursionID() == excursionID) currentExcursion = e;
                }
                repository.delete(currentExcursion);
                Toast.makeText(ExcursionDetails.this, currentExcursion.getExcursionTitle() + " was deleted.", Toast.LENGTH_LONG).show();
                finish();
            }
        });


//        ArrayAdapter<Integer> vacationIdAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, vacationIdList);
//        Spinner spinner = findViewById(R.id.spinnerExcursionDetails);



    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // if top-left back (or "up) button pressed
            // Populating parent vacation details page
            Intent intent = new Intent(this, VacationDetails.class);
            intent.putExtra("id", vacationID);

            for (Vacation v : repository.getAllVacations()) {
                if (v.getVacationID() == vacationID) {
                    intent.putExtra("name", v.getVacationTitle());
                    intent.putExtra("staying at", v.getAccommodationName());
                    intent.putExtra("start date", v.getStartDate());
                    intent.putExtra("end date", v.getEndDate());
                }
            }
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        // if bottom-left back button pressed
        // Populating parent vacation details page
        super.onBackPressed();
        Intent intent = new Intent(ExcursionDetails.this, VacationDetails.class);
        intent.putExtra("id", vacationID);
        for (Vacation v : repository.getAllVacations()) {
            if (v.getVacationID() == vacationID) {
                intent.putExtra("name", v.getVacationTitle());
                intent.putExtra("staying at", v.getAccommodationName());
                intent.putExtra("start date", v.getStartDate());
                intent.putExtra("end date", v.getEndDate());
            }
        }
        startActivity(intent);
        finish();
    }

    private void setUpDatePickerListener() {
        dateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                editDate.setText(sdf.format(myCalendar.getTime()));
            }
        };

        editDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ExcursionDetails.this, dateListener,
                        myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

}