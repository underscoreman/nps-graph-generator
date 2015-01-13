import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.Box.Filler;
import javax.swing.Timer;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.io.*;
import java.nio.charset.CharacterCodingException;

public class InputFrame extends JFrame{
	private static final int FRAME_WIDTH = 450;
	private static final int FRAME_HEIGHT = 100;
	
	private JButton button1, browse, buttonClear, closeFiles;
	private JPanel panel;
	private JTextField input, output;
	private ArrayList<JFrame> outputFrames = new ArrayList<JFrame>();
	private JLabel inputLabel, outputLabel;
	private Component parent;
	private Checkbox checkbox = new Checkbox("Write to Output Directory");
	private Checkbox checkbox2 = new Checkbox("Write to Song Folder");
	private Checkbox popUp = new Checkbox("Pop graphs up");
	
	/**
	 * The main constructor
	 */
	public InputFrame(){
		createTextField();
		createButton();
		createPanel();
	}
	
	/**
	 * Creates the main text fields
	 */
	private void createTextField(){
		inputLabel = new JLabel("Enter Folder: ");
		input = new JTextField(10);
		outputLabel = new JLabel("Output Directory: ");
		output = new JTextField(10);
	}
	
	/**
	 * The inner class defining the main graph generator button
	 * @author ssunnn
	 *
	 */
	class GenerateGraphListener implements ActionListener{
		public void actionPerformed(ActionEvent event){
			long startTime = System.currentTimeMillis();
			String folder = input.getText();
			File curr = new File(folder);
			ArrayList<File> matchingFiles = findFile(curr, ".sm");
			for (int i=0; i<matchingFiles.size(); i++){/*
				ArrayList<NoteDensity> noteDensities = null;
				try{
					noteDensities = test.getNoteDensities(x, 4);
					noteDensities.addAll(test.getNoteDensities(x,6));
					noteDensities.addAll(test.getNoteDensities(x,8));
				}
				catch(Exception e) {continue;}
				for (NoteDensity al : noteDensities){
			    	JFrame f = new JFrame();
			    	outputFrames.add(f);
			        //f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			        f.setSize(640,170);
			        f.setLocation(650,280);
			        String temp = al.getTitle() + " (" + al.getDifficulty() + ") | Average NPS: " + new DecimalFormat("##.##").format(getAverageNPS(al.getNoteDensity())) + " | Max NPS: " + getMax(al.getNoteDensity());
			        f.setTitle(temp); 
			        f.add(new drawGraph(al.getNoteDensity(), temp));
			        f.setVisible(true);
			   }
			   */
				File x = matchingFiles.get(i);
				try {
					writeImages(x, checkbox.getState(), checkbox2.getState(), popUp.getState());
				} catch (IOException e) {continue;} //IF ANY UNMAPPABLE CHARACTER EXCEPTIONS OCCUR gaygaygaygay
			}
			System.out.println(System.currentTimeMillis() - startTime);
		}
	}
	
	/**
	 * clear input
	 */
	class ClearInput implements ActionListener{
		public void actionPerformed(ActionEvent event){
			input.setText("");
		}
	}
	
	class CloseFiles implements ActionListener{
		public void actionPerformed(ActionEvent event) {
			for (JFrame j : outputFrames)
				j.setVisible(false);
		}
	}
	class ChooseFile implements ActionListener{
		public void actionPerformed(ActionEvent event){
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			/*try {
				Scanner sc = new Scanner(new File("data.txt"));
				String s = sc.nextLine();
				chooser.setCurrentDirectory(new File(s));
			} catch (FileNotFoundException e) {}*/
			int returnVal = chooser.showOpenDialog(parent);
			if (returnVal == JFileChooser.APPROVE_OPTION){
				String temp = chooser.getSelectedFile().getAbsolutePath();
				input.setText(temp);
				System.setProperty("user.home", new File(temp).getParent());
			}
		}
	}
	
	/**
	 * creates the buttons
	 */
	private void createButton(){
		button1 = new JButton("Generate NPS Graph");
		buttonClear = new JButton("Clear input");
		closeFiles = new JButton("Close graphs");
		closeFiles.addActionListener(new CloseFiles());
		browse = new JButton("Browse...");
		browse.addActionListener(new ChooseFile());
		button1.addActionListener(new GenerateGraphListener());
		buttonClear.addActionListener(new ClearInput());
	}
	
	/**
	 * creates all the panels
	 */
	private void createPanel(){
		panel = new JPanel();
		panel.add(inputLabel);
		panel.add(input);
		panel.add(button1);
		panel.add(browse);
		panel.add(buttonClear);
		panel.add(closeFiles);
		panel.add(outputLabel);
		panel.add(output);
		panel.add(checkbox);
		panel.add(checkbox2);
		panel.add(popUp);
		add(panel);
	}
	
