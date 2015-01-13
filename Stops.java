
public class Stops {
	private double beatNumber, stopTime;
	
	public Stops(double beatNumber, double stopTime){
		this.stopTime = stopTime;
		this.beatNumber = beatNumber;
	}
	
	public double getBeat(){
		return beatNumber;
	}
	
	public double getStopTime(){
		return stopTime;
	}
}
