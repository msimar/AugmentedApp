package com.univ.helsinki.app.activities;

import java.io.IOException;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.MediaController;
import android.widget.Toast;

import com.univ.helsinki.app.R;

public class AudioDialog extends Dialog implements OnPreparedListener,
		MediaController.MediaPlayerControl, DialogInterface.OnDismissListener {
	
	private static final String TAG = AudioDialog.class.getSimpleName();

	private MediaPlayer mediaPlayer;
	private MediaController mediaController;

	private String audioFile;

	private Handler handler = new Handler();

	private Context mContext;

	public AudioDialog(Context context, String track) {
		super(context);
		this.audioFile = track;
		this.mContext = context;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_audio_player);

		setTitle("Playing " + audioFile);

		mediaPlayer = new MediaPlayer();
		mediaPlayer.setOnPreparedListener(this);

		mediaController = new MediaController(mContext){
			@Override
		    public void hide() {
				//Do not hide.
				super.show();
		    }
		};

		try {
			mediaPlayer.setDataSource(audioFile);
			mediaPlayer.prepare();
			mediaPlayer.start();
		} catch (IOException e) {
			Log.e(TAG, "Could not open file " + audioFile + " for playback.", e);
		}

	}

	@Override
	protected void onStop() {
		// mediaController.hide();
		if(mediaPlayer != null) {
            if(mediaPlayer.isPlaying())
            {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaController.removeAllViews();
            mediaPlayer = null;
        }
		Toast.makeText(mContext, "onStop", Toast.LENGTH_SHORT).show();
		super.onStop();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// the MediaController will hide after 3 seconds - tap the screen to
		// make it appear again
		mediaController.show();
		return false;
	}

	// --MediaPlayerControl
	// methods----------------------------------------------------
	@Override
	public void start() {
		if(mediaPlayer != null)
			mediaPlayer.start();
	}

	@Override
	public void pause() {
		if(mediaPlayer != null)
			mediaPlayer.pause();
	}

	@Override
	public int getDuration() {
		return mediaPlayer != null ? mediaPlayer.getDuration() : 0;
	}

	@Override
	public int getCurrentPosition() {
		return mediaPlayer != null ? mediaPlayer.getCurrentPosition() : 0;
	}

	@Override
	public void seekTo(int i) {
		if(mediaPlayer != null)
			mediaPlayer.seekTo(i);
	}

	@Override
	public boolean isPlaying() {
		return mediaPlayer != null ? mediaPlayer.isPlaying() : false;
	}

	@Override
	public int getBufferPercentage() {
		return 0;
	}

	@Override
	public boolean canPause() {
		return true;
	}

	@Override
	public boolean canSeekBackward() {
		return true;
	}

	@Override
	public boolean canSeekForward() {
		return true;
	}

	// --------------------------------------------------------------------------------

	public void onPrepared(MediaPlayer mediaPlayer) {
		Log.d(TAG, "onPrepared");
		mediaController.setMediaPlayer(this);
		mediaController.setAnchorView(findViewById(R.id.main_audio_view));

		handler.post(new Runnable() {
			public void run() {
				mediaController.setEnabled(true);
				mediaController.requestFocus();
				mediaController.show();
			}
		});
	}

	@Override
	public int getAudioSessionId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		// TODO Auto-generated method stub
		if(mediaPlayer != null) {
            if(mediaPlayer.isPlaying())
            {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaController.removeAllViews();
            mediaPlayer = null;
        }
		//Toast.makeText(mContext, "onDismiss", Toast.LENGTH_SHORT).show();
		dialog.dismiss();
	}
}
