import java.util.Random;


/**
 * @author mamakos
 *
 */
public class Game
{
	private int[][][][] payoff;
	
	private int[][] A= new int[2][2];
	private int[][] B= new int[2][2];
	private int[][] C= new int[2][2];

	
	private int me = 0;
	private int opponent = 0;
	private int cIter = 0;	  // current iteration
	private int iters = 0;
	private String oppPrevAction = "";	// the previous action of my opponent ("A" or "B")
	private int prevOutcome = 0;		// my outcome from the previous iteration
	private int myScore = 0;			// my total outcome
	private String state = "";			// can be "A", "B" or "C"
	private boolean A_Chicken=false,A_PD=false,A_Coord=false;
	private boolean B_Chicken=false,B_PD=false,B_Coord=false;
	private boolean C_Chicken=false,C_PD=false,C_Coord=false;
	private boolean Found=false,ExpA=false;


	
	
	private String myaction = "";		//can be A or B
	
	
	
	private float A_avg_a=0,A_avg_b=0,A_avg_c=0,A_avg_d=0;
	private float B_avg_a=0,B_avg_b=0,B_avg_c=0,B_avg_d=0;
	private float C_avg_a=0,C_avg_b=0,C_avg_c=0,C_avg_d=0;
	
	private float A_sum_a=0,A_sum_b=0,A_sum_c=0,A_sum_d=0;
	private float B_sum_a=0,B_sum_b=0,B_sum_c=0,B_sum_d=0;
	private float C_sum_a=0,C_sum_b=0,C_sum_c=0,C_sum_d=0;

	
	private int nA1=0,nB1=0,nC1=0;
	private int nA2=0,nB2=0,nC2=0;
	private int nA3=0,nB3=0,nC3=0;
	private int nA4=0,nB4=0,nC4=0;
	
	public Game()
	{
		payoff = new int[3][2][2][2]; 	// [states][actionsP1][actionsP2][players]
	}
	
	public void setPlayers(int me, int opponent)
	{
		this.me = me;
		this.opponent = opponent;
	}
	
	public void setIters(int iters)
	{
		this.iters = iters;
	}
	
	public void setFound(boolean Found)
	{
		this.Found = Found;
	}
	
