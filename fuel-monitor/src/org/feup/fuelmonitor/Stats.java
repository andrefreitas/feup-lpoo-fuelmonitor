package org.feup.fuelmonitor;

import java.util.ArrayList;
import java.util.Calendar;

import org.graphview.GraphView;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import com.actionbarsherlock.app.SherlockActivity;

/**
 * Stats - Creates the statistics for the consumptions.
 */
public class Stats extends SherlockActivity {
	// private static final String TAG = "Stats";
	// private long mVehicleID;
	private FuelMonitorDbAdapter mDbHelper;
	private long mVehicleId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stats);
		mDbHelper = new FuelMonitorDbAdapter(this);
		mDbHelper.open();

		Spinner spinner = (Spinner) findViewById(R.id.stats_CarSpinner);
		fillSpinner();

		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				mVehicleId = ((SimpleCursorAdapter) parent.getAdapter())
						.getCursor().getLong(
								((SimpleCursorAdapter) parent.getAdapter())
										.getCursor().getColumnIndex("_id"));
				buildGraph();

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// NOT NEEDED
			}

		});
	}

	/**
	 * BuildGraph - Builds the statistics graph bar
	 */
	private void buildGraph() {

		String[] months = new String[] { "Jan.", "Fev.", "Mar.", "Abr.",
				"Mai.", "Jun.", "Jul.", "Ago.", "Set.", "Out.", "Nov.", "Dez." };

		// Calendar class for fetching the year and the year months until this
		// date
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH) + 1;

		// Add the months
		ArrayList<String> listMonths = new ArrayList<String>();
		for (int i = 0; i < month; i++) {
			listMonths.add(months[i]);
		}
		String[] actualMonths = new String[month];
		listMonths.toArray(actualMonths);

		float[] values = new float[month];
		// Get the consumptions

		for (int i = 0; i < month; i++) {
			values[i] = mDbHelper.getAverageFuelConsumptionByDate(mVehicleId,
					i + 1, year);
		}

		// Add the graphview as a view in the RelativeLayout inside the activity
		RelativeLayout layoutGraph = (RelativeLayout) findViewById(R.id.stats_GraphLayout);
		GraphView graphView = new GraphView(this, values,
				"Consumos de " + year, actualMonths, null, GraphView.BAR);
		layoutGraph.removeAllViews();
		layoutGraph.addView(graphView);
	}

	/**
	 * Fills the spinner with the registrations of the vehicles
	 */
	private void fillSpinner() {

		Cursor vehicleCursor = mDbHelper.fetchVehicles();
		// TODO Use a CursorLoader (startManagingCursor is deprecated)
		startManagingCursor(vehicleCursor);

		SimpleCursorAdapter vehicleAdapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_spinner_item, vehicleCursor,
				new String[] { "registration" },
				new int[] { android.R.id.text1 });

		Spinner spinner = (Spinner) findViewById(R.id.stats_CarSpinner);
		spinner.setAdapter(vehicleAdapter);

		// This is needed in case one vehicle (in the middle) had been
		// deleted (Default - Select previous vehicle)
		SimpleCursorAdapter adapter = (SimpleCursorAdapter) spinner
				.getAdapter();
		Cursor cursor = adapter.getCursor();
		int findInt = mDbHelper.getLastFuelingVehicleID();
		for (int i = 0; i < adapter.getCount(); i++) {
			cursor.moveToPosition(i);
			if ((cursor.getLong(cursor.getColumnIndex("_id"))) == findInt) {
				spinner.setSelection(i);
				break;
			}
		}
	}

	/**
	 * On destroy of the activity method
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mDbHelper.close();
	}
}