	/**
	 * gets max
	 * @param data
	 * @return max of data
	 */
	private static int getMax(ArrayList<Integer> data){
		int max = -Integer.MAX_VALUE;
        for(int i = 0; i < data.size(); i++) {
            if(data.get(i) > max)
                max = data.get(i);
        }
        
        return max;
	}
	
	private static double getAverageNPS(ArrayList<Integer> data){
		double retval = 0;
		for (int i : data)
			retval+=i;
		return retval/data.size();
	}
	
	private static int getTotalNotes(ArrayList<Integer> data){
		int retval = 0;
		for (int i=0; i<data.size(); i+=2)
			retval+=data.get(i);
		return retval;
	}
	
	private static String timeConversion(int x){
		if (x < 0) throw new IllegalArgumentException();
		return x/60 + ":" + ((x%60 < 10) ? ("0"+x%60) : x%60);
	}
	
	/**
	 * finds all files in dir and its subfolders ending with end.
	 * @param dir
	 * @param end
	 * @return all files in dir and its subfolders ending with end.
	 */
	private ArrayList<File> findFile(File dir, String end){
		ArrayList<File> retval = new ArrayList<File>();
		for (File f : dir.listFiles()){
			if (f.isDirectory()){
				retval.addAll(findFile(f,end));
			}
			else
				if (f.toString().endsWith(".sm"))
					retval.add(f);
		}
		return retval;
	}
	
	private static String getInfo(NoteDensity al){
		return al.getTitle() + " (" + al.getDifficulty() + ") | Average NPS: " + new DecimalFormat("##.##").format(getAverageNPS(al.getNoteDensity())) + " | Max NPS: " + getMax(al.getNoteDensity());
	}
	
	private void writeImages(File fi, boolean writeDesktop, boolean writeSongFolder, boolean popUp) throws IOException{
		if (fi.toString().endsWith(".sm")){
			ArrayList<NoteDensity> noteDensities = new ArrayList<NoteDensity>();
			try{
			noteDensities = test.getNoteDensities(fi);
			}
			catch(IOException e){}
			String s = "";
			if (output.getText().trim().equals("")) 
				s = System.getProperty("user.home") + "/Desktop/nps";
			else if (output.getText().startsWith("DESKTOP"))
				s = System.getProperty("user.home") + "/Desktop" + output.getText().substring(7);
			else
				s = output.getText();
			
			if (writeSongFolder)
				new File(fi.getParentFile().toString() + "/nps").mkdir();
			if (writeDesktop)
     			new File(s).mkdir();
			for (NoteDensity nd : noteDensities){
				ArrayList<Integer> data = nd.getNoteDensity();
				//MAKE IMAGE
		        //widht and height
		        int w = 575;
		        int h = 100;
		        
				BufferedImage bi = new BufferedImage(w+50, h+30, BufferedImage.TYPE_INT_ARGB);
		        Graphics2D g2 = bi.createGraphics(); //(Graphics2D) g;
		        drawGraph(g2, nd, w, h);
		        
		        
		        //raito image
		        try { //[^a-zA-Z0-9\\.\\-]
		        	String filename = nd.getTitle().toLowerCase().replaceAll("[\\/:*?\"<>|]", "").replaceAll("\\s", "-")+"-"+nd.getDifficulty().toLowerCase()+"-"+nd.getNumKeys()+"key.png";
		        	if (writeDesktop)
		        		ImageIO.write(bi, "PNG", new File(s + "/" + filename));
		        	if (writeSongFolder)
		        		ImageIO.write(bi, "PNG", new File(fi.getParentFile().toString() + "/nps/" + filename));
				} catch (IOException e) {}
		        
		        //pop up
		        if (popUp){
		        	JFrame j = new JFrame();
		        	outputFrames.add(j);
		        	j.setSize(w+55,h+58);
			        j.setLocation(650,280);
			        j.setTitle(nd.getTitle() + " (" + nd.getDifficulty() + ") | Notes: " + getTotalNotes(data) + " | Length: " + timeConversion(data.size()/2) + " | # Keys: " + nd.getNumKeys());
			        j.setResizable(false);
			        //j.setIconImage(ImageIO.read(test.class.getClassLoader().getResource("Down Hold Head Inactive 12th.png")));
			        j.add(new asdf(nd));
			        j.setVisible(true);
		        }
			}
		}
	}
	
	public static void drawGraph(Graphics2D g2, NoteDensity nd, int w, int h){
		ArrayList<Integer> data = nd.getNoteDensity();
		
		 g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
              RenderingHints.VALUE_ANTIALIAS_ON);
		//w = (w * data.size())/180;
		//set font
		Font f = new Font("Verdana", Font.BOLD, 12);
		g2.setFont(f);
		
		//set background
		g2.setComposite(AlphaComposite.Clear);
		g2.fillRect(0, 0, 0, 0);
		