	public void makeAverageOutcomes(){
		switch (state){//check in which state u r and calculate average outcome
		case "A"://nA* is a counter which we need to calculate averages		
			if(myaction.equals("A")){
				if(oppPrevAction.equals("A")){//case AA
					//System.out.println("MPIKE");
					A_avg_a=(A_avg_a*nA1+prevOutcome)/(nA1+1);	
					A_sum_a=A_sum_a+prevOutcome;
					nA1++;
					break;	

				}
				else{					//AB
					A_avg_b=(A_avg_b*nA2+prevOutcome)/(nA2+1);	
					A_sum_b=A_sum_b+prevOutcome;
					nA2++;
					break;	
					}
				}
			else{
				if(oppPrevAction.equals("A")){//BA
					//System.out.println("MPIKE");
					A_avg_c=(A_avg_c*nA3+prevOutcome)/(nA3+1);	
					A_sum_c=A_sum_c+prevOutcome;
					nA3++;
					break;	

				}
				else{						//BB
					A_avg_d=(A_avg_d*nA4+prevOutcome)/(nA4+1);	
					A_sum_d=A_sum_d+prevOutcome;
					nA4++;
					break;	

					}
				}
		case "B"://same as A
			if(myaction.equals("A")){
				if(oppPrevAction.equals("A")){//case AA
					//System.out.println("MPIKE");
					B_avg_a=(B_avg_a*nB1+prevOutcome)/(nB1+1);	
					B_sum_a=B_sum_a+prevOutcome;
					nB1++;
					break;	

				}
				else{					//AB
					B_avg_b=(B_avg_b*nB2+prevOutcome)/(nB2+1);	
					B_sum_b=B_sum_b+prevOutcome;
					nB2++;
					break;	
					}
				}
			else{
				if(oppPrevAction.equals("A")){//BA
					//System.out.println("MPIKE");
					B_avg_c=(B_avg_c*nB3+prevOutcome)/(nB3+1);	
					B_sum_c=B_sum_c+prevOutcome;
					nB3++;
					break;	

				}
				else{						//BB
					B_avg_d=(B_avg_d*nB4+prevOutcome)/(nB4+1);	
					B_sum_d=B_sum_d+prevOutcome;
					nB4++;
					break;	

					}
				}
//			break;	
		case "C"://same as A 
			if(myaction.equals("A")){
				if(oppPrevAction.equals("A")){//case AA
					//System.out.println("MPIKE");
					C_avg_a=(C_avg_a*nC1+prevOutcome)/(nC1+1);
					C_sum_a=C_sum_a+prevOutcome;
					nC1++;
					break;	

				}
				else{					//AB
					C_avg_b=(C_avg_b*nC2+prevOutcome)/(nC2+1);	
					C_sum_b=C_sum_b+prevOutcome;
					nC2++;
					break;	
					}
				}
			else{
				if(oppPrevAction.equals("A")){//BA
					//System.out.println("MPIKE");
					C_avg_c=(C_avg_c*nC3+prevOutcome)/(nC3+1);	
					C_sum_c=C_sum_c+prevOutcome;
					nC3++;
					break;	

				}
				else{						//BB
					C_avg_d=(C_avg_d*nC4+prevOutcome)/(nC4+1);	
					C_sum_d=C_sum_d+prevOutcome;
					nC4++;
					break;	

					}
				}
//			break;	
		default: 
				System.out.println("Invalid State Recognition");
				break;
		}
	}
	
	
	public int GetnA1(){
		return nA1;
	}
	public int GetnA2(){
		return nA2;
	}
	public int GetnA3(){
		return nA3;
	}
	public int GetnA4(){
		return nA4;
	}
	public int GetnB1(){
		return nB1;
	}
	public int GetnB2(){
		return nB2;
	}
	public int GetnB3(){
		return nB3;
	}
	public int GetnB4(){
		return nB4;
	}
	public int GetnC1(){
		return nC1;
	}
	public int GetnC2(){
		return nC2;
	}
	public int GetnC3(){
		return nC3;
	}
	public int GetnC4(){
		return nC4;
	}
	
	public int GetTimesOfPlayedA(){
		return nA1+nA2+nA3+nA4;
	}
	
	public int GetTimesOfPlayedB(){
		return nB1+nB2+nB3+nB4;
	}
	
	public int GetTimesOfPlayedC(){
		return nC1+nC2+nC3+nC4;
	}
	
