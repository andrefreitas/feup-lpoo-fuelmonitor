package org.feup.fuelmonitor;

import java.io.File;

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

public class VehicleList extends SherlockListActivity {

	// private static final String TAG = "FuelMonitorVehicleList";
	private FuelMonitorDbAdapter mDbHelper;

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

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, Menu.FIRST, 0, R.string.vehicle_edit);
		menu.add(0, Menu.FIRST + 1, 0, R.string.vehicle_delete);
	}

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

	private void fillData() {
		Cursor vehicleCursor = mDbHelper.fetchVehicles();
		// TODO Use a CursorLoader (startManagingCursor is deprecated)
		// TODO Perhaps save thumbnail to database?
		startManagingCursor(vehicleCursor);
		SimpleCursorAdapter vehicleAdapter = new SimpleCursorAdapter(this,
				R.layout.vehiclerow, vehicleCursor, new String[] { "makeName",
						"model", "registration", "registration" }, new int[] {
						R.id.vehicleRow_make, R.id.vehicleRow_model,
						R.id.vehicleRow_pic, R.id.vehicleRow_registration });
		vehicleAdapter.setViewBinder(new ViewBinder() {

			public boolean setViewValue(View view, Cursor cursor,
					int columnIndex) {
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

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mDbHelper.close();
	}

}
