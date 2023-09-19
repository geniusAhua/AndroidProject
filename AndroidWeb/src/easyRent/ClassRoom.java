package easyRent;

import com.sun.prism.Image;

public class ClassRoom {
	private String imagPath;
	private String classRoom;
	private String classRoomSeat;
	
	public ClassRoom(String imagPath, String classRoom, String classRoomSeat) {
		this.imagPath = imagPath;
		this.classRoom = classRoom;
		this.classRoomSeat = classRoomSeat;
	}
	
	public void setImagPath(String imagPath) {
		this.imagPath = imagPath;
	}
	
	public void setClassRoom(String classRoom) {
		this.classRoom = classRoom;
	}
	
	public void setClassRoomSeat(String classRoomSeat) {
		this.classRoomSeat = classRoomSeat;
	}
	
	public String getImagPath() {
		return imagPath;
	}
	
	public String getClassRoom() {
		return classRoom;
	}
	
	public String getClassRoomSeat() {
		return classRoomSeat;
	}
}
