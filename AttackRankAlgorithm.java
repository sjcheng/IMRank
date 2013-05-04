



import java.util.List;
import java.util.ArrayList;

public class AttackRankAlgorithm {

	DirectedGraph graph;
	PageRankAlgorithm pr;
	LeaderRankAlgorithm lr;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String gfile = "F:\\Chengsuqi\\Experiments\\dataset\\douban\\douban.550k.edgeslist.txt";
	    //gfile = "C:\\douban\\testG-1.txt";
		//String rsfile = "C:\\douban\\douban.550k.edgeslist-degree.txt";
		System.out.println("begin to read in graph");
		AttackRankAlgorithm ara = new AttackRankAlgorithm(gfile);
		//ara.attackLeaderRank();
		System.out.println("deletereciprocallinks");
//		System.out.println("attack pageRank:");
		//ara.attackPageRank(1,100);
		System.out.println("attack leaderRank:");
		ara.attackLeaderRank(1,100);
		//ara.attackPageRank();
		System.out.println("begin to save score");
		//pr.savetoFile(rsfile);
	}
	
	
	public AttackRankAlgorithm(String gfile){
		graph = DirectedGraph.ConstructFromFile(gfile);
		pr = new PageRankAlgorithm();
		lr = new LeaderRankAlgorithm(graph);
	}

	public void attackPageRank(int start, int length){

//		int[] testid = {394360, 363324, 42165, 493294, 39575, 1476, 294699, 541159, 102408, 500144, 520690,
//				464299, 466032, 475823, 252996, 344467, 499707, 441492, 537493, 133169, 374653,
//				83739, 143084, 314122, 474730, 320705, 198873, 240349, 180461, 458917, 89398, 
//				141986, 464856, 387028, 295274, 515272, 149601, 323151, 424971, 16634, 425852,
//				349622, 506939, 235179, 475673, 81037, 491744, 135490, 447094, 307278};
//		int start = 1;
//		int length = 50;
		int[] testid = this.getTestidByPageRank(start, length);
		//int[] testid = {1,2,3};
//		pr.calculateScore(graph);
//		pr.rank(graph);
//		System.out.println("finish to caculate the origin state");
		//print out rank
//		for(Node node: graph.getNodes()){
//			System.out.println(node.getNodeID()+"\t"+node.rspre+"\t"+node.rank);
//		}
		List<Node> fanstemp = null;
		List<Node> leaderstemp = null;
		Node node = null;
		
		for(int id=0; id < testid.length; id++){
			node = graph.getNodeByID(testid[id]);
			fanstemp = new ArrayList<Node>();
			leaderstemp = new ArrayList<Node>();
			for(Node fan: node.getFans())
				fanstemp.add(fan);
			for(Node leader: node.getLeaders())
				leaderstemp.add(leader);
			
			//this.deleteReciprocalLeaders(node);
			//this.deleteNonReciprocalLeaders(node);
			//this.addNonReciprocalFans(node);
			//this.maxReciprocallinks(node);
			this.deleteReciprocalFans(node);
			pr.calculateScore(graph);
			pr.rank(graph);
			//System.out.println("Spammer "+node.getNodeID());
			int prerank = start + id;
			System.out.println(prerank+"\t"+node.getNodeID()+"\t"+node.getOutDegree()+"\t"+node.rspre+"\t"+node.rank);
			
//			recover links
			node.removeAlllinks();
			node.setFans(fanstemp);
			node.setLeaders(leaderstemp);
		}
		
		fanstemp = null;
		leaderstemp = null;
		node = null;
	}
	
	
	public int[] getTestidByPageRank(int start, int length){
		pr.calculateScore(graph);
		pr.rank(graph);
		System.out.println("finish to get testid");
		return pr.getTestid(graph, start, length);
	}
	
	public int[] getTestidByLeaderRank(int start, int length){
		lr.calculateScore();
		lr.rank(graph);
		System.out.println("finish to get testid");
		return lr.getTestid(graph, start, length);
	}
	
	public void attackLeaderRank(int start, int length){

		//int[] testid = {394360,363324,42165,493294,39575,1476,294699,541159,102408,500144};//pagerank
//		int [] testid = {394360, 39575, 363324, 42165, 466032, 1476, 493294, 344467, 520690,133169, 
//				464299, 314122, 83739, 102408, 89398, 320705, 541159, 323151, 537493, 135490,
//				23136, 198873, 458917, 474730, 475823, 235179, 16634, 141986, 511946, 515272,
//				349622,	464856, 499707, 424971, 441492, 116964, 167041, 240349, 25591, 491744,
//				374653, 398950, 40475, 106662, 358957, 475673, 452680, 234660, 372804, 447166
//		};
//		
//		int start = 1;
//		int length = 50;
		int[] testid = this.getTestidByLeaderRank(start, length);

		//int[] testid = {1, 2, 3, 4, 5, 6};
//		lr.calculateScore();
//		lr.rank(graph);
		//print out rank
//		for(Node node: graph.getNodes()){
//			System.out.println(node.getNodeID()+"\t"+node.rspre+"\t"+node.rank);
//		}
		List<Node> fanstemp = null;
		List<Node> leaderstemp = null;
		Node node = null;
		for(int id=0; id < testid.length; id++){
			node = graph.getNodeByID(testid[id]);
			
			fanstemp = new ArrayList<Node>();
			leaderstemp = new ArrayList<Node>();
			for(Node fan: node.getFans())
				fanstemp.add(fan);
			for(Node leader: node.getLeaders())
				leaderstemp.add(leader);
			
			int prerank = start + id;
			this.deleteNonReciprocalFans(node);
			//this.deleteReciprocalFans(node);
			//this.deleteReciprocalLeaders(node);
			//this.maxReciprocallinks(node);
			//this.deleteNonReciprocalLeaders(node);
			//this.addNonReciprocalFans(node);
			//lr.addGroundNode();
			lr.calculateScore();
			lr.rank(graph);
			//System.out.println("Spammer "+node.getNodeID());
			System.out.println(prerank+"\t"+node.getNodeID()+"\t"+node.getOutDegree()+"\t"+node.rspre+"\t"+node.rank);
			
			//recover links
			node.removeAlllinks();
			node.setFans(fanstemp);
			node.setLeaders(leaderstemp);
		}
	}
	
	//single node mode
	public void deleteNonReciprocalLeaders(Node node)
	{
		//Node node = graph.getNodeByID(nodeid);
		
		List<Node> removes = new ArrayList<Node>();
		
		List<Node> leaders = node.getLeaders();
		for(Node leader:leaders){
				if(!node.hasFan(leader))
				{
				   removes.add(leader);
				}
		}
		for(Node remove: removes)
		{
			node.deleteLeader(remove);
			remove.deleteFan(node);
		}
		
		removes = null;
		leaders = null;
	}
	
	
    //	single node mode
	public void deleteReciprocalLeaders(Node node)
	{
		//Node node = graph.getNodeByID(nodeid);
		
		List<Node> removes = new ArrayList<Node>();
		
		List<Node> leaders = node.getLeaders();
		for(Node leader:leaders){
				if(node.hasFan(leader))
				{
				   removes.add(leader);
				}
		}
		
		for(Node remove: removes)
		{
			node.deleteLeader(remove);
			remove.deleteFan(node);
		}

		removes = null;
		leaders = null;
	}
	
	
	public void deleteReciprocalFans(Node node)
	{
		//Node node = graph.getNodeByID(nodeid);
		
		List<Node> removes = new ArrayList<Node>();
		
		List<Node> leaders = node.getLeaders();
		for(Node leader:leaders){
				if(node.hasFan(leader))
				{
				   removes.add(leader);
				}
		}
		
		for(Node remove: removes)
		{
//			node.deleteLeader(remove);
//			remove.deleteFan(node);
			remove.deleteLeader(node);
			node.deleteFan(remove);
		}

		removes = null;
		leaders = null;
	}
	
	public void deleteNonReciprocalFans(Node node)
	{
		//Node node = graph.getNodeByID(nodeid);
		
		List<Node> removes = new ArrayList<Node>();
		
		List<Node> fans = node.getFans();
		for(Node fan:fans){
				if(!node.hasLeader(fan))
				{
				   removes.add(fan);
				}
		}
		
		for(Node remove: removes)
		{
//			node.deleteLeader(remove);
//			remove.deleteFan(node);
			remove.deleteLeader(node);
			node.deleteFan(remove);
		}

		removes = null;
		fans = null;
	}
	
	public void addNonReciprocalFans(Node node){

		//Node node = graph.getNodeByID(nodeid);	
		List<Node> adds = new ArrayList<Node>();
		List<Node> fans = node.getFans();
		for(Node fan:fans){
				if(!node.hasLeader(fan))
				{
				   adds.add(fan);
				}
		}
		for(Node add: adds)
		{
			node.addLeader(add);
			add.addFan(node);
		}
		adds = null;
		fans = null;
	}
	
	public void maxReciprocallinks(Node node){

		//Node node = graph.getNodeByID(nodeid);
	
		List<Node> removeLeaders = new ArrayList<Node>();
		List<Node> reserveLeaders = new ArrayList<Node>();
		List<Node> addFans = new ArrayList<Node>();
		
		List<Node> fans = node.getFans();
		List<Node> leaders = node.getLeaders();
		
		for(Node fan:fans){
				if(!node.hasLeader(fan))
				{
				   addFans.add(fan);
				}else
				{
					reserveLeaders.add(fan);
				}
		}

	
		for(Node leader: leaders)
		{
			if(!reserveLeaders.contains(leader)){
				//add this leader to remove queue
			   removeLeaders.add(leader);
			}
		}
		
		for(Node addFan: addFans)
		{
			//add this fan as a leader
			addFan.addFan(node);
			node.addLeader(addFan);
		}
		
		for(Node removeLeader: removeLeaders)
		{
			//remove this  leader
			removeLeader.deleteFan(node);
			node.deleteLeader(removeLeader);
		}
		
		removeLeaders = null;
		reserveLeaders = null;
		addFans = null;
		fans = null;
		leaders = null;
	}
	
	//multi-nodes mode
	
}
