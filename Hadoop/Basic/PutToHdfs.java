import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.hadoop.conf.*;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.Path;

/*
* 將 local 端檔案上傳到 Hadoop HDFS
*/
public class PutToHdfs {
  // 將檔案從local上傳到 hdfs , DataInputPath 為 local 的來源, HdfsInoutPath 為 hdfs 的目的端
  public static boolean putToHdfs(String DataInputPath, String HdfsInputPath, Configuration conf) {
    Path HdfsPath = new Path(HdfsInputPath);
    CheckAndDelete.checkAndDelete(HdfsInputPath,conf);
    try {
      // 產生操作hdfs的物件
      FileSystem hdfs = HdfsPath.getFileSystem(conf);
      // 上傳
      hdfs.copyFromLocalFile(false, new Path(DataInputPath),new Path(HdfsInputPath));

    } catch (IOException e1) {
      e1.printStackTrace();
      return false;
    }
    return true;
  }
//將 Hdfs 檔案存在String變數裡面, HdfsOutputPath 為 Hdfs 的來源
  public static String ReadFile(String HdfsOutputPath,Configuration conf) {
	  String result="";
	  //產生HdfsOutputPath的路徑
	  Path dstPath = new Path(HdfsOutputPath);
	  try{
	  //產生操作hdfs的物件
	  FileSystem hdfs = dstPath.getFileSystem(conf);
	//運算結果的路徑
	  Path outputPath = new Path(HdfsOutputPath);
	  //開啟輸入資料流
	  FSDataInputStream fsStream = hdfs.open(outputPath);
	  //將Hdfs檔案放到br變數裡面,進行讀取的動作
	  BufferedReader br =new BufferedReader (new InputStreamReader(fsStream,"UTF-8"));

	       String line;
	       while ( (line = br.readLine()) !=null){
	    	   if (result=="")
	    	   {
	    		   result = line;
	    	   }else{
	    		   result += ","+line;
	    	   }
	       }

	       br.close();
	       fsStream.close();

	  }catch(IOException e2) {
	      e2.printStackTrace();
	  }
	  return result;
	  }
//  檢測檔案在 hdfs 中是否存在,存在刪除
  public static class CheckAndDelete {
		static boolean checkAndDelete(final String FilePath, Configuration conf) {
			Path dst_path = new Path(FilePath);
			try {
				FileSystem hdfs = dst_path.getFileSystem(conf);
				if (hdfs.exists(dst_path)) {
					hdfs.delete(dst_path, true);
				}
			} catch (IOException e3) {
				e3.printStackTrace();
				return false;
			}
			return true;
		}
	}

  static public void main(String args[]){
    Configuration conf = new Configuration();
    String DataInputPath = "/home/hadoop/Book/Black";
    String HdfsInputPath = "/user/hadoop/BookData/Black";
    //HdfsOutputPath這變數為運算結果的路徑，這邊的範例是沒有進行運算，直接指定所上傳的路徑，來測試說是否可以從 Hdfs 上面讀取資料下來
    String HdfsOutputPath = "hdfs:/user/hadoop/BookData/Black/123.txt";
    String result;

    if(putToHdfs(DataInputPath,HdfsInputPath,conf))
    {
    	System.out.println("成功上傳");
    }else{
    	System.out.println("上傳失敗");
    }
    result=ReadFile(HdfsOutputPath,conf);
    System.out.print(result);

  }
}
