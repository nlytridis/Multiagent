import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Client
{
	// game-related fields
	private int id = 0;
	private int N = 0;
	private int mainType = 0;
	private int subType = 0;
	private String name = "";
	private int[][] typeMatrix = null;
	private int iters = 0;
	private int[] types0 = null;		// agents of type 0
	private int[] types1 = null;		// agents of type 1
	private boolean proposer = false;	// I am a proposer
	private int taskValue = 0;			// task value
	private int nType0 = 0;				// agents needed of type0
	private int nType1 = 0;				// agents needed of type1
	private int taskType = -1;			// the type of the task (0,1 or 2)
	private int taskId = -1;			// id of the task
	private int[] proposed = null;		// the two agents that I have proposed to
	private int[] proposedShares = null;	// the values that I have proposed (multiples of 5)
	private ArrayList<Integer> proposersList = null;	// list with the proposers (excluding myself)
	private double score = 0.0;
	private Random rng = null;
	private int stepReward = 5;
	private ArrayList<int[]> receivedProposals = null;	// having the role of a proposed member
	private ArrayList<Integer> memberIndex = null;		// the index (0 or 1) of my membership of a proposal
	private int[] acceptedProposal = null;
	private int memberOfAccProp = 0;					// member (0 or 1) of the proposal I have accepted
	private static int [] beliefs;
	private static int avgBelief;
	
	// connection-related fields
	private static final int PORT = 9876;
	private DatagramSocket socket = null;
	private byte[] receiveData = null;
	private byte[] sendData = null;
	private int size = 200;
	private DatagramPacket receivePacket = null;
	private DatagramPacket sendPacket = null;
	private InetAddress host = null;
	
	public Client()
	{
		name = "N.L.P.S";	// replace with your name
		
		try
		{
			// configure connection related fields
			socket = new DatagramSocket();
			receiveData = new byte[size];
			sendData = new byte[size];
			host = InetAddress.getLocalHost();
			receivePacket = new DatagramPacket(receiveData, receiveData.length);
			sendPacket = new DatagramPacket(sendData, sendData.length, host, PORT);
			
		}
		catch(IOException e)
		{
			// print the occured exception
			System.out.println(e.getClass().getName() + " : " + e.getMessage());
		}
	}

	private void introduce()
	{
		try
		{
			// send my name
			sendData = name.getBytes("UTF-8");
			sendPacket.setData(sendData);
			sendPacket.setLength(sendData.length);
			socket.send(sendPacket);
			
			// wait to receive the first message from the server
			// Form of the message : P,PORTS[i],iters,N,id,Main,Sub
			socket.receive(receivePacket);
			
			// reconfigure sendPacket
			sendPacket.setAddress(receivePacket.getAddress());	// just in case
			sendPacket.setPort(receivePacket.getPort());
			
			// process the message
			String msg = new String(receivePacket.getData(), 0, receivePacket.getLength(), "UTF-8");
			System.out.println("Message from server : " + msg);			
			
			String fields[] = msg.split(",");
			
			// the first character of this message must be "P"
			if(!fields[0].equals("P"))
			{
				System.out.println("The first character of the first received message is not "
						+ " \"P\"! Exiting now...");
			}
			
			// fields[1] == new PORT, already handled
			
			iters = Integer.parseInt(fields[2]);
			N = Integer.parseInt(fields[3]);
			id = Integer.parseInt(fields[4]);
			mainType = Integer.parseInt(fields[5]);
			subType = Integer.parseInt(fields[6]);
			
			typeMatrix = new int[N][2];//first column represents main type
									   //the other three comlumn represents subtype belief 1/0 if sure/unsure 
			types0 = new int[N/2];
			types1 = new int[N/2];
			proposed = new int[N/6];
			proposedShares = new int[N/6];
			proposersList = new ArrayList<Integer>();
			rng = new Random();
			receivedProposals = new ArrayList<int[]>();
			memberIndex = new ArrayList<Integer>();
			beliefs=new int [12];
			avgBelief=0;
			for(int i=0;i<beliefs.length;i++)
				beliefs[i]=0;		//In start we believe all the agents are good ti=0
			
			System.out.println("My id is " + id + ", my types (main and sub) are " + mainType + " and " + subType);
		}
		catch(IOException e)
		{
			// print the occured exception
			System.out.println(e.getClass().getName() + " : " + e.getMessage());
		}
	}
	
	private void receiveTypes()
	{
		try
		{
			// wait to receive the second message from the server
			// Form of the message : M,main1,main2,...,mainN
			socket.receive(receivePacket);
			
			String msg = new String(receivePacket.getData(), 0, receivePacket.getLength(), "UTF-8");
			System.out.println("Message from server : " + msg);	
			
			String[] fields = msg.split(",");
			
			if(!fields[0].equals("M"))
			{
				System.out.println("The first character of the second received message is not "
						+ " \"M\"! Exiting now...");
			}
			
			String[] s1 = new String[N];
			
			// dismiss the first character, "M"
			for(int i=0; i<N; i++)
				s1[i] = fields[i+1];
			
			int t0Index = -1;
			int t1Index = -1;
			
			for(int i=0; i<N; i++)
			{
				if(i != id)		// don't count yourself
				{
					typeMatrix[i][0] = Integer.parseInt(s1[i]);
					
					if(typeMatrix[i][0] == 0)
					{
						t0Index++;
						types0[t0Index] = i;
					}
					else
					{
						t1Index++;
						types1[t1Index] = i;
					}
				}
			}
			
			// setting the right values of types0 and types1
			// by excluding myself from their size,
			// that is one has to be of size 5 and one of 6
			
			int[] types0Temp = new int[t0Index + 1];
			for(int i=0; i<types0Temp.length; i++)
				types0Temp[i] = types0[i];
			
			types0 = new int[t0Index + 1];	// assuring consistency
			types0 = types0Temp;
			
			int[] types1Temp = new int[t1Index + 1];
			for(int i=0; i<types1Temp.length; i++)
				types1Temp[i] = types1[i];
			
			types1 = new int[t1Index + 1];	// assuring consistency
			types1 = types1Temp;

			for(int i=0; i<N; i++)
			{
				
			}
			
		}
		catch(IOException e)
		{
			// print the occurred exception
			System.out.println(e.getClass().getName() + " : " + e.getMessage());
		}
	}
	
	private void loop()
	{
		for(int iter=0; iter<iters; iter++)
		{
			// new tasks will be sent
			this.reset();
			for(int i=0;i<beliefs.length;i++){
				avgBelief+=beliefs[i];
			}
			avgBelief=avgBelief/11;
			try
			{
				// wait to receive the tasks from the server
				// Form of the message : "T#proposer0,value0,type0_0,type0_1,taskType_0,taskId0#...(tasks 1 and 2)...
				//					       #proposer3,value3,type3_0,type3_1,taskType_3,taskId3"
				socket.receive(receivePacket);
				
				String msg = new String(receivePacket.getData(), 0, receivePacket.getLength(), "UTF-8");
				System.out.println("Message from server : " + msg);
				
				String[] tasks = msg.split("#");	// tasks[0] == "T"
				
				if(!tasks[0].equals("T"))
				{
					System.out.println("This message should start with T!\nExiting now...");
					return;
				}
				
				this.handleTasks(tasks);
				
				// If I am a proposer I have to send a proposal
				if(proposer)	
					this.makeProposal();
				
				// wait to receive the proposals that are related to me
				// Form of the message : "C#id0,share0,id1,share1,proposer,value,taskId#...(rest of the proposals)"
				// If I am a proposer or I haven't been proposed for any task then I will just receive "C"
				socket.receive(receivePacket);
				
				msg = new String(receivePacket.getData(), 0, receivePacket.getLength(), "UTF-8");
				System.out.println("Message from server : " + msg);
				
				boolean proposed = this.proposalRespond(msg);
				
				if(!proposed)
					if(!proposer)
						continue;	// I am neither a proposer or a proposed member in this round
				
				// I am a proposer or a proposed member so I have to wait to get the coalition formation message
				socket.receive(receivePacket);
				
				msg = new String(receivePacket.getData(), 0, receivePacket.getLength(), "UTF-8");
				System.out.println("Message from server : " + msg);
				
				this.handleOutcome(msg);
				
				
				
				
			}
			catch(IOException e)
			{
				// print the occurred exception
				System.out.println(e.getClass().getName() + " : " + e.getMessage());
			}
				
		}	
	}
	
	private void handleTasks(String tasks[])
	{
		// tasks[0] == "T"
		
		for(int i=1; i<tasks.length; i++)
		{
			String[] task = tasks[i].split(",");
			
			int tProposer = Integer.parseInt(task[0]);	// task i-1 proposer
			
			if(tProposer == id)		// I am the proposer
			{
				proposer = true;
				taskValue = Integer.parseInt(task[1]);
				nType0 = Integer.parseInt(task[2]);
				nType1 = Integer.parseInt(task[3]);
				taskType = Integer.parseInt(task[4]);
				taskId = Integer.parseInt(task[5]);
			}
			else
			{
				proposersList.add(tProposer);
				// there is more exploitable information
			}
		}
	}
	
	private void makeProposal()
	{
		// Proposals strategy of Dummy agent :
		// 1. Propose random members, from those that cover the task demands
		// 2. Propose a share of the task value in {10,15,20,...,40} randomly
		
		
		// exclude proposers from possible potential member
		ArrayList<Integer> types0List = new ArrayList<Integer>();
		ArrayList<Integer> types1List = new ArrayList<Integer>();
		
		for(int i=0; i<types0.length; i++)
		{
			boolean toJoin = true;
			
			for(int j=0; j<proposersList.size(); j++)
				if(types0[i] == proposersList.get(j))
					toJoin = false;
			
			if(toJoin)
				types0List.add(types0[i]);
		}
		
		for(int i=0; i<types1.length; i++)
		{
			boolean toJoin = true;
			
			for(int j=0; j<proposersList.size(); j++)
				if(types1[i] == proposersList.get(j))
					toJoin = false;
			
			if(toJoin)
				types1List.add(types1[i]);
		}
		
		
		if(taskType == 0 || taskType == 1)
		{
			// taskType = 0 or 1, so I need to find two agents 
			// of different type to propose to
			
			// choose an agent of type 0
			int rand = rng.nextInt(types0List.size());
			
			
			int temp=beliefs[types0List.get(0)],max=-1;
			for(int i=0;i<types0List.size();i++){
				if(temp<=beliefs[types0List.get(i)]){
					temp=beliefs[types0List.get(i)];
					max=i;
					}
			}
			if(iters<100){
				proposed[0] = types0List.get(rand);
				types0List.remove(rand);
			}
			else{
			proposed[0] = types0List.get(max);
			types0List.remove(max);
			}
			// define the share of proposed[0]
			// value is from [10,40], with step of 5
			rand = rng.nextInt(2) + 5;		// generate a value in [0,6] and add 2
			if(subType==2)
			proposedShares[0] = 7 * stepReward;	// stepReward = 5
			else
			proposedShares[0] = 5 * stepReward;
			// choose an agent of type 1
			rand = rng.nextInt(types1List.size());
			temp=beliefs[types1List.get(0)];max=-1;
			for(int i=0;i<types1List.size();i++){
				if(temp<=beliefs[types1List.get(i)]){
					temp=beliefs[types1List.get(i)];
					max=i;
					}
			}
			if(iters<100){
				proposed[1] = types1List.get(rand);
				types1List.remove(rand);
			}
			else{
			proposed[1] = types1List.get(max);
			types1List.remove(max);
			}
			
			// define the share of proposed[1]
			rand = rng.nextInt(2) + 5;		// generate a value in [0,6] and add 2
			if(subType==2)
			proposedShares[1] = 7 * stepReward;	// stepReward = 5
			else
			proposedShares[1] = 5 * stepReward;
		}
		else	// task is of type 2
		{
			// the first potential member can be of any type
			int randType = rng.nextInt(2);
			int temp=-1,max=-1;
			int rand = 0;
			
			if(randType == 0)
			{	temp=beliefs[types0List.get(0)];
				//rand = rng.nextInt(types0List.size());
				for(int i=0;i<types0List.size();i++){
					if(temp<=beliefs[types0List.get(i)]){
						temp=beliefs[types0List.get(i)];
						max=i;
						}
				}
				if(iters<100){
					proposed[0] = types0List.get(rand);
					types0List.remove(rand);
				}
				else{
				proposed[0] = types0List.get(max);
				types0List.remove(max);
				}
			}
			else
			{	temp=beliefs[types1List.get(0)];
				//rand = rng.nextInt(types1List.size());
				for(int i=0;i<types1List.size();i++){
					if(temp<=beliefs[types1List.get(i)]){
						temp=beliefs[types1List.get(i)];
						max=i;
					}
			}
				if(iters<100){
					proposed[0] = types1List.get(rand);
					types1List.remove(rand);
				}
				else{
				proposed[0] = types1List.get(max);
				types1List.remove(max);
				}
			}
			
			// define the share of proposed[0]
			rand = rng.nextInt(2) + 5;		// generate a value in [0,1] and add 5
			if(subType==2)
			proposedShares[0] = 7 * stepReward;	// stepReward = 5
			else
			proposedShares[0] = 5 * stepReward;
			
			// there can't be 3 members of the same type
			if(mainType == randType)
			{
				if(randType == 0)
				{	temp=beliefs[types1List.get(0)];max=-1;
					// choose an agent of type 1
					//rand = rng.nextInt(types1List.size());
					for(int i=0;i<types1List.size();i++){
						if(temp<=beliefs[types1List.get(i)]){
							temp=beliefs[types1List.get(i)];
							max=i;
						}
					}
					if(iters<100){
						proposed[1] = types1List.get(rand);
						types1List.remove(rand);
					}
					else{
					proposed[1] = types1List.get(max);
					types1List.remove(max);
					}
				}
				else
				{
					temp=beliefs[types0List.get(0)];max=-1;
					// choose an agent of type 0
					//rand = rng.nextInt(types0List.size());
					for(int i=0;i<types0List.size();i++){
						if(temp<=beliefs[types0List.get(i)]){
							temp=beliefs[types0List.get(i)];
							max=i;
						}
					}
					if(iters<100){
						proposed[1] = types0List.get(rand);
						types0List.remove(rand);
					}
					else{
					proposed[1] = types0List.get(max);
					types0List.remove(max);
					}
				}
			}
			else
			{
				// choose any type
				rand = rng.nextInt(2);
				
				if(rand == 0)
				{	temp=beliefs[types0List.get(0)];max=-1;
					// choose an agent of type 0
					//rand = rng.nextInt(types0List.size());
					for(int i=0;i<types0List.size();i++){
						if(temp<=beliefs[types0List.get(i)]){
							temp=beliefs[types0List.get(i)];
							max=i;
						}
					}
					if(iters<100){
						proposed[1] = types0List.get(rand);
						types0List.remove(rand);
					}
					else{
					proposed[1] = types0List.get(max);
					types0List.remove(max);
					}
				}
				else
				{	temp=beliefs[types1List.get(0)];max=-1;
					// choose an agent of type 1
					//rand = rng.nextInt(types1List.size());
					for(int i=0;i<types1List.size();i++){
						if(temp<=beliefs[types1List.get(i)]){
							temp=beliefs[types1List.get(i)];
							max=i;
						}
					}
					if(iters<100){
						proposed[1] = types1List.get(rand);
						types1List.remove(rand);
					}
					else{
					proposed[1] = types1List.get(max);
					types1List.remove(max);
					}
				}
			}
			
			// define the share of proposed[1]
			rand = rng.nextInt(2) + 5;		// generate a value in [0,6] and add 2
			if(subType==2)
			proposedShares[1] = 7 * stepReward;	// stepReward = 5
			else
			proposedShares[1] = 5 * stepReward;
		}
		
		String proposal = proposed[0] + ",";
		proposal += proposedShares[0] + ",";
		proposal += proposed[1] + ",";
		proposal += proposedShares[1];
		
		System.out.println("My proposal is " + proposal);
		System.out.println("Task type : " + taskType + ", my type : " + mainType + ", agent's "
				+ proposed[0] + " type : " + typeMatrix[proposed[0]][0] + ", agent's "
				+ proposed[1] + " type : " + typeMatrix[proposed[1]][0]);
		
		try
		{
			// send my proposal
			sendData = proposal.getBytes("UTF-8");
			sendPacket.setData(sendData);
			sendPacket.setLength(sendData.length);
			socket.send(sendPacket);
		}
		catch(IOException e)
		{
			// print the occurred exception
			System.out.println(e.getClass().getName() + " : " + e.getMessage());
		}
	}
	
	private boolean proposalRespond(String msg)
	{
		// If am a proposer I already know the content of the message ("C")
		if(proposer)
			return false;
		
		String[] proposals = msg.split("#");
		
		// proposals[0] == "C"
		if(!proposals[0].equals("C"))
			System.out.println("The message should start with C.");
		
		// check if I have not been proposed for any coalition
		if(proposals.length == 1)
		{
			System.out.println("I have not been been proposed for any coalition.");
			return false;
		}
		
		// process all of the proposals
		for(int i=1; i<proposals.length; i++)
		{
			String[] proposal = proposals[i].split(",");
			
			int member1 = Integer.parseInt(proposal[0]);
			int perc1 = Integer.parseInt(proposal[1]);
			int member2 = Integer.parseInt(proposal[2]);
			int perc2 = Integer.parseInt(proposal[3]);
			int proposer = Integer.parseInt(proposal[4]);
			int value = Integer.parseInt(proposal[5]);
			int taskId = Integer.parseInt(proposal[6]);
			
			int[] tempVec = {member1,perc1,member2,perc2,proposer,value,taskId};
			receivedProposals.add(tempVec);
			
			if(id == member1)
				memberIndex.add(0);
			else
				memberIndex.add(1);
		}

		int response = this.chooseProposal();
		
		if(response != -1)
		{
			for(int i=0; i<receivedProposals.size(); i++)
				if(receivedProposals.get(i)[6] == response)
				{
					acceptedProposal = new int[receivedProposals.size()];
					acceptedProposal = receivedProposals.get(i);
						
					System.out.println("I have accepted task " + acceptedProposal[6]);
					memberOfAccProp = memberIndex.get(i);
				}
		}
		else
		{
			System.out.println("I didn't accept any of the tasks");
		}
		
		System.out.println(response);
		//System.out.println("the approves proposal is a[0]="+acceptedProposal[0]+" ,a[1]= "+ acceptedProposal[1]+ "a[2]="+acceptedProposal[2]+"a[3]=" +acceptedProposal[3]+" a[4]="+ acceptedProposal[4]+ " a[5]="+acceptedProposal[5]);
		// send my response
		try
		{
			sendData = Integer.toString(response).getBytes("UTF-8");
			sendPacket.setData(sendData);
			sendPacket.setLength(sendData.length);
			socket.send(sendPacket);
		}
		catch(IOException e)
		{
			// print the occurred exception
			System.out.println(e.getClass().getName() + " : " + e.getMessage());
		}
		
		return true;
	}
	
	private int chooseProposal()
	{
		// Response strategy of N.L.P.S agent :
		// 1. Find the task that maximizes the expected value w.r.t my beliefs maxΣΒi(t)Pr(o/t)R(o)
		// 2. If myPerc < 20 then P_acc = 50% ese accept
		// where P_acc stands for the probability of acceptance
		
		double[] expectedVal = new double[receivedProposals.size()];
		double max = -1;
		int chosenProposal = -1;
		int taskVal = 0;
		int myPerc = 0;
		int chosenPerc = 0;
		int otherAgent=-1;
		int proposer=-1;
		double Pr=0, subt1=0, subt2=0, subP=0;
		
		
		
		
		for(int i=0; i<receivedProposals.size(); i++)
		{
			if(memberIndex.get(i) == 0){
				myPerc = receivedProposals.get(i)[1]; 
				if(subType==0)
					subt1=2;
				else if(subType==1)
					subt1=1.5;
				else
					subt1=1;
				otherAgent=receivedProposals.get(i)[2];
				if(beliefs[otherAgent]>=avgBelief+2)
					subt2=2;
				else if(beliefs[otherAgent]>=avgBelief-2)
					subt2=1.5;
				else
					subt2=1;
			}
			else{
				myPerc = receivedProposals.get(i)[3];
				if(subType==0)
					subt2=2;
				else if(subType==1)
					subt2=1.5;
				else
					subt2=1;
				otherAgent=receivedProposals.get(i)[0];
				if(beliefs[otherAgent]>=avgBelief+2)
					subt1=2;
				else if(beliefs[otherAgent]>=avgBelief-2)
					subt1=1.5;
				else
					subt1=1;
				}
			proposer= receivedProposals.get(i)[4];
			taskVal = receivedProposals.get(i)[5];
		
			if(beliefs[proposer]>=avgBelief+2)
				subP=2;
			else if(beliefs[proposer]>=avgBelief-2)
				subP=1.5;
			else
				subP=1;
			Pr=(subP+subt1+subt2)/7;
			expectedVal[i] = ((double)taskVal * ((double)myPerc / 100.0))*Pr;			
			if(expectedVal[i] > max)
			{
				max = expectedVal[i];
				chosenProposal = receivedProposals.get(i)[6];
				chosenPerc = myPerc;
			}
		}
			
		double rand = rng.nextDouble();
		
		if(chosenPerc <= 20)
		{
			if(rand < 0.5)
			{
				return chosenProposal;
			}
		}
		else{
			return chosenProposal;
		}
		return -1; // I do not accept any proposal
		// dummy's acceptance strategy
		/*
		if(chosenPerc >= 30)
		{
			return chosenProposal;
		}
		else if(chosenPerc == 25)
		{
			if(rand < 0.8)
			{
				return chosenProposal;
			}
		}
		else if(chosenPerc == 20)
		{
			if(rand < 0.75)
			{
				return chosenProposal;
			}
		}
		else if(chosenPerc == 15)
		{
			if(rand < 0.6)
			{
				return chosenProposal;
			}
		}
		else if(chosenPerc == 10)
		{
			if(rand < 0.5)
			{
				return chosenProposal;
			}
		}
		else if(chosenPerc == 5)
		{
			if(rand < 0.25)
			{
				return chosenProposal;
			}
		}
		else
		{
			System.out.println("My share of proposal " + chosenProposal +" is " + chosenPerc + " (not valid).");
		}
		
		*/
		
	}
	
	private void handleOutcome(String msg)
	{
		String[] fields = msg.split(",");
		
		// first letter must "F"
		if(!fields[0].equals("F"))
		{
			System.out.println("The first letter should be F, but it is " + fields[0] + ".");
			return;
		}
		
		if(fields[1].equals("-1"))		// the coalition has not been formed
		{
			if(proposer)
			{
				if(fields.length == 4)
				{
					System.out.println("Both of the proposed members did not accept.");		
					// process information about the other members
					if((subType==0)||(subType==1)){
						//beliefs[proposed[0]]--;
						//beliefs[proposed[1]]--;
					}
				}
				else
				{
					System.out.println("One of the other agents did not accept.");
					// fields[2] is the agent that did not accept
					// process information about the other members
					if((subType==0)||(subType==1)){
						//beliefs[proposed[0]]--;
						//beliefs[proposed[1]]--;
					}

				}
			}
			else
			{
				if(acceptedProposal == null)
				{
					System.out.println("I did not want any of the proposals.");
				}
				else
				{
					System.out.println("The other proposed agent did not accept.");
					// process information about the other members
				}
			}
		}
		else if(fields[1].equals("0"))		// the coalition was formed but didn't succeed
		{
			System.out.println("The coalition was formed but it didn't succeed.");
			// process information about the other members
			if(proposer){
				
				beliefs[proposed[0]]--;
				beliefs[proposed[1]]--;
				
				/*if(subType==2){
					beliefs[proposed[0]]-=10;
					beliefs[proposed[1]]-=10;
				}
				else if(subType==1){
					beliefs[proposed[0]]-=5;
					beliefs[proposed[1]]-=5;
				}
				else{
					beliefs[proposed[0]]--;
					beliefs[proposed[1]]--;
				}*/
			}
			else{
				
				if(memberOfAccProp==0){
					beliefs[acceptedProposal[4]]--;
					beliefs[acceptedProposal[2]]--;
					}
				else{
					beliefs[acceptedProposal[4]]--;
					beliefs[acceptedProposal[0]]--;
			}
				/*
				if(subType==0){
					if(memberOfAccProp==0){
							beliefs[acceptedProposal[4]]-=10;
							beliefs[acceptedProposal[2]]-=10;
							}
					else{
						beliefs[acceptedProposal[4]]-=10;
						beliefs[acceptedProposal[0]]-=10;
					}
					}
				else if(subType==1){
					if(memberOfAccProp==0){
							beliefs[acceptedProposal[4]]-=5;
							beliefs[acceptedProposal[2]]-=5;
							}
					else{
						beliefs[acceptedProposal[4]]-=5;
						beliefs[acceptedProposal[0]]-=5;
					}
					}
				else{
					if(memberOfAccProp==0){
						beliefs[acceptedProposal[4]]--;
						beliefs[acceptedProposal[2]]--;
						}
					else{
						beliefs[acceptedProposal[4]]--;
						beliefs[acceptedProposal[0]]--;
				}
				}*/
			}
			}
		
		else if(fields[1].equals("1"))
		{
			if(!proposer)
			{
				System.out.println("The coalition has succeeded.");
				
				int myPerc = 0;
				
				if(memberOfAccProp == 0)
					myPerc = acceptedProposal[1];
				else
					myPerc = acceptedProposal[3];
				
				int myVal = acceptedProposal[5];
				
				score += (double)myVal * (double)myPerc / 100.0;
				
				// process information about the other members
				if(proposer){
					
					beliefs[proposed[0]]++;
					beliefs[proposed[1]]++;
					/*
					if(subType==2){
						beliefs[proposed[0]]+=10;
						beliefs[proposed[1]]+=10;
					}
					else if(subType==1){
						beliefs[proposed[0]]+=5;
						beliefs[proposed[1]]+=5;
					}
					else{
						beliefs[proposed[0]]++;
						beliefs[proposed[1]]++;
					}*/
				}
				else{
					
					if(memberOfAccProp==0){
						beliefs[acceptedProposal[4]]++;
						beliefs[acceptedProposal[2]]++;
						}
					else{
						beliefs[acceptedProposal[4]]++;
						beliefs[acceptedProposal[0]]++;
					}
					/*if(subType==2){
						if(memberOfAccProp==0){
								beliefs[acceptedProposal[4]]+=10;
								beliefs[acceptedProposal[2]]+=10;
								}
						else{
							beliefs[acceptedProposal[4]]+=10;
							beliefs[acceptedProposal[0]]+=10;
						}
						}
					else if(subType==1){
						if(memberOfAccProp==0){
							beliefs[acceptedProposal[4]]+=5;
							beliefs[acceptedProposal[2]]+=5;
							}
						else{
							beliefs[acceptedProposal[4]]+=5;
							beliefs[acceptedProposal[0]]+=5;
					}
					}
					else{
						if(memberOfAccProp==0){
							beliefs[acceptedProposal[4]]++;
							beliefs[acceptedProposal[2]]++;
							}
						else{
							beliefs[acceptedProposal[4]]++;
							beliefs[acceptedProposal[0]]++;
					}
					}*/
				
			}}
			else
			{
				System.out.println("The coalition that I proposed has succeeded.");
				
				int myPerc = 100 - proposedShares[0] - proposedShares[1];
				
				score += (double)taskValue * (double)myPerc / 100.0;
				// process information about the other members
				
				beliefs[proposed[0]]++;
				beliefs[proposed[1]]++;
				
				/*if(subType==2){
					beliefs[proposed[0]]+=10;
					beliefs[proposed[1]]+=10;
				}
				else if(subType==1){
					beliefs[proposed[0]]+=5;
					beliefs[proposed[1]]+=5;
				}
				else{
					beliefs[proposed[0]]++;
					beliefs[proposed[1]]++;
				}*/
			}	
		}
		
		System.out.println("My current score is " + score);
	}
	
	private void reset()
	{
		proposer = false;	
		taskValue = 0;			
		nType0 = 0;		
		nType1 = 0;			
		taskType = -1;		
		taskId = -1;
		proposed = new int[N/6];
		proposedShares = new int[N/6];
		proposersList = new ArrayList<Integer>();
		receivedProposals = new ArrayList<int[]>();
		memberIndex = new ArrayList<Integer>();
		acceptedProposal = null;
		memberOfAccProp = 0;
		avgBelief=0;
	}
	
	public static void main(String[] args)
	{
		Client client = new Client();
		
		client.introduce();
		client.receiveTypes();
		
		client.loop();
		for(int i=0;i<beliefs.length;i++)
		System.out.println("The belief vector is "+beliefs[i]+"  for agent "+i);
		System.out.println("average="+avgBelief);
	}
}
