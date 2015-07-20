package mars.mp3player;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Queue;

import mars.lrc.LrcProcessor;
import mars.model.Mp3Info;
import mars.mp3player.service.PlayerService;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class PlayerActivity extends Activity {
	ImageButton beginButton = null;
	ImageButton pauseButton = null;
	ImageButton stopButton = null;
	Button lrcButton = null;
	private TextView lrcTextView = null;
	private Mp3Info mp3Info = null;
	private ArrayList<Queue> queue = null;
	private Handler handler = new Handler();
	private UdateTimeCallback updateTimeCallback = null;
	private long begin = 0;
	private long nextTimeMill = 0;
	private long currentTimeMill = 0;
	private String message = null;
	private long pauseTimeMills = 0;
	private boolean isPlaying = false;
	Intent intent = new Intent();
	private boolean isdisplaycrc = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player);
		intent = getIntent();
		mp3Info = (Mp3Info) intent.getSerializableExtra("mp3Info");
		Log.d("PlayerActivity", "mp3Info = " + mp3Info);
		beginButton = (ImageButton) findViewById(R.id.begin);
		pauseButton = (ImageButton) findViewById(R.id.pause);
		stopButton = (ImageButton) findViewById(R.id.stop);
		lrcButton = (Button) findViewById(R.id.lrc);
		beginButton.setOnClickListener(new BeginButtonListener());
		pauseButton.setOnClickListener(new PauseButtonListener());
		stopButton.setOnClickListener(new StopButtonListener());
		lrcButton.setOnClickListener(new lrcButtonListener());
		lrcTextView = (TextView) findViewById(R.id.lrcTextView);
	}

	class BeginButtonListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			intent.setClass(PlayerActivity.this, PlayerService.class);
			intent.putExtra("MSG", AppConstant.PlayerMsg.PLAY_MSG);	
			if (isdisplaycrc) {
				prepareLrc(mp3Info.getLrcName());
				begin = System.currentTimeMillis();
				handler.postDelayed(updateTimeCallback, 5);
			}
			startService(intent);
			isPlaying = true;
		}

	}

	private void prepareLrc(String lrcName) {
		try {
			Log.d("prepareLrc", "lrcName =" + lrcName);
			InputStream inputStream = new FileInputStream(Environment
					.getExternalStorageDirectory().getAbsolutePath()
					+ File.separator + "mp3" + File.separator + lrcName);	
			LrcProcessor lrcProcessor = new LrcProcessor();
			queue = lrcProcessor.process(inputStream);
			updateTimeCallback = new UdateTimeCallback(queue);
			begin = 0;
			currentTimeMill = 0;
			nextTimeMill = 0;
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
			System.out.println("meiy");
		}
	}

	class PauseButtonListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			intent.setClass(PlayerActivity.this, PlayerService.class);
			intent.putExtra("MSG", AppConstant.PlayerMsg.PAUSE_MSG);
			startService(intent);
			if (isdisplaycrc) {
				if (isPlaying) {
					handler.removeCallbacks(updateTimeCallback);
					pauseTimeMills = System.currentTimeMillis();
				} else {
					handler.postDelayed(updateTimeCallback, 5);
					begin = System.currentTimeMillis() - pauseTimeMills + begin;
				}
			}
			isPlaying = isPlaying ? false : true;

		}

	}

	class StopButtonListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			intent.setClass(PlayerActivity.this, PlayerService.class);
			intent.putExtra("MSG", AppConstant.PlayerMsg.STOP_MSG);
			startService(intent);
			if (isdisplaycrc) {
				handler.removeCallbacks(updateTimeCallback);
			}
			
		}
	}

	class lrcButtonListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			isdisplaycrc = true;
		}
	}

	class UdateTimeCallback implements Runnable {
		Queue time = null;
		Queue messages = null;

		public UdateTimeCallback(ArrayList<Queue> queue) {
			time = queue.get(1);
			messages = queue.get(1);
		}

		@Override
		public void run() {
			long offset = System.currentTimeMillis() - begin;
			if (currentTimeMill == 0) {
				nextTimeMill = (Long) time.poll();
				message = (String) messages.poll();
			}
			if (offset >= nextTimeMill) {
				lrcTextView.setText(message);
				nextTimeMill = (Long) time.poll();
				message = (String) messages.poll();
			}
			currentTimeMill = currentTimeMill + 10;
			handler.postDelayed(updateTimeCallback, 10);
		}

	}

}
