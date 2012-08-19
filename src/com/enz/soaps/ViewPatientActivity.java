package com.enz.soaps;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class ViewPatientActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_patient);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_view_patient, menu);
        return true;
    }
}
