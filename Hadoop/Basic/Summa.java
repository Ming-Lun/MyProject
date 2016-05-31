
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
*  利用Hadoop，進行矩陣相乘
*/

public class Summa {

 //實作Map功能的物件
 public static class MyMap extends MapReduceBase
                         implements Mapper<LongWritable, Text, Text, DoubleWritable> {

  public void map(LongWritable key,
		          Text value,
		          OutputCollector<Text, DoubleWritable> output,
		          Reporter reporter) throws IOException {

   //從資料檔中讀入一筆資料
   String line = value.toString();

   //取得矩形的高
   double height = Double.parseDouble(line.substring(0, line.indexOf(" ")));

   //取得矩形的寬
   double width = Double.parseDouble(line.substring(line.indexOf(" ") + 1, line.length()));

   //儲存矩形面積
   output.collect(new Text("square"),  new DoubleWritable(height * width));
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

   //將所有map運算的結果加總，完成積分運算
   double totalArea = 0.0;
   while (values.hasNext()) {
	totalArea += values.next().get();
   }

   //儲存在負責包裹輸出資料的物件中
   output.collect(new Text("TotalArea"),  new DoubleWritable(totalArea));
  }
 }

 public static void main(String[] args) throws Exception {

  boolean isLocalFolderOk = false;
  boolean isHDFSFolderOk = false;

  String folderName = "input-summa";

  //所要計算的積分區間下限
  int rangeA = 0;

  //所要計算的積分區間上限
  int rangeB = 10;

  //區間所要分隔的片段數
  int splits = 10000;

  //片段數不得為0
  if (splits != 0) {
    //計算每一個矩形的寬度
    double width = (double)(rangeB - rangeA)/(double)splits;
    if (width != 0) {
      //計算每一個矩形的高,利用公式 y = x ^ 2
      try {
    	//在本端檔案系統中，建立暫存資料檔的目錄
    	File folder = new File(folderName);
    	folder.mkdir();

        //在本端檔案系統中，建立新的資料檔
        DataOutputStream fsOut = new DataOutputStream(new FileOutputStream("./" + folderName + "/dataFile"));

        //根據片段數，建立資料片段
        double x = rangeA; //區間下限為起始點
        double y = 0; //矩形的高

        //建立資料檔內容
        //矩形的高 + 空白 + 矩形的寬
    	for (int i = 0; i < splits; i++) {

    	  //根據欲進行積分的公式，查得矩形的高
    	  y = x * x; //公式: y = x ^ 2

    	  //System.out.print("" + y + " " + width + "\n");

    	  //寫出矩形資料
    	  fsOut.writeBytes("" + y + " " + width + "\n");
    	  fsOut.flush();

    	  //移到下一個片段
    	  x += width;
    	}

    	//關閉本端的檔案
    	fsOut.close();
    	fsOut = null;

    	System.out.println("create local folder:" + folderName + " and data file done.");
    	isLocalFolderOk = true;
      } catch (Exception e) {
    	System.out.println("create local folder, error:" + e);
      }
    } else {
      System.out.println("width could not to be 0");
    }
  } else {
	System.out.println("splits could not to be 0");
  }

  //已在本端建立資料檔，準備複製至HDFS
  if (isLocalFolderOk) {
   try {
    //取得預設的組態設定
    Configuration conf = new Configuration();

    //設定檔案資料來源與目的位置
    Path srcPath = new Path("./" + folderName); //本端目錄
    Path dstPath = new Path("./"+folderName); //HDFS根目錄

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
   JobConf conf = new JobConf(Summa.class);
   conf.setJobName("Summa");

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
   FileOutputFormat.setOutputPath(conf, new Path("./output-summa"));

   //執行計算工作
   JobClient.runJob(conf);
   System.out.println("operation done.");
  }
 }
}
