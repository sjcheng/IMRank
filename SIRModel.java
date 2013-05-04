
import graph.Node;
import graph.Graph;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class SIRModel {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		double p = 0.01;
		int num = 3;
		String oriFile = "E:\\chengsq\\data\\Email-EuAll.txt";
		String corenessFile = "E:\\chengsq\\data\\Email-EuAll_CORENESS.txt";
		SIRModel SIR = new SIRModel();
		//SIR.initial(g);
		int len = 5000;
		for(int step=0; step<=10*len; step=step+len)
		{
	    	String graphFile = oriFile.replace(".txt", "_"+step+".txt");
	    	Graph g = Graph.ConstructFromFile(graphFile);
	    	g.readinCoreness(corenessFile);
	    	double value = SIR.runOneGraph(num, p, g);
	    	System.out.println(step+"\t"+g.getRc()+"\t"+value);
		}
	}

	public double runOneGraph(int num, double p, Graph g)
	{
		//System.out.println(g.getGraphSize()+"\t"+g.getGraphEdgeNum());
		//SIR.initial(g);
		double value = runNumNucleus(num,p,g);
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
		List<Node> infNode;
		List<Node> remNode = new ArrayList<Node>();
		while(true)
		{
			//get all the suspected nodes
			infNode = new ArrayList<Node>();
			for(Node node: g.getNodes())
			{
				if(node.getState() == Node.INFECTIOUS)
					infNode.add(node);
			}
			
			if(infNode.size() == 0) {break;}
			
			//infect process
			for(Node node: infNode)
			{
				node.infectNeighborSIR(p);
			}
			
			for(Node node: infNode)
			{
				remNode.add(node);
			}
			
			//System.out.println(((double)remNode.size())/g.getGraphSize());
			
		}
		
		//not include initial nodes
		int maxcore = g.getMaxcore();
		int maxcoreSize = 0;
		for(Node node: g.getNodes())
		{
			if(node.getCoreness() == maxcore)
				maxcoreSize ++;
		}
		return(((double)(remNode.size()-maxcoreSize))/g.getGraphSize());
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
