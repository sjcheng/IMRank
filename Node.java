

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
//import java.util.HashMap;

public class Node {
 
//	public final static int SUSCEPTIBLE = 1;
//	public final static int INFECTIOUS = 2;
//	public final static int RECOVERED = 3;
	
	int id;
	public List<Node> leaders;
    public Map<Node,Double> sendtoLeaders; 
	public List<Node> fans;
    public Map<Node, Double> getFromFans;
    public Map<Node, Double> propagation;
//	HashMap<Integer, Double> leadersP = new HashMap<Integer, Double>();
//	HashMap<Integer, Double> fansP = new HashMap<Integer, Double>();
	
	
	DirectedGraph g;
	MultiEdgesDirectedGraph multig;
	boolean multiedge = true;
	public double rspre;
	public double rsafter;
    public double newRecieve=0;
    public double newback = 0;
    public double newsend = 0;
    public double oldScore=0;
    public double newScore=0;
	public int rank;
//	
//	public double preAuthority;
//	public double preHub;
//	public double afterAuthority;
//	public double afterHub;
//	public int authorityRank;
//	public int hubRank;
	
//	public double pagerank_score;
//	public int pagarank;
//	public double leaderrank_score;
//	public int leaderrank;
//	public double indegree_score;
//	public int indegree;
	
	public int pre_status = -1;//
//	public int after_status = SUSCEPTIBLE;
	
	public int componentID = -1;
	public int before_component_size = 0;
	public int pre_component_size = 0;
	
	public double nodep = -1;
	
	//public List<Pair> record;
//	public HashMap<Integer, Integer> record = new HashMap<Integer, Integer>();
//	public HashMap<Integer, Integer> record_detail = new HashMap<Integer, Integer>();
	
//	public HashMap<Integer, Integer> getRecord_detail() {
//		return record_detail;
//	}

//	public void setRecord_detail(HashMap<Integer, Integer> record_detail) {
//		this.record_detail = record_detail;
//	}

//	public boolean canbeInfected(){
////		if(pre_status == RECOVERED) return false;
////		if(pre_status == INFECTIOUS) return false;
////		if(after_status == INFECTIOUS) return false;
//		if(pre_status == SUSCEPTIBLE) 
//			return true;
//		return false;
//	}
//	int state;
//	int coreness;
	
//	public int getCoreness() {
//		return coreness;
//	}

//	public void setCoreness(int coreness) {
//		this.coreness = coreness;
//	}

	public Node(int i, DirectedGraph graph)
	{
		id = i;
		leaders = new ArrayList<Node>();
		fans = new ArrayList<Node>();
        this.sendtoLeaders = new HashMap<Node, Double>();
        this.getFromFans = new HashMap<Node, Double>();
        this.propagation = new HashMap<Node, Double>();
		g = graph;
//		rspre = 0;
//		rsafter = 0;
//		rank = -1;
		//record = new ArrayList<Pair>();
//		state = Node.SUSCEPTIBLE;
	}
	
	public Node(int i, MultiEdgesDirectedGraph graph)
	{
		id = i;
		leaders = new ArrayList<Node>();
		fans = new ArrayList<Node>();
        this.sendtoLeaders = new HashMap<Node, Double>();
        this.getFromFans = new HashMap<Node, Double>();
        this.propagation = new HashMap<Node, Double>();
		multig = graph;
//		rspre = 0;
//		rsafter = 0;
//		rank = -1;
		multiedge = true;
		
//		state = Node.SUSCEPTIBLE;
	}
	
	public void removeAlllinks(){
		for(Node leader: leaders){
			leader.deleteFan(this);
		}
		for(Node fan: fans){
			fan.deleteLeader(this);
		}
		leaders = new ArrayList<Node>();
		fans = new ArrayList<Node>();
	}
	
	
	
	public void setFans(List<Node> fanstemp) {
		//this.fans = fans;
		for(Node fan: fanstemp)
		{
			fans.add(fan);
			fan.addLeader(this);
		}
	}

	public void setLeaders(List<Node> leaderstemp) {
		//this.leaders = leaders;
		for(Node leader: leaderstemp)
		{
			leaders.add(leader);
			leader.addFan(this);
		}
	}

	public int getNodeID()
	{
		return id;
	}
	
	public boolean hasLeader(Node dest)
	{
		 if(leaders.contains(dest)) return true;
     	 return false;
	}
	
	public boolean hasFan(Node dest)
	{
		 if(fans.contains(dest)) return true;
     	 return false;
	}
	
    public boolean addLeader(Node nei)
    {
    	if(multiedge){
    		return leaders.add(nei);
    	}else{
        	if(leaders.contains(nei)) {
        		//System.out.println("contain this leader");
         		return false;
        	}
        	return leaders.add(nei);
    	}
    }
    
    public boolean addFan(Node nei)
    {
    	if(multiedge){
        	return fans.add(nei);
    	}else{
    		if(fans.contains(nei)) {
        		//System.out.println("contain this fan");
        		return false;
            	}
            	return fans.add(nei);
    	}
    	
    }
    
    public boolean deleteLeader(Node nei)
    {
    	return leaders.remove(nei);
    }
	
    public boolean deleteFan(Node nei)
    {
    	return fans.remove(nei);
    }
    
    public int getDegree()
    {
    	return leaders.size()+fans.size();
    }
    
    public int getOutDegree()
    {
    	return leaders.size();
    }
    
    public int getInDegree()
    {
    	return fans.size();
    }
    
    public List<Node> getLeaders()
    {
    	return leaders;
    }
    
    public List<Node> getFans()
    {
    	return fans;
    }

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

//	public HashMap<Integer, Integer> getRecord() {
//		return record;
//	}
//
//	public void setRecord(HashMap<Integer, Integer> record) {
//		this.record = record;
//	}

    
}

