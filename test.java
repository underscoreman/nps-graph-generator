import java.awt.*;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.UnmappableCharacterException;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class test extends JPanel{
	
	public static final double resolution = 0.5; //just raiight 1 second too little 0.1 too much
	
	
	/**
	 * Gets a .sm file and returns its note density. Pretty simple, right?
	 * 
	 * @param filename the .sm file (no worrying about exceptions lol)
	 * @return the note density of said file
	 * @throws IOException 
	 */
	public static ArrayList<NoteDensity> getNoteDensities(File f) throws NoSuchElementException, IOException{
		String bpm;
		String stop;
		String difficulty = "";
		String title = "";
		Song input = new Song();
		
		//bpmchanges, stops, and the current measure
		ArrayList<BPM> bpmChanges = new ArrayList<BPM>();
		ArrayList<Stops> stops = new ArrayList<Stops>();
		ArrayList<String> measure = new ArrayList<String>();
		
		//current beat, second, bpm, and beatIncrement (depends on number of lines in a measure)
		double currentBeat = 0;
		double currentSecond = 0;
		double currentBPM = 0;
		double beatIncrement;
		
		//the occurrence of the notes (seconds) and the corresponding note density
		ArrayList<Double> notes = new ArrayList<Double>();		
		ArrayList<NoteDensity> retval = new ArrayList<NoteDensity>();
		
		//create a file with .sm
		//File f = new File(filename);
		
		/*
		File[] matchingFiles = f.listFiles(new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return name.endsWith(".sm");
		    }
		});
		*/
		
		
		//Scanner read = new Scanner(f); //matchingFiles[0]
		BufferedReader read = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));

		
		while (true){
			String temp = "";
			try{
				temp = read.readLine(); 
			}
			catch (NoSuchElementException asdf){
				read.readLine();
			}
			
				title = temp.substring(temp.indexOf(':')+1, temp.length() - 1).trim();
				if (title.length() == 0)
					title = f.getParentFile().getName();
				break;
			
		}
		
		//read in bpms
		bpm = "";
		boolean bool = true;
		while (bool == true){
            String s = read.readLine();
			if (s.substring(0,6).equals("#BPMS:")) {
				bpm += s;
				while (bpm.charAt(bpm.length()-1) != ';'){
					s = read.readLine();
					bpm += s;
				}
				bool = false;
			}
		}
		bpm = bpm.substring(6, bpm.length()-1);
	    Scanner readBPM = new Scanner(bpm).useDelimiter("=|,");
	    while (readBPM.hasNext()){
	    	double beatNumber = Double.parseDouble(readBPM.next());
	    	double speed = Double.parseDouble(readBPM.next());
	    	if (speed == 0) break;
	    	/*
	    	if (speed < 0){
	    		System.out.println("Negative BPM detected");
	    		System.exit(0);
	    	}
	    	*/
	    	if (bpmChanges.isEmpty() ? false : beatNumber == bpmChanges.get(bpmChanges.size()-1).getBeat())
	    		bpmChanges.remove(bpmChanges.size()-1);
	    	bpmChanges.add(new BPM(beatNumber, speed));
	    }
	    
		//read in stops
	    stop = "";
	    bool = true;
		while (bool == true){
			String t = read.readLine();
			if (t.substring(0,7).equals("#STOPS:")) {
				stop += t;
				while (stop.charAt(stop.length()-1) != ';'){
					t = read.readLine();
					stop += t;
				}
				bool = false;
			}
		}
		stop = stop.substring(7, stop.length()-1);
	    Scanner readStops = new Scanner(stop).useDelimiter("=|,");
	    while (readStops.hasNext()){
	    	double beatNumber = Double.parseDouble(readStops.next());
	    	double stopTime = Double.parseDouble(readStops.next());
	    	if (stops.isEmpty() ? false : beatNumber == stops.get(stops.size()-1).getBeat())
	    		stops.remove(stops.size()-1);
	    	stops.add(new Stops(beatNumber, stopTime));
	    }
	    
	    
	    //ArrayList<Stops> copyStops = stops;
	    //ArrayList<BPM> copyBPM = bpmChanges;
	    //read rest of file
	    while (read.readLine() != null){
	    	int numKeys = 0;
	    	//stops = copyStops;
	    	//bpmChanges = copyBPM;
		    while (true){
		    	String temp = "";
		    	try{
		    		temp = read.readLine();
		    		if (temp == null)
		    			throw new NoSuchElementException();
		    	}
		    	catch (NoSuchElementException nsee){
		    		return retval;
		    	}
		    	if (temp.startsWith("#NOTES:")){
		    		read.readLine();
		    		read.readLine();
		    		difficulty = read.readLine().replace(":","").replace(" ","");
		    		difficulty = difficulty.substring(0,1).toUpperCase()+difficulty.substring(1);
		    	}
		    	try{
		    		numKeys = temp.length();
		    		Integer.parseInt(temp); // checks for start of note data
		    	}
		    	catch (NumberFormatException nfe){
		    		continue;
		    	}
		    	measure.add(temp);
		    	break;
		    }
		    double max = 0;
		    while (true){
		    	String temp = read.readLine();
		    	if (temp.isEmpty()) 
		    		continue;
                if (temp.charAt(0) == ',' || temp.charAt(0) == ';'){
		    		
		    		//In the current measure
		    		Iterator<String> itr = measure.iterator();
		    		
		    		//beat increment per beat (because it depends)
		    		beatIncrement = (double) 4 / (double) measure.size();
		    		
		    		//per element increment
		    		while (itr.hasNext()){
		    			String curr = itr.next();
		    			
		    			//add notes to ArrayList 'notes'
		    			for (int i=0; i<curr.length(); i++)
		    				if (max <= currentSecond && (curr.charAt(i) == '1' || curr.charAt(i) == '2' || curr.charAt(i) == '4')){
		    					notes.add((double)Math.round(currentSecond * 1000) / 1000);
		    					max = currentSecond;
		    				}
		    			
		    			//ADD UP TOTAL SECONDS BETWEEN TWO LINES
		    			//add stops
		    			for (Stops s : stops){
		    				if (s.getBeat()>=currentBeat && s.getBeat()<currentBeat+beatIncrement){
		    					currentSecond+=s.getStopTime();
		    					//stops.remove(0);
		    					//i--;
		    				}
		    				else if (s.getBeat() >= currentBeat+beatIncrement)
		    					break;
		    			}
		    			//get bpm's between two lines
		    			ArrayList<BPM> tempBPMList = new ArrayList<BPM>();
		    			int indicator = 0;
		    			for (BPM b : bpmChanges){
		    				if (b.getBeat() == currentBeat){
		    					currentBPM = b.getSpeed();
		    					//bpmChanges.remove(0);
		    				}
		    				if (b.getBeat() > currentBeat && b.getBeat() < currentBeat+beatIncrement){
		    					tempBPMList.add(b);
		    					indicator++;
		    					if (tempBPMList.size() > 1)
		    						System.out.println("gay");
		    					//bpmChanges.remove(0);
		    				}
		    				else if (b.getBeat() >= currentBeat+beatIncrement)
		    					break;
		    				//i--;
		    			}
		    			if (indicator == 0){
		    				currentSecond += 60*beatIncrement/currentBPM;
		    			}
		    			else{
		    				currentSecond += 60*(tempBPMList.get(0).getBeat() - currentBeat)/currentBPM;
		    				for (int i=1; i<tempBPMList.size(); i++)
		    					currentSecond += 60*(tempBPMList.get(i).getBeat() - tempBPMList.get(i-1).getBeat())/tempBPMList.get(i-1).getSpeed();
		    				currentBPM = tempBPMList.get(tempBPMList.size()-1).getSpeed(); //these two were switched, causing a bug
		    				currentSecond += 60*(currentBeat + beatIncrement - tempBPMList.get(tempBPMList.size()-1).getBeat())/currentBPM;
		    			}
		    			currentBeat += beatIncrement;
		    			tempBPMList.clear();
		    		}
		    		
		    		measure.clear();
		    		if (temp.charAt(0) == ';' || (temp.length() == 2 && temp.charAt(1) == ';'))
		    			break;
		    	}
		    	else{
		    		measure.add(temp);
		    	}
		    }//end of while loop
		    
		    //System.out.println(notes);
		    
		    if (!notes.isEmpty()){
			    //NOW WE NEED TO GET THE NOTE DENSITY
			    //note that this is measured every HALF (not 1/120 wtf) second cause i say so
			    ArrayList<Integer> noteDensity = new ArrayList<Integer>();
			    double end = notes.get(notes.size()-1);
			    for (double i=notes.get(0); i<end; i+=resolution){
			    	int temp = 0;
			    	while (notes.get(0) < i) 
			    		notes.remove(0);
			    	for (double j : notes){
			    		if (j >= i && j < i+1){
			    			temp++;
			    		}
			    		if (j >= i+1)
			    			break;
			    	}
			    	noteDensity.add(temp);
			    }
			    
			    //remove leading 0's in noteDensity
			    while (!noteDensity.isEmpty() && noteDensity.get(0) == 0)
			    	noteDensity.remove(0);
			    
			    //System.out.println(noteDensity);
			    //Collections.sort(noteDensity);
			    //add noteDensity to retval (a 2D arrayList)
			    retval.add(new NoteDensity(noteDensity, difficulty, title, numKeys));
		    }
		    
		    //clear shit
		    currentSecond = 0;
		    currentBeat = 0;
		    notes.clear();
		    //read.readLine();
	    } //end of enormous while loop
	    
	    return retval;
	}
	
	public static void main(String[] args) throws Exception{
		JFrame input = new InputFrame();
		input.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		input.setSize(740, 93);
		input.setLocation(0, 300);
		input.setResizable(false);
		//input.setIconImage(ImageIO.read(test.class.getClassLoader().getResource("Down Hold Head Inactive 12th.png")));
		input.setTitle("Enter Folder");
		input.setVisible(true);
		/*
		Scanner sc = new Scanner(System.in);
		ArrayList<NoteDensity> noteDensities = null;
		try {
			//System.out.print("# of keys? (4/6) ");
			//int numKeys = Integer.parseInt(sc.nextLine());
			//if (numKeys != 4 && numKeys != 6)
			//	throw new Exception();
			
			System.out.print("Input file: ");
			String s = sc.nextLine();
		    noteDensities = getNoteDensities(s, 4);
		}
		catch (FileNotFoundException fnfe){
			System.out.println("file not found");
			System.exit(0);
		}
		catch (Exception e){
			System.out.println("error: unable to read file");
			System.exit(0);
		}
	    for (NoteDensity al : noteDensities){
	    	JFrame f = new JFrame();
	        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        f.setSize(640,140);
	        f.setLocation(200,200);
	        f.setTitle(al.getTitle() + " (" + al.getDifficulty() + ") | Max NPS: " + getMax(al.getNoteDensity())); 
	        f.add(new drawGraph(al.getNoteDensity()));
	        f.setVisible(true);
	   }
	   */
	   
    }
	
	
	/**
	 * currently unused herp derp
	 * @param data
	 * @return
	 */
	private static int getMax(ArrayList<Integer> data){
		int max = -Integer.MAX_VALUE;
        for(int i = 0; i < data.size(); i++) {
            if(data.get(i) > max)
                max = data.get(i);
        }
        
        return max;
	}
 }