/*
 * Copyright (C) 2008 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.feup.fuelmonitor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
/**
 * A class for manipulating the database
 */
public class FuelMonitorDbAdapter {

	private static final String TAG = "FuelMonitorDbAdapter";
	private DatabaseHelper mDbHelper;	/* The database helper */
	private SQLiteDatabase mDb; 		/* The sqlite database */

	/**
	 * Database creation sql statement
	 */
	private static final String FUELTYPE_CREATE = "CREATE TABLE FuelType ("
			+ "  _id INTEGER PRIMARY KEY,"
			+ "  name nvarchar2 NOT NULL UNIQUE);";
	private static final String MAKE_CREATE = "CREATE TABLE Make ("
			+ "  _id INTEGER PRIMARY KEY,"
			+ "  name nvarchar2 NOT NULL UNIQUE);";
	private static final String VEHICLE_CREATE = "CREATE TABLE Vehicle ("
			+ "  _id INTEGER PRIMARY KEY," + "  kms integer NOT NULL ,"
			+ "  year integer NOT NULL ," + "  fuelCapacity integer NOT NULL ,"
			+ "  registration nvarchar2 NOT NULL UNIQUE ON CONFLICT IGNORE,"
			+ "  model nvarchar2 NOT NULL," + "  idMake integer NOT NULL,"
			+ "  idFuelType integer REFERENCES FuelType ON DELETE CASCADE);";
	private static final String FUELING_CREATE = "CREATE TABLE Fueling ("
			+ "  _id INTEGER PRIMARY KEY," + "  date date NOT NULL ,"
			+ "  kmsAtFueling integer NOT NULL ,"
			+ "  fuelStation nvarchar2 NOT NULL ,"
			+ "  quantity double NOT NULL ," + "  cost float NOT NULL ,"
			+ "  courseTypeCity integer NOT NULL ,"
			+ "  courseTypeRoad integer NOT NULL ,"
			+ "  courseTypeFreeway integer NOT NULL ,"
			+ "  drivingStyle integer NOT NULL ,"
			+ "  idVehicle integer REFERENCES Vehicle ON DELETE CASCADE);";

	private static final String DATABASE_NAME = "data";
	private static final int DATABASE_VERSION = 1;

	private final Context mCtx;

	/**
	 * A class for creating the data base structure
	 *
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper {

		private final Context mCtx;

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			mCtx = context;
		}

		@Override
		public void onCreate(SQLiteDatabase db) {

			Log.i(TAG, "Creating FuelType table");
			db.execSQL(FUELTYPE_CREATE);
			Log.i(TAG, "Creating Make table");
			db.execSQL(MAKE_CREATE);
			Log.i(TAG, "Creating Vehicle table");
			db.execSQL(VEHICLE_CREATE);
			Log.i(TAG, "Creating Fueling table");
			db.execSQL(FUELING_CREATE);

			// TODO Ship app with pre-populated and created database
			Log.i(TAG, "Populating FuelType table");
			ContentValues fuelTypes = new ContentValues();
			String[] fuelTypesList = mCtx.getResources().getStringArray(
					R.array.fuelType_database_list);

			for (int i = 0; i < fuelTypesList.length; i++) {
				fuelTypes.put("name", fuelTypesList[i]);
				db.insert("fuelType", null, fuelTypes);
			}

			Log.i(TAG, "Populating Make table");
			ContentValues makes = new ContentValues();
			String[] makeList = mCtx.getResources().getStringArray(
					R.array.make_database_list);

			for (int i = 0; i < makeList.length; i++) {
				makes.put("name", makeList[i]);
				db.insert("make", null, makes);
			}

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS Fueling;");
			db.execSQL("DROP TABLE IF EXISTS FuelType;");
			db.execSQL("DROP TABLE IF EXISTS Make;");
			db.execSQL("DROP TABLE IF EXISTS Model;");
			db.execSQL("DROP TABLE IF EXISTS Vehicle;");
			onCreate(db);
		}
	}

	/**
	 * Constructor - takes the context to allow the database to be
	 * opened/created
	 * 
	 * @param ctx
	 *            the Context within which to work
	 */
	public FuelMonitorDbAdapter(Context ctx) {
		this.mCtx = ctx;
	}

	/**
	 * Open the Fuel Monitor database. If it cannot be opened, try to create a
	 * new instance of the database. If it cannot be created, throw an exception
	 * to signal the failure
	 * 
	 * @return this (self reference, allowing this to be chained in an
	 *         initialization call)
	 * @throws SQLException
	 *             if the database could be neither opened or created
	 */
	public FuelMonitorDbAdapter open() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();

		if (!mDb.isReadOnly())
			// Enable foreign key constraints (MAY NOT WORK ON < 2.1)
			mDb.execSQL("PRAGMA foreign_keys=ON;");

