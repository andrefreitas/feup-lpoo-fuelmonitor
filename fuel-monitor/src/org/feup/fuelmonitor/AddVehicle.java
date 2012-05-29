package org.feup.fuelmonitor;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.app.Activity;
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

public class AddVehicle extends Activity {

	private static final String TAG = "FuelMonitorAddVehicle";
	private FuelMonitorDbAdapter mDbHelper;
	private static File mTempFile;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDbHelper = new FuelMonitorDbAdapter(this);
		// mTempFile = null;
		setContentView(R.layout.addvehicle);
		final Spinner make = (Spinner) findViewById(R.id.addvehicle_brandSpinner);
		final TextView model = (TextView) findViewById(R.id.addvehicle_modelText);
		final Spinner fuelType = (Spinner) findViewById(R.id.addvehicle_fuelTypeSpinner);
		final TextView capacity = (TextView) findViewById(R.id.addvehicle_fuelCapacityText);
		final TextView license = (TextView) findViewById(R.id.addvehicle_registrationText);
		final Spinner year = (Spinner) findViewById(R.id.addvehicle_yearSpinner);
		final TextView kms = (TextView) findViewById(R.id.addvehicle_kmsText);
		final CheckBox photo = (CheckBox) findViewById(R.id.addvehicle_photoCheckBox);
		final Button save = (Button) findViewById(R.id.addvehicle_saveButton);

		mDbHelper.open();

		ArrayAdapter<Integer> yearAdapter = new ArrayAdapter<Integer>(this,
				android.R.layout.simple_spinner_item);

		for (int y = 2012; y >= 1900; y--)
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

		save.setOnClickListener(new View.OnClickListener() {
			// TODO Set Make and Fueltype IDs from database
			public void onClick(View v) {
				if (!model.getText().toString().equals("")
						&& !license.getText().toString().equals("")
						&& !capacity.getText().toString().equals("")
						&& !kms.getText().toString().equals("")) {
					if (mDbHelper.addVehicle(
							make.getSelectedItemPosition() + 1, model.getText()
									.toString(), fuelType
									.getSelectedItemPosition(), Short
									.parseShort(capacity.getText().toString()),
							license.getText().toString(), Short.parseShort(year
									.getSelectedItem().toString()), Integer
									.parseInt(kms.getText().toString())) > 0) {
						if (photo.isChecked()) {
							File directory = new File(Environment
									.getExternalStorageDirectory(),
									"fuelmonitor/");
							directory.mkdirs();
							File file = new File(directory, (license.getText()
									.toString() + ".jpg"));
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
							mTempFile = new File(directory, (license.getText()
									.toString()));
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

	// TODO FIX ORIENTATION BUG WITH BITMAP
	// TODO FIX ACTIVITY RESULT WHEN HORIZONTAL CAM (temp solution disabled
	// rotation)
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

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mDbHelper.close();
	}

}
