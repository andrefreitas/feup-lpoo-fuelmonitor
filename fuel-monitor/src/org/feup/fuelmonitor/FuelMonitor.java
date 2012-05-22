package org.feup.fuelmonitor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class FuelMonitor extends Activity {
	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main);
		Button b1 = (Button) findViewById(R.id.main_fuelingButton);
		final Context c = this;
		b1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(c, AddVehicle.class);
				startActivity(i);

			}
		});
	}

}