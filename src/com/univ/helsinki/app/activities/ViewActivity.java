package com.univ.helsinki.app.activities;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.univ.helsinki.app.MainActivity;
import com.univ.helsinki.app.R;
import com.univ.helsinki.app.core.Feed;
import com.univ.helsinki.app.db.RecentActivityDataSource;
import com.univ.helsinki.app.db.ResourcePool;
import com.univ.helsinki.app.util.Constant;

public class ViewActivity extends Activity implements OnClickListener {

	public static final String EXTRAS_ROW_ID = "row_id";

	private TextView tvTitle;
	private TextView tvContent;
	private TextView tvTimestamp;
	
	private int mListItemIndex;
	
	private MediaPlayer mPlayer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_view);
		
		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		tvTitle = (TextView) findViewById(R.id.title);
		tvContent = (TextView) findViewById(R.id.content);
		tvTimestamp = (TextView) findViewById(R.id.timestamp);
		
		View viewShare = findViewById(R.id.viewShare);
		View viewDelete = findViewById(R.id.viewDelete);
		View viewPlay = findViewById(R.id.viewPlay);
		View viewInfo = findViewById(R.id.viewInfo);
		
		viewShare.setOnClickListener(this);
		viewDelete.setOnClickListener(this);
		viewPlay.setOnClickListener(this);
		viewInfo.setOnClickListener(this);

		Bundle extras = getIntent().getExtras();

		if (extras == null) {
			finish();
		} else {
			mListItemIndex = extras.getInt(EXTRAS_ROW_ID);

			ResourcePool.getInstance().inti(this);
			List<Feed> mFeedList = ResourcePool.getInstance().getAllFeed();
			
//			int indexOfComma = mFeedList.get(mListItemIndex).getContent().indexOf(',');
			
//			if( indexOfComma != -1 ){
//				tvTitle.setText(mFeedList.get(mListItemIndex).getContent().substring(0,indexOfComma));
//				tvContent.setText(mFeedList.get(mListItemIndex).getContent());
//				tvTimestamp.setText( " Visited on " + mFeedList.get(mListItemIndex).getUpdatedTimestamp());
//			}else
			{
				tvTitle.setText(mFeedList.get(mListItemIndex).getTitle());
				tvContent.setText(mFeedList.get(mListItemIndex).getContent());
				tvTimestamp.setText( " Visited on " + mFeedList.get(mListItemIndex).getUpdatedTimestamp());
			}
		}
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	@Override
	protected void onResume() {
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
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.viewShare:{
			shareIt(tvTitle.getText().toString(), tvContent.getText().toString());
		}break;
		case R.id.viewDelete:{
			
			ResourcePool.getInstance().removeFeed(mListItemIndex);
			 
			this.finish();
			
		}break;
		case R.id.viewPlay:{
//			int resId = R.raw.a;
//			
//			// Release any resources from previous MediaPlayer
//			if (mPlayer !=null) {
//				mPlayer.release();
//			}
//			// Create a new MediaPlayer to play this sound
//			mPlayer = MediaPlayer.create(this, resId);
//			mPlayer.start();
			
			initDownloader();
			
		}break;
		case R.id.viewInfo:{
			String inURL = "https://en.wikipedia.org/wiki/" + tvTitle.getText().toString();
		    
			Intent browse = new Intent( Intent.ACTION_VIEW , Uri.parse( inURL ) );
			startActivity( browse );
		}break;

		default:
			break;
		}
	}
	// declare the dialog as a member field of your activity
	private ProgressDialog mProgressDialog;
	
	private void initDownloader(){
		
		// instantiate it within the onCreate method
		mProgressDialog = new ProgressDialog(ViewActivity.this);
		mProgressDialog.setMessage("Downloading Audio");
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setCancelable(true);
		
		String url = "";

		// execute this when the downloader must be fired
		final DownloadTask downloadTask = new DownloadTask(ViewActivity.this);
		downloadTask.execute(Constant.AUDIO_URL[0]);

		mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
		    @Override
		    public void onCancel(DialogInterface dialog) {
		        downloadTask.cancel(true);
		    }
		});
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
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    // Respond to the action bar's Up/Home button
	    case android.R.id.home:
	        NavUtils.navigateUpFromSameTask(this);
	        return true;
	    }
	    return super.onOptionsItemSelected(item);
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
	            
	            AudioDialog aDialog = new AudioDialog(ViewActivity.this, "/sdcard/temp.mp3");
	    		aDialog.setCanceledOnTouchOutside(true) ;
	    		aDialog.setCancelable(true);
	    		aDialog.show();
	        }
	    }
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
	}
}
