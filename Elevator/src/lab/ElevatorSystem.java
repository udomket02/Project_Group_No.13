
package lab;

public class ElevatorSystem {

	private final ElevatorController elevator1;
	private final ElevatorController elevator2;

	public ElevatorSystem(ElevatorController elevator1, ElevatorController elevator2) {
		this.elevator1 = elevator1;
		this.elevator2 = elevator2;
	}

	public int callElevator(HallRequest request) {

		int floor = request.getFloor();

		int distance1 = Math.abs(elevator1.getElevator().getCurrentFloor() - floor);
		int distance2 = Math.abs(elevator2.getElevator().getCurrentFloor() - floor);

		if (distance1 <= distance2) {
			if (elevator1.addHallRequest(request)) {
				return 1;
			}
		} else {
			if (elevator2.addHallRequest(request)) {
				return 2;
			}
		}

		return 0;
	}

	public boolean selectDestination(int elevatorNumber, int floor) {

		if (elevatorNumber == 1) {
			return elevator1.addDestination(floor);
		} else if (elevatorNumber == 2) {
			return elevator2.addDestination(floor);
		}

		System.out.println("Invalid elevator number.");
		return false;
	}

	public boolean isWaitingForDestinationInput(int elevatorNumber) {

		if (elevatorNumber == 1) {
			return elevator1.isWaitingForDestinationInput();
		} else if (elevatorNumber == 2) {
			return elevator2.isWaitingForDestinationInput();
		}

		return false;
	}

	public void moveElevators() {

		if (elevator1.canAutoRun()) {
			elevator1.step();
		}

		if (elevator2.canAutoRun()) {
			elevator2.step();
		}
	}

	public boolean hasWork() {
		return elevator1.canAutoRun() || elevator2.canAutoRun();
	}

	public ElevatorController getElevator1() {
		return elevator1;
	}

	public ElevatorController getElevator2() {
		return elevator2;
	}
}