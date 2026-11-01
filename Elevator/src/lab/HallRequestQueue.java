package lab;

import java.util.LinkedList;
import java.util.Queue;

public class HallRequestQueue {

	private final Queue<HallRequest> requestQueue;

	public HallRequestQueue() {
		requestQueue = new LinkedList<>();
	}

	public boolean addHallRequest(HallRequest request) {

		if (hasSameHallRequest(request.getFloor(), request.getDirection())) {
			return false;
		}

		requestQueue.offer(request);
		return true;
	}

	// Linear Search: ตรวจสอบคำขอที่ซ้ำกัน
	public boolean hasSameHallRequest(int floor, String direction) {

		for (HallRequest request : requestQueue) {

			if (request.getFloor() == floor && request.getDirection().equals(direction)) {
				return true;
			}
		}

		return false;
	}

	public boolean hasHallRequests() {
		return !requestQueue.isEmpty();
	}

	public HallRequest peekHallRequest() {
		return requestQueue.peek();
	}

	// ค้นหาทิศทางของคำขอที่ชั้นปัจจุบัน
	public String getFirstHallDirectionAtFloor(int floor) {

		for (HallRequest request : requestQueue) {

			if (request.getFloor() == floor) {
				return request.getDirection();
			}
		}

		return "IDLE";
	}

	// นำคำขอที่ชั้นและทิศทางตรงกันออกจาก Queue
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

	public Queue<HallRequest> getRequestQueue() {
		return requestQueue;
	}
}