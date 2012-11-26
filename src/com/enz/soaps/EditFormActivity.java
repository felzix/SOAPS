package com.enz.soaps;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.net.Uri;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import java.util.List;
import android.widget.Spinner;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.Date;

public class EditFormActivity extends Activity {
	private long form_id;

	private Patient spinner_patient;
	private EditText patient_name_entry;
	private EditText subjective;
	private EditText objective;
	private EditText asomething;
	private EditText psomething;
	private EditText ssomething;
	
	private Patient non_selection_patient;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_form);
		patient_name_entry = (EditText) findViewById(R.id.new_form_entry_patient_name);
		subjective = (EditText) findViewById(R.id.new_form_subjective);
		objective = (EditText) findViewById(R.id.new_form_objective);
		asomething = (EditText) findViewById(R.id.new_form_a);
		psomething = (EditText) findViewById(R.id.new_form_p);
		ssomething = (EditText) findViewById(R.id.new_form_s);
		
		String patient_name = "";
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			form_id = extras.getLong("com.enz.soaps.form.id");
			patient_name = extras.getString("com.enz.soaps.patient.name");
		} else {
			// XXX something broke
		}
		populateExistingPatientSpinner();
		spinner_patient.setName(patient_name);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_new_form, menu);
		return true;
	}
	
	private void populateExistingPatientSpinner(){
		String[] projection = new String[] { PatientsTable.COLUMN_ID,
				PatientsTable.COLUMN_NAME };

		List<Patient> patients = new ArrayList<Patient>();
		
		/*Non-selection*/
		non_selection_patient = new Patient();
		non_selection_patient.setName("Existing patient");
		patients.add(non_selection_patient);
		
		Uri uri = Uri.parse(SOAPSContentProvider.CONTENT_URI + "/patients/");

		Cursor c = managedQuery(uri, projection, null, null, null);
		if (c != null) {
			if (c.moveToFirst()) {
				do {
					String name = c.getString(c
							.getColumnIndex(PatientsTable.COLUMN_NAME));
					long id = c.getLong(c
							.getColumnIndex(PatientsTable.COLUMN_ID));
					Patient patient = new Patient();
					patient.setId(id);
					patient.setName(name);
					patients.add(patient);
				} while (c.moveToNext());
			}
		}

		Spinner spinner = (Spinner) findViewById(R.id.new_form_spinner_patient_name);
		ArrayAdapter<Patient> adapter = new ArrayAdapter<Patient>(this,
				android.R.layout.simple_list_item_1, patients);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapter, View view,
					int position, long l) {
				spinner_patient = (Patient) adapter.getItemAtPosition(position);
			}
			@Override
			public void onNothingSelected(AdapterView<?> adapter) {
				//TODO figure out what user behavior triggers this
				//     best hypothesis: pressing back button
			}
		});
	}

	public void saveForm(View view) {
		String s_patient_name = patient_name_entry.getText().toString();
		if (s_patient_name.trim().equals("")) {
			if (spinner_patient == non_selection_patient) {  /*No selection made.*/
				//TODO abort save
			} else {
				s_patient_name = spinner_patient.getName();
			}
		}
		
		String s_subjective = subjective.getText().toString();
		String s_objective = objective.getText().toString();
		String s_asomething = asomething.getText().toString();
		String s_psomething = psomething.getText().toString();
		String s_ssomething = ssomething.getText().toString();
		/*
		 * TODO only do performSave if saving is OK here. need name, filled-in
		 * sections
		 */
		long form_id = performSave(s_patient_name, s_subjective,
					       		   s_objective, s_asomething,
					        	   s_psomething, s_ssomething);
		/* Move to viewing the form */
		Intent intent = new Intent(EditFormActivity.this, ViewFormActivity.class);
		intent.putExtra("com.enz.soaps.form.id", form_id);
		intent.putExtra("com.enz.soaps.patient.name", s_patient_name);
		startActivity(intent);

	}

	private long performSave(String s_patient_name, String s_subjective,
			String s_objective, String s_asomething, String s_psomething,
			String s_ssomething) {
		/* Find patient if it already exists. Ignores case. */
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
		/*
		 * XXX if (uri == null) { // New database uri =
		 * getContentResolver().insert(SOAPSContentProvider.CONTENT_URI,
		 * values); } else { // Update todo getContentResolver().update(uri,
		 * values, null, null); }
		 */
		long id = Long.parseLong(uri.getLastPathSegment());
		return id;
	}

	private Patient findPatientByName(String soughtName) {
		String[] projection = new String[] { PatientsTable.COLUMN_ID,
				PatientsTable.COLUMN_NAME };
		Uri uri = Uri.parse(SOAPSContentProvider.CONTENT_URI + "/patients/");
		Cursor c = managedQuery(uri, projection, null, null, null);

		if (c != null) {
			if (c.moveToFirst()) {
				do {
					String name = c.getString(c
							.getColumnIndex(PatientsTable.COLUMN_NAME));
					if (name.equalsIgnoreCase(soughtName)) {
						long id = c.getLong(c
								.getColumnIndex(PatientsTable.COLUMN_ID));
						Patient patient = new Patient();
						patient.setId(id);
						patient.setName(name);
						return patient;
					}
				} while (c.moveToNext());
			}
		}
		return null;
	}

	private Patient addPatient(String name) {
		ContentValues values = new ContentValues();
		values.put(PatientsTable.COLUMN_NAME, name);

		Uri uri = Uri.parse(SOAPSContentProvider.CONTENT_URI + "/patients/");
		uri = getContentResolver().insert(uri, values);
		/* TODO don't assume uri is non-null or a string describing a long */
		long id = Long.parseLong(uri.getLastPathSegment());
		Patient patient = new Patient();
		patient.setId(id);
		patient.setName(name);
		return patient;
	}
}
