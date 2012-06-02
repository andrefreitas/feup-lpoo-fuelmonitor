package org.feup.fuelmonitor;
import java.util.ArrayList;
import java.util.Calendar;

import org.graphview.*;

import com.actionbarsherlock.app.SherlockActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

/**
 * GraphViewDemo creates some dummy data to demonstrate the GraphView component.
 * @author Arno den Hond
 *
 */
public class Stats extends SherlockActivity{
	private long mVehicleID;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		float[] values = new float[] { 2.0f,1.5f, 2.5f, 1.0f , 3.0f };
		String[] months = new String[] { "Jan.", "Fev.", "Mar.", "Abr.", "Mai.","Jun.","Jul.","Ago.","Set.","Out.","Nov.","Dez."};
		
		// Calendar class for fetching the year and the year months until this date
		Calendar c = Calendar.getInstance(); 
		int year= c.get(Calendar.YEAR);
		int month=c.get(Calendar.MONTH)+1;
		
		// Add the months
		ArrayList<String> listMonths = new ArrayList<String>();
		for(int i=0; i<month; i++){
			listMonths.add(months[i]);
		}
		String[] actualMonths=new String[month];
		listMonths.toArray(actualMonths);
		
		// Add the graphview as a view in the RelativeLayout inside the activity
		setContentView(R.layout.stats);
		RelativeLayout layoutGraph = (RelativeLayout) findViewById(R.id.stats_GraphLayout);
		GraphView graphView = new GraphView(this, values, "Consumos de "+year,actualMonths, null, GraphView.BAR);
		layoutGraph.addView(graphView);
	}
}