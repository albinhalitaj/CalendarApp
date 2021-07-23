package com.example.calendarapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calendarapp.R;
import com.example.calendarapp.models.Events;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    List<Events> events;
    Context context;
    int dayNo;
    FrameLayout.LayoutParams  eventParams;
    FrameLayout.LayoutParams  timeParams;

    public RecyclerAdapter(Context context,List<Events> events) {
        this.events = events;
        this.context = context;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.allevents_layout,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.ViewHolder holder, int position) {
        final Events events = this.events.get(position);
        String dayNo = events.getDATE().substring(events.getDATE().length() - 2);
        String date = events.getDATE();
        int year = Integer.parseInt(events.getYEAR());
        int month = Integer.parseInt(date.substring(5,date.length() - 3));
        int day = Integer.parseInt(dayNo);
        DayOfWeek dow = LocalDate.of(year,month,day).getDayOfWeek();
        String output = String.valueOf(dow);
        SimpleDateFormat sdf = new SimpleDateFormat("dd");
        String todayDate = sdf.format(new Date());
        if (day == Integer.parseInt(todayDate)){
            holder.circle.setBackground(ContextCompat.getDrawable(context,R.drawable.filled_circle));
            holder.day.setTextColor(Color.WHITE);
            holder.date.setTextColor(Color.WHITE);
        }
        if (day == this.dayNo){
            holder.event.setText("    " + events.getEVENT());
            holder.time.setText(events.getTIME());
            holder.day.setVisibility(View.INVISIBLE);
            holder.date.setVisibility(View.INVISIBLE);
            holder.circle.setVisibility(View.INVISIBLE);
            holder.month.setVisibility(View.INVISIBLE);
            eventParams = (FrameLayout.LayoutParams) holder.event.getLayoutParams();
            timeParams = (FrameLayout.LayoutParams) holder.time.getLayoutParams();
            eventParams.setMargins(eventParams.leftMargin,0,eventParams.rightMargin,eventParams.bottomMargin);
            timeParams.setMargins(timeParams.leftMargin,90,timeParams.rightMargin,timeParams.bottomMargin);
            holder.event.setLayoutParams(eventParams);
            holder.time.setLayoutParams(timeParams);
        } else {
            holder.event.setText("    " + events.getEVENT());
            holder.time.setText(events.getTIME());
            holder.month.setText(events.getMONTH());
            holder.day.setText(output.substring(0,3));
            holder.date.setText(dayNo);
            holder.day.setVisibility(View.VISIBLE);
            holder.date.setVisibility(View.VISIBLE);
            holder.circle.setVisibility(View.VISIBLE);
        }

        this.dayNo = day;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView date,day,event,month,time;
        ImageView circle;
        MaterialCardView card;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            day = itemView.findViewById(R.id.day);
            event = itemView.findViewById(R.id.eventname);
            circle = itemView.findViewById(R.id.circle);
            month = itemView.findViewById(R.id.month);
            time = itemView.findViewById(R.id.time);
            card = itemView.findViewById(R.id.card);
        }
    }
}
