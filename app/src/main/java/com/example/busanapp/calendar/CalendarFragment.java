package com.example.busanapp.calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.busanapp.R;

public class CalendarFragment extends Fragment {
    private View view;

    CustomCalendarView customCalendarView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_calendar, container, false);

        customCalendarView = (CustomCalendarView) view.findViewById(R.id.custom_calendar_view);

        return view;
    }
}