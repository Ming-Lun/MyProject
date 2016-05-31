/*
 * 圍棋棋譜落子位置
*/

public class GoStone extends GoPoint implements Cloneable {
	public static int size;
	public int color;

	public GoStone(int size, int x, int y, int color) {
		super(size, x, y);
		this.color = color;
		this.size = size;
	}

	public void setColor(int c) {
		this.color = c;
	}

	public int hashcode() {
		return y * size + x;
	}

	public boolean equals(GoStone p) {
		if (this.hashcode() == p.hashcode()) {
			return true;
		} else {
			return false;
		}
	}

	public GoStone clone() {
		GoStone result = new GoStone(this.size, this.x, this.y, this.color);

		return result;
	}
}
