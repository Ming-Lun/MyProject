import greenfoot.*;

/*
 * 圍棋棋譜邊碼器畫面參數設定
*/
public class GameWorld extends World {
	int size;
	int posX0, posY0, offsetX, offsetY;
	Stone[][] stones;
	int[][] board;

	public GameWorld(int s) {
		super(600, 590, 1);
		size = s;

		switch (size) {
		case 9:
			setBackground(new GreenfootImage("./images/board9.jpg"));
			posX0 = 73;
			posY0 = 66;
			offsetX = 57;
			offsetY = 57;
			stones = new Stone[9][9];
			board = new int[9][9];
			setStones(9);
			break;
		case 13:
			setBackground(new GreenfootImage("./images/board13.jpg"));
			posX0 = 53;
			posY0 = 48;
			offsetX = 41;
			offsetY = 41;
			stones = new Stone[13][13];
			board = new int[13][13];
			setStones(13);
			break;
		case 19:
			setBackground(new GreenfootImage("./images/board19.jpg"));
			posX0 = 41;
			posY0 = 39;
			offsetX = 29;
			offsetY = 29;
			stones = new Stone[19][19];
			board = new int[19][19];
			setStones(19);
			break;
		}
	}

	private void setStones(int s) {
		for (int i = 0; i < s; i++) {
			for (int j = 0; j < s; j++) {
				stones[i][j] = new Stone(0, s, i, j);
				stones[i][j].setImage("./images/empty.jpg");
				addObject(stones[i][j], posX0 + offsetX * i, posY0 + offsetY
						* j);
			}
		}
	}
}
