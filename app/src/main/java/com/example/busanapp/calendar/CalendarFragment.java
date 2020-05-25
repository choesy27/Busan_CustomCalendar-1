package com.example.busanapp.calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.busanapp.R;
import com.example.busanapp.calendar.decorators.EventDecorator;
import com.example.busanapp.calendar.decorators.OneDayDecorator;
import com.example.busanapp.calendar.decorators.SaturdayDecorator;
import com.example.busanapp.calendar.decorators.SundayDecorator;
import com.example.busanapp.calendar.memo.MemoActivity;
import com.example.busanapp.calendar.memo.MemoContract;
import com.example.busanapp.calendar.memo.MemoDBHelper;
import com.example.busanapp.calendar.memo.TextAdapter;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

public class CalendarFragment extends Fragment {
    private View view;

    private final OneDayDecorator oneDayDecorator = new OneDayDecorator();
    MaterialCalendarView materialCalendarView;
    //  String time, kcal, menu;
    //  Cursor cursor;

    public static final int REQUEST_CODE_INSERT = 1000;

    private SimpleDateFormat mFormat = new SimpleDateFormat("yyyy/M/d");
    private TextView mTextDate;
    private String mTime;
    private ArrayList<String> list;

    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView recyclerView;

    private AlertDialog.Builder builder;

    private TextAdapter textAdapter;
    private MemoDBHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_calendar, container, false);

        init();

        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setAdapter(textAdapter);

        getMemoCursor();

        materialCalendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.from(2000, 0, 1))       // 달력의 시작
                .setMaximumDate(CalendarDay.from(2030, 11, 31))     // 달력의 끝
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        materialCalendarView.addDecorators(new SundayDecorator(), new SaturdayDecorator(), oneDayDecorator);

        String[] result = {"2017,03,18", "2017,04,18", "2017,05,18", "2017,06,18"};

        new ApiSimulator(result).executeOnExecutor(Executors.newSingleThreadExecutor());

        materialCalendarView.setOnDateChangedListener((widget, date, selected) -> {
            int Year = date.getYear();
            int Month = date.getMonth() + 1;
            int Day = date.getDay();

            String shot_Day = Year + "/" + Month + "/" + Day;

            getMemoCursor();
            mTextDate.setText(shot_Day);
        });

        // 메모 클릭 이벤트(수정)
        textAdapter.setOnItemClickListener((TextAdapter.OnItemClickListener) (v, pos) -> {
            Intent intent = new Intent(getActivity(), MemoActivity.class);
            intent.putExtra("SelectedDate", mTime);

            String[] params = {mTime};

            Cursor cursor = (Cursor) dbHelper.getReadableDatabase().query(MemoContract.MemoEntry.TABLE_NAME, null, "date=?", params, null, null, null);
            cursor.moveToPosition(pos);

            int id = cursor.getInt(cursor.getColumnIndexOrThrow(MemoContract.MemoEntry._ID));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(MemoContract.MemoEntry.COLUMN_NAME_TITLE));
            String contents = cursor.getString(cursor.getColumnIndexOrThrow(MemoContract.MemoEntry.COLUMN_NAME_CONTENTS));

            intent.putExtra("id", id);
            intent.putExtra("title", title);
            intent.putExtra("contents", contents);

            startActivityForResult(intent, REQUEST_CODE_INSERT);
        });

        /* Memo Delete */
        textAdapter.setOnItemLongClickListener((TextAdapter.OnItemLongClickListener) (v, pos) -> {
            String[] params = {mTime};

            Cursor cursor = (Cursor) dbHelper.getReadableDatabase().query(MemoContract.MemoEntry.TABLE_NAME, null, "date=?", params, null, null, null);
            cursor.moveToPosition(pos);

            final int id = cursor.getInt(cursor.getColumnIndexOrThrow(MemoContract.MemoEntry._ID));

            builder.setTitle("메모 삭제");
            builder.setMessage("메모를 삭제하시겠습니까?");
            builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    int deletedCount = db.delete(MemoContract.MemoEntry.TABLE_NAME, MemoContract.MemoEntry._ID + "=" + id, null);

                    if (deletedCount == 0) {
                        Toast.makeText(getActivity(), "삭제 실패", Toast.LENGTH_SHORT).show();
                    } else {
                        getMemoCursor();
                        Toast.makeText(getActivity(), "메모가 삭제되었습니다", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            builder.setNegativeButton("취소", null);
            builder.show();
        });

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.add_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.item_add) {
            Intent intent = new Intent(getActivity(), MemoActivity.class);
            intent.putExtra("SelectedDate", mTime);

            // error
            startActivityForResult(intent, REQUEST_CODE_INSERT);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class ApiSimulator extends AsyncTask<Void, Void, List<CalendarDay>> {
        String[] Time_Result;

        ApiSimulator(String[] Time_Result) {
            this.Time_Result = Time_Result;
        }

        @Override
        protected List<CalendarDay> doInBackground(@NonNull Void... voids) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Calendar calendar = Calendar.getInstance();
            ArrayList<CalendarDay> dates = new ArrayList<>();

            /* 특정날짜 달력에 점표시 해주는 곳 */
            /* 월은 0이 1월. 년,일은 그대로 */
            // string 문자열인 Time_Result을 받아와서 ,를 기준으로 자르고 string을 int로 변환
            for (int i = 0; i < Time_Result.length; i++) {
                CalendarDay day = CalendarDay.from(calendar);
                String[] time = Time_Result[i].split(",");
                int year = Integer.parseInt(time[0]);
                int month = Integer.parseInt(time[1]);
                int dayy = Integer.parseInt(time[2]);

                dates.add(day);
                calendar.set(year, month - 1, dayy);
            }
            return dates;
        }

        @Override
        protected void onPostExecute(@NonNull List<CalendarDay> calendarDays) {
            super.onPostExecute(calendarDays);

            if (getActivity().isFinishing()) {
                return;
            }

            materialCalendarView.addDecorator(new EventDecorator(R.color.colorSelectedDate, calendarDays, getActivity()));
        }
    }

    private void init() {
        list = new ArrayList<>();
        builder = new AlertDialog.Builder(getActivity());
        textAdapter = new TextAdapter(list);
        mTextDate = (TextView) view.findViewById(R.id.selectDate);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerMemo);

        materialCalendarView = (MaterialCalendarView) view.findViewById(R.id.calendarView);

        /* Today Date */
        Date date = new Date();
        mTime = mFormat.format(date);
        mTextDate.setText(mTime);

        dbHelper = MemoDBHelper.getInstance(getActivity());
    }

    // 커서의 데이터를 Arraylist에 저장하는 메서드
    private void getMemoCursor() {
        String[] params = {mTime};
        list.clear();

        Cursor cursor = dbHelper.getReadableDatabase().query(MemoContract.MemoEntry.TABLE_NAME, null, "date=?", params, null, null, null);

        while (cursor.moveToNext()) {
            list.add(cursor.getString(cursor.getColumnIndex(MemoContract.MemoEntry.COLUMN_NAME_TITLE)));
        }

        textAdapter.notifyDataSetChanged();
        show();
    }

    // 일정 추가 후 갱신
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_INSERT) {
            getMemoCursor();
        }
    }

    public void show() {
        dbHelper = MemoDBHelper.getInstance(getActivity());

        String[] params = {mTime};

        Cursor cursor = (Cursor) dbHelper.getReadableDatabase().query(MemoContract.MemoEntry.TABLE_NAME, null, "date=?", params, null, null, null);
        cursor.moveToFirst();

        Intent intent = new Intent(getActivity(), CalendarFragment.class);
    }
}