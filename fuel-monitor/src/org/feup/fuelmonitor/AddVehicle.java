package org.feup.fuelmonitor;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class AddVehicle extends Activity {

	private FuelMonitorDbAdapter mDbHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDbHelper = new FuelMonitorDbAdapter(this);
		setContentView(R.layout.addvehicle);
		final Spinner make = (Spinner) findViewById(R.id.addvehicle_brandSpinner);
		final TextView model = (TextView) findViewById(R.id.addvehicle_modelText);
		final Spinner fuelType = (Spinner) findViewById(R.id.addvehicle_fuelTypeSpinner);
		final TextView license = (TextView) findViewById(R.id.addvehicle_licenseText);
		final Spinner year = (Spinner) findViewById(R.id.addvehicle_yearSpinner);
		final Button save = (Button) findViewById(R.id.addvehicle_saveButton);
		String[] array_spinner = new String[2];
		array_spinner[0] = "Olá";
		array_spinner[1] = "Peru";
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, array_spinner);
		ArrayAdapter<Integer> addVehicleYearAdapter = new ArrayAdapter<Integer>(
				this, android.R.layout.simple_spinner_item);
		for (int y = 2012; y >= 1900; y--)
			addVehicleYearAdapter.add(y);
		
		mDbHelper.open();
		
		Cursor fuelTypes = mDbHelper.fetchAllFuelingTypes();
		
		startManagingCursor( fuelTypes);

    	SimpleCursorAdapter fuelTypesAdapter =
    	new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, fuelTypes, new String[]{"name"}, new int[]{android.R.id.text1});
		
		fuelType.setAdapter(fuelTypesAdapter);
		
		make.setAdapter(adapter);
		
		year.setAdapter(addVehicleYearAdapter);

		save.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if (model.getText() != "" && license.getText() != "") {
					// mDbHelper.addFueling(make, model, fuelType, license,
					// year);
				}

			}
		});
	}

}
