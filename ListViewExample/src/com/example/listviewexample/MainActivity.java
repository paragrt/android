package com.example.listviewexample;

import java.util.Arrays;
import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Prabu
 *
 */
public class MainActivity extends ListActivity {

	final List<String> elements = Arrays.asList("Element 1", "Element 2", "Element 3",
			"Element 4", "Element 5");

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_main);
		ListView listView = getListView();
		listView.setTextFilterEnabled(true);	
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.activity_main,elements);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Toast.makeText(getApplicationContext(),
						((TextView) view).getText(), Toast.LENGTH_SHORT).show();
			}
		});
	}
}
