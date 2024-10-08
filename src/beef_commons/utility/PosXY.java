package beef_commons.utility;

public class PosXY {
	int x;
	int y;
	public PosXY(int x, int y) {
		super();
		this.x = x;
		this.y = y;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}

	@Override
	public boolean equals(Object obj) {
		PosXY temp = (PosXY) obj;
		if (this.x == temp.x && this.y == temp.y){
			return true;
		} else return false;
	}

	@Override
	public String toString() {
		return x + "," + y;
	}
}
