package lab;

import java.util.ArrayList;

public class DestinationList {

	private final ArrayList<Integer> destinationFloors;

	public DestinationList() {
		destinationFloors = new ArrayList<>();
	}

	// Linear Search: ตรวจสอบว่าชั้นปลายทางมีอยู่แล้วหรือไม่
	public boolean hasDestination(int floor) {

		for (int existingFloor : destinationFloors) {

			if (existingFloor == floor) {
				return true;
			}
		}

		return false;
	}

	// เพิ่มชั้นปลายทางโดยไม่ให้ซ้ำ
	public boolean addDestination(int floor) {

		if (hasDestination(floor)) {
			return false;
		}

		destinationFloors.add(floor);
		return true;
	}

	// Insertion Sort: เรียงชั้นตามทิศทางของลิฟต์
	public void sortDestinations(String direction) {

		for (int i = 1; i < destinationFloors.size(); i++) {

			int key = destinationFloors.get(i);
			int j = i - 1;

			if (direction.equals("UP")) {

				while (j >= 0 && destinationFloors.get(j) > key) {
					destinationFloors.set(j + 1, destinationFloors.get(j));
					j--;
				}

			} else if (direction.equals("DOWN")) {

				while (j >= 0 && destinationFloors.get(j) < key) {
					destinationFloors.set(j + 1, destinationFloors.get(j));
					j--;
				}
			}

			destinationFloors.set(j + 1, key);
		}
	}

	public boolean removeDestination(int floor) {
		return destinationFloors.remove(Integer.valueOf(floor));
	}

	public boolean hasDestinations() {
		return !destinationFloors.isEmpty();
	}

	public ArrayList<Integer> getDestinationFloors() {
		return destinationFloors;
	}
}