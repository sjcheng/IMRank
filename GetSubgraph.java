import java.util.ArrayList;
import java.util.List;

public class GetSubgraph {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        String graphfile = "E:/Experiments/IMSolutions/karate_edgelist_0.txt";
        int[] top10p03 = { 33, 0, 16, 24, 4, 26, 11, 12, 17, 14 };
        int[] top10p01 = { 33, 0, 32, 1, 24, 16, 4, 2, 25, 26 };
        graphfile = "E:/Experiments/IMSolutions/hep.txt";
        //graphfile = "E:/Experiments/IMSolutions/phy.txt";
        int[] heptop10 = {562, 359};//{ 131, 287, 639, 267, 100, 608, 559, 124, 359, 80 };
        int[] phytop10 = {10054, 10041, 10068, 10057, 10051, 10040, 10046, 10061, 10044, 10062, 10065, 10058, 10052, 10047, 10048, 10067
                ,10042, 10050, 10069, 10055, 10053, 10066, 10045, 10056, 10064, 10059, 10060}; //{17529, 17540, 17541, 17527, 17533 };// {25609, 17544, 13566, 8515, 12650, 2034, 5551, 15758, 17540, 5567,
                                            // 22795};
        GetSubgraph test = new GetSubgraph();
        for (int i = 2; i < 10; i++) {
            System.out.println("top-" + i + "\t");
            test.getSubgraph(heptop10, graphfile, i);
            test.getCommon(heptop10, graphfile, i);
        }
    }

    public void getSubgraph(int[] ids, String graphfile, int k) {
        MultiEdgesDirectedGraph g = MultiEdgesDirectedGraph.ConstructFromUndirectedFile(graphfile);
        List<Node> seeds = new ArrayList<Node>();
        for (int j = 0; j < k; j++)
            seeds.add(g.getNodeByID(ids[j]));
        // print out the related edges
        getRelatedEdges(seeds, g, false, true);
    }

    public void getCommon(int[] ids, String graphfile, int k){
        MultiEdgesDirectedGraph g = MultiEdgesDirectedGraph.ConstructFromUndirectedFile(graphfile);
        List<Node> seeds = new ArrayList<Node>();
        for (int j = 0; j < k; j++)
            seeds.add(g.getNodeByID(ids[j]));
        // print out the related edges
        getCommonNeighbor(seeds, g);
    }
    
    public void getRelatedEdges(List<Node> seeds, MultiEdgesDirectedGraph g, boolean directed, boolean limit) {
        for (int i = 0; i < seeds.size(); i++) {
            Node seed = seeds.get(i);
            List<Node> neis = seed.getFans();
            for (int j = 0; j < neis.size(); j++) {
                Node nei = neis.get(j);
                if (limit) {// only output the edges between nodes in seeds
                    if (seeds.contains(nei)) {
                        if (directed) {
                            System.out.println(seed.getNodeID() + "\t" + nei.getNodeID());
                        } else {
                            if (seed.getNodeID() > nei.getNodeID())
                                System.out.println(seed.getNodeID() + "\t" + nei.getNodeID());
                        }
                    }
                } else {
                    if (directed) {
                        System.out.println(seed.getNodeID() + "\t" + nei.getNodeID());
                    } else {
                        if (seeds.contains(nei)) {
                            if (seed.getNodeID() > nei.getNodeID())
                                System.out.println(seed.getNodeID() + "\t" + nei.getNodeID());
                        } else {
                            System.out.println(seed.getNodeID() + "\t" + nei.getNodeID());
                        }

                    }
                }
            }
        }

    }

    public void getCommonNeighbor(List<Node> seeds, MultiEdgesDirectedGraph g) {
        //MultiEdgesDirectedGraph g = MultiEdgesDirectedGraph.ConstructFromUndirectedFile(graphfile);
        int sum = 0;
        for (int i = 0; i < seeds.size(); i++) {
            Node seed = seeds.get(i);
           
            List<Node> neis = seed.getFans();
            List<Node> newneis = new ArrayList<Node>();
           
            for(int k=0; k<neis.size(); k++){
                if(newneis.contains(neis.get(k)))
                    continue;
                newneis.add(neis.get(k)); 
            }
            
            for(int j = i+1; j<seeds.size(); j++){
                int commontemp = 0;
                
                List<Node> neis2 = seeds.get(j).getFans();
                List<Node> newneis2 = new ArrayList<Node>();
               
                for(int k=0; k<neis2.size(); k++){
                    if(newneis2.contains(neis2.get(k)))
                        continue;
                    newneis2.add(neis2.get(k)); 
                }
                
                System.out.print(seed.getNodeID()+"\t"+newneis.size()+"\t"+seeds.get(j).getNodeID()+"\t"+newneis2.size()+"\t");
                for (int k = 0; k < newneis.size(); k++) {
                    Node nei = newneis.get(k);
                    if(newneis2.contains(nei))
                    {
                        sum++;
                        commontemp ++;
                    }
                        
                }
                System.out.println(commontemp);
            }
        }
        System.out.println(2*(double)sum/(double)(seeds.size()*(seeds.size()-1)));
    }

}
