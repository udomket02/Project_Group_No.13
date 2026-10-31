
package lab;

public class HallRequest {

	private final int floor;
	private final String direction;

	public HallRequest(int floor, String direction) {
		this.floor = floor;
		this.direction = direction;
	}

	public int getFloor() {
		return floor;
	}

	public String getDirection() {
		return direction;
	}

	@Override
	public String toString() {
		return "Floor " + floor + " (" + direction + ")";
	}
}
