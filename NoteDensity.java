import java.awt.*;
import java.awt.geom.Line2D;
import java.io.FileNotFoundException;
import java.util.*;

import javax.swing.JFrame;
import javax.swing.JPanel;


public class NoteDensity{
	
	private ArrayList<Integer> data;
	private String difficulty;
	private String title;
	private int numKeys;
	
	public NoteDensity (ArrayList<Integer> density, String difficulty, String title, int numKeys){
		data = density;
		this.difficulty = difficulty;
		this.title = title;
		this.numKeys = numKeys;
	}
	
	public String getDifficulty(){
		return this.difficulty;
	}
	
	public ArrayList<Integer> getNoteDensity(){
		return data;
	}
	
	public String getTitle(){
		return title;
	}
	
	public int getNumKeys(){
		return this.numKeys;
	}
}
