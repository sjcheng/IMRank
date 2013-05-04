import java.util.List;
import java.util.StringTokenizer;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;

/*
 * Tranditional SIR model 
 * 
 */

public class TSIRModelDirectedGraph {

	/**
	 * @param args
	 */
/*
 * tranditional SIR model
 */
	static double infec;
	static double recov;
	public static String root = "C:\\Experiments\\influence\\";
	// DirectedGraph dg;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		//String rank = Rank.PAGERANK;
		String gfile = "slashdot.txt";
		//String gfile = args[0];
		//infec = Double.parseDouble(args[1]);
		infec = 0.01;
		//recov = Double.parseDouble(args[2]);
		recov = 1;
		TSIRModelDirectedGraph test = new TSIRModelDirectedGraph();
		test.testsinglenode(gfile, infec, recov);
	    //tsir.runInstanceTopk(50000,infec, recov, gfile, rank, dg);
	}
	
	public TSIRModelDirectedGraph(){
		
	}
	
	public void testsinglenode(String network, double infec, double recov){
		DirectedGraph dg = DirectedGraph.ConstructFromFile(new File(root + network)); 
		TSIRModelDirectedGraph tsir = new TSIRModelDirectedGraph(infec, recov);
		
		Node[] sources = null;
		if(network.equals("slashdot.txt")){
	    	sources = tsir.getTestSingleNodesSlashdot(dg);
		}else if(network.equals("epinions.txt")){
			sources = tsir.getTestSingleNodesEpinions(dg);
		}else if(network.equals("douban.txt")){
			sources = tsir.getTestSingleNodesDouban(dg);
		}else if(network.equals("hetph-cit.txt")){
			sources = tsir.getTestSingleNodesHepCit(dg);
		}else if(network.equals("goodreads.txt")){
			sources = tsir.getTestSingleNodesGoodreads(dg);
		}
//		infec = 0.02;//0.01;
//		recov = 1.0;
		tsir.runInstance(sources, infec, recov, network, dg);
	}
	
	public Node[] getTestSingleNodesSlashdot(DirectedGraph dg){
		int[] nodeid_slashdot = {1152,1701,70,3,2813,76,949,12517,9,1093,940,1239,86,629,4864,754,1964,
		92,1111,450,2455,2452,1183,922,40018,501,41479,1125,641,3657,50,48,733,54,13642,1143,661,56};
		Node[] singleNodes = new Node[nodeid_slashdot.length];
		for(int i=0; i<nodeid_slashdot.length; i++)
			singleNodes[i] = dg.getNodeByID(nodeid_slashdot[i]);
		return singleNodes;
	}
	
	public Node[] getTestSingleNodesGoodreads(DirectedGraph dg){
		int[] nodeid_goodreads = {285721, 11150, 187410, 128840, 258034, 1389, 30759, 98741,
				242610, 156142, 51704, 228325, 36033, 290374, 258352};
		Node[] singleNodes = new Node[nodeid_goodreads.length];
		for(int i=0; i<nodeid_goodreads.length; i++)
			singleNodes[i] = dg.getNodeByID(nodeid_goodreads[i]);
		return singleNodes;
	}
	
	public Node[] getTestSingleNodesHepCit(DirectedGraph dg){
		int[] nodeid_hepcit = {9206203, 9311214, 9205221, 9206205, 9408384, 9603249,
		9205228, 9205227, 9606211, 9205238, 9502336, 9508347, 9306212, 9204225, 9508343,
		9211266, 9908362, 9303281, 9908363, 9801271, 9612313, 9803445, 9305306, 9306320,
		9506380, 9507378, 9807344, 9811291, 9211309, 9512380, 9308246, 9905221, 9207234,
		9303255, 9807216, 9606399, 9209268, 9310316, 9903282, 9306309, 9309289, 9212204,
		9207219, 9711395, 9212206, 9711396, 9502298, 9212203, 9604387, 9306289, 9609381, 
		9207228, 9704207, 9204215, 9210253, 9803466, 9905312, 9206242, 9806292, 9207213,
		9403338, 9206236, 9802290, 9203213, 9302210, 9401310, 9211256, 9308292, 9203203, 
		9203201, 9806404, 9305228, 9304257, 9203220, 9402253, 9208254, 9208249, 9705442,
		9406315};
		
		Node[] singleNodes = new Node[nodeid_hepcit.length];
		for(int i=0; i<nodeid_hepcit.length; i++)
			singleNodes[i] = dg.getNodeByID(nodeid_hepcit[i]);
		return singleNodes;
	}
	
	public Node[] getTestSingleNodesDouban(DirectedGraph dg){
		int[] nodeid_douban = {102786, 218288, 458601, 434030, 258959, 526022, 327260,
				469461, 449539, 223897, 113125, 30662, 186110, 81164, 379959, 476506, 511100,
				313192, 316900, 420274, 194766};
		
		Node[] singleNodes = new Node[nodeid_douban.length];
		for(int i=0; i<nodeid_douban.length; i++)
			singleNodes[i] = dg.getNodeByID(nodeid_douban[i]);
		return singleNodes;
	}
	
	
	
	public Node[] getTestSingleNodesEpinions(DirectedGraph dg){
		int[] nodeid_epinions = {11597,33931, 549, 414, 3343, 4948, 4542, 1502, 131, 2738,
				6800, 3749, 20, 1955, 219, 3147, 267, 10369, 1891, 876, 1287, 1345, 1882,
				3253, 1397, 16973, 3847, 1328, 161, 2293, 382, 106, 5592, 2285, 5148, 17354,
				1682, 666, 1313, 2363, 57, 1274, 966, 2842};
		Node[] singleNodes = new Node[nodeid_epinions.length];
		for(int i=0; i<nodeid_epinions.length; i++)
			singleNodes[i] = dg.getNodeByID(nodeid_epinions[i]);
		return singleNodes;
	}
	
	
	public void runInstanceTopk(int topk, double infec, double recov, String gfile, String rank, DirectedGraph dg){
		//Node source = null;
		List<Node> sources = this.getTopkNodesList(dg, topk);
		//System.out.println(sources.size());
		int sample = 1;
		String rsfile = root + gfile.replace(".txt","_") + "TOPKT_infec"+infec+"_recov"+recov+"_"+rank+"_singleNode_inf.txt";
		String rssamplefile = root + gfile.replace(".txt","_") + "TOPKT_infec"+infec+"_recov"+recov+"_"+rank+"_singleNode_inf_sample.txt";
		String rsdetailfile = root + gfile.replace(".txt","_") + "TOPKT_infec"+infec+"_recov"+recov+"_"+rank+"_singleNode_inf_detail.txt";
		influenceofNodes(sources, dg, rsfile, rssamplefile,
					rsdetailfile, sample);
		sources = null;
	}
	
	
	public void runInstance(Node[] sources, double infec, double recov, String gfile, DirectedGraph dg){
		Node source = null;
		int sample = 20000;
		for (int i = 0; i < sources.length; i++) {
			source = sources[i];
			String rsfile = root + gfile.replace(".txt","_") + "TOPKT_infec"+infec+"_recov"+recov+"_singleNode_inf.txt";
			String rssamplefile = root + gfile.replace(".txt","_") + "TOPKT_infec"+infec+"_recov"+recov+"_singleNode_inf_sample.txt";
			String rsdetailfile = root + gfile.replace(".txt","_") + "TOPKT_infec"+infec+"_recov"+recov+"_singleNode_inf_detail.txt";
			influenceofSingleNode(source, dg, rsfile, rssamplefile,
					rsdetailfile, sample);
		};
		source = null;
	}
	
	public void getRankfile(String root, String gfile, String rankkind, DirectedGraph dg){
		
		String rankfile = root + gfile.replace(".txt", "_"+rankkind+".txt");
		//rankfile = root + "as_cn_only_MGPR.txt";//epinions_newPageRank.txt
        try
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(rankfile)));
            String line ;
            int count=0;
            while((line=br.readLine())!=null)
            {
                count++;
                line = line.trim() ;
                if(line.length()==0)
                {
                    System.out.println("line length is zero");
                   continue;
                }
                //line = line.replace('-',' ');
                StringTokenizer st = new StringTokenizer(line);
                int nodeid;
                double score;
                int rank;
                try{
                	nodeid = Integer.parseInt(st.nextToken());
                	score = Double.parseDouble(st.nextToken());
                	rank = Integer.parseInt(st.nextToken());
                	
                	Node node = dg.getNodeByID(nodeid);
                	node.rspre = score;
                	node.rank = rank;
//                	if(node.rank == -1)
//                		System.out.println();
                }catch(Exception e){
                	e.printStackTrace();
                }
                
            }
        }catch(Exception e){
        	e.printStackTrace();
        }
	}
	
	public Node[] getTopkNodes(DirectedGraph dg, int topk){
		Node[] topknodes = new Node[topk];
		for(Node node:dg.getNodes()){
			if(node.rank > topk) 
				continue;
			topknodes[node.rank-1] = node;
		}
		return topknodes;
	}
	
	public List<Node> getTopkNodesList(DirectedGraph dg, int topk){
		
		//System.out.println(topk);
		List<Node> topknodes = new ArrayList();
		for(Node node:dg.getNodes()){
			if(node.rank > topk) 
				continue;
			topknodes.add(node);
			//System.out.println(node.getNodeID()+"\t"+node.getRank()+"\t"+node.rspre);
		}
		//System.out.println(topknodes.size());
		return topknodes;
	}
	
	// set recovery as 1/<kin>
	public void setRecov(DirectedGraph dg) {
		double inDegree = 0;
		for (Node node : dg.getNodes()) {
			inDegree = inDegree + node.getInDegree();
		}
		inDegree = inDegree / dg.getGraphSize();
		this.recov = 1.0 / inDegree;
		System.out.println("recov = " + this.recov);
	}

	public void setRecov(double recov) {
		this.recov = recov;
	}

	public TSIRModelDirectedGraph(double infec, double recov) {
		this.infec = infec;
		this.recov = recov;
	}

	public double influenceofSingleNode(Node source, DirectedGraph dg,
			String rsfile, String rssamplefile, String rsdetailfile, int sample) {

		double sum = 0;
		double sum_per = 0;
		int[] num = new int[sample];
		double[] per = new double[sample];

		PrintWriter outrs = null;
		PrintWriter outrsdetail = null;
		PrintWriter outrssample = null;
		
		try {
			outrs = new PrintWriter(new BufferedWriter(new FileWriter(rsfile,true)));
			outrsdetail = new PrintWriter(new BufferedWriter(
						new FileWriter(rsdetailfile,true)));
			outrssample = new PrintWriter(new BufferedWriter(new FileWriter(
					rssamplefile,true)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		outrssample.append("-----------source= "+source.getNodeID()+" -------------");
		outrssample.append("\r\n");
		outrsdetail.append("-----------source= "+source.getNodeID()+" -------------");
		outrsdetail.append("\r\n");
		
		for (int i = 0; i < sample; i++) {
			outrsdetail.append("sample = "+i);
			outrsdetail.append("\r\n");
			initialGraph(dg);
			setSingleSource(source);
			num[i] = singleInfectedProcess(dg, outrsdetail);
			per[i] = ((double)num[i]) / dg.getGraphSize();
			sum = sum + num[i];
			sum_per = sum_per + per[i];
			outrssample.append("S"+i + "\t" + num[i] + "\t" + per[i]);
			outrssample.append("\r\n");
			outrsdetail.append("S"+i + "\t" + num[i] + "\t" + per[i]);
			outrsdetail.append("\r\n");
		}
		
		double div = 0;
		double div_per = 0;
		double ave = sum / sample;
		double ave_per = sum_per / sample;
		for (int i = 0; i < sample; i++) {
			div = div + Math.pow((num[i] - ave), 2);
			div_per = div_per + Math.pow((per[i] - ave_per), 2);
		}
		div = Math.sqrt(div / sample);
		div_per = Math.sqrt(div_per / sample);

		outrs.append(source.getNodeID() + "\t" + ave + "\t" + div + "\t"
				+ ave_per + "\t" + div_per);
		outrs.append("\r\n");
		outrssample.append(source.getNodeID() + "\t" + ave + "\t" + div + "\t"
				+ ave_per + "\t" + div_per);
		outrssample.append("\r\n");
		outrsdetail.append(source.getNodeID() + "\t" + ave + "\t" + div + "\t"
				+ ave_per + "\t" + div_per);
		outrsdetail.append("\r\n");
		
		try {
			outrs.close();
			outrsdetail.close();
			outrssample.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println(source.getNodeID() + "\t" + ave + "\t" + div + "\t"
				+ ave_per + "\t" + div_per);
		return ave;
	}

	public double influenceofNodes(List<Node> sources, DirectedGraph dg,
			String rsfile, String rssamplefile, String rsdetailfile, int sample) {

		double sum = 0;
		double sum_per = 0;
		int[] num = new int[sample];
		double[] per = new double[sample];

		PrintWriter outrs = null;
		PrintWriter outrsdetail = null;
		PrintWriter outrssample = null;
		
		try {
			outrs = new PrintWriter(new BufferedWriter(new FileWriter(rsfile,true)));
			outrsdetail = new PrintWriter(new BufferedWriter(
						new FileWriter(rsdetailfile,true)));
			outrssample = new PrintWriter(new BufferedWriter(new FileWriter(
					rssamplefile,true)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		outrssample.append("-----------source= top "+sources.size()+" -------------");
		outrssample.append("\r\n");
		outrsdetail.append("-----------source= top "+sources.size()+" -------------");
		outrsdetail.append("\r\n");
		
		for (int i = 0; i < sample; i++) {
			outrsdetail.append("sample = "+i);
			outrsdetail.append("\r\n");
			initialGraph(dg);
			setSources(sources);
			num[i] = singleInfectedProcess(dg, outrsdetail);
			per[i] = ((double)num[i]) / dg.getGraphSize();
			sum = sum + num[i];
			sum_per = sum_per + per[i];
			outrssample.append("S"+i + "\t" + num[i] + "\t" + per[i]);
			outrssample.append("\r\n");
			outrsdetail.append("S"+i + "\t" + num[i] + "\t" + per[i]);
			outrsdetail.append("\r\n");
		}
		
		double div = 0;
		double div_per = 0;
		double ave = sum / sample;
		double ave_per = sum_per / sample;
		for (int i = 0; i < sample; i++) {
			div = div + Math.pow((num[i] - ave), 2);
			div_per = div_per + Math.pow((per[i] - ave_per), 2);
		}
		div = Math.sqrt(div / sample);
		div_per = Math.sqrt(div_per / sample);

		outrs.append(sources.size() + "\t" + ave + "\t" + div + "\t"
				+ ave_per + "\t" + div_per);
		outrs.append("\r\n");
		outrssample.append(sources.size() + "\t" + ave + "\t" + div + "\t"
				+ ave_per + "\t" + div_per);
		outrssample.append("\r\n");
		outrsdetail.append(sources.size() + "\t" + ave + "\t" + div + "\t"
				+ ave_per + "\t" + div_per);
		outrsdetail.append("\r\n");
		
		try {
			outrs.close();
			outrsdetail.close();
			outrssample.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println(sources.size() + "\t" + ave + "\t" + div + "\t"
				+ ave_per + "\t" + div_per);
		return ave;
	}
	
	public int singleInfectedProcess(DirectedGraph dg, PrintWriter outrsdetail) {

		int recoveredNum = 0;
		List<Node> infectedNode = new ArrayList<Node>();

		for (Node node : dg.getNodes()) {
			if (node.pre_status == Node.INFECTIOUS){
				infectedNode.add(node);
				//System.out.println("source "+node.getNodeID());
			}
		}
		
		List<Node> afterInfectedNode;
		List<Node> afterRecoveredNode;
		int step = 0;
		while (infectedNode.size() > 0) {
			outrsdetail.append("T" + step + " :\t" + infectedNode.size() + "\t"
					+ recoveredNum + "\t"
					+ (infectedNode.size() + recoveredNum));
			outrsdetail.append("\r\n");
			step++;
			afterInfectedNode = new ArrayList<Node>();
			afterRecoveredNode = new ArrayList<Node>();
			
			for(Node node: infectedNode){
				for(Node fan: node.getFans()){
					if(fan.pre_status == Node.INFECTIOUS || fan.pre_status == Node.RECOVERED || fan.after_status == Node.INFECTIOUS)
						continue;
					
					if(Math.random() < infec){
				    	afterInfectedNode.add(fan);
				    	fan.after_status = Node.INFECTIOUS;
					}	
				}
				if (Math.random() < recov) {
					afterRecoveredNode.add(node);
					node.pre_status = Node.RECOVERED;
					recoveredNum++;
				}
				
			}
			for (Node node : afterRecoveredNode) {
				node.pre_status = Node.RECOVERED;
				infectedNode.remove(node);
			}
			for (Node node : afterInfectedNode) {
				node.pre_status = Node.INFECTIOUS;
				infectedNode.add(node);
			}
			//System.out.println("step "+step+"\t"+infectedNode.size()+"\t"+(recoveredNum+infectedNode.size()));
		}

		afterInfectedNode = null;
		afterRecoveredNode = null;
		return recoveredNum;
	}

	/* set the statuses of all the nodes as susceptible */
	public void initialGraph(DirectedGraph dg) {
		for (Node node : dg.getNodes()) {
			node.pre_status = Node.SUSCEPTIBLE;
			node.after_status = -1;
		}
	}

	/* set several nodes as sources */
	public void setSource(List<Node> sources) {
		for (Node node : sources) {
			node.pre_status = Node.INFECTIOUS;
			node.after_status = -1;
		}
	}

	/* set single node as a source */
	public void setSingleSource(Node source) {
		source.pre_status = Node.INFECTIOUS;
		source.after_status = -1;
	}
	
	
	public void setSources(List<Node> sources) {
		//System.out.println("set sources: "+sources.size());
		for(Node source: sources){
			//System.out.print(source.getNodeID()+" ");
		source.pre_status = Node.INFECTIOUS;
		source.after_status = -1;
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