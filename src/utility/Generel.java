package utility;

import java.util.Random;

public abstract class Generel {
	public static void main(String[] args) {

		testPrintBoard(board);

	}
	public static  String[] board = new String[20]; // Board to hold randomly generated map.
	/*{    // 20x20
			"wwwwwwwwwwwwwwwwwwww",
			"w        ww        w",
			"w w  w  www w  w  ww",
			"w w  w   ww w  w  ww",
			"w  w               w",
			"w w w w w w w  w  ww",
			"w w     www w  w  ww",
			"w w     w w w  w  ww",
			"w   w w  w  w  w   w",
			"w     w  w  w  w   w",
			"w ww ww        w  ww",
			"w  w w    w    w  ww",
			"w        ww w  w  ww",
			"w         w w  w  ww",
			"w        w     w  ww",
			"w  w              ww",
			"w  w www  w w  ww ww",
			"w w      ww w     ww",
			"w   w   ww  w      w",
			"wwwwwwwwwwwwwwwwwwww"
	}; */


	public static String[] constructBoard(int width, int height) {
		String[] board = initBoardEdges(width, height);

		//int numObstacles = 75;
		int numObstacles = (int) ((width * height) % ((width*height)/3.7));
		System.out.println("OBSTACLES COUNT: " + numObstacles);
		placeObstacles(board, numObstacles, width, height);

		return board;
	}
	/** Laver et tomt board efter de ønskede dimensioner */
	private static String[] initBoardEdges(int width, int height) {
		String[] board = new String[height];

		for (int i = 0; i < height; i++) {
			StringBuilder boardRow = new StringBuilder();

			for (int j = 0; j < width; j++) {
				if (i == 0 || i == height - 1 || j == 0 || j == width - 1) {
					boardRow.append('w');
				} else {
					boardRow.append(' ');
				}
			}
			board[i] = boardRow.toString();
		}
		return board;
	}

	/** Fylder et 'tomt' board med tilfældigt placerede brikker, bestemt efter
	 * antalsparameteren numObstacles */
	private static void placeObstacles(String[] board, int numObstacles, int width, int height) {
		Random random = new Random();

		for (int i = 0; i < numObstacles; i++) {
			int obstacleX = random.nextInt(width - 2) + 1;
			int obstacleY = random.nextInt(height - 2) + 1;

			if (board[obstacleY].charAt(obstacleX) == ' ') {
				StringBuilder rowBuilder = new StringBuilder(board[obstacleY]);
				rowBuilder.setCharAt(obstacleX, 'w');
				board[obstacleY] = rowBuilder.toString();
			} else {
				i--;
			}
		}
	}

	private static void testPrintBoard(String[] board) {
		int i = 0;
		for (String row : board) {
			System.out.printf("%-2d : %s\n", i++, row);
		}
	}

}
