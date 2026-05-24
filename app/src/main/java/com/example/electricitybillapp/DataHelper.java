package com.example.electricitybillapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "electricity.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "bills";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_MONTH = "month";
    public static final String COLUMN_UNIT = "unit";
    public static final String COLUMN_TOTAL = "total_charges";
    public static final String COLUMN_REBATE = "rebate";
    public static final String COLUMN_FINAL = "final_cost";

    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_MONTH + " TEXT, " +
                    COLUMN_UNIT + " INTEGER, " +
                    COLUMN_TOTAL + " REAL, " +
                    COLUMN_REBATE + " REAL, " +
                    COLUMN_FINAL + " REAL" + ")";

    public DataHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long insertBill(String month, int unit, double total, double rebate, double finalCost) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MONTH, month);
        values.put(COLUMN_UNIT, unit);
        values.put(COLUMN_TOTAL, total);
        values.put(COLUMN_REBATE, rebate);
        values.put(COLUMN_FINAL, finalCost);
        return db.insert(TABLE_NAME, null, values);
    }

    public Cursor getAllBills() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_ID + " DESC", null);
    }

    public Cursor getBillById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public int updateBill(int id, String month, int unit, double total, double rebate, double finalCost) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MONTH, month);
        values.put(COLUMN_UNIT, unit);
        values.put(COLUMN_TOTAL, total);
        values.put(COLUMN_REBATE, rebate);
        values.put(COLUMN_FINAL, finalCost);
        return db.update(TABLE_NAME, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public int deleteBill(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }
}
