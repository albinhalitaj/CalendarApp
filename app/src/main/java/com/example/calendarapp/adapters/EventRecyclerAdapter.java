package com.example.calendarapp.adapters;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calendarapp.R;
import com.example.calendarapp.data.DbOpenHelper;
import com.example.calendarapp.data.DbStructure;
import com.example.calendarapp.models.Events;
import com.example.calendarapp.utilities.AlarmReceiver;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EventRecyclerAdapter extends RecyclerView.Adapter<EventRecyclerAdapter.ViewHolder> {

    Context context;
    ArrayList<Events> events;
    DbOpenHelper dbOpenHelper;

    public EventRecyclerAdapter(Context context, ArrayList<Events> events) {
        this.context = context;
        this.events = events;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_events_rowlayout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  EventRecyclerAdapter.ViewHolder holder, int position) {
        final Events events = this.events.get(position);
        holder.event.setText(events.getEVENT());
        holder.dateTxt.setText(events.getDATE());
        holder.time.setText(events.getTIME());

        holder.deleteBtn.setOnClickListener(v -> {
            DeleteCalendarEvent(events.getEVENT(),events.getDATE(),events.getTIME());
            this.events.remove(position);
            notifyDataSetChanged();
        });

        if (isAlarmed(events.getDATE(),events.getEVENT(),events.getTIME())){
            holder.setAlarm.setImageResource(R.drawable.ic_action_notification_on);
        }else{
            holder.setAlarm.setImageResource(R.drawable.ic_action_notification_off);
        }

        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime(ConvertStringToDate(events.getDATE()));
        Calendar timeCalendar = Calendar.getInstance();
        int alarmYear = dateCalendar.get(Calendar.YEAR);
        int alarmMonth = dateCalendar.get(Calendar.MONTH);
        int alarmDay = dateCalendar.get(Calendar.DAY_OF_MONTH);
        timeCalendar.setTime(ConvertStringToTime(events.getTIME()));
        int alarmHour = timeCalendar.get(Calendar.HOUR_OF_DAY);
        int alarmMinute = timeCalendar.get(Calendar.MINUTE);

        holder.setAlarm.setOnClickListener(v -> {
            if (isAlarmed(events.getDATE(),events.getEVENT(),events.getTIME())){
                holder.setAlarm.setImageResource(R.drawable.ic_action_notification_off);
                cancelAlarm(getRequestCode(events.getDATE(),events.getEVENT(),events.getTIME()));
                updateEvent(events.getDATE(),events.getEVENT(),events.getTIME(),"off");
            }else {
                holder.setAlarm.setImageResource(R.drawable.ic_action_notification_on);
                Calendar alarmCalendar = Calendar.getInstance();
                alarmCalendar.set(alarmYear,alarmMonth,alarmDay,alarmHour,alarmMinute);
                setAlarm(alarmCalendar,events.getEVENT(),events.getTIME(),getRequestCode(events.getDATE(),events.getEVENT(),events.getTIME()));
                updateEvent(events.getDATE(),events.getEVENT(),events.getTIME(),"on");
            }
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView dateTxt,event,time;
        Button deleteBtn;
        ImageButton setAlarm;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTxt = itemView.findViewById(R.id.eventdate);
            event = itemView.findViewById(R.id.eventname);
            time = itemView.findViewById(R.id.eventtime);
            deleteBtn = itemView.findViewById(R.id.deletevent);
            setAlarm = itemView.findViewById(R.id.alarmeBtn);
        }
    }

    private Date ConvertStringToDate(String eventDate){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date date = null;
        try {
            date = format.parse(eventDate);
        }catch (ParseException e){
            e.printStackTrace();
        }
        return date;
    }

    private Date ConvertStringToTime(String eventDate){
        SimpleDateFormat format = new SimpleDateFormat("kk:mm", Locale.ENGLISH);
        Date date = null;
        try {
            date = format.parse(eventDate);
        }catch (ParseException e){
            e.printStackTrace();
        }
        return date;
    }

    private void DeleteCalendarEvent(String event, String date, String time){
        dbOpenHelper = new DbOpenHelper(context);
        SQLiteDatabase dataBase = dbOpenHelper.getWritableDatabase();
        dbOpenHelper.DeleteEvent(event,date,time,dataBase);
        dbOpenHelper.close();
        Toast.makeText(context,"Deleted successfully",Toast.LENGTH_SHORT).show();
    }

    private boolean isAlarmed(String date, String event, String time){
        boolean alarmed = false;
        dbOpenHelper = new DbOpenHelper(context);
        SQLiteDatabase database = dbOpenHelper.getReadableDatabase();
        Cursor cursor = dbOpenHelper.ReadIDEvents(date,event,time,database);
        while (cursor.moveToNext()){
            String notify = cursor.getString(cursor.getColumnIndex(DbStructure.NOTIFY));
            if (notify.equals("on")){
                alarmed = true;
            }else {
                alarmed = false;
            }
        }
        cursor.close();
        dbOpenHelper.close();
        return alarmed;
    }

    private void setAlarm(Calendar calendar, String event, String time, int requestCode){
        Intent intent = new Intent(context.getApplicationContext(),AlarmReceiver.class);
        intent.putExtra("event",event);
        intent.putExtra("time",time);
        intent.putExtra("id",requestCode);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,requestCode,intent,PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager)context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
    }

    private void cancelAlarm(int requestCode){
        Intent intent = new Intent(context.getApplicationContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,requestCode,intent,PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager)context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
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

    private void updateEvent(String date,String event,String time,String notify){
        dbOpenHelper = new DbOpenHelper(context);
        SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
        dbOpenHelper.UpdateEvent(date,event,time,notify,database);
        dbOpenHelper.close();
    }
}
