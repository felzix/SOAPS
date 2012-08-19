package com.enz.soaps;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import java.util.List;
import java.util.ArrayList;
import android.database.Cursor;
import com.enz.soaps.sqlite.Patient;

public class MainActivity extends Activity {
	
	private List<Patient> patients;
	private ListView lv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Uri uri = PatientsContentProvider.CONTENT_URI;
        
        String[] projection = new String[] {
        		PatientsTable.COLUMN_ID,
        		PatientsTable.COLUMN_NAME
        };
        
        Cursor c = managedQuery(uri, projection, null, null, null);
        patients = new ArrayList<Patient>();
        if (c != null ) {
            if  (c.moveToFirst()) {
                do {
                    String name = c.getString(c.getColumnIndex(PatientsTable.COLUMN_NAME));
                    long id = c.getLong(c.getColumnIndex(PatientsTable.COLUMN_ID));
                    Patient patient = new Patient();
                    patient.setId(id);
                    patient.setName(name);
                    patients.add(patient);
                }while (c.moveToNext());
            }
        }
        
        c.close();
        lv = (ListView)findViewById(R.id.patients_list);
        ArrayAdapter<Patient> adapter = new ArrayAdapter<Patient>(this, android.R.layout.simple_list_item_1, patients);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        	@Override
        	public void onItemClick(AdapterView<?> adapter, View view, int position, long l) {
        		Intent intent = new Intent(MainActivity.this, ViewPatientActivity.class);
            	Patient selected = (Patient)lv.getItemAtPosition(position);
            	intent.putExtra("com.enz.soaps.patient.id", selected.getId());
            	startActivity(intent);
        	}
		});
        
        EditText ed_txt = (EditText)findViewById(R.id.search_patients);
        ed_txt.addTextChangedListener(new TextWatcher() {
        	public void afterTextChanged(Editable s) {}
        	public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    public void newForm(View view) {
    	startActivity(new Intent(MainActivity.this, NewFormActivity.class));
    }
    @Override
    public void onResume() {
    	super.onResume();
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    }
}
