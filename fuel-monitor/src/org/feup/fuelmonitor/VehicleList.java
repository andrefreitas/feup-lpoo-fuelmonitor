package org.feup.fuelmonitor;

import java.io.File;
import java.util.Calendar;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListActivity;
/**
 * VehicleList - A class for listing in a activity the vehicles
 */
public class VehicleList extends SherlockListActivity {
	private FuelMonitorDbAdapter mDbHelper; /* The class for managing the data base */
	
	/**
	 * Function that is called when the activity is created
	 * @param savedInstanceState a object from the android system that have informations that may be useful for the activity
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vehiclelist);
		mDbHelper = new FuelMonitorDbAdapter(this);
		Button addVehicle = (Button) findViewById(R.id.vehiclelist_addvehicle);

		mDbHelper.open();

		fillData();

		registerForContextMenu(getListView());

		addVehicle.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), AddVehicle.class);
				startActivity(i);
			}
		});

		getListView().setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> adapter, View view,
					int position, long arg) {
				Intent i = new Intent(getApplicationContext(),
						FuelingList.class);
				long id = arg;
				i.putExtra("vehicleID", id);
				startActivity(i);
			}
		});
	}
	
	/**
	 * A function to populate the context menu that is visible in the press long of a vehicle
	 * @param v the vehicle
	 * @param menu the menu itself
	 * @param menuInfo the information about the menu
	 */

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, Menu.FIRST, 0, R.string.vehicle_edit);
		menu.add(0, Menu.FIRST + 1, 0, R.string.vehicle_delete);
	}
	/**
	 * Function that is called when a car from the list is selected.
	 * @param item the item that is being selected
	 * @return true upon success
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		long id = ((AdapterContextMenuInfo) item.getMenuInfo()).id;
		switch (item.getItemId()) {
		case Menu.FIRST:
			Intent i = new Intent(getApplicationContext(), AddVehicle.class);
			i.putExtra("edit", true);
			i.putExtra("vehicleID", id);
			startActivity(i);
			return true;
		case Menu.FIRST + 1:
			File directory = new File(
					Environment.getExternalStorageDirectory(), "fuelmonitor/");
			String registration = mDbHelper.getRegistrationByID(id);
			File imgFile = new File(directory, (registration + ".jpg"));
			File thumbFile = new File(directory, (registration + "t.jpg"));
			imgFile.delete();
			thumbFile.delete();
			mDbHelper.deleteVehicle(id);
			fillData();
			return true;
		}
		return super.onContextItemSelected(item);
	}

	/**
	 * Fills all the list with the vehicles objects
	 */
	private void fillData() {
		Cursor vehicleCursor = mDbHelper.fetchVehicles();
		// TODO Use a CursorLoader (startManagingCursor is deprecated)
		// TODO Perhaps save thumbnail to database?
		startManagingCursor(vehicleCursor);
		SimpleCursorAdapter vehicleAdapter = new SimpleCursorAdapter(this,
				R.layout.vehiclerow, vehicleCursor, new String[] { "makeName",
						"model", "registration", "registration", "_id" },
				new int[] { R.id.vehicleRow_make, R.id.vehicleRow_model,
						R.id.vehicleRow_pic, R.id.vehicleRow_registration,
						R.id.vehicleRow_avgConsumption });
		vehicleAdapter.setViewBinder(new ViewBinder() {

			public boolean setViewValue(View view, Cursor cursor,
					int columnIndex) {
				if (view.getId() == R.id.vehicleRow_avgConsumption) {
					if (mDbHelper.getNumFuelings() > 0) {
						TextView text = (TextView) view;
						text.setText(Float.toString(mDbHelper
								.getAverageFuelConsumptionByVehicleID(cursor
										.getInt(columnIndex))));
					}
					return true;
				}
				if (view instanceof TextView) {
					TextView text = (TextView) view;
					text.setText(cursor.getString(columnIndex));
					return true;
				}
				if (view.getId() == R.id.vehicleRow_pic) {
					ImageView image = (ImageView) view;
					File directory = new File(Environment
							.getExternalStorageDirectory(), "fuelmonitor/");
					File file = new File(directory, (cursor
							.getString(columnIndex) + "t.jpg"));
					if (file.exists() && file.length() > 0)
						image.setImageBitmap(BitmapFactory.decodeFile(file
								.getAbsolutePath()));
					return true;

				}
				return false;
			}
		});
		getListView().setAdapter(vehicleAdapter);
	}

	/**
	 * Function that is called when the activity is destroyed
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mDbHelper.close();
	}

}
