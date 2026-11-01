package lab;

public class ElevatorController {

	private final Elevator elevator;
	private final HallRequestQueue hallRequestQueue;
	private final DestinationList destinationList;

	private final int minFloor;
	private final int maxFloor;

	private boolean waitingForDestinationInput;
	private boolean destinationChosenForCurrentBoarding;

	private String boardingDirection;

	public ElevatorController(Elevator elevator, HallRequestQueue hallRequestQueue, DestinationList destinationList,
			int minFloor, int maxFloor) {

		this.elevator = elevator;
		this.hallRequestQueue = hallRequestQueue;
		this.destinationList = destinationList;
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

		if (!hallRequestQueue.addHallRequest(request)) {
			System.out.println("This elevator request already exists.");
			return false;
		}

		// หากลิฟต์อยู่ที่ชั้นนั้นพอดีและไม่มีงานอื่นค้างอยู่ ให้เปิดรับผู้โดยสารทันที
		if (elevator.getCurrentFloor() == floor && !waitingForDestinationInput && !destinationList.hasDestinations()
				&& (elevator.getDirection().equals("IDLE") || hallRequestQueue.getRequestQueue().size() == 1)) {

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

		// ตรวจสอบชั้นปลายทางซ้ำ
		if (!destinationList.addDestination(floor)) {

			destinationChosenForCurrentBoarding = true;

			System.out.println("Destination floor " + floor + " is already scheduled.");

			return true;
		}

		destinationList.sortDestinations(boardingDirection);

		destinationChosenForCurrentBoarding = true;

		System.out.println("Destination floor " + floor + " added.");

		return true;
	}

	public void step() {

		if (!prepareForMovement()) {
			return;
		}

		if (elevator.getDirection().equals("IDLE")) {

			String directionHere = hallRequestQueue.getFirstHallDirectionAtFloor(elevator.getCurrentFloor());

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

		int arrivedFloor = elevator.getCurrentFloor();

		boolean stoppedForDestination = destinationList.removeDestination(arrivedFloor);

		if (stoppedForDestination) {
			elevator.openDoor();
			System.out.println("Arrived at destination Floor " + arrivedFloor + ". Door opened.");
		}

		// 1. รับผู้โดยสารที่ไปทิศทางเดียวกับที่ลิฟต์กำลังเคลื่อนที่ก่อน
		boolean pickedUp = serveHallRequestAtCurrentFloor(movingDirection);

		// 2. หากไม่มีคนไปทิศทางเดียวกัน และไม่มีงานข้างหน้าในทิศทางเดิมแล้ว
		// จึงรับคนทิศทางตรงข้ามที่ชั้นนี้ได้
		if (!pickedUp && !hasWorkAhead(arrivedFloor, movingDirection)) {

			String waitingDirection = hallRequestQueue.getFirstHallDirectionAtFloor(arrivedFloor);

			if (!waitingDirection.equals("IDLE")) {
				pickedUp = serveHallRequestAtCurrentFloor(waitingDirection);
			}
		}

		// หากส่งผู้โดยสารครบและไม่มีคำขอค้างอยู่ ให้ปรับสถานะกลับเป็น IDLE
		if (stoppedForDestination && !pickedUp && !destinationList.hasDestinations()
				&& !hallRequestQueue.hasHallRequests()) {
			elevator.setDirection("IDLE");
		}
	}

	public boolean prepareForMovement() {

		if (waitingForDestinationInput) {

			if (!destinationChosenForCurrentBoarding
					&& !hasDestinationInDirection(elevator.getCurrentFloor(), boardingDirection)) {
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

		if (currentDirection.equals("UP") && hasWorkAhead(currentFloor, "UP")) {
			return;
		}

		if (currentDirection.equals("DOWN") && hasWorkAhead(currentFloor, "DOWN")) {
			return;
		}

		if (destinationList.hasDestinations()) {

			int targetFloor = destinationList.getDestinationFloors().get(0);

			if (targetFloor > currentFloor) {
				elevator.setDirection("UP");

			} else if (targetFloor < currentFloor) {
				elevator.setDirection("DOWN");

			} else {
				elevator.setDirection("IDLE");
			}

			return;
		}

		if (hallRequestQueue.hasHallRequests()) {

			HallRequest request = hallRequestQueue.peekHallRequest();
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

	// Linear Search: ตรวจสอบว่ามีงานอยู่ข้างหน้าหรือไม่
	public boolean hasWorkAhead(int currentFloor, String direction) {

		for (int floor : destinationList.getDestinationFloors()) {

			if (direction.equals("UP") && floor > currentFloor) {
				return true;
			}

			if (direction.equals("DOWN") && floor < currentFloor) {
				return true;
			}
		}

		for (HallRequest request : hallRequestQueue.getRequestQueue()) {

			if (direction.equals("UP") && request.getFloor() > currentFloor) {
				return true;
			}

			if (direction.equals("DOWN") && request.getFloor() < currentFloor) {
				return true;
			}
		}

		return false;
	}

	private boolean serveHallRequestAtCurrentFloor(String direction) {

		int currentFloor = elevator.getCurrentFloor();

		if (!hallRequestQueue.removeHallRequestsAtFloor(currentFloor, direction)) {
			return false;
		}

		elevator.setDirection(direction);
		elevator.openDoor();

		waitingForDestinationInput = true;
		boardingDirection = direction;
		destinationChosenForCurrentBoarding = false;

		System.out.println("Passengers picked up at Floor " + currentFloor + " (" + direction + ").");

		if (hasDestinationInDirection(currentFloor, direction)) {
			System.out.println("An existing destination is available in this direction.");
			System.out.println("Please use Menu 2 to add a destination floor, or Move One Step to continue.");
		} else {
			System.out.println("Please use Menu 2 to select a destination floor.");
		}

		System.out.println("Door opened automatically.");

		return true;
	}

	// Linear Search: ตรวจสอบปลายทางในทิศทางที่กำลังเดินทาง
	private boolean hasDestinationInDirection(int currentFloor, String direction) {

		for (int floor : destinationList.getDestinationFloors()) {

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

		return hallRequestQueue.hasHallRequests() || destinationList.hasDestinations();
	}

	public boolean isWaitingForDestinationInput() {
		return waitingForDestinationInput;
	}

	public Elevator getElevator() {
		return elevator;
	}

	public HallRequestQueue getHallRequestQueue() {
		return hallRequestQueue;
	}

	public DestinationList getDestinationList() {
		return destinationList;
	}
}