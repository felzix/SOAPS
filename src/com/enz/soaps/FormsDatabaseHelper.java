package com.enz.soaps;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FormsDatabaseHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "formstable.db";
	private static final int DATABASE_VERSION = 5;

	public FormsDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		FormsTable.onCreate(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		FormsTable.onUpgrade(db, oldVersion, newVersion);
	}
}
