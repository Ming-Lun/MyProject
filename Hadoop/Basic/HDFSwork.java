import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import java.net.URI;
import org.apache.hadoop.io.IOUtils;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import org.apache.hadoop.util.Progressable;

/*
* 將 local 端檔案上傳到 Hadoop HDFS ，進行檔案複製刪除
*/

public class HDFSwork {

	public static void uploadHDFS(String input,String inputHDFS){
		Configuration conf=new Configuration();
		Path in=new Path(input);
		Path out=new Path(inputHDFS);

		try{
			FileSystem fsout=out.getFileSystem(conf);
			if(fsout.exists(out)){
				fsout.delete(out);
			}
			fsout.mkdirs(out);
			fsout.copyFromLocalFile(false, in, out);

			System.out.println("MyHDFS, copy file ok");
		}catch(Exception e){
			System.out.println("MyHDFS, copy file error:" + e);
		}
	}

	public static void readHDFS(String inputHDFS) throws Exception{
		Configuration conf=new Configuration();
		FileSystem fs=FileSystem.get(URI.create(inputHDFS), conf);
		InputStream in=null;
		try{
			in=fs.open(new Path(inputHDFS));
			IOUtils.copyBytes(in, System.out, 4096,false);
		}finally{
			IOUtils.closeStream(in);
			System.out.println("\n-----------END-------------");
		}
	}

	public static void writeHDFS(String input,String inputHDFS)throws Exception{
		InputStream in=new BufferedInputStream(new FileInputStream(input));

		Configuration conf=new Configuration();
		FileSystem fs=new Path(inputHDFS).getFileSystem(conf);
		OutputStream out = fs.create(new Path(inputHDFS),new Progressable(){
			public void progress(){
				System.out.print(".");
			}
		});

		IOUtils.copyBytes(in, out, 4096,true);
		System.out.println("MyHDFS, copy file ok");
	}

	public static void deleteHDFS(String inputHDFS) throws Exception{
		FileSystem fs=FileSystem.get(URI.create(inputHDFS),new Configuration());
		if(fs.exists(new Path(inputHDFS)))
			fs.delete(new Path(inputHDFS));
		System.out.println("MyHDFS, delete file ok");
	}

	public static void main(String[] args) throws Exception{
		String input,inputHDFS;
		input=".//hdfsTest.txt";
		inputHDFS=".//inputHDFS//";

		uploadHDFS(input,inputHDFS);
		readHDFS(inputHDFS+"hdfsTest.txt");
		deleteHDFS(inputHDFS);

		writeHDFS(input,inputHDFS+"hdfsTest.txt");
		readHDFS(inputHDFS+"hdfsTest.txt");
		deleteHDFS(inputHDFS);
	}
}
