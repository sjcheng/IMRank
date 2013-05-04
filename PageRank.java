
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.List;
import DirectedGraph.*;
import java.util.*;


public class PageRank {

	DirectedGraph graph;
	double c = 0.15;
	Hashtable IDToLRHashtable = new Hashtable();
	
	public static void main(String[] args){
		String gfile = "E:\\Chengsuqi\\Experiments\\dataset\\influence\\testG.txt";
        gfile = "E:/Experiments/IMRank/karate_edgelist_1.txt";
		String rsfile = "E:\\Chengsuqi\\Experiments\\dataset\\influence\\testG-pr.txt";
        rsfile = "E:/Experiments/IMRank/karate_edgelist_1_pr.txt";
//		gfile = "F:\\Chengsuqi\\Experiments\\dataset\\douban\\douban.550k.reciprocaledgeslist.txt";
//		rsfile = "C:\\douban\\douban.550k.reciprocaledgeslist-pageRank.txt";
		System.out.println("begin to read in graph");
		PageRank pr = new PageRank(gfile);
		System.out.println("begin to caculate score");
		pr.calculateScore();
		System.out.println("begin to save score");
		pr.savetoFile(rsfile);
	}
	
	public PageRank(String gfile)
	{
	     graph = DirectedGraph.ConstructFromFile(gfile);
	}
	
	public PageRank(DirectedGraph graph)
	{
	     this.graph = graph;
	}

	public void calculateScore(){
		//assign one unit score to every node
		List<Node> nodes = graph.getNodes();
		for(Node node: nodes){
		    	node.rspre = 1;
		}

		//diffusion to stable state
		double error = 10000;
		double error_threshold=0.00002;
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
		}
		
		System.out.println("Done!");
	}
	
	public void savetoFile(String rsfile){
		List<Node> nodes = graph.getNodes();
		try{
			PrintWriter out= new PrintWriter(new BufferedWriter(new FileWriter(rsfile)));
	    	for(Node node: nodes)
		    {
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

