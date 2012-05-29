package org.feup.fuelmonitor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class FuelMonitor extends Activity {
	/** Called when the activity is first created. */

	private FuelMonitorDbAdapter mDbHelper;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mDbHelper = new FuelMonitorDbAdapter(this);

		setContentView(R.layout.main);
		Button b1 = (Button) findViewById(R.id.main_fuelingButton);
		Button b2 = (Button) findViewById(R.id.main_vehiclesButton);
		final Context c = this;
		b1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				mDbHelper.open();
				int numVehicles = mDbHelper.getNumVehicles();
				mDbHelper.close();
				if (numVehicles > 0) {
					Intent i = new Intent(c, AddFueling.class);
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
				Intent i = new Intent(c, VehicleList.class);
				startActivity(i);

			}
		});
	}

}