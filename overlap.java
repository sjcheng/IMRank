import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.util.Set;
import java.util.HashSet;
import java.util.List;


public class overlap {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
        String root = "C:\\Experiments\\influence\\";
		String gfile = "douban.txt";
		DirectedGraph dg = DirectedGraph.ConstructFromFile(root+gfile);
		int[] rank_PR = new int[dg.getGraphSize()];
		int[] rank_HITSA = new int[dg.getGraphSize()];
		int[] rank_HITSH = new int[dg.getGraphSize()];
		int[] rank_LR = new int[dg.getGraphSize()];
		int[] rank_ID = new int[dg.getGraphSize()];
		
		overlap a = new overlap();
		a.readingRank(root, gfile, Rank.PAGERANK, rank_PR);
		a.readingRank(root, gfile, Rank.HITSAUTHORITY, rank_HITSA);
		a.readingRank(root, gfile, Rank.HITSHUB, rank_HITSH);
		a.readingRank(root, gfile, Rank.LEADERRANK, rank_LR);
		a.readingRank(root, gfile, Rank.INDEGREE, rank_ID);
		
		a.printdeletecombinedNodes(rank_PR, rank_LR, rank_ID);
		
//		System.out.println(gfile);
//		System.out.println("PR VS HITSA-----------------");
//		a.compareOverlap(rank_PR, rank_HITSA);
//		
//		System.out.println("PR VS HITSH-----------------");
//		a.compareOverlap(rank_PR, rank_HITSH);
//		
//		System.out.println("PR VS LR-----------------");
//		a.compareOverlap(rank_PR, rank_LR);
//		
//		System.out.println("PR VS ID-----------------");
//		a.compareOverlap(rank_PR, rank_ID);
//		
//		System.out.println("HITSA VS HITSH-----------------");
//		a.compareOverlap(rank_HITSA, rank_HITSH);
//		
//		System.out.println("HITSA VS LR-----------------");
//		a.compareOverlap(rank_HITSA, rank_LR);
//		
//		System.out.println("HITSA VS ID-----------------");
//		a.compareOverlap(rank_HITSA, rank_ID);
//		
//		System.out.println("HITSH VS LR-----------------");
//		a.compareOverlap(rank_HITSH, rank_LR);
//		
//		System.out.println("HITSH VS ID-----------------");
//		a.compareOverlap(rank_HITSH, rank_ID);
//		
//		System.out.println("LR VS ID-----------------");
//		a.compareOverlap(rank_LR, rank_ID);
	}

	public void readingRank(String root, String gfile, String rankkind, int[] nodeid_rank){
		String rankfile = root + gfile.replace(".txt", "_"+rankkind+".txt");
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
                line = line.replace('-',' ');
                StringTokenizer st = new StringTokenizer(line);
                int nodeid;
                int rank;
                try{
                	nodeid = Integer.parseInt(st.nextToken());
                	st.nextToken();
                	rank = Integer.parseInt(st.nextToken());
                	nodeid_rank[rank-1] = nodeid;
                }catch(Exception e){
                	e.printStackTrace();
                }
                
            }
        }catch(Exception e){
        	e.printStackTrace();
        }
	}
	
	public void compareOverlap(int[] rank1, int[] rank2){
		//top10-1000 overlap
		for(int i=0; i<500; i=i+10){
			//rank1 
			int count = 0;
			for(int j=0; j<i+10; j++){
				//rank2
				for(int k=0; k<i+10; k++)
				{
					if(rank1[j]==rank2[k])
					{
						count ++;
						break;
					}
				}
			}
			System.out.println((i+10)+"\t"+count+"\t"+((double)count)/(i+10));			
		}
		
	}
	
	//common including nodes
	public void printcombineNodes(int[] rank1, int[] rank2, int[] rank3){
		Set<Integer> nodes = new HashSet<Integer>();
		for(int i=0; i<50; i++){
			nodes.add(rank1[i]);
			nodes.add(rank2[i]);
			nodes.add(rank3[i]);
		}
		for(Integer i:nodes){
			System.out.println(i);
		}
	}
	
    //	common including nodes
	public void printdeleteSpecialNodes(int[] rank1, int[] rank2, int[] rank3){
		Set<Integer> ranklist1 = new HashSet<Integer>();
		Set<Integer> ranklist2 = new HashSet<Integer>();
		Set<Integer> ranklist3 = new HashSet<Integer>();
		for(int i=0; i<50; i++){
			ranklist1.add(rank1[i]);
			ranklist2.add(rank2[i]);
			ranklist3.add(rank3[i]);
		}
		System.out.println("--1--");
		for(Integer i:ranklist1){
			if(ranklist2.contains(i) && ranklist3.contains(i)) 
				continue;
			System.out.println(i);
		}
		System.out.println("--2--");
		for(Integer i:ranklist2){
			if(ranklist1.contains(i) && ranklist3.contains(i)) 
				continue;
			System.out.println(i);
		}
		System.out.println("--3--");
		for(Integer i:ranklist3){
			if(ranklist1.contains(i) && ranklist2.contains(i)) 
				continue;
			System.out.println(i);
		}
	}
	
    //	common including nodes
	public void printdeletecombinedNodes(int[] rank1, int[] rank2, int[] rank3){
		Set<Integer> ranklist1 = new HashSet<Integer>();
		Set<Integer> ranklist2 = new HashSet<Integer>();
		Set<Integer> ranklist3 = new HashSet<Integer>();
		Set<Integer> ranklist4 = new HashSet<Integer>();
		for(int i=0; i<50; i++){
			ranklist1.add(rank1[i]);
			ranklist2.add(rank2[i]);
			ranklist3.add(rank3[i]);
		}
		System.out.println("--1--");
		for(Integer i:ranklist1){
			if(ranklist2.contains(i) && ranklist3.contains(i)) 
				continue;
			ranklist4.add(i);
		}
		System.out.println("--2--");
		for(Integer i:ranklist2){
			if(ranklist1.contains(i) && ranklist3.contains(i)) 
				continue;
			ranklist4.add(i);
		}
		System.out.println("--3--");
		for(Integer i:ranklist3){
			if(ranklist1.contains(i) && ranklist2.contains(i)) 
				continue;
			ranklist4.add(i);
		}
		for(Integer i:ranklist4){
			System.out.println(i);
		}
	}
	
}