		// Draw ordinate.
		//g2.draw(new Line2D.Double(PAD, PAD, PAD, h-PAD));
		// Draw abcissa.
		//g2.draw(new Line2D.Double(PAD, h-PAD, w-PAD, h-PAD));
		//left border
		g2.setComposite(AlphaComposite.Src);
		g2.draw(new Line2D.Double(50,h,50,0));
		
		// Draw 7/14/21/28 lines
		g2.setPaint(Color.GRAY);
		for (int i=1; i<6; i++){
		if (i==5) g2.setPaint(Color.BLACK);
		g2.draw(new Line2D.Double(50, i*h/5, w+50, i*h/5));
		}
		//borders
		g2.draw(new Line2D.Double(50,0,50,h));
		g2.draw(new Line2D.Double(50,0,w+50,0));
		g2.draw(new Line2D.Double(w+49,0,w+49,h));
		
		//scaling
		double xInc = (double)(w)/(data.size()-1);
		double scale = (getMax(data) > 35) ? (double)(h)/getMax(data) : (double)(h)/35;
		
		// Mark data points.
		//for(int i = 0; i < data.length; i++) {
		//    double x = PAD + i*xInc;
		//    double y = h - PAD - scale*data[i];
		//    g2.fill(new Ellipse2D.Double(x-2, y-2, 4, 4));
		//}
		//draw graph and fill it in with appropriate coloring
		for(int i = 0; i < data.size() - 1; i++){
			//vertical lines (1:00)
		    //if (i%120 == 0)
				//g2.draw(new Line2D.Double(50+i*xInc, 0, 50+i*xInc, h));
			//polygon
			int[] xPoly = {(int) (50 + i*xInc), (int) (50 + (i+1)*xInc), (int) (50 + (i+1)*xInc), (int) (50 + i*xInc)};
			int[] yPoly = {(int) (h - scale*data.get(i)), (int) (h - scale*data.get(i+1)), h, h};
			Polygon tempTrapezoid = new Polygon(xPoly, yPoly, 4);
			g2.setPaint(blend(new Color(0,107,255), Color.RED, data.get(i+1)/(getMax(data)>35 ? getMax(data) : 35.0)));
			g2.fillPolygon(tempTrapezoid);
			g2.setPaint(Color.BLACK);
			g2.draw(new Line2D.Double(50 + i*xInc, h - scale*data.get(i), 50 + (i+1)*xInc, h - scale*data.get(i+1)));
		}
		
		//7/14/21/28 labels
		g2.setPaint(Color.GRAY);
		for (int i=1; i<5; i++)
		g2.drawString((getMax(data) > 35) ? ("" + getMax(data)*(5-i)/5.0) : ("" + 7*(5-i)), (getMax(data) > 35) ? 7 : 15, i*h/5 + 5);
		
		//metadata
		double textWidth = g2.getFont().getStringBounds(getInfo(nd), g2.getFontRenderContext()).getWidth();
		double fontSize = textWidth>w-16 ? 12*(w-16)/textWidth : 12;
		g2.setFont(new Font("Verdana", Font.BOLD, (int)fontSize));
		g2.drawString(getInfo(nd), 50, h + 20);

	}
	
	/**
	 * blends two colors c1 and c2, with weight ratio between 0 and 1.
	 * @param c1
	 * @param c2
	 * @param ratio
	 * @return an average of c1 and c2 (ratio)
	 */
	static Color blend( Color c1, Color c2, double ratio ) {
	    if ( ratio > 1f ) ratio = 1f;
	    else if ( ratio < 0f ) ratio = 0f;
	    double iRatio = 1.0f - ratio;

	    int i1 = c1.getRGB();
	    int i2 = c2.getRGB();

	    int a1 = (i1 >> 24 & 0xff);
	    int r1 = ((i1 & 0xff0000) >> 16);
	    int g1 = ((i1 & 0xff00) >> 8);
	    int b1 = (i1 & 0xff);

	    int a2 = (i2 >> 24 & 0xff);
	    int r2 = ((i2 & 0xff0000) >> 16);
	    int g2 = ((i2 & 0xff00) >> 8);
	    int b2 = (i2 & 0xff);

	    int a = (int)((a1 * iRatio) + (a2 * ratio));
	    int r = (int)((r1 * iRatio) + (r2 * ratio));
	    int g = (int)((g1 * iRatio) + (g2 * ratio));
	    int b = (int)((b1 * iRatio) + (b2 * ratio));

	    return new Color( a << 24 | r << 16 | g << 8 | b );
	}
	
	static class asdf extends JPanel{
		NoteDensity nd;
		final int w=575, h=100;
		
		public asdf(NoteDensity nd){
			this.nd = nd;
		}
		
		protected void paintComponent(Graphics g){
			InputFrame.drawGraph((Graphics2D)g, nd, w, h);
        }
	}
}


