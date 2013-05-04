import java.util.List;
import java.util.ArrayList;

public class PageRankAnalysis {

	/**
	 * @param args
	 */
	static int step = 51;
	static String root ="C:\\Experiments\\influence\\";
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String gfile = "C:\\Experiments\\influence\\as_cn_only.txt";
		//String rsfile = "F:\\Chengsuqi\\Experiments\\dataset\\influence\\testG-pr.txt";
		DirectedGraph g = DirectedGraph.ConstructFromFile(gfile);
		System.out.println("begin to read in graph");
		PageRankAlgorithm pr = new PageRankAlgorithm();
		System.out.println("begin to caculate score");
		pr.calculateScore(g);
		pr.rank(g);
		pr.savetoFile(g, root+50+".txt");
		System.out.println("begin to analysis score");
		PageRankAnalysis pra = new PageRankAnalysis();
		pra.preProcessGraphPageRank(g);
		//pra.iterativePRScore(g);
		//pra.analysisPRScore(g);
		//pr.savetoFile(rsfile);
	}

	public PageRankAnalysis(){
		
	}
	
	public void analysisPRScore(DirectedGraph g){
		int count1 = 0;//count the number of links which is from low node to high node
		int count2 = 0;//count the number of links A.rspre-node.rspre = 1;
		for(Node node: g.getNodes()){
			//get its fans
			for(Node fan: node.getFans()){
				if(fan.rspre > node.rspre){
					count1 ++;
				}
				
				if((fan.rspre - node.rspre)>1){
					count2 ++;
					System.out.println(fan.getNodeID()+" --> "+node.getNodeID());
				}
			}
		}
		System.out.println(count1+"\t"+(double)count1/(double)g.getGraphEdgeNum()+"\t"+count2+"\t"+(double)count2/(double)g.getGraphEdgeNum());
	}
	
	public void iterativePRScore(DirectedGraph g){
		int count1 = 0;//count the number of links which is from low node to high node
		int count2 = 0;//count the number of links A.rspre-node.rspre = 1;
		int origraphedge = g.getGraphEdgeNum();
		List<Node> removeFans = null;
		for(Node node: g.getNodes()){
			//get its fans
			removeFans = new ArrayList<Node>();
			for(Node fan: node.getFans()){
				if(fan.rspre > node.rspre){
					count1 ++;
					//System.out.println(fan.getNodeID()+" --> "+node.getNodeID());
					removeFans.add(fan);
				}
			
//				if((fan.rspre - node.rspre)>1){
//					count2 ++;
//				}
			}
            for(Node fan: removeFans){
				node.deleteFan(fan);
				fan.deleteLeader(node);
			}
		}
		System.out.println(count1+"\t"+(double)count1/(double)origraphedge+"\t"+count2+"\t"+(double)count2/(double)origraphedge);
		PageRankAlgorithm pr = new PageRankAlgorithm();
		System.out.println("begin to caculate score");
		pr.calculateScore(g);
		pr.rank(g);
		String rsfile = root + step +".txt";
		pr.savetoFile(g, rsfile);
		step++;
		//pr.printOut(g);
		if(count1==0) return;
		iterativePRScore(g);
	}
	
	//delete some edges
	public void preProcessGraphPageRank(DirectedGraph g){
		int count1 = 0;//count the number of links which is from low node to high node
		int count2 = 0;//count the number of links A.rspre-node.rspre = 1;
		int origraphedge = g.getGraphEdgeNum();
		
		for(Node node: g.getNodes()){
			node.rsafter = node.getFans().size();
		}
		
		List<Node> removeFans = null;
		for(Node node: g.getNodes()){
			//get its fans
			removeFans = new ArrayList<Node>();
			for(Node fan: node.getFans()){
				if(fan.rsafter > node.rsafter){
					count1 ++;
					//System.out.println(fan.getNodeID()+" --> "+node.getNodeID());
					removeFans.add(fan);
				}
			
			}
            for(Node fan: removeFans){
				node.deleteFan(fan);
				fan.deleteLeader(node);
			}
		}
		
		//add self-loop
		int localNum = 0;
		for(Node node: g.getNodes()){
			if(node.getLeaders().size()==0)
			{
				node.addFan(node);
				node.addLeader(node);
				localNum++;
			}
		}
		
		System.out.println(count1+"\t"+(double)count1/(double)origraphedge+"\t"+count2+"\t"+(double)count2/(double)origraphedge);
		System.out.println(localNum);
		
		PageRankAlgorithm pr = new PageRankAlgorithm();
		System.out.println("begin to caculate score");
		pr.calculateScore(g);
		pr.rank(g);
		String rsfile = root + 100 +".txt";
		pr.savetoFile(g, rsfile);
	}
}
