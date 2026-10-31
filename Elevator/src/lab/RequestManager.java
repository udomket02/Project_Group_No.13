
package lab;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class RequestManager {

	private final Queue<HallRequest> requestQueue;
	private final ArrayList<Integer> destinationFloors;

	public RequestManager() {
		requestQueue = new LinkedList<>();
		destinationFloors = new ArrayList<>();
	}

	public boolean addHallRequest(HallRequest request) {

		if (hasSameHallRequest(request.getFloor(), request.getDirection())) {
			return false;
		}

		requestQueue.offer(request);
		return true;
	}

	public boolean hasSameHallRequest(int floor, String direction) {

		for (HallRequest request : requestQueue) {

			if (request.getFloor() == floor && request.getDirection().equals(direction)) {
				return true;
			}
		}

		return false;
	}

	public boolean addDestination(int floor) {

		for (int existingFloor : destinationFloors) {

			if (existingFloor == floor) {
				return true;
			}
		}

		destinationFloors.add(floor);
		return true;
	}

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

	public boolean hasHallRequests() {
		return !requestQueue.isEmpty();
	}

	public HallRequest peekHallRequest() {
		return requestQueue.peek();
	}

	public String getFirstHallDirectionAtFloor(int floor) {

		for (HallRequest request : requestQueue) {

			if (request.getFloor() == floor) {
				return request.getDirection();
			}
		}

		return "IDLE";
	}

	public boolean removeHallRequestsAtFloor(int floor, String direction) {

		int queueSize = requestQueue.size();
		boolean found = false;

		for (int i = 0; i < queueSize; i++) {

			HallRequest request = requestQueue.poll();

			if (request.getFloor() == floor && request.getDirection().equals(direction)) {

				found = true;

			} else {

				requestQueue.offer(request);
			}
		}

		return found;
	}

	public boolean hasWorkAhead(int currentFloor, String direction) {

		for (int floor : destinationFloors) {

			if (direction.equals("UP") && floor > currentFloor) {
				return true;
			}

			if (direction.equals("DOWN") && floor < currentFloor) {
				return true;
			}
		}

		for (HallRequest request : requestQueue) {

			if (!request.getDirection().equals(direction)) {
				continue;
			}

			if (direction.equals("UP") && request.getFloor() > currentFloor) {
				return true;
			}

			if (direction.equals("DOWN") && request.getFloor() < currentFloor) {
				return true;
			}
		}

		return false;
	}

	public Queue<HallRequest> getRequestQueue() {
		return requestQueue;
	}

	public ArrayList<Integer> getDestinationFloors() {
		return destinationFloors;
	}
}
