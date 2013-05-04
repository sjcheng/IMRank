


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

public class MSIRModelDirectedGraph {

	/**
	 * @param args
	 */

	double infec;

	double recov;

	// DirectedGraph dg;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String root = "C:\\Experiments\\influence\\";
		String gfile = "douban.txt";
		//gfile = "testG.txt";
		String rsfile;
		String rssamplefile;
		String rsdetailfile;
		DirectedGraph dg = DirectedGraph.ConstructFromFile(new File(root
				+ gfile));
		double infec = 0.15;
		double recov = 0.1;

		MSIRModelDirectedGraph sir = new MSIRModelDirectedGraph(infec, recov);
		// int[] sourceid = {394360,274725,331024,394360};
		int[] sourceid = {565, 1476, 3333, 3992, 6637, 8177, 10592, 14506, 16634, 23136, 25591, 30662, 
				37212, 37253, 39575, 40475, 42165, 42901, 52455, 54035, 64649, 70001, 75105, 77269, 77969, 81037,
				83739, 88859, 89398, 92888, 94347, 94979, 98317, 102408, 102912, 106662,108050,110381,112896,
				116964, 117914, 118483, 122770, 129199, 133169, 135490, 141986,
				143084, 149366, 149601, 159141, 167041, 173445, 174020, 177664,
				180461, 194766, 198873, 213327, 218288, 223897, 233176, 234405,
				234660, 235179, 237057, 238968, 240349, 252996, 257035, 261120,
				274725, 275728, 278997, 287919, 294699, 295274, 299408, 302645,
				307072, 307278, 313192, 314122, 315359, 316900, 320705, 320705,
				323151, 327260, 331024, 344467, 349622, 353506, 358957, 360495,
				360639, 363324, 366669, 368272, 368870, 372804, 374653, 376346,
				378976, 379959, 387028, 389173, 393869, 394360, 398950, 406147,
				407074, 408706, 411214, 413164, 424971, 425852, 434030, 435764,
				436302, 437575, 441492, 442425,	442911, 447094, 447166, 449846,
				452680, 455727, 458314, 458917,	461337, 462940, 464299, 464856,
				466032, 469461, 474730, 475376,	475673, 475823, 475924, 487328,
				491744, 493294, 499707, 500144,	501847, 505641, 506939, 509058,
				510079, 511946, 513189, 514377, 515272, 515733, 520690, 526022,
				537493, 537493, 539252, 540831,	541159};
		int sample = 100;
		Node source = null;
		for (int i = 0; i < sourceid.length; i++) {
			source = dg.getNodeByID(sourceid[i]);
			rsfile = root + "infec"+infec+"_recov"+recov+"_singleNode_inf.txt";
			rssamplefile = root + "infec"+infec+"_recov"+recov+"_singleNode_inf_sample.txt";
			rsdetailfile = root + "infec"+infec+"_recov"+recov+"_singleNode_inf_detail.txt";
			sir.influenceofSingleNode(source, dg, rsfile, rssamplefile,
					rsdetailfile, sample);
		};
		
		infec = 0.2;
		recov = 0.1;
		for (int i = 0; i < sourceid.length; i++) {
			source = dg.getNodeByID(sourceid[i]);
			rsfile = root + "infec"+infec+"_recov"+recov+"_singleNode_inf.txt";
			rssamplefile = root + "infec"+infec+"_recov"+recov+"_singleNode_inf_sample.txt";
			rsdetailfile = root + "infec"+infec+"_recov"+recov+"_singleNode_inf_detail.txt";
			sir.influenceofSingleNode(source, dg, rsfile, rssamplefile,
					rsdetailfile, sample);
		};
		
		
		/* an instant */
		// sir.initialGraph(dg);
		// sir.setSingleSource(dg.getNodeByID(sourceid));
		// sir.singleInfectedProcess(dg, rsfile, rsdetailfile);
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

	public MSIRModelDirectedGraph(double infec, double recov) {
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
			if (node.pre_status == Node.INFECTIOUS)
				infectedNode.add(node);
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
			for (Node node : infectedNode) {
				// recover
				if (Math.random() < recov) {
					// node.pre_status = Node.RECOVERED;
					afterRecoveredNode.add(node);
					recoveredNum++;
				}

				// only choose one fan to infect
				if (node.getInDegree() == 0)
					continue;

				int randomIndex = (int) (node.getInDegree() * Math.random());
				Node fan = node.getFans().get(randomIndex);
				if (!fan.canbeInfected())
					continue;
				if (afterInfectedNode.contains(fan))
					continue;
				// infect
				if (Math.random() < infec) {
					afterInfectedNode.add(fan);
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
//			if (outrsdetail != null)
//				outrsdetail.println();
		}

		afterInfectedNode = null;
		afterRecoveredNode = null;
		return recoveredNum;
	}

	/* set the statuses of all the nodes as susceptible */
	public void initialGraph(DirectedGraph dg) {
		for (Node node : dg.getNodes()) {
			node.pre_status = Node.SUSCEPTIBLE;
			// node.after_status = -1;
		}
	}

	/* set several nodes as sources */
	public void setSource(List<Node> sources) {
		for (Node node : sources) {
			node.pre_status = Node.INFECTIOUS;
			// node.after_status = -1;
		}
	}

	/* set single node as a source */
	public void setSingleSource(Node source) {
		source.pre_status = Node.INFECTIOUS;
		// source.after_status = -1;
	}
}
