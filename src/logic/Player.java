package logic;

import utility.*;

public class Player {
	String name;
	PosXY pos;
	int point;
	String facingDir;

	public Player(String name, PosXY posXY, String facingDir) {
		this.name = name;
		this.pos = posXY;
		this.facingDir = facingDir;
		this.point = 0;
	};
	
	public PosXY getPos() {
		return this.pos;
	}
	public void setPos(PosXY p) {
		this.pos =p;
	}
	public int getXpos() {
		return pos.getX();
	}
	public void setXpos(int xpos) {
		this.pos.setX(xpos);
	}
	public int getYpos() {
		return pos.getY();
	}
	public void setYpos(int ypos) {
		this.pos.setY(ypos);
	}
	public String getFacingDir() {
		return facingDir;
	}
	public void setFacingDir(String facingDir) {
		this.facingDir = facingDir;
	}
	public void addPoints(int p) {
		point+=p;
	}
	public String toString() {
		return name+":   "+point;
	}
}
