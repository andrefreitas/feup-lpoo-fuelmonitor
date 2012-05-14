package org.feup.fuelmonitor;

import android.app.Activity;
import android.os.Bundle;

public class FuelMonitor extends Activity {
    /** Called when the activity is first created. */
	private FuelMonitorDbAdapter mDbHelper;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mDbHelper = new FuelMonitorDbAdapter(this);
        mDbHelper.open();
    }
}