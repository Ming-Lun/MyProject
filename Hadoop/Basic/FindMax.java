
import java.io.IOException;
import java.util.*;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;

/*
* 利用 Hadoop，找出最大值
*/
public class FindMax {

 //實作Map功能的物件
 public static class MyMap extends MapReduceBase
                         implements Mapper<LongWritable, Text, Text, Text> {

  public void map(LongWritable key,
		          Text value,
		          OutputCollector<Text, Text> output,
		          Reporter reporter) throws IOException {
   //從文字檔讀入一行資料
   String line = value.toString();

   if (line != null && !line.equals("")) {
    //進行字串剖析的工作
    StringTokenizer tokenizer = new StringTokenizer(line);

    //取得地區名稱，用以當成主鍵
    String city = tokenizer.nextToken();

    //取得量測日期
    String date = tokenizer.nextToken();

    //取得空氣污染指標(PSI)
    String psi = tokenizer.nextToken();

    //儲存在負責包裹輸出資料的物件中
    output.collect(new Text(city), new Text(date + " " + psi));
   }
  }
 }

 //實作Reduce功能的物件
 public static class MyReduce extends MapReduceBase
                            implements Reducer<Text, Text, Text, Text> {
  //取得所有主鍵值相同的資料
  public void reduce(Text key,
		             Iterator<Text> values,
		             OutputCollector<Text, Text> output,
		             Reporter reporter) throws IOException {

   String tmpData = ""; //暫存最大偵測值的日期
   int tmpPSI = 0;      //暫存最大偵測值的PSI值

   //輪詢所有主鍵值相同的資料, 主鍵值為city
   while (values.hasNext()) {
    try {
     //判斷是否為最大的PSI, 如果是則儲存之
     StringTokenizer tokenizer = new StringTokenizer(values.next().toString());

     //取得偵測日期
     String date = tokenizer.nextToken();

     //取得PSI值
     int psi = Integer.parseInt(tokenizer.nextToken());

     //判斷是否為最大偵測值
     if (psi > tmpPSI) {
      tmpPSI  = psi;
      tmpData = date;
     }
    } catch (Exception e) {
    }
   }

   //儲存在負責包裹輸出資料的物件中
   output.collect(key,  new Text(tmpData + " " + tmpPSI));
  }
 }

 public static void main(String[] args) throws Exception {


  String[] argv = { "input-psi", "output-psimax1" };
  args = argv;

  JobConf conf = new JobConf(FindMax.class);
  conf.setJobName("FindMax");

  //設定輸出入資料格式
  conf.setInputFormat(TextInputFormat.class);
  conf.setOutputFormat(TextOutputFormat.class);

  //設定實作Map與Reduce功能的類別
  conf.setMapperClass(MyMap.class);
  //conf.setCombinerClass(MyReduce.class);
  conf.setReducerClass(MyReduce.class);

  //設定輸出資料主鍵值類別
  conf.setOutputKeyClass(Text.class);

  //設定輸出資料資料類別
  conf.setOutputValueClass(Text.class);

  //設定輸入檔案路徑
  FileInputFormat.setInputPaths(conf, new Path(args[0]));

  //設定輸出檔案路徑
  FileOutputFormat.setOutputPath(conf, new Path(args[1]));

  //執行計算工作
  JobClient.runJob(conf);
 }
}
