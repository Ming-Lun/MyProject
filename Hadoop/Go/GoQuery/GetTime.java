import java.text.SimpleDateFormat;
import java.util.Date;

/*
*	取得當下時間，做為此次運算結果的log
*/
public class GetTime {

	public static String getDateTime() {
		SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
		Date date = new Date();
		String strDate = sdFormat.format(date);
		return strDate;
	}
}
