
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.List;

import DirectedGraph.*;

import java.io.File;

import java.util.*;

public class PageRankAlgorithm {

	double c = 0.15;
	Element[] prScore = null;
	//Hashtable IDToLRHashtable = new Hashtable();
	
	public static void main(String[] args){
		String gfile = "F:\\Chengsuqi\\Experiments\\dataset\\influence\\testG.txt";
		String rsfile = "F:\\Chengsuqi\\Experiments\\dataset\\influence\\testG-hits.txt";
		gfile = "F:\\Chengsuqi\\Experiments\\dataset\\influence\\douban.txt";
		rsfile = "F:\\Chengsuqi\\Experiments\\dataset\\influence\\douban-pageRank.txt";
		gfile = "C:\\Experiments\\influence\\douban.txt";
		rsfile = "C:\\Experiments\\influence\\douban-pageRank.txt";
		System.out.println("begin to read in graph");
		DirectedGraph g = DirectedGraph.ConstructFromFile(new File(gfile));
		PageRankAlgorithm pr = new PageRankAlgorithm();
		//System.out.println("begin to caculate score");
		pr.calculateScore(g);
	    pr.rank(g);
		//System.out.println("begin to save score");
		pr.savetoFile(g, rsfile);
	}
	
	public void calculateScore(DirectedGraph graph){
		//assign one unit score to every node
		List<Node> nodes = graph.getNodes();
		for(Node node: nodes){
		    	node.rspre = 1;
		    	node.rank = -1;
		    	node.rsafter = 0;
		}

		//diffusion to stable state
		double error = 10000;
		double error_threshold=0.00000000001;
		int step = 1;
		while(error>error_threshold){
			//System.out.println("step: "+step+" error: "+error);
			//int count = 0;
			double other = 0;
			for(Node node: nodes){
				if(node.getOutDegree() == 0 ){ 
					double partscore = (1-c)*node.rspre/(double)graph.getGraphSize();
					other = other + partscore;
					continue;
				}
				int under = node.getOutDegree();
				List<Node> leaders = node.getLeaders();
				
				double partscore = (1-c)*node.rspre/(double)under;
			
				for(Node leader: leaders)
					leader.rsafter = leader.rsafter + partscore;
			}
			
			//caculate error
			error = 0;
			for(Node node: nodes){
				node.rsafter = node.rsafter + c + other;
				error = error + Math.abs(node.rsafter - node.rspre);
			}
			
			error = error/(double)graph.getGraphSize();
			
			for(Node node: nodes){
				node.rspre = node.rsafter;
				node.rsafter = 0;
			}
			
			step++;
			//System.out.println("step: "+step+" error: "+error);
		}
		
		nodes = null;
		//System.out.println("Done!");
	}
	
	public void rank(DirectedGraph graph){
		prScore = new Element[graph.getGraphSize()];
		//System.out.println(prScore.length+"\t"+graph.getGraphSize());
		int count = 0;
		for(Node node: graph.getNodes()){
			prScore[count] = new Element(node.getNodeID(), node.rspre);
			count++;
		}
		Arrays.sort(prScore);
		count = 1;
		for(Element element:prScore){
			Node node = graph.getNodeByID(element.index);
			node.setRank(count);
			//System.out.println(node.getNodeID()+"\t"+count);
			count++;
		}
	}
	
	public int[] getTestid(DirectedGraph graph, int start, int length){
		int[] testid = new int[length];
		for(int i = 0; i<length ;i++){
			int index = start + i -1;
			testid[i] = prScore[index].index;
		}
		
		return testid;
	}
	
	public double getPRScore(Node node){
		
		return node.rspre;
		
	}
	
    public double getPR(Node node){
		
		return node.rank;
		
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
	
	public void printOut(DirectedGraph g){
	    for(Node node: g.getNodes())
		    {
	    	    	System.out.println(node.getNodeID() + "\t" +node.rspre +"\t"+node.rank);
		    } 
	}
	
	public void savetoFile(DirectedGraph g, String rsfile){
		List<Node> nodes = g.getNodes();
		try{
			PrintWriter out= new PrintWriter(new BufferedWriter(new FileWriter(rsfile)));
	    	for(Node node: nodes)
		    {
//	    		if(node.rank == -1)
//	    			System.out.println(node.getNodeID()+"\t"+node.rspre+"\t"+node.rank);
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

}

