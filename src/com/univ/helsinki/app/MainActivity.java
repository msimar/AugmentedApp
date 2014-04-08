package com.univ.helsinki.app;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.univ.helsinki.app.activities.ViewActivity;
import com.univ.helsinki.app.adapter.RecentActivityAdapter;
import com.univ.helsinki.app.core.Feed;
import com.univ.helsinki.app.db.RecentActivityDataSource;

public class MainActivity extends Activity {

	private EditText feildTitle;
	private EditText feildContent;
	private Button btnRecent;

	private RecentActivityDataSource mDatasource;

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;

	private String[] mSidePanelTitles;

	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	
	private ListView mListview;
	private RecentActivityAdapter mAdapter;
	
	private List<Feed> mFeedList;
	
	private MediaPlayer mPlayer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		initSideDrawer(savedInstanceState);

		mDatasource = new RecentActivityDataSource(MainActivity.this);
		mDatasource.open();

		mListview = (ListView) findViewById(R.id.listview);
		mListview.setVisibility(View.INVISIBLE);

		mFeedList = mDatasource.getAllFeeds();
		
		if(mFeedList.size() > 0){
			mListview.setVisibility(View.VISIBLE);
			findViewById(R.id.emptystub).setVisibility(View.GONE);
		}
		
		mAdapter = new RecentActivityAdapter(MainActivity.this, mFeedList);
		
		registerForContextMenu(mListview);
		
		// Show contextview on item click
		mListview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
				// TODO Auto-generated method stub
				//adapter.showContextMenuForChild(view);
				Intent intent = new Intent(MainActivity.this, ViewActivity.class);
				intent.putExtra(ViewActivity.EXTRAS_ROW_ID, position-1);
				startActivity(intent);
			}
		});
		
		LayoutInflater inflater = getLayoutInflater();
		ViewGroup header = (ViewGroup)inflater.inflate(R.layout.listview_header, mListview, false);
		mListview.addHeaderView(header, null, false);
		
		mListview.setAdapter(mAdapter);
	}

	private void initSideDrawer(Bundle savedInstanceState) {

		mTitle = mDrawerTitle = getTitle();

		mSidePanelTitles = getResources().getStringArray( R.array.sidepanel_array);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		// set a custom shadow that overlays the main content when the drawer
		// opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		// set up the drawer's list view with items and click listener
		mDrawerList.setAdapter(new ArrayAdapter<String>(this,
				R.layout.drawer_list_item, mSidePanelTitles));
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		// enable ActionBar app icon to behave as action to toggle nav drawer
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description for accessibility */
		R.string.drawer_close /* "close drawer" description for accessibility */
		) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			selectItem(0);
		}

		// Set the drawer toggle as the DrawerListener
		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}

	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView parent, View view, int position, long id) {
			selectItem(position);
		}
	}

	/** Swaps fragments in the main content view */
	private void selectItem(int position) {
		// Highlight the selected item, update the title, and close the drawer
		mDrawerList.setItemChecked(position, true);
		//setTitle(mSidePanelTitles[position]);
		mDrawerLayout.closeDrawer(mDrawerList);
		
		// Do some Action for events
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.listview) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

			String[] menuItems = getResources().getStringArray(R.array.listitem_menu_array);
			for (int i = 0; i < menuItems.length; i++) {
				menu.add(Menu.NONE, i, i, menuItems[i]);
			}
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

		int menuItemIndex = item.getItemId();

		String[] menuItems = getResources().getStringArray(R.array.listitem_menu_array);

		String menuItemName = menuItems[menuItemIndex];

		if (menuItemName.equalsIgnoreCase("Share")) {
			shareIt(mFeedList.get(info.position).getTitle(), 
					mFeedList.get(info.position).getContent());
		}else if (menuItemName.equalsIgnoreCase("delete")) {
			
			long id = mFeedList.get(info.position).getId();
			
			if(mDatasource != null){
				mDatasource.delete(id);
			}
			
			mFeedList.remove(info.position);
			mAdapter.notifyDataSetChanged();
			
		}else if (menuItemName.contains("More")) {
			String inURL = "https://en.wikipedia.org/wiki/" + mFeedList.get(info.position).getTitle();
			    
			Intent browse = new Intent( Intent.ACTION_VIEW , Uri.parse( inURL ) );
			startActivity( browse );
		}else if (menuItemName.contains("Play")) {
			
			int resId = R.raw.a;
			
			// Release any resources from previous MediaPlayer
			if (mPlayer !=null) {
				mPlayer.release();
			}
			// Create a new MediaPlayer to play this sound
			mPlayer = MediaPlayer.create(this, resId);
			mPlayer.start();
			
		}

		return true;
	}

	private void shareIt(String title, String content) {
		// sharing implementation here
		Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
		sharingIntent.setType("text/plain");
		sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Emrald AR App - " + title);
		sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, content);
		startActivity(Intent.createChooser(sharingIntent, "Share via"));
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		// TODO Auto-generated method stub
		IntentResult scanningResult = IntentIntegrator.parseActivityResult(
				requestCode, resultCode, intent);
		if (scanningResult != null) {

			// we have a result
			String scanContent = scanningResult.getContents();
			String scanFormat = scanningResult.getFormatName();

			if ((scanFormat != null) && scanFormat.trim().length() > 0) {
				
				if(mDatasource == null)
					mDatasource = new RecentActivityDataSource(MainActivity.this);
				
				mDatasource.open();

				mFeedList.add(0,mDatasource.createFeed(scanFormat, scanContent));
				
				mAdapter.notifyDataSetChanged();
				
				if(mFeedList.size() > 0){
					mListview.setVisibility(View.VISIBLE);
					findViewById(R.id.emptystub).setVisibility(View.GONE);
				}
			}  
		} else {
			Toast toast = Toast.makeText(getApplicationContext(), "No scan data received!", Toast.LENGTH_SHORT);
			toast.show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Pass the event to ActionBarDrawerToggle, if it returns
		// true, then it has handled the app icon touch event
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_scan_qr:
			IntentIntegrator scanIntegrator = new IntentIntegrator(
					MainActivity.this);
			scanIntegrator.initiateScan();
			return true;
		case R.id.action_settings:
			// openSettings();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the nav drawer is open, hide action items related to the content
		// view
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		// menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	protected void onResume() {
		if (mDatasource != null)
			mDatasource.open();
		super.onResume();
	}

	@Override
	protected void onPause() {
		if (mDatasource != null)
			mDatasource.close();
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		if(null != mPlayer){
			mPlayer.release();
		}
		super.onDestroy();
	}

}
