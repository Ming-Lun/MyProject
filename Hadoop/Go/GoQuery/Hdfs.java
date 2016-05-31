import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

/*
* 現有的棋譜資訊上傳到 Hadoop HDFS 上
*/


public class Hdfs {
	// 將檔案從local上傳到 hdfs , DataInputPath 為 local 的來源, HdfsInoutPath 為 hdfs 的目的端(判斷檔案是否存在，存在刪除重新上傳)
	public static void PutToHdfs(String DataInputPath, String HdfsInputPath,		Configuration conf) {
		Path HdfsPath = new Path(HdfsInputPath);
		checkAndDelete(HdfsInputPath, conf);
		try {
			// 產生操作hdfs的物件
			FileSystem hdfs = HdfsPath.getFileSystem(conf);
			// 上傳
			hdfs.copyFromLocalFile(false, new Path(DataInputPath), new Path(
					HdfsInputPath));

		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	// 下載HDFS的檔案到Local儲存
	public static void GetFileFromHdfs(String HdfsPath, String LocalPath,Configuration conf) {
		Path Hdfs_Path = new Path(HdfsPath);
		try {
			// 產生操作hdfs的物件

			FileSystem hdfs = Hdfs_Path.getFileSystem(conf);
			if (hdfs.exists(Hdfs_Path)) {
				// 下載
				hdfs.copyToLocalFile(false, new Path(HdfsPath), new Path(
						LocalPath));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// 檢測檔案在 hdfs 中是否存在,存在刪除
	public static void checkAndDelete(final String FilePath, Configuration conf) {
		Path Hdfs_Path = new Path(FilePath);
		try {
			FileSystem hdfs = Hdfs_Path.getFileSystem(conf);
			if (hdfs.exists(Hdfs_Path)) {
				hdfs.delete(Hdfs_Path, true);
			}
		} catch (IOException e3) {
			e3.printStackTrace();
		}

	}
	//將檔案從local上傳到 hdfs , DataInputPath 為 local 的來源, HdfsInoutPath 為 hdfs 的目的端(判斷檔案是否存在，存在不上傳)
	public static void fileToHdfs (String DataInputPath, String HdfsInputPath,Configuration conf){

			Path dst_path = new Path(HdfsInputPath);
			// 判斷運算資料是否存在
			try {
				FileSystem hdfs = dst_path.getFileSystem(conf);
				if (!hdfs.exists(dst_path)) {
					PutToHdfs(DataInputPath, HdfsInputPath, conf);
				}
			} catch (IOException e3) {
				e3.printStackTrace();
			}


	}
}
