package com.enz.soaps;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class NewFormActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_form);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_new_form, menu);
        return true;
    }
}
