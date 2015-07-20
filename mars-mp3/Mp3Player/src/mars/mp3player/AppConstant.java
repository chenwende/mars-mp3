package mars.mp3player;

public class AppConstant {
	public class PlayerMsg{
		public static final int PLAY_MSG = 1 ;
		public static final int PAUSE_MSG = 2 ;
		public static final int STOP_MSG = 3 ;

	}
	public class DownMsg{
		public static final int DOWN_FAIL = -1 ;
		public static final int DOWN_PASS = 0 ;
		public static final int DOWN_EXIST = 1 ;
	}
	public class URL{
		public static final String BASE_URL = "http://192.168.1.100:8088/mp3/";
	}
    
	
}
