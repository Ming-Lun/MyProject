import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/*
*	取得Hadoop分群後BlockPath，並確認Block內的資料是否為所需要的內容
*/

public class BlockPath {

	public static String[] getFileList(String folderPath) {

		StringBuffer fileList = new StringBuffer();
		String[] filename = null;
		try {
			java.io.File folder = new java.io.File(folderPath);
			String[] list = folder.list();
			filename = list;

		} catch (Exception e) {
			System.out.println("Block Path Error");
		}

		return filename;
	}

	public static String[] BlockData(String Path) {
		String[] blockName = getFileList(Path);
		String blockData = null;
		boolean state = false;
		for (int i = 0; i < blockName.length; i++) {
			String temp = ReadBlock(Path + blockName[i]);
			if (state == false && temp != null) {
				blockData = temp;
				state = true;
			} else if (temp != null) {
				blockData += "," + temp;
			}
		}
		String[] allBlockData = blockData.split(",");

		return allBlockData;
	}

	// 藉由讀取裡面的內容來確定是否為所需要的 Block
	public static String ReadBlock(String blockPath) {

		FileInputStream fstream = null;
		int count = 0;
		String blockData = null;
		try {
			fstream = new FileInputStream(blockPath);
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}
		DataInputStream in = new DataInputStream(fstream);

		Scanner scan = new Scanner(new InputStreamReader(in));
		while (scan.hasNext()) {
			if (count > 1) {
				break;
			}
			String scanData = scan.nextLine();
			if (scanData.contains("bookMapKey")) {
				String[] temp = scanData.split(":");
				blockData = temp[0] + ";" + blockPath;
				break;
			}
			count++;

		}
		try {
			in.close();
		} catch (IOException e) {

			e.printStackTrace();
		}
		return blockData;
	}
}
