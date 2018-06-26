package Main;

import java.awt.Toolkit;
import java.awt.datatransfer.*;
import java.io.IOException;

public class ClipBoard extends Thread implements ClipboardOwner {
	
	Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
	
	ClipBoardListener cblistener = null;

	public void run() {
		Transferable trans = sysClip.getContents(this);
		regainOwnership(trans);
		while (true) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) { e.printStackTrace(); }
		}
	}
	
	public void setClipBoardListener(ClipBoardListener h) {
		this.cblistener = h;
	}

	public void lostOwnership(Clipboard c, Transferable t) {
	    Transferable contents = sysClip.getContents(this); //EXCEPTION
	    processContents(contents);
	    regainOwnership(contents);
	}

	void processContents(Transferable t) {
		//System.out.println("Processing: " + t);
		try {
			this.cblistener.ClipBoardChanged((String)sysClip.getData(DataFlavor.stringFlavor));
		} catch (UnsupportedFlavorException e) {e.printStackTrace();
		} catch (IOException e) { e.printStackTrace(); }
	}

	void regainOwnership(Transferable t) {
		sysClip.setContents(t, this);
	}
}