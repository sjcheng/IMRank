


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;

import DirectedGraph.*;
import java.util.*;
public class LeaderRank {

	DirectedGraph graph;
	Node gn;

	
	Hashtable IDToLRHashtable = new Hashtable();
	
	public static void main(String[] args){
		String gfile = "F:\\Chengsuqi\\Experiments\\LeaderRank\\testG-4.txt";
		String rsfile = "F:\\Chengsuqi\\Experiments\\LeaderRank\\testG-4_lrs.txt";
		gfile = "C:\\douban\\douban.550k.reciprocaledgeslist.txt";
		rsfile = "C:\\douban\\douban.550k.reciprocaledgeslist-leaderRank.txt";
		LeaderRank lr = new LeaderRank(gfile);
		System.out.println("finish to read in graph");
		lr.addGroundNode();
		System.out.println("finish to addground node");
		lr.calculateScore();
		System.out.println("finish to calculate score");
		lr.savetoFile(rsfile);
		System.out.println("finish to calculate score");
	}
	
	public LeaderRank(String gfile)
	{
	     graph = DirectedGraph.ConstructFromFile(gfile);
	     gn = new Node(Integer.MAX_VALUE,graph);
	}
	
	public void addGroundNode(){
		 graph.addNode(gn);
		 List<Node> nodes = graph.getNodes();
		 if(nodes.get(nodes.size()-1).equals(gn)) 
			 System.out.println("right");
		 System.out.println("stop");
		 System.out.println("begin to add");
		 int i = 0;
		 for(; i<(nodes.size()-1); i++){
			 Node node = nodes.get(i);
			 //if(!node.equals(gn)){
		    	 //graph.addDirectEdge(node, gn);
		    	 //graph.addDirectEdge(gn, node);
				 
			 //}
			 node.addFan(gn);
			 node.addLeader(gn);
			 gn.addFan(node);
			 gn.addLeader(node);
			 //System.out.println(i);
		 }
	}
	
	public void calculateScore(){
		//assign one unit score to every node except gn
		List<Node> nodes = graph.getNodes();
		for(Node node: nodes){
			if(!node.equals(gn))
		    	node.rspre = 1;
		}

		//diffusion to stable state
		double error = 10000;
		double error_threshold=0.00002;
		int step = 1;
		while(error>error_threshold){
			System.out.println("step: "+step);
			for(Node node: nodes){
				double partscore = node.rspre/(double)node.getOutDegree();
				List<Node> leaders = node.getLeaders();
				for(Node leader: leaders)
					leader.rsafter = leader.rsafter + partscore;
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
		double gnScore = 0;;
		for(Node node: nodes){
			if(node.equals(gn)){
				gnScore = gn.rspre;
				gn.rspre = 0;
				break;}
		}
		double partgnScore = gnScore/(double)(graph.getGraphSize()-1);
		for(Node node: nodes){
			if(!node.equals(gn))
				node.rspre = node.rspre + partgnScore;
		}
		System.out.println("Done!");
	}
	
	public void savetoFile(String rsfile){
		List<Node> nodes = graph.getNodes();
		try{
			PrintWriter out= new PrintWriter(new BufferedWriter(new FileWriter(rsfile)));
	    	for(Node node: nodes)
		    {
	    		if(!node.equals(gn))
	    	    	out.println(node.getNodeID() + "\t" +node.rspre);
		    }
		    out.close();
	     	}
		catch(IOException e)
		    {
			e.printStackTrace();
		    }
	}
}