	public boolean match(){
		if(
				((A_avg_b>=A_avg_d)&&(A_avg_d>=A_avg_c)&&(A_avg_c>=A_avg_a))	&&
				(((B_avg_a>=B_avg_c)&&(B_avg_d>=B_avg_b)						&&
				  (B_avg_a>=B_avg_b)&&(B_avg_d>=B_avg_c)))						&&
				  !((C_avg_b>=C_avg_d)&&(C_avg_d>=C_avg_c)&&(C_avg_c>=C_avg_a))	&&
			      !((C_avg_a>=C_avg_c)&&(C_avg_d>=C_avg_b)      &&
			      (C_avg_a>=C_avg_b)&&(C_avg_d>=C_avg_c))){//chicken
			A_Chicken=true;
			B_Coord=true;
			C_PD=true;
			//B_Chicken=false;
			//C_Chicken=false;
			return true;
		}
		else if(((B_avg_b>=B_avg_d)&&(B_avg_d>=B_avg_c)&&(B_avg_c>=B_avg_a))	&&
				(((A_avg_a>=A_avg_c)&&(A_avg_d>=A_avg_b)						&&
						  (A_avg_a>=A_avg_b)&&(A_avg_d>=A_avg_c)))						&&
					   !(((C_avg_b>=C_avg_d)&&(C_avg_d>=C_avg_c))&&(C_avg_c>=C_avg_a))	&&
					   !(((C_avg_a>=C_avg_c)&&(C_avg_d>=C_avg_b)						&&
						  (C_avg_a>=C_avg_b)&&(C_avg_d>=C_avg_c)))
						){//coord
			A_Coord=true;
			B_Chicken=true;
			C_PD=true;
			
			//B_Coord=false;
			//C_Coord=false;
			return true;
		}
		else if(
				((C_avg_b>=C_avg_d)&&(C_avg_d>=C_avg_c)&&(C_avg_c>=C_avg_a))	&&
			   (((B_avg_a>=B_avg_c)&&(B_avg_d>=B_avg_b)							&&
				 (B_avg_a>=B_avg_b)&&(B_avg_d>=B_avg_c)))						&&
			  !(((A_avg_b>=A_avg_d)&&(A_avg_d>=A_avg_c))&&(A_avg_c>=A_avg_a))	&&
			  !(((A_avg_a>=A_avg_c)&&(A_avg_d>=A_avg_b)	 						&&
				 (A_avg_a>=A_avg_b)&&(A_avg_d>=A_avg_c)))
						){
			A_PD=true;
			C_Chicken=true;
			B_Coord=true;
			//B_PD=false;l
			//C_PD=false;
			return true;
		}
		else if(
				((B_avg_b>=B_avg_d)&&(B_avg_d>=B_avg_c)&&(B_avg_c>=B_avg_a))	&&
				(((C_avg_a>=C_avg_c)&&(C_avg_d>=C_avg_b)						&&
				  (C_avg_a>=C_avg_b)&&(C_avg_d>=C_avg_c)))						&&
			   !(((A_avg_b>=A_avg_d)&&(A_avg_d>=A_avg_c))&&(A_avg_c>=A_avg_a))	&&
			   !(((A_avg_a>=A_avg_c)&&(A_avg_d>=A_avg_b)						&&
				  (A_avg_a>=A_avg_b)&&(A_avg_d>=A_avg_c)))
				
				){
			A_PD=true;
			C_Coord=true;
			B_Chicken=true;
			//B_PD=false;l
			//C_PD=false;
			return true;
		}
		else if(
				((A_avg_b>=A_avg_d)&&(A_avg_d>=A_avg_c)&&(A_avg_c>=A_avg_a))	&&
				(((C_avg_a>=C_avg_c)&&(C_avg_d>=C_avg_b)						&&
						  (C_avg_a>=C_avg_b)&&(C_avg_d>=C_avg_c)))						&&
					   !(((B_avg_b>=B_avg_d)&&(B_avg_d>=B_avg_c))&&(B_avg_c>=B_avg_a))	&&
					   !(((B_avg_a>=B_avg_c)&&(B_avg_d>=B_avg_b)						&&
						  (B_avg_a>=B_avg_b)&&(B_avg_d>=B_avg_c)))
						){//chicken
			A_Chicken=true;
			C_Coord=true;
			B_PD=true;
			//B_Chicken=false;
			//C_Chicken=false;
			return true;
		}
		else if(
				((C_avg_b>=C_avg_d)&&(C_avg_d>=C_avg_c)&&(C_avg_c>=C_avg_a))	&&
				(((A_avg_a>=A_avg_c)&&(A_avg_d>=A_avg_b)						&&
				  (A_avg_a>=A_avg_b)&&(A_avg_d>=A_avg_c)))						&&
			   !(((B_avg_b>=B_avg_d)&&(B_avg_d>=B_avg_c))&&(B_avg_c>=B_avg_a))	&&
			   !(((B_avg_a>=B_avg_c)&&(B_avg_d>=B_avg_b)						&&
				  (B_avg_a>=B_avg_b)&&(B_avg_d>=B_avg_c)))
				){//coord
			A_Coord=true;
			C_Chicken=true;
			B_PD=true;
			//B_Coord=false;
			//C_Coord=false;
			return true;
		}
		else
			return false;
			
	}
	

