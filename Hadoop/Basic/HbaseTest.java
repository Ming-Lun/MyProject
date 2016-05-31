
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;

/*
*  Hadoop HBASE 功能新增、修改、刪除 
*/

public class HbaseTest {

 public HbaseTest() {
  //設定ZooKeeper的節點，連接埠號
  HBaseConfiguration config = new HBaseConfiguration();
  config.clear();
  config.set("hbase.zookeeper.quorum", "linux1.freejavaman");
  config.set("hbase.zookeeper.property.clientPort","2181");

  doScan(config);
  //doPut(config);
  //doUpdate(config);
  //doDeleteRow(config);
  //doDeleteFamily(config);
  //doDeleteColumn(config);
  //doCreateTable(config);
  //doDropColumn(config);
  //doModifyColumn(config);
 }


/*
 //修改與新增行家族
 private void doModifyColumn(HBaseConfiguration config) {
  try {
   //HBase管理元件
   HBaseAdmin admin = new HBaseAdmin(config);

   //表格名稱
   String tableName = "ProductTable";

   //判斷表格是否已經存在
   if (admin.tableExists(tableName)) {
	//禁能該表格
	admin.disableTable(tableName);

	//增加新的行家族
	HColumnDescriptor familyDesc = new HColumnDescriptor("memo");
	admin.addColumn(tableName, familyDesc);

	//修改行家族
	HColumnDescriptor familyDesc2 = new HColumnDescriptor("spec");
	familyDesc2.setTimeToLive(60 * 3);
	admin.modifyColumn(tableName, familyDesc2);

	//致能該表格
	admin.enableTable(tableName);
   }
  } catch (Exception e) {
   System.out.println("doDropColumn error:" + e);
  }
 }
 */

 //刪除行家族
 private void doDropColumn(HBaseConfiguration config) {
  try {
   //HBase管理元件
   HBaseAdmin admin = new HBaseAdmin(config);

   //表格名稱
   String tableName = "ProductTable";

   //判斷表格是否已經存在
   if (admin.tableExists(tableName)) {
	//禁能該表格
	admin.disableTable(tableName);

	//如果存在，才可以修改綱要
	admin.deleteColumn(tableName, "price");

	//致能該表格
	admin.enableTable(tableName);
   }
  } catch (Exception e) {
   System.out.println("doDropColumn error:" + e);
  }
 }

 //建立資料表格
 private void doCreateTable(HBaseConfiguration config) {
  try {
   //HBase管理元件
   HBaseAdmin admin = new HBaseAdmin(config);

   //表格名稱
   String tableName = "ProductTable";

   //判斷表格是否已經存在
   if (!admin.tableExists(tableName)) {
	//如果不存在，便開始設定表格綱要

	//建立描述表格的物件
	HTableDescriptor tableDesc = new HTableDescriptor(tableName);

	//增加對行家族的描述
	HColumnDescriptor familyDesc = new HColumnDescriptor("spec");

	//增加對行家族的描述
	HColumnDescriptor familyDesc2 = new HColumnDescriptor("price");

	//將行家族的描述加到表格描述之中
	tableDesc.addFamily(familyDesc);
	tableDesc.addFamily(familyDesc2);

	//根據表格描述，建立表格
	admin.createTable(tableDesc);
   }
  } catch (Exception e) {
   System.out.println("doCreateTable error:" + e);
  }
 }

 //執行刪除一行資料的功能
 private void doDeleteColumn(HBaseConfiguration config) {
  try {
   //與單一HBase表格溝通的物件
   HTable htable = new HTable(config, "MovieTable");

   //包裹刪除功能的物件
   Delete del = new Delete("X-Men".getBytes());

   //設定所要刪除的行家族與修飾詞
   del = del.deleteColumn("cast".getBytes(), "Directed".getBytes());

   //執行刪除的工作
   htable.delete(del);
  } catch (Exception e) {
   System.out.println("doDeleteColumn error:" + e);
  }
 }

