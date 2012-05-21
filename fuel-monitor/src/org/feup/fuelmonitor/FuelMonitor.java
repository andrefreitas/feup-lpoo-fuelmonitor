package org.feup.fuelmonitor;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class FuelMonitor extends Activity {
	/** Called when the activity is first created. */
	private FuelMonitorDbAdapter mDbHelper;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDbHelper = new FuelMonitorDbAdapter(this);
		openMainMenu();
		// mDbHelper.open();
	}

	@Override
	protected void onStop() {
		super.onStop();
		mDbHelper.close();
	}

	@Override
	public void onBackPressed() {
		openMainMenu();
	}
	
	private void openMainMenu() {
		setContentView(R.layout.main);
		Button b1 = (Button) findViewById(R.id.fuelingButton);

		b1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				openInsertFueling();
			}
		});

	}

	private void openInsertFueling() {
		setContentView(R.layout.addfueling);
		Spinner s = (Spinner) findViewById(R.id.brandSpinner);
		Spinner s2 = (Spinner) findViewById(R.id.fuelTypeSpinner);
		Spinner s3 = (Spinner) findViewById(R.id.yearSpinner);
		String[] array_spinner = new String[2];
		array_spinner[0] = "Olá";
		array_spinner[1] = "Peru";
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, array_spinner);
		ArrayAdapter<Integer> addVehicleYearAdapter = new ArrayAdapter<Integer>(
				this, android.R.layout.simple_spinner_item);
		for (int y = 2012; y >= 1900; y--)
			addVehicleYearAdapter.add(y);
		s.setAdapter(adapter);
		s2.setAdapter(adapter);
		s3.setAdapter(addVehicleYearAdapter);
	}

}