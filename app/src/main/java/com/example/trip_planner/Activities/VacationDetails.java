package com.example.trip_planner.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.TextView;
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

public class VacationDetails extends AppCompatActivity {

    int vacationID;
    String vacationTitle;
    String accommodationName;

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
    int numExcursions;
    List<Excursion> filteredExcursions;

    private VacationViewModel vacationViewModel;
    private ExcursionAdapter excursionAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacation_details);
        repository = new Repository(getApplication());

        String myFormat = "MM/dd/yy";
        sdf = new SimpleDateFormat(myFormat, Locale.US);
        sdf.setLenient(false);
        myCalendarStart = Calendar.getInstance();
        myCalendarEnd = Calendar.getInstance();

        vacationTitle = getIntent().getStringExtra("name");
        editTitle = findViewById(R.id.editTextVacationTitle);
        editTitle.setText(vacationTitle);

        accommodationName = getIntent().getStringExtra("staying at");
        editAccommodation = findViewById(R.id.editTextStayingAt);
        editAccommodation.setText(accommodationName);

        vacationID = getIntent().getIntExtra("id", -1);

        startDate = getIntent().getStringExtra("start date");
        editStartDate = findViewById(R.id.editTextStartDate);
        editStartDate.setText(startDate);

        endDate = getIntent().getStringExtra("end date");
        editEndDate = findViewById(R.id.editTextEndDate);
        editEndDate.setText(endDate);

        setUpStartDatePickerListener();
        setUpEndDatePickerListener();

        filteredExcursions = new ArrayList<Excursion>();

        vacationViewModel = new ViewModelProvider(this, new VacationViewModelFactory(repository)).get(VacationViewModel.class);
        final int[] vacationID = {getIntent().getIntExtra("id", -1)};
        if (vacationID[0] != -1) {
            vacationViewModel.loadVacation(vacationID[0]);
            // Observe the vacation data
            vacationViewModel.getVacation().observe(this, vacation -> {
                if (vacation != null) {
                    editTitle.setText(vacation.getVacationTitle());
                    editAccommodation.setText(vacation.getAccommodationName());
                    editStartDate.setText(vacation.getStartDate());
                    editEndDate.setText(vacation.getEndDate());
                }
            });
        }

        // List of associated excursions
        RecyclerView recyclerView = findViewById(R.id.recyclerViewExcursions);
        final ExcursionAdapter excursionAdapter = new ExcursionAdapter(this);
        recyclerView.setAdapter(excursionAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        repository.getAllExcursions().observe(this, new Observer<List<Excursion>>() {
            @Override
            public void onChanged(List<Excursion> allExcursions) {
                filteredExcursions.clear();
                for (Excursion e : allExcursions) {
                    if (e.getVacationID() == vacationID[0]) {
                        filteredExcursions.add(e);
                    }
                }
                excursionAdapter.setExcursions(filteredExcursions);
            }
        });

        // Add excursion button
        Button buttonAddExcursion = findViewById(R.id.buttonAddExcursion);
        buttonAddExcursion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vacationID[0] == -1) {
                    Toast.makeText(VacationDetails.this, "Please save vacation before adding excursions", Toast.LENGTH_LONG).show();
                }
                else {
                    Intent intent = new Intent(VacationDetails.this, ExcursionDetails.class);
                    // so the excursion knows which vacation it belongs to
                    intent.putExtra("vacID", vacationID[0]);
                    startActivity(intent);
                }
            }
        });

        // Save Vacation button
        Button buttonSaveVacation = findViewById(R.id.buttonSaveVacation);
        buttonSaveVacation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Vacation[] vacation = new Vacation[1];

                String title = editTitle.getText().toString();
                String accommodation = editAccommodation.getText().toString();
                String startDateString = editStartDate.getText().toString();
                String endDateString = editEndDate.getText().toString();

                // all required fields, correct date formatting, start date before end date
                if (!dataInputValidation()) return;

                repository.getAllVacations().observe(VacationDetails.this, new Observer<List<Vacation>>() {
                    @Override
                    public void onChanged(List<Vacation> vacations) {
                        if (vacations != null) {
                            // New vacation
                            if (vacationID[0] == -1) {
                                if (vacations.isEmpty())
                                    vacationID[0] = 1;
                                else
                                    vacationID[0] = vacations.get(vacations.size() - 1).getVacationID() + 1;
                                vacation[0] = new Vacation(vacationID[0], title, accommodation, startDateString, endDateString);
                                repository.insert(vacation[0]);
                                Toast.makeText(VacationDetails.this, title + " was added.", Toast.LENGTH_LONG).show();
                            }

                            Intent intent = new Intent(VacationDetails.this, VacationList.class);
                            startActivity(intent);
                        }
                    }
                });
            }
        });


        // Delete Vacation button
        Button buttonDeleteVacation = findViewById(R.id.buttonDeleteVacation);
        buttonDeleteVacation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (vacationID[0] == -1) {
                    finish();
                    return;
                }

                repository.getAllVacations().observe(VacationDetails.this, new Observer<List<Vacation>>() {
                    @Override
                    public void onChanged(List<Vacation> vacations) {
                        if (vacations != null) {
                            for (Vacation vac : vacations) {
                                if (vac.getVacationID() == vacationID[0]) {
                                    currentVacation = vac;
                                    break;
                                }
                            }

                            numExcursions = 0;
                            for (Excursion excursion : repository.getAllExcursions().getValue()) {
                                if (excursion.getVacationID() == vacationID[0]) ++numExcursions;
                            }

                            if (numExcursions == 0) {
                                repository.delete(currentVacation);
                                Toast.makeText(VacationDetails.this, currentVacation.getVacationTitle() + " was deleted.", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(VacationDetails.this, VacationList.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(VacationDetails.this, "Please delete this vacation's excursions first.", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_vacation_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        // Make sure you have Notifications Enabled in your device emulator settings.
        // Toast still works regardless.
        if (item.getItemId() == R.id.notify) {
            if (!dataInputValidation()) return false;
            Date myDateStart = null;
            Date myDateEnd = null;

            try {
                myDateStart = sdf.parse(editStartDate.getText().toString());
                myDateEnd = sdf.parse(editEndDate.getText().toString());
            } catch (ParseException e) {
                Toast.makeText(VacationDetails.this, "Make sure you use the correct date format (mm/dd/yy)", Toast.LENGTH_LONG).show();
                return false;
            }

            try {
                Long triggerStart = myDateStart.getTime();
                Long triggerEnd = myDateEnd.getTime();

                Intent intentStart = new Intent(VacationDetails.this, MyReceiver.class);
                Intent intentEnd = new Intent(VacationDetails.this, MyReceiver.class);
                intentStart.putExtra("key", editTitle.getText().toString() + " is starting today!");
                intentEnd.putExtra("key", editTitle.getText().toString() + " is ending today.");
                PendingIntent senderStart = PendingIntent.getBroadcast(VacationDetails.this, ++MainActivity.numAlert, intentStart, PendingIntent.FLAG_IMMUTABLE);
                PendingIntent senderEnd = PendingIntent.getBroadcast(VacationDetails.this, ++MainActivity.numAlert, intentEnd, PendingIntent.FLAG_IMMUTABLE);

                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerStart, senderStart);
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerEnd, senderEnd);

                Toast.makeText(VacationDetails.this, "Notifications set for " +
                                editStartDate.getText().toString() + " and " + editEndDate.getText().toString(),
                        Toast.LENGTH_LONG).show();

            } catch (Exception e) {
                Toast.makeText(VacationDetails.this, "Something went wrong during notification setup. :(", Toast.LENGTH_LONG).show();
            }
        }

        if (item.getItemId() == R.id.share) {
            if (!dataInputValidation()) return false;
            Intent sendIntent = new Intent();
            sendIntent.setAction((Intent.ACTION_SEND));

            StringBuilder stringFilteredExcursions = new StringBuilder();

            for (Excursion e : filteredExcursions) {
                stringFilteredExcursions.append(e.getExcursionTitle()).append("\n");
            }

            String sharedMessage = "Here are my trip details. " + "\n" +
                    "Trip: " + editTitle.getText().toString() + "\n" +
                    "Staying at: " + editAccommodation.getText().toString() + "\n" +
                    "Starts on: " + editStartDate.getText().toString() + "\n" +
                    "Ends on: " + editEndDate.getText().toString() + "\n" +
                    "Excursions:" + "\n" + stringFilteredExcursions.toString();

            sendIntent.putExtra(Intent.EXTRA_TEXT, sharedMessage);
            sendIntent.putExtra(Intent.EXTRA_TITLE, editTitle.getText().toString());
            sendIntent.setType("text/plain");

            Intent shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // same code as onCreate, throws NullPointerException when put into a function
        RecyclerView recyclerView = findViewById(R.id.recyclerViewExcursions);
        final ExcursionAdapter excursionAdapter = new ExcursionAdapter(this);
        recyclerView.setAdapter(excursionAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        repository.getAllExcursions().observe(this, new Observer<List<Excursion>>() {
            @Override
            public void onChanged(List<Excursion> allExcursions) {
                filteredExcursions.clear();
                for (Excursion e : allExcursions) {
                    if (e.getVacationID() == vacationID) {
                        filteredExcursions.add(e);
                    }
                }
                excursionAdapter.setExcursions(filteredExcursions);
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

    private boolean dataInputValidation() {

        String title = editTitle.getText().toString();
        String startDateString = editStartDate.getText().toString();
        String endDateString = editEndDate.getText().toString();

        // All required fields validation
        if (title.equals("") || startDateString.equals("") || endDateString.equals("")) {
            Toast.makeText(VacationDetails.this, "Title, Start Date, and End Date are required.", Toast.LENGTH_LONG).show();
            return false;
        }

        // Correct date format for both fields validation
        Date compareDateStart;
        Date compareDateEnd;
        try {
            compareDateStart = sdf.parse(startDateString);
            compareDateEnd = sdf.parse(endDateString);
        } catch (ParseException e) {
            Toast.makeText(VacationDetails.this, "Make sure you use the correct date format (mm/dd/yy)", Toast.LENGTH_LONG).show();
            return false;
        }

        // Date end and start validation
        if (compareDateStart.compareTo(compareDateEnd) > 0) {
            Toast.makeText(VacationDetails.this, "End Date must be after the Start Date.", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}