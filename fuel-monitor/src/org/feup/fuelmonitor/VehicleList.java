package org.feup.fuelmonitor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class VehicleList extends Activity {

	private FuelMonitorDbAdapter mDbHelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vehiclelist);
		mDbHelper = new FuelMonitorDbAdapter(this);
		Button addVehicle = (Button) findViewById(R.id.vehiclelist_addvehicle);
		ListView vehicleList = (ListView) findViewById(R.id.vehiclelist_list);
		
		final Context c = this;
		
		mDbHelper.open();		
		Cursor vehicleCursor = mDbHelper.fetchVehicles();
		//TODO Use a CursorLoader (startManagingCursor is deprecated)
		startManagingCursor(vehicleCursor);

		SimpleCursorAdapter vehicleAdapter = new SimpleCursorAdapter(this,
				R.layout.vehiclerow, vehicleCursor,
				new String[] { "registration" }, new int[] { R.id.vehicleRow });
		
		vehicleList.setAdapter(vehicleAdapter);
		
		addVehicle.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(c, AddVehicle.class);
				startActivity(i);

			}
		});
	}

}
