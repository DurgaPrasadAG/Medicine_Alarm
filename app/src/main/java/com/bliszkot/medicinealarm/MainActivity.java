package com.bliszkot.medicinealarm;

import static android.content.ContentValues.TAG;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private FloatingActionButton fab;
    SwipeRefreshLayout swipeRefreshLayout;
    Boolean refreshed;
    List<String> itemIds = new ArrayList<>();
    RecyclerView recyclerView;
    String medicineName, date, time, requestCode;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fab = findViewById(R.id.floatingActionButton);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        fab.setOnClickListener(v -> {
            Intent intent = new Intent(this, AlarmActivity.class);
            startActivity(intent);
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        showEmptyCard();

        swipeRefreshLayout.setOnRefreshListener(() -> {
            fetchFromDb();
            if (itemIds.isEmpty()) showEmptyCard();
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    private void fetchFromDb() {
        if(!itemIds.isEmpty()) {
            itemIds.clear();
        }
        DbHelper dbHelper = new DbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] projection = {
                BaseColumns._ID,
                DbHelper.FeedEntry.COLUMN_NAME_MEDICINE_NAME,
                DbHelper.FeedEntry.COLUMN_NAME_DATE,
                DbHelper.FeedEntry.COLUMN_NAME_TIME,
                DbHelper.FeedEntry.COLUMN_NAME_RES_ID,
        };

// How you want the results sorted in the resulting Cursor
        String sortOrder = DbHelper.FeedEntry.COLUMN_NAME_DATE + " DESC";

        Cursor cursor = db.query(
                DbHelper.FeedEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );


        while(cursor.moveToNext()) {
            medicineName = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.FeedEntry.COLUMN_NAME_MEDICINE_NAME));
            itemIds.add(medicineName);
            date = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.FeedEntry.COLUMN_NAME_DATE));
            itemIds.add(date);
            time = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.FeedEntry.COLUMN_NAME_TIME));
            itemIds.add(time);
            requestCode = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.FeedEntry.COLUMN_NAME_RES_ID));
            itemIds.add(requestCode);
        }
        //[1, 2, 3, 4 | 5, 6, 7, 8 | 9, 10, 11, 12]
        int size = itemIds.size() / 4;
        // size = 3
        MedicineData[] medicineData1 = new MedicineData[size];
        for (int i = 0, j = 0; i < size; i++, j+=4) {
            medicineData1[i] = new MedicineData(
                    itemIds.get(j),itemIds.get(j+1),itemIds.get(j+2), itemIds.get(j+3)
            );
        }
        MedicineAdapter medicineAdapter1 = new MedicineAdapter(medicineData1,MainActivity.this);
        recyclerView.setAdapter(medicineAdapter1);

        cursor.close();
    }

    void showEmptyCard() {
        MedicineData[] medicineData = new MedicineData[]{
                new MedicineData("No Alarms.","If an alarm is set, then swipe down to refresh alarm list.\nLong click on this card to delete or stop an alarm.","", ""),
        };

        MedicineAdapter medicineAdapter = new MedicineAdapter(medicineData,MainActivity.this);
        recyclerView.setAdapter(medicineAdapter);
    }
}