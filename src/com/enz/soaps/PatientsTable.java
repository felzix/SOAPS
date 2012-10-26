package com.enz.soaps;

import android.database.sqlite.SQLiteDatabase;

public class PatientsTable {

	public static final String TABLE_PATIENTS = "patient";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NAME = "name";

	private static final String DATABASE_CREATE = "create table "
			+ TABLE_PATIENTS + "("
			+ COLUMN_ID	+ " integer primary key autoincrement, "
			+ COLUMN_NAME + " text not null);";

	public static void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		// TODO do something smarter here or total data loss :(
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PATIENTS);
		onCreate(db);
	}
}
