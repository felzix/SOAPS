package com.enz.soaps;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ViewPatientActivity extends Activity {

	private List<Form> forms;
	private ListView lv;
	private Cursor forms_cursor;
	private Patient patient;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_patient);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			patient = new Patient();
			patient.setId(extras.getLong("com.enz.soaps.patient.id"));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_view_patient, menu);
		return true;
	}

	@Override
	public void onResume() {
		super.onResume();

		/* Patient Info */
		String[] projection = new String[] { PatientsTable.COLUMN_NAME};

		Uri uri = Uri.parse(SOAPSContentProvider.CONTENT_URI + "/patients/"
				+ String.valueOf(patient.getId()));
		Cursor c = managedQuery(uri, projection, null, null, null);

		if (c != null) {
			if (c.moveToFirst()) {
				String name = c.getString(c
						.getColumnIndex(PatientsTable.COLUMN_NAME));
				patient.setName(name);
				TextView tv_patient_name = (TextView) findViewById(R.id.view_patient_name);
				tv_patient_name.setText(name);
			}
		}

		/* Forms */
		projection = new String[] { FormsTable.COLUMN_ID,
				FormsTable.COLUMN_PATIENT_ID, FormsTable.COLUMN_DATE };

		forms = new ArrayList<Form>();

		uri = Uri.parse(SOAPSContentProvider.CONTENT_URI + "/forms/");

		forms_cursor = managedQuery(uri, projection, null, null, null);
		c = forms_cursor; // easier name
		if (c != null) {
			if (c.moveToFirst()) {
				do {
					long patient_id = c.getLong(c
							.getColumnIndex(FormsTable.COLUMN_PATIENT_ID));
					if (patient_id == patient.getId()) {
						long id = c.getLong(c
								.getColumnIndex(FormsTable.COLUMN_ID));
						String date_str = c.getString(c
								.getColumnIndex(FormsTable.COLUMN_DATE));
						Date date = new Date(date_str);
						Form form = new Form();
						form.setId(id);
						form.setDate(date);
						forms.add(form);
					}

				} while (c.moveToNext());
			}
		}

		lv = (ListView) findViewById(R.id.forms_list);
		ArrayAdapter<Form> adapter = new ArrayAdapter<Form>(this,
				android.R.layout.simple_list_item_1, forms);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View view,
					int position, long l) {
				Intent intent = new Intent(ViewPatientActivity.this,
						ViewFormActivity.class);
				Form selected = (Form) lv.getItemAtPosition(position);
				intent.putExtra("com.enz.soaps.form.id", selected.getId());
				intent.putExtra("com.enz.soaps.patient.name", patient.getName());
				startActivity(intent);
			}
		});
	}
}
