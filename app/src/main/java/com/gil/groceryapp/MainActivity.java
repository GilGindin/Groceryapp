package com.gil.groceryapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private SQLiteDatabase mDatabase;
    private AdpterGrocery mAdpterGrocery;
    private EditText edittext_name;
    private TextView textview_amount;
    private int mAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GroceryDbHelper dbHelper = new GroceryDbHelper(this);
        mDatabase = dbHelper.getWritableDatabase();
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdpterGrocery = new AdpterGrocery(this, getAllItems());
        recyclerView.setAdapter(mAdpterGrocery);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                removeItem((Long) viewHolder.itemView.getTag());
            }
        }).attachToRecyclerView(recyclerView);

        edittext_name = findViewById(R.id.edittext_name);
        textview_amount = findViewById(R.id.textview_amount);

        Button button_decrease = findViewById(R.id.button_decrease);
        Button button_increase = findViewById(R.id.button_increase);
        Button button_add = findViewById(R.id.button_add);

        button_decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decreae();
            }
        });

        button_increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increase();
            }
        });

        button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }
        });
    }

    private void decreae() {
        if (mAmount > 0) {
            mAmount--;
            textview_amount.setText(String.valueOf(mAmount));
        }
    }

    private void increase() {
        mAmount++;
        textview_amount.setText(String.valueOf(mAmount));
    }

    private void addItem() {

        if (edittext_name.getText().toString().trim().length() == 0 || mAmount == 0) {
            return;
        }
        String name = edittext_name.getText().toString();
        ContentValues cv = new ContentValues();
        cv.put(GroceryContract.GroceryEntry.COLUMN_NAME, name);
        cv.put(GroceryContract.GroceryEntry.COLUMN_AMOUNT, mAmount);

        mDatabase.insert(GroceryContract.GroceryEntry.TABLE_NAME, null, cv);
        mAdpterGrocery.swapCursor(getAllItems());
        edittext_name.getText().clear();
    }

    private void removeItem(Long id) {
        mDatabase.delete(GroceryContract.GroceryEntry.TABLE_NAME, GroceryContract.GroceryEntry._ID + "=" + id, null);
        mAdpterGrocery.swapCursor(getAllItems());
    }

    private Cursor getAllItems() {
        return mDatabase.query(GroceryContract.GroceryEntry.TABLE_NAME, null, null, null, null, null, GroceryContract.GroceryEntry.COLUMN_TIMESTAMP + " DESC");
    }
}
