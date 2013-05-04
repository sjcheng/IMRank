import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;


public class Richclub {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String root = "C:\\Experiments\\influence\\";
		String gfile = "goodreads.txt";
		String rank = Rank.PAGERANK;
		DirectedGraph dg = DirectedGraph.ConstructFromFile(root+gfile);
		Richclub rc = new Richclub();
		Node[] rankedNodes = rc.getRankfile(root, gfile, rank, dg);
		rc.getRichclub(1000, rankedNodes, dg);
	}

	public void getRichclub(int number, Node[] rankedNodes, DirectedGraph dg){
//		for(int i=1; i<=number; i++)
//			System.out.println(rankedNodes[i].getNodeID());
			
		for(int i=10; i<=number; i=i+10){
			//count the links
			int count = 0;
			for(int j=1; j<=i; j++){
				Node node = rankedNodes[j];
				for(int k=j+1; k<i; k++){
					Node nei = rankedNodes[k];
					if(node.hasFan(nei))
					{
						//System.out.println(nei.getNodeID()+"\t"+node.getNodeID());
						count++;
					}if(node.hasLeader(nei)){
						//System.out.println(node.getNodeID()+"\t"+nei.getNodeID());
						count++;
					}
				}
				node = null;
			}
			System.out.println(i+"\t"+count+"\t"+((double)count)/(i*(i-1)));
		}
	}
	
	public Node[] getRankfile(String root, String gfile, String rankkind, DirectedGraph dg){
		Node[] rankedNode = new Node[dg.getGraphSize()+1];
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
                	rankedNode[node.rank] = node;
                	
                }catch(Exception e){
                	e.printStackTrace();
                }
                
            }
        }catch(Exception e){
        	e.printStackTrace();
        }
        return rankedNode;
	}
}
