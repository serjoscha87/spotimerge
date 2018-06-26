package Main;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.LookAndFeel;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


public class Main_Spotimerge implements ActionListener, ClipBoardListener, DocumentListener {

	JTextArea ta = new JTextArea();
	JTextArea taBl = new JTextArea();
	JCheckBox cbAutoCopy = new JCheckBox();
	JLabel labelLinksCount = new JLabel("0");
	Border b = null;
	
	public Main_Spotimerge() {		
		JFrame f = new JFrame("Spotimerge");
		b = BorderFactory.createLineBorder(f.getRootPane().getBackground(), 10);
		f.setLayout(new BoxLayout(f.getContentPane(), BoxLayout.PAGE_AXIS));
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getRootPane().setBorder(b);
		//f.setIconImage(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB_PRE));
		f.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("icon.png")));

		/*
		 *  Elems
		 */
		
		JLabel l1 = new JLabel("Spotimerge. Merge Playlist and exclude a backlist");
		l1.setFont(new Font("Arial", Font.BOLD, 15));
		l1.setBorder(b);
		
		JPanel p1 = new JPanel(); // panel for the TAs
		p1.setLayout(new BorderLayout());
		
		JPanel p3 = new JPanel(); // für die input areas
		p3.setLayout(new GridLayout(1,2, 5,0));
		ta.setRows(30);
		ta.setColumns(35);
		ta.getDocument().addDocumentListener(this);
		taBl.setRows(30);
		taBl.setColumns(35);
		JScrollPane spTa = new JScrollPane(ta);
		spTa.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		JScrollPane spBl = new JScrollPane(taBl);
		p3.add(spTa);
		p3.add(spBl);
		
		JPanel p2 = new JPanel(); // für die labels
		p2.setLayout(new GridLayout(1,2, 5,0));
		JLabel l2 = new JLabel("URLs zu den Songs aus allen Playlists");
		JLabel l3 = new JLabel("URLs zu den BLACKLIST Songs");
		p2.add(l2);
		p2.add(l3);
		
		// add the 2 panes to the first container pane
		p1.add(p2, BorderLayout.NORTH);
		p1.add(p3, BorderLayout.CENTER);
		
		JPanel p4 = new JPanel();
		p4.setLayout(new FlowLayout());
		JButton mergeButton = new JButton("merge");
		mergeButton.addActionListener(this);
		//
		cbAutoCopy.setText("(?) Autocopy?");
		//cbAutoCopy.setSelected(true);
		cbAutoCopy.setToolTipText("In die Zwischenablage kopierte Spotify Links automatisch in das linke Eingabefeld (Non-Blacklist) übernehmen?");
		//
		
		//
		p4.add(cbAutoCopy);
		p4.add(mergeButton);
		p4.add(new JLabel("Eingefügte Links:"));
		p4.add(labelLinksCount);
		
		f.add(l1);
		l1.setAlignmentX(Component.CENTER_ALIGNMENT);
		f.add(p1);
		f.add(p4);
		//mergeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		// Rest
		
		f.pack();
		f.setLocationRelativeTo(null);
		f.setVisible(true);
		
		/*
		 * Clipboard
		 */
	    ClipBoard cb = new ClipBoard();
	    cb.setClipBoardListener(this);
	    cb.start();
	}

	public void actionPerformed(ActionEvent e) {
		/*
		 *  Logic
		 */
		ArrayList<String> songs = new ArrayList<String>();
		
		// Fill array with all songst but prevent dupes
		for(String currRow : ta.getText().split("\n")) {
			if(!songs.contains(currRow))
				songs.add(currRow);
		}
		
		// remove blacklisted songs
		for(String currRow : taBl.getText().split("\n")) {
			if(songs.contains(currRow))
				songs.remove(songs.indexOf(currRow));
		}
		
		
		/*
		 *  UI
		 */
		JFrame resFr = new JFrame("Ergebnis");
		resFr.setLayout(new BorderLayout());
		//res.getRootPane().setBorder(b);
		resFr.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("icon.png")));
		resFr.setSize(new Dimension(800, 600));
		
		JTextArea resArea = new JTextArea(String.join("\n", songs));
		JScrollPane resSP = new JScrollPane(resArea);
		
		JLabel labelResCount = new JLabel("Nach merge übrige Links: "+songs.size());
		labelResCount.setBorder(b);
		
		resFr.add(resSP, BorderLayout.CENTER);
		resFr.add(labelResCount, BorderLayout.SOUTH);
		//resFr.pack();
		resFr.setLocationRelativeTo(null);
		resFr.setVisible(true);
	}

	private int last_cb = 0;
	public void ClipBoardChanged(String s) {
		
		if(!cbAutoCopy.isSelected()) return;
		
		int curr_cb = s.hashCode();
		
		if(last_cb == curr_cb) {
			// CB updated but content unchanged
			System.out.println("UNCHANGED");
		}
		else {
			// CB updated, content CHANGED!
			last_cb = curr_cb;
			
			if(s.split("\n")[0].substring(0, 24).equals("https://open.spotify.com")) {  
				ta.setText(ta.getText() + "\n" + s);
			}
		}
	}
	
	public void changedUpdate(DocumentEvent e) {
		taUpdated();
	}
	public void insertUpdate(DocumentEvent e) {
		taUpdated();
	}
	public void removeUpdate(DocumentEvent e) {
		taUpdated();
	}
	
	public void taUpdated() {
		labelLinksCount.setText(ta.getText().split("\n").length+"");
	}
	
	/*
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		new Main_Spotimerge();
	}

}
