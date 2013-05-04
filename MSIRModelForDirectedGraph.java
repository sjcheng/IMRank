

import java.util.List;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

/*
 * we employ a variant of the SIR model to examine the spreading influence of the top-ranked users [16]. 
 * At each step, from every infected individual, one randomly selected fan gets infected with probability p,
 * which resembles the direction of information flow. Infected individuals recover with probability 1/<kin>
 *  at each step, where <kin> is the average in-degree of all users. 
 * 
 */

public class MSIRModelForDirectedGraph {

	/**
	 * @param args
	 */
	
	double infec;
	double recov;
	
	//DirectedGraph dg;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String root = "C:\\Experiments\\influence\\";
		String gfile = "douban.txt";
		gfile = "testG.txt";
		String rsfile;
		String rsdetailfile;
		DirectedGraph dg = DirectedGraph.ConstructFromFile(new File(root + gfile));
		double infec = 0.4;
		double recov = 0.2;
		
		MSIRModelForDirectedGraph sir = new MSIRModelForDirectedGraph(infec, recov);
		//int[] sourceid = {394360,274725,331024,394360};
		int[] sourceid = {1,2,3,4,5};
		int sample = 5;
		Node source = null;
		for(int i=0; i<sourceid.length; i++){
			source = dg.getNodeByID(sourceid[i]);
			rsfile = root+i+"_singleNode_inf_"+sourceid[i]+".txt";
			rsdetailfile = root+"singleNode_inf_"+sourceid[i]+"_detail.txt";
			rsdetailfile = "";
     		sir.influenceofSingleNode(source, dg, rsfile, rsdetailfile, sample);
		};
		/*an instant*/
//		sir.initialGraph(dg);
//		sir.setSingleSource(dg.getNodeByID(sourceid));
//		sir.singleInfectedProcess(dg, rsfile, rsdetailfile);
	}

	
	
	//set recovery as 1/<kin>
	public void setRecov(DirectedGraph dg){
		double inDegree = 0;
		for(Node node: dg.getNodes()){
			inDegree = inDegree + node.getInDegree();
		}
		inDegree = inDegree/dg.getGraphSize();
		this.recov = 1.0/inDegree;
		System.out.println("recov = "+ this.recov);
	}
	
	public void setRecov(double recov){
		this.recov = recov;
	}
	
	public MSIRModelForDirectedGraph(double infec, double recov){
		this.infec = infec;
		this.recov = recov;
	}
	
	public double influenceofSingleNode(Node source, DirectedGraph dg, String rsfile, String rsdetailfile, int sample){
		
		double sum = 0;
		
		PrintWriter outrs = null;
		PrintWriter outrsdetail = null;
		
		try{
			outrs = new PrintWriter(new BufferedWriter(new FileWriter(rsfile)));
			if(rsdetailfile!="")
		    	outrsdetail = new PrintWriter(new BufferedWriter(new FileWriter(rsdetailfile)));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		for(int i=0; i<sample; i++){
			initialGraph(dg);
			setSingleSource(source);
			//double temp = singleInfectedProcess(dg, outrs, outrsdetail);
			double temp = singleInfectedProcess(dg, outrs, outrsdetail);
			sum = sum + temp;
		}
		
		try{
			outrs.close();
			if(outrsdetail!=null)
		    	outrsdetail.close();
		}catch(Exception e){
			e.printStackTrace();
		}

		System.out.println(source.getNodeID()+"\t"+sum/sample);
		return sum/sample;
	}
	
	public int singleInfectedProcess(DirectedGraph dg, PrintWriter outrs, PrintWriter outrsdetail){
	
		outrs.println("One sample:");
		if(outrsdetail!=null)
          outrsdetail.println("One sample:");		
		
    	int recoveredNum = 0;
    	List<Node> infectedNode = new ArrayList<Node>();
    	
    	for(Node node: dg.getNodes()){
    		if(node.pre_status == Node.INFECTIOUS)
    			infectedNode.add(node);
    	}
    	
    	List<Node> afterInfectedNode;
    	List<Node> afterRecoveredNode;
    	int step = 0;
		while(infectedNode.size() > 0){
			//System.out.println(step+"\t"+infectedNode.size()+"\t"+recoveredNum);
			outrs.println("step "+step+" : "+infectedNode.size()+"  "+recoveredNum+"  "+(infectedNode.size()+recoveredNum));
			step++;
			if(outrsdetail!=null)
		    	outrsdetail.println("step "+step+" :");
			afterInfectedNode = new ArrayList<Node>();
			afterRecoveredNode  = new ArrayList<Node>();
	    	for(Node node: infectedNode){
                //recover
    	 		if(Math.random() < recov){
		    		//node.pre_status = Node.RECOVERED;
		    		afterRecoveredNode.add(node);
		    		recoveredNum ++;
    	 		}
    	 		
	    		//only choose one fan to infect
	    		if(node.getInDegree() == 0 )
	    				continue;
	    		
	    		int randomIndex = (int)(node.getInDegree()*Math.random());
	    		Node fan = node.getFans().get(randomIndex);
	    		if(!fan.canbeInfected()) continue;
	    		if(afterInfectedNode.contains(fan)) continue;
	    		//infect 
	    		if(Math.random() < infec){
	    		    afterInfectedNode.add(fan);
	    		    if(outrsdetail!=null)
	    		       outrsdetail.print(node.getNodeID()+ "("+node.getInDegree()+","+node.getOutDegree()+") -> "+fan.getNodeID()+"("+fan.getInDegree()+","+fan.getOutDegree()+");");
	    		}
	    		
    		}
	    	if(outrsdetail!=null){
	    	outrsdetail.println();
	    	outrsdetail.print("recovered ("+afterRecoveredNode.size()+","+recoveredNum+")"+":  ");}
	    	for(Node node: afterRecoveredNode){
	    		node.pre_status = Node.RECOVERED;
	    		infectedNode.remove(node);
	    		if(outrsdetail!=null)
	    		outrsdetail.print(node.getNodeID()+"("+node.getInDegree()+","+node.getOutDegree()+")  ");
	    	}
	    	if(outrsdetail!=null){
	    	outrsdetail.println();
	    	outrsdetail.print("Infected ("+afterInfectedNode.size()+","+(infectedNode.size()+afterInfectedNode.size()-afterRecoveredNode.size())+")");}
	    	for(Node node: afterInfectedNode){
	    		node.pre_status = Node.INFECTIOUS;
	    		infectedNode.add(node);
	    		//outrsdetail.print(node.getNodeID()+"("+node.getInDegree()+","+node.getOutDegree()+")  ");
	    	}
	    	if(outrsdetail!=null)
	        	outrsdetail.println();
	    }

		afterInfectedNode = null;
		afterRecoveredNode = null;
		return recoveredNum;
	}
	
	/*set the statuses of all the nodes as susceptible*/
	public void initialGraph(DirectedGraph dg){
		for(Node node: dg.getNodes()){
			node.pre_status = Node.SUSCEPTIBLE;
			//node.after_status = -1;
		}
	}
	
	/*set several nodes as sources*/
	public void setSource(List<Node> sources){
		for(Node node: sources){
			node.pre_status = Node.INFECTIOUS;
			//node.after_status = -1;
		}
	}
	
	/*set single node as a source*/
	public void setSingleSource(Node source){
		source.pre_status = Node.INFECTIOUS;
		//source.after_status = -1;
	}
}
