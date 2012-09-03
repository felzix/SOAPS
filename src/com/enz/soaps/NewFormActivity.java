package com.enz.soaps;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.net.Uri;
import android.widget.EditText;
import android.content.ContentValues;
import android.database.Cursor;
import java.util.Date;

public class NewFormActivity extends Activity {
	
	private EditText patient_name;
	private EditText subjective;
	private EditText objective;
	private EditText asomething;
	private EditText psomething;
	private EditText ssomething;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_form);
        patient_name = (EditText)findViewById(R.id.new_form_patient_name);
        subjective = (EditText)findViewById(R.id.new_form_subjective);
        objective = (EditText)findViewById(R.id.new_form_objective);
        asomething = (EditText)findViewById(R.id.new_form_a);
        psomething = (EditText)findViewById(R.id.new_form_p);
        ssomething = (EditText)findViewById(R.id.new_form_s);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_new_form, menu);
        return true;
    }

    public void saveForm(View view) {
    	String s_patient_name = patient_name.getText().toString();
    	String s_subjective = subjective.getText().toString();
    	String s_objective = objective.getText().toString();
    	String s_asomething = asomething.getText().toString();
    	String s_psomething = psomething.getText().toString();
    	String s_ssomething = ssomething.getText().toString();
    	/* TODO only do performSave if saving is OK here. need name, filled-in sections */
    	performSave(s_patient_name, s_subjective, s_objective, s_asomething, s_psomething, s_ssomething);
    }

    private void performSave(String s_patient_name, String s_subjective, String s_objective, String s_asomething, String s_psomething, String s_ssomething) {
    	/* Find patient if it already exists. Ignore case. */
    	Patient patient = findPatientByName(s_patient_name);
    	if (patient == null) {
    		patient = addPatient(s_patient_name);
    	}
    	
    	Date date = new Date();
    	
    	ContentValues values = new ContentValues();
    	values.put(FormsTable.COLUMN_PATIENT_ID, patient.getId());
    	values.put(FormsTable.COLUMN_SUBJECTIVE, s_subjective);
    	values.put(FormsTable.COLUMN_OBJECTIVE, s_objective);
    	values.put(FormsTable.COLUMN_ASOMETHING, s_asomething);
    	values.put(FormsTable.COLUMN_PSOMETHING, s_psomething);
    	values.put(FormsTable.COLUMN_SSOMETHING, s_ssomething);
    	values.put(FormsTable.COLUMN_DATE, date.toString());
    	
    	Uri uri = Uri.parse(SOAPSContentProvider.CONTENT_URI + "/forms/");
    	uri = getContentResolver().insert(uri, values);
    	/*XXX
    	if (uri == null) {
    		// New database
    		uri = getContentResolver().insert(SOAPSContentProvider.CONTENT_URI, values);
    	} else {
    		// Update todo
    		getContentResolver().update(uri, values, null, null);
    	}*/
    }
    
    private Patient findPatientByName(String soughtName) {
        String[] projection = new String[] {
        		PatientsTable.COLUMN_ID,
        		PatientsTable.COLUMN_NAME
        };
        Uri uri = Uri.parse(SOAPSContentProvider.CONTENT_URI + "/patients/");
    	Cursor c = managedQuery(uri, projection, null, null, null);
        
        if (c != null ) {
            if  (c.moveToFirst()) {
                do {
                    String name = c.getString(c.getColumnIndex(PatientsTable.COLUMN_NAME));
                    if (name.equalsIgnoreCase(soughtName)) {
                    	long id = c.getLong(c.getColumnIndex(PatientsTable.COLUMN_ID));
                        Patient patient = new Patient();
                        patient.setId(id);
                        patient.setName(name);
                        return patient;
                    }
                }while (c.moveToNext());
            }
        }
        return null;
    }
    
    private Patient addPatient(String name) {
    	ContentValues values = new ContentValues();
    	values.put(PatientsTable.COLUMN_NAME, name);
    	
    	Uri uri = Uri.parse(SOAPSContentProvider.CONTENT_URI + "/patients/");
    	uri = getContentResolver().insert(uri, values);
    	/*TODO don't assume uri is non-null or a string describing a long*/
    	long id = Long.parseLong(uri.getLastPathSegment());
    	Patient patient = new Patient();
    	patient.setId(id);
    	patient.setName(name);
    	return patient;
    }
}
