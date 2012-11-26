package com.enz.soaps;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.content.ContentUris;
import android.net.Uri;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.text.TextUtils;

public class SOAPSContentProvider extends ContentProvider {
	private PatientsDatabaseHelper patientsDB;
	private FormsDatabaseHelper formsDB;

	private static final String AUTHORITY = "com.enz.soaps.provider";

	private static final int PATIENTS = 10;
	private static final int PATIENT_ID = 20;
	private static final int FORMS = 30;
	private static final int FORM_ID = 40;

	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

	public static final String CONTENT_PATIENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
			+ "/patients";
	public static final String CONTENT_PATIENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
			+ "/patient";
	public static final String CONTENT_FORM_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
			+ "/forms";
	public static final String CONTENT_FORM_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
			+ "/form";

	private static final UriMatcher sURIMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(AUTHORITY, "patients", PATIENTS);
		sURIMatcher.addURI(AUTHORITY, "patients/#", PATIENT_ID);
		sURIMatcher.addURI(AUTHORITY, "forms", FORMS);
		sURIMatcher.addURI(AUTHORITY, "forms/#", FORM_ID);
	}

	public SOAPSContentProvider() {
	}

	@Override
	synchronized public boolean onCreate() {
		patientsDB = new PatientsDatabaseHelper(getContext());
		formsDB = new FormsDatabaseHelper(getContext());
		return true;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	synchronized public int delete(Uri uri, String selection,
			String[] selectionArgs) {
		int uriType = sURIMatcher.match(uri);
		if (uriType == PATIENTS || uriType == PATIENT_ID) {
			return deletePatient(uriType, uri, selection, selectionArgs);
		} else if (uriType == FORMS || uriType == FORM_ID) {
			return deleteForm(uriType, uri, selection, selectionArgs);
		} else {
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
	}

	private int deletePatient(int uriType, Uri uri, String selection,
			String[] selectionArgs) {
		SQLiteDatabase sqlDB = patientsDB.getWritableDatabase();
		int rowsDeleted = 0;
		switch (uriType) {
		case PATIENTS:
			rowsDeleted = sqlDB.delete(PatientsTable.TABLE_PATIENTS, selection,
					selectionArgs);
			break;
		case PATIENT_ID:
			String id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = sqlDB.delete(PatientsTable.TABLE_PATIENTS,
						PatientsTable.COLUMN_ID + "=" + id, null);
			} else {
				rowsDeleted = sqlDB.delete(PatientsTable.TABLE_PATIENTS,
						PatientsTable.COLUMN_ID + "=" + id + " and "
								+ selection, selectionArgs);
			}
			break;
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsDeleted;
	}

	private int deleteForm(int uriType, Uri uri, String selection,
			String[] selectionArgs) {
		SQLiteDatabase sqlDB = formsDB.getWritableDatabase();
		int rowsDeleted = 0;
		switch (uriType) {
		case FORMS:
			rowsDeleted = sqlDB.delete(FormsTable.TABLE_FORMS, selection,
					selectionArgs);
			break;
		case FORM_ID:
			String id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = sqlDB.delete(FormsTable.TABLE_FORMS,
						FormsTable.COLUMN_ID + "=" + id, null);
			} else {
				rowsDeleted = sqlDB.delete(FormsTable.TABLE_FORMS,
						FormsTable.COLUMN_ID + "=" + id + " and " + selection,
						selectionArgs);
			}
			break;
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsDeleted;
	}

	@Override
	synchronized public Uri insert(Uri uri, ContentValues values) {
		int uriType = sURIMatcher.match(uri);
		if (uriType == PATIENTS) {
			return insertPatient(uri, values);
		} else if (uriType == FORMS) {
			return insertForm(uri, values);
		} else {
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
	}

	private Uri insertPatient(Uri uri, ContentValues values) {
		SQLiteDatabase sqlDB = patientsDB.getWritableDatabase();
		long id = sqlDB.insert(PatientsTable.TABLE_PATIENTS, null, values);
		uri = ContentUris.withAppendedId(uri, id);
		getContext().getContentResolver().notifyChange(uri, null);
		return uri;
	}

	private Uri insertForm(Uri uri, ContentValues values) {
		SQLiteDatabase sqlDB = formsDB.getWritableDatabase();
		long id = sqlDB.insert(FormsTable.TABLE_FORMS, null, values);
		uri = ContentUris.withAppendedId(uri, id);
		getContext().getContentResolver().notifyChange(uri, null);
		return uri;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		int uriType = sURIMatcher.match(uri);

		if (uriType == PATIENTS || uriType == PATIENT_ID) {
			return queryPatients(uriType, uri, projection, selection,
					selectionArgs, sortOrder);
		} else if (uriType == FORMS || uriType == FORM_ID) {
			return queryForms(uriType, uri, projection, selection,
					selectionArgs, sortOrder);
		} else {
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
	}

	private Cursor queryPatients(int uriType, Uri uri, String[] projection,
			String selection, String[] selectionArgs, String sortOrder) {

		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(PatientsTable.TABLE_PATIENTS);

		if (uriType == PATIENT_ID) {
			// Adding the ID to the original query
			queryBuilder.appendWhere(PatientsTable.COLUMN_ID + "="
					+ uri.getLastPathSegment());
		}

		if (sortOrder == null || sortOrder == "") {
			sortOrder = PatientsTable.COLUMN_NAME;
		}

		SQLiteDatabase db = patientsDB.getWritableDatabase();
		Cursor cursor = queryBuilder.query(db, projection, selection,
				selectionArgs, null, null, sortOrder);
		// Make sure that potential listeners are getting notified
		cursor.setNotificationUri(getContext().getContentResolver(), uri);

		return cursor;
	}

	private Cursor queryForms(int uriType, Uri uri, String[] projection,
			String selection, String[] selectionArgs, String sortOrder) {

		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(FormsTable.TABLE_FORMS);

		if (uriType == FORM_ID) {
			// Adding the ID to the original query
			queryBuilder.appendWhere(FormsTable.COLUMN_ID + "="
					+ uri.getLastPathSegment());
		}

		if (sortOrder == null || sortOrder == "") {
			sortOrder = FormsTable.COLUMN_DATE;
		}

		SQLiteDatabase db = formsDB.getWritableDatabase();
		Cursor cursor = queryBuilder.query(db, projection, selection,
				selectionArgs, null, null, sortOrder);
		// Make sure that potential listeners are getting notified
		cursor.setNotificationUri(getContext().getContentResolver(), uri);

		return cursor;
	}

	@Override
	synchronized public int update(Uri uri, ContentValues values,
			String selection, String[] selectionArgs) {
		// TODO: Implement this to handle requests to update one or more rows.
		
		int uriType = sURIMatcher.match(uri);

		if (uriType == PATIENTS || uriType == PATIENT_ID) {
			return updatePatients(uriType, uri, values, selection,
					selectionArgs);
		} else if (uriType == FORMS || uriType == FORM_ID) {
			return updateForms(uriType, uri, values, selection,
					selectionArgs);
		} else {
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
	}
	
	private Cursor updateForms(int uriType, Uri uri, ContentValues values,
			String selection, String[] selectionArgs) {

		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(FormsTable.TABLE_FORMS);

		if (uriType == FORM_ID) {
			// Adding the ID to the original query
			queryBuilder.appendWhere(FormsTable.COLUMN_ID + "="
					+ uri.getLastPathSegment());
		}

		SQLiteDatabase db = formsDB.getWritableDatabase();
		Cursor cursor = queryBuilder.query(db, projection, selection,
				selectionArgs, null, null, sortOrder);
		// Make sure that potential listeners are getting notified
		cursor.setNotificationUri(getContext().getContentResolver(), uri);

		return cursor;
	}
}
