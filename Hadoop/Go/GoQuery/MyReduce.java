import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/*
* Hadoop Reduce
* 1.分群後 Block 檔案大小的判斷是否小於 1G ，小於的情況則是存入記憶體，否則利用讀取檔案的方式進行搜尋
* 2.Server 連線，進行分散式棋譜搜尋比對，並記錄每次回傳的落子位置
* 3.完成搜尋工作後，輸出每次回傳的落子結果
*/
public class MyReduce extends Reducer<Text, Text, Text, Text> {

	Text ReduceKey = new Text();
	Text ReduceValue = new Text();
	Socket server;

	public void reduce(Text key, Iterable<Text> value, Context context)
			throws IOException, InterruptedException {

		// 變數宣告
		// Server的訊息
		String SeverData;
		// 比對的結果
		String Result;
		// 紀錄所有 Block 的路徑
		List<String> BlockPathList = new ArrayList<String>();
		// 存入記憶體時的棋譜資訊
		List<String> BookList = new ArrayList<String>();
		// 比對到相同盤面的落子資訊
		List<String> BookData = new ArrayList<String>();
		// 紀錄每次比對的盤面和回傳結果
		List<String> Record = new ArrayList<String>();// 紀錄搜尋的盤面和結果，以分號隔開
		// 連線狀態
		boolean LinkState = false;
		// 比對時找到結果的狀態
		boolean AnsState = false;
		// 分群檔案 的大小狀態
		boolean splitFileSizeState = false;
		// 紀錄Map File 輸出的大小
		float splitFileSize = 0;

		// 紀錄 所有分群檔案 的路徑
		for (Text val : value) {
			BlockPathList.add(val.toString());
		}

		// 計算 分群檔案 的大小為何，單位為Byte
		FileSize fileSize = new FileSize();
		for (int i = 0; i < BlockPathList.size(); i++) {
			splitFileSize = splitFileSize
					+ fileSize.ReadFileSize(BlockPathList.get(i));
		}

		// 判斷 分群檔案 的大小，是否小於 1G ，小於的情況則是存入記憶體，否則利用讀取檔案的方式進行搜尋
		if (splitFileSize < 1024 * 1024 * 1024) {

			for (int i = 0; i < BlockPathList.size(); i++) {
				{

					FileInputStream fstream = null;

					try {
						fstream = new FileInputStream(BlockPathList.get(i));
					} catch (FileNotFoundException e) {

						e.printStackTrace();
					}
					DataInputStream in = new DataInputStream(fstream);

					Scanner scan = new Scanner(new InputStreamReader(in));
					while (scan.hasNext()) {
						String[] temp = scan.nextLine().split(":");
						BookList.add(temp[1]);
					}
					try {
						in.close();
					} catch (IOException e) {

						e.printStackTrace();
					}
				}
			}
			splitFileSizeState = true;
		}

		// 連線Server的IP
		server = new Socket("XXX.XXX.XXX.XXX", XXX);

		// 資料流設定
		DataInputStream serverin = new DataInputStream(server.getInputStream()); // 輸入流
		DataOutputStream serverout = new DataOutputStream(server
				.getOutputStream());// 輸出流

		// 請求連線
		serverout.writeUTF("JOIN_");

		serverout.flush();
		// 連線判斷
		SeverData = serverin.readUTF();
		// if (SeverData=="ACCEPT"){
		LinkState = true;
		// }

		BoardEncoder boardEncoed = new BoardEncoder();
		// 持續接收Server的訊息，直到收到 end 訊息跳出迴圈，結束工作
		while (LinkState == true) {

			SeverData = serverin.readUTF();
			// Server傳訊訊息判斷，以下為進行搜尋比對
			if (SeverData.startsWith("BOARD_")) {

				// 進行盤面資訊編碼
				String[] temp;
				temp = SeverData.substring(6).split(";");

				String BoardCode = boardEncoed.BoardEncoed(temp[1], temp[0]);

				/*
				 * 分群檔案 的檔案大小 小於 記憶體 的情況， 則對存入記憶體的棋譜資訊依序進行讀取和比對的動作
				 */
				if (splitFileSizeState = true) {
					for (int i = 0; i < BookList.size(); i++) {
						if (BookList.get(i).contains(BoardCode)) {
							String[] temp1;
							temp1 = BookList.get(i).split(" ");
							BookData.add(temp1[temp1.length - 1]);
							AnsState = true;
						}
					}
				}
				/*
				 * 分群檔案 的檔案大小 大於 記憶體 的情況， 則對 分群檔案 依序進行讀取和比對的動作
				 */
				else {

					for (int i = 0; i < BlockPathList.size(); i++) {

						FileInputStream fstream = null;
						try {
							fstream = new FileInputStream(BlockPathList.get(i));

						} catch (FileNotFoundException e) {

							e.printStackTrace();
						}
						DataInputStream BookIn = new DataInputStream(fstream);

						Scanner scan = new Scanner(
								new InputStreamReader(BookIn));

						while (scan.hasNext()) {

							String s = scan.nextLine();
							// 分群檔案 檔案內容進行切割
							String[] temp1 = s.split(":");
							// 依序進行比對
							for (int j = 0; j < temp1.length; j++) {
								// 判斷是否有相同的盤面，有則把落子資訊存入BookData
								if (temp1[j].contains(BoardCode)) {
									String[] temp2 = temp1[j].substring(5)
											.split(" ");
									BookData.add(temp2[temp2.length - 1]);
									AnsState = true;
								}
							}
						}
						try {
							BookIn.close();
						} catch (IOException e) {

							e.printStackTrace();
						}
					}
				}
				// 比對時找到結果的狀態判斷
				if (AnsState == true) {
					int RandNum = (int) ((Math.random() * BookData.size()));
					Result = BookData.get(RandNum);
					serverout.writeUTF("RESULT_" + Result);
					serverout.flush();

				} else {
					Result = "-1";
					serverout.writeUTF("RESULT_" + Result);
					serverout.flush();
				}
				// 把每次要比對的盤面資訊和回傳結果記錄下來
				Record.add(BoardCode + ";" + Result);

			}
			// 收到 end 訊息跳出迴圈，結束工作
			if (SeverData.startsWith("end")) {
				break;
			}
			// 每次結束比對工作時，把比對到結果清空，避免落子位置為舊的盤面資訊的情況發生
			BookData.clear();
			// 比對時找到結果的狀態變為初始值
			AnsState = false;

		}
		// 搜尋過程的紀錄輸出
		for (int i = 0; i < Record.size(); i++) {
			String[] temp;
			temp = Record.get(i).split(";");
			ReduceKey.set(temp[0]);
			ReduceValue.set(temp[1]);
			context.write(ReduceKey, ReduceValue);
		}

	}
}
