import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.StringTokenizer;
import java.io.File;

public class RankProcess {
	
	static String root = "C:\\Experiments\\influence\\";//note: this root should be changed
	
	public static void main(String[] args){
		RankProcess dp = new RankProcess();
		
		String network = "epinions";
		String gfile = root + network +".txt";
		
		/*
		 * rank algorithm
		 * each file format is "nodeid  rankscore  rank";
		 */
		String pagerankfile = root + network + "_PR" +".txt";
		String leaderrankfile = root + network + "_LR" +".txt";
		String hitsAutfile = root + network + "_HITS(A)" + ".txt";
		String hitsHubfile = root + network + "_HITS(H)" + ".txt";
		String fansrankfile = root + network + "_FANS" + ".txt";
		
		dp.recordPageRank(gfile, pagerankfile);
		//dp.recordFans(gfile, fansrankfile);
		//dp.recordLeaderRank(gfile, leaderrankfile);
		//dp.recordHITS(gfile, hitsAutfile, hitsHubfile);
	}
	
	//pagerank
	public void recordPageRank(String gfile, String rsfile){
		DirectedGraph g = DirectedGraph.ConstructFromFile(gfile);
		PageRankAlgorithm pr = new PageRankAlgorithm();
		System.out.println("begin to caculate pagerank score");
		pr.calculateScore(g);
	    pr.rank(g);
		System.out.println("begin to save pagerank score");
		pr.savetoFile(g, rsfile);
		g = null;
		pr = null;
	}
	
	//leaderrank
	public void recordLeaderRank(String gfile, String rsfile){
		DirectedGraph g = DirectedGraph.ConstructFromFile(gfile);
		LeaderRankAlgorithm lr = new LeaderRankAlgorithm(g);
		System.out.println("begin to caculate leaderrank score");
		lr.calculateScore();
		lr.rank(g);
		System.out.println("begin to save leaderrank score");
		lr.savetoFile(rsfile);
		g = null;
		lr = null;
	}
	
    //	HITS
	public void recordHITS(String gfile, String rs1file, String rs2file){
		HITSRankAlgorithm hitsr = new HITSRankAlgorithm(gfile);
		System.out.println("begin to caculate HITS score");
		hitsr.calculateScore();
		hitsr.rankByAuthority();
		hitsr.rankByHub();
		System.out.println("begin to save HITS score");
		hitsr.savetoFile(rs1file, rs2file);
		
	}
	
    //	fans
	public void recordFans(String gfile, String rsfile){
		System.out.println("begin to caculate fans score");
		DirectedGraph g = DirectedGraph.ConstructFromFile(new File(gfile));
		InDegreeRank ir = new InDegreeRank();
	    ir.rank(g);
		System.out.println("begin to save fans score");
		ir.savetoFile(g, rsfile);
		g = null;
		ir = null;
	}
}
