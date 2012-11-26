package com.enz.soaps;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.net.Uri;
import android.widget.TextView;
import android.content.Intent;
import android.database.Cursor;

public class ViewFormActivity extends Activity {
	private long form_id;
	private String patient_name;

	private TextView tv_patient_name;
	private TextView tv_date;
	private TextView tv_subjective;
	private TextView tv_objective;
	private TextView tv_asomething;
	private TextView tv_psomething;
	private TextView tv_ssomething;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_form);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			form_id = extras.getLong("com.enz.soaps.form.id");
			patient_name = extras.getString("com.enz.soaps.patient.name");
		} else {
			// XXX something broke
		}

		tv_patient_name = (TextView) findViewById(R.id.view_form_patient_name);
		tv_date = (TextView) findViewById(R.id.view_form_date);
		tv_subjective = (TextView) findViewById(R.id.view_form_subjective);
		tv_objective = (TextView) findViewById(R.id.view_form_objective);
		tv_asomething = (TextView) findViewById(R.id.view_form_a);
		tv_psomething = (TextView) findViewById(R.id.view_form_p);
		tv_ssomething = (TextView) findViewById(R.id.view_form_s);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_view_form, menu);
		return true;
	}

	@Override
	public void onResume() {
		super.onResume();
		String[] projection = new String[] { FormsTable.COLUMN_ID,
				FormsTable.COLUMN_PATIENT_ID, FormsTable.COLUMN_DATE,
				FormsTable.COLUMN_SUBJECTIVE, FormsTable.COLUMN_OBJECTIVE,
				FormsTable.COLUMN_ASOMETHING, FormsTable.COLUMN_PSOMETHING,
				FormsTable.COLUMN_SSOMETHING };
		Uri uri = Uri.parse(SOAPSContentProvider.CONTENT_URI + "/forms/"
				+ String.valueOf(form_id));

		Cursor c = managedQuery(uri, projection, null, null, null);
		if (c != null) {
			if (c.moveToFirst()) {
				/* Patient Name */
				tv_patient_name.setText(patient_name);
				/* Date */
				String date = c.getString(c
						.getColumnIndex(FormsTable.COLUMN_DATE));
				tv_date.setText(date);
				/* Subjective, Objective, etc */
				String subjective = c.getString(c
						.getColumnIndex(FormsTable.COLUMN_SUBJECTIVE));
				tv_subjective.setText(subjective);
				String objective = c.getString(c
						.getColumnIndex(FormsTable.COLUMN_OBJECTIVE));
				tv_objective.setText(objective);
				String asomething = c.getString(c
						.getColumnIndex(FormsTable.COLUMN_ASOMETHING));
				tv_asomething.setText(asomething);
				String psomething = c.getString(c
						.getColumnIndex(FormsTable.COLUMN_PSOMETHING));
				tv_psomething.setText(psomething);
				String ssomething = c.getString(c
						.getColumnIndex(FormsTable.COLUMN_SSOMETHING));
				tv_ssomething.setText(ssomething);
			}
		}
	}
	
	public void gotoEdit() {
		Intent intent = new Intent(ViewFormActivity.this,
				EditFormActivity.class);
		intent.putExtra("com.enz.soaps.form.id", form_id);
		intent.putExtra("com.enz.soaps.patient.name", patient_name);
		startActivity(intent);
	}
	

	public void gotoHome() {
		Intent intent = new Intent(ViewFormActivity.this,
				MainActivity.class);
		startActivity(intent);
	}
}
