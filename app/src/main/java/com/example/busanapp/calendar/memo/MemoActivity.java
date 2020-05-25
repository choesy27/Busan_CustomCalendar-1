package com.example.busanapp.calendar.memo;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.busanapp.R;

import java.util.Objects;

public class MemoActivity extends AppCompatActivity {
    private EditText mTitle;
    private EditText mContent;
    private String SeletedDate;
    private int mMemoID;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);

        mTitle = (EditText) findViewById(R.id.Title_Edit);
        mContent = (EditText) findViewById(R.id.Content_Edit);
        toolbar = (Toolbar) findViewById(R.id.memo_toolbar);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_icon);
        getSupportActionBar().setTitle("메모 작성");

        Intent intent = getIntent();
        if (intent != null) {
            SeletedDate = intent.getStringExtra("SelectedDate");
            //mDateText.setText(SeletedDate);

            mMemoID = intent.getIntExtra("id", -1);

            // 받아온 ID가 있으면 테이블 수정으로 간주하여 타이틀과 컨텐츠를 불러온다.
            if (mMemoID != -1) {
                String title = intent.getStringExtra("title");
                String contents = intent.getStringExtra("contents");

                mTitle.setText(title);
                mContent.setText(contents);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.item_save:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        String title = mTitle.getText().toString();
        String contents = mContent.getText().toString();

        /* 메모가 비어있는지 판단 */
        if (TextUtils.isEmpty(title) && TextUtils.isEmpty(contents)) {
            Toast.makeText(this, "입력된 내용이 없어 저장되지 않았습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 저장할 데이터를 contentValues에 저장
        ContentValues contentValues = new ContentValues();
        contentValues.put(MemoContract.MemoEntry.DATE, SeletedDate);                /* Save Selected Date */
        contentValues.put(MemoContract.MemoEntry.COLUMN_NAME_TITLE, title);         /* Save Title */
        contentValues.put(MemoContract.MemoEntry.COLUMN_NAME_CONTENTS, contents);   /* Save Content */

        // helper 인스턴스에서 쓰기가능한 데이터베이스를 가져옴
        SQLiteDatabase db = MemoDBHelper.getInstance(this).getWritableDatabase();

        if (mMemoID == -1) {
            long newRowId = db.insert(MemoContract.MemoEntry.TABLE_NAME, null, contentValues);
            if (newRowId == -1) {
                Toast.makeText(this, "저장 실패", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "메모가 저장되었습니다.", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
            }

        } else {        // 받아온 ID값이 있으므로 데이터를 업데이트(수정) 시킨다.
            int count = db.update(MemoContract.MemoEntry.TABLE_NAME, contentValues, MemoContract.MemoEntry._ID + "=" + mMemoID, null);

            if (count == 0) {
                Toast.makeText(this, "수정에 문제가 발생하였습니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "수정되었습니다.", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
            }
        }
        super.onBackPressed();
    }
}