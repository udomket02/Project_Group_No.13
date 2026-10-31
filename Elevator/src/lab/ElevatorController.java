
package lab;

public class ElevatorController {

	private final Elevator elevator;
	private final RequestManager requestManager;

	private final int minFloor;
	private final int maxFloor;

	private boolean waitingForDestinationInput;
	private boolean destinationChosenForCurrentBoarding;

	private String boardingDirection;

	public ElevatorController(Elevator elevator, RequestManager requestManager, int minFloor, int maxFloor) {

		this.elevator = elevator;
		this.requestManager = requestManager;
		this.minFloor = minFloor;
		this.maxFloor = maxFloor;

		waitingForDestinationInput = false;
		destinationChosenForCurrentBoarding = false;
		boardingDirection = "IDLE";
	}

	public boolean addHallRequest(HallRequest request) {

		int floor = request.getFloor();
		String direction = request.getDirection();

		if (floor < minFloor || floor > maxFloor) {
			System.out.println("Invalid floor.");
			return false;
		}

		if (!direction.equals("UP") && !direction.equals("DOWN")) {
			System.out.println("Invalid direction.");
			return false;
		}

		if (floor == maxFloor && direction.equals("UP")) {
			System.out.println("Cannot go UP from the top floor.");
			return false;
		}

		if (floor == minFloor && direction.equals("DOWN")) {
			System.out.println("Cannot go DOWN from the ground floor.");
			return false;
		}

		if (!requestManager.addHallRequest(request)) {
			System.out.println("This elevator request already exists.");
			return false;
		}

		if (elevator.getCurrentFloor() == floor && elevator.getDirection().equals("IDLE") && !elevator.isDoorOpen()
				&& !requestManager.hasDestinations()) {

			serveHallRequestAtCurrentFloor(direction);
		}

		return true;
	}

	public boolean addDestination(int floor) {

		if (!waitingForDestinationInput || !elevator.isDoorOpen()) {
			System.out.println("No passenger is currently boarding this elevator.");
			return false;
		}

		if (floor < minFloor || floor > maxFloor) {
			System.out.println("Invalid floor.");
			return false;
		}

		if (floor == elevator.getCurrentFloor()) {
			System.out.println("Elevator is already on this floor.");
			return false;
		}

		if (boardingDirection.equals("UP") && floor < elevator.getCurrentFloor()) {
			System.out.println("UP direction requires a higher floor.");
			return false;
		}

		if (boardingDirection.equals("DOWN") && floor > elevator.getCurrentFloor()) {
			System.out.println("DOWN direction requires a lower floor.");
			return false;
		}

		if (!requestManager.addDestination(floor)) {
			destinationChosenForCurrentBoarding = true;
			System.out.println("Destination floor " + floor + " is already scheduled.");
			return true;
		}

		requestManager.sortDestinations(boardingDirection);

		destinationChosenForCurrentBoarding = true;

		System.out.println("Destination floor " + floor + " added.");

		return true;
	}

	public void step() {

		if (!prepareForMovement()) {
			return;
		}

		if (elevator.getDirection().equals("IDLE")) {

			String directionHere = requestManager.getFirstHallDirectionAtFloor(elevator.getCurrentFloor());

			if (!directionHere.equals("IDLE")) {
				serveHallRequestAtCurrentFloor(directionHere);
				return;
			}
		}

		chooseDirection();

		if (elevator.getDirection().equals("IDLE")) {
			System.out.println("No pending requests.");
			return;
		}

		String movingDirection = elevator.getDirection();
		int currentFloor = elevator.getCurrentFloor();

		if (movingDirection.equals("UP")) {
			elevator.setCurrentFloor(currentFloor + 1);
		} else {
			elevator.setCurrentFloor(currentFloor - 1);
		}

		boolean stoppedForDestination = requestManager.removeDestination(elevator.getCurrentFloor());

		boolean pickedUp = false;

		if (!requestManager.hasDestinations()) {

			String waitingDirection = requestManager.getFirstHallDirectionAtFloor(elevator.getCurrentFloor());

			if (!waitingDirection.equals("IDLE")) {
				pickedUp = serveHallRequestAtCurrentFloor(waitingDirection);
			}

		} else {

			pickedUp = serveHallRequestAtCurrentFloor(movingDirection);
		}

		if (stoppedForDestination && !pickedUp) {
			elevator.openDoor();
		}
	}

