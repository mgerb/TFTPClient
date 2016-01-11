import java.awt.List;
import java.io.FileOutputStream;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;


public class TFTPRequest {

	private DatagramPacket packet, incPacket, ackPacket;
	private DatagramSocket socket;
	
	
	// constructor to create a new Datagram Socket
	public TFTPRequest()
	{
		try
		{
			socket = new DatagramSocket();
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
			
		}	
	}
	

	
	public void readFile(String fileName, String mode, InetAddress address, int port)
	{
		
		
		//Create read request TFTP packet
		byte[] message = TFTPPacket.ReadRequest(fileName, mode);
		
		//declare an array list to store incoming data
		ArrayList<Byte> array = new ArrayList<Byte>();
		
		//temp byte for incoming packet
		byte[] temp = new byte[516];
		
		//create incoming and outgoing packets
		packet = new DatagramPacket(message, message.length, address, port);
		
		
		//set block to 1 to start
		short block = 1;
		
		//declare ackMessage - 4 bytes
		byte[] ackMessage = new byte[4];
		
		try
		{
			//send request packet
			socket.send(packet);
			
			System.out.println("Request Packet Sent");
			
			
			boolean x = true;
			
			//loop to receive packets - will terminate when packet received is less than 516 bytes
				while(x==true)
				{
					
				//receive packets on socket
				
				incPacket = new DatagramPacket(temp, temp.length);
				
				
				socket.receive(incPacket);
				System.out.println("Packet Received - Length: " + incPacket.getLength());
				
				
				
				//outputs the incoming bytes
					
					byte[] incomingByte = new byte[incPacket.getLength()];
					byte[] tempByte = incPacket.getData();
					
					
					
					for(int i = 0; i < incPacket.getLength(); i++)
					{
						incomingByte[i] = tempByte[i];
						System.out.print(incomingByte[i] + " ");
					}
					
					System.out.println();
				
					ByteBuffer bb = ByteBuffer.allocate(2);
					bb.order(ByteOrder.LITTLE_ENDIAN);
					bb.put(incomingByte[3]);
					bb.put(incomingByte[2]);
					block = bb.getShort(0);
					
				//takes data from incoming packet and adds it to the array list
				//starts at 4th byte to remove packet header contents
					
				
					for(int i = 4; i < incomingByte.length; i++)
					{
						array.add(incomingByte[i]);
					}
					
				//will print an error if an error packet is received
					if(incomingByte[1] ==4)
						System.out.println("Error Packet Received");
				
					
					//creates TFTP packet ack message - creates new datagram packet and inserts TFTP message
					ackMessage = TFTPPacket.ackSend(block);
					ackPacket = new DatagramPacket(ackMessage, ackMessage.length, address, incPacket.getPort());
					
					
					//send outgoing ack message to server
					socket.send(ackPacket);
					System.out.println("Ack Sent");
				
				
				//outputs ack message sent
				
				System.out.print(ackMessage[0] + " " + ackMessage[1] + " 0 ");
				System.out.print(block);
				System.out.println();
				System.out.println();
				
				
				
				//if the block received is less than 516 bytes client will realize it is last packet and terminate the loop
				if(incomingByte.length < 516)
				{
					x = false;
					socket.close();
				}
				}
			
			System.out.println("Loop Terminated");
			
			//converts array list to a byte array of fixed size
			byte[] finalArray = new byte[array.size()];
			
			for(int i = 0; i < array.size(); i++)
			{
				finalArray[i] = array.get(i);
			}
			
			
			//outputs received file
			FileOutputStream fos = new FileOutputStream("c:/" + fileName) ;
			fos.write(finalArray);
			fos.close();
			System.out.println("File output");
			
			
		}
		
		
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println();
			
		}
	}
	
	
	
	
}
