


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Hashtable;
//import PageRankAlgorithm.Element;


import java.util.*;
public class LeaderRankAlgorithm {

	DirectedGraph graph;
	Node gn;
	Element[] lrScore = null;
	Hashtable IDToLRHashtable = new Hashtable();
	
	
	public LeaderRankAlgorithm(DirectedGraph graph)
	{
	     //graph = DirectedGraph.ConstructFromFile(gfile);
		 this.graph = graph;
	     gn = new Node(Integer.MAX_VALUE,graph);
	}
	
	public LeaderRankAlgorithm(String gfile)
	{
	     graph = DirectedGraph.ConstructFromFile(gfile);
		 //this.graph = graph;
	     gn = new Node(Integer.MAX_VALUE,graph);
	}
	
	public static void main(String[] args){
		String gfile = "F:\\Chengsuqi\\Experiments\\dataset\\influence\\testG.txt";
		String rsfile = "F:\\Chengsuqi\\Experiments\\dataset\\influence\\testG-hits.txt";
		gfile = "F:\\Chengsuqi\\Experiments\\dataset\\influence\\douban.txt";
		rsfile = "F:\\Chengsuqi\\Experiments\\dataset\\influence\\douban-leaderRank.txt";
		gfile = "C:\\Experiments\\influence\\douban.txt";
		rsfile = "C:\\Experiments\\influence\\douban-leaderRank.txt";
		
		System.out.println("begin to read in graph");
		LeaderRankAlgorithm lr = new LeaderRankAlgorithm(gfile);
		System.out.println("begin to caculate score");
		lr.calculateScore();
		lr.rank(lr.graph);
		System.out.println("begin to save score");
		lr.savetoFile(rsfile);
	}
	
	public void addGroundNode(){
		 graph.addNode(gn);
		 List<Node> nodes = graph.getNodes();
//		 if(nodes.get(nodes.size()-1).equals(gn)) 
//			 System.out.println("right");
//		 System.out.println("stop");
//		 System.out.println("begin to add");
		 int i = 0;
		 for(; i<(nodes.size()-1); i++){
			 Node node = nodes.get(i);
			 node.fans.add(gn);
			 node.leaders.add(gn);
			 gn.fans.add(node);
			 gn.leaders.add(node);
//			 node.addFan(gn);
//			 node.addLeader(gn);
//			 gn.addFan(node);
//			 gn.addLeader(node);
		 }
	}
	
	public void calculateScore(){
		//assign one unit score to every node except gn
		addGroundNode();
		List<Node> nodes = graph.getNodes();
		for(Node node: nodes){
			if(!node.equals(gn)){
		    	node.rspre = 1;
		    	node.rsafter = 0;
		    	node.rank = -1;
			}else{
				node.rspre = 0;
				node.rsafter = 0;
				node.rank = -1;
			}
		}

		//diffusion to stable state
		double error = 10000;
		double error_threshold=0.0000000000001;
		int step = 1;
		while(error>error_threshold){
			//System.out.println("step: "+step);
			for(Node node: nodes){
				double partscore = node.rspre/(double)node.getOutDegree();
				List<Node> leaders = node.getLeaders();
				for(Node leader: leaders)
					leader.rsafter = leader.rsafter + partscore;
				leaders = null;
			}
			
			//caculate error
			error = 0;
			for(Node node: nodes){
				error = error + Math.abs(node.rsafter - node.rspre);
			}
			error = error/(double)graph.getGraphSize();
			
			for(Node node: nodes){
				node.rspre = node.rsafter;
				node.rsafter = 0;
				//System.out.println(node.getNodeID()+"\t"+node.rspre);
			}
			
			step++;
		}
		
		//give the score of the ground node to other nodes
//		double gnScore = 0;;
//		for(Node node: nodes){
//			if(node.equals(gn)){
//				gnScore = gn.rspre;
//				gn.rspre = 0;
//				break;}
//		}
		double gnScore = gn.rspre;
		gn.rspre = 0;
		graph.removeNode(gn);
		
		double partgnScore = gnScore/(double)(graph.getGraphSize());
		for(Node node: graph.getNodes()){
//			if(!node.equals(gn))
				node.rspre = node.rspre + partgnScore;
		}
		
		
		nodes = null;
		//System.out.println("Done!");
	}
	
	public void savetoFile(String rsfile){
		List<Node> nodes = graph.getNodes();
		try{
			PrintWriter out= new PrintWriter(new BufferedWriter(new FileWriter(rsfile)));
	    	for(Node node: nodes)
		    {
	    		if(!node.equals(gn))
	    	    	out.println(node.getNodeID() + "\t" +node.rspre+"\t"+node.rank);
		    }
		    out.close();
	     	}
		catch(IOException e)
		    {
			e.printStackTrace();
		    }
		nodes = null;
	}
	
	public void rank(DirectedGraph graph){
		lrScore = new Element[graph.getGraphSize()];
		int count = 0;
		for(Node node: graph.getNodes()){
			lrScore[count] = new Element(node.getNodeID(), node.rspre);
			count++;
		}
		Arrays.sort(lrScore);
		count = 1;
		for(Element element:lrScore){
			Node node = graph.getNodeByID(element.index);
			node.setRank(count);
			//System.out.println(node.rspre);
			count++;
		}
	}
	
	public int[] getTestid(DirectedGraph graph, int start, int length){
		int[] testid = new int[length];
		for(int i = 0; i<length ;i++){
			int index = start + i -1;
			testid[i] = lrScore[index].index;
		}
		
		return testid;
	}
	
	static class Element implements Comparable<Element>
	{
		int index;
		double value;
		
		public Element(int index, double value)
		{
			this.index = index;
			this.value = value;
		}
		
		public int compareTo(Element e)
		{
			if(value < e.value)
				return 1;
			else if(value == e.value)
				return 0;
			else
				return -1;
		}
	}
}
