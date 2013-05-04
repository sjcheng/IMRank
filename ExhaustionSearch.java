
import java.util.List;
import java.util.ArrayList;

public class ExhaustionSearch {

	DirectedGraph graph;
	double p;
	int iter_num = 20000;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String filename = "C:\\smallnetnewfinal.txt";
		ExhaustionSearch search = new ExhaustionSearch(filename, 0.8);
		search.exhaustion(2);
	}

	public ExhaustionSearch(String graphFile, double p){
		graph = DirectedGraph.ConstructFromUndirectedFile(graphFile);
		this.p = p;
	}
	
	
	public void exhaustion(int k){
		List<Node> allnodes = graph.getNodes();
		List<Node> selected = new ArrayList<Node>();
		exhaustiveSearch(selected, allnodes, k, 0 );
	}
	
	public void exhaustiveSearch(List<Node> selected, List<Node> allnodes, int k, int newloc){
		int need = k - selected.size();
		int choice = allnodes.size() - newloc;

		if((need < choice) || need == choice ){
			if(need == 1){
				for(int j = 0; j < choice; j++){
			    	selected.add(allnodes.get(j+newloc));
			     	double result = runICModel(selected);
			    	System.out.print(result+"\t:\t");
		    		for(Node node: selected){
		     			System.out.print(node.id+" ");
		    		}
		    		System.out.println();
		    		//remove them
			    	selected.remove(allnodes.get(j+newloc));
				}
			}else{
				for(; newloc < allnodes.size()-need+1; newloc++){
		    		selected.add(allnodes.get(newloc));
		    	   	exhaustiveSearch(selected, allnodes, k, newloc+1);
		    		selected.remove(allnodes.get(newloc));
				}
			}
		}else{
			//need < choice
			return;
		}
	
	}
	
	public double runICModel(List<Node> seeds){
		double ave_spread_size = 0;
		//IC process  
		for(int i=0; i<iter_num; i++){
			ave_spread_size = ave_spread_size + runICModelProcess(seeds);
		}
		ave_spread_size = (double)ave_spread_size/(double)iter_num;
		return ave_spread_size;
	}
	
	public int runICModelProcess(List<Node> seeds){
		int spread_size = 0;
		//initial graph
		for(Node node: graph.getNodes()){
			node.pre_status = 0;//0 means it is inactive
		}
		for(Node node: seeds){
			node.pre_status = 1;//1 means it is active
		}
		//cascade
		List<Node> preactived = new ArrayList<Node>();
		List<Node> tempactived = new ArrayList<Node>();
		
		for(Node node: seeds){
			preactived.add(node);
		}
		
		while(preactived.size() > 0){
			for(Node source: preactived){
				//active its neighbors
				for(Node nei: source.getFans()){
					if(nei.pre_status == 1)
						continue;
					if(Math.random() < p){
						nei.pre_status = 1;
						tempactived.add(nei);
					}
				}
			}
			
			//mark next step active nodes
			preactived =  new ArrayList<Node>();
			for(Node node: tempactived){
				preactived.add(node);
			}
			tempactived = new ArrayList<Node>();
		}
		for(Node node: graph.getNodes()){
			if(node.pre_status==1)
				spread_size++;
		}
		return spread_size;
	}
}
