package org.feup.fuelmonitor;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;

public class VehicleList extends ListActivity {

	private FuelMonitorDbAdapter mDbHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vehiclelist);
		mDbHelper = new FuelMonitorDbAdapter(this);
		Button addVehicle = (Button) findViewById(R.id.vehiclelist_addvehicle);

		final Context c = this;

		mDbHelper.open();

		fillData();

		registerForContextMenu(getListView());

		addVehicle.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(c, AddVehicle.class);
				startActivity(i);
			}
		});
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, Menu.FIRST, 0, R.string.vehicle_delete);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case Menu.FIRST:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
					.getMenuInfo();
			mDbHelper.deleteVehicle(info.id);
			fillData();
			return true;
		}
		return super.onContextItemSelected(item);
	}

	private void fillData() {
		Cursor vehicleCursor = mDbHelper.fetchVehicles();
		// TODO Use a CursorLoader (startManagingCursor is deprecated)
		// TODO Implement getting the make name from the ID
		startManagingCursor(vehicleCursor);
		SimpleCursorAdapter vehicleAdapter = new SimpleCursorAdapter(this,
				R.layout.vehiclerow, vehicleCursor, new String[] { "idMake",
						"model" }, new int[] { R.id.vehicleRow_make,
						R.id.vehicleRow_model });

		getListView().setAdapter(vehicleAdapter);
	}

}