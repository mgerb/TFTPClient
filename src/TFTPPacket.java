import java.io.File;
import java.io.FileInputStream;
import java.net.DatagramPacket;
import java.nio.ByteBuffer;


public class TFTPPacket {
	
	//method to take a file name input and mode and convert it to a tftp packet
	//this packet will then go inside a datagram packet
	//this method is for the readrequest option within tftp
	//converts the inputs to a byte array with 1 in the opcode
	public static byte[] ReadRequest(String filename, String mode)
	{
		byte[] fb = filename.getBytes();
		byte[] mb = mode.getBytes();
		
		//create new byte array - this will be the array that gets returned
		//create array with length of filename + mode + 4 bytes (for the opcode and 0 bytes)
		byte[] message = new byte[(fb.length) + (mb.length) + 4];
		
		//set the opcode
		message[0] = 0;
		message[1] = 1;
		
		//copies contents of the filename to the message starting at postion 3 of the byte array
		System.arraycopy(fb, 0, message, 2, fb.length);
		
		//adds a zero bit
		message[fb.length + 2] = 0;
		
		//copies contents of mode
		System.arraycopy(mb, 0, message, fb.length+3, mb.length);
		
		//adds final 0 bit to end
		message[fb.length + mb.length + 3] = 0;
		
		return message;
	}
	
	//write method that converts inputs to a tftp packet with 2 in the opcode
	public static byte[] WriteRequest(String filename, String mode)
	{
		byte[] fb = filename.getBytes();
		byte[] mb = mode.getBytes();
		
		byte[] message = new byte[(fb.length) + (mb.length) + 4];
		
		message[0] = 0;
		message[1] = 2;
		
		System.arraycopy(fb, 0, message, 2, fb.length);
		
		message[fb.length + 2] = 0;
		
		System.arraycopy(mb, 0, message, fb.length+3, mb.length);
		
		message[fb.length + mb.length + 3] = 0;
		
		return message;	
	}

	//error method that converts inputs to a tftp packet with 5 in the opcode
	public static byte[] errorMessage(String input, int errorCode)
	{
		byte[] inputMessage = input.getBytes();
		
		byte[] message = new byte[inputMessage.length + 5];
		
		message[1] = 5;
		
		message[3] = (byte) errorCode;
		
		System.arraycopy(inputMessage, 0, message, 4, inputMessage.length);
		
		message[inputMessage.length + 4] = 0;
		
		return message;
	}
	
	
	//method to create the ack TFTP packet
	//takes in block number and adds to packet
	public static byte[] ackSend(short block)
	{
		byte[] ack = new byte[4];
		
		ByteBuffer buffer = ByteBuffer.allocate(2);
		buffer.putShort(block);
		buffer.flip();
		byte[] newB = buffer.array();
		
		
		ack[0] = 0;
		ack[1] = 4;
		ack[2] = newB[0];
		ack[3] = newB[1];
		
		return ack;
	}
	
	//method to convert a file to a byte array
	public static byte[] convertToBytes(File file)
	{
		byte[] b = new byte[(int) file.length()];
		
		try
		{
			FileInputStream input = new FileInputStream(file);
			input.read(b);
			
		}
		
		catch(Exception e)
		{
			System.out.println("Error converting file to bytes");
		}
		
		return b;
	}

	
	public static int parseIncMessage(byte[] packet)
	{
		System.out.println("Converting Packet");
		byte[] incMessage = packet;
		
		int i = 4;
		
		while (i <= 515)
		{
			if(incMessage[i] == 0)
			{
				i++;
				break;
			}
			i++;
		}
		System.out.println(i);
		return i;
	}
}
