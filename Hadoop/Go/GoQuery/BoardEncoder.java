
/*
*	接收到圍棋當前盤面資料，排除邊界選取要盤面訊息，
* 先以2進制方式黑子代表01，白子代表10
* 再轉換為10進制，其結果為編碼內容
*/

public class BoardEncoder {
	// 編碼的內容
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

	// 盤面編碼
	public static String BoardEncoed(String Pattern_Board, String BoardSize) {
		int code_value;
		int size = Integer.valueOf(BoardSize);
		String[][] pattern_array = new String[size][size];
		char dst[];
		String[] pattern_temp = new String[size];
		int count = 0;
		String Pattern = "";
		String code = "";

		// 排除邊界，選取要得位置
		int start = size + 3;
		for (int i = 0; i < size; i++) {
			Pattern += Pattern_Board.substring(start, start + size);
			start = (size + 3) + ((i + 1) * (size + 2));
		}

		dst = Pattern.toCharArray();
		for (int i = 0; i < size; i++) {
			pattern_temp[i] = "";
		}

		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				pattern_array[i][j] = String.valueOf(dst[count]);
				count++;
			}
		}
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				pattern_temp[i] += pattern_array[i][j];

			}
		}

		for (int i = 0; i < size; i++) {
			dst = pattern_temp[i].toCharArray();
			code_value = hashcode_pattern(dst, size);
			if (i == size - 1) {
				code += String.valueOf(code_value);
			} else {
				code += String.valueOf(code_value) + " ";
			}
		}
		// System.out.println("WriteData:" + code);
		return code;
	}
}
