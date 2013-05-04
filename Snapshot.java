import java.util.ArrayList;


public class Snapshot {

	ArrayList<ArrayList<Integer>> snapshot;
	//int[] ranksize;

	public Snapshot(int k){
		snapshot = new ArrayList<ArrayList<Integer>>();
		//ranksize = new int[k];
		for(int i=0; i<k; i++){
			snapshot.add(new ArrayList<Integer>());
		}
	}
	
    public ArrayList<ArrayList<Integer>> getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(ArrayList<ArrayList<Integer>> snapshot) {
        this.snapshot = snapshot;
    }

    public Snapshot(int k, MultiEdgesDirectedGraph g){
        snapshot = new ArrayList<ArrayList<Integer>>();
        //ranksize = new int[k];
        for(int i=0; i<k; i++){
            snapshot.add(new ArrayList<Integer>());
        }
        
        for(Node node: g.nodes){
            snapshot.get(node.componentID-1).add(node.id);
        }
        
       // System.out.print("snapshot"+k);
    }
    
    public void print(){
        for(ArrayList<Integer> temp: snapshot){
            for(int i: temp)
                System.out.print(i+"\t");
            System.out.println();
        }
    }
    
//	public ArrayList<Integer> getCertainShapshot(int rank){
//		return snapshot.get(rank-1);
//	}

//	public int[] getRanksize() {
//		return ranksize;
//	}
//
//	public void setRanksize(int[] ranksize) {
//		this.ranksize = ranksize;
//	}
//	
//	public void setRanksize(int i, int value){
//		ranksize[i-1] = value;
//	}
//	
//	public int getRanksize(int i){
//		return ranksize[i-1];
//	}
    
//    public static Snapshot generateSimpleSnapshot(MultiEdgesDirectedGraph g){
//          Snapshot snapshot = null;
//          
//          return snapshot;
//    }
}
