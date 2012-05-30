package org.feup.fuelmonitor;

import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.SimpleCursorAdapter;

import com.actionbarsherlock.app.SherlockListActivity;

public class FuelingList extends SherlockListActivity {

	// private static final String TAG = "FuelMonitorFuelingList";
	private FuelMonitorDbAdapter mDbHelper;
	private long mVehicleID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mVehicleID = getIntent().getLongExtra("vehicleID", 0);
		setContentView(R.layout.fuelinglist);
		mDbHelper = new FuelMonitorDbAdapter(this);

		mDbHelper.open();

		fillData();

		registerForContextMenu(getListView());

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, Menu.FIRST, 0, R.string.fueling_delete);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case Menu.FIRST:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
					.getMenuInfo();
			mDbHelper.deleteFueling(info.id);
			fillData();
			return true;
		}
		return super.onContextItemSelected(item);
	}

	private void fillData() {
		Cursor fuelingCursor = mDbHelper.fetchFuelingsByVehicleID(mVehicleID);
		// TODO Use a CursorLoader (startManagingCursor is deprecated)
		startManagingCursor(fuelingCursor);
		SimpleCursorAdapter fuelingAdapter = new SimpleCursorAdapter(this,
				R.layout.fuelingrow, fuelingCursor, new String[] { "quantity",
						"cost" }, new int[] { R.id.fuelingRow_quantity,
						R.id.fuelingRow_cost });
		getListView().setAdapter(fuelingAdapter);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mDbHelper.close();
	}

}
