import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/*
*取得Hadoop Reduce階段執行時取的當前Map所分群後的Block檔案大小
*/
public class FileSize {
	// 讀取檔案大小
	public static float ReadFileSize(String filePath) {

		// 檔案大小，單位為Byte
		float filesize = 0;

		try {
			File file = new File(filePath);
			FileInputStream fis = new FileInputStream(file);

			filesize = (float) fis.available();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return filesize;
	}
}
