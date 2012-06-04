package org.feup.fuelmonitor;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;

/**
 * A class for the adding vehicle activity
 *
 */
public class AddVehicle extends SherlockActivity {

	private static final String TAG = "FuelMonitorAddVehicle";
	private FuelMonitorDbAdapter mDbHelper;
	private static File mTempFile;
	private boolean edit;
	private long mVehicleID;

	/**
	 * Function that is called when the activity is created.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		edit = getIntent().getBooleanExtra("edit", false);
		mDbHelper = new FuelMonitorDbAdapter(this);
		// mTempFile = null;
		setContentView(R.layout.addvehicle);
		final Spinner make = (Spinner) findViewById(R.id.addvehicle_brandSpinner);
		final TextView model = (TextView) findViewById(R.id.addvehicle_modelText);
		final Spinner fuelType = (Spinner) findViewById(R.id.addvehicle_fuelTypeSpinner);
		final TextView capacity = (TextView) findViewById(R.id.addvehicle_fuelCapacityText);
		final TextView registration = (TextView) findViewById(R.id.addvehicle_registrationText);
		final Spinner year = (Spinner) findViewById(R.id.addvehicle_yearSpinner);
		final TextView kms = (TextView) findViewById(R.id.addvehicle_kmsText);
		final CheckBox photo = (CheckBox) findViewById(R.id.addvehicle_photoCheckBox);
		final Button save = (Button) findViewById(R.id.addvehicle_saveButton);

		mDbHelper.open();

		ArrayAdapter<Integer> yearAdapter = new ArrayAdapter<Integer>(this,
				android.R.layout.simple_spinner_item);

		for (int y = Calendar.getInstance().get(Calendar.YEAR); y >= 1900; y--)
			yearAdapter.add(y);

		Cursor makeCursor = mDbHelper.fetchMakes();
		// TODO Use a CursorLoader (startManagingCursor is deprecated)
		startManagingCursor(makeCursor);

		SimpleCursorAdapter makeAdapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_spinner_item, makeCursor,
				new String[] { "name" }, new int[] { android.R.id.text1 });

		Cursor fuelTypeCursor = mDbHelper.fetchFuelingTypes();

		startManagingCursor(fuelTypeCursor);

		SimpleCursorAdapter fuelTypeAdapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_spinner_item, fuelTypeCursor,
				new String[] { "name" }, new int[] { android.R.id.text1 });

		fuelType.setAdapter(fuelTypeAdapter);

		make.setAdapter(makeAdapter);

		year.setAdapter(yearAdapter);

		if (edit) {
			mVehicleID = getIntent().getLongExtra("vehicleID", 0);
			Cursor editVehicle = mDbHelper.getVehicleByID(mVehicleID);
			editVehicle.moveToFirst();
			make.setSelection(editVehicle.getInt(editVehicle
					.getColumnIndex("idMake")) - 1);
			model.setText(editVehicle.getString(editVehicle
					.getColumnIndex("model")));
			fuelType.setSelection(editVehicle.getInt(editVehicle
					.getColumnIndex("idFuelType")) - 1);
			capacity.setText(Integer.toString(editVehicle.getInt(editVehicle
					.getColumnIndex("fuelCapacity"))));
			registration.setText(editVehicle.getString(editVehicle
					.getColumnIndex("registration")));
			year.setSelection(Calendar.getInstance().get(Calendar.YEAR)
					- editVehicle.getInt(editVehicle.getColumnIndex("year")));
			kms.setText(Integer.toString(editVehicle.getInt(editVehicle
					.getColumnIndex("kms"))));
		}

		save.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (!model.getText().toString().equals("")
						&& !registration.getText().toString().equals("")
						&& !capacity.getText().toString().equals("")
						&& !kms.getText().toString().equals("")) {
					long queryRetCode;
					if (edit) {
						String oldReg = mDbHelper
								.getRegistrationByID(mVehicleID);
						queryRetCode = mDbHelper
								.editVehicle(
										mVehicleID,
										((SimpleCursorAdapter) make
												.getAdapter())
												.getCursor()
												.getLong(
														((SimpleCursorAdapter) make
																.getAdapter())
																.getCursor()
																.getColumnIndex(
																		"_id")),
										model.getText().toString(),
										((SimpleCursorAdapter) fuelType
												.getAdapter())
												.getCursor()
												.getLong(
														((SimpleCursorAdapter) fuelType
																.getAdapter())
																.getCursor()
																.getColumnIndex(
																		"_id")),
										Short.parseShort(capacity.getText()
												.toString()), registration
												.getText().toString(), Short
												.parseShort(year
														.getSelectedItem()
														.toString()), Integer
												.parseInt(kms.getText()
														.toString()));
						// change photo file names
						if (queryRetCode > 0) {
							File directory = new File(Environment
									.getExternalStorageDirectory(),
									"fuelmonitor/");
							String newReg = mDbHelper
									.getRegistrationByID(mVehicleID);
							File oldimgFile = new File(directory,
									(oldReg + ".jpg"));
							File oldthumbFile = new File(directory,
									(oldReg + "t.jpg"));
							File newimgFile = new File(directory,
									(newReg + ".jpg"));
							File newthumbFile = new File(directory,
									(newReg + "t.jpg"));
							oldimgFile.renameTo(newimgFile);
							oldthumbFile.renameTo(newthumbFile);
						}
					} else {
						queryRetCode = mDbHelper.addVehicle(
								((SimpleCursorAdapter) make.getAdapter())
										.getCursor().getLong(
												((SimpleCursorAdapter) make
														.getAdapter())
														.getCursor()
														.getColumnIndex("_id")),
								model.getText().toString(),
								((SimpleCursorAdapter) fuelType.getAdapter())
										.getCursor().getLong(
												((SimpleCursorAdapter) fuelType
														.getAdapter())
														.getCursor()
														.getColumnIndex("_id")),
								Short.parseShort(capacity.getText().toString()),
								registration.getText().toString(), Short
										.parseShort(year.getSelectedItem()
												.toString()), Integer
										.parseInt(kms.getText().toString()));
					}
					if (queryRetCode > 0) {
						if (photo.isChecked()) {
							File directory = new File(Environment
									.getExternalStorageDirectory(),
									"fuelmonitor/");
							directory.mkdirs();
							File file = new File(directory, (registration
									.getText().toString() + ".jpg"));
							file.delete();
							try {
								file.createNewFile();
							} catch (IOException e) {
								Log.e(TAG, "Error creating image file");
							}
							Intent i = new Intent(
									android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
							i.putExtra(MediaStore.EXTRA_OUTPUT,
									Uri.fromFile(file));
							mTempFile = new File(directory, (registration
									.getText().toString()));
							startActivityForResult(i, 1);
						} else
							finish();
					} else {
						int duration = Toast.LENGTH_SHORT;

						Toast toast = Toast
								.makeText(
										getApplicationContext(),
										getString(R.string.add_vehicle_error_inserting_toast),
										duration);
						toast.show();
					}

				} else {
					int duration = Toast.LENGTH_SHORT;

					Toast toast = Toast.makeText(getApplicationContext(),
							getString(R.string.add_vehicle_fill_all_toast),
							duration);
					toast.show();
				}
			}
		});

	}

	/**
	 * Function to handle the activity result value
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1) {
			File imgFile = new File(mTempFile.getPath() + ".jpg");
			if (resultCode == RESULT_CANCELED)
				imgFile.delete();
			else if (resultCode == RESULT_OK) {
				Bitmap bmp = BitmapFactory
						.decodeFile(imgFile.getAbsolutePath());
				File thumbfile = new File(mTempFile.getPath() + "t.jpg");
				OutputStream out = null;
				try {
					out = new BufferedOutputStream(new FileOutputStream(
							thumbfile));
				} catch (FileNotFoundException e) {
					Log.e(TAG, "Error creating thumbnail file");
				} finally {
					if (out != null) {
						try {
							Bitmap thumb = Bitmap.createScaledBitmap(bmp,
									bmp.getWidth() / 10, bmp.getHeight() / 10,
									false);
							thumb.compress(CompressFormat.JPEG, 60, out);
							out.close();
						} catch (IOException e) {
							Log.e(TAG, "Error writing thumbnail file");
						}
					}
				}

			}
			finish();

		}
	}
	/**
	 * Function that is called when the activity is destroyed.
	 */

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mDbHelper.close();
	}

}
