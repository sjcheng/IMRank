import java.util.ArrayList;
import java.util.List;
import java.io.*;
import java.util.Map;

public class NodeAttackAffect {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

        String graphfile = "E:/Experiments/IMSolutions/karate_edgelist_0.txt";
        int[] top10p03 = {33, 0, 16,  24,  4, 26,  11,  12,  17,  14};
        int[] top10p01 = {33,   0,   32,  1,   24,  16,  4,   2,   25,  26};
        NodeAttackAffect test = new NodeAttackAffect();
        test.testAllNodes(graphfile);
       
    }

    public void testAllNodes(String graphfile){
        MultiEdgesDirectedGraph g = MultiEdgesDirectedGraph.ConstructFromUndirectedFile(graphfile);
        List<Integer> removeNodes = new ArrayList<Integer>();
        double p = 0.3;
        //influenceMax(graphfile, removeNodes, 1, p);
        for(int i=0; i<g.getGraphSize(); i++){
            removeNodes.clear();
            removeNodes.add(g.getNodes().get(i).getNodeID());
            influenceMax(graphfile, removeNodes, 1, p);
        }
        
    }
    
    public void attackNode(List<Integer> removeNodes, MultiEdgesDirectedGraph g){
       for(int i=0; i<removeNodes.size(); i++){
          // Node temp = g.getNodeByID(33);
           //g.removeNode(temp);
           Node temp = g.getNodeByID(removeNodes.get(i));
           g.removeNode(temp);
       }
    }
    
    public double[] influenceMax(String graphfile, List<Integer> removeNodes, int k, double p){
        MultiEdgesDirectedGraph g = MultiEdgesDirectedGraph.ConstructFromUndirectedFile(graphfile);
        attackNode(removeNodes, g);
        TopkComponentsForUniformIC topk = new TopkComponentsForUniformIC();
        
        String tempgfile = "E:/tempgfile.txt";
        g.saveUnDirectedGraphToFile(tempgfile);
        
        g = MultiEdgesDirectedGraph.ConstructFromUndirectedFile(tempgfile);
        SeedsInfluenceSpread spread = new SeedsInfluenceSpread(g);
        
        double[] temp = topk.extractSeedsSG(tempgfile, k, p, 20000);
        System.out.println("seed:"+"\t"+temp[0]);
        
        List<Node> seeds = new ArrayList<Node>();
        for(int i=0; i<temp.length; i++)
            seeds.add(g.getNodeByID((int)temp[i]));
        Map rs = spread.getInfluenceSpread(seeds, p, 20000);
        for(int b=0; b<removeNodes.size(); b++){
        System.out.print("remove:\t"+ removeNodes.get(0) + "\t");
        System.out.println((int) temp[0] +"\t"+rs.get("AVE_SPREAD_SIZE")+"\t"+rs.get("VAR"));
    }
        if(removeNodes.size()==0)
          System.out.println("no remove:\t"+(int) temp[0]+"\t" +rs.get("AVE_SPREAD_SIZE")+"\t"+rs.get("VAR"));
        //delete this temp graph
        File fs =new File(tempgfile);
        fs.delete();
        return temp;
    }
}
