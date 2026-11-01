package lab;

import java.util.Scanner;

public class Main {

	private static final int MIN_FLOOR = 1;
	private static final int MAX_FLOOR = 6;

	private static final Scanner scanner = new Scanner(System.in);

	public static void main(String[] args) {

		Elevator elevator1 = new Elevator(1);
		Elevator elevator2 = new Elevator(1);

		HallRequestQueue queue1 = new HallRequestQueue();
		HallRequestQueue queue2 = new HallRequestQueue();

		DestinationList destinationList1 = new DestinationList();
		DestinationList destinationList2 = new DestinationList();

		ElevatorController controller1 = new ElevatorController(elevator1, queue1, destinationList1, MIN_FLOOR,
				MAX_FLOOR);

		ElevatorController controller2 = new ElevatorController(elevator2, queue2, destinationList2, MIN_FLOOR,
				MAX_FLOOR);

		ElevatorSystem system = new ElevatorSystem(controller1, controller2);

		int choice;

		do {

			System.out.println("\n\n\n============================================");
			System.out.println("          ELEVATOR SYSTEM (Demo)");
			System.out.println("============================================");
			System.out.println("1. Call Elevator");
			System.out.println("2. Select Destination");
			System.out.println("3. Elevator Movement & Status");
			System.out.println("0. Exit");
			System.out.println("============================================");

			choice = readInt("Select menu: ");

			switch (choice) {

			case 1:
				callElevator(system);
				break;

			case 2:
				selectDestination(system);
				break;

			case 3:
				movementStatusMenu(system);
				break;

			case 0:
				System.out.println("Exit Elevator System.");
				break;

			default:
				System.out.println("Invalid menu. Please try again.");
			}

		} while (choice != 0);

		scanner.close();
	}

	private static void callElevator(ElevatorSystem system) {

		while (true) {

			int floor = readInt("Enter your floor (1-6): ");

			if (floor < MIN_FLOOR || floor > MAX_FLOOR) {
				System.out.println("Invalid floor. Please enter 1-6.");
				continue;
			}

			String direction;

			while (true) {

				System.out.print("Direction (U/D): ");
				direction = scanner.nextLine().trim().toUpperCase();

				if (direction.equals("U") || direction.equals("D")) {
					break;
				}

				System.out.println("Invalid direction. Please enter U or D.");
			}

			if (floor == MAX_FLOOR && direction.equals("U")) {
				System.out.println("Cannot go UP from the top floor. Please enter again.");
				continue;
			}

			if (floor == MIN_FLOOR && direction.equals("D")) {
				System.out.println("Cannot go DOWN from the ground floor. Please enter again.");
				continue;
			}

			String fullDirection = direction.equals("U") ? "UP" : "DOWN";

			HallRequest request = new HallRequest(floor, fullDirection);

			int assignedElevator = system.callElevator(request);

			if (assignedElevator == 0) {
				System.out.println("This request already exists. Please enter another request.");
				continue;
			}

			System.out.println("Elevator " + assignedElevator + " has been assigned.");

			System.out.println("Elevator " + assignedElevator + " is assigned to respond to Floor " + floor + ".");

			break;
		}
	}

	private static void selectDestination(ElevatorSystem system) {

		int elevatorNumber;

		while (true) {

			elevatorNumber = readInt("Select Elevator (1 or 2): ");

			if (elevatorNumber == 1 || elevatorNumber == 2) {
				break;
			}

			System.out.println("Invalid elevator number. Please enter 1 or 2.");
		}

		if (!system.isWaitingForDestinationInput(elevatorNumber)) {

			System.out.println("No passenger is waiting to select a destination for Elevator " + elevatorNumber + ".");

			return;
		}

		while (true) {

			int floor = readInt("Enter destination floor (1-6): ");

			if (floor < MIN_FLOOR || floor > MAX_FLOOR) {
				System.out.println("Invalid floor. Please enter 1-6.");
				continue;
			}

			if (system.selectDestination(elevatorNumber, floor)) {
				break;
			}

			System.out.println("Please enter a valid destination floor.");
		}
	}

	private static void movementStatusMenu(ElevatorSystem system) {

		int choice;

		do {

			System.out.println("\n=====================================");
			System.out.println("     ELEVATOR MOVEMENT & STATUS");
			System.out.println("=====================================");
			System.out.println("1. Move One Step");
			System.out.println("2. Auto Move");
			System.out.println("3. Show Elevator Status");
			System.out.println("0. Back to Main Menu");
			System.out.println("=====================================");

			choice = readInt("Select menu: ");

			switch (choice) {

			case 1:
				moveOneStep(system);
				break;

			case 2:
				autoSimulation(system);
				break;

			case 3:
				printSimulation(system.getElevator1(), system.getElevator2());
				break;

			case 0:
				break;

			default:
				System.out.println("Invalid menu. Please try again.");
			}

		} while (choice != 0);
	}

	private static void moveOneStep(ElevatorSystem system) {

		system.moveElevators();

		printSimulation(system.getElevator1(), system.getElevator2());
	}

	private static void autoSimulation(ElevatorSystem system) {

		if (!system.hasWork()) {
			System.out.println("No elevator movement available.");
			return;
		}

		while (system.hasWork()) {

			system.moveElevators();

			printSimulation(system.getElevator1(), system.getElevator2());
		}

		System.out.println("Auto Simulation finished.");
	}

	private static void printSimulation(ElevatorController controller1, ElevatorController controller2) {

		Elevator e1 = controller1.getElevator();
		Elevator e2 = controller2.getElevator();

		System.out.println("\n========== ELEVATOR STATUS ==========");

		for (int floor = MAX_FLOOR; floor >= MIN_FLOOR; floor--) {

			String display1 = getElevatorDisplay(e1, floor, "E1");
			String display2 = getElevatorDisplay(e2, floor, "E2");

			System.out.println("Floor " + floor + " | " + display1 + " | " + display2);
		}

		System.out.println("-------------------------------------");

		System.out.println("E1: Floor " + e1.getCurrentFloor() + " | Direction: " + e1.getDirection() + " | Door: "
				+ (e1.isDoorOpen() ? "OPEN" : "CLOSED"));

		System.out.println("E2: Floor " + e2.getCurrentFloor() + " | Direction: " + e2.getDirection() + " | Door: "
				+ (e2.isDoorOpen() ? "OPEN" : "CLOSED"));

		System.out.println("\nE1 Destinations: " + controller1.getDestinationList().getDestinationFloors());

		System.out.println("E1 Hall Requests: " + controller1.getHallRequestQueue().getRequestQueue());

		System.out.println("\nE2 Destinations: " + controller2.getDestinationList().getDestinationFloors());

		System.out.println("E2 Hall Requests: " + controller2.getHallRequestQueue().getRequestQueue());

		System.out.println("=====================================");
	}

	private static String getElevatorDisplay(Elevator elevator, int floor, String name) {

		if (elevator.getCurrentFloor() != floor) {
			return "       ";
		}

		if (elevator.isDoorOpen()) {
			return "[" + name + " OPEN]";
		}

		if (elevator.getDirection().equals("UP")) {
			return "[" + name + " ▲]";
		}

		if (elevator.getDirection().equals("DOWN")) {
			return "[" + name + " ▼]";
		}

		return "[" + name + " ■]";
	}

	private static int readInt(String message) {

		while (true) {

			System.out.print(message);
			String input = scanner.nextLine();

			try {
				return Integer.parseInt(input.trim());

			} catch (NumberFormatException e) {

				System.out.println("Invalid input. Please enter a number.");
			}
		}
	}
}