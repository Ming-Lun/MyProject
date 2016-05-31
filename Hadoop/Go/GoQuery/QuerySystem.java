import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.GenericOptionsParser;

/*
* 圍棋棋譜 Hadoop 搜尋主程式
*/

public class QuerySystem {
	static boolean isBoardCodeOk = false;
	static boolean isHDFSFolderOk = false;

	// Start Cloud
	public static void RunCloud(String[] args) {
		// public static String RunCloud(String Data) {
		System.out.println("第一階段造雲");

		// 變數設定
		String BoardSize = "9";
		// 檔案所要分個的數量
		String SplitFileNum = "2";

		//取得當前時間
		GetTime time = new GetTime();
		String TimeNow = time.getDateTime();

		// ............................................路徑暫時寫死，等出來後在改，檔案上傳到HDFS會產生覆蓋現象..........................................
		// 棋譜BookInputPath
		String BookInputPath = "/home/hadoop/BookData/" + BoardSize + "/All/";
		// BookSplitPath，棋譜切割後檔案儲存的檔案路徑
		String BookSplitPath = "/home/hadoop/BookData/" + BoardSize + "/SplitFile/";
		//設定 Map 讀取幾次的假資料
		String MapWorkCount ="/home/hadoop/BookData/MapWorkCount/";
		//設定 Map 讀取幾次的假資料，上傳到Hdfs的路徑
		String MapWorkCountUpPath = "hdfs:/user/hadoop/MapWorkCount/";
		// BookUpPath，棋譜切割後檔案上傳到HDFS後的檔案路徑
		String BookUpPath = "hdfs:/user/hadoop/BookData/" + BoardSize ;

		// HdfsOutputPath，運算完結果存放在HDFS中的檔案路徑
		String HdfsOutputPath = "hdfs:/user/hadoop/QueryGoBookOutput/"
				+ BoardSize + "/";
		// HdfsOutputLocalPath，下載HDFS中運算完的結果到local的檔案路徑
		String HdfsOutputLocalPath = "/home/hadoop/BookData/OutRecal/"
				+ TimeNow + "/";

		String JobName = "QueryGoBook";

		Configuration conf = new Configuration();

		//宣告 Hdfs class 要使用裡面的功能
		Hdfs hdfs =new Hdfs();
		//刪除之前運算的結果
		hdfs.checkAndDelete(HdfsOutputPath, conf);


		// 棋譜把全部上傳到Hdfs
		if (isHDFSFolderOk == false) {

			SplitFile splitFile = new SplitFile();

			splitFile.SplitFile(BookInputPath, BookSplitPath, Integer.valueOf(SplitFileNum));
			hdfs.fileToHdfs(BookSplitPath, BookUpPath, conf);
			hdfs.fileToHdfs(MapWorkCount, MapWorkCountUpPath, conf);
			isHDFSFolderOk =true;

		}
		// 全部要上傳的檔都上傳到Hdfs，執行造雲計畫
		if (isHDFSFolderOk == true) {
			System.out.println("造雲囉");
			// eclipse using

			String[] argv = { MapWorkCountUpPath, HdfsOutputPath };
			args = argv;
			String[] otherArgs = new GenericOptionsParser(conf, args)
					.getRemainingArgs();
			if (otherArgs.length != 2) {
				System.err.println("Usage : Sort <in> <out>");
				System.exit(2);
			}
			try {
				// 把所要切割的檔案數量改變成hadoop能接受的全域變數

				// 把盤面編碼好的資料設定為hadoop可以用的全域變數的格式，藉此來設定key
				Job job = new Job(conf, JobName);
				job.setJarByClass(QuerySystem.class);
				job.setMapperClass(MyMap.class);
				job.setReducerClass(MyReduce.class);
				job.setOutputKeyClass(Text.class);
				job.setOutputValueClass(Text.class);
				// 設定ReduceTasks數量，跟所要分割的棋譜數量一樣
				job.setNumReduceTasks(Integer.valueOf(SplitFileNum));
				FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
				FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));

				boolean success = job.waitForCompletion(true);

			} catch (Exception e) {
			}
			System.out.println("雲好了");

		}
		//下載運算後的紀錄到本地端硬碟
		hdfs.GetFileFromHdfs(HdfsOutputPath, HdfsOutputLocalPath, conf);

		isBoardCodeOk = false;
		isHDFSFolderOk = false;
	}

	public static void main(String args[]) {

		RunCloud(args);

		System.out.println("跑完了");

	}

}
