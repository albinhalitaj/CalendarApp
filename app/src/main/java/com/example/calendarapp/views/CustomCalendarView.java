package com.example.calendarapp.views;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calendarapp.R;
import com.example.calendarapp.adapters.EventRecyclerAdapter;
import com.example.calendarapp.adapters.GridAdapter;
import com.example.calendarapp.adapters.RecyclerAdapter;
import com.example.calendarapp.data.DbOpenHelper;
import com.example.calendarapp.data.DbStructure;
import com.example.calendarapp.models.Events;
import com.example.calendarapp.utilities.AlarmReceiver;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class CustomCalendarView extends LinearLayout {

    ImageButton nextButton,previousButton;
    TextView currentDate;
    GridView gridView;
    private static final int MAX_CALENDAR_DAYS = 42;
    Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
    Context context;
    DbOpenHelper dbOpenHelper;

    Dialog alertDialog;

    SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy",Locale.ENGLISH);
    SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM",Locale.ENGLISH);
    SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy",Locale.ENGLISH);
    SimpleDateFormat eventDateFormat = new SimpleDateFormat("yyyy-MM-dd",Locale.ENGLISH);

    GridAdapter gridAdapter;

    List<Date> dates = new ArrayList<>();
    List<Events> eventsList = new ArrayList<>();
    List<Events> allEventsList = new ArrayList<>();

    int alarmYear,alarmMonth,alarmDay,alarmHour,alarmMinute;

    public CustomCalendarView(Context context) {
        super(context);
    }

    @SuppressLint("ResourceAsColor")
    public CustomCalendarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        this.context = context;
        InitializeLayout();
        SetUpCalendar();
        DisplayAllEvents();

        previousButton.setOnClickListener(v -> {
            calendar.add(Calendar.MONTH,-1);
            SetUpCalendar();
        });

        nextButton.setOnClickListener(v -> {
            calendar.add(Calendar.MONTH,1);
            SetUpCalendar();
        });

        gridView.setOnItemClickListener((parent, view, position, id) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setCancelable(true);
            View addView = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_newevent_layout,null);
            EditText eventName = addView.findViewById(R.id.eventname);
            TextView eventTime = addView.findViewById(R.id.eventtime);
            ImageButton setTime = addView.findViewById(R.id.seteventtime);
            Button addEvent = addView.findViewById(R.id.addevent);

            CheckBox alarmMe = addView.findViewById(R.id.alarme);
            Calendar dateCalendar = Calendar.getInstance();
            dateCalendar.setTime(dates.get(position));
            alarmYear = dateCalendar.get(Calendar.YEAR);
            alarmMonth = dateCalendar.get(Calendar.MONTH);
            alarmDay = dateCalendar.get(Calendar.DAY_OF_MONTH);


            setTime.setOnClickListener(v -> {
                Calendar calendar = Calendar.getInstance();
                int hrs = calendar.get(Calendar.HOUR_OF_DAY);
                int minutes = calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(addView.getContext(), R.style.Theme_AppCompat_Dialog,
                        (view1, hourOfDay, minute) -> {
                            Calendar c = Calendar.getInstance();
                            c.set(Calendar.HOUR_OF_DAY,hourOfDay);
                            c.set(Calendar.MINUTE,minute);
                            c.setTimeZone(TimeZone.getDefault());
                            SimpleDateFormat hformate = new SimpleDateFormat("K:mm a",Locale.ENGLISH);
                            String EventTime = hformate.format(c.getTime());
                            eventTime.setText(EventTime);
                            alarmHour = c.get(Calendar.HOUR_OF_DAY);
                            alarmMinute = c.get(Calendar.MINUTE);
                        },hrs,minutes,false);
                timePickerDialog.show();
            });

            String date = eventDateFormat.format(dates.get(position));
            String month = monthFormat.format(dates.get(position));
            String year = yearFormat.format(dates.get(position));

            addEvent.setOnClickListener(v -> {
                if (alarmMe.isChecked()) {
                    SaveEvent(eventName.getText().toString(),eventTime.getText().toString(),date,month,year,"on");
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(alarmYear,alarmMonth,alarmDay,alarmHour,alarmMinute);
                    setAlarm(calendar,eventName.getText().toString(),eventTime.getText().toString(),
                            getRequestCode(date,eventName.getText().toString(),eventTime.getText().toString()));
                } else {
                    SaveEvent(eventName.getText().toString(),eventTime.getText().toString(),date,month,year,"off");
                }
                SetUpCalendar();
                DisplayAllEvents();
                alertDialog.dismiss();
            });
            builder.setView(addView);
            alertDialog = builder.create();
            alertDialog.show();
        });

        gridView.setOnItemLongClickListener((parent, view, position, id) -> {
            String date = eventDateFormat.format(dates.get(position));

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setCancelable(true);
            View showView = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_events_layout,null);
            RecyclerView recyclerView = showView.findViewById(R.id.EventsRV);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(showView.getContext());
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setHasFixedSize(true);

            EventRecyclerAdapter eventRecyclerAdapter = new EventRecyclerAdapter(showView.getContext(),CollectEventsPerDate(date));
            recyclerView.setAdapter(eventRecyclerAdapter);
            eventRecyclerAdapter.notifyDataSetChanged();

            builder.setView(showView);
            alertDialog = builder.create();
            if (CollectEventsPerDate(date).size() > 0){
                alertDialog.show();
            } else {
                Toast.makeText(context,"No Events at this date",Toast.LENGTH_SHORT).show();
            }

            alertDialog.setOnCancelListener(dialog -> {
                SetUpCalendar();
                DisplayAllEvents();
            });

            return true;
        });
    }

    public CustomCalendarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void setAlarm(Calendar calendar,String event,String time,int requestCode){
        Intent intent = new Intent(context.getApplicationContext(), AlarmReceiver.class);
        intent.putExtra("event",event);
        intent.putExtra("time",time);
        intent.putExtra("id",requestCode);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,requestCode,intent,PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager)context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
    }

    private int getRequestCode(String date,String event,String time){
        int code = 0;
        dbOpenHelper = new DbOpenHelper(context);
        SQLiteDatabase database = dbOpenHelper.getReadableDatabase();
        Cursor cursor = dbOpenHelper.ReadIDEvents(date,event,time,database);
        while (cursor.moveToNext()){
            code = cursor.getInt(cursor.getColumnIndex(DbStructure.ID));
        }
        cursor.close();
        dbOpenHelper.close();
        return code;
    }


    private void InitializeLayout() {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.calendar_layout,this);
        nextButton = view.findViewById(R.id.nextBtn);
        previousButton = view.findViewById(R.id.previousBtn);
        currentDate = view.findViewById(R.id.currentDate);
        gridView = view.findViewById(R.id.gridview);
    }

    private void SetUpCalendar() {
        String currentDate = dateFormat.format(calendar.getTime());
        this.currentDate.setText(currentDate);
        dates.clear();
        Calendar monthCalendar = (Calendar) calendar.clone();
        monthCalendar.set(Calendar.DAY_OF_MONTH,1);
        int firstDayofMonth = monthCalendar.get(Calendar.DAY_OF_WEEK) - 1;
        monthCalendar.add(Calendar.DAY_OF_MONTH,-firstDayofMonth);

        CollectEventsPerMonth(monthFormat.format(calendar.getTime()),yearFormat.format(calendar.getTime()));

        while (dates.size() < MAX_CALENDAR_DAYS){
            dates.add(monthCalendar.getTime());
            monthCalendar.add(Calendar.DAY_OF_MONTH,1);
        }

        gridAdapter = new GridAdapter(context,dates,calendar,eventsList);
        gridView.setAdapter(gridAdapter);
    }

    private void CollectEventsPerMonth(String month,String year) {
        eventsList.clear();
        dbOpenHelper = new DbOpenHelper(context);
        SQLiteDatabase database = dbOpenHelper.getReadableDatabase();
        Cursor cursor = dbOpenHelper.ReadEventsPerMonth(month,year,database);
        while (cursor.moveToNext()){
            String Event = cursor.getString(cursor.getColumnIndex(DbStructure.EVENT));
            String time = cursor.getString(cursor.getColumnIndex(DbStructure.TIME));
            String date = cursor.getString(cursor.getColumnIndex(DbStructure.DATE));
            String Month = cursor.getString(cursor.getColumnIndex(DbStructure.MONTH));
            String Year = cursor.getString(cursor.getColumnIndex(DbStructure.YEAR));
            Events event = new Events(Event,time,date,Month,Year);
            eventsList.add(event);
        }
        cursor.close();
        dbOpenHelper.close();
    }

    private ArrayList<Events> CollectEventsPerDate(String date) {
        ArrayList<Events> events = new ArrayList<>();
        dbOpenHelper = new DbOpenHelper(context);
        SQLiteDatabase database = dbOpenHelper.getReadableDatabase();
        Cursor cursor = dbOpenHelper.ReadEvents(date,database);
        while (cursor.moveToNext()){
            String Event = cursor.getString(cursor.getColumnIndex(DbStructure.EVENT));
            String time = cursor.getString(cursor.getColumnIndex(DbStructure.TIME));
            String Date = cursor.getString(cursor.getColumnIndex(DbStructure.DATE));
            String Month = cursor.getString(cursor.getColumnIndex(DbStructure.MONTH));
            String Year = cursor.getString(cursor.getColumnIndex(DbStructure.YEAR));
            Events event = new Events(Event,time,Date,Month,Year);
            events.add(event);
        }
        cursor.close();
        dbOpenHelper.close();
        return events;
    }

    private void SaveEvent(String event,String time,String date,String month,String year,String notify) {
        dbOpenHelper = new DbOpenHelper(context);
        SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
        dbOpenHelper.SaveEvent(event,time,date,month,year,notify,database);
        dbOpenHelper.close();
        Toast.makeText(context,"Event Saved",Toast.LENGTH_SHORT).show();
    }

    private void DisplayAllEvents(){
        GetAllEvents();
        RecyclerView recyclerView = findViewById(R.id.allEventsRv);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        RecyclerAdapter adapter = new RecyclerAdapter(context,allEventsList);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemViewCacheSize(allEventsList.size());
        adapter.notifyDataSetChanged();
    }


    private void GetAllEvents() {
        allEventsList.clear();
        dbOpenHelper = new DbOpenHelper(context);
        SQLiteDatabase database = dbOpenHelper.getReadableDatabase();
        Cursor cursor = dbOpenHelper.ReadAllEvents(database);
        while (cursor.moveToNext()){
            String Event = cursor.getString(cursor.getColumnIndex(DbStructure.EVENT));
            String time = cursor.getString(cursor.getColumnIndex(DbStructure.TIME));
            String date = cursor.getString(cursor.getColumnIndex(DbStructure.DATE));
            String Month = cursor.getString(cursor.getColumnIndex(DbStructure.MONTH));
            String Year = cursor.getString(cursor.getColumnIndex(DbStructure.YEAR));
            Events event = new Events(Event,time,date,Month,Year);
            allEventsList.add(event);
        }
        cursor.close();
        dbOpenHelper.close();
    }
}