		return this;
	}

	public void close() {
		mDbHelper.close();
	}

	public long addVehicle(long make, String model, long l, short fuelCapacity,
			String registration, short year, int kms) {
		ContentValues vehicle = new ContentValues();
		vehicle.put("idMake", make);
		vehicle.put("model", model);
		vehicle.put("idFuelType", l);
		vehicle.put("fuelCapacity", fuelCapacity);
		vehicle.put("registration", registration);
		vehicle.put("year", year);
		vehicle.put("kms", kms);
		return mDb.insert("vehicle", null, vehicle);
	}

	public long editVehicle(long rowId, long make, String model, long l,
			short fuelCapacity, String registration, short year, int kms) {
		ContentValues vehicle = new ContentValues();
		vehicle.put("idMake", make);
		vehicle.put("model", model);
		vehicle.put("idFuelType", l);
		vehicle.put("fuelCapacity", fuelCapacity);
		vehicle.put("registration", registration);
		vehicle.put("year", year);
		vehicle.put("kms", kms);
		return mDb.update("vehicle", vehicle, "_id = ?",
				new String[] { String.valueOf(rowId) });
	}

	public long addFueling(String date, int kms, String fuelStation,
			float quantity, float cost, int courseTypeCity, int courseTypeRoad,
			int courseTypeFreeway, int drivingStyle, long vehicle) {
		ContentValues fueling = new ContentValues();
		fueling.put("date", date);
		fueling.put("kmsAtFueling", kms);
		fueling.put("fuelStation", fuelStation);
		fueling.put("quantity", quantity);
		fueling.put("cost", cost);
		fueling.put("courseTypeCity", courseTypeCity);
		fueling.put("courseTypeRoad", courseTypeRoad);
		fueling.put("courseTypeFreeway", courseTypeFreeway);
		fueling.put("drivingStyle", drivingStyle);
		fueling.put("idVehicle", vehicle);
		return mDb.insert("fueling", null, fueling);
	}

	public long editFueling(long rowId, String date, int kms,
			String fuelStation, float quantity, float cost, int courseTypeCity,
			int courseTypeRoad, int courseTypeFreeway, int drivingStyle,
			long vehicle) {
		ContentValues fueling = new ContentValues();
		fueling.put("date", date);
		fueling.put("kmsAtFueling", kms);
		fueling.put("fuelStation", fuelStation);
		fueling.put("quantity", quantity);
		fueling.put("cost", cost);
		fueling.put("courseTypeCity", courseTypeCity);
		fueling.put("courseTypeRoad", courseTypeRoad);
		fueling.put("courseTypeFreeway", courseTypeFreeway);
		fueling.put("drivingStyle", drivingStyle);
		fueling.put("idVehicle", vehicle);
		return mDb.update("fueling", fueling, "_id = ?",
				new String[] { String.valueOf(rowId) });
	}

	public Cursor fetchFuelingTypes() {

		return mDb.query("FuelType", new String[] { "_id", "name" }, null,
				null, null, null, null);
	}

	public Cursor fetchMakes() {

		return mDb.query("Make", new String[] { "_id", "name" }, null, null,
				null, null, null);
	}

	public Cursor fetchVehicles() {
		return mDb
				.rawQuery(
						"SELECT V._id, model, M.name as makeName, registration FROM Make M, Vehicle V WHERE V.idmake = M._id",
						null);
	}

	public Cursor getVehicleByID(long rowId) {
		return mDb.query("Vehicle", null, "_id=?",
				new String[] { String.valueOf(rowId) }, null, null, null);
	}

	public Cursor getFuelingByID(long rowId) {
		return mDb.query("Fueling", null, "_id=?",
				new String[] { String.valueOf(rowId) }, null, null, null);
	}

	public String getRegistrationByID(long rowId) {
		Cursor result = mDb.query("Vehicle", new String[] { "registration" },
				"_id=?", new String[] { String.valueOf(rowId) }, null, null,
				null);
		result.moveToFirst();
		return result.getString(0);
	}

	public boolean deleteVehicle(long rowId) {
		return mDb.delete("vehicle", "_id=?",
				new String[] { String.valueOf(rowId) }) > 0;
	}

	public int getNumVehicles() {
		Cursor result = mDb.query("Vehicle", new String[] { "COUNT(*)" }, null,
				null, null, null, null);
		result.moveToFirst();
		return result.getInt(0);
	}

	public int getNumFuelings() {
		Cursor result = mDb.query("Fueling", new String[] { "COUNT(*)" }, null,
				null, null, null, null);
		result.moveToFirst();
		return result.getInt(0);
	}

	public int getMinKms(long rowId) {
		Cursor result = mDb.query("Vehicle", new String[] { "kms" }, "_id=?",
				new String[] { String.valueOf(rowId) }, null, null, null);
		result.moveToFirst();
		return result.getInt(0);

	}

	public int getMaxKms(long rowId) {
		Cursor result = mDb.query("Fueling",
				new String[] { "MAX(kmsAtFueling)" }, "idVehicle=?",
				new String[] { String.valueOf(rowId) }, null, null, null);
		result.moveToFirst();
		return result.getInt(0);

	}

	public float averageConsumption(int year, int month, int vehicleID) {
		Cursor result = mDb.rawQuery("SELECT AVG(*) FROM Fueling "
				+ "Where idVehicle=" + vehicleID + " and date<" + year + "-"
				+ month + "-30 and date >" + year + "-" + month + "-0", null);
		return result.getFloat(0);
	}

	public boolean deleteFueling(long rowId) {
		return mDb.delete("fueling", "_id=?",
				new String[] { String.valueOf(rowId) }) > 0;
	}

	public Cursor fetchFuelingsByVehicleID(long rowId) {
		return mDb.query("Fueling", new String[] { "_id", "quantity", "cost",
				"kmsAtFueling", "drivingStyle", "fuelStation" }, "idVehicle=?",
				new String[] { String.valueOf(rowId) }, null, null,
				"kmsAtFueling");
	}

	public int getFuelingCourseTypeCity(long rowId) {
		Cursor cursor = mDb.query("Fueling", new String[] { "courseTypeCity" },
				"_id=?", new String[] { String.valueOf(rowId) }, null, null,
				null);
		cursor.moveToFirst();
		return cursor.getInt(0);
	}

	public int getFuelingCourseTypeRoad(long rowId) {
		Cursor cursor = mDb.query("Fueling", new String[] { "courseTypeRoad" },
				"_id=?", new String[] { String.valueOf(rowId) }, null, null,
				null);
		cursor.moveToFirst();
		return cursor.getInt(0);
	}

	public int getFuelingCourseTypeFreeway(long rowId) {
		Cursor cursor = mDb.query("Fueling",
				new String[] { "courseTypeFreeway" }, "_id=?",
				new String[] { String.valueOf(rowId) }, null, null, null);
		cursor.moveToFirst();
		return cursor.getInt(0);
	}

	public float getAverageFuelConsumptionByVehicleID(long rowId) {
		Cursor result = mDb.query("Fueling", new String[] {
				"MAX(kmsAtFueling)", "SUM(quantity)" }, "idVehicle=?",
				new String[] { String.valueOf(rowId) }, null, null, null);
		result.moveToFirst();
		int totalKms = result.getInt(0) - this.getMinKms(rowId);
		double totalLitres = result.getDouble(1);

		return (float) ((totalLitres * 100) / totalKms);
	}

	public int getPreviousKms(long idFueling, int idVehicle) {
		Cursor result = mDb
				.query("Fueling",
						new String[] { "kmsAtFueling" },
						"kmsAtFueling < (SELECT kmsAtFueling from Fueling WHERE _id=?)",
						new String[] { String.valueOf(idFueling) }, null, null,
						"kmsAtFueling", "1");
		// if this is the lowest km value, use the first one (when added
		// vehicle)
		if (result.getCount() == 0)
			return getMinKms(idVehicle);
		result.moveToFirst();
		int resultInt = result.getInt(0);
		return resultInt;
	}

	public float getAverageFuelConsumptionByFuelingID(long rowId) {
		Cursor result = mDb.query("Fueling", new String[] {
				"kmsAtFueling, idVehicle", "quantity" }, "_id=?",
				new String[] { String.valueOf(rowId) }, null, null, null);
		result.moveToFirst();
		int currentKms = result.getInt(0);
		int idVehicle = result.getInt(1);
		int prevKms = getPreviousKms(rowId, idVehicle);
		int totalKms = currentKms - prevKms;
		double totalLitres = result.getDouble(2);

		return (float) ((totalLitres * 100) / totalKms);
	}

	public float getAverageFuelConsumptionByDate(long rowId, int month, int year) {
		Cursor result = mDb
				.query("Fueling",
						new String[] { "MAX(kmsAtFueling)", "SUM(quantity)" },
						"idVehicle=? AND strftime('%m', `date`) = ? AND strftime('%Y', `date`) = ?",
						new String[] { String.valueOf(rowId),
								String.format("%02d", month),
								String.valueOf(year) }, null, null, null);
		result.moveToFirst();
		// Have to subtract to previous fueling kms
		Cursor firstFuelingCursor = mDb
				.query("Fueling",
						new String[] { "_id" },
						"idVehicle=? AND strftime('%m', `date`) = ? AND strftime('%Y', `date`) = ?",
						new String[] { String.valueOf(rowId),
								String.format("%02d", month),
								String.valueOf(year) }, null, null, null, "1");
		firstFuelingCursor.moveToFirst();
		if(result.getCount()==0 || firstFuelingCursor.getCount()==0)
			return 0;
		int firstFuelingID = firstFuelingCursor.getInt(0);
		int totalKms = result.getInt(0)
				- getPreviousKms(firstFuelingID, (int) rowId);
		double totalLitres = result.getDouble(1);

		return (float) ((totalLitres * 100) / totalKms);
	}

}
