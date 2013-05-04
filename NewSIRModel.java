

import java.util.ArrayList;
import java.util.List;

import graph.Graph;
import graph.Node;

public class NewSIRModel {

	/**
	 * @param args
	 * 2011-06-30
	 * chengsuqi
	 */
	Graph graph;
	ModelParameter modelParameter;
	SourceStrategy sourceStrategy;
	ImmunityStrategy immunityStrategy;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String netFile = "F:\\SpreadModel\\Email-Enron_RW_36082_200.txt";
		
//		double infectPro = 0.2;
//		double recoverPro = 1.0;
//		ModelParameter para = new ModelParameter(infectPro, recoverPro);
//		
//		double sourcePercent = 0.02;
//		int sourceKind = SourceStrategy.RANDOM;
//		SourceStrategy sourceStrategy = new SourceStrategy(sourceKind, sourcePercent);
//
//		double immunityPercent = 0.0; 
//		int immunityKind = ImmunityStrategy.MAXCORENESS;
//		ImmunityStrategy immunityStrategy = new ImmunityStrategy(immunityKind, immunityPercent);
//		
		
		double infectPro;
		double recoverPro = 1.0;
		ModelParameter para;
		
		double sourcePercent;
		int sourceKind;
		SourceStrategy sourceStrategy;

		double immunityPercent; 
		int immunityKind;
		ImmunityStrategy immunityStrategy;
		int instance = 500;
		
		infectPro = 0.2;
		
		sourcePercent = 0.01;
		sourceKind = SourceStrategy.RANDOM;
		
		System.out.println("infectPro: "+infectPro);
		System.out.println("source-percent: "+sourcePercent);
		//System.out.println("non-immunity");
		immunityPercent = 0;
		immunityKind = ImmunityStrategy.MAXDEGREE;
		immunityStrategy = new ImmunityStrategy(immunityKind, immunityPercent);
		sourceStrategy = new SourceStrategy(sourceKind, sourcePercent);
		immunityStrategy = new ImmunityStrategy(immunityKind, immunityPercent);
		para = new ModelParameter(infectPro, recoverPro);
		test(instance, netFile, para, sourceStrategy, immunityStrategy);
		
		//System.out.println("ImmunityStrategy.MAXDEGREE");
		immunityKind = ImmunityStrategy.MAXDEGREE;
		
    		immunityPercent = 0.05;
    		immunityStrategy = new ImmunityStrategy(immunityKind, immunityPercent);
	    	immunityStrategy = new ImmunityStrategy(immunityKind, immunityPercent);
     		sourceStrategy = new SourceStrategy(sourceKind, sourcePercent);
    		immunityStrategy = new ImmunityStrategy(immunityKind, immunityPercent);
	    	para = new ModelParameter(infectPro, recoverPro);
	    	test(instance, netFile, para, sourceStrategy, immunityStrategy);
		
	    	immunityPercent = 0.1;
	    	immunityStrategy = new ImmunityStrategy(immunityKind, immunityPercent);
     		immunityStrategy = new ImmunityStrategy(immunityKind, immunityPercent);
	    	sourceStrategy = new SourceStrategy(sourceKind, sourcePercent);
	    	immunityStrategy = new ImmunityStrategy(immunityKind, immunityPercent);
    		para = new ModelParameter(infectPro, recoverPro);
    		test(instance, netFile, para, sourceStrategy, immunityStrategy);
		
	    	immunityPercent = 0.15;
	    	immunityStrategy = new ImmunityStrategy(immunityKind, immunityPercent);
	    	immunityStrategy = new ImmunityStrategy(immunityKind, immunityPercent);
	    	sourceStrategy = new SourceStrategy(sourceKind, sourcePercent);
    		immunityStrategy = new ImmunityStrategy(immunityKind, immunityPercent);
	    	para = new ModelParameter(infectPro, recoverPro);
	    	test(instance, netFile, para, sourceStrategy, immunityStrategy);
		
	    //System.out.println("ImmunityStrategy.MAXBC");
	 	immunityKind = ImmunityStrategy.MAXBC;
      		immunityPercent = 0.05;
     		immunityStrategy = new ImmunityStrategy(immunityKind, immunityPercent);
    		immunityStrategy = new ImmunityStrategy(immunityKind, immunityPercent);
	      	sourceStrategy = new SourceStrategy(sourceKind, sourcePercent);
	      	immunityStrategy = new ImmunityStrategy(immunityKind, immunityPercent);
 	    	para = new ModelParameter(infectPro, recoverPro);
      		test(instance, netFile, para, sourceStrategy, immunityStrategy);
		
