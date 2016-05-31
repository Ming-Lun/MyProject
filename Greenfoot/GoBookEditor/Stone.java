import greenfoot.*;

/*
 * 圍棋棋譜邊碼器畫面參數設定
*/
public class Stone extends Actor {
	int color, size, posX, posY;

	public Stone(int c, int s, int x, int y) {
		color = c;
		size = s;
		posX = x;
		posY = y;
	}

	public void setColor(int c) {
		color = c;

		if (color == 0) {
			setImage("./images/empty.jpg");
		} else {
			String imgPath = "./images/";

			switch (color) {
			case 1:
				imgPath += "bstone";
				break;
			case 2:
				imgPath += "wstone";
				break;
			}

			switch (size) {
			case 9:
				imgPath += "9.jpg";
				break;
			case 13:
				imgPath += "13.jpg";
				break;
			case 19:
				imgPath += "19.jpg";
				break;
			}

			setImage(imgPath);
		}

	}

	public void act() {
		// Add your action code here.
	}
}