	public void GetNum()
	{	
		System.out.println("HERE ARE THE TIME EACH STATE IS PLAYED");		
		System.out.println("A:"+(nA1+nA2+nA3+nA4)+"\n   A="+nA1+" B="+nA2+"\n   C="+nA3+" D="+nA4);
		System.out.println("B:"+(nB1+nB2+nA3+nB4)+"\n   A="+nB1+" B="+nB2+"\n   C="+nB3+" D="+nB4);
		System.out.println("C:"+(nC1+nC2+nA3+nC4)+"\n   A="+nC1+" B="+nC2+"\n   C="+nC3+" D="+nC4);

	}
	
	public void setState(String state)
	{
		this.state = state;
	}
	
	public void incrementIter()
	{
		cIter++;
	}
	
	public int getCiter()
	{
		return cIter;
	}
	
	public void update(int prevOutcome, String oppPrevAction, String state)
	{		
		this.prevOutcome = prevOutcome;
		this.oppPrevAction = oppPrevAction;
		this.state = state;
		myScore += prevOutcome;
	}
	public void GetAvgOutcomes(){
		
		System.out.println("The OutComes for state A");
		System.out.println("AA="+A_avg_a+" AB="+A_avg_b+"\nBA="+A_avg_c+" BB="+A_avg_d);
		System.out.println("The OutComes for state B");
		System.out.println("AA="+B_avg_a+" AB="+B_avg_b+"\nBA="+B_avg_c+" BB="+B_avg_d);
		System.out.println("The OutComes for state C");
		System.out.println("AA="+C_avg_a+" AB="+C_avg_b+"\nBA="+C_avg_c+" BB="+C_avg_d);

	}
	
	public void GetStates(){
		if((A_Chicken==true)&&(B_Coord==true)&&(C_PD==true)){
		System.out.println("The OutComes for state A=Chicken");
		System.out.println("The OutComes for state B=Coord");
		System.out.println("The OutComes for state C=PD");
		}
		else if((B_Chicken==true)&&(A_Coord==true)&&(C_PD==true)){
			System.out.println("The OutComes for state A=Coord");
			System.out.println("The OutComes for state B=Chicken");
			System.out.println("The OutComes for state C=PD");
			}
		else if((B_Chicken==true)&&(C_Coord==true)&&(A_PD==true)){
			System.out.println("The OutComes for state A=PD");
			System.out.println("The OutComes for state B=Chicken");
			System.out.println("The OutComes for state C=Coord");
			}
		else if((A_Chicken==true)&&(C_Coord==true)&&(B_PD==true)){
			System.out.println("The OutComes for state A=Chicken");
			System.out.println("The OutComes for state B=PD");
			System.out.println("The OutComes for state C=Coord");
			}
		else if((C_Chicken==true)&&(A_Coord==true)&&(B_PD==true)){
			System.out.println("The OutComes for state A=Coord");
			System.out.println("The OutComes for state B=PD");
			System.out.println("The OutComes for state C=Chicken");
			}
		else if((C_Chicken==true)&&(B_Coord==true)&&(A_PD==true)){
			System.out.println("The OutComes for state A=PD");
			System.out.println("The OutComes for state B=Coord");
			System.out.println("The OutComes for state C=Chicken");
			}
		else{
			System.out.println("WARNING");
			System.out.print("Asum/n=\n	"+A_sum_a/nA1+"	, "+A_sum_b/nA2+"\n "+A_sum_c/nA3+" , "+A_sum_d/nA4);
			System.out.print("Bsum/n=\n	"+B_sum_a/nB1+"	, "+B_sum_b/nB2+"\n "+B_sum_c/nB3+" , "+B_sum_d/nB4);
			System.out.print("Csum/n=\n	"+C_sum_a/nC1+"	, "+C_sum_b/nC2+"\n "+C_sum_c/nC3+" , "+C_sum_d/nC4);

			//System.out.println("A_Chicken="+A_Chicken+"B_Chicken="+B_Chicken+"C_Chicken="+C_Chicken);
			//System.out.println("A_Coord="+A_Coord+"B_Coord="+B_Coord+"C_Coord="+C_Coord);
			//System.out.println("A_PD="+A_PD+"B_PD="+B_PD+"C_PD="+C_PD);	
			//GetAvgOutcomes();
		}
	}
	
public boolean explore(float a, float b, float c ,float d){
	if((a+b>c+d)==true)
	return true;
	else	
	return false;
}
	
