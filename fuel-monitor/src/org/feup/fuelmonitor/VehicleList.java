package org.feup.fuelmonitor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class VehicleList extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vehiclelist);

		Button addVehicle = (Button) findViewById(R.id.vehiclelist_addvehicle);

		final Context c = this;

		addVehicle.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(c, AddVehicle.class);
				startActivity(i);

			}
		});
	}

}
