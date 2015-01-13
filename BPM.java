
public class BPM {
	private double beatNumber, speed;
	
	public BPM(double beatNumber, double speed) {
		this.beatNumber = beatNumber;
		this.speed = speed;
	}
	
	public double getBeat(){
		return beatNumber;
	}
	
	public double getSpeed(){
		return speed;
	}
}
