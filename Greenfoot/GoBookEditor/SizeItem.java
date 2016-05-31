import greenfoot.*;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JFileChooser;

/*
 * 圍棋棋譜邊碼器畫面參數設定
*/
public class SizeItem extends Actor {

	int size;
	int wc;// WinColor to int

	// String KomiNumber="7.5";

	public SizeItem(int s) {
		size = s;

		switch (size) {
		case 9:
			this.setImage("./images/button-9.png");
			break;
		case 13:
			this.setImage("./images/button-13.png");
			break;
		case 19:
			this.setImage("./images/button-19.png");
			break;
		}
	}

	public void act() {
		if (Greenfoot.mouseClicked(this)) {
			GameWorld gw = new GameWorld(size);
			Greenfoot.setWorld(gw);

			ArrayList<GoStone> moveSEQ = null;
			SGF sgf = null;
			int[][] LastRecord;

			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setCurrentDirectory(new File("."));
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

			fileChooser.showOpenDialog(null);
			String Path = fileChooser.getSelectedFile().getAbsolutePath();
			String[] FilenName = getFileList(Path);

			String PassNane1, PassNane2 = null, color;

			for (int point = 0; point < FilenName.length; point++) {
				Manual_code code = new Manual_code();

				File file = new File("" + Path + "\\" + FilenName[point]);
				String now_record = "";
				String PassP = "";
				try {
					sgf = new SGF();
					moveSEQ = sgf.getMovesFromSGF(file);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				GoBoard gb = new GoBoard();
				LastRecord = new int[GoStone.size][GoStone.size];
				for (GoStone p : moveSEQ) {
					gb.makeMove(p);
				}

				if (SGF.WinColor.equals("B")) {
					wc = 1;
					color = "Black";
				} else {
					wc = 2;
					color = "White";
				}
				ArrayList<GoStone[][]> br = gb.BR;
				PassNane1 = color + "-" + SGF.KomiNumber + "-"
						+ FilenName[point] + "-" + getDateTime();
				PassNane2 = color + "-" + SGF.KomiNumber;
				for (GoStone[][] record : br) {
					for (int i = 0; i < GoStone.size; i++) {
						for (int j = 0; j < GoStone.size; j++) {
							if (record[i][j].color == wc
									&& record[i][j].color != LastRecord[i][j]) {
								for (int k = 0; k < GoStone.size; k++) {
									for (int l = 0; l < GoStone.size; l++) {
										PassP += LastRecord[k][l];
									}
								}
								code.insert_pattern(GoStone.size, GoStone.size,
										PassP, record[i][j].x, record[i][j].y,
										PassNane1, PassNane2);
							}
							PassP = "";
							gw.stones[i][j].setColor(record[i][j].color);
							now_record += record[i][j].color;
						}
					}
					for (int i = 0; i < GoStone.size; i++) {
						for (int j = 0; j < GoStone.size; j++) {

							LastRecord[i][j] = Integer.valueOf(String
									.valueOf(now_record.charAt(i * GoStone.size
											+ j)));
						}
					}
					now_record = "";
					Greenfoot.delay(30);
				}
				file = new File(PassNane2 + ".book");

				if (file.exists()) {
					code.ReadBook(PassNane2);
				}
				try {
					code.export_code(PassNane2);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			System.out.println("Allwork is Over");
		}
	}

	public String getDateTime() {
		SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
		Date date = new Date();
		String strDate = sdFormat.format(date);
		return strDate;
	}

	public String[] getFileList(String folderPath) {
		StringBuffer fileList = new StringBuffer();
		String[] filename = null;
		try {
			java.io.File folder = new java.io.File(folderPath);
			String[] list = folder.list();
			filename = list;
		} catch (Exception e) {
			System.out.println("error");
		}

		return filename;
	}
}