 //執行刪除行家族的功能
 private void doDeleteFamily(HBaseConfiguration config) {
  try {
   //與單一HBase表格溝通的物件
   HTable htable = new HTable(config, "MovieTable");

   //包裹刪除功能的物件
   Delete del = new Delete("X-Men".getBytes());

   //設定所要刪除的行家族
   del = del.deleteFamily("info".getBytes());

   //執行刪除的工作
   htable.delete(del);
  } catch (Exception e) {
   System.out.println("doDeleteFamily error:" + e);
  }
 }


 //執行刪除一列資料的功能
 private void doDeleteRow(HBaseConfiguration config) {
  try {
   //與單一HBase表格溝通的物件
   HTable htable = new HTable(config, "MovieTable");

   //包裹刪除功能的物件
   Delete del = new Delete("Harry Potter 7".getBytes());

   //執行刪除的工作
   htable.delete(del);
  } catch (Exception e) {
   System.out.println("doDeleteRow error:" + e);
  }
 }


 //執行修改資料的功能
 private void doUpdate(HBaseConfiguration config) {
  try {
   //與單一HBase表格溝通的物件
   HTable htable = new HTable(config, "MovieTable");

   //包裹修改功能的物件
   Put put = new Put("Harry Potter 7".getBytes());

   //設定所要修改的行家族的前置詞，修飾詞，及資料內容
   put.add("cast".getBytes(),"Starring".getBytes(), "Emma Watson".getBytes());

   //執行修改的工作
   htable.put(put);
  } catch (Exception e) {
   System.out.println("doPut error:" + e);
  }
 }


 //執行新增資料的功能
 private void doPut(HBaseConfiguration config) {
  try {
   //與單一HBase表格溝通的物件
   HTable htable = new HTable(config, "MovieTable");

   //包裹新增功能的物件
   Put put = new Put("Harry Potter 7".getBytes());

   //設定所要新增的行家族的前置詞，修飾詞，及資料內容
   put.add("info".getBytes(),"RunningTime".getBytes(), "2 hrs. 10 min.".getBytes());
   put.add("info".getBytes(),"ReleaseDate".getBytes(), "July 15th, 2011".getBytes());
   put.add("cast".getBytes(),"Starring".getBytes(), "Daniel Radcliffe".getBytes());
   put.add("cast".getBytes(),"Directed".getBytes(), "David Yates".getBytes());

   //執行新增的工作
   htable.put(put);
  } catch (Exception e) {
   System.out.println("doPut error:" + e);
  }
 }

 //執行查詢的功能
 private void doScan(HBaseConfiguration config) {
  try {
   //與單一HBase表格溝通的物件
   HTable htable = new HTable(config, "MovieTable");

   //包裹查詢功能的物件
   Scan scan = new Scan();

   //設定所要查詢的行家族的前置詞，及其修飾詞
   scan.addColumn("info".getBytes(),"RunningTime".getBytes());
   scan.addColumn("info".getBytes(),"ReleaseDate".getBytes());
   scan.addColumn("cast".getBytes(),"Starring".getBytes());
   scan.addColumn("cast".getBytes(),"Directed".getBytes());

   //設定查詢的起始列主鍵
   scan.setStartRow(Bytes.toBytes("X-Men"));

   //設定查詢的終止列主鍵
   scan.setStopRow(Bytes.toBytes("X-Men"));

   //輪詢查詢結果
   for(Result result : htable.getScanner(scan)) {
	//取出每一筆查詢結果
	String rTime = new String(result.getValue("info".getBytes(),"RunningTime".getBytes()));
	String rDate = new String(result.getValue("info".getBytes(),"ReleaseDate".getBytes()));
	String actor = new String(result.getValue("cast".getBytes(),"Starring".getBytes()));
	String director = new String(result.getValue("cast".getBytes(),"Directed".getBytes()));

	System.out.println("rTime:" + rTime + ",rDate:" + rDate + ",actor:" + actor + ",director:" + director);
   }
  } catch (Exception e) {
   System.out.println("doScan error:" + e);
  }
 }

 public static void main(String[] args) {
  new HbaseTest();
 }
}