      		immunityPercent = 0.1;
       		immunityStrategy = new ImmunityStrategy(immunityKind, immunityPercent);
      		immunityStrategy = new ImmunityStrategy(immunityKind, immunityPercent);
     		sourceStrategy = new SourceStrategy(sourceKind, sourcePercent);
       		immunityStrategy = new ImmunityStrategy(immunityKind, immunityPercent);
      		para = new ModelParameter(infectPro, recoverPro);
      		test(instance, netFile, para, sourceStrategy, immunityStrategy);
		
       		immunityPercent = 0.15;
      		immunityStrategy = new ImmunityStrategy(immunityKind, immunityPercent);
     		immunityStrategy = new ImmunityStrategy(immunityKind, immunityPercent);
     		sourceStrategy = new SourceStrategy(sourceKind, sourcePercent);
     		immunityStrategy = new ImmunityStrategy(immunityKind, immunityPercent);
     		para = new ModelParameter(infectPro, recoverPro);
      		test(instance, netFile, para, sourceStrategy, immunityStrategy);
      		
	    //System.out.println("ImmunityStrategy.MAXCORENESS");	
      	immunityKind = ImmunityStrategy.MAXCORENESS;
      		immunityPercent = 0.05;
     		immunityStrategy = new ImmunityStrategy(immunityKind, immunityPercent);
    		immunityStrategy = new ImmunityStrategy(immunityKind, immunityPercent);
	      	sourceStrategy = new SourceStrategy(sourceKind, sourcePercent);
	      	immunityStrategy = new ImmunityStrategy(immunityKind, immunityPercent);
 	    	para = new ModelParameter(infectPro, recoverPro);
      		test(instance, netFile, para, sourceStrategy, immunityStrategy);
		
      		immunityPercent = 0.1;
       		immunityStrategy = new ImmunityStrategy(immunityKind, immunityPercent);
      		immunityStrategy = new ImmunityStrategy(immunityKind, immunityPercent);
     		sourceStrategy = new SourceStrategy(sourceKind, sourcePercent);
       		immunityStrategy = new ImmunityStrategy(immunityKind, immunityPercent);
      		para = new ModelParameter(infectPro, recoverPro);
      		test(instance, netFile, para, sourceStrategy, immunityStrategy);
		
       		immunityPercent = 0.15;
      		immunityStrategy = new ImmunityStrategy(immunityKind, immunityPercent);
     		immunityStrategy = new ImmunityStrategy(immunityKind, immunityPercent);
     		sourceStrategy = new SourceStrategy(sourceKind, sourcePercent);
     		immunityStrategy = new ImmunityStrategy(immunityKind, immunityPercent);
     		para = new ModelParameter(infectPro, recoverPro);
      		test(instance, netFile, para, sourceStrategy, immunityStrategy);
      		
    	//System.out.println("ImmunityStrategy.RANDOM");	
      	immunityKind = ImmunityStrategy.RANDOM;
      		immunityPercent = 0.05;
     		immunityStrategy = new ImmunityStrategy(immunityKind, immunityPercent);
    		immunityStrategy = new ImmunityStrategy(immunityKind, immunityPercent);
	      	sourceStrategy = new SourceStrategy(sourceKind, sourcePercent);
	      	immunityStrategy = new ImmunityStrategy(immunityKind, immunityPercent);
 	    	para = new ModelParameter(infectPro, recoverPro);
      		test(instance, netFile, para, sourceStrategy, immunityStrategy);
		
      		immunityPercent = 0.1;
       		immunityStrategy = new ImmunityStrategy(immunityKind, immunityPercent);
      		immunityStrategy = new ImmunityStrategy(immunityKind, immunityPercent);
     		sourceStrategy = new SourceStrategy(sourceKind, sourcePercent);
       		immunityStrategy = new ImmunityStrategy(immunityKind, immunityPercent);
      		para = new ModelParameter(infectPro, recoverPro);
      		test(instance, netFile, para, sourceStrategy, immunityStrategy);
		
