import java.util.ArrayList;
/*
 * 圍棋棋譜邊碼器畫面呈現
*/
class GoBoard {
	public static final int PASS = -1;
	public static final int EMPTY = 0;
	public static final int BLACK = 1;
	public static final int WHITE = 2;

	private final int BoardSize = GoStone.size;
	private final int MaxStringNum = 200;
	private final int[][] adj = { { 0, -1 }, { 1, 0 }, { 0, 1 }, { -1, 0 } };

	private GoStone[][] board = new GoStone[BoardSize][BoardSize];
	private GoString[] strings = new GoString[MaxStringNum];
	private GoString[][] stringOfStone = new GoString[BoardSize][BoardSize];

	public ArrayList<GoStone[][]> BR;

	public GoBoard() {
		for (int i = 0; i < MaxStringNum; i++) // prepare strings
		{
			strings[i] = new GoString(i);
		}

		for (int i = 0; i < BoardSize; i++) // prepare stones on board
		{
			for (int j = 0; j < BoardSize; j++) {
				board[i][j] = new GoStone(BoardSize, i + 1, j + 1, EMPTY);
				stringOfStone[i][j] = null;
			}
		}

		BR = new ArrayList<GoStone[][]>(400);
		BR.add(cloneBoard());
	}

	public boolean makeMove(GoStone m) {
		GoStone nb = null;
		boolean hasAdjFriend = false;

		if (m.color > 0) // not pass
		{
			int i = 0;
			for (i = 0; i < 4; i++) // consider 4 neighbors
			{
				nb = getAdjStone(m, i); // neighbor of m at direction i
				if ((nb != null) && (nb.color == flipColor(m.color))) {
					GoString nbString = stringOfStone[nb.x - 1][nb.y - 1];
					if (nbString.getNumLiberties() == 1) {
						captureString(nbString); // capture neighnor's string
					} else {
						GoPoint np = new GoPoint(m.size, m.x, m.y);
						nbString.removeLiberty(np); // remove liberty m from
													// neighnor's string
					}
				}
			}

			for (i = 0; i < 4; i++) {
				nb = getAdjStone(m, i);
				if ((nb != null) && (nb.color == m.color)) {
					stringOfStone[nb.x - 1][nb.y - 1].addStone(m,
							getLiberties(m)); // add m to neighbor's string
					stringOfStone[m.x - 1][m.y - 1] = stringOfStone[nb.x - 1][nb.y - 1]; // change
																							// m's
																							// string
																							// to
																							// neighbor's
																							// string
					hasAdjFriend = true;
					break;
				}
			}

			if (i < 3) {
				for (int j = i + 1; j < 4; j++) {
					nb = getAdjStone(m, j);
					if ((nb != null) && (nb.color == m.color)) {
						if (!stringOfStone[m.x - 1][m.y - 1]
								.equals(stringOfStone[nb.x - 1][nb.y - 1])) {
							GoString nst = stringOfStone[nb.x - 1][nb.y - 1]
									.clone();
							stringOfStone[m.x - 1][m.y - 1]
									.merge(stringOfStone[nb.x - 1][nb.y - 1]);
							stringOfStone[m.x - 1][m.y - 1].removeLiberty(m);
							for (GoStone p : nst.stones) {
								stringOfStone[p.x - 1][p.y - 1] = stringOfStone[m.x - 1][m.y - 1];
							}
						}
					}
				}
			}

			if (!hasAdjFriend) {
				int k;
				for (k = 0; k < MaxStringNum; k++) {
					if (strings[k].getNumStones() == 0) {
						break;
					}
				}

				strings[k].color = m.color;
				strings[k].addStone(m, getLiberties(m));
				stringOfStone[m.x - 1][m.y - 1] = strings[k];
			}
		}

		board[m.x - 1][m.y - 1].color = m.color;
		BR.add(cloneBoard());

		return true;
	}

	private GoStone getAdjStone(GoStone m, int i) {
		GoStone result = null;

		int x = m.x + adj[i][0];
		int y = m.y + adj[i][1];
		if (x > 0 && y > 0 && x <= BoardSize && y <= BoardSize) {
			if (board[x - 1][y - 1].color > 0) {
				result = board[x - 1][y - 1];
			}
		}

		return result;
	}

	private ArrayList<GoPoint> getLiberties(GoStone p) {
		ArrayList<GoPoint> result = new ArrayList<GoPoint>(400);

		for (int i = 0; i < 4; i++) {
			int x = p.x + adj[i][0];
			int y = p.y + adj[i][1];
			if (x > 0 && y > 0 && x <= BoardSize && y <= BoardSize) {
				GoStone nb = board[x - 1][y - 1];
				if (nb.color == EMPTY) {
					GoPoint np = new GoPoint(nb.size, nb.x, nb.y);
					result.add(np);
				}
			}
		}

		return result;
	}

	public void captureString(GoString s) {
		int c = s.color;

		if (s.getNumStones() > 0) {
			for (GoStone stone : s.stones) {
				board[stone.x - 1][stone.y - 1].color = EMPTY;
				stringOfStone[stone.x - 1][stone.y - 1] = null;
			}

			for (GoStone stone : s.stones) {
				for (int i = 0; i < 4; i++) {
					GoStone nb = getAdjStone(stone, i);
					if ((nb != null) && (nb.color == flipColor(c))) {
						GoPoint np = new GoPoint(
								board[stone.x - 1][stone.y - 1].size,
								board[stone.x - 1][stone.y - 1].x,
								board[stone.x - 1][stone.y - 1].y);
						stringOfStone[nb.x - 1][nb.y - 1].addLiberty(np);
					}
				}
			}

			s.delAll();
		}
	}

	public int flipColor(int c) {
		return c % 2 + 1;
	}

	public void setPointColor(GoStone p, int c) {
		board[p.x - 1][p.y - 1].color = c;
	}

	public int getPointColor(GoStone p) {
		return board[p.x - 1][p.y - 1].color;
	}

	public GoStone[][] cloneBoard() {
		GoStone[][] result = new GoStone[BoardSize][BoardSize];

		for (int i = 0; i < BoardSize; i++) {
			for (int j = 0; j < BoardSize; j++) {
				result[i][j] = board[i][j].clone();
			}
		}

		return result;
	}

	public void showBoard() {
		for (int i = 0; i < BoardSize; i++) {
			for (int j = 0; j < BoardSize; j++) {
				System.out.println(board[i][j].color);
			}
			System.out.println();
		}
		System.out.println();
	}
}
