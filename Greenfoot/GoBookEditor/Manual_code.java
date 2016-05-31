import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/*
* 棋譜編碼，以及記錄棋譜相對應落子位置
*/
public class Manual_code {

	List<String> pattern_nn = new ArrayList<String>();
	List<String> p_nn = new ArrayList<String>();
	static List<String> BookRecord = new ArrayList<String>();
	static int bookcount = 0;
	// List<String> final_pattern = new ArrayList<String>();
	String pattern_open[] = new String[3];
	String pattern_open_array[][];
	String pattern_transform[];

	int first_num;
	int second_num;
	String point_x;
	String point_y;
	int size;
	char[] dst;

	int count;
	String pattern[][];
	String pattern_name;

	public static int hashcode_pattern(char[] pattern, int size) {
		int pattern_code = 0;

		for (int i = 0; i < size; i++) {
			pattern_code = pattern_code << 2;
			switch (pattern[i]) {

			case '1':// black
				pattern_code += 1;
				break;
			case '2':// white
				pattern_code += 2;
				break;

			}
		}

		return pattern_code;
	}

	public static String[] pattern_transform(String[][] pattern, int first_num,
			String point_x, String point_y, String pattern_name) {
		String pattern_transform[] = new String[16];
		String pattren_before[] = new String[16];
		int code_value;

		int size;

		String pattern_array[][] = new String[first_num][first_num];
		String pattern_temp[] = new String[first_num];
		String all_code[] = new String[16];

		String[][] transform_1 = new String[first_num][first_num];
		String[][] transform_2 = new String[first_num][first_num];
		String[][] transform_3 = new String[first_num][first_num];
		String[][] transform_4 = new String[first_num][first_num];
		String[][] transform_5 = new String[first_num][first_num];
		String[][] transform_6 = new String[first_num][first_num];
		String[][] transform_7 = new String[first_num][first_num];
		String[][] transform_8 = new String[first_num][first_num];
		String[][] transform_9 = new String[first_num][first_num];
		String[][] transform_10 = new String[first_num][first_num];
		String[][] transform_11 = new String[first_num][first_num];
		String[][] transform_12 = new String[first_num][first_num];
		String[][] transform_13 = new String[first_num][first_num];
		String[][] transform_14 = new String[first_num][first_num];
		String[][] transform_15 = new String[first_num][first_num];

		char dst[] = null;

		transform_1 = pattern_rotate(pattern, first_num);
		transform_2 = pattern_rotate(transform_1, first_num);
		transform_3 = pattern_rotate(transform_2, first_num);

		transform_4 = pattern_corresponding(pattern, first_num);
		transform_5 = pattern_corresponding(transform_1, first_num);
		transform_6 = pattern_corresponding(transform_2, first_num);
		transform_7 = pattern_corresponding(transform_3, first_num);

		transform_8 = pattern_mirroring(pattern, first_num);
		transform_9 = pattern_rotate(transform_8, first_num);
		transform_10 = pattern_rotate(transform_9, first_num);
		transform_11 = pattern_rotate(transform_10, first_num);

		transform_12 = pattern_corresponding(transform_8, first_num);
		transform_13 = pattern_corresponding(transform_9, first_num);
		transform_14 = pattern_corresponding(transform_10, first_num);
		transform_15 = pattern_corresponding(transform_11, first_num);

		for (int i = 0; i < 16; i++) {
			pattren_before[i] = "";

		}
		for (int i = 0; i < first_num; i++) {
			for (int j = 0; j < first_num; j++) {
				pattren_before[0] += pattern[i][j];
				pattren_before[1] += transform_1[i][j];
				pattren_before[2] += transform_2[i][j];
				pattren_before[3] += transform_3[i][j];
				pattren_before[4] += transform_4[i][j];
				pattren_before[5] += transform_5[i][j];
				pattren_before[6] += transform_6[i][j];
				pattren_before[7] += transform_7[i][j];
				pattren_before[8] += transform_8[i][j];
				pattren_before[9] += transform_9[i][j];
				pattren_before[10] += transform_10[i][j];
				pattren_before[11] += transform_11[i][j];
				pattren_before[12] += transform_12[i][j];
				pattren_before[13] += transform_13[i][j];
				pattren_before[14] += transform_14[i][j];
				pattren_before[15] += transform_15[i][j];
			}
		}

		for (int i = 0; i < 16; i++) {
			all_code[i] = "";
		}

		for (int i = 0; i < 16; i++) {
			size = first_num;
			dst = pattren_before[i].toCharArray();

			for (int j = 0; j < first_num; j++) {
				pattern_temp[j] = "";
			}
			int count = 0;
			for (int j = 0; j < first_num; j++) {
				for (int k = 0; k < first_num; k++) {
					pattern_array[j][k] = String.valueOf(dst[count]);
					count++;
				}
			}

			for (int j = 0; j < first_num; j++) {
				for (int k = 0; k < first_num; k++) {
					pattern_temp[j] += pattern_array[j][k];

				}
			}
			for (int j = 0; j < first_num; j++) {
				dst = pattern_temp[j].toCharArray();
				code_value = hashcode_pattern(dst, size);
				if (j == first_num - 1) {
					all_code[i] += String.valueOf(code_value);
				} else {
					all_code[i] += String.valueOf(code_value) + ",";
				}

			}
			pattern_transform[i] = all_code[i] + ":" + point_x + "," + point_y
					+ "_" + pattern_name;
		}

		return pattern_transform;
	}

