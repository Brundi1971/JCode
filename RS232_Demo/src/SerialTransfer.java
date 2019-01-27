import java.awt.Choice;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
//import java.sql.DriverManager;
import java.sql.ResultSet;
//import java.sql.Statement;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
//import javax.swing.JSlider;
import javax.swing.Timer;

import com.fazecast.jSerialComm.*;

import java.lang.Thread;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;


//import java.util.concurrent.TimeUnit

public class SerialTransfer {
	
	public static int i=0;
	public static float temp;
	public static JLabel lab, lb_status;
	public static JButton btn;
	public static JFrame win;
	public static Scanner scan;
	public static Boolean AccesDB = false;
	public static Connection myConn = null;
	public static long PointerID;
	public static String query;
	public static PreparedStatement myStmt1;
	public static Boolean AccesCOM = false;
	public static Date dNow;
	public static SimpleDateFormat ft;
			
	public static void main(String[] args)
	{		
		win = new JFrame();
		win.setBounds(100, 100, 500, 300);
		//win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		win.getContentPane().setLayout(null);
		
		btn = new JButton("Open");
		btn.setBounds(150, 20, 70, 20);	
		
		JButton btnDB = new JButton("Open DB");
		btnDB.setBounds(250, 20, 100, 20);	
		
		lab = new JLabel("T :");
		lab.setBounds(150, 100, 300, 35);
		lab.setFont(new Font(lab.getName(),Font.BOLD ,30));
		
		lb_status = new JLabel("status_connection : ");
		lb_status.setBounds(10, 10, 300, 35);
		//lb_status.setFont(new Font(lab.getName(),Font.BOLD ,30));
		lb_status.setVisible(false);
		
		Choice myList = new Choice();
		myList.setBounds(10, 20, 100, 20);
		
		JPanel P = new JPanel();
		P.setBounds(10, 150, 300, 50);
		P.setBackground(Color.PINK);
		P.setName("STATUS");
		
		
		win.getContentPane().add(btn);
		win.getContentPane().add(btnDB);
		win.getContentPane().add(myList);
		win.getContentPane().add(lab);
		P.add(lb_status);
		win.getContentPane().add(P);
		win.setVisible(true);
		
		dNow = new Date();
		ft = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a"); //("E yyyy.MM.dd 'at' hh:mm:ss a zzz");
		System.out.println(ft.format(dNow));
		
			
		SerialPort ports[] = SerialPort.getCommPorts();	
		for(int i = 0; i < ports.length; i++){myList.add(ports[i].getSystemPortName());}
		if (myList.getItemCount() == 0){ btn.setEnabled(false);}
		System.out.println(myList.getItemCount());

		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				
				//int j = myList.getSelectedIndex();
				SerialPort port = ports[myList.getSelectedIndex()];
				port.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
				
				if (port.isOpen()){
					port.closePort();
					//System.out.println("Portul a fost inchis!");
					JOptionPane.showMessageDialog(null, "Portul " + port.getSystemPortName() + " a fost inchis!");
					btn.setText("Open");
					myList.setEnabled(true);
				}
				else
				{
					if (port.openPort())
					{
						//System.out.println("Portul a fost deschis!");
						JOptionPane.showMessageDialog(null, "Portul " + port.getSystemPortName() + " a fost deschis!");
						myList.setEnabled(false);
						btn.setText("Close");
						
						Thread thread = new Thread(){
							@Override public void run(){
								scan = new Scanner(port.getInputStream());//.useDelimiter(" ");
								//Scanner scan = new Scanner(System.in);//.useDelimiter(" ");
								//catch(Exception e){System.out.println("Nu a reusit functia");}
								//System.out.println("Delimitatorul: " + scan.delimiter());
								
								while(scan.hasNextLine())
								{
									//int numar=0;
									try{temp = Float.parseFloat(scan.nextLine());}catch(Exception e){}
									//slider.setValue(numar);
									lab.setText(temp + " grade C");
									AccesCOM = true;
									//System.out.println(temp);
								}
								scan.close();
							}
						};
						thread.start();
					}
					else
					{
						//System.out.println("Portul nu poate fi deschis");
						JOptionPane.showMessageDialog(null, "Portul " + port.getSystemPortName() + " nu poate fi deschis!");
					}
				}
			}
		});
		
		/*
		try{
			final String ConnectionUrl = "jdbc:mysql://localhost:3306/testdb"+,+"root"+,+"mugly11";
			//System.out.println(ConnectionUrl);
			Class.forName("com.mysql.jdbc.Driver");
			myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/testdb","root","mugly11");
			//myConn=DriverManager.getConnection(ConnectionUrl);
			//JOptionPane.showMessageDialog(null, "S-a realizat conectarea la baza de date testdb!");
			System.out.println("Conectare realizata ");
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		*/
				
		btnDB.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				//ConnectionUrl = "jdbc:mysql://localhost:3306/selenium"+textField.getText()+passwordField.getPassword();
				final String ConnectionUrl = "jdbc:mysql://localhost:3306/testdb;User=root;Password=mugly11";
				System.out.println(ConnectionUrl);
				
				try 
				{
					//Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
					Class.forName("com.mysql.jdbc.Driver");
					myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/testdb","root","mugly11");
					//myConn=DriverManager.getConnection(ConnectionUrl);
					//JOptionPane.showMessageDialog(null, "S-a realizat conectarea la baza de date testdb!");
					System.out.println("Conectare realizata ");
					
					//Statement myStmt = myConn.createStatement();
					PreparedStatement myStmt = myConn.prepareStatement("SELECT COUNT(ID) FROM tabel_temp");
					//ResultSetmyRs = myStmt.executeQuery("SELECT COUNT (ID) FROM tabel_temp");
					ResultSet myRs = myStmt.executeQuery();
					
					while(myRs.next())
					{
						//System.out.println(myRs.getString());
						PointerID = myRs.getLong("COUNT(ID)");
						
						//System.out.println(myRs.getString("COUNT(ID)"));
					}	
					AccesDB = true;
					
					//lb_status.setBounds(0,win.getHeight()-lb_status.getHeight()-30, 200, 35);
					lb_status.setVisible(true);
					lb_status.setText("Status DB connection [connected]; PointerID [" + PointerID + "]");
				
					System.out.println("s-a iesit : " + myConn.isClosed());
								
					//String query = "INSERT INTO furnizoriinfo (ID, Companie, CUI) VALUE ('6', 'PIGA', '12133')";
					//JOptionPane.showMessageDialog(null, query);
				
					//PreparedStatement myStmt1 = myConn.prepareStatement(query);
					//myStmt1.execute();
					//myStmt1.close();
					
				} 
				catch (Exception e) 
				{
					JOptionPane.showMessageDialog(null, "Conectarea la baza de date selenium a esuat !");
					e.printStackTrace();
				}
			}
		});
		
	
		System.out.println("S-a intrat in Timer");
		Timer tm = new Timer(1000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				modify_variable();
			}
		});
		tm.start();
		
		
		win.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		//win.setDefaultCloseOperation(port.closePort());
		win.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		        if (JOptionPane.showConfirmDialog(win, 
		            "Are you sure to close this window?", "Doresti sa inchizi?", 
		            JOptionPane.YES_NO_OPTION,
		            JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
		            //port.closePort();
		            System.exit(0);
		        }
		    }
		});
	}
	
	
	public static void modify_variable()
	{
		lab.setText(Integer.toString(i));
		//i++;	
		//System.out.println(ft.format(dNow));
		
		if ((AccesDB) && (AccesCOM)){
			try
			{
				PointerID++;
				query = "INSERT INTO tabel_temp (ID, Locatia, Data, Temperatura) VALUES ('" + PointerID + "', 'Acasa', '" + ft.format(dNow) + "', '" + temp + "')";
				//JOptionPane.showMessageDialog(null, query);
				//System.out.println("s-a intrat : " + PointerID);
		
				myStmt1 = myConn.prepareStatement(query);
				myStmt1.execute();
				myStmt1.close();
			}
			catch (Exception e) 
			{
				//JOptionPane.showMessageDialog(null, "Conectarea la baza de date testdb a esuat !");
				e.printStackTrace();
				//System.out.println("s-a iesit : " + myConn.isClosed());
			}
		}	
	}
}
	
	/*
	win.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			System.out.println(arg0);
			
		}
	});
	
	//ports = SerialPort.getCommPorts();	
	System.out.println("Selecteaza un port!");
	//for (port : ports) 
	for(int i = 0; i < ports.length; i++)
	{
		myList.add(ports[i].getSystemPortName());
		System.out.println(i + ". " + ports[i].getSystemPortName());
	}
	
	Scanner s = new Scanner(System.in);
	int chosePort = s.nextInt();
	System.out.println(chosePort);
	
	SerialPort port = ports[chosePort - 1];
		
	//if (port.isOpen()){System.out.println("Portul este inca deschis si va fi oprit!"); port.closePort();}
	//port.setBaudRate(9600);
	//port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
	port.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
	if(port.openPort())
	{
		System.out.println("Portul a fost deschis!");
		System.out.println("BaudRate: " + port.getBaudRate());
		System.out.println("Port: " + port.getSystemPortName());
		System.out.println("Port: " + port.getDescriptivePortName());
	}
	else
	{
		System.out.println("Portul nu poate fi deschis!");
		return;
	}
	
	//port.setBaudRate(57600);
	//port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
	//port.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
	
	Scanner scan = new Scanner(port.getInputStream());//.useDelimiter(" ");
	//Scanner scan = new Scanner(System.in);//.useDelimiter(" ");
	//catch(Exception e){System.out.println("Nu a reusit functia");}
	System.out.println("Delimitatorul: " + scan.delimiter());
	
	while(scan.hasNextLine())
	{
		int numar=0;
		try{numar = Integer.parseInt(scan.nextLine());}catch(Exception e){}
		//if (data.hasNextLine()) 
		//{
			//System.out.println(scan.nextLine());
		//}
		System.out.println(numar);
		slider.setValue(numar);
		lab.setText("Temperatura ambientala: " + numar);
	}
	
	System.out.println("S-a iesit: " + port.getDescriptivePortName());
	port.closePort();	
*/


//@Override
//protected void finalize() throws Throwable {
	// TODO Auto-generated method stub
	//super.finalize();
//}
	
	
