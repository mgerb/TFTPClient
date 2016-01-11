import java.io.File;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class TFTPWrite {

	private DatagramPacket outgoingPacket, incomingPacket, dataPacket;
	private DatagramSocket socket;
	
	
	//constructor that creates new socket
	public TFTPWrite()
	{
		 try
		 {
		  socket = new DatagramSocket();
		 } 
		 catch (SocketException se) 
		 {   
		      se.printStackTrace();
		 }
	}

	
	
    public void writeFile(File file, String mode, InetAddress address, int port) 
    { 
    	//create write receive message TFTP packet
        byte[] message = TFTPPacket.WriteRequest(file.getName(), mode);
        
        //create byte array for incoming ack message - size 4 bytes
        byte[] incmessage = new byte[4];
        
        
        
        short blockNumber;
        
        //create write receive datagram packet
        outgoingPacket = new DatagramPacket(message, message.length, address, port);

     

        //create incoming ack packet
        incomingPacket = new DatagramPacket(incmessage, incmessage.length);
        
        
        
        //try to send write receive packet
        try
        {
        	socket.send(outgoingPacket);
        	
        	System.out.println("Request Packet Sent");
        }
        
        catch(Exception e)
        {
        	System.out.println("Error on write request");
        	e.printStackTrace();
        }
        
        
        //receive packets
        try
        {
        	//receive first ack with block number 0
        	socket.receive(incomingPacket);
        	
        	incmessage = incomingPacket.getData();
        	
        	//will continue sending data packet if ack is received
        	if(incmessage[1] == 4)	
        	{
        		blockNumber = 1;	//sets the block number to 1
        		
        		System.out.println("Request Ack Received");
        		
        		//create new byte array for the file being sent
        		byte[] totalData = new byte[TFTPPacket.convertToBytes(file).length];
        		totalData = TFTPPacket.convertToBytes(file);
        		
        		
        		int counter = 0;
        		int numleft = totalData.length;
        		boolean nonNegative = true;
        		
        		
        		
        		while(nonNegative == true)
        		{
        			
        			byte[] data;
        			//create data byte array - size 516 = data(512) + (4)headers
        			if(numleft < 512)
        				data = new byte[numleft + 4];
        			
        			else
        				data = new byte[516];
        			
        			//create outgoing data packet
        	        dataPacket = new DatagramPacket(data, data.length, address, port);
        			
        			//set opcode for data packet
            		data[1] = 3;
            		
            		byte[] tempBlock = TFTPPacket.ackSend(blockNumber);
            		
        			//set block number
            		data[2] = tempBlock[2];
            		data[3] = tempBlock[3];	

            		System.out.println(data[2] + " " + data[3]);
            		
            	//if there are less than 512 bits left in the file the loop will send the last
            	// packet and then terminate
        		if (numleft < 512 )
        		{
        			System.arraycopy(totalData, counter, data, 4, numleft);
        			
            		nonNegative = false;
        		}
        		
        		//else add bytes to data packet starting where it left off before
        		else
        		{
            		System.arraycopy(totalData, counter , data, 4, 512);
        		}
  
        		//set data packet and sent it
        		dataPacket.setData(data);
        		dataPacket.setPort(incomingPacket.getPort());
        		socket.send(dataPacket);
        		System.out.println("Packet Sent");
        		
        		
        		//receive ack message with block number
        		socket.receive(incomingPacket);
        		
        		byte[] tempB = incomingPacket.getData();
        		
        		ByteBuffer bb = ByteBuffer.allocate(2);
				bb.order(ByteOrder.LITTLE_ENDIAN);
				bb.put(tempB[3]);
				bb.put(tempB[2]);
				blockNumber = bb.getShort(0);
        		blockNumber++;
        		
        		counter = counter + 512;
        		numleft = numleft - 512;
        		
        		
        		
        		//checks to see if the packet received is an error message
        		byte[] checkAck = incomingPacket.getData();
        		if (checkAck[1] == 4)
        			System.out.println("Ack Received");
        		else
        			System.out.println("Error");
        		}
        	}
        	
        	else
        	{
        		System.out.println("Error receiving ack message");
        		
        	}
        }
        
        catch(Exception e)
        {
        	System.out.println("Error Receiving");
        	e.printStackTrace();
        }
        
         socket.close();
    }
}
