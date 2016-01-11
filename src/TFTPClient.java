import java.io.File;
import java.net.*;
import javax.swing.*;


public class TFTPClient {
	

	
	public static void main(String[] args) throws UnknownHostException {
		// TODO Auto-generated method stub

		
		String filePath, ipAddress, fileName;
		
		int port;
		
		InetAddress address;
		
		JFileChooser browse = new JFileChooser();
		
		boolean done = false;
                
                ipAddress = JOptionPane.showInputDialog("Enter Server IP. /n ex. xxx.xxx.xxx");      
                port = Integer.parseInt(JOptionPane.showInputDialog("Please enter the port"));
                address = InetAddress.getByName(ipAddress);
                
      while(!done)
    {
        
          
        boolean correct = false;
        while(!correct)
        {
            int readOrWrite = Integer.parseInt(JOptionPane.showInputDialog("Enter the numer of the option you would like \n"
                                                                            +"1 - Send file to server \n"
                                                                            +"2 - Download file from server \n"
		                                                            +"3 - exit"));
         
         

            switch(readOrWrite)
            {
                case 1://upload to server
                    TFTPWrite c = new TFTPWrite(); 
                    browse.showOpenDialog(null);
               
                    File file = browse.getSelectedFile();
                    System.out.println(file.getName());//@testingPurposes
                    
                    c.writeFile(file,"octet",address,port);
                    correct = !correct;
                    break;
                    
                case 2://download from server
                    
                    TFTPRequest request = new TFTPRequest();
                    String input = JOptionPane.showInputDialog("Enter the name of the file you are looking for.");
                   request.readFile(input, "octet", address, port);
                    
                   
                    correct = !correct;
                    break;
                    
                case 3://exit program
                    System.exit(0);
                    
                default://invalid entry loop back to question
                    JOptionPane.showMessageDialog(null, "Error: Not a valid Option. Please enter valid number.");
                    break;
            }
        }
        

        String repeat = JOptionPane.showInputDialog("Type \"Y\" to repeat or any other key to end.");//asks user if they want to repeat
        
        if (!repeat.equalsIgnoreCase("y"))//controls when to exit loop 
            done = !done;
    }
    
    }
                
                
                
	

}