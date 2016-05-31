import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/*
* 原始棋譜上對到 Hadoop HDFS 前，進行分群數量的檔案分割
*/
public class SplitFile {
	static List<String> allInputFilePath = new ArrayList<String>();

	// 利用遞迴方式找出原始黨的資料夾下所有檔案路徑
	public static void FilePath(String strPath, boolean workState) {
		try {
			File f = new File(strPath);
			if (f.isDirectory()) {
				File[] fList = f.listFiles();
				for (int j = 0; j < fList.length; j++) {
					if (fList[j].isDirectory()) {
						FilePath(fList[j].getPath(), workState);
					}
				}
				for (int j = 0; j < fList.length; j++) {

					if (workState == true) {
						// 紀錄input檔案位置
						allInputFilePath.add(fList[j].getPath());
					} else {
						// delete output file
						File deleteFile = new File(fList[j].getPath());
						if (deleteFile.exists()) {
							deleteFile.delete();
						}
					}

				}
			}
		} catch (Exception e) {
			System.out.println("Error： " + e);
		}
	}

	// 切割檔案
	public static void SplitFile(String inputPath, String outputPath,
			int SplitNum) {

		// 紀錄input file location
		FilePath(inputPath, true);
		// delete all output file
		FilePath(outputPath, false);
		// spilt file for the all input file
		for (int i = 0; i < allInputFilePath.size(); i++) {

			int count = 0;
			FileInputStream fstream = null;
			try {
				fstream = new FileInputStream(allInputFilePath.get(i));
			} catch (FileNotFoundException e) {

				e.printStackTrace();
			}
			DataInputStream in = new DataInputStream(fstream);

			Scanner scan = new Scanner(new InputStreamReader(in));

			while (scan.hasNext()) {

				String scanData = scan.nextLine();
				int pathCount = count % SplitNum + 1;
				String fileName = "SplitFile" + pathCount;
				String mapKey = "bookMapKey" + pathCount;

				try {
					String filePath = outputPath + fileName + ".txt";

					FileWriter fileWriter = new FileWriter(filePath, true);
					BufferedWriter bufferedWriter = new BufferedWriter(
							fileWriter);
					bufferedWriter.write(mapKey + ":" + scanData);
					bufferedWriter.newLine();
					bufferedWriter.close();

				} catch (Exception e) {
					e.printStackTrace();
				}


				count++;
			}
			try {
				in.close();

			} catch (IOException e) {

				e.printStackTrace();
			}
		}

	}
}
