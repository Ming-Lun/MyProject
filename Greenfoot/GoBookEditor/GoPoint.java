import java.awt.Point;

/*
 * 圍棋棋譜落子位置
*/

public class GoPoint extends Point implements Cloneable {
	public static int size;

	public GoPoint(int size, int x, int y) {
		super(x, y);
		this.size = size;
	}

	public int hashcode() {
		return y * size + x;
	}

	public boolean equals(GoPoint p) {
		if (this.hashcode() == p.hashcode()) {
			return true;
		} else {
			return false;
		}
	}

	public GoPoint clone() {
		GoPoint result = new GoPoint(this.size, this.x, this.y);

		return result;
	}
}
