package com.univ.helsinki.app.activities;

import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.univ.helsinki.app.R;
import com.univ.helsinki.app.core.Feed;
import com.univ.helsinki.app.db.RecentActivityDataSource;

public class ViewActivity extends Activity implements OnClickListener {

	public static final String EXTRAS_ROW_ID = "row_id";

	private RecentActivityDataSource mDatasource;
	
	private List<Feed> mFeedList;
	
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

			mDatasource = new RecentActivityDataSource(ViewActivity.this);
			mDatasource.open();

			mFeedList = mDatasource.getAllFeeds();
			
			tvTitle.setText(mFeedList.get(mListItemIndex).getTitle());
			tvContent.setText(mFeedList.get(mListItemIndex).getContent());
			tvTimestamp.setText(mFeedList.get(mListItemIndex).getUpdatedTimestamp());
		}
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.viewShare:{
			shareIt(tvTitle.getText().toString(), tvContent.getText().toString());
		}break;
		case R.id.viewDelete:{
			long id = mFeedList.get(mListItemIndex).getId();
			
			if(mDatasource != null){
				mDatasource.delete(id);
			}
			
			mFeedList.remove(mListItemIndex);
			
			this.finish();
			
		}break;
		case R.id.viewPlay:{
			int resId = R.raw.a;
			
			// Release any resources from previous MediaPlayer
			if (mPlayer !=null) {
				mPlayer.release();
			}
			// Create a new MediaPlayer to play this sound
			mPlayer = MediaPlayer.create(this, resId);
			mPlayer.start();
		}break;
		case R.id.viewInfo:{
			String inURL = "https://en.wikipedia.org/wiki/" + mFeedList.get(mListItemIndex).getTitle();
		    
			Intent browse = new Intent( Intent.ACTION_VIEW , Uri.parse( inURL ) );
			startActivity( browse );
		}break;

		default:
			break;
		}
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
}
