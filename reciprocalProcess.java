import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.StringTokenizer;
import java.io.File;

public class reciprocalProcess {
	
	static String root = "";//note: this root should be changed
	
	public static void main(String[] args){
		reciprocalProcess dp = new reciprocalProcess();
		
		//root = "F:\\Chengsuqi\\Experiments\\dataset\\influence\\";
		//String network = "epinions";
		String network = args[0];
		String gfile = root + network +".txt";
		
		/*
		 * reciprocal links pattern
		 */
		String basicinfofile = root + network + "_basic"+".txt";
		DirectedGraph g = DirectedGraph.ConstructFromFile(gfile);
		dp.getBasicInfo(g, basicinfofile);
		
		String fans_leaders = root + network +"_fans_leaders.txt";
		String fans_aveleaders = root + network + "_fans_aveleaders.txt";
		String leaders_avefans = root + network + "_leaders_avefans.txt";
		

		dp.getfansleaders(g, fans_leaders, fans_aveleaders, leaders_avefans);
		
		String fans_reciNum = root + network + "_fans_reciNum.txt";
		String fans_avereciNum = root + network + "_fans_avereciNum.txt";

		String leaders_reciNum = root + network + "_leaders_reciNum.txt";
		String leaders_avereciNum = root + network + "_leaders_avereciNum.txt";	
		
		dp.getReciprocallinks(g, fans_reciNum, fans_avereciNum, leaders_reciNum, leaders_avereciNum);
		
	}
	
	/*
	 * this file includes information about N, M, normal-reci-ratio, 
	 * non-bias-reci-ratio, assortativity-reciprocallinks, assortativity-non-reciprocallinks
	 */
	public void getBasicInfo(DirectedGraph tempg, String rsfile){
//		print out the result files
		try{
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(new File(rsfile))));
		out.println("N = "+tempg.getGraphSize());
		out.println("M = " + tempg.getGraphEdgeNum());
		int reciNum = tempg.reciprocalLinks();
		out.println("ReciNum = " + reciNum);
		out.println("ReciPairNum = "+reciNum/2);
		out.println("Reci-Normal-ratio = "+((double)reciNum)/tempg.getGraphEdgeNum());
		
		//PRL 2004
		double L = tempg.getGraphEdgeNum();
		double L_reci = reciNum;
		double N = tempg.getGraphSize();
		double alpha = L/(N*(N-1));
		double nonbaisrr = ((L_reci/L)-alpha)/(1-alpha);
		
		out.println("Reci-NonBais-ratio = " + nonbaisrr);
		
		//tempg = null;
		
		//pearson coefficient -- reciprocal links VS all links
		int[] end1 = new int[tempg.getGraphEdgeNum()];
		int[] end2 = new int[tempg.getGraphEdgeNum()];
		int count1 = 0;
		
		int[] end11 = new int[tempg.getGraphEdgeNum()];
		int[] end12 = new int[tempg.getGraphEdgeNum()];
		int count11 = 0;
		
		//for(Node node: tempg.getNodes()){
		int size = tempg.getNodes().size();
		for(int i=0; i<size; i++){
			Node node = tempg.getNodes().get(i);
			int size_fans = node.getFans().size();
			for(int j = 0; j<size_fans; j++){
				Node fan = node.getFans().get(j);
				end1[count1] = fan.getInDegree();
				end2[count1] = node.getInDegree();
				count1 ++;
				if(fan.getNodeID() < node.getNodeID() && fan.hasFan(node))
				{
					end11[count11] = node.getInDegree();
					end12[count11] = fan.getInDegree();
					count11 ++;
				}
			}
		}
		
