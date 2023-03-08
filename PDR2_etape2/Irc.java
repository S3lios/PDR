import java.awt.*;
import java.awt.event.*;
import java.rmi.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;
import java.rmi.registry.*;


public class Irc extends Frame {

	public TextArea		text;
	public TextField	cptText;
	public Panel		subsPanel;
	public int 			cpt;
	public TextField	data;
	SharedObject		sentence;
	static String		myName;

	public static void main(String argv[]) {
		
		if (argv.length != 1) {
			System.out.println("java Irc <name>");
			return;
		}
		myName = argv[0];
	
		// initialize the system
		Client.init();
		
		// look up the IRC object in the name server
		// if not found, create it, and register it in the name server
		SharedObject s = Client.lookup("IRC");
		if (s == null) {
			s = Client.create(new Sentence());
			Client.register("IRC", s);
		}
		// create the graphical part
		new Irc(s);
	}

	public Irc(SharedObject s) {
		setName(myName);
	
		setLayout(new FlowLayout());
		
		text=new TextArea(10,60);
		text.setEditable(false);
		text.setForeground(Color.red);
		add(text);
	
		data=new TextField(60);
		add(data);
		Button write_button = new Button("write");
		write_button.addActionListener(new writeListener(this));
		add(write_button);
		Button read_button = new Button("read");
		read_button.addActionListener(new readListener(this));
		add(read_button);
		Button subscribe_button = new Button("subscribe");
		subscribe_button.addActionListener(new subscribeListener(this));
		add(subscribe_button);
		Button unsubscribe_button = new Button("unsubscribe");
		unsubscribe_button.addActionListener(new unsubscribeListener(this));
		add(unsubscribe_button);

		Button track_button = new Button("track");
		track_button.addActionListener(new trackListener(this));
		add(track_button);

		Button leave_track_button = new Button("leave_track");
		leave_track_button.addActionListener(new leave_trackListener(this));
		add(leave_track_button);
		
		subsPanel = new Panel();
		subsPanel.setBackground(Color.BLACK);
		add(subsPanel);


		cptText = new TextField();
		cptText.setEditable(false);
		cptText.setText("0");

		add(cptText);

		setSize(600,300);
		text.setBackground(Color.BLACK); 

		show();
		
		sentence = s;
	}
}



class readListener implements ActionListener {
	Irc irc;
	public readListener (Irc i) {
		irc = i;
	}
	public void actionPerformed (ActionEvent e) {
		
		// lock the object in read mode
		irc.sentence.lock_read();
		
		// invoke the method
		String s = ((Sentence)(irc.sentence.obj)).read();
		
		// unlock the object
		irc.sentence.unlock();
		
		// display the read value
		irc.text.append(s+"\n");

		if (irc.sentence.isSubscribed()) {
			irc.cpt = 0;
			irc.cptText.setText("0");
			irc.subsPanel.setBackground(Color.GREEN);
		}

	}
}

class writeListener implements ActionListener {
	Irc irc;
	public writeListener (Irc i) {
        	irc = i;
	}
	public void actionPerformed (ActionEvent e) {
		
		// get the value to be written from the buffer
        String s = irc.data.getText();
        
        // lock the object in write mode
		irc.sentence.lock_write();
		
		// invoke the method
		((Sentence)(irc.sentence.obj)).write(Irc.myName+" wrote "+s);
		irc.data.setText("");
		
		// unlock the object
		irc.sentence.unlock();

		if (irc.sentence.isSubscribed()) {
			irc.cpt = 0;
			irc.cptText.setText("0");
			irc.subsPanel.setBackground(Color.GREEN);
		}
		

	}
}

class subscribeListener implements ActionListener {
	Irc irc;

	public subscribeListener(Irc i) {
		irc = i;
	}

	public void actionPerformed (ActionEvent e) {
		irc.sentence.setCallBack(new CallBack() {
			
			@Override
			public void call() {
				irc.cpt++;
				irc.cptText.setText(irc.cpt + "");
				irc.subsPanel.setBackground(Color.ORANGE);
			}
			
		});

		irc.sentence.subscribe();
		irc.subsPanel.setBackground(Color.GREEN);
	}
}

class unsubscribeListener implements ActionListener {
	Irc irc;
	public unsubscribeListener(Irc i) {
		irc = i;
	}

	public void actionPerformed (ActionEvent e) {
		irc.sentence.unsuscribe();
		irc.cpt = 0;
		irc.cptText.setText(irc.cpt + "");
		irc.subsPanel.setBackground(Color.BLACK);
	}
}

class trackListener implements ActionListener {
	Irc irc;
	public trackListener(Irc i) {
		irc = i;
	}

	public void actionPerformed (ActionEvent e) {
		irc.sentence.setCallBack(new CallBack() {
			
			@Override
			public void call() {
				// lock the object in read mode // passer un objet en param du Callback pour éviter de lock/unlock
				irc.sentence.lock_read();
		
				// invoke the method
				String s = ((Sentence)(irc.sentence.obj)).read();
		
				// unlock the object
				irc.sentence.unlock();
		
				// display the read value
				irc.text.append(s+"\n");
			}
			
		});
		irc.sentence.track();
		irc.subsPanel.setBackground(Color.BLUE);
	}
}

class leave_trackListener implements ActionListener {
	
	Irc irc;
	public leave_trackListener(Irc i) {
		irc = i;
	}

	public void actionPerformed (ActionEvent e) {
		irc.sentence.leave_track();
		irc.subsPanel.setBackground(Color.BLACK);
	}
}

