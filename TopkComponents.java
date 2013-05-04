import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class TopkComponents {

	static int inter_num = 0;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		extractSeedsMain();
		//extractSeedsMain(args[0], Integer.parseInt(args[1]), Double.parseDouble(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]));
	}
    
	
	
	public static void exsitInComponent(){
		
		//read in seeds
		String seedsfile = "E:\\Experiments\\IM\\Results\\Greedy(CELF)\\hep_0.01_CELF_seeds.txt";
		String filename = "E:\\Experiments\\IM\\networks\\hep.txt";
		int[] seeds = OverlapSimple.readinSeeds(seedsfile);
		double p = 0.01;
		TopkComponents topk = new TopkComponents();
		int number = 10000;
		
		int[][] record = new int[seeds.length][number];
		
		//caculate the frequent of the seeds in top-k components
		for(inter_num=0; inter_num<number; inter_num++){
	    	 MultiEdgesDirectedGraph gtemp  = MultiEdgesDirectedGraph.ConstructFromUndirectedFile(filename);
	    	 topk.deleteGraph(gtemp, p);
	    	 int[][] rs = topk.statSeedsRank(gtemp, seeds);
	    	 for(int i=0; i<seeds.length; i++){
	    		 record[i][inter_num] = rs[0][i];
	    	 }
	    	 gtemp = null;
		}
		
		//stat record
		//from the point of top-component, the percent of covered top-k components
		boolean[] flag = new boolean[40000];
		int[] covered = new int[40000];
		for(int i=0; i<number; i++){
			for(int k=0; k<seeds.length; k++)
				 flag[k] = false;
			
			for(int j=0; j<seeds.length; j++){
				if(flag[record[j][i]])
					continue;
				covered[record[j][i]]++;
				flag[record[j][i]] = true;
			}
		}
		
		for(int i=1; i < 1000; i++){
			System.out.println((double)covered[i]/(double)number);
		}
	}
	
	public static void extractSeedsMain(String filename, int k, double p, int k_rank, int snapshot_number){
		TopkComponents topk = new TopkComponents();
		topk.extractSeedsByTopk(filename, k,  p,  k_rank, snapshot_number);
	}
	
	public static void extractSeedsMain(){
		String filename = "E:\\Experiments\\IM\\networks\\phy.txt";
		int[] k_rank = {10};
		int[] snapshot_number = {200,400,600,800,1000};
		
		double p = 0.01;
		int k = 50;
		TopkComponents topk = new TopkComponents();
		for(int i=0; i<k_rank.length; i++)
			for(int j=1; j<snapshot_number.length; j++)
				//for( int z = 0; z < snapshot_number.length; z++)
		            topk.extractSeedsByTopk(filename, k, p, k_rank[i], snapshot_number[j]);
	
	}
	
	public void extractSeedsByTopk(String filename, int k, double p, int k_rank, int snapshot_number){
		//String filename = "E:\\Experiments\\IM\\networks\\hep.txt";
		
		//int k = 50;
		//int k_rank = 15;
		//double p = 0.008;
		MultiEdgesDirectedGraph g  = MultiEdgesDirectedGraph.ConstructFromUndirectedFile(filename);
		
		//get a snapshot, and give value to every nodes which stays in top-k component  s
		List<Node> seeds = new ArrayList<Node>();
		//int number = 200;
		ArrayList<Snapshot> snapshots = new ArrayList<Snapshot>();
		for(int i=0; i<snapshot_number; i++){
			snapshots.add(new Snapshot(k_rank));
		}

		//
		for(int i=0; i<snapshot_number; i++){
			if(i%50==0)
		    	System.out.print(i);
	    	 MultiEdgesDirectedGraph gtemp  = MultiEdgesDirectedGraph.ConstructFromUndirectedFile(filename);
	    	 deleteGraph(gtemp, p);
	    	 caculateComponentsandRank(gtemp);
	    	 //add the snapshot to records
	    	 for(Node node: gtemp.getNodes()){
	    		 if(node.rank <= k_rank){
	    			 Node newnode = g.getNodeByID(node.id);
	    			 newnode.getRecord().put(i, node.rank);
	    			 newnode.getRecord_detail().put(i, node.componentID);
	    			 snapshots.get(i).getCertainShapshot(node.rank).add(node.id);
	    			 snapshots.get(i).setRanksize(node.rank, node.pre_component_size);
	    		 }
	    	 }
	    	 gtemp = null;
		}
		System.out.println();
		ArrayList<Node> candidates = new ArrayList<Node>();
		
		
		int threshold = (int)(0.0 * snapshot_number);
		for(int i=0; i<g.NumberofNodes(); i++){
			if(g.getNodes().get(i).record.size() < threshold) continue;
			candidates.add(g.getNodes().get(i));
		}
		
		
		while(seeds.size() < k){
			//caculate the scores
			double[] scores = new double[candidates.size()];
			double[] temp = new double[candidates.size()];
		
			int maxID = -1;
			double maxScore = -1;
			//int preid = -1;
			
			for(int i=0; i<candidates.size(); i++){
				
				double score = 0;
				HashMap<Integer, Integer> hm = candidates.get(i).record;
				Iterator iterator = hm.entrySet().iterator();
				temp[i] = hm.size();
				while(iterator.hasNext()) {
					 java.util.Map.Entry entry = (java.util.Map.Entry)iterator.next();
					 int iter_num = (Integer)entry.getKey();
					 int rank = (Integer)entry.getValue();
					 score = score + snapshots.get(iter_num).getRanksize(rank);
					 //score = score + snapshots.get(inter_num).getCertainShapshot(rank).size();
					 //score = score + 1.0;
					 //score = score + 1.0/(double)(Integer)entry.getValue();
				}
			
				scores[i] = score;
				if(score > maxScore)
				{
					maxScore = score;
					maxID = i;
				}
			}
			//preid = maxID;
			maxID = candidates.get(maxID).id;
			seeds.add(g.getNodeByID(maxID));
			
			
			//update node records
			HashMap<Integer, Integer> hm = g.getNodeByID(maxID).record;
			Iterator iterator = hm.entrySet().iterator();
			int count = 0;
			//System.out.println(g.getNodeByID(maxID).id);
			while(iterator.hasNext()) {
				 java.util.Map.Entry entry = (java.util.Map.Entry)iterator.next();
				 int rank = (Integer)entry.getValue();
				 int iter_num = (Integer)entry.getKey();
				 //System.out.println("iter_num: "+iter_num+" rank:"+ rank +"\t size: "+snapshots.get(iter_num).getRanksize(rank));
				 
				 //remove the nodes in the certain snapsot and the certain rank
				 int detail = g.getNodeByID(maxID).getRecord_detail().get(iter_num);
				 for(Integer id:snapshots.get(iter_num).getCertainShapshot(rank)){
					 if(id == maxID) continue;
					 Node node = g.getNodeByID(id);
					 HashMap tt = node.getRecord_detail();
					 int detailtemp = (Integer)tt.get(iter_num);
					 if(detail == detailtemp){
						//System.out.println("remove"+id+"\t iternum: "+iter_num+"\t"+"  rank: "+g.getNodeByID(id).getRecord().get(iter_num));
						g.getNodeByID(id).getRecord().remove(iter_num);
						//System.out.println(g.getNodeByID(id).getRecord().get(iter_num));
//						if(a == null)
//							System.out.println("wrong");
						
						//g.getNodeByID(id).getRecord_detail().remove(iter_num);
						count ++;
					 }
				}	 
			}
			
			//System.out.println("update "+count+" == "+ (maxScore-temp[preid]) +"  PS: "+maxScore+"\t"+temp[preid]);
			//remove the seed
			candidates.remove(g.getNodeByID(maxID));
		}
		
		for(Node seed: seeds){
			System.out.println(seed.getNodeID());
		}
		System.out.println();
		//score: 1/k-- rank1 = 1 rank2 = 1/2 rank3 = 1/3
		SeedsInfluenceSpread spread = new SeedsInfluenceSpread(g);
		spread.getInfluenceSpread(seeds, p);
		g = null;
	}
	
	
	public static void main1(){
		String filename = "E:\\Experiments\\IM\\networks\\BA_1000_2_0.txt";
		TopkComponents topk = new TopkComponents();
		
		double p = 0.01;
		MultiEdgesDirectedGraph g  = MultiEdgesDirectedGraph.ConstructFromUndirectedFile(filename);
		int number = 20000;
		String rsfile = filename.replace(".txt", "_"+p+"_"+number+".txt");
    	int[][] scores = new int[g.nodes.size()][number];
    	//g = null;
    	 
		for(inter_num=0; inter_num<number; inter_num++){
	    	 MultiEdgesDirectedGraph gtemp  = MultiEdgesDirectedGraph.ConstructFromUndirectedFile(filename);
	    	 topk.deleteGraph(gtemp, p, scores);
	    	 gtemp = null;
		}
		try{
			PrintWriter out= new PrintWriter(new BufferedWriter(new FileWriter(rsfile)));
		//stat
		for(int i=0; i<g.nodes.size(); i++){
			//check wether it is in top1
			int count1 = 0;
			int count2 = 0;
			int count3 = 0;
			int count4 = 0;
			int count5 = 0;
			
			for(int j=0; j<number; j++){
				if(scores[i][j] == 1)
					count1++;
				if(scores[i][j] == 2)
					count2++;
				if(scores[i][j] == 3)
					count3++;
				if(scores[i][j] == 4)
					count4++;
				if(scores[i][j] == 5)
					count5++;

			}
			if((double)count1/number > 0) 
			System.out.println(i+"\t"+(double)count1/number+"\t"+(double)count2/number+"\t"+(double)count3/number+"\t"+(double)count4/number+"\t"+(double)count5/number);
		}
		out.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void main2(){

		String filename = "E:\\Experiments\\IM\\networks\\BA_1000_2_0.txt";
		TopkComponents topk = new TopkComponents();
		
		double p = 0.005;
		MultiEdgesDirectedGraph g  = MultiEdgesDirectedGraph.ConstructFromUndirectedFile(filename);
		int number = 10000;
		int k_num = 11;
    	double[][] scoresk = new double[g.nodes.size()][k_num];
    	//g = null;
		String rsfile = filename.replace(".txt", "_"+p+"_"+number+".txt");
		for(inter_num=0; inter_num<number; inter_num++){
	    	 MultiEdgesDirectedGraph gtemp  = MultiEdgesDirectedGraph.ConstructFromUndirectedFile(filename);
	    	 topk.deleteGraph(gtemp, p, scoresk);
	    	 gtemp = null;
		}
		
		try{
			PrintWriter out= new PrintWriter(new BufferedWriter(new FileWriter(rsfile)));
		//stat
		for(int i=0; i<g.nodes.size(); i++){
			out.print(i+"\t");
			for(int j=0; j<k_num; j++){
				scoresk[i][j] = scoresk[i][j]/number;
				out.print((double)scoresk[i][j]+"\t");
			}
			out.println();
			//if((double)scoresk[i][1] > 0.005) 
			//System.out.println(i+"\t"+(double)scoresk[i][1]/number+"\t"+(double)scoresk[i][2]/number+"\t"+(double)scoresk[i][3]/number+"\t"+(double)scoresk[i][4]/number+"\t"+(double)scoresk[i][5]/number);
		    //out.println(i+"\t"++"\t"+(double)scoresk[i][2]/number+"\t"+(double)scoresk[i][3]/number+"\t"+(double)scoresk[i][4]/number+"\t"+(double)scoresk[i][5]/number);
		    
		}
		out.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void maxSpread(){
		String filename = "E:\\Experiments\\IM\\networks\\BA_1000_2_0.txt";
		TopkComponents topk = new TopkComponents();
		double p = 0.4;
		int number = 20000;
		double[][] maxspreadk = new double[number][50];
		for(int i=0; i<number; i++)
			for(int j=0; j<50; j++)
				maxspreadk[i][j] = 0;
		
		//String rsfile = filename.replace(".txt", "_"+p+"_"+number+".txt");
		for(inter_num=0; inter_num<number; inter_num++){
	    	 MultiEdgesDirectedGraph gtemp  = MultiEdgesDirectedGraph.ConstructFromUndirectedFile(filename);
	    	 topk.deleteGraphMaxSpread(gtemp, p, maxspreadk );
	    	 gtemp = null;
		}
		for(int i=1; i<number; i++){
			for(int j=0; j<50; j++)
				maxspreadk[0][j] = maxspreadk[0][j] + maxspreadk[i][j];
		}
		for(int i=0; i<50; i++)
			System.out.println((i+1)+"\t"+maxspreadk[0][i]/number);
	}

	public void deleteGraph(MultiEdgesDirectedGraph g, double p){
   	 //delete the edges whose related propagtion small than p
   	 List<Node> removeFans = null;
   	 for(Node node: g.getNodes()){
   		 removeFans = new ArrayList<Node>();
   		 try{
        		 for(Node fan: node.getFans()){
    			    if(node.getNodeID() > fan.getNodeID())
    			    	continue;
    			    if(Math.random() > p){
    			    	removeFans.add(fan);
    			    }
       		 }
   		 }catch(Exception e){
   			 e.printStackTrace();
   		 }
   		 for(Node removeFan: removeFans){
   			 //System.out.println("remove edge: "+ node.id+"\t"+removeFan.id);
   			 node.fans.remove(removeFan);
   			 node.leaders.remove(removeFan);
   			 removeFan.leaders.remove(node);
   			 removeFan.fans.remove(node);
   		 }
   	 }
}

	public int[][] statSeedsRank(MultiEdgesDirectedGraph g, int[] seeds){
		caculateComponentsandRank(g);
	
		int[][] rank = new int[2][seeds.length];
		    for(int i = 0; i < seeds.length; i++){
		       Node node = g.getNodeByID(seeds[i]);
		       rank[0][i] = node.rank;
		       rank[1][i] = node.pre_component_size;
		    }
		
		return rank;
	}
	
	public void caculateComponentsandRank(MultiEdgesDirectedGraph g){

		int maxsize = 0;
		//int maxMapID = 0;
		int[] sizeofcomponent = new int[g.getGraphSize()+1];
		
		//initial
		for(Node node: g.nodes){
			node.componentID = -1;
		}
		
		//stat
		List<Node> restnodes = new ArrayList<Node>(g.nodes);
		
		int componentID = 0;
		while(restnodes.size() > 0){
			componentID ++;
			
	        //BFS
			List<Node> onecomponent = new ArrayList<Node>();
			List<Node> previsit = new ArrayList<Node>();
			List<Node> temp = new ArrayList<Node>();
			previsit.add(restnodes.get(0));
			while(previsit.size()>0){
				for(Node node: previsit){
					for(Node fan: node.fans){
						if(onecomponent.contains(fan) || temp.contains(fan)||previsit.contains(fan))
							continue;
						temp.add(fan);
					}
				}
				for(Node node: previsit)
					onecomponent.add(node);
				previsit = new ArrayList<Node>(temp);
				temp.clear();
			}
			
			for(Node node: onecomponent){
				node.componentID = componentID;
				restnodes.remove(node);
				node.pre_component_size = onecomponent.size();
			}
			
			sizeofcomponent[componentID] = onecomponent.size();
		}
		
//		int[] tempSize = new int[componentID];
//		for(int i = 1; i < componentID+1; i++){
//			tempSize[i-1] = sizeofcomponent[i];
//		}
		Set<Integer> temp = new HashSet<Integer>();
		for(int i=1; i<componentID; i++)
			temp.add(sizeofcomponent[i]);
		int[] tempSize = new int[temp.size()];
		Iterator iter = temp.iterator();
		int tempi = 0;
		while(iter.hasNext()){
			tempSize[tempi] = (Integer)iter.next();
			tempi++;
		}
		
		
	   	Arrays.sort(tempSize);
	   	
		for(Node node: g.nodes){
			int rank = 0;
			for(int i=tempSize.length-1; i>=0; i--){
				if(node.pre_component_size == tempSize[i]){
					rank = tempSize.length - i;
//					if(rank==1)
//						System.out.println("rank = 1");
					break;
				}
			}
			node.rank = rank;
//			if(node.rank==1)
//				System.out.println("rank 1:\t"+node.id+"\t"+node.componentID);
//			if(node.rank==2)
//				System.out.println("rank 2:\t"+node.id+"\t"+node.componentID);
//			
		}
		//System.out.println("----------------------");
	}
	
	public void deleteGraph(MultiEdgesDirectedGraph g, double p, int[][] scores){
	    	 //delete the edges whose related propagtion small than p

	    	 List<Node> removeFans = null;
	    	 for(Node node: g.getNodes()){
	    		 removeFans = new ArrayList<Node>();
	    		 try{
	         		 for(Node fan: node.getFans()){
	     			    if(node.getNodeID() > fan.getNodeID())
	     			    	continue;
	     			    if(Math.random() > p){
	     			    	removeFans.add(fan);
	     			    }
	        		 }
	    		 }catch(Exception e){
	    			 e.printStackTrace();
	    		 }
	    		 for(Node removeFan: removeFans){
	    			 //System.out.println("remove edge: "+ node.id+"\t"+removeFan.id);
	    			 node.fans.remove(removeFan);
	    			 node.leaders.remove(removeFan);
	    			 removeFan.leaders.remove(node);
	    			 removeFan.fans.remove(node);
	    		 }
	    	 }
	    	 
	    	 //statistic the distribution of components 
	    	 cacluateComponents(g, scores);
	    	 //g = null;
	    	 //return topk;
}
	
	public void deleteGraph(MultiEdgesDirectedGraph g, double p, double[][] scoresk){
   	 //delete the edges whose related propagtion small than p

   	 List<Node> removeFans = null;
   	 for(Node node: g.getNodes()){
   		 removeFans = new ArrayList<Node>();
   		 try{
        		 for(Node fan: node.getFans()){
    			    if(node.getNodeID() > fan.getNodeID())
    			    	continue;
    			    if(Math.random() > p){
    			    	removeFans.add(fan);
    			    }
       		 }
   		 }catch(Exception e){
   			 e.printStackTrace();
   		 }
   		 for(Node removeFan: removeFans){
   			 //System.out.println("remove edge: "+ node.id+"\t"+removeFan.id);
   			 node.fans.remove(removeFan);
   			 node.leaders.remove(removeFan);
   			 removeFan.leaders.remove(node);
   			 removeFan.fans.remove(node);
   		 }
   	 }
   	 
   	 //statistic the distribution of components 
   	 cacluateComponents(g, scoresk);
   	 //g = null;
   	 //return topk;
}
	
	
	
	public void deleteGraphMaxSpread(MultiEdgesDirectedGraph g, double p, double[][] scoresk){
	   	 //delete the edges whose related propagtion small than p
	   	 List<Node> removeFans = null;
	   	 for(Node node: g.getNodes()){
	   		 removeFans = new ArrayList<Node>();
	   		 try{
	        		 for(Node fan: node.getFans()){
	    			    if(node.getNodeID() > fan.getNodeID())
	    			    	continue;
	    			    if(Math.random() > p){
	    			    	removeFans.add(fan);
	    			    }
	       		 }
	   		 }catch(Exception e){
	   			 e.printStackTrace();
	   		 }
	   		 for(Node removeFan: removeFans){
	   			 //System.out.println("remove edge: "+ node.id+"\t"+removeFan.id);
	   			 node.fans.remove(removeFan);
	   			 node.leaders.remove(removeFan);
	   			 removeFan.leaders.remove(node);
	   			 removeFan.fans.remove(node);
	   		 }
	   	 }
	   	 
	   	 //statistic the distribution of components 
	   	 cacluateComponentsMaxSpread(g, scoresk);
	   	 //g = null;
	   	 //return topk;
	}
	
	
	public int[] topkid(int componentID, int maxsize, MultiEdgesDirectedGraph g){
		
		int[] topid = new int[maxsize];
		int i = 0;
		for(Node node: g.nodes){
			if(node.componentID == componentID){
				topid[i] = node.id;
			}
		}
		return topid;
	}
	
	
public void cacluateComponents(MultiEdgesDirectedGraph g, int[][] scores){
	int maxsize = 0;
	int maxMapID = 0;
	int[] sizeofcomponent = new int[g.getGraphSize()+1];
	
	//initial
	for(Node node: g.nodes){
		node.componentID = -1;
	}
	
	//stat
	List<Node> restnodes = new ArrayList<Node>();
	for(Node node: g.nodes){
		restnodes.add(node);
	}
	
	int componentID = 0;
	while(restnodes.size() > 0){
		componentID ++;
		
        //BFS
		List<Node> onecomponent = new ArrayList<Node>();
		List<Node> previsit = new ArrayList<Node>();
		List<Node> temp = new ArrayList<Node>();
		previsit.add(restnodes.get(0));
		while(previsit.size()>0){
			for(Node node: previsit){
				for(Node fan: node.fans){
					if(onecomponent.contains(fan) || temp.contains(fan))
						continue;
					temp.add(fan);
				}
			}
			for(Node node: previsit)
				onecomponent.add(node);
			previsit = new ArrayList<Node>();
			for(Node node: temp){
				previsit.add(node);
			}
			temp = new ArrayList<Node>();
		}
		
		for(Node node: onecomponent){
			node.componentID = componentID;
			restnodes.remove(node);
			node.pre_component_size = onecomponent.size();
		}
		
		sizeofcomponent[componentID] = onecomponent.size();
		if(onecomponent.size() > maxsize)
		{
			maxsize = onecomponent.size();
			maxMapID = componentID;
		}
	}
	
	int[] tempSize = new int[componentID];
	for(int i = 1; i < componentID+1; i++){
		tempSize[i-1] = sizeofcomponent[i];
	}
	
   	Arrays.sort(tempSize);
	for(Node node: g.nodes){
		if(node.pre_component_size == tempSize[componentID-1])
			scores[node.id][inter_num] = 1;
		if(node.pre_component_size == tempSize[componentID-2])
			scores[node.id][inter_num] = 2;
		if(node.pre_component_size == tempSize[componentID-3])
			scores[node.id][inter_num] = 3;
		if(node.pre_component_size == tempSize[componentID-4])
			scores[node.id][inter_num] = 4;
		if(node.pre_component_size == tempSize[componentID-5])
			scores[node.id][inter_num] = 5;
	}
	}


public void cacluateComponentsMaxSpread(MultiEdgesDirectedGraph g, double[][] scores){
	int maxsize = 0;
	int maxMapID = 0;
	int[] sizeofcomponent = new int[g.getGraphSize()+1];
	
	//initial
	for(Node node: g.nodes){
		node.componentID = -1;
	}
	
	//stat
	List<Node> restnodes = new ArrayList<Node>();
	for(Node node: g.nodes){
		restnodes.add(node);
	}
	
	int componentID = 0;
	while(restnodes.size() > 0){
		componentID ++;
		
        //BFS
		List<Node> onecomponent = new ArrayList<Node>();
		List<Node> previsit = new ArrayList<Node>();
		List<Node> temp = new ArrayList<Node>();
		previsit.add(restnodes.get(0));
		while(previsit.size()>0){
			for(Node node: previsit){
				for(Node fan: node.fans){
					if(onecomponent.contains(fan) || temp.contains(fan))
						continue;
					temp.add(fan);
				}
			}
			for(Node node: previsit)
				onecomponent.add(node);
			previsit = new ArrayList<Node>();
			for(Node node: temp){
				previsit.add(node);
			}
			temp = new ArrayList<Node>();
		}
		
		for(Node node: onecomponent){
			node.componentID = componentID;
			restnodes.remove(node);
			node.pre_component_size = onecomponent.size();
		}
		
		sizeofcomponent[componentID] = onecomponent.size();
		if(onecomponent.size() > maxsize)
		{
			maxsize = onecomponent.size();
			maxMapID = componentID;
		}
	}
	
	int[] tempSize = new int[componentID];
	for(int i = 1; i < componentID+1; i++){
		tempSize[i-1] = sizeofcomponent[i];
	}
	double[] tempScores = new double[51];
   	Arrays.sort(tempSize);
   	
   	//scores[1] = tempSize[componentID-1];
	for(int i=1; i<51; i++){
		tempScores[i] = tempScores[i-1] + tempSize[componentID-i];
	}
	
	for(int i=0; i<50; i++){
		scores[inter_num][i] = tempScores[i+1]; 
	}
}


public void cacluateComponents(MultiEdgesDirectedGraph g, double[][] scores){
	int maxsize = 0;
	int maxMapID = 0;
	int[] sizeofcomponent = new int[g.getGraphSize()+1];
	
	//initial
	for(Node node: g.nodes){
		node.componentID = -1;
	}
	
	//stat
	List<Node> restnodes = new ArrayList<Node>();
	for(Node node: g.nodes){
		restnodes.add(node);
	}
	
	int componentID = 0;
	while(restnodes.size() > 0){
		componentID ++;
		
        //BFS
		List<Node> onecomponent = new ArrayList<Node>();
		List<Node> previsit = new ArrayList<Node>();
		List<Node> temp = new ArrayList<Node>();
		previsit.add(restnodes.get(0));
		while(previsit.size()>0){
			for(Node node: previsit){
				for(Node fan: node.fans){
					if(onecomponent.contains(fan) || temp.contains(fan))
						continue;
					temp.add(fan);
				}
			}
			for(Node node: previsit)
				onecomponent.add(node);
			previsit = new ArrayList<Node>();
			for(Node node: temp){
				previsit.add(node);
			}
			temp = new ArrayList<Node>();
		}
		
		for(Node node: onecomponent){
			node.componentID = componentID;
			restnodes.remove(node);
			node.pre_component_size = onecomponent.size();
		}
		
		sizeofcomponent[componentID] = onecomponent.size();
		if(onecomponent.size() > maxsize)
		{
			maxsize = onecomponent.size();
			maxMapID = componentID;
		}
	}
	
	int[] tempSize = new int[componentID];
	for(int i = 1; i < componentID+1; i++){
		tempSize[i-1] = sizeofcomponent[i];
	}
	
   	Arrays.sort(tempSize);
	for(Node node: g.nodes){
		int rank = 0;
		for(int i=0; i<tempSize.length; i++){
			if(node.pre_component_size == tempSize[i]){
				rank = tempSize.length - i;
				break;
			}
		}
		if(rank < 11){
			scores[node.id][rank] = scores[node.id][rank] +1.0;
		}
	}
	}
}