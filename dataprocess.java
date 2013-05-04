


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.StringTokenizer;
import java.io.File;

public class dataprocess {
	
	static String root = "F:\\Chengsuqi\\Experiments\\dataset\\influence\\";//note: this root should be changed
	
	public static void main(String[] args){
		dataprocess dp = new dataprocess();
		
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
		dp.recordFans(gfile, fansrankfile);
		dp.recordLeaderRank(gfile, leaderrankfile);
		dp.recordHITS(gfile, hitsAutfile, hitsHubfile);
		
		/*
		 * reciprocal links pattern
		 */
		String basicinfofile = root + network + "_basic"+".txt";
		String fans_leaders = root + network +"_fans_leaders.txt";
		String fans_aveleaders = root + network + "_fans_aveleaders.txt";
		String leaders_avefans = root + network + "_leaders_avefans.txt";
		String fans_reciNum = root + network + "_fans_reciNum.txt";
		String leaders_reciNum = root + network + "_leaders_reciNum.txt";
		String fans_avereciNum = root + network + "_fans_avereciNum.txt";
		String leaders_avereciNum = root + network + "_leaders_avereciNum.txt";	
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
	
	/*
	 * change 1,2 to 2 1
	 */
	public void tempChangeFormat(String gfile, String newgfile){

        try
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(gfile))));
            PrintWriter outrs = new PrintWriter(new BufferedWriter(new FileWriter(newgfile)));
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
                
                if(line.contains("#")) continue;
                //StringTokenizer st = new StringTokenizer(line);
                
                String[] strs = line.split(",");
                     
                int toID;
                int fromID;
                
                try
                {
                    toID = Integer.parseInt(strs[0]);
                    fromID = Integer.parseInt(strs[1]);
                }
                catch(Exception exp)
                {
                    System.out.println(line);
                    continue;
                }
               
                outrs.println(fromID+"\t"+toID);
            }
            
            br.close();
            outrs.close();
        }
        catch(IOException exp)
        {
            System.out.println(exp);
        }	
	
	}
	
	public void getProperties(String gfile){
		DirectedGraph tempg = DirectedGraph.ConstructFromFile(gfile);
		System.out.println(tempg.getGraphSize()+"\t"+tempg.getGraphEdgeNum());
		int reciNum = tempg.reciprocalLinks();
		System.out.println(reciNum+"\t"+reciNum/2+"\t"+((double)reciNum)/tempg.getGraphEdgeNum());
	}
	
	
	public DirectedGraph XMLtoEdgelist(String xmlFile){

        DirectedGraph g = new DirectedGraph();
        try
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(xmlFile))));
            String line ;
            int count=0;
            //<edge sender_id="1" receiver_id="29" />
            while((line=br.readLine())!=null)
            {
                count++;
                line = line.trim() ;
                if(line.length()==0)
                {
                    System.out.println("line length is zero");
                   continue;
                }
                
                if(!line.contains("id")) continue;
                
                String[] strs = line.split("\"");
                     
                //StringTokenizer st = new StringTokenizer(line);
                //System.out.println(strs[1]+"----\t-----"+strs[3]);
                int fromID;
                int toID;
                try
                {
                    fromID = Integer.parseInt(strs[1]);
                    toID = Integer.parseInt(strs[3]);
                }
                catch(Exception exp)
                {
                    System.out.println(line);
                    continue;
                }
                if(fromID==toID) continue; 
                
                //int i1 = g.getNodeindex(fromID);
                int i1 = g.getNodeindex(fromID);
                //int i2 = g.getNodeindex(toID);
                int i2 = g.getNodeindex(toID);
                
                Node src,dest;
                if(i1<0)
                {
                    src = new Node(fromID, g);
                    if(g.addNode(src))
                    {

                    }
                    else
                    {
                       System.out.println("Add src node "+src.getNodeID()+" failed in line "+line);
                    }
                }
                else
                {
                    src = g.getNodes().get(i1);
                }
                if(i2<0)
                {
                    dest = new Node(toID, g);
                    if(g.addNode(dest))
                    {

                    }
                    else
                    {
                      System.out.println("Add dest node "+dest.getNodeID()+" failed in line "+line);
                    }
                }
                else
                {
                    dest = g.getNodes().get(i2);
                }

                if(g.addDirectEdge(src, dest))
                {
                    //System.out.println("Add edge: "+src.getNodeID()+" "+dest.getNodeID());
                }
                else
                {
                   //System.out.println("Add edge: "+src.getNodeID()+" "+dest.getNodeID()+" failed");

                }
                if(count>999999 && (count%1000000)==0)
                      System.out.println(count);
            }
            br.close();
            System.out.println("Number of line:"+count);
        }
        catch(IOException exp)
        {
            System.out.println(exp);
        }
        return g;
	}
	
	public void removeRedundantEdges(String gfile, String newgfile){
		DirectedGraph tempg = DirectedGraph.ConstructFromFile(gfile);
		tempg.saveToFile(newgfile);
	}
	
	public void tempChange(String gfile, String rsfile){

        try
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(gfile))));
            PrintWriter outrs = new PrintWriter(new BufferedWriter(new FileWriter(rsfile)));
            String line ;
            int count=0;
            //remove -1
            while((line=br.readLine())!=null)
            {
                count++;
                line = line.trim() ;
                if(line.length()==0)
                {
                    System.out.println("line length is zero");
                   continue;
                }
                
                if(line.contains("#")) continue;
                StringTokenizer st = new StringTokenizer(line);
                
                //String[] strs = line.split(" ");
                     
                int fromID;
                int toID;
                
                try
                {
                    fromID = Integer.parseInt(st.nextToken());
                    toID = Integer.parseInt(st.nextToken());
                }
                catch(Exception exp)
                {
                    System.out.println(line);
                    continue;
                }
               
                outrs.println(toID+"\t"+fromID);
            }
            
            br.close();
            outrs.close();
        }
        catch(IOException exp)
        {
            System.out.println(exp);
        }	
	}
	
	
	/*
	 * rsfile records the direct followers VS friends
	 */
	public void getFollowertofriends(String gfile, String rsfile, String rs_ave_file){
		DirectedGraph g = DirectedGraph.ConstructFromFile(gfile);
		int maxIndegree = 0;
		for(Node node: g.getNodes()){
			if(node.getInDegree() > maxIndegree)
				maxIndegree = node.getInDegree();
		}
		
		//int[] number = new int[maxIndegree+1];
		int[] followers = new int[maxIndegree+1];
		double[] friends = new double[maxIndegree+1];
		
		
		for(Node node: g.getNodes()){
			followers[node.getInDegree()]++;
			int friendsNum = 0;
			for(Node fan: node.getFans()){
				if(node.hasLeader(fan)){
					//reciprocal follower
					friendsNum++;
				}			
			}
			node.rspre = friendsNum;
			friends[node.getInDegree()] = friends[node.getInDegree()] + friendsNum;	
		}
		
		for(int i = 0; i<maxIndegree+1; i++){
			if(followers[i] == 0) continue;
			friends[i] = friends[i]/followers[i];
		}
		
		
		//print out the result files
		 try{
			 PrintWriter rs = new PrintWriter(new BufferedWriter(new FileWriter(new File(rsfile))));
	         PrintWriter rs_ave = new PrintWriter(new BufferedWriter(new FileWriter(rs_ave_file)));
		 
	         //print out rs
	         for(Node node: g.getNodes()){
	        	 rs.println(node.getInDegree()+"\t"+node.rspre);
	         }
		 
	         //print out rs average
	         for(int i = 0; i<maxIndegree+1; i++){
	 			if(followers[i] == 0) continue;
	 			rs_ave.println(i+"\t"+friends[i]);
	 		}
	         
	         rs.close();
	         rs_ave.close();
		 }catch(Exception e){
			 e.printStackTrace();
		 }
	}
	
	
	public void SignedNetworktoPositiveEdgelist(String snFile, String rsFile){
        try
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(snFile))));
            PrintWriter outrs = new PrintWriter(new BufferedWriter(new FileWriter(rsFile)));
            String line ;
            int count=0;
            //remove -1
            while((line=br.readLine())!=null)
            {
                count++;
                line = line.trim() ;
                if(line.length()==0)
                {
                    System.out.println("line length is zero");
                   continue;
                }
                
                if(line.contains("#")) continue;
                StringTokenizer st = new StringTokenizer(line);
                
                //String[] strs = line.split(" ");
                     
                int fromID;
                int toID;
                int sign;
                try
                {
                    fromID = Integer.parseInt(st.nextToken());
                    toID = Integer.parseInt(st.nextToken());
                    sign = Integer.parseInt(st.nextToken());
                }
                catch(Exception exp)
                {
                    System.out.println(line);
                    continue;
                }
                if(sign<0) continue; 
                outrs.println(fromID+"\t"+toID);
            }
            
            br.close();
            outrs.close();
        }
        catch(IOException exp)
        {
            System.out.println(exp);
        }
	}
}
