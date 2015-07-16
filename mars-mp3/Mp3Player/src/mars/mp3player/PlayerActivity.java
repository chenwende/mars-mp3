package mars.mp3player;

import java.io.File;

import mars.model.Mp3Info;
import mars.mp3player.service.PlayerService;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class PlayerActivity extends Activity {
	ImageButton beginButton = null;
	ImageButton pauseButton = null;
	ImageButton stopButton = null;
	private Mp3Info mp3Info = null;
	Intent intent = new Intent();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player);
		intent = getIntent();
		//mp3Info = (Mp3Info) intent.getSerializableExtra("mp3Info");
		beginButton = (ImageButton) findViewById(R.id.begin);
		pauseButton = (ImageButton) findViewById(R.id.pause);
		stopButton = (ImageButton) findViewById(R.id.stop);
		beginButton.setOnClickListener(new BeginButtonListener());
		pauseButton.setOnClickListener(new PauseButtonListener());
		stopButton.setOnClickListener(new StopButtonListener());
	}

	class BeginButtonListener implements OnClickListener {

		@Override
		public void onClick(View v) {			
			intent.setClass(PlayerActivity.this,PlayerService.class);		
			intent.putExtra("MSG", AppConstant.PlayerMsg.PLAY_MSG);
			startService(intent);
		}

	}

	class PauseButtonListener implements OnClickListener {

		@Override
		public void onClick(View v) {		
			intent.setClass(PlayerActivity.this,PlayerService.class);
			intent.putExtra("MSG", AppConstant.PlayerMsg.PAUSE_MSG);
			startService(intent);
		}

	}

	class StopButtonListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			intent.setClass(PlayerActivity.this,PlayerService.class);
			intent.putExtra("MSG", AppConstant.PlayerMsg.STOP_MSG);
			startService(intent);
		}
	}

}
