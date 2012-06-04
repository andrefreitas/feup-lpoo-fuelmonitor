package org.feup.fuelmonitor;

import java.util.ArrayList;
import java.util.Calendar;

import org.graphview.*;

import com.actionbarsherlock.app.SherlockActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import org.feup.fuelmonitor.FuelMonitorDbAdapter;

/**
 * Stats - Creates the statistics for the consumptions.
 */
public class Stats extends SherlockActivity {
	private long mVehicleID;
	private FuelMonitorDbAdapter mDbHelper;
	private long vehicleId;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stats);
		mDbHelper = new FuelMonitorDbAdapter(this);
		mDbHelper.open();
		
		fillSpinner();
		
		Spinner spinner = (Spinner) findViewById(R.id.stats_CarSpinner);
		
		vehicleId = ((SimpleCursorAdapter) spinner
			       .getAdapter()).getCursor().getLong(
			       ((SimpleCursorAdapter) spinner.getAdapter())
			         .getCursor().getColumnIndex("_id"));
		
		buildGraph();
		
	}
/**
 * BuildGraph
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
		
		float [] values = new float[month];
		// Get the consumptions

		for(int i=0; i<month; i++){
			values[i]=mDbHelper.getAverageFuelConsumptionByDate(vehicleId,i+1,year);
		}
	
		// Add the graphview as a view in the RelativeLayout inside the activity
		RelativeLayout layoutGraph = (RelativeLayout) findViewById(R.id.stats_GraphLayout);
		GraphView graphView = new GraphView(this, values,
				"Consumos de " + year, actualMonths, null, GraphView.BAR);
		layoutGraph.addView(graphView);
	}

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
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mDbHelper.close();
	}
}
