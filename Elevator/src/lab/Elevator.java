package lab;

public class Elevator {

	private int currentFloor;
	private String direction;
	private boolean doorOpen;

	public Elevator(int initialFloor) {
		currentFloor = initialFloor;
		direction = "IDLE";
		doorOpen = false;
	}

	public int getCurrentFloor() {
		return currentFloor;
	}

	public void setCurrentFloor(int currentFloor) {
		this.currentFloor = currentFloor;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public boolean isDoorOpen() {
		return doorOpen;
	}

	public void openDoor() {
		doorOpen = true;
	}

	public void closeDoor() {
		doorOpen = false;
	}
}