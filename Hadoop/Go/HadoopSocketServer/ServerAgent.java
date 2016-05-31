import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/*
*  模擬圍棋當前盤面資訊與各台 Hadoop Reduce 連線以及利用socket方式進行資料的傳遞
*/
public class ServerAgent extends Thread
{
	Socket client; //客戶端
	DataInputStream din;//輸入流
	DataOutputStream dout;//輸出流
	boolean working;

	public ServerAgent(Socket sc,DataInputStream din,DataOutputStream dout)
	{
		this.client = sc;
		this.din = din;
		this.dout = dout;
		this.working = true;
	}

	public void run()
	{
		while(working)
		{
			try
			{
				String msg = din.readUTF();
//				System.out.println(msg);
				if (msg.startsWith("RESULT_")) {
					String result = msg.substring(7);// 得到截取的字串
					Server.report(result);
				}
			}
			catch (IOException e)
			{
				turnoff();
//				e.printStackTrace();
			}
		}
	}

	public void search(String boardMap)
	{
		if(boardMap!="end")
		{
			String msg = "BOARD_" + boardMap;
			try {
				dout.writeUTF(msg);
			} catch (IOException e) {

				e.printStackTrace();
			}
		}else
		{
			try {
				dout.writeUTF(boardMap);
				client.close();
			} catch (IOException e) {
				turnoff();
				e.printStackTrace();
			}

		}
	}

	public void turnoff()
	{
		working = false;
	}
}
