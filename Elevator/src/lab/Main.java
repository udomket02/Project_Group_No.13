package lab;

import java.util.Scanner;

public class Main {

	private static final int MIN_FLOOR = 1;
	private static final int MAX_FLOOR = 6;

	private static final Scanner scanner = new Scanner(System.in);

	public static void main(String[] args) {

		Elevator elevator = new Elevator(1);
		HallRequestQueue queue = new HallRequestQueue();
		DestinationList destinationList = new DestinationList();

		ElevatorController controller = new ElevatorController(elevator, queue, destinationList, MIN_FLOOR, MAX_FLOOR);

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
				callElevator(controller);
				break;

			case 2:
				selectDestination(controller);
				break;

			case 3:
				movementStatusMenu(controller);
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

	private static void callElevator(ElevatorController controller) {

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
				System.out.println("Cannot go UP from the top floor.");
				continue;
			}

			if (floor == MIN_FLOOR && direction.equals("D")) {
				System.out.println("Cannot go DOWN from the ground floor.");
				continue;
			}

			String fullDirection = direction.equals("U") ? "UP" : "DOWN";

			HallRequest request = new HallRequest(floor, fullDirection);

			if (!controller.addHallRequest(request)) {
				System.out.println("Request could not be added. Please try again.");
				continue;
			}

			System.out.println("Elevator has been assigned to Floor " + floor + ".");
			break;
		}
	}

	private static void selectDestination(ElevatorController controller) {

		if (!controller.isWaitingForDestinationInput()) {
			System.out.println("No passenger is waiting to select a destination.");
			return;
		}

		while (true) {
			int floor = readInt("Enter destination floor (1-6): ");

			if (floor < MIN_FLOOR || floor > MAX_FLOOR) {
				System.out.println("Invalid floor. Please enter 1-6.");
				continue;
			}

			if (controller.addDestination(floor)) {
				break;
			}

			System.out.println("Please enter a valid destination floor.");
		}
	}

	private static void movementStatusMenu(ElevatorController controller) {

		int choice;

		do {
			System.out.println("=====================================");
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
				controller.step();
				printSimulation(controller);
				break;

			case 2:
				autoSimulation(controller);
				break;

			case 3:
				printSimulation(controller);
				break;

			case 0:
				break;

			default:
				System.out.println("Invalid menu. Please try again.");
			}

		} while (choice != 0);
	}

	private static void autoSimulation(ElevatorController controller) {

		if (!controller.canAutoRun()) {
			System.out.println("No elevator movement available.");
			return;
		}

		while (controller.canAutoRun()) {
			controller.step();
			printSimulation(controller);
		}

		System.out.println("Auto Simulation finished.");
	}

	private static void printSimulation(ElevatorController controller) {

		Elevator elevator = controller.getElevator();

		System.out.println("\n========== ELEVATOR STATUS ==========");

		for (int floor = MAX_FLOOR; floor >= MIN_FLOOR; floor--) {
			String display = getElevatorDisplay(elevator, floor);
			System.out.println("Floor " + floor + " | " + display);
		}

		System.out.println("-------------------------------------");
		System.out.println("Current Floor: " + elevator.getCurrentFloor());
		System.out.println("Direction: " + elevator.getDirection());
		System.out.println("Door: " + (elevator.isDoorOpen() ? "OPEN" : "CLOSED"));

		System.out.println("\nDestinations: " + controller.getDestinationList().getDestinationFloors());

		System.out.println("Hall Requests: " + controller.getHallRequestQueue().getRequestQueue());

		System.out.println("=====================================");
	}

	private static String getElevatorDisplay(Elevator elevator, int floor) {

		if (elevator.getCurrentFloor() != floor) {
			return "       ";
		}

		if (elevator.isDoorOpen()) {
			return "[ OPEN ]";
		}

		if (elevator.getDirection().equals("UP")) {
			return "[  ▲  ]";
		}

		if (elevator.getDirection().equals("DOWN")) {
			return "[  ▼  ]";
		}

		return "[ IDLE ]";
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