	public String selectAction()
	{	
		// "A" or "B" must be returned		
		Random rng = new Random();	
		double rand = rng.nextDouble();
	if (Found==false){
		switch(state){
		case "A":
			ExpA=explore(A_sum_a/nA1,A_sum_b/nA2,A_sum_c/nA3,A_sum_d/nA4);
			if(ExpA){
				if(rand<0.7)
					myaction="A";
				else
					myaction="B";
			}
			else{
				if(rand<0.7)
					myaction="B";
				else
					myaction="A";
			}
			break;
		case "B":
			ExpA=explore(B_sum_a/nB1,B_sum_b/nB2,B_sum_c/nB3,B_sum_d/nB4);
			if(ExpA){
				if(rand<0.7)
					myaction="A";
				else
					myaction="B";
			}
			else{
				if(rand<0.7)
					myaction="B";
				else
					myaction="A";
			}
			break;
		
		case "C":
			ExpA=explore(C_sum_a/nC1,C_sum_b/nC2,C_sum_c/nC3,C_sum_d/nC4);
			if(ExpA){
				if(rand<0.7)
					myaction="A";
				else
					myaction="B";
			}
			else{
				if(rand<0.7)
					myaction="B";
				else
					myaction="A";
			}
			break;
		default:
			if(rand > 0.5)
				myaction= "A";
			else{
				myaction="B";
			}
			break;
	}
	return myaction;
	}
	else{
	switch(state){
	case "A":
		if(A_Chicken==true){
			if((A_avg_c+A_avg_d)>(A_avg_a+A_avg_b)){
					myaction="B";
			}
			else{
				if(rand<0.7)
					myaction="B";
				else
					myaction="A";
				}
		break;
		}
		if(A_PD==true){
			myaction=oppPrevAction;
			break;
		}
		
		if(A_Coord){
			if(A_avg_a>A_avg_d)
				myaction="A";
			else
				myaction="B";
		break;
		}
	
		
	case "B":
		if(B_Chicken==true){
			if((B_avg_c+B_avg_d)>(B_avg_a+B_avg_b)){
					myaction="B";
			}
			else{
				if(rand>0.7)
					myaction="B";
				else
					myaction="C";
				}
		break;
		}
		if(B_PD==true){
			myaction=oppPrevAction;
			break;
		}
		if(B_Coord){
			if(B_avg_a>B_avg_d)
				myaction="A";
			else
				myaction="B";
			break;
		}
		
	case "C":
		if(C_Chicken==true){
			if((C_avg_c+C_avg_d)>(C_avg_a+C_avg_b)){
					myaction="B";
			}
			else{
				if(rand<0.7)
					myaction="B";
				else
					myaction="A";
				}
			
		
		break;
		}
		if(C_PD==true){
			myaction=oppPrevAction;break;
		}
		if(C_Coord){
			if(C_avg_a>C_avg_d)
				myaction="A";
			else
				myaction="B";
			break;
		}
		
		
	default:
		
		if(rand > 0.5){
			myaction= "A";}
		else{
			myaction="B";
			return myaction;
		}
		break;
	}
	return myaction;

	}
		
	}

}
