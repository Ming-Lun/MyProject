import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;
import java.util.StringTokenizer;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
/*
* 讀取SGF檔案，取得所要的資料
*/
public class SGF extends JFrame {
	static String KomiNumber;
	static String WinColor;
	int Count = 0;
	String letters[] = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k",
			"l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x",
			"y", "z", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K",
			"L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X",
			"Y", "Z" };

	private ArrayList<GoStone> moveSEQ;

	private Scanner sc;

	public SGF() throws IOException {
		// JFileChooser fileChooser = new JFileChooser();
		// fileChooser.setCurrentDirectory(new File("."));
		// fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		//
		// fileChooser.showOpenDialog(null);
		// String Path = fileChooser.getSelectedFile().getAbsolutePath();
		// String []FilenName=getFileList(Path);
		// for (int i = 0;i < FilenName.length;i++)
		// {
		// File file = new File(""+Path+"\\"+FilenName[i]);
		// bookname=FilenName[i];
		// moveSEQ = getMovesFromSGF(file);
		// }

		// JFileChooser fileopen = new JFileChooser();
		// fileopen.setCurrentDirectory(new File("."));
		// FileFilter filter = new FileNameExtensionFilter("sgf File", "sgf");
		// fileopen.addChoosableFileFilter(filter);
		//
		// int ret = fileopen.showDialog(null, "Open file");
		// //get the bookname
		// bookname=fileopen.getSelectedFile().getName();
		//
		// if (ret == JFileChooser.APPROVE_OPTION)
		// {
		// File file = fileopen.getSelectedFile();
		// moveSEQ = getMovesFromSGF(file);
		// }

	}

	public ArrayList<GoStone> getMovesFromSGF(File f) throws IOException {
		ArrayList<GoStone> moves = new ArrayList<GoStone>(400);
		int size;
		int x, y, color;

		FileReader fr = new FileReader(f);
		String content = "";
		sc = new Scanner(fr);
		while (sc.hasNext()) {
			content += sc.next();
		}
		fr.close();

		// get tokens by ";"
		StringTokenizer tokenizer = new StringTokenizer(content, ";");

		// get size from SZ[.]
		tokenizer.nextToken();
		String BoarDataString = tokenizer.nextToken();
		String moveString = BoarDataString;
		String SizeData;
		SizeData = BoarDataString.substring(BoarDataString.indexOf("SZ") + 2);
		SizeData = SizeData.substring(1, SizeData.indexOf(']'));
		size = Integer.parseInt(SizeData);

		// get Komi from KM[.]
		KomiNumber = BoarDataString.substring(BoarDataString.indexOf("KM") + 2);
		KomiNumber = KomiNumber.substring(1, KomiNumber.indexOf(']'));

		// get Win Color from KM[.]
		String WinData;
		String[] temp;
		WinData = BoarDataString.substring(BoarDataString.indexOf("RE") + 2);
		WinData = WinData.substring(1, WinData.indexOf(']'));
		temp = WinData.split("\\+");
		WinColor = temp[0];

		if (moveString.indexOf("AB") != -1) {
			moveString = moveString.substring(moveString.indexOf("AB") + 2);
			moveString = moveString.substring(1, moveString.indexOf(']'));
			color = 1;
			x = from_sgf_coordinate(moveString.charAt(0));
			y = from_sgf_coordinate(moveString.charAt(1));
			GoStone p = new GoStone(size, x, y, color);
			moves.add(p);
			Count++;
		}

		// get move from B[.] or W[.]
		while (tokenizer.hasMoreTokens()) {
			moveString = tokenizer.nextToken();
			if (moveString.startsWith("B")) {
				moveString = moveString.substring(moveString.indexOf("B") + 1);
				moveString = moveString.substring(1, moveString.indexOf(']'));
				color = 1;
				if (moveString.equals("")) {
					x = y = -1;
					color = -1;
				} else {
					x = from_sgf_coordinate(moveString.charAt(0));
					y = from_sgf_coordinate(moveString.charAt(1));
				}
				GoStone p = new GoStone(size, x, y, color);
				moves.add(p);
				Count++;
			} else if (moveString.startsWith("W")) {
				moveString = moveString.substring(moveString.indexOf("W") + 1);
				moveString = moveString.substring(1, moveString.indexOf(']'));
				color = 2;
				if (moveString.equals("")) {
					x = y = -1;
					color = -1;
				} else {
					x = from_sgf_coordinate(moveString.charAt(0));
					y = from_sgf_coordinate(moveString.charAt(1));
				}
				GoStone p = new GoStone(size, x, y, color);
				moves.add(p);
				Count++;
			}
		}

		return moves;
	}

	public int from_sgf_coordinate(char ch) {
		for (int i = 0; i < 52; i++) {
			if (ch == letters[i].charAt(0)) {
				return i + 1;
			}
		}

		return -1;
	}

	public ArrayList<GoStone> getMoveSEQ() {
		return moveSEQ;
	}
}
