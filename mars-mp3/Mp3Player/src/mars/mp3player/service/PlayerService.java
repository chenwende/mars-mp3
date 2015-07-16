package mars.mp3player.service;

import java.io.File;

import mars.model.Mp3Info;
import mars.mp3player.AppConstant;
import mars.mp3player.PlayerActivity;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

public class PlayerService extends Service {
	MediaPlayer mediaPlayer = null;
	private boolean isPlaying = false;
	private boolean isPause = false;
	private boolean isReleased = false;

	@Override
	public IBinder onBind(Intent arg0) {		
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Mp3Info mp3Info = (Mp3Info) intent.getSerializableExtra("mp3Info");
		int MSG = intent.getIntExtra("MSG", 0);
		if (MSG == AppConstant.PlayerMsg.PLAY_MSG) {
			if (mp3Info != null) {
				Play(mp3Info);
			}
		} else {
			if (MSG == AppConstant.PlayerMsg.PAUSE_MSG) {
				pause();
			} else if (MSG == AppConstant.PlayerMsg.STOP_MSG) {
				stop();
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}

	private void stop() {
		if (mediaPlayer != null) {
			if (isPlaying) {
				if (!isReleased) {
					mediaPlayer.stop();
					mediaPlayer.release();
					isReleased = true;
				}
				isPlaying = false;
			}
		}

	}

	private void pause() {
		Log.d("pause", "mediaPlayer = " + mediaPlayer.toString());
		if (mediaPlayer != null) {
			if (!isReleased) {
				if (!isPause) {
					//Log.d("pause1", "mediaPlayer = " + mediaPlayer.toString());
					mediaPlayer.pause();
					isPause = true;
					isPlaying = true;
				} else {
					//Log.d("pause2", "mediaPlayer = " + mediaPlayer.toString());
					mediaPlayer.start();
					isPause = false;
				}
			}
		}

	}

	private void Play(Mp3Info mp3Info) {
		String path = getMp3Path(mp3Info);
		mediaPlayer = MediaPlayer.create(this, Uri.parse("file://" + path));
		mediaPlayer.setLooping(false);
		mediaPlayer.start();
		isPlaying = true;
		isReleased = false;
	}

	private String getMp3Path(Mp3Info mp3Info) {
		String SDCardRoot = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		String path = SDCardRoot + File.separator + "mp3" + File.separator
				+ mp3Info.getMp3Name();
		return path;
	}

}
