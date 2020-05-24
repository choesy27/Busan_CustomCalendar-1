package com.example.busanapp.calendar;

import android.app.AlertDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.busanapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CustomCalendarView extends LinearLayout {
    // internal components
//  private LinearLayout header;
    private ImageView btnPrev;
    private ImageView btnNext;
    private TextView txtDate;
    private GridView grid;

    // 기본으로 6주 출력
    private static final int MAX_CALENDAR_DAYS = 42;

    // 캘린더
    Calendar calendar = Calendar.getInstance(Locale.KOREAN);
    Context context;
    SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.KOREAN);
    //  SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.KOREAN);
    //  SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.KOREAN);
    GridAdapter GridAdapter;            // 캘린더 날짜
    List<Date> dates = new ArrayList<>();
    List<Events> eventsList = new ArrayList<>();

    public CustomCalendarView(Context context) {
        super(context);
    }

    public CustomCalendarView(final Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        IntializeLayout();

        SetUPCalendar();

        // 저번 달
        btnPrev.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH, -1);
                SetUPCalendar();
            }
        });

        // 다음 달
        btnNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH, 1);
                SetUPCalendar();
            }
        });

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(true);
            }
        });
    }

    public CustomCalendarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void IntializeLayout() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.calendar_layout, this);

        btnNext = view.findViewById(R.id.calendar_next_button);
        btnPrev = view.findViewById(R.id.calendar_prev_button);
        txtDate = view.findViewById(R.id.calendar_current_month);
        grid = view.findViewById(R.id.calendar_grid);
    }

    private void SetUPCalendar() {
        String currentDate = dateFormat.format(calendar.getTime());
        txtDate.setText(currentDate);

        dates.clear();

        Calendar MonthCalendar = (Calendar) calendar.clone();

        // determine the cell for current month's beginning
        MonthCalendar.set(Calendar.DAY_OF_MONTH, 1);
        int FirstDayofMonth = MonthCalendar.get(Calendar.DAY_OF_WEEK) - 1;

        // move calendar backwards to the beginning of the week
        MonthCalendar.add(Calendar.DAY_OF_MONTH, -FirstDayofMonth);

        // fill cells (42 days calendar as per our business logic)
        while (dates.size() < MAX_CALENDAR_DAYS) {
            dates.add(MonthCalendar.getTime());
            MonthCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        GridAdapter = new GridAdapter(context, dates, calendar, eventsList);
        grid.setAdapter(GridAdapter);
    }
}