package org.feup.fuelmonitor;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class AddFueling extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
