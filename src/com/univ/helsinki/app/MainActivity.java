package com.univ.helsinki.app;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
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

import com.univ.helsinki.app.activities.AudioDialog;

import com.qualcomm.vuforia.samples.VideoPlayback.app.VideoPlayback.VideoPlayback;

import com.univ.helsinki.app.activities.ViewActivity;
import com.univ.helsinki.app.adapter.RecentActivityAdapter;
import com.univ.helsinki.app.core.Feed;
import com.univ.helsinki.app.db.RecentActivityDataSource;
import com.univ.helsinki.app.db.ResourcePool;
import com.univ.helsinki.app.util.Constant;

public class MainActivity extends Activity {
	
	static final String LOGTAG = "AugmentedApp";
	
	static final int ARREQCODE = 1234;

	private EditText feildTitle;
	private EditText feildContent;
	private Button btnRecent;

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;

	private String[] mSidePanelTitles;

	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	
	private ListView mListview;
	private RecentActivityAdapter mAdapter;
	
	private MediaPlayer mPlayer;
	
	private boolean isBootstrap = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		initSideDrawer(savedInstanceState);


		mListview = (ListView) findViewById(R.id.listview);
		mListview.setVisibility(View.INVISIBLE);

		ResourcePool.getInstance().inti(this);
		List<Feed> mFeedList = ResourcePool.getInstance().getAllFeed();
		
		if(mFeedList.size() > 0){
			mListview.setVisibility(View.VISIBLE);
			findViewById(R.id.emptystub).setVisibility(View.GONE);
		}
		
		mAdapter = new RecentActivityAdapter(MainActivity.this);
		
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
		
