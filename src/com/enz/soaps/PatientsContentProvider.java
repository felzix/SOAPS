package com.enz.soaps;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.net.Uri;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import com.enz.soaps.sqlite.*;
import android.text.TextUtils;
import java.util.Arrays;
import java.util.HashSet;

public class PatientsContentProvider extends ContentProvider {
	private PatientsDatabaseHelper database;
	
	private static final int PATIENTS = 10;
	private static final int PATIENT_ID = 20;
	
	private static final String AUTHORITY = "com.enz.soaps.provider";
	
	private static final String BASE_PATH = "todos";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);
	
	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/patients";
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/patient";
	
	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(AUTHORITY, BASE_PATH, PATIENTS);
		sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", PATIENT_ID);
	}
	
    public PatientsContentProvider() {
    }

    @Override
    synchronized public int delete(Uri uri, String selection, String[] selectionArgs) {
    	int uriType = sURIMatcher.match(uri);
    	SQLiteDatabase sqlDB = database.getWritableDatabase();
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
    					PatientsTable.COLUMN_ID + "=" + id, 
    					null);
    		} else {
    			rowsDeleted = sqlDB.delete(PatientsTable.TABLE_PATIENTS,
    					PatientsTable.COLUMN_ID + "=" + id 
    					+ " and " + selection,
    					selectionArgs);
    		}
    		break;
    	default:
    		throw new IllegalArgumentException("Unknown URI: " + uri);
    	}
    	getContext().getContentResolver().notifyChange(uri, null);
    	return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
    	return null;
    }

    @Override
    synchronized public Uri insert(Uri uri, ContentValues values) {
    	int uriType = sURIMatcher.match(uri);
    	SQLiteDatabase sqlDB = database.getWritableDatabase();
    	long id = 0;
    	switch (uriType) {
    		case PATIENTS:
    			id = sqlDB.insert(PatientsTable.TABLE_PATIENTS, null, values);
    			break;
    		default:
    			throw new IllegalArgumentException("Unknown URI: " + uri);
    	}
    	getContext().getContentResolver().notifyChange(uri, null);
    	return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    synchronized public boolean onCreate() {
    	database = new PatientsDatabaseHelper(getContext());
    	return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
    	
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        // Check if the caller has requested a column which does not exists
        checkColumns(projection);

        // Set the table
        queryBuilder.setTables(PatientsTable.TABLE_PATIENTS);

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
        case PATIENTS:
          break;
        case PATIENT_ID:
          // Adding the ID to the original query
          queryBuilder.appendWhere(PatientsTable.COLUMN_ID + "="
              + uri.getLastPathSegment());
          break;
        default:
          throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection,
            selectionArgs, null, null, sortOrder);
        // Make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    synchronized public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
    private void checkColumns(String[] projection) {
        String[] available = { PatientsTable.COLUMN_NAME,
            PatientsTable.COLUMN_ENTRY_COUNT, PatientsTable.COLUMN_ID };
        if (projection != null) {
          HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
          HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
          // Check if all columns which are requested are available
          if (!availableColumns.containsAll(requestedColumns)) {
            throw new IllegalArgumentException("Unknown columns in projection");
          }
        }
      }
    
}
