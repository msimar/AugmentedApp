package com.univ.helsinki.app.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.univ.helsinki.app.R;
import com.univ.helsinki.app.adapter.RecentActivityAdapter;
import com.univ.helsinki.app.db.RecentActivityDataSource;

public class RecentActivity extends Activity {

	private RecentActivityDataSource mDatasource;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recent);

		mDatasource = new RecentActivityDataSource(this);
		mDatasource.open();

		final ListView listview = (ListView) findViewById(R.id.listview);

		listview.setAdapter(
				new RecentActivityAdapter(RecentActivity.this));
		
		registerForContextMenu(listview);
	}
	
	@Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
      if (v.getId()== R.id.listview) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
        
        String[] menuItems = getResources().getStringArray(R.array.listitem_menu_array);
        for (int i = 0; i<menuItems.length; i++) {
          menu.add(Menu.NONE, i, i, menuItems[i]);
        }
      }
    }
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	  AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
	  
	  int menuItemIndex = item.getItemId();
	  
	  String[] menuItems = getResources().getStringArray(R.array.listitem_menu_array);
	  
	  String menuItemName = menuItems[menuItemIndex];
	  
	  // list item name :: info.position

	  return true;
	}

	@Override
	protected void onResume() {
		mDatasource.open();
		super.onResume();
	}

	@Override
	protected void onPause() {
		mDatasource.close();
		super.onPause();
	}
}
