import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;

/*
* 利用Hadoop 實作 MonteCarlo 練習
*/

public class MonteCarlo {

 //亂數要產生的座標數
 public static int totalPoint = 100;

 //實作Map功能的物件
 public static class MyMap extends MapReduceBase
                         implements Mapper<LongWritable, Text, Text, DoubleWritable> {

  public void map(LongWritable key,
		          Text value,
		          OutputCollector<Text, DoubleWritable> output,
		          Reporter reporter) throws IOException {

   double x = Math.random(); //取得0 ~ 1的亂數
   double y = Math.random(); //取得0 ~ 1的亂數

   x -= 0.5; //使得 x 介於 -0.5 ~ 0.5之間
   y -= 0.5; //使得 y 介於 -0.5 ~ 0.5之間

   //判斷是否落在圓內, 則記錄一筆落在圓內的記錄
   if ((x * x) + (y * y) <= 0.25) {
	output.collect(new Text("InCircle"),  new DoubleWritable(1.0));
   }
  }
 }

 //實作Reduce功能的物件
 public static class MyReduce extends MapReduceBase
                            implements Reducer<Text, DoubleWritable, Text, DoubleWritable> {
  //取得所有主鍵值相同的資料
  public void reduce(Text key,
		             Iterator<DoubleWritable> values,
		             OutputCollector<Text, DoubleWritable> output,
		             Reporter reporter) throws IOException {

   //判斷map運算後，共有多少筆落在圓內
   double inCircle = 0.0;
   while (values.hasNext()) {
    values.next();
    inCircle++;
   }

   //圓周率 = 4 * (n/m)
   double pi = 4 * (inCircle/MonteCarlo.totalPoint);

   //儲存在負責包裹輸出資料的物件中
   output.collect(new Text("PI"),  new DoubleWritable(pi));
  }
 }

 public static void main(String[] args) throws Exception {

  boolean isLocalFolderOk = false;
  boolean isHDFSFolderOk = false;

  String folderName = "input-montecarlo";

  //根據要產生的亂數座標數，動態建立資料檔
  try {
	//在本端檔案系統中，建立暫存資料檔的目錄
	File folder = new File(folderName);
	if(!folder.exists())
		folder.mkdir();

    //在本端檔案系統中，建立新的資料檔
    DataOutputStream fsOut = new DataOutputStream(new FileOutputStream("./" + folderName + "/dataFile"));

    //根據座標數，建立資料片段
    for (int i = 0; i < MonteCarlo.totalPoint; i++) {
     fsOut.writeBytes("" + i + "\n");
     fsOut.flush();
    }

    //關閉本端的檔案
    fsOut.close();
    fsOut = null;

    System.out.println("create local folder:" + folderName + " and data file done.");
    isLocalFolderOk = true;
  } catch (Exception e) {
	System.out.println("create local folder, error:" + e);
  }

  //已在本端建立資料檔，準備複製至HDFS
  if (isLocalFolderOk) {
   try {
    //取得預設的組態設定
    Configuration conf = new Configuration();

    //設定檔案資料來源與目的位置
    Path srcPath = new Path("./" + folderName); //本端目錄
    Path dstPath = new Path("./"+ folderName); //HDFS根目錄

	//取得封存檔案系統資訊的物件
	FileSystem hdfs = dstPath.getFileSystem(conf);

	//將本端目錄中的檔案, 複製至HDFS
	hdfs.copyFromLocalFile(false, srcPath, dstPath);

	System.out.println("copy local folder to HDFS done.");
	isHDFSFolderOk = true;
   } catch (Exception e) {
	System.out.println("copy local folder to HDFS error:" + e);
   }
  }

  //資料檔案準備完成，開始進行運算
  if (isHDFSFolderOk) {
   JobConf conf = new JobConf(MonteCarlo.class);
   conf.setJobName("MonteCarlo");

   //設定輸出入資料格式
   conf.setInputFormat(TextInputFormat.class);
   conf.setOutputFormat(TextOutputFormat.class);

   //設定實作Map與Reduce功能的類別
   conf.setMapperClass(MyMap.class);
   conf.setReducerClass(MyReduce.class);

   //設定輸出資料主鍵值類別
   conf.setOutputKeyClass(Text.class);

   //設定輸出資料資料類別
   conf.setOutputValueClass(DoubleWritable.class);

   //設定輸入檔案路徑
   FileInputFormat.setInputPaths(conf, new Path("./" + folderName));

   //設定輸出檔案路徑
   FileOutputFormat.setOutputPath(conf, new Path("./output-montecarlo"));

   //執行計算工作
   JobClient.runJob(conf);

   System.out.println("operation done.");
  }
 }
}
