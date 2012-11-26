package com.enz.soaps;

import android.database.sqlite.SQLiteDatabase;

public class FormsTable {

	public static final String TABLE_FORMS = "FORM";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_PATIENT_ID = "patient_id";
	public static final String COLUMN_DATE = "date";
	public static final String COLUMN_SUBJECTIVE = "subjective";
	public static final String COLUMN_OBJECTIVE = "objective";
	public static final String COLUMN_ASOMETHING = "asomething";
	public static final String COLUMN_PSOMETHING = "psomething";
	public static final String COLUMN_SSOMETHING = "ssomething";

	private static final String DATABASE_CREATE = "create table " + TABLE_FORMS
			+ "(" + COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_PATIENT_ID + " text not null, " + COLUMN_DATE
			+ " text not null, " + COLUMN_SUBJECTIVE + " text not null, "
			+ COLUMN_OBJECTIVE + " text not null, " + COLUMN_ASOMETHING
			+ " text not null, " + COLUMN_PSOMETHING + " text not null, "
			+ COLUMN_SSOMETHING + " text not null);";

	public static void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		// TODO do something smarter here or total data loss :(
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_FORMS);
		onCreate(db);
	}
}
