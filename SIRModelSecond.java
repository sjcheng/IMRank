
import graph.Node;
import graph.Graph;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class SIRModelSecond {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		double p = 0.3;
		String oriFile = "C:\\net0702-2.txt";
		SIRModelSecond SIR = new SIRModelSecond();
		int len = 10000;
		double sum = 0;
		double num = 0;
		for(int step=0; step<len; step++)
		{
	    	Graph g = Graph.ConstructFromFile(oriFile);
	    	num = g.getGraphSize();
	    	List<Node> initialNodes =  SIR.initialNodes(g);
	    	SIR.infectNodes(initialNodes);
	    	double value = SIR.runOneGraph(p, g);
	    	sum = sum + value;
	    	System.out.println(step+"\t"+value);
		}
		System.out.println(sum/len+"\t"+(sum/len)/num);
	}

	public List<Node> initialNodes(Graph g){
		List<Node> nodes = new ArrayList<Node>();
		//int[] ids = {81,82,83};
		int[] ids = {4,5,6};
		//int[] ids = {23,24,25};
		for(int i=0; i<ids.length; i++)
			{
			     Node node = g.getNodeByID(ids[i]);
			     nodes.add(node);
			}
		return nodes;
	}
	
	public double runOneGraph(double p, Graph g)
	{
		//System.out.println(g.getGraphSize()+"\t"+g.getGraphEdgeNum());
		//SIR.initial(g);
		double value = this.run(p, g);
		return value;
	}
	
	public double runNumNucleus(int num, double p, Graph g)
	{
		double per = 0;
		for(int i=0; i<num; i++)
		{
			for(Node node: g.getNodes())
				node.setState(Node.SUSCEPTIBLE);
			initialNuclues(g);
			per = per + run(p,g);
		}
		per = per/num;
		return per;
	}
	
	public void initial(Graph g)
	{
//		int id = 701;
//		Node node = g.getNodeByID(id);
//		node.setState(Node.INFECTIOUS);
	}
	
	//all the nodes in nuclues are infectious
	public void initialNuclues(Graph g)
	{
		int maxcore = 0;
		for(Node node: g.getNodes())
		{
			if(node.getCoreness() > maxcore) 
			   maxcore = node.getCoreness();
		}
		
		for(Node node: g.getNodes())
		{
			if(node.getCoreness() == maxcore)
			{
				node.setState(Node.INFECTIOUS);
			}
		}
	}
	
	//度最大的前n个最大的节点，节点总数等于nuclues中的数目
	public void initialLargestNodesVSNuclu(Graph g)
	{
		int maxcore = 0;
		for(Node node: g.getNodes())
		{
			if(node.getCoreness() > maxcore) 
			   maxcore = node.getCoreness();
		}
		int count = 0;
		for(Node node: g.getNodes())
		{
			if(node.getCoreness() == maxcore)
			{
				count ++;
			}
		}
		initialLargestNodes(count, g);
	}
	
    // the largest n nodes in the network are infectious
	public void initialLargestNodes(int n, Graph g)
	{
		Element[] elements = new Element[g.getGraphSize()];
		int vn = g.getGraphSize();
		
		for(int i = 0; i < vn; i++)
		{
			elements[i] = new Element(i,g.getNodes().get(i).getDegree());
		}
		
		Arrays.sort(elements);
		
		List<Node> infecNodes = new ArrayList<Node>();
		
		for(int i = 0; i < n; i++)
		{
			int index = elements[vn-1-i].index;
			infecNodes.add(g.getNodes().get(index));
		}

		infectNodes(infecNodes);
	}
	
	public void infectNodes(List<Node> nodes)
	{
		for(Node node: nodes)
		{
			node.setState(Node.INFECTIOUS);
		}
	}
	
	public double run(double p, Graph g)
	{
		List<Node> infNodebefore;
		List<Node> remNode = new ArrayList<Node>();
		while(true)
		{
			//get all the suspected nodes
			infNodebefore = new ArrayList<Node>();
			
			for(Node node: g.getNodes())
			{
				if(node.getState() == Node.INFECTIOUS)
					infNodebefore.add(node);
			}
			
			if(infNodebefore.size() == 0) {break;}
			
			//infect process
			for(Node node: infNodebefore)
			{
				node.infectNeighborSIR(p);
			}
			
			for(Node node: infNodebefore)
			{
				remNode.add(node);
			}
			
			//System.out.println(((double)remNode.size())/g.getGraphSize());
			
		}
		
		//not include initial nodes
		return(double)(remNode.size()-3);
		//return(((double)(remNode.size()-3))/g.getGraphSize());
		//return(((double)remNode.size())/g.getGraphSize());
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
             if(value<e.value)
                 return 1;
             else if(value == e.value)
                 return 0;
            else
                 return -1;
        }
    }
    
}
