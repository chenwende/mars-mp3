package mars.lrc;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

public class LrcProcessor {
	public ArrayList<Queue> process(InputStream inputStream) {
		Queue<Long> timeMills = new LinkedList<Long>();
		Queue<String> messages = new LinkedList<String>();
		ArrayList<Queue> queues = new ArrayList<Queue>();
		try {
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream,"utf-8");
			//InputStreamReader inputStreamReader = new InputStreamReader(
			//		inputStream,"GBK");
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String temp = null;
			int i = 0;
			Pattern pattern = Pattern.compile("\\[([^\\]]+)\\]");
			String result =null;
			boolean b = true;
			while ((temp = bufferedReader.readLine()) != null) {
				Log.d("", "temp = " + temp);
				i++;
				Matcher matcher = pattern.matcher(temp);
				if (matcher.find()) {
					if (result != null) {
						messages.add(result);
					}
					String timStr = matcher.group();
					Log.d("LrcProcessor", "timStr =" + timStr);
					Long timeMilLong = time2Long(timStr.substring(1,timStr.length()-1));
					if (b) {
						timeMills.offer(timeMilLong);
					}
					String msg = temp.substring(10);
					result = "" + msg + "\n";
				}else {
					result = result + temp + "\n";
				}
				
			}
			messages.add(result);
			queues.add(timeMills);
			queues.add(messages);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return queues;
	}
	public Long time2Long(String timeStr) {
		  Log.d("time2Long","timeStr = " +timeStr);
		  String str1[] = timeStr.split(":");
		  int min = Integer.parseInt(str1[0]);
		  String str2[] = str1[1].split("\\.");
		  int sec = Integer.parseInt(str2[0]);
		  int mill = Integer.parseInt(str2[1]);
		  return min * 60 * 1000 + sec * 1000 + mill * 10L;
		 }
}
