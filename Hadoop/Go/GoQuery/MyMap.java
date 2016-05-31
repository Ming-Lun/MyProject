import java.io.IOException;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Mapper;

/*
* Hadoop Map 進行棋譜分群動作，並配對相對應的 Key/Value
*/


public class MyMap extends Mapper<LongWritable, Text, Text, Text> {

	Text MapKeyData = new Text();
	Text MapValueData = new Text();


	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

		//所有 Block 實際存放在硬碟中的初始 Path
		String Blockpath="/home/hadoop/dfs/data/current/";
		BlockPath  blockPath =new BlockPath();

		//所有 Block 實際存放在硬碟中的 Path
		String [] allBlockData=blockPath.BlockData(Blockpath);

		//利用 陣列 紀錄 Block 的 MapKey 和  Block Path，以 ";" ，分號前面為 MapKey，後為 Block Path
		for(int i = 0;i<allBlockData.length;i++){
			String []temp = allBlockData[i].split(";");

			MapKeyData.set(temp[0]);
			MapValueData.set(temp[1]);
			context.write(MapKeyData, MapValueData);
		}




	}
}
