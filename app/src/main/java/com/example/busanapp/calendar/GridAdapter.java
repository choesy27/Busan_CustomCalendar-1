package com.example.busanapp.calendar;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.busanapp.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class GridAdapter extends ArrayAdapter {
    LayoutInflater inflater;
    Calendar currentDate;
    List<Date> dates;
    List<Events> events;

    public GridAdapter(@NonNull Context context, List<Date> dates, Calendar currentDate, List<Events> events) {
        super(context, R.layout.single_cell_layout);

        this.dates = dates;
        this.currentDate = currentDate;
        this.events = events;

        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Date monthDate = dates.get(position);
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime(monthDate);

        int DayNo = dateCalendar.get(Calendar.DAY_OF_MONTH);
        int displayMonth = dateCalendar.get(Calendar.MONTH) + 1;
        int displayYear = dateCalendar.get(Calendar.YEAR);
        int currentMonth = currentDate.get(Calendar.MONTH) + 1;
        int currentYear = currentDate.get(Calendar.YEAR);

        int year = 2020;
        int month = 5;
        int displayDay = dateCalendar.get(Calendar.DATE);
        int currentDay = currentDate.get(Calendar.DATE);

        View view = convertView;

        // inflate item if it does not exist yet
        if (view == null) {
            view = inflater.inflate(R.layout.single_cell_layout, parent, false);
        }

        TextView Day_Number = view.findViewById(R.id.calendar_day);

        // 글자색 변경
        if (displayMonth == currentMonth && displayYear == currentYear) {

            if (position % Calendar.DAY_OF_WEEK == 0) {
                Day_Number.setTextColor(Color.parseColor("red"));
            } else if (position % Calendar.DAY_OF_WEEK == 6) {
                Day_Number.setTextColor(Color.parseColor("blue"));
            } else {
                Day_Number.setTextColor(Color.parseColor("black"));
            }

        } else {

            if (position % Calendar.DAY_OF_WEEK == 0) {
                Day_Number.setTextColor(Color.parseColor("#FFCCCB"));
            } else if (position % Calendar.DAY_OF_WEEK == 6) {
                Day_Number.setTextColor(Color.parseColor("#80BEC5FF"));
            } else {
                Day_Number.setTextColor(Color.parseColor("#66BDBDBD"));
            }

            // 터치 이벤트 막기
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        }

        // 오늘 날짜 표시하기
        if (year == currentYear && month == currentMonth && displayDay == currentDay) {
//          view.setBackgroundColor(getContext().getResources().getColor(R.color.CurrentBackground));
            Day_Number.setTypeface(null, Typeface.BOLD);
            Day_Number.setTextColor(Color.parseColor("#bd71ff"));
        }

        TextView EventNumber = view.findViewById(R.id.events_id);

        Day_Number.setText(String.valueOf(DayNo));

        Calendar eventCalendar = Calendar.getInstance();
        ArrayList<String> arrayList = new ArrayList<>();

        return view;
    }

    @Override
    public int getCount() {
        return dates.size();
    }

    @Override
    public int getPosition(@Nullable Object item) {
        return dates.indexOf(item);
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return dates.get(position);
    }
}