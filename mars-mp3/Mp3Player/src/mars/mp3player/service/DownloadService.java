package mars.mp3player.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import mars.download.HttpDownloader;
import mars.model.Mp3Info;
import mars.mp3player.AppConstant;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class DownloadService extends Service{

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	//ÿ���û����ListActivity���е�һ����Ŀʱ���ͻ���ø÷���
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {	
		//��Intent�����н�Mp3Info����ȡ��
		Mp3Info mp3Info = (Mp3Info)intent.getSerializableExtra("mp3Info");
		Log.d("MP3", "onStartCommand : mp3Info = " + mp3Info);
		//����һ�������̣߳�����Mp3Info������Ϊ�������ݵ��̶߳�����
		DownloadThread downloadThread = new DownloadThread(mp3Info);
		//�������߳�
		Thread thread = new Thread(downloadThread);
		thread.start();
		return super.onStartCommand(intent, flags, startId);
	}
	
	class DownloadThread implements Runnable{
		private Mp3Info mp3Info = null;
		public DownloadThread(Mp3Info mp3Info){
			this.mp3Info = mp3Info;
		}
		@Override
		public void run() {
			//���ص�ַhttp://192.168.1.100:8088/mp3/
			//����MP3�ļ������֣��������ص�ַ		
			String mp3Url = null;
			String lrcUrl = null;
			mp3Url = AppConstant.URL.BASE_URL + URLEncoder.encode(mp3Info.getMp3Name());
			lrcUrl = AppConstant.URL.BASE_URL + URLEncoder.encode(mp3Info.getMp3Name());
			//lrcUrl = AppConstant.URL.BASE_URL + URLEncoder.encode(mp3Info.getMp3Name(), "UTF-8");
			//���������ļ����õĶ���
			HttpDownloader httpDownloader = new HttpDownloader();
			//���ļ��������������洢��SDCard����		
			int mp3result = httpDownloader.downFile(mp3Url, "mp3", mp3Info.getMp3Name());
			int lrcresult = httpDownloader.downFile(lrcUrl, "mp3", mp3Info.getLrcName());
			String mp3resultMessage = null;
			String lrcresultMessage = null;
			if(mp3result == AppConstant.DownMsg.DOWN_FAIL){
				mp3resultMessage =  mp3Info.getMp3Name() + "下载失败";
			}
			else if(mp3result == AppConstant.DownMsg.DOWN_PASS){
				mp3resultMessage = mp3Info.getMp3Name() + "下载成功";
			}
			else if(mp3result == AppConstant.DownMsg.DOWN_EXIST){
				mp3resultMessage =  mp3Info.getMp3Name() + "已经存在";
			}
			if(lrcresult == AppConstant.DownMsg.DOWN_FAIL){
				lrcresultMessage = mp3Info.getLrcName() + "下载失败";
			}
			else if(lrcresult == AppConstant.DownMsg.DOWN_PASS){
				lrcresultMessage = mp3Info.getLrcName() + "下载成功";
			}
			else if(lrcresult ==  AppConstant.DownMsg.DOWN_EXIST){
				lrcresultMessage = mp3Info.getLrcName() + "已经存在";
			}
			//Toast.makeText(DownloadService.this, "MP3下载结果：" +mp3resultMessage, Toast.LENGTH_SHORT).show();
			//Toast.makeText(DownloadService.this, "歌词下载结果：" +lrcresultMessage, Toast.LENGTH_SHORT).show();
			//Log.d("MP3","resultMessage = " +resultMessage);
			//ʹ��Notification��ʾ�ͻ����ؽ��
		}
		
	}

}
