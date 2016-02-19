
package lss;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;


/*
 * Lost Sale Saver
 * 
 * Review & save (or remove) lost sale incident files;
 * When saved, reformat to XML and add to lostsave.xml
 * 
 * Input file format:	[date]
 * 						[part number]
 * 						[customer]
 * 						comment 		<--optional
 */  

public class LostSaleSaver {
	
	static String myPath = "/lostsales/";
	static File dir = new File(myPath);
	static String dirString[] = new String[200];
	static JList<String> dirList;
	static JScrollPane dirScroll;
	static JTextArea text;
	static JScrollPane textScroll;
	static JLabel jlab;
	static String selectedFile = null;
	static String[] lines = new String[1000];

	private static void createAndShowGUI() {
		
		// Create and setup Frame
		JFrame f = new JFrame(" LostSale Saver");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setPreferredSize(new Dimension(1500,800));		
		f.getContentPane().setBackground(Color.white);
		f.setLayout(new FlowLayout());
		
		// Build buttons
		JButton btnSelect = new JButton("View Selected");
		btnSelect.setPreferredSize(new Dimension(50,30));
		JButton btnRemove= new JButton("Remove Selected");
		btnRemove.setPreferredSize(new Dimension(50,30));		
		JButton btnAddToDB = new JButton("Add to SaveFile");
		btnAddToDB.setPreferredSize(new Dimension(50,30));
		
		// Load Directory list
		dirString = dir.list();
		dirList = new JList<String>(dirString);
		dirList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);	
		dirScroll = new JScrollPane(dirList);
		dirScroll.setPreferredSize(new Dimension(400,600));
		
		// Load Text area
		text = new JTextArea();
		text.setBackground(new Color(248, 213, 131));
		textScroll = new JScrollPane(text);
		textScroll.setPreferredSize(new Dimension(850,600));
		
		// Current label
		jlab = new JLabel("Current selection: none");

		// Create Left Panel; add components
		JPanel lp = new JPanel();
		lp.setLayout(new BorderLayout());
		lp.add(dirScroll, BorderLayout.NORTH);
		lp.add(btnSelect);	
		lp.add(btnAddToDB,BorderLayout.SOUTH);
				
		// Create Right Panel; add components
		JPanel rp = new JPanel();
		rp.setLayout(new BorderLayout());
		rp.add(textScroll, BorderLayout.NORTH);
		rp.add(btnRemove, BorderLayout.SOUTH);	
		
		// Add panels to content pane
		f.add(lp, BorderLayout.WEST);
		f.add(rp, BorderLayout.EAST);
		f.add(jlab);

		// Display
		f.pack();
		f.setVisible(true);
	
		// Directory Listener
		dirList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent le) {
				int idx = dirList.getSelectedIndex();			
				if (idx != -1) {
					selectedFile = dirString[idx];
					jlab.setText("Current selection: " + selectedFile);	
				} else {
					selectedFile = null;
					jlab.setText("Current selection: none");
				}
			}		
		});
	
		// View Action listener 
		btnSelect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				text.setText("");
				// clear 
				for (int i=0; i<lines.length; i++)
					lines[i]=null;
				// load records into array
				try {
					BufferedReader in = new BufferedReader(new FileReader(myPath+selectedFile));
					String line;
					int i = 0;
					while ( (line = in.readLine()) != null) {
						lines[i]=line;
						i += 1;
					}
					in.close();								
				} catch (Exception ex) {}
					// build output
					for(String w: lines) {
						if (w != null) {
							text.append(w + '\n');
						}
					}
				}
		});	
	
		// Remove Action listener 
		btnRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				File deleteFile = new File(myPath + selectedFile);
				deleteFile.delete();
				dirString = dir.list();				
				dirList.setListData(dirString);
			}
		});
	
		// Add_to_DB Action listener 
		btnAddToDB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
			
				// Get selected files; 
				java.util.List<String> sval=dirList.getSelectedValuesList();
							 
				// Process each selection
				for(int i=0;i<sval.size();i++) {
					try {
						String fileToXML = sval.get(i);		                    			
						//System.out.println(fileToXML);
	            	
						// create lostsale.XML if not exist
						File xmlSave = new File(myPath + "lostsave.xml");
						xmlSave.createNewFile(); 
						dirString = dir.list();
						dirList.setListData(dirString);
						FileWriter xs = new FileWriter(xmlSave, true);	
	    			
						// copy records from current file to save file
						BufferedReader in = new BufferedReader(new FileReader(myPath+fileToXML));
						String line = null;
						StringBuffer xmlLine = null;
						while ( (line = in.readLine()) != null) {
							xmlLine = new StringBuffer("<save date=\"");
							int from = line.indexOf("[");
							int to = line.indexOf("]");
							xmlLine.append(line.substring(from+1,to) + "\" partno=\"");
							from = line.indexOf("[", to);
							to = line.indexOf("]", from);
							xmlLine.append(line.substring(from+1,to) + "\" customer=\"");
							from = line.indexOf("[", to);
							to = line.indexOf("]", from);
							xmlLine.append(line.substring(from+1,to) + "\" comment=\"");
							xmlLine.append(line.substring(to+1) + "\" \\>" + "\n"); 
							xs.write(xmlLine.toString());
						}
						in.close();				
						xs.close();
	        		} catch (Exception ex) {}        	            	
				} // end for
			}
		});	
		
	} // end createAndShowGUI
	
	// Place on event dispatch thread
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}
	
} // end class
