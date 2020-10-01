import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Random;

public class Client
{
	private static final int PORTServer = 9876;
	private DatagramSocket clientSocket = null;
	private byte[] sendData = null;
	private byte[] receiveData = null;
	private int size = 200;
	private DatagramPacket sendPacket = null;
	private DatagramPacket receivePacket = null;
	private InetAddress host = null;
	
	private String myName = "client";
	private String receivedMsg = "";
	private int me = 0;				// me or opponent
	private int opponent= 0;		// will be set to 1
	private int delay = 100;		// never set it to 0
	private Game game = null;
	private boolean Found=false;
	private int fIter=0;
	
	public Client()
	{
		// initialization of the fields
		try
		{
			clientSocket = new DatagramSocket();
			
			sendData = new byte[size];
			receiveData = new byte[size];
			
			host = InetAddress.getLocalHost();
			
			receivePacket = new DatagramPacket(receiveData, receiveData.length);
			sendPacket = new DatagramPacket(sendData, sendData.length, host, PORTServer);
		}
		catch(SocketException | UnknownHostException e)
		{
			// print the occured exception
			System.out.println(e.getClass().getName() + " : " + e.getMessage());
		}
		
		// add a random number from 0 to 19 at the end of the name
		Random rng = new Random();
		int rand = rng.nextInt(20);
		myName += rand;
		
		game = new Game();		
	}
	
	private void introduce()
	{
		try
		{
			// send my name
			sendData = myName.getBytes("UTF-8");
			sendPacket.setData(sendData);
			sendPacket.setLength(sendData.length);
			clientSocket.send(sendPacket);
			
			// receive my player id (1 or 2) and PORT that I will be talking to
			clientSocket.receive(receivePacket);
			
			// reconfiguration
			sendPacket.setAddress(receivePacket.getAddress());
			sendPacket.setPort(receivePacket.getPort());
			
			String s1 = new String(receivePacket.getData(), 0, receivePacket.getLength(), "UTF-8");
			
			if(!Character.toString(s1.charAt(0)).equals("P"))
				System.out.println("The first character should be P");
			
			if(Integer.parseInt(Character.toString(s1.charAt(1))) == 1)		// I am player 1
			{
				me = 0;
				opponent = 1;
			}
			else	// I am player 2
			{
				me = 1;
				opponent = 0;
			}
			
			game.setPlayers(me, opponent);
			
			// ensure that the right PORT has been received
			System.out.println("PORT that I am sending to : " + s1.substring(2));
		}
		catch(IOException e)
		{
			// print the occured exception
			System.out.println(e.getClass().getName() + " : " + e.getMessage());
		}
	}
	
	private void receiveMessages()
	{
		// keep on receiving messages and act according to their content
		while(true)
		{
			try
			{
				// waiting for a message from the server
				clientSocket.receive(receivePacket);
				
				// get the content
				receivedMsg = new String(receivePacket.getData(), 0, receivePacket.getLength(), "UTF-8");
				
				System.out.println(game.getCiter() + ". Message from server : " + receivedMsg);
				
				// get the first letter of the String - it can be "B", "T" or "E"
				String firstLetter = Character.toString(receivedMsg.charAt(0));
				
				if(firstLetter.equals("B"))		// the game has begun
				{
					String state = Character.toString(receivedMsg.charAt(1));
					game.setState(state);
					
					int iters = Integer.parseInt(receivedMsg.substring(2));
					game.setIters(iters);
					
					this.doWait();
					
					String action = game.selectAction();
					
					game.incrementIter();
					
					sendData = action.getBytes("UTF-8");
					sendPacket.setData(sendData);
					sendPacket.setLength(sendData.length);
					clientSocket.send(sendPacket);			
				}				
				else if(firstLetter.equals("T"))	// an iteration has taken place
				{
					String s1 = receivedMsg.substring(1);
					
					String fields[] = s1.split(",");
					
					int myOutcome = Integer.parseInt(fields[0]);
					String oppAction = fields[1];
					String state = fields[2];
/*
 * 
 * MY IMPLEMENTATION STARTS HERE
 * 
 */
					
					game.update(myOutcome, oppAction, state);
					game.makeAverageOutcomes();
					int flag=0;
					
					/*if((game.getCiter()>=30)&&(game.getCiter()<100)){
					if(Found);
					else
					Found=game.match();
					}*/
						
						
					
					
					if(((game.GetTimesOfPlayedA()>7)&&(game.GetTimesOfPlayedB()>7))&&(game.GetTimesOfPlayedC()>7)){
					//System.out.println("mpike");
						if(flag==0){
							fIter=game.getCiter();
						}
					if(Found==true){
						game.setFound(Found);
					//	game.GetAvgOutcomes();
						
					}
					else{
						Found=game.match();
						
					}
					}
					
					/*if((game.getCiter()==fIter+50)&&Found){
						Found=game.match();	
						game.GetAvgOutcomes();
						//fIter=game.getCiter();

					}*/

					
					this.doWait();
					if(game.getCiter()==30){
						game.GetAvgOutcomes();

					}
					
					if((game.getCiter()==450)){
						game.GetAvgOutcomes();

					}
					
					String action = game.selectAction();
					game.incrementIter();
					
					sendData = action.getBytes("UTF-8");
					sendPacket.setData(sendData);
					sendPacket.setLength(sendData.length);
					clientSocket.send(sendPacket);
				}
				else	// firstLetter.equals("E") - the game has ended
				{
					String s1 = receivedMsg.substring(1);
					
					String fields[] = s1.split(",");
					
					int[] finalScore = {Integer.parseInt(fields[0]), Integer.parseInt(fields[1])};
					
					if(finalScore[me] > finalScore[opponent])
						s1 = "I won : ";
					else if(finalScore[me] < finalScore[opponent])
						s1 = "I lost : ";
					else
						s1 = "Draw : ";
					
					System.out.println(s1 + finalScore[0] + " - " + finalScore[1]);
					game.GetStates();
					game.GetNum();
					game.GetAvgOutcomes();
					break;					
				}
			}
			catch(IOException e)
			{
				System.out.println(e.getClass().getName() + " : " + e.getMessage());
			}		
		}
	}
	
	private void doWait()
	{
		try
		{
			synchronized(this)
			{
				this.wait(delay);
			}
		}
		catch(InterruptedException e)
		{
			System.out.println(e.getClass().getName() + " : " + e.getMessage());
		}
	}

	public static void main(String[] args)
	{
		Client client = new Client();
		
		// optionally adding delay to response
		if(args.length == 1)
			client.delay = Integer.parseInt(args[0]);
		
		// send and receive the first messages
		client.introduce();
		
		// start receiving messages;
		client.receiveMessages();
	}

}
