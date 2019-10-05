package com.example.customfancontroller;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    DialView mCustomView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCustomView = findViewById(R.id.dialView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // Menu options set the number of selections that appear in the
    // fan dial.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle menu item clicks here.
        // Use the orderInCategory of menu item as the count n.
        int n = item.getOrder();
        // Use the DialView setSelectionCount() setter method.
        mCustomView.setSelectionCount(n);
        return super.onOptionsItemSelected(item);
    }
}