		if( position == 2 ){
			Log.d(LOGTAG, "AR scan_button selected");
	       	Intent intent = new Intent();
	       	intent.setClassName("com.univ.helsinki.app","com.qualcomm.vuforia.samples.VideoPlayback.app.VideoPlayback.VideoPlayback");
	       	startActivityForResult(intent, ARREQCODE);
		}else if( position == 1 ){
			IntentIntegrator scanIntegrator = new IntentIntegrator(MainActivity.this);
			scanIntegrator.initiateScan();
		}else if( position == 0 ){
			if(!isBootstrap){
				IntentIntegrator scanIntegrator = new IntentIntegrator(MainActivity.this);
				scanIntegrator.initiateScan();
			}
		}
		
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
			shareIt(ResourcePool.getInstance().getAllFeed().get(info.position).getTitle(), 
					ResourcePool.getInstance().getAllFeed().get(info.position).getContent());
		}else if (menuItemName.equalsIgnoreCase("delete")) {
			
			ResourcePool.getInstance().removeFeed(info.position);
			 
			mAdapter.notifyDataChanged();
			
		}else if (menuItemName.contains("More")) {
			String inURL = "https://en.wikipedia.org/wiki/" + ResourcePool.getInstance().getAllFeed().get(info.position).getTitle();
			    
			Intent browse = new Intent( Intent.ACTION_VIEW , Uri.parse( inURL ) );
			startActivity( browse );
		}else if (menuItemName.contains("Play")) {
			
			/*int resId = R.raw.a;
			
			// Release any resources from previous MediaPlayer
			if (mPlayer !=null) {
				mPlayer.release();
			}
			// Create a new MediaPlayer to play this sound
			mPlayer = MediaPlayer.create(this, resId);
			mPlayer.start();*/
			
			initDownloader();
			
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
		
		Log.i("DEBUG", "intent : " + intent);
		
		switch (requestCode) {
			case ARREQCODE:
				
				if(intent!= null ){
					
					String title = intent.getStringExtra(VideoPlayback.EXTRAS_AR_TITLE);
					String content = intent.getStringExtra(VideoPlayback.EXTRAS_AR_CONTENT);

					ResourcePool.getInstance().addFeed(0, ResourcePool.getInstance().createFeed(title, content));
					mAdapter.notifyDataChanged();
					
					if(ResourcePool.getInstance().getAllFeed().size() > 0){
						mListview.setVisibility(View.VISIBLE);
						findViewById(R.id.emptystub).setVisibility(View.GONE);
					}
				}
				break;
			
			default:

				IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
				if (scanningResult != null) {

					// we have a result
					String scanContent = scanningResult.getContents();
					String scanFormat = scanningResult.getFormatName();

					if ((scanFormat != null) && scanFormat.trim().length() > 0) {
						
						int indexOfComma = scanContent.indexOf(',');
						
						Feed feed = ResourcePool.getInstance().createFeed(scanContent.substring(0,indexOfComma), scanContent);
						ResourcePool.getInstance().addFeed(0, feed );
						mAdapter.notifyDataChanged();
						
						if(ResourcePool.getInstance().getAllFeed().size() > 0){
							mListview.setVisibility(View.VISIBLE);
							findViewById(R.id.emptystub).setVisibility(View.GONE);
						}
					}  
				} else {
					Toast toast = Toast.makeText(getApplicationContext(), "No scan data received!", Toast.LENGTH_SHORT);
					toast.show();
				}
				
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
			IntentIntegrator scanIntegrator = new IntentIntegrator(MainActivity.this);
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
		mAdapter.notifyDataChanged();
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		if(null != mPlayer){
			mPlayer.release(); 
		}
		ResourcePool.getInstance().destroy();
		super.onDestroy();
	}
	
	// declare the dialog as a member field of your activity
	private ProgressDialog mProgressDialog;
	
	private void initDownloader(){
		
		// instantiate it within the onCreate method
		mProgressDialog = new ProgressDialog(MainActivity.this);
		mProgressDialog.setMessage("Downloading Audio");
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setCancelable(true);
		
		String url = "";

		// execute this when the downloader must be fired
		final DownloadTask downloadTask = new DownloadTask(MainActivity.this);
		downloadTask.execute(Constant.AUDIO_URL[0]);

		mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
		    @Override
		    public void onCancel(DialogInterface dialog) {
		        downloadTask.cancel(true);
		    }
		});
	}
	
	// usually, subclasses of AsyncTask are declared inside the activity class.
	// that way, you can easily modify the UI thread from here
	private class DownloadTask extends AsyncTask<String, Integer, String> {

	    private Context context;
	    private PowerManager.WakeLock mWakeLock;

	    public DownloadTask(Context context) {
	        this.context = context;
	    }

	    @Override
	    protected String doInBackground(String... sUrl) {
	        InputStream input = null;
	        OutputStream output = null;
	        HttpURLConnection connection = null;
	        try {
	            URL url = new URL(sUrl[0]);
	            connection = (HttpURLConnection) url.openConnection();
	            connection.connect();

	            // expect HTTP 200 OK, so we don't mistakenly save error report
	            // instead of the file
	            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
	                return "Server returned HTTP " + connection.getResponseCode()
	                        + " " + connection.getResponseMessage();
	            }

	            // this will be useful to display download percentage
	            // might be -1: server did not report the length
	            int fileLength = connection.getContentLength();

	            // download the file
	            input = connection.getInputStream();
	            output = new FileOutputStream("/sdcard/temp.mp3");

	            byte data[] = new byte[4096];
	            long total = 0;
	            int count;
	            while ((count = input.read(data)) != -1) {
	                // allow canceling with back button
	                if (isCancelled()) {
	                    input.close();
	                    return null;
	                }
	                total += count;
	                // publishing the progress....
	                if (fileLength > 0) // only if total length is known
	                    publishProgress((int) (total * 100 / fileLength));
	                output.write(data, 0, count);
	            }
	        } catch (Exception e) {
	            return e.toString();
	        } finally {
	            try {
	                if (output != null)
	                    output.close();
	                if (input != null)
	                    input.close();
	            } catch (IOException ignored) {
	            }

	            if (connection != null)
	                connection.disconnect();
	        }
	        return null;
	    }
	    
	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        // take CPU lock to prevent CPU from going off if the user 
	        // presses the power button during download
	        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
	        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
	             getClass().getName());
	        mWakeLock.acquire();
	        mProgressDialog.show();
	    }

	    @Override
	    protected void onProgressUpdate(Integer... progress) {
	        super.onProgressUpdate(progress);
	        // if we get here, length is known, now set indeterminate to false
	        mProgressDialog.setIndeterminate(false);
	        mProgressDialog.setMax(100);
	        mProgressDialog.setProgress(progress[0]);
	    }

	    @Override
	    protected void onPostExecute(String result) {
	        mWakeLock.release();
	        mProgressDialog.dismiss();
	        if (result != null)
	            Toast.makeText(context,"Download error: "+result, Toast.LENGTH_LONG).show();
	        else{
	            Toast.makeText(context,"File downloaded", Toast.LENGTH_SHORT).show();
	            
	            /*int resId = R.raw.a;
				
				// Release any resources from previous MediaPlayer
				if (mPlayer !=null) {
					mPlayer.release();
				}
				// Create a new MediaPlayer to play this sound
				mPlayer = MediaPlayer.create(this, resId);
				
				mPlayer.start();*/
	            
	            /*mPlayer = new MediaPlayer();
	            
	            try {
	            	mPlayer.setDataSource("/sdcard/temp.mp3");
	            	mPlayer.prepare();
	            	mPlayer.start();
	            } catch (Exception e) {
	                e.printStackTrace();
	            }*/
	            
	            AudioDialog aDialog = new AudioDialog(MainActivity.this, "/sdcard/temp.mp3");
	    		aDialog.setCanceledOnTouchOutside(true) ;
	    		aDialog.setCancelable(true);
	    		aDialog.show();
	        }
	    }
	}

}
