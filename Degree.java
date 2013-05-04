


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;
import DirectedGraph.*;

import java.util.*;

public class Degree {

	DirectedGraph graph;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String gfile = "F:\\Chengsuqi\\Experiments\\LeaderRank\\testG.txt";
		String rsfile = "F:\\Chengsuqi\\Experiments\\LeaderRank\\testG_prs.txt";
		gfile = "C:\\douban\\douban.550k.edgeslist.txt";
		rsfile = "C:\\douban\\douban.550k.edgeslist-degree.txt";
		System.out.println("begin to read in graph");
		Degree pr = new Degree(gfile);

		System.out.println("begin to save score");
		pr.savetoFile(rsfile);
	}
	
	public Degree(String gfile)
	{
	     graph = DirectedGraph.ConstructFromFile(gfile);
	}
	
	public void savetoFile(String rsfile){
		List<Node> nodes = graph.getNodes();
		try{
			PrintWriter out= new PrintWriter(new BufferedWriter(new FileWriter(rsfile)));
	    	for(Node node: nodes)
		    {
	    	    	out.println(node.getNodeID() + "\t" +node.getInDegree()+"\t"+node.getOutDegree());
		    }
		    out.close();
	     	}
		catch(IOException e)
		    {
			e.printStackTrace();
		    }
	}

}
