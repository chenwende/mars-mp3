package mars.mp3player.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Queue;

import mars.lrc.LrcProcessor;
import mars.model.Mp3Info;
import mars.mp3player.AppConstant;
import mars.mp3player.PlayerActivity;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class PlayerService extends Service {
	MediaPlayer mediaPlayer = null;
	private boolean isPlaying = false;
	private boolean isPause = false;
	private boolean isReleased = false;
	ArrayList<Queue> queues = null;
	private Handler handler = new Handler();
	private UdateTimeCallback updateTimeCallback = null;
	private long begin = 0;
	private long currentTimeMill = 0;
	private long nextTimeMill = 0;
	private long pauseTimeMills = 0;
	private Mp3Info mp3Info = null;
	private String message = null;

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
				play(mp3Info);
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

	private void play(Mp3Info mp3Info) {
		String path = getMp3Path(mp3Info);
		mediaPlayer = MediaPlayer.create(this, Uri.parse("file://" + path));
		mediaPlayer.setLooping(false);
		mediaPlayer.start();
		prepareLrc(mp3Info.getLrcName());
		begin = System.currentTimeMillis();
		// 延后5毫秒执行UpdateTimeCallback
		handler.postDelayed(updateTimeCallback, 5);
		isPlaying = true;
		isReleased = false;
	}

	private void pause() {
		if (isPlaying) {
			mediaPlayer.pause();
			handler.removeCallbacks(updateTimeCallback);
			// 暂停时取得当前时间
			pauseTimeMills = System.currentTimeMillis();
		} else {
			mediaPlayer.start();
			// 再次播放
			handler.postDelayed(updateTimeCallback, 5);
			// 得到再次开始播放的时间
			begin = System.currentTimeMillis() - pauseTimeMills + begin;
		}
		// 如果当前状态时暂停的话，点击后状态改为播放；如果是播放的话，点击后改为暂停
		isPlaying = isPlaying ? false : true;

	}

	private void stop() {
		if (mediaPlayer != null) {
			if (isPlaying) {
				if (!isReleased) {
					handler.removeCallbacks(updateTimeCallback);
					mediaPlayer.stop();
					mediaPlayer.release();
					isReleased = true;
				}
				isPlaying = false;

			}
		}
	}

	private String getMp3Path(Mp3Info mp3Info) {
		String SDCardRoot = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		String path = SDCardRoot + File.separator + "mp3" + File.separator
				+ mp3Info.getMp3Name();
		Log.d("getMp3Path", "SDCardRoot = " + SDCardRoot);
		return path;
	}

	private void prepareLrc(String lrcName) {
		try {
			Log.d("prepareLrc", "lrcName =" + lrcName);
			InputStream inputStream = new FileInputStream(Environment
					.getExternalStorageDirectory().getAbsolutePath()
					+ File.separator + "mp3" + File.separator + lrcName);
			LrcProcessor lrcProcessor = new LrcProcessor();
			queues = lrcProcessor.process(inputStream);
			updateTimeCallback = new UdateTimeCallback(queues);
			begin = 0;
			currentTimeMill = 0;
			nextTimeMill = 0;
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
			System.out.println("meiy");
		}
	}

	class UdateTimeCallback implements Runnable {
		Queue time = null;
		Queue messages = null;

		public UdateTimeCallback(ArrayList<Queue> queue) {
			time = queue.get(0);
			messages = queue.get(1);
			Log.d("UdateTimeCallback", "time =" + time);
			Log.d("UdateTimeCallback", "messages =" + messages);
		}

		@Override
		public void run() {
			long offset = System.currentTimeMillis() - begin;
			if (currentTimeMill == 0) {
				nextTimeMill = (Long) time.poll();
				message = (String) messages.poll();
			}
			if (offset >= nextTimeMill) {
				Intent intent = new Intent();
				intent.setAction(AppConstant.LRC_MESSAGE_ACTION);
				intent.putExtra("lrcMessage", message);
				sendBroadcast(intent);
				nextTimeMill = (Long) time.poll();
				message = (String) messages.poll();
			}
			currentTimeMill = currentTimeMill + 10;
			handler.postDelayed(updateTimeCallback, 10);
		}

	}

}
