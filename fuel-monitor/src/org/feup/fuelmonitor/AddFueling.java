package org.feup.fuelmonitor;

import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class AddFueling extends Activity {
	// private static final String TAG = "FuelMonitorAddFueling";
	private FuelMonitorDbAdapter mDbHelper;
	private int mYear;
	private int mMonth;
	private int mDay;
	private Spinner mDatePick;
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			updateDisplay();
		}
	};

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 0:
			return new DatePickerDialog(this, mDateSetListener, mYear, mMonth,
					mDay) {

				// Boolean to prevent recursion
				boolean dateFixed = false;
				final Calendar c = Calendar.getInstance();

				public void onDateChanged(DatePicker view, int year, int month,
						int day) {

					if (!dateFixed) {
						if (year >= c.get(Calendar.YEAR)) {
							if (year > c.get(Calendar.YEAR)) {
								year = c.get(Calendar.YEAR);
								dateFixed = true;
							}

							if (month > c.get(Calendar.MONTH)) {
								month = c.get(Calendar.MONTH);
								dateFixed = true;
							}

							if (day > c.get(Calendar.DAY_OF_MONTH)) {
								day = c.get(Calendar.DAY_OF_MONTH);
								dateFixed = true;
							}
						}

						if (dateFixed)
							updateDate(year, month, day);
					} else
						dateFixed = false;
				}
			};
		}
		return null;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDbHelper = new FuelMonitorDbAdapter(this);
		// mTempFile = null;
		setContentView(R.layout.addfueling);
		final Spinner vehicle = (Spinner) findViewById(R.id.addfueling_vehicleSpinner);
		mDatePick = (Spinner) findViewById(R.id.addfueling_datePickSpinner);
		final TextView fuelStation = (TextView) findViewById(R.id.addfueling_fuelStationText);
		final TextView kms = (TextView) findViewById(R.id.addfueling_kmsText);
		final TextView quantity = (TextView) findViewById(R.id.addfueling_quantityText);
		final TextView cost = (TextView) findViewById(R.id.addfueling_costText);
		final CheckBox courseTypeCity = (CheckBox) findViewById(R.id.addfueling_courseTypeCityCheckBox);
		final CheckBox courseTypeRoad = (CheckBox) findViewById(R.id.addfueling_courseTypeRoadCheckBox);
		final CheckBox courseTypeFreeway = (CheckBox) findViewById(R.id.addfueling_courseTypeFreewayCheckBox);
		final Spinner drivingStyle = (Spinner) findViewById(R.id.addfueling_drivingStyleSpinner);
		final Button save = (Button) findViewById(R.id.addfueling_saveButton);

		ArrayAdapter<String> dateAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item);

		mDatePick.setAdapter(dateAdapter);

		mDbHelper.open();

		Cursor vehicleCursor = mDbHelper.fetchVehicles();
		// TODO Use a CursorLoader (startManagingCursor is deprecated)
		startManagingCursor(vehicleCursor);

		SimpleCursorAdapter vehicleAdapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_spinner_item, vehicleCursor,
				new String[] { "registration" },
				new int[] { android.R.id.text1 });

		vehicle.setAdapter(vehicleAdapter);

		// get the current date
		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);

		// display the current date
		updateDisplay();

		mDatePick.setOnTouchListener(new View.OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP)
					showDialog(0);
				return true;
			}
		});

		mDatePick.setOnKeyListener(new View.OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {

				if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
					showDialog(0);
					return true;
				} else {
					return false;
				}
			}

		});

		save.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (!fuelStation.getText().toString().equals("")
						&& !kms.getText().toString().equals("")
						&& !quantity.getText().toString().equals("")
						&& !cost.getText().toString().equals("")
						&& (courseTypeCity.isChecked()
								|| courseTypeRoad.isChecked() || courseTypeFreeway
									.isChecked())) {
					if (mDbHelper.addFueling(
							new String(mYear + "-" + (mMonth + 1) // Month
																	// starts at
																	// 0
									+ "-" + mDay),
							Integer.parseInt(kms.getText().toString()),
							fuelStation.getText().toString(),
							Float.parseFloat(quantity.getText().toString()),
							Float.parseFloat(cost.getText().toString()),
							(courseTypeCity.isChecked()) ? 1 : 0,
							(courseTypeRoad.isChecked()) ? 1 : 0,
							(courseTypeFreeway.isChecked()) ? 1 : 0,
							drivingStyle.getSelectedItemPosition() + 1,
							((SimpleCursorAdapter) vehicle.getAdapter())
									.getCursor().getLong(
											((SimpleCursorAdapter) vehicle
													.getAdapter()).getCursor()
													.getColumnIndex("_id"))) > 0)
						finish();
					else {
						int duration = Toast.LENGTH_SHORT;

						Toast toast = Toast
								.makeText(
										getApplicationContext(),
										getString(R.string.add_fueling_error_inserting_toast),
										duration);
						toast.show();
					}

				} else {
					int duration = Toast.LENGTH_SHORT;

					Toast toast = Toast.makeText(getApplicationContext(),
							getString(R.string.add_fueling_fill_all_toast),
							duration);
					toast.show();
				}
			}
		});

	}

	private void updateDisplay() {

		@SuppressWarnings("unchecked")
		ArrayAdapter<String> dateAdapter = (ArrayAdapter<String>) mDatePick
				.getAdapter();

		dateAdapter.clear();

		dateAdapter.add(new String(mDay + "-" + (mMonth + 1) // Month starts at
																// 0
				+ "-" + mYear));

		mDatePick.setAdapter(dateAdapter);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mDbHelper.close();
	}

}