	public boolean prepareForMovement() {

		if (waitingForDestinationInput) {

			if (!destinationChosenForCurrentBoarding) {
				System.out.println("Please select at least one destination floor.");
				return false;
			}

			waitingForDestinationInput = false;
			destinationChosenForCurrentBoarding = false;
			boardingDirection = "IDLE";

			if (elevator.isDoorOpen()) {
				elevator.closeDoor();
			}

			return true;
		}

		if (elevator.isDoorOpen()) {
			elevator.closeDoor();
		}

		return true;
	}

	private void chooseDirection() {

		int currentFloor = elevator.getCurrentFloor();
		String currentDirection = elevator.getDirection();

		if (currentDirection.equals("UP") && requestManager.hasWorkAhead(currentFloor, "UP")) {
			return;
		}

		if (currentDirection.equals("DOWN") && requestManager.hasWorkAhead(currentFloor, "DOWN")) {
			return;
		}

		if (requestManager.hasDestinations()) {

			int targetFloor = requestManager.getDestinationFloors().get(0);

			if (targetFloor > currentFloor) {
				elevator.setDirection("UP");

			} else if (targetFloor < currentFloor) {
				elevator.setDirection("DOWN");

			} else {
				elevator.setDirection("IDLE");
			}

			return;
		}

		if (requestManager.hasHallRequests()) {

			HallRequest request = requestManager.peekHallRequest();
			int targetFloor = request.getFloor();

			if (targetFloor > currentFloor) {
				elevator.setDirection("UP");

			} else if (targetFloor < currentFloor) {
				elevator.setDirection("DOWN");

			} else {
				elevator.setDirection(request.getDirection());
			}

			return;
		}

		elevator.setDirection("IDLE");
	}

	private boolean serveHallRequestAtCurrentFloor(String direction) {

		int currentFloor = elevator.getCurrentFloor();

		if (!requestManager.removeHallRequestsAtFloor(currentFloor, direction)) {
			return false;
		}

		elevator.setDirection(direction);
		elevator.openDoor();

		waitingForDestinationInput = true;
		boardingDirection = direction;

		if (hasDestinationInDirection(currentFloor, direction)) {

			destinationChosenForCurrentBoarding = true;

			System.out.println("Passengers picked up at Floor " + currentFloor + " (" + direction + ").");
			System.out.println("An existing destination is available in this direction.");
			System.out.println("The elevator can continue without selecting a destination again.");

		} else {

			destinationChosenForCurrentBoarding = false;

			System.out.println("Passengers picked up at Floor " + currentFloor + " (" + direction + ").");
			System.out.println("Please use Menu 2 to select a destination floor.");
		}

		System.out.println("Door opened automatically.");

		return true;
	}

	private boolean hasDestinationInDirection(int currentFloor, String direction) {

		for (int floor : requestManager.getDestinationFloors()) {

			if (direction.equals("UP") && floor > currentFloor) {
				return true;
			}

			if (direction.equals("DOWN") && floor < currentFloor) {
				return true;
			}
		}

		return false;
	}

	public boolean canAutoRun() {

		if (waitingForDestinationInput && !destinationChosenForCurrentBoarding) {
			return false;
		}

		return requestManager.hasHallRequests() || requestManager.hasDestinations() || elevator.isDoorOpen();
	}

	public boolean isWaitingForDestinationInput() {
		return waitingForDestinationInput;
	}

	public Elevator getElevator() {
		return elevator;
	}

	public RequestManager getRequestManager() {
		return requestManager;
	}
}