       		immunityPercent = 0.15;
      		immunityStrategy = new ImmunityStrategy(immunityKind, immunityPercent);
     		immunityStrategy = new ImmunityStrategy(immunityKind, immunityPercent);
     		sourceStrategy = new SourceStrategy(sourceKind, sourcePercent);
     		immunityStrategy = new ImmunityStrategy(immunityKind, immunityPercent);
     		para = new ModelParameter(infectPro, recoverPro);
      		test(instance, netFile, para, sourceStrategy, immunityStrategy);
	}

	public static void test(int instance, String netFile, ModelParameter para, SourceStrategy sourceStrategy, ImmunityStrategy immunityStrategy){
		double sum = 0;
		double[] values = new double[instance];
		for(int i = 0; i < instance; i++){
	    	NewSIRModel model = new NewSIRModel(netFile, para, sourceStrategy, immunityStrategy);
	    	model.initial();
	    	//double value = model.runSIR();
	    	values[i] = model.runSIR();
	    	sum = sum + values[i];
		}
		double aveV = sum/instance;
		double standardV = 0;
		for(int i=0; i<instance; i++){
			standardV += (values[i]-aveV)*(values[i]-aveV);
		}
		standardV = Math.sqrt(standardV/instance);
		java.text.DecimalFormat  df = new java.text.DecimalFormat("#.000");  
		System.out.println(df.format(aveV)+"\t"+df.format(standardV));
	}
	
	public double runSIR(){
		return runSIR(modelParameter.infectPro, modelParameter.recoverPro, graph);
	}
	
	public double runSIS(){
		return runSIS(modelParameter.infectPro, modelParameter.recoverPro, graph);
	}
	
	public double runSIR(double infect, double recover, Graph g)
	{
		List<Node> infNode;
		List<Node> remNodes = new ArrayList<Node>();
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
			List<Node> preInfectNodes = new ArrayList<Node>();
			for(Node node: infNode)
			{
				node.infectNeighborSIR(infect, preInfectNodes);
			}
			
			for(Node node: preInfectNodes){
				node.setState(Node.INFECTIOUS);
			}
			
			int count = 0;
			//recover process
			for(Node node: infNode)
			{
				if(node.recoverSIR(recover)){
					if(remNodes.contains(node))
						System.out.println("repeat recover node, wrong!");
					else 
						{remNodes.add(node);count++;}
				}
			}
			
			//System.out.println("step: "+ preInfectNodes.size() +"  "+count);
		}

		//not include initial nodes
		
		return(((double)(remNodes.size()))/g.getGraphSize());
	}
	
	
	public double runSIS(double infect, double recover, Graph g)
	{
		List<Node> infNode;
		
		int step = g.getGraphSize();
		double[] infP = new double[20];
		for(int i = 0; i<infP.length; i++)
			infP[i] = 0;
		boolean flag = true;
		while(true)
		{
			//get all the suspected nodes
			infNode = new ArrayList<Node>();
			for(Node node: g.getNodes())
			{
				if(node.getState() == Node.INFECTIOUS)
					infNode.add(node);
			}
			if(step <= 20) infP[step-1] = infNode.size();
			if(infNode.size() == 0) {flag = false; break;}
			//System.out.println("infect range: "+ infNode.size());
			//infect process
			List<Node> preInfectNodes = new ArrayList<Node>();
			for(Node node: infNode)
			{
				node.infectNeighborSIR(infect, preInfectNodes);
			}
			
			for(Node node: preInfectNodes){
				node.setState(Node.INFECTIOUS);
			}

			//recover process
			for(Node node: infNode)
			{
				node.recoverSIS(recover);
			}
			step --;
			if(step == 0) break;
		}

		if(flag == false) return 0;
		for(int i = 1; i < infP.length; i++ ){
			infP[0] = infP[0]+infP[i];
		}
		double aveS = ((double)infP[0])/20;
		return(aveS/g.getGraphSize());
	}
	
	public NewSIRModel(String netFile, ModelParameter para, SourceStrategy sourceStrategy, ImmunityStrategy immunityStrategy){
		graph = LoadGraph(netFile);
		this.modelParameter = para;
		this.sourceStrategy = sourceStrategy;
		this.sourceStrategy.setGraph(graph);
		this.immunityStrategy = immunityStrategy;
		this.immunityStrategy.setGraph(graph);
	}
	
	public void initial(){
		//set sourcenodes
        List<Node> infecNodes = sourceStrategy.getSourceNodes();
		infectNodes(infecNodes);
		
		//set immunitynodes
		List<Node> immunitynodes = immunityStrategy.getImmuniityNodes();
		immunityNodes(immunitynodes);
	}
	
	public void infectNodes(List<Node> nodes)
	{
		for(Node node: nodes)
		{
			node.setState(Node.INFECTIOUS);
		}
	}
	
	public void immunityNodes(List<Node> nodes)
	{
		for(Node node: nodes)
		{
			node.setState(Node.RECOVERED);
		}
	}
	
	public Graph LoadGraph(String graphFile){
		//readin graph
		Graph g = Graph.ConstructFromFile(graphFile);
		//readin node properties
		String corenessFile = graphFile.replace(".txt", "_coreness.txt");
		String BCFile = graphFile.replace(".txt", "_betweenness.txt");
		g.readinCorenessNew(corenessFile);
		g.readinBC(BCFile);
		return g;
	}
	
}
