package org.feup.fuelmonitor;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListActivity;

public class FuelingList extends SherlockListActivity {

	private static final String TAG = "FuelMonitorFuelingList";
	private FuelMonitorDbAdapter mDbHelper;
	private long mVehicleID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mVehicleID = getIntent().getLongExtra("vehicleID", 0);
		setContentView(R.layout.fuelinglist);
		mDbHelper = new FuelMonitorDbAdapter(this);

		mDbHelper.open();

		fillData();

		registerForContextMenu(getListView());

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, Menu.FIRST, 0, R.string.fueling_edit);
		menu.add(0, Menu.FIRST + 1, 0, R.string.fueling_delete);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		long id = ((AdapterContextMenuInfo) item.getMenuInfo()).id;
		switch (item.getItemId()) {
		case Menu.FIRST:
			Intent i = new Intent(getApplicationContext(), AddFueling.class);
			i.putExtra("edit", true);
			i.putExtra("fuelingID", id);
			startActivity(i);
			return true;
		case Menu.FIRST + 1:
			mDbHelper.deleteFueling(id);
			fillData();
			return true;
		}
		return super.onContextItemSelected(item);
	}

	private void fillData() {
		Cursor fuelingCursor = mDbHelper.fetchFuelingsByVehicleID(mVehicleID);
		// TODO Use a CursorLoader (startManagingCursor is deprecated)
		startManagingCursor(fuelingCursor);
		SimpleCursorAdapter fuelingAdapter = new SimpleCursorAdapter(this,
				R.layout.fuelingrow, fuelingCursor,
				new String[] { "quantity", "cost", "kmsAtFueling", "_id",
						"drivingStyle", "_id" }, new int[] {
						R.id.fuelingRow_quantity, R.id.fuelingRow_cost,
						R.id.fuelingRow_kms, R.id.fuelingRow_avgConsumption,
						R.id.fuelingRow_drivingStyle,
						R.id.fuelingRow_courseType });
		fuelingAdapter.setViewBinder(new ViewBinder() {

			@Override
			public boolean setViewValue(View view, Cursor cursor,
					int columnIndex) {
				if (view.getId() == R.id.fuelingRow_courseType) {
					TextView courseTypeText = (TextView) view;
					boolean courseTypeCity = mDbHelper.getFuelingCourseTypeCity(cursor.getInt(columnIndex))==1;
					boolean courseTypeRoad = mDbHelper.getFuelingCourseTypeRoad(cursor.getInt(columnIndex))==1;
					boolean courseTypeFreeway = mDbHelper.getFuelingCourseTypeFreeway(cursor.getInt(columnIndex))==1;
					if(courseTypeCity)
						courseTypeText.setText(courseTypeText.getText().toString() + " C");
					if(courseTypeRoad)
						courseTypeText.setText(courseTypeText.getText().toString() + " E");
					if(courseTypeFreeway)
						courseTypeText.setText(courseTypeText.getText().toString() + " AE");
					return true;
				}
				if (view.getId() == R.id.fuelingRow_drivingStyle) {
					int drivingStyle = cursor.getInt(columnIndex);
					TextView drivingStyleText = (TextView) view;
					switch (drivingStyle) {
					case 1:
						drivingStyleText.setText("Condução calma");
						break;
					case 2:
						drivingStyleText.setText("Condução normal");
						break;
					case 3:
						drivingStyleText.setText("Condução agressiva");
						break;
					}
					return true;
				}
				if (view.getId() == R.id.fuelingRow_avgConsumption) {
					TextView avgConsumptionText = (TextView) view;
					float avgConsumption = (mDbHelper
							.getAverageFuelConsumptionByFuelingID(cursor
									.getInt(columnIndex)));
					if (avgConsumption > 0)
						avgConsumptionText.setText(Float
								.toString(avgConsumption) + " l/100Km");
					return true;
				}
				if (view instanceof TextView) {
					TextView text = (TextView) view;
					text.setText(cursor.getString(columnIndex));
					return true;
				}
				return false;
			}
		});
		getListView().setAdapter(fuelingAdapter);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mDbHelper.close();
	}

}
