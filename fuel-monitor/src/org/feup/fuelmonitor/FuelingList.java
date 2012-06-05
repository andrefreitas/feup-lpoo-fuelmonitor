package org.feup.fuelmonitor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListActivity;

/**
 * Lists the fueling in a activity
 * 
 */
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
		Button addFueling = (Button) findViewById(R.id.fuelinglist_addfueling);

		mDbHelper.open();

		fillData();
		fillTotals();

		registerForContextMenu(getListView());

		addFueling.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(getApplicationContext(), AddFueling.class);
				i.putExtra("idVehicle", mVehicleID);
				startActivityForResult(i, 2);
			}
		});
	}

	/**
	 * Populates the context menu
	 */

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, Menu.FIRST, 0, R.string.fueling_edit);
		menu.add(0, Menu.FIRST + 1, 0, R.string.fueling_delete);
	}

	/**
	 * Function to handle a selection of a context menu
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		long id = ((AdapterContextMenuInfo) item.getMenuInfo()).id;
		switch (item.getItemId()) {
		case Menu.FIRST:
			Intent i = new Intent(getApplicationContext(), AddFueling.class);
			i.putExtra("edit", true);
			i.putExtra("fuelingID", id);
			startActivityForResult(i, 1);
			return true;
		case Menu.FIRST + 1:
			mDbHelper.deleteFueling(id);
			fillData();
			fillTotals();
			return true;
		}
		return super.onContextItemSelected(item);
	}

	/**
	 * Fills the list with the fuelling data
	 */
	private void fillData() {
		Cursor fuelingCursor = mDbHelper.fetchFuelingsByVehicleID(mVehicleID);
		// TODO Use a CursorLoader (startManagingCursor is deprecated)
		startManagingCursor(fuelingCursor);
		SimpleCursorAdapter fuelingAdapter = new SimpleCursorAdapter(this,
				R.layout.fuelingrow, fuelingCursor, new String[] { "quantity",
						"cost", "kmsAtFueling", "_id", "drivingStyle", "_id",
						"fuelStation", "date" }, new int[] {
						R.id.fuelingRow_quantity, R.id.fuelingRow_cost,
						R.id.fuelingRow_kms, R.id.fuelingRow_avgConsumption,
						R.id.fuelingRow_drivingStyle,
						R.id.fuelingRow_courseType,
						R.id.fuelingRow_fuelStation, R.id.fuelingRow_date });
		fuelingAdapter.setViewBinder(new ViewBinder() {

			@Override
			public boolean setViewValue(View view, Cursor cursor,
					int columnIndex) {
				if (view.getId() == R.id.fuelingRow_date) {
					TextView dateText = (TextView) view;
					try {
						SimpleDateFormat date = new SimpleDateFormat(
								"yyyy-MM-dd");
						date.parse(cursor.getString(columnIndex));
						Calendar c = date.getCalendar();
						dateText.setText(String.format("%02d-%02d-%d",
								c.get(Calendar.DAY_OF_MONTH),
								(c.get(Calendar.MONTH) + 1),
								c.get(Calendar.YEAR)));
					} catch (ParseException e) {
						Log.e(TAG, "Error parsing date from database");
						return false;
					}
					return true;
				}
				if (view.getId() == R.id.fuelingRow_courseType) {
					TextView courseTypeText = (TextView) view;
					boolean courseTypeCity = mDbHelper
							.getFuelingCourseTypeCity(cursor
									.getInt(columnIndex)) == 1;
					boolean courseTypeRoad = mDbHelper
							.getFuelingCourseTypeRoad(cursor
									.getInt(columnIndex)) == 1;
					boolean courseTypeFreeway = mDbHelper
							.getFuelingCourseTypeFreeway(cursor
									.getInt(columnIndex)) == 1;
					if (courseTypeCity)
						courseTypeText.setText(courseTypeText.getText()
								.toString() + " C");
					if (courseTypeRoad)
						courseTypeText.setText(courseTypeText.getText()
								.toString() + " E");
					if (courseTypeFreeway)
						courseTypeText.setText(courseTypeText.getText()
								.toString() + " AE");
					return true;
				}
				if (view.getId() == R.id.fuelingRow_drivingStyle) {
					int drivingStyle = cursor.getInt(columnIndex);
					TextView drivingStyleText = (TextView) view;
					switch (drivingStyle) {
					case 1:
						drivingStyleText
								.setText(getString(R.string.fueling_list_drivingStyle_calm));
						break;
					case 2:
						drivingStyleText
								.setText(getString(R.string.fueling_list_drivingStyle_normal));
						break;
					case 3:
						drivingStyleText
								.setText(getString(R.string.fueling_list_drivingStyle_agressive));
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
						avgConsumptionText.setText(String.format(
								"%.1f l/100Km", avgConsumption));
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// update totals on edit or new fueling
		if (requestCode == 1 || requestCode == 2) {
			fillTotals();
		}
	}

	private void fillTotals() {
		if (mDbHelper.getNumFuelings(mVehicleID) > 0) {
			TextView totalkms = (TextView) findViewById(R.id.fuelinglist_totalkms);
			TextView totallitres = (TextView) findViewById(R.id.fuelinglist_totallitres);
			TextView totalcost = (TextView) findViewById(R.id.fuelinglist_totalcost);

			totalkms.setText(String.valueOf(mDbHelper.getTotalKms(mVehicleID))
					+ " Kms");
			totallitres.setText(String.format("%.1f l",
					mDbHelper.getTotalLitres(mVehicleID)));
			totalcost.setText(String.format("%.1f €",
					mDbHelper.getTotalCost(mVehicleID)));
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mDbHelper.close();
	}

}
