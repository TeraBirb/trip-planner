package com.example.trip_planner.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.trip_planner.Database.Repository;
import com.example.trip_planner.Entities.Excursion;
import com.example.trip_planner.Entities.Vacation;
import com.example.trip_planner.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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
    List<Vacation> vacationList;
    List<Excursion> excursionList;

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
        excursionList = new ArrayList<Excursion>();

        // Retrieve all excursions
        repository.getAllExcursions().observe(this, new Observer<List<Excursion>>() {
            @Override
            public void onChanged(List<Excursion> excursions) {
                excursionList = excursions;
            }
        });

        editTitle = findViewById(R.id.editTextExcursionTitle);
        editDate = findViewById(R.id.editTextExcursionDate);

        editTitle.setText(excursionTitle);
        editDate.setText(excursionDate);

        sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);
        myCalendar = Calendar.getInstance();
        setUpDatePickerListener();

        // Observe vacation data
        repository.getAllVacations().observe(this, new Observer<List<Vacation>>() {
            @Override
            public void onChanged(List<Vacation> vacations) {
                vacationList = vacations;
                ArrayList<Integer> vacationIdList = new ArrayList<>();
                for (Vacation vacation : vacations) {
                    vacationIdList.add(vacation.getVacationID());
                }
            }
        });

        // Save (add or update) Excursion Button
        Button buttonSaveExcursion = findViewById(R.id.buttonSaveExcursion);
        buttonSaveExcursion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!dataInputValidation()) return;

                Excursion excursion;

                // adding a new excursion
                if (excursionID == -1) {
                    // if there are no excursions yet in the database
                    if (excursionList.size() == 0) {
                        excursionID = 1;
                    }
                    else {
                        int lastExcursionIndex = excursionList.size() - 1;
                        excursionID = excursionList.get(lastExcursionIndex).getExcursionID() + 1;
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
                if (excursionID == -1) {
                    finish();
                    return;
                }

                for (Excursion e : excursionList) {
                    if (e.getExcursionID() == excursionID) {
                        currentExcursion = e;
                        break;
                    }
                }

                if (currentExcursion != null) {
                    repository.delete(currentExcursion);
                    Toast.makeText(ExcursionDetails.this, currentExcursion.getExcursionTitle() + " was deleted.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ExcursionDetails.this, "Excursion not found.", Toast.LENGTH_LONG).show();
                }
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_vacation_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // if top-left back (or "up) button pressed
            // Populating parent vacation details page
            Intent intent = new Intent(this, VacationDetails.class);
            intent.putExtra("id", vacationID);

            for (Vacation v : vacationList) {
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
        // Make sure you have Notifications Enabled in your device emulator settings.
        // Toast still works regardless.
        if (item.getItemId() == R.id.notify) {
            if (!dataInputValidation()) return false;
            Date myDate = null;

            try {
                myDate = sdf.parse(editDate.getText().toString());
            } catch (ParseException e) {
                Toast.makeText(ExcursionDetails.this, "Make sure you use the correct date format (mm/dd/yy)", Toast.LENGTH_LONG).show();
                return false;
            }

            try {
                Long triggerDate = myDate.getTime();

                Intent intent = new Intent(ExcursionDetails.this, MyReceiver.class);
                intent.putExtra("key", editTitle.getText().toString() + " is today!");
                PendingIntent sender = PendingIntent.getBroadcast(ExcursionDetails.this, ++MainActivity.numAlert, intent, PendingIntent.FLAG_IMMUTABLE);

                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerDate, sender);

                Toast.makeText(ExcursionDetails.this, "Notification set for " + editDate.getText().toString(), Toast.LENGTH_LONG).show();

            } catch (Exception e) {
                Toast.makeText(ExcursionDetails.this, "Something went wrong during notification setup. :(", Toast.LENGTH_LONG).show();
            }
        }

        if (item.getItemId() == R.id.share) {
            if (!dataInputValidation()) return false;
            Intent sendIntent = new Intent();
            sendIntent.setAction((Intent.ACTION_SEND));

            String sharedMessage = "Here are my excursion details. " + "\n" +
                    "Excursion: " + editTitle.getText().toString() + "\n" +
                    "Date: " + editDate.getText().toString();

            sendIntent.putExtra(Intent.EXTRA_TEXT, sharedMessage);
            sendIntent.putExtra(Intent.EXTRA_TITLE, editTitle.getText().toString());
            sendIntent.setType("text/plain");

            Intent shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);
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
        for (Vacation v : vacationList) {
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
    private boolean dataInputValidation() {

        String title = editTitle.getText().toString();
        String dateString = editDate.getText().toString();

        // All required fields validation
        if (title.equals("") || dateString.equals("") ) {
            Toast.makeText(ExcursionDetails.this, "Title and date are required.", Toast.LENGTH_LONG).show();
            return false;
        }

        // Correct date format for both fields validation
        sdf.setLenient(false);
        Date inputDate;
        try {
            inputDate = sdf.parse(dateString);
        } catch (ParseException e) {
            Toast.makeText(ExcursionDetails.this, "Make sure you use the correct date format (mm/dd/yy)", Toast.LENGTH_LONG).show();
            return false;
        }

        // Date falls within parent vacation date
        Date vacationStartDate = null;
        Date vacationEndDate = null;

        for (Vacation v : vacationList) {
            if (v.getVacationID() == vacationID) {
                try {
                    vacationStartDate = sdf.parse(v.getStartDate());
                    vacationEndDate = sdf.parse(v.getEndDate());
                } catch (ParseException e) {
                    Toast.makeText(ExcursionDetails.this, "Something went wrong during vacation date parsing. :(", Toast.LENGTH_LONG).show();
                }
            }
        }
        if (inputDate.before(vacationStartDate) || inputDate.after(vacationEndDate)) {
            Toast.makeText(ExcursionDetails.this, "The excursion date must fall within the vacation start and end dates.", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

}