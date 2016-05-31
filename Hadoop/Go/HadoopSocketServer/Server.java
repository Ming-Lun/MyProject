import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/*
* 模擬圍棋當前盤面資訊傳送到 Hadoop Readuce 各台電腦進行盤面搜尋後回傳的實際結果
*/

public class Server {

	static ArrayList<ServerAgent> clients = new ArrayList<ServerAgent>();
	static ServerSocket server;
	static String result;
	static final int MaxClients = 2, channel = 9999;
	static int numClients = 0;

	public static void SocketLink() {
		try {
			server = new ServerSocket(channel);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Listening on " + channel + "...");

		/*
		 * 與客戶端連線
		 */
		while (numClients < MaxClients) // 迴圈等待客戶連線
		{
			try {
				Socket client = server.accept(); // 接收客戶端連線資料
				DataInputStream din = new DataInputStream(
						client.getInputStream()); // 輸入流
				DataOutputStream dout = new DataOutputStream(
						client.getOutputStream());// 輸出流
				String msg = din.readUTF();

				if (msg.startsWith("JOIN_")) {
					if (numClients < MaxClients) {
						numClients++;
						System.out.println("Client " + numClients
								+ " connected...");
						dout.writeUTF("ACCEPT");

						ServerAgent sa = new ServerAgent(client, din, dout);
						clients.add(sa);
						sa.start();

					} else // 人數已滿
					{
						dout.close();// 關閉輸出流
						din.close();// 關閉輸入流
						client.close();// 關閉連接
					}
				} else {
					System.out.println("Illegal connection!");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("The First Step of Linking is Over");
	}

	public static void AssignWork(String SearchData) {
		for (int i = 0; i < numClients; i++) {
			clients.get(i).search(SearchData);

		}

		/*
		 * 等待結果回傳
		 */
		try {
			Thread.sleep(8);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public static void main(String str[]) throws IOException {

		//實驗變數的設定
		String boardMap = "9;888888888888000000000880000000008800000000088000000000880000000008800000000088000000000880000000008800000000088888888";

		int WorkCounter = 0;//搜尋次數計數器
		int TotalWork = 1000;//總搜尋次數
		long JobStartTime;// 每次總搜開始時間
		long JobEndTime;// 每次總搜結束時間
		String FileName = "StepWorkTime.txt";// 輸出檔案名稱
		String AvgTime;// 平均運算時間
		int TotalTime = 0 ;// 總共花費時間
		String[] OutputTime = new String[TotalWork];//紀錄每次工作時間


		// 等待hadoop各台電腦連結
		SocketLink();
		// 分派工作

		while (WorkCounter < TotalWork) {
			System.out.println("趕快跑，跑第幾圈了?    Ans: 第 " + (WorkCounter + 1) + " 圈");
			JobStartTime = System.currentTimeMillis();
			result = "";
			String FinalAns;
			AssignWork(boardMap);
			ArrayList<String> AnsList =new ArrayList<String>();
			boolean AnsState = false;
			if (result.equals("")) {
				System.out.println("No client resturns!");
			} else {
				result="output"+result;
				//運算後的答案做切割
				String [] temp=result.split(" ");
				for (int i =1;i<temp.length;i++)
				{
					if(temp[i]!="-1")
					{
						AnsList.add(temp[i]);
						AnsState =true;
					}
				}
				if (AnsState==true)
				{
					int RandNum = (int) ((Math.random()*AnsList.size()));
					FinalAns = AnsList.get(RandNum);
				}else
				{
					FinalAns = "-1";
				}
				System.out.println("最終回傳結果:"+FinalAns);
				AnsList.clear();
				AnsState =false;
			}
			JobEndTime = System.currentTimeMillis();
			OutputTime[WorkCounter] = String.valueOf(JobEndTime - JobStartTime);
			WorkCounter++;
			System.out.println(" ");
		}
		//送出結束工作的訊息
		AssignWork("end");
		//計算工作的總共花費時間
		for (int i = 0; i < WorkCounter; i++) {
			TotalTime += Integer.parseInt(OutputTime[i]);
		}
		//計算每次工作的平均時間
		AvgTime = String.valueOf(TotalTime / Integer.valueOf(WorkCounter));
		//輸出實驗數據
		try {
			String filepath = FileName;
			FileOutputStream fileOutputStream;
			fileOutputStream = new FileOutputStream(filepath);
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
					fileOutputStream);

			outputStreamWriter.write("時間單位:Millisecond(MS)" + "\r\n");
			outputStreamWriter.write("總共搜尋次數:" + WorkCounter + "\r\n");
			outputStreamWriter.write("搜尋總共花費時間:" + TotalTime + "\r\n");
			outputStreamWriter.write("每次搜尋平均時間:" + AvgTime + "\r\n");
			outputStreamWriter.write("\r\n");
			outputStreamWriter.write("每次搜尋的時間\r\n");
			for (int i = 0; i < WorkCounter; i++) {
//				outputStreamWriter.write((i+1) + ":" + OutputTime[i] + "\r\n");
				outputStreamWriter.write(OutputTime[i] + "\r\n");
			}
			outputStreamWriter.close();

		} catch (Exception e1) {
			e1.printStackTrace();
		}

	}

	public static synchronized void report(String r) {

		result = result + " " + r;
	}

}
