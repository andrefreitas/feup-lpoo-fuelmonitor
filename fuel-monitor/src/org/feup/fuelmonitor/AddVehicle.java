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
		final TextView capacity = (TextView) findViewById(R.id.addvehicle_fuelCapacityText);
		final TextView license = (TextView) findViewById(R.id.addvehicle_registrationText);
		final Spinner year = (Spinner) findViewById(R.id.addvehicle_yearSpinner);
		final TextView kms = (TextView) findViewById(R.id.addvehicle_kmsText);
		final Button save = (Button) findViewById(R.id.addvehicle_saveButton);
		
		mDbHelper.open();
		
		

		ArrayAdapter<Integer> yearAdapter = new ArrayAdapter<Integer>(this,
				android.R.layout.simple_spinner_item);
		
		for (int y = 2012; y >= 1900; y--)
			yearAdapter.add(y);

		Cursor makeCursor = mDbHelper.fetchMakes();

		startManagingCursor(makeCursor);

		SimpleCursorAdapter makeAdapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_spinner_item, makeCursor,
				new String[] { "name" }, new int[] { android.R.id.text1 });
		
		Cursor fuelTypeCursor = mDbHelper.fetchFuelingTypes();

		startManagingCursor(fuelTypeCursor);

		SimpleCursorAdapter fuelTypeAdapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_spinner_item, fuelTypeCursor,
				new String[] { "name" }, new int[] { android.R.id.text1 });

		fuelType.setAdapter(fuelTypeAdapter);

		make.setAdapter(makeAdapter);

		year.setAdapter(yearAdapter);

		save.setOnClickListener(new View.OnClickListener() {
			//TODO Set Make and Fueltype IDs from database
			public void onClick(View v) {
				if (!model.getText().toString().equals("") && !license.getText().toString().equals("") && !capacity.getText().toString().equals("") && !kms.getText().toString().equals(""))
					if(mDbHelper.addVehicle(make.getSelectedItemPosition(), model.getText().toString(), fuelType.getSelectedItemPosition(), Short.parseShort(capacity.getText().toString()), license.getText().toString(), Short.parseShort(year.getSelectedItem().toString()), Integer.parseInt(kms.getText().toString()))>0)
					{
						finish();
					}

			}
		});
	}

}
