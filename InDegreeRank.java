



import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

//import PageRankAlgorithm.Element;


public class InDegreeRank {

	Element[] idrScore = null;
	
	public static void main(String[] args){
		String gfile = "F:\\Chengsuqi\\Experiments\\dataset\\influence\\testG.txt";
		String rsfile = "F:\\Chengsuqi\\Experiments\\dataset\\influence\\testG-hits.txt";
		gfile = "F:\\Chengsuqi\\Experiments\\dataset\\influence\\douban.txt";
		rsfile = "F:\\Chengsuqi\\Experiments\\dataset\\influence\\douban-pageRank.txt";
		gfile = "C:\\Experiments\\influence\\douban.txt";
		rsfile = "C:\\Experiments\\influence\\douban-InDegreeRank.txt";
		System.out.println("begin to read in graph");
		DirectedGraph g = DirectedGraph.ConstructFromFile(new File(gfile));
		InDegreeRank ir = new InDegreeRank();
	    ir.rank(g);
		System.out.println("begin to save score");
		ir.savetoFile(g, rsfile);
	}
	
	
	public void rank(DirectedGraph graph){
		idrScore = new Element[graph.getGraphSize()];
		int count = 0;
		for(Node node: graph.getNodes()){
			idrScore[count] = new Element(node.getNodeID(), node.getInDegree());
			count++;
		}
		Arrays.sort(idrScore);
		count = 1;
		for(Element element:idrScore){
			Node node = graph.getNodeByID(element.index);
			node.setRank(count);
			count++;
		}
}
	public void savetoFile(DirectedGraph g, String rsfile){
		List<Node> nodes = g.getNodes();
		try{
			PrintWriter out= new PrintWriter(new BufferedWriter(new FileWriter(rsfile)));
	    	for(Node node: nodes)
		    {
	    	    	out.println(node.getNodeID() + "\t" +node.getInDegree()+"\t"+node.rank);
		    }
		    out.close();
	     	}
		catch(IOException e)
		    {
			e.printStackTrace();
		    }
		nodes = null;
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