	public static String[][] pattern_mirroring(String[][] pattern, int first_num) {

		String pattern_mirroring[][] = new String[first_num][first_num];

		int mid, mid_left, mid_right;
		mid = first_num / 2;
		if ((first_num % 2) == 0) {
			for (int i = 0; i < first_num; i++) {
				mid_left = mid - 1;
				mid_right = mid;

				for (int j = 0; j < mid; j++) {
					pattern_mirroring[i][mid_right] = pattern[i][mid_left];
					pattern_mirroring[i][mid_left] = pattern[i][mid_right];
					mid_right++;
					mid_left--;
				}
			}
		} else {

			for (int i = 0; i < first_num; i++) {
				mid_left = mid - 1;
				mid_right = mid + 1;

				for (int j = 0; j < mid; j++) {
					pattern_mirroring[i][mid] = pattern[i][mid];

					pattern_mirroring[i][mid_right] = pattern[i][mid_left];
					pattern_mirroring[i][mid_left] = pattern[i][mid_right];
					mid_right++;
					mid_left--;
				}
			}

		}
		return pattern_mirroring;

	}

	public static String[][] pattern_rotate(String[][] pattern, int first_num) {

		String p_rotate = "";
		char dst[];
		int counter = 0;

		String pattern_rotate[][] = new String[first_num][first_num];

		for (int i = 0; i < first_num; i++) {
			for (int j = first_num - 1; j >= 0; j--) {
				p_rotate += pattern[j][i];

			}
		}

		dst = p_rotate.toCharArray();
		for (int i = 0; i < first_num; i++) {
			for (int j = 0; j < first_num; j++) {
				pattern_rotate[i][j] = String.valueOf(dst[counter]);
				counter++;
			}
		}

		return pattern_rotate;

	}

	public static String[][] pattern_corresponding(String[][] pattern,
			int first_num) {

		String pattern_before_corresponding = "";

		String pattern_after_corresponding = "";

		String pattern_corresponding[][] = new String[first_num][first_num];
		char dst[];
		int counter = 0;

		for (int i = 0; i < first_num; i++) {
			for (int j = 0; j < first_num; j++) {
				pattern_before_corresponding += pattern[i][j];

			}
		}
		dst = pattern_before_corresponding.toCharArray();

		for (int i = 0; i < first_num * first_num; i++) {

			switch (dst[i]) {
			case '0':
				pattern_after_corresponding += "0";
				break;
			case '1':// black to white
				pattern_after_corresponding += "2";
				break;
			case '2':// white to black
				pattern_after_corresponding += "1";
				break;

			}
		}

		dst = pattern_after_corresponding.toCharArray();
		counter = 0;
		for (int i = 0; i < first_num; i++) {
			for (int j = 0; j < first_num; j++) {
				pattern_corresponding[i][j] = String.valueOf(dst[counter]);
				counter++;
			}
		}
		return pattern_corresponding;

	}

