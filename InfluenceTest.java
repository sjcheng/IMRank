import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InfluenceTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

        /*
        String filename = "E:/Experiments/IMSolutions/karate_edgelist_0.txt";
        int[] seeds = {33};
        double p=0.2;
        int R = 10000;
        int iterNum = 5000;
        InfluenceTest test = new InfluenceTest();
        test.getInfluence(filename, seeds, R, iterNum, p);
        */
        InfluenceTest test = new InfluenceTest();
        String filename = "D:/SF_1000_r2_TrueAss30000.txt";
        test.testAllNodes(filename, 20000, 0.06);
        test.testAllNodes(filename, 20000, 0.07);
        test.testAllNodes(filename, 20000, 0.08);
        test.testAllNodes(filename, 20000, 0.09);
        //test.testAllNodes(filename, 20000, 0.01);
        //test.testAllNodes(filename, 20000, 0.02);
        //test.testAllNodes(filename, 20000, 0.03);
        //test.testAllNodes(filename, 20000, 0.04);
        //test.testAllNodes(filename, 20000, 0.05);
        //test.testAllNodes(filename, 20000, 0.1);
    }

    public void testAllNodes(String filename, int R, double p){
        System.out.println("filename="+filename+"\tR="+R+"\tp="+p);
        MultiEdgesDirectedGraph g = MultiEdgesDirectedGraph.ConstructFromUndirectedFile(filename);
        SeedsInfluenceSpread spread = new SeedsInfluenceSpread(g);
        List<Node> seeds = new ArrayList<Node>();
        Map rs;
        for(int i=0; i<g.getGraphSize(); i++){
            Node node = g.getNodeByIndex(i);
            if(node.getDegree() <= 10)
                continue;
            seeds.clear();
            seeds.add(node);
            rs = spread.getInfluenceSpread(seeds, p, R);
            System.out.println(node.getNodeID()+"\t"+node.getDegree()+"\t"+rs.get("AVE_SPREAD_SIZE")+"\t"+rs.get("VAR"));
        }

    }
    
    public void getInfluence(String filename, int[] seeds, int R, int iterNum, double p){
        MultiEdgesDirectedGraph g = MultiEdgesDirectedGraph.ConstructFromUndirectedFile(filename);
        SeedsInfluenceSpread spread = new SeedsInfluenceSpread(g);
        List<Node> newseeds = new ArrayList<Node>();
        for (int j = 0; j < seeds.length; j++)
            newseeds.add(g.getNodeByID(seeds[j]));

        Map rs = spread.getInfluenceSpread(newseeds, p, R);
        while(iterNum>0){
            rs = spread.getInfluenceSpread(newseeds, p, R);
             System.out.println("R="+R+"\t"+Double.parseDouble(rs.get("AVE_SPREAD_SIZE").toString()));
             iterNum--;
        }
       
    }
    
    
}
