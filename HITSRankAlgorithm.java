



import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

//import LeaderRankAlgorithm.Element;

import java.io.*;

public class HITSRankAlgorithm {

	DirectedGraph graph;
	Element[] hitsScore = null;
	//Hashtable IDToHITSHashtable = new Hashtable();
	
	public static void main(String[] args){
		String gfile = "F:\\Chengsuqi\\Experiments\\dataset\\influence\\testG.txt";
		String rsfile = "F:\\Chengsuqi\\Experiments\\dataset\\influence\\testG-hits.txt";
		gfile = "F:\\Chengsuqi\\Experiments\\dataset\\influence\\douban.txt";
		gfile = "C:\\Experiments\\influence\\epinions.txt";
		String rs1file = "C:\\Experiments\\influence\\epinions-HITS(Authority).txt";
		String rs2file = "C:\\Experiments\\influence\\epinions-HITS(HUB).txt";
		System.out.println("begin to read in graph");
		HITSRankAlgorithm pr = new HITSRankAlgorithm(gfile);
		System.out.println("begin to caculate score");
		pr.calculateScore();
	    pr.rankByAuthority();
	    pr.rankByHub();
		System.out.println("begin to save score");
		pr.savetoFile(rs1file, rs2file);
	}
	
	public HITSRankAlgorithm(String gfile){
		DirectedGraph g = DirectedGraph.ConstructFromFile(new File(gfile));
		this.graph = g;
	}
	
	public HITSRankAlgorithm(DirectedGraph graph){
		this.graph = graph;
	}
	
	public void calculateScore(){
		//set the initial 
		for(Node node: graph.getNodes()){
			node.preAuthority = 1;
			node.preHub = 1;
			//node.afterAuthority = 0;
			//node.afterHub = 0;
			node.authorityRank = -1;
			node.hubRank = -1;
		}
		
//		double error1 = 10000;
//		double error2 = 10000;
//		double error_threshold=0.0000000001;

		//run 
		int step = 1;
		//F:\\Chengsuqi\\Experiments\\dataset\\influence\\douban.txt
		while(step < 60){
		    //update authority scores
			for(Node node: graph.getNodes()){
				for(Node fan: node.getFans()){
					node.preAuthority = node.preAuthority + fan.preHub;
				}
			}
			
			//update hub scores
			for(Node node: graph.getNodes()){
				for(Node leader: node.getLeaders()){
					node.preHub = node.preHub + leader.preAuthority;
				}
			}
			
			double sumAuthority = 0;
			double sumHub = 0;
			
			for(Node node: graph.getNodes()){
				sumAuthority = sumAuthority + node.preAuthority;
				sumHub = sumHub + node.preHub;
			}
			
            //caculate error
//			error1 = 0;
//			error2 = 0;
			double newAuthority;
			double newHub;
			for(Node node: graph.getNodes()){
				newAuthority = node.preAuthority/sumAuthority;
				newHub = node.preHub/sumHub;
//				error1 = error1 + Math.abs(node.afterAuthority - newAuthority);
//				error2 = error2 + Math.abs(node.afterHub - newHub);
				node.afterAuthority = newAuthority;
				node.afterHub = newHub;
			}
			
//			error1 = error1/(double)graph.getGraphSize();
//			error2 = error2/(double)graph.getGraphSize();
//			System.out.println(step+"  "+error1+error2);
			System.out.println(step);
			step++;
		}
	}
	
	public void rankByAuthority(){
		hitsScore = new Element[graph.getGraphSize()];
		int count = 0;
		for(Node node: graph.getNodes()){
			hitsScore[count] = new Element(node.getNodeID(), node.afterAuthority);
			count++;
		}
		Arrays.sort(hitsScore);
		count = 1;
		for(Element element:hitsScore){
			Node node = graph.getNodeByID(element.index);
			node.authorityRank = count;
			//System.out.println(node.rspre);
			count++;
		}
	}
	
	public void rankByHub(){
		hitsScore = new Element[graph.getGraphSize()];
		int count = 0;
		for(Node node: graph.getNodes()){
			hitsScore[count] = new Element(node.getNodeID(), node.afterHub);
			count++;
		}
		Arrays.sort(hitsScore);
		count = 1;
		for(Element element:hitsScore){
			Node node = graph.getNodeByID(element.index);
			node.hubRank = count;
			//System.out.println(node.rspre);
			count++;
		}
	}
	
	public void savetoFile(String rs1file, String rs2file){
		List<Node> nodes = graph.getNodes();
		try{
			PrintWriter out1= new PrintWriter(new BufferedWriter(new FileWriter(rs1file)));
			PrintWriter out2= new PrintWriter(new BufferedWriter(new FileWriter(rs2file)));
	    	for(Node node: nodes)
		    {
	    	    out1.println(node.getNodeID() + "\t" +node.afterAuthority+"\t"+node.authorityRank);
	    	    out2.println(node.getNodeID() + "\t" +node.afterHub+"\t"+node.hubRank);
		    }
		    out1.close();
		    out2.close();
	     	}
		catch(IOException e)
		    {
			e.printStackTrace();
		    }
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
