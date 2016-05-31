import java.util.ArrayList;

public class GoString implements Cloneable {
	public int strID;
	public int color;
	public ArrayList<GoStone> stones;
	public ArrayList<GoPoint> liberties;

	public GoString(int i) {
		strID = i;
		color = GoBoard.EMPTY;
		stones = new ArrayList<GoStone>(400);
		liberties = new ArrayList<GoPoint>(400);
	}

	void addStone(GoStone p) {
		for (GoStone i : stones) {
			if (i.equals(p)) {
				return;
			}
		}
		stones.add(p);
		removeLiberty((GoPoint) p);
	}

	void addStone(GoStone s, ArrayList<GoPoint> lb) {
		addStone(s);
		addLiberties(lb);
		removeLiberty((GoPoint) s);
	}

	void addStones(ArrayList<GoStone> st) {
		for (GoStone p : st) {
			addStone(p);
		}
	}

	void removeStone(GoStone s) {
		for (GoStone p : stones) {
			if (p.equals(s)) {
				stones.remove(p);
				return;
			}
		}
	}

	void addLiberty(GoPoint p) {
		for (GoPoint i : liberties) {
			if (i.equals(p)) {
				return;
			}
		}

		GoPoint np = new GoPoint(p.size, p.x, p.y);
		liberties.add(np);
	}

	void addLiberties(ArrayList<GoPoint> lb) {
		for (GoPoint p : lb) {
			addLiberty(p);
		}
	}

	void removeLiberty(GoPoint p) {
		for (GoPoint i : liberties) {
			if (i.equals(p)) {
				liberties.remove(i);
				return;
			}
		}
	}

	void merge(GoString str) {
		addStones(str.stones);
		addLiberties(str.liberties);
		str.delAll();
	}

	void delAll() {
		color = GoBoard.EMPTY;
		stones.clear();
		liberties.clear();
	}

	public int getID() {
		return this.strID;
	}

	public int getNumStones() {
		return stones.size();
	}

	public int getNumLiberties() {
		return liberties.size();
	}

	public ArrayList<GoStone> getStones() {
		return stones;
	}

	public ArrayList<GoPoint> getLiberties() {
		return liberties;
	}

	public boolean equals(GoString s) {
		if (this.strID == s.strID) {
			return true;
		}

		return false;
	}

	public GoString clone() {
		GoString nst = new GoString(strID);

		for (GoStone s : stones) {
			nst.addStone(s);
		}
		for (GoPoint p : liberties) {
			nst.addLiberty(p);
		}

		return nst;
	}
}