	// insert data
	public void insert_pattern(int first, int second, String p, int point_x,
			int point_y, String p_name1, String p_name2) {
		dst = p.toCharArray();
		first_num = first;
		second_num = second;
		pattern = new String[first_num][second_num];
		String PointX = String.valueOf(point_x);
		String PointY = String.valueOf(point_y);

		count = 0;
		for (int i = 0; i < first_num; i++) {
			for (int j = 0; j < first_num; j++) {
				pattern[i][j] = String.valueOf(dst[count]);
				count++;

			}
		}
		pattern_transform = pattern_transform(pattern, first_num, PointX,
				PointY, p_name1);

		if (pattern_nn.size() == 0) {

			for (int i = 0; i < pattern_transform.length; i++) {
				pattern_nn.add(pattern_transform[i]);
			}
		} else {
			for (int i = 0; i < pattern_transform.length; i++) {
				p_nn.add(pattern_transform[i]);
			}
			for (int i = 0; i < pattern_nn.size(); i++) {
				for (int j = 0; j < p_nn.size(); j++) {
					if (pattern_nn.get(i).equals(p_nn.get(j))) {
						p_nn.remove(j);
						--j;
					}
				}
			}
			for (int i = 0; i < p_nn.size(); i++) {
				pattern_nn.add(p_nn.get(i));
				p_nn.remove(i);
				--i;
			}
		}

	}

	// ReadBook old data
	public static void ReadBook(String name) {
		String s;
		boolean status = true;
		FileInputStream fstream = null;
		try {
			fstream = new FileInputStream(name + ".book");
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}
		DataInputStream in = new DataInputStream(fstream);

		Scanner scan = new Scanner(new InputStreamReader(in));
		while (scan.hasNext()) {
			s = scan.nextLine();
			if (status == true) {
				bookcount = Integer.valueOf(s);
				status = false;
			} else {

				BookRecord.add(s);
			}
			try {
				in.close();
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
	}

	// output data
	public void export_code(String name) throws IOException {

		try {
			String filepath = name + ".book";
			FileOutputStream fileOutputStream;
			fileOutputStream = new FileOutputStream(filepath);
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
					fileOutputStream);

			List<String> final_pattern = new ArrayList<String>();

			Set<String> pattern_set = new HashSet<String>();

			for (int i = 0; i < pattern_nn.size(); i++) {
				pattern_set.add(pattern_nn.get(i));
			}

			Object[] tempArray = pattern_set.toArray();
			for (int i = 0; i < tempArray.length; i++) {
				final_pattern.add((String) tempArray[i]);
			}
			for (int i = 0; i < BookRecord.size(); i++) {
				for (int j = 0; j < final_pattern.size(); j++) {
					String temp[];

					temp = final_pattern.get(j).split("_");
					if (BookRecord.get(i).contains(temp[0]) == true) {
						final_pattern.remove(j);
						--j;
					}
				}
			}
			for (int i = 0; i < final_pattern.size(); i++) {
				BookRecord.add(final_pattern.get(i));
				final_pattern.remove(i);
				--i;
			}

			outputStreamWriter.write((bookcount + 1) + "\r\n");
			for (int i = 0; i < BookRecord.size(); i++) {
				outputStreamWriter.write(BookRecord.get(i) + "\r\n");
			}
			final_pattern.clear();
			BookRecord.clear();
			outputStreamWriter.close();
			pattern_nn.clear();
			pattern_nn.clear();
			bookcount = 0;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

}
