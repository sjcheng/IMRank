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
 * Tranditional SI model 
 * 
 */

public class SIModelDirectedGraph {

	/**
	 * @param args
	 */
/*
 * tranditional SIR model
 */
	static double infec;
	//static double recov;
	public static String root = "C:\\Experiments\\influence\\";


	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String rank = Rank.PAGERANK;
		String gfile = "douban.txt";
		
		DirectedGraph dg = DirectedGraph.ConstructFromFile(new File(root + gfile));


		SIModelDirectedGraph tsir = new SIModelDirectedGraph(infec);
		tsir.getRankfile(root, gfile, rank, dg);

		//get top-k nodes 
		//int sample = 50;
		int topk = 50;
		
		Node[] sources = tsir.getTopkNodes(dg, topk);
		
		infec = 0.004;
		tsir.runInstance(sources, infec, gfile, rank, dg);
	}

	public void runInstance(Node[] sources, double infec, String gfile, String rank, DirectedGraph dg){
		Node source = null;
		int sample = 1000;
		for (int i = 0; i < sources.length; i++) {
			source = sources[i];
			String rsfile = root + gfile.replace(".txt","_") + "SI_infec"+infec+"_"+rank+"_singleNode_inf.txt";
			String rssamplefile = root + gfile.replace(".txt","_") + "SI_infec"+infec+"_"+rank+"_singleNode_inf_sample.txt";
			String rsdetailfile = root + gfile.replace(".txt","_") + "SI_infec"+infec+"_"+rank+"_singleNode_inf_detail.txt";
			influenceofSingleNode(source, dg, rsfile, rssamplefile,
					rsdetailfile, sample);
		};
		source = null;
	}
	
	public void getRankfile(String root, String gfile, String rankkind, DirectedGraph dg){
		String rankfile = root + gfile.replace(".txt", "-"+rankkind+".txt");
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
	

	public SIModelDirectedGraph(double infec) {
		this.infec = infec;
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
			// if(outrsdetail!=null)
			outrsdetail.close();
			outrssample.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println(source.getNodeID() + "\t" + ave + "\t" + ave_per);
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
		int step = 0;
		while (infectedNode.size() > 0) {
			outrsdetail.append("T" + step + " :\t" + infectedNode.size() + "\t"
					+ recoveredNum + "\t"
					+ (infectedNode.size() + recoveredNum));
			outrsdetail.append("\r\n");
			step++;
			afterInfectedNode = new ArrayList<Node>();
			
			for(Node node: infectedNode){
				for(Node fan: node.getFans()){
					if(fan.pre_status == Node.INFECTIOUS || fan.after_status == Node.INFECTIOUS)
						continue;
					
					if(Math.random() < infec){
				    	afterInfectedNode.add(fan);
				    	fan.after_status = Node.INFECTIOUS;
					}	
				}
			}

			for (Node node : afterInfectedNode) {
				node.pre_status = Node.INFECTIOUS;
				infectedNode.add(node);
			}

		}

		afterInfectedNode = null;
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