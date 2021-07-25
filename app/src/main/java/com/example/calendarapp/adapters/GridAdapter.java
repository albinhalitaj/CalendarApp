package com.example.calendarapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.calendarapp.R;
import com.example.calendarapp.models.Events;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GridAdapter extends ArrayAdapter {

    List<Date> dates;
    Calendar currentDate;
    List<Events> events;
    LayoutInflater inflater;

    public GridAdapter(@NonNull Context context, List<Date> dates, Calendar currentDate, List<Events> events) {
        super(context, R.layout.single_cell_layout);

        this.dates = dates;
        this.currentDate = currentDate;
        this.events = events;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return dates.size();
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return dates.get(position);
    }

    @Override
    public int getPosition(@Nullable Object item) {
        return dates.indexOf(item);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Date monthDate = dates.get(position);
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime(monthDate);
        int dayNo = dateCalendar.get(Calendar.DAY_OF_MONTH);
        int displayMonth = dateCalendar.get(Calendar.MONTH) + 1;
        int displayYear = dateCalendar.get(Calendar.YEAR);
        int currentMonth = currentDate.get(Calendar.MONTH) + 1;
        int currentYear = currentDate.get(Calendar.YEAR);

        View view = convertView;
        if (view == null){
            view = inflater.inflate(R.layout.single_cell_layout,parent,false);
        }

        TextView dayNumber = view.findViewById(R.id.calendar_day);
        ImageView eventNumber = view.findViewById(R.id.events_id);
        dayNumber.setText(String.valueOf(dayNo));

        if(displayMonth == currentMonth && displayYear == currentYear){
            dayNumber.setTextColor(ContextCompat.getColor(getContext(),R.color.blue));
        }
        else {
            dayNumber.setTextColor(Color.parseColor("#cccccc"));
        }

        int todaysDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        int month = Calendar.getInstance().get(Calendar.MONTH);
        if (Integer.parseInt(dayNumber.getText().toString()) == todaysDay && month == displayMonth - 1){
            dayNumber.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.filled_circle));
            dayNumber.setTextColor(Color.WHITE);
            dayNumber.setTextSize(13);
        }

        Calendar eventCalendar = Calendar.getInstance();
        ArrayList<String> arrayList = new ArrayList<>();
        for (int i = 0;i < events.size();i++) {
            eventCalendar.setTime(ConvertStringToDate(events.get(i).getDATE()));
            if (dayNo == eventCalendar.get(Calendar.DAY_OF_MONTH) && displayMonth == eventCalendar.get(Calendar.MONTH)+1
                    && displayYear == eventCalendar.get(Calendar.YEAR)){
                arrayList.add(events.get(i).getEVENT());
                eventNumber.setVisibility(View.VISIBLE);
                //eventNumber.setText(events.get(i).getEVENT());
                //eventNumber.setTextColor(Color.parseColor("#515355"));
            }
        }
        return view;
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
}