		double r = this.directedpearson(end1, end2, count1);
		double mulr = this.mulPearson(end11, end12, count11);
		out.println("Assortativity coefficiency of all links: "+ r);
		out.println("Assortativity coefficiency of reciprocal links: "+ mulr);
		out.close();
		
		 }catch(Exception e){
			 e.printStackTrace();
		 }
	}

	public double mulPearson(int[] end1, int[] end2, int M){
		double r = 0;
		
		//double M = end1.length;
		long up1 = 0;
		long base = 0;
		long down1 = 0;
		double down2 = 0;
		for(int i = 0; i < M; i++){
		    up1 = up1 + end1[i]*end2[i];
			base = base + end1[i]+end2[i];
			down1 = down1 + end1[i]*end1[i]+end2[i]*end2[i];
		}
		up1 = up1/M;
		down2 = down1/(2*M);
		base = base/(2*M);
		base = base*base;
		r = (up1-base)/(down2-base);
		
		return r;
	}
	
	public double directedpearson(int[] end1, int[] end2, int len){
		double r = 0;
		
		double up = 0;
		long down_l = 0;
        long down_r = 0;
		double avex = 0;
		double avey = 0;
		//double len=end1.length;
		for(int i = 0; i<len; i++){
			avex = avex + end1[i];
			avey = avey + end2[i];
		}
		avex = avex/len;
		avey = avey/len;
		for(int i = 0; i<len; i++){
			up = up + (end1[i] - avex)*(end2[i] - avey);
            down_l = down_l + (long)Math.pow((end1[i]-avex),2);
            down_r = down_r + (long)Math.pow((end2[i]-avey), 2);
		}
		down_l = (long) Math.sqrt(down_l);
		down_r = (long) Math.sqrt(down_r);
		r = up/(down_l*down_r);
		return r;
	}
	
	/*
	 * rsfile records the direct followers VS friends
	 */
	public void getReciprocallinks(DirectedGraph g, String rs1file, String rs_ave1_file, String rs2file, String rs_ave2_file){
		//DirectedGraph g = DirectedGraph.ConstructFromFile(gfile);
		int maxIndegree = 0;
		int maxOutdegree = 0;
		int size = g.getNodes().size();
		for(int i = 0; i < size; i++){
			Node node = g.getNodes().get(i);
			if(node.getInDegree() > maxIndegree)
				maxIndegree = node.getInDegree();
			if(node.getOutDegree() > maxOutdegree)
				maxOutdegree = node.getOutDegree();
		}
		
		//int[] number = new int[maxIndegree+1];
		int[] followers = new int[maxIndegree+1];
		double[] friends_followers = new double[maxIndegree+1];
		
		int[] leaders = new int[maxOutdegree+1];
		double[] friends_leaders = new double[maxOutdegree+1];
		
		for(int i = 0; i < size; i++){
			Node node = g.getNodes().get(i);
			followers[node.getInDegree()]++;
			leaders[node.getOutDegree()]++;
			int friendsNum = 0;
			int size_fans = node.getFans().size();
			for(int j = 0; j< size_fans; j++){
				Node fan = node.getFans().get(j);
				if(node.hasLeader(fan)){
					//reciprocal follower
					friendsNum++;
				}			
			}
			node.rspre = friendsNum;
			friends_followers[node.getInDegree()] = friends_followers[node.getInDegree()] + friendsNum;	
			friends_leaders[node.getOutDegree()] = friends_leaders[node.getOutDegree()] + friendsNum;
		}
		
		for(int i = 0; i<maxIndegree+1; i++){
			if(followers[i] == 0) continue;
			friends_followers[i] = friends_followers[i]/followers[i];
		}
		
		for(int i = 0; i<maxOutdegree+1; i++){
			if(leaders[i] == 0) continue;
			friends_leaders[i] = friends_leaders[i]/leaders[i];
		}
		
		//print out the result files
		 try{
			 PrintWriter rs1 = new PrintWriter(new BufferedWriter(new FileWriter(new File(rs1file))));
	         PrintWriter rs1_ave = new PrintWriter(new BufferedWriter(new FileWriter(rs_ave1_file)));
		    
	         PrintWriter rs2 = new PrintWriter(new BufferedWriter(new FileWriter(new File(rs2file))));
	         PrintWriter rs2_ave = new PrintWriter(new BufferedWriter(new FileWriter(rs_ave2_file)));
	         
	         //print out rs
	 		for(int i = 0; i < size; i++){
				Node node = g.getNodes().get(i);
	        	 rs1.println(node.getInDegree()+"\t"+node.rspre);
	         }
		 
	         //print out rs average
	         for(int i = 0; i<maxIndegree+1; i++){
	 			if(followers[i] == 0) continue;
	 			rs1_ave.println(i+"\t"+friends_followers[i]);
	 		}
	         
             //print out rs
	 		for(int i = 0; i < size; i++){
				Node node = g.getNodes().get(i);
	        	 rs2.println(node.getOutDegree()+"\t"+node.rspre);
	         }
		 
	         //print out rs average
	         for(int i = 0; i<maxOutdegree+1; i++){
	 			if(leaders[i] == 0) continue;
	 			rs2_ave.println(i+"\t"+friends_leaders[i]);
	 		}
	         
	         rs1.close();
	         rs1_ave.close();
	         rs2.close();
	         rs2_ave.close();
	         
		 }catch(Exception e){
			 e.printStackTrace();
		 }
	}
	
	/*
	 * need change
	 */
	public void getfansleaders(DirectedGraph g, String fans_leaders, String fans_aveLeaders, String leaders_avefans){
		
		//DirectedGraph g = DirectedGraph.ConstructFromFile(gfile);
		int maxIndegree = 0;
		int maxOutdegree = 0;
		int size = g.getNodes().size();
		for(int i = 0; i < size; i++){
			Node node = g.getNodes().get(i);
			if(node.getInDegree() > maxIndegree)
				maxIndegree = node.getInDegree();
			if(node.getOutDegree() > maxOutdegree)
				maxOutdegree = node.getOutDegree();
		}
		
		//the same fans VS average leaders
		int[] aveLeaders = new int[maxIndegree+1];
		double[] fans_aveLeaders_Num = new double[maxIndegree+1];
		
		//the same leaders VS average fans
		int[] aveFans = new int[maxOutdegree+1];
		double[] fans_aveFans_Num = new double[maxOutdegree+1];
		
		for(int i = 0; i < size; i++){
			Node node = g.getNodes().get(i);
			aveLeaders[node.getInDegree()]++;
			aveFans[node.getOutDegree()]++;
			fans_aveLeaders_Num[node.getInDegree()] = fans_aveLeaders_Num[node.getInDegree()] + node.getOutDegree();	
			fans_aveFans_Num[node.getOutDegree()] = fans_aveFans_Num[node.getOutDegree()] + node.getInDegree();
		}
		
		for(int i = 0; i<maxIndegree+1; i++){
			if(aveLeaders[i] == 0) continue;
			fans_aveLeaders_Num[i] = fans_aveLeaders_Num[i]/aveLeaders[i];
		}
		
		for(int i = 0; i<maxOutdegree+1; i++){
			if(aveFans[i] == 0) continue;
			fans_aveFans_Num[i] = fans_aveFans_Num[i]/aveFans[i];
		}
		
		//print out the result files
		 try{
			 PrintWriter rs1 = new PrintWriter(new BufferedWriter(new FileWriter(new File(fans_leaders))));
	         PrintWriter rs_ave1 = new PrintWriter(new BufferedWriter(new FileWriter(fans_aveLeaders)));
	         PrintWriter rs_ave2 = new PrintWriter(new BufferedWriter(new FileWriter(new File(leaders_avefans))));
	         //PrintWriter rs2_ave = new PrintWriter(new BufferedWriter(new FileWriter(rs_ave2_file)));
	         
	         //print out rs
	 		for(int i = 0; i < size; i++){
				Node node = g.getNodes().get(i);
	        	 rs1.println(node.getInDegree()+"\t"+node.getOutDegree());
	         }
		 
	         //print out rs average1
	         for(int i = 0; i<maxIndegree+1; i++){
	 			if(aveLeaders[i] == 0) continue;
	 			rs_ave1.println(i+"\t"+fans_aveLeaders_Num[i]);
	 		}
		 
	         //print out rs average
	         for(int i = 0; i<maxOutdegree+1; i++){
	 			if(aveFans[i] == 0) continue;
	 			rs_ave2.println(i+"\t"+fans_aveFans_Num[i]);
	 		}
	         
	         rs1.close();
	         rs_ave1.close();
	         rs_ave2.close();
	         
		 }catch(Exception e){
			 e.printStackTrace();
		 }
	}
}
