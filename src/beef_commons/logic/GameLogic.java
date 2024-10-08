package beef_commons.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import gui.Gui;
import beef_commons.utility.Generel;
import beef_commons.utility.PosXY;


public abstract class GameLogic {
public static List<Player> players = new ArrayList<>(); //TODO LUC: skal denne stå her, eller henvise til en anden List?

	
	
	public static Player makePlayer(String name) {
		Player me;
		PosXY pos = getRandomFreePosition();
		me = new Player(name,pos,"up");
		players.add(me);
		return me;
	};

	/** finds a random new position which is:
	 * 1) not wall
	 * 2) not occupied by other players  */
	public static PosXY getRandomFreePosition()
	{
		if (Generel.board[0].isEmpty()) {
			Generel.board = Generel.constructBoard(20,20);
			System.out.println("Board is null: constructing new board.");
		}

		int x = 1;
		int y = 1;
		boolean foundfreepos = false;
		while  (!foundfreepos) {
			Random r = new Random();
			x = Math.abs(r.nextInt()%18) +1;
			y = Math.abs(r.nextInt()%18) +1;
			if (Generel.board[y].charAt(x)==' ') // er det gulv ?
			{
				foundfreepos = true;
				for (Player p: players) {
					if (p.getXpos()==x && p.getYpos()==y) //pladsen optaget af en anden 
						foundfreepos = false;
				}
				
			}
		}
		PosXY pos = new PosXY(x,y);
		return pos;
	}
	public static void updatePlayerServer(Player me, int delta_x, int delta_y, String facingDir)
	{
		me.facingDir = facingDir;
		int x = me.getXpos(),y = me.getYpos();

		// when moving up against wall
		if (Generel.board[y+delta_y].charAt(x+delta_x)=='w') {
			me.addPoints(-1);
		}
		else {
			// collision detection
			Player player = getPlayerAt(new PosXY(x+delta_x, y+delta_y));
			if (player!=null) {
				me.addPoints(10);
				//update the other player
				player.addPoints(-10);
				PosXY randPos = getRandomFreePosition();
				player.setPos(randPos);
				PosXY oldPos = new PosXY(x+delta_x,y+delta_y);
			} else
				me.addPoints(1);
			PosXY oldPos = me.getPos();
			PosXY newPos = new PosXY(x+delta_x,y+delta_y);
			me.setPos(newPos);
		}
	}
	
	public static Player getPlayerAt(PosXY posXY) {
		for (Player player : players) {
			if (player.pos.equals(posXY)) {
				return player;
			}
		}
		return null;
	}
}
