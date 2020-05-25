package com.example.busanapp.calendar.memo;

import android.provider.BaseColumns;

public class MemoContract {
    /* 인스턴스화 금지 */
    private MemoContract() {

    }

    public static class MemoEntry implements BaseColumns {
        public static final String TABLE_NAME = "memo";
        public static final String DATE = "date";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_CONTENTS = "contents";
    }
}