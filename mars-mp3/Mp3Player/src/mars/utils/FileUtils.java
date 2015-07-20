package mars.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import mars.model.Mp3Info;
import android.R.string;
import android.os.Environment;
import android.util.Log;

public class FileUtils {
	private String SDCardRoot;

	public FileUtils() {
		// �õ���ǰ�ⲿ�洢�豸��Ŀ¼
		SDCardRoot = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + File.separator;
		Log.d("FileUtils", "SDCardRoot = " + SDCardRoot);
		SDCardRoot = "/sdcard/";
	}

	/**
	 * ��SD���ϴ����ļ�
	 * 
	 * @throws IOException
	 */
	public File createFileInSDCard(String fileName, String dir)
			throws IOException {
		File file = new File(SDCardRoot + dir + File.separator + fileName);
		Log.d("MP3", "createFileInSDCard : file = " + file.toString());
		file.createNewFile();
		return file;
	}

	/**
	 * ��SD���ϴ���Ŀ¼
	 * 
	 * @param dirName
	 */
	public File creatSDDir(String dir) {
		File dirFile = new File(SDCardRoot + dir + File.separator);
		System.out.println(dirFile.mkdirs());
		Log.d("MP3", "dirFile.mkdirs() = " + dirFile.mkdirs());
		return dirFile;
	}

	/**
	 * �ж�SD���ϵ��ļ����Ƿ����
	 */
	public boolean isFileExist(String fileName, String path) {
		File file = new File(SDCardRoot + path + File.separator + fileName);
		return file.exists();
	}

	/**
	 * ��һ��InputStream���������д�뵽SD����
	 */
	public File write2SDFromInput(String path, String fileName,
			InputStream input) {

		File file = null;
		OutputStream output = null;
		try {
			creatSDDir(path);
			file = createFileInSDCard(fileName, path);
			output = new FileOutputStream(file);
			byte buffer[] = new byte[4 * 1024];
			int temp;
			while ((temp = input.read(buffer)) != -1) {
				output.write(buffer, 0, temp);
			}
			output.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				output.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return file;
	}

	/**
	 * ��ȡĿ¼�е�Mp3�ļ������ֺʹ�С
	 */
	public List<Mp3Info> getMp3Files(String path) {
		List<Mp3Info> mp3Infos = new ArrayList<Mp3Info>();
		File file = new File(SDCardRoot + File.separator + path);
		Log.d("MP3", "file = " + file.toString());
		File[] files = file.listFiles();
		if (file.exists()) {
			for (int i = 0; i < files.length; i++) {
				if (files[i].getName().endsWith("mp3")) {
					Mp3Info mp3Info = new Mp3Info();
					mp3Info.setMp3Name(files[i].getName());
					mp3Info.setMp3Size(files[i].length() + "");
					// ����lrcName
					String temp = files[i].getName();
					String newStr = temp.replaceAll("mp3","lrc");
					Log.d("getMp3Files", "newStr = " + newStr);
					for (int j = 0; j < files.length; j++) {
						if (files[j].getName().equalsIgnoreCase(newStr)) {
							mp3Info.setLrcName(files[j].getName());
							mp3Info.setLrcSize(files[j].length() + "");
						}
					}
					Log.d("getMp3Files", "mp3Info = " + mp3Info);
					mp3Infos.add(mp3Info);

				}

			}
		}
		
		return mp3Infos;
	}


}