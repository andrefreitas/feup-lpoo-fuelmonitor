package org.feup.fuelmonitor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
/**
 * FuelMonitor - This is the home screen class
 */
public class FuelMonitor extends SherlockActivity {

	private FuelMonitorDbAdapter mDbHelper; /* The object for manipulating the data */
	
	/**
	 * Function that is called when the activity is created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mDbHelper = new FuelMonitorDbAdapter(this);

		setContentView(R.layout.main);
		Button b1 = (Button) findViewById(R.id.main_fuelingButton);
		Button b2 = (Button) findViewById(R.id.main_vehiclesButton);
		Button b3 = (Button) findViewById(R.id.main_statsButton);
		Button b4 = (Button) findViewById(R.id.main_gasButton);
		b1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				mDbHelper.open();
				int numVehicles = mDbHelper.getNumVehicles();
				mDbHelper.close();
				if (numVehicles > 0) {
					Intent i = new Intent(getApplicationContext(),
							AddFueling.class);
					startActivity(i);
				} else {
					int duration = Toast.LENGTH_SHORT;

					Toast toast = Toast.makeText(getApplicationContext(),
							getString(R.string.vehicle_menu_no_vehicles_toast),
							duration);
					toast.show();
				}
			}
		});
		b2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(),
						VehicleList.class);
				startActivity(i);

			}
		});
		b3.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mDbHelper.open();
				int numVehicles = mDbHelper.getNumVehicles();
				mDbHelper.close();
				if (numVehicles > 0) {
					Intent i = new Intent(getApplicationContext(), Stats.class);
					startActivity(i);
				} else {
					int duration = Toast.LENGTH_SHORT;

					Toast toast = Toast.makeText(getApplicationContext(),
							getString(R.string.vehicle_menu_no_vehicles_toast),
							duration);
					toast.show();
				}

			}
		});
		b4.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				int duration = Toast.LENGTH_SHORT;
				Toast toast = Toast.makeText(getApplicationContext(),
						getString(R.string.menu_not_implemented_toast),
						duration);
				toast.show();

			}
		});
	}

}