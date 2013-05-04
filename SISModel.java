

import graph.Graph;
import graph.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import SIRModel.Element;


public class SISModel {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		double p = 0.04;
		double a = 0.8;
		String oriFile = "E:\\chengsq\\data\\Email-EuAll.txt";
		String corenessFile = "E:\\chengsq\\data\\Email-EuAll_CORENESS.txt";
		int num = 1;
		//Graph g = Graph.ConstructFromFile(graphFile);
		//System.out.println(g.getGraphSize()+"\t"+g.getGraphEdgeNum());
		SISModel SIS = new SISModel();
		
		int len = 5000;
		for(int step=0; step<=10*len; step=step+len)
		{
	    	String graphFile = oriFile.replace(".txt", "_"+step+".txt");
	    	Graph g = Graph.ConstructFromFile(graphFile);
	    	g.readinCoreness(corenessFile);
	    	double value = SIS.runOneGraph(num, p, a, g);
	    	System.out.println(step+"\t"+g.getRc()+"\t"+value);
		}
		//SIS.initial(g);
		//SIS.run(p,a,g);
	}

	public double runOneGraph(int num, double p, double a, Graph g)
	{
		//System.out.println(g.getGraphSize()+"\t"+g.getGraphEdgeNum());
		//SIR.initial(g);
		double value = runNumNucleus(num,p,a,g);
		return value;
	}
	
	public double runNumNucleus(int num, double p, double a, Graph g)
	{
		double per = 0;
		for(int i=0; i<num; i++)
		{
			for(Node node: g.getNodes())
				node.setState(Node.SUSCEPTIBLE);
			initialNuclues(g);
			per = per + run(p,a,g);
		}
		per = per/num;
		return per;
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
	
	public void initial(Graph g)
	{
		int id = 701;
		Node node = g.getNodeByID(id);
		node.setState(Node.INFECTIOUS);
	}
	
	public double run(double p, double a, Graph g)
	{
		int step = g.getGraphSize()/4;
		List<Node> infNode;
		double[] infP = new double[20];
		for(int i = 0; i<infP.length; i++)
			infP[i] = 0;
		
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
			else
			{
				//System.out.println(((double)infNode.size())/g.getGraphSize());
			}
			
			//infect process
			for(Node node: infNode)
			{
				node.infectNeighborSIS(p, a);
			}
			step --;
			if(step < 20)
			{
				double t = ((double)infNode.size())/g.getGraphSize();
				//System.out.println(t);
				infP[step] = t;
			}
		    if(step == 0) break; 
		}
		
		for(int i=1; i<20; i++)
			infP[0] = infP[0] + infP[i];
		//System.out.println("final:\t"+infP[0]/20);
		return infP[0]/20;
	}


}
