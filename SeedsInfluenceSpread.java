import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class SeedsInfluenceSpread {

    MultiEdgesDirectedGraph graph;

    // double[] p;
    int iter_num = 50000;

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        // main3();
        String filename = "E:\\Experiments\\IMSoultions\\networks\\hep.txt";
        int k = 50;
        int iter_num = 40000;
        String rs = filename.replace(".txt", "_WC_Ramdom_" + k + "_"+iter_num+".txt");

        SeedsInfluenceSpread spread = new SeedsInfluenceSpread(filename);
        List<Node> seeds = spread.getRandomSeeds(50);
        try {
            PrintWriter out = new PrintWriter(new File(rs));
            for (int i = 0; i < iter_num; i++) {
                int ssize = spread.runWeightedICModelProcess(seeds);
                out.println(i+"\t"+ssize);
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Node> getRandomSeeds(int k) {

        List<Node> seeds = new ArrayList<Node>();
        while (seeds.size() < k) {
            int index = (int) (Math.random() * graph.getGraphSize());
            Node temp = graph.getNodeByIndex(index);
            if (seeds.contains(temp))
                continue;
            seeds.add(temp);
        }
        return seeds;
    }

    public static void smallTest(Node oneNode) {

    }

    public static void main3() {
        String filename = "E:\\Experiments\\IM\\networks\\hep.txt";
        SeedsInfluenceSpread spread = new SeedsInfluenceSpread(filename);
        Node node = spread.graph.getNodeByID(646);
        spread.printNei(node);

        node = spread.graph.getNodeByID(3683);
        spread.printNei(node);
    }

    public void printNei(Node node) {
        for (Node nei : node.getFans()) {
            System.out.println(nei.id);
        }
        System.out.println();
    }

    // test a set of seeds in a given p
    public static void main2() {

        String filename = "E:\\Experiments\\IM\\networks\\hep.txt";
        String seedfilepre = "E:\\Experiments\\IM\\ConsoleCode\\HEP_10.24\\hep_";
        seedfilepre = "E:\\Experiments\\IM\\Results\\Greedy(CELF)\\hep_";
        // String[] p = {"0.005","0.01","0.015","0.02","0.025","0.03","0.035","0.04","0.045","0.05",
        // "0.055","0.06","0.065","0.07","0.075","0.08","0.085","0.09","0.095","0.10",
        // "0.11","0.12","0.13","0.14","0.15","0.16","0.17","0.18","0.19","0.20",
        // "0.21","0.22","0.23","0.24","0.24","0.25","0.26","0.27","0.28","0.29","0.30"};
        String[] p = { "0.004", "0.006", "0.008", "0.01", "0.012", "0.014", "0.016", "0.018", "0.02", "0.022", "0.024",
                "0.026", "0.028", "0.030", "0.032", "0.034", "0.036", "0.038", "0.04", "0.042", "0.044", "0.046" };
        // ,"0.044","0.046","0.048", "0.05","0.06","0.065","0.07","0.075","0.08",
        // "0.085","0.09","0.095","0.10","0.11","0.12","0.13","0.14","0.15","0.16","0.17",
        // "0.18","0.19","0.20","0.21","0.22","0.23","0.24","0.24","0.25","0.26","0.27",
        // "0.28","0.29","0.30"};
        SeedsInfluenceSpread spread = new SeedsInfluenceSpread(filename);

        for (int i = 1; i < 17; i++) {
            // for a given p's seed, test its performance under other p
            String seedsfile = seedfilepre + p[i] + "_CELF_seeds.txt";
            for (int j = 0; j < 17; j++) {
                List<Node> seeds = spread.readinSeeds(seedsfile);
                System.out.print(p[i] + "\t");
                spread.getInfluenceSpread(seeds, Double.parseDouble(p[j]));
            }
        }

    }

    public SeedsInfluenceSpread(String graphFile) {
        graph = MultiEdgesDirectedGraph.ConstructFromUndirectedFile(graphFile);
    }

    public SeedsInfluenceSpread(MultiEdgesDirectedGraph graph) {
        this.graph = graph;
    }

    public SeedsInfluenceSpread() {
    }
    
    // get seeds
    public List<Node> readinSeeds(String seedsfile) {
        List<Node> seeds = new ArrayList<Node>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(seedsfile)));
            String line;
            int count = 0;
            line = br.readLine();
            while ((line = br.readLine()) != null) {

                line = line.trim();
                if (line.length() == 0) {
                    System.out.println("line length is zero");
                    continue;
                }
                // line = line.replace('-',' ');
                StringTokenizer st = new StringTokenizer(line);
                int nodeid = -1;
                try {
                    nodeid = Integer.parseInt(st.nextToken());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                seeds.add(graph.getNodeByID(nodeid));
                count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return seeds;
    }

    
    public Map getInfluenceSpread(List<Node> seeds, double p) {
        return runICModel(seeds, p);
    }

    public Map getInfluenceSpread(List<Node> seeds, double p, int R) {
        return runICModel(seeds, p, R);
    }
    
    public Map<String, Double> getWCInfluenceSpread(List<Node> seeds) {
        return runWCModel(seeds);
    }

    public Map<String, Double> runICModel(List<Node> seeds, double p) {
        double ave_spread_size = 0;
        double[] spread = new double[iter_num];
        double var = 0;
        // IC process
        for (int i = 0; i < iter_num; i++) {
            spread[i] = runICModelProcess(seeds, p);
            // System.out.println(spread[i]);
            ave_spread_size = ave_spread_size + spread[i];
        }

        // average spread size
        ave_spread_size = (double) ave_spread_size / (double) iter_num;

        // var
        for (int i = 0; i < iter_num; i++) {
            var = var + (spread[i] - ave_spread_size) * (spread[i] - ave_spread_size);
        }

        var = var / iter_num;
        var = Math.sqrt(var);
        Map<String, Double> rs = new HashMap<String, Double>();
        rs.put("AVE_SPREAD_SIZE", ave_spread_size);
        rs.put("VAR", var);
        return rs;
        // System.out.println(p+"\t"+ave_spread_size+"\t"+var);
    }

    
    public Map<String, Double> runICModel(List<Node> seeds, double p, int R) {
        double ave_spread_size = 0;
        double[] spread = new double[R];
        double var = 0;
        // IC process
        for (int i = 0; i < R; i++) {
            spread[i] = runICModelProcess(seeds, p);
            // System.out.println(spread[i]);
            ave_spread_size = ave_spread_size + spread[i];
        }

        // average spread size
        ave_spread_size = (double) ave_spread_size / (double) R;

        // var
        for (int i = 0; i < R; i++) {
            var = var + (spread[i] - ave_spread_size) * (spread[i] - ave_spread_size);
        }

        var = var / R;
        var = Math.sqrt(var);
        Map<String, Double> rs = new HashMap<String, Double>();
        rs.put("AVE_SPREAD_SIZE", ave_spread_size);
        rs.put("VAR", var);
        return rs;
        // System.out.println(p+"\t"+ave_spread_size+"\t"+var);
    }
    
    public Map<String, Double> runWCModel(List<Node> seeds) {
        double ave_spread_size = 0;
        double[] spread = new double[iter_num];
        double var = 0;
        // IC process
        for (int i = 0; i < iter_num; i++) {
            spread[i] = runWeightedICModelProcess(seeds);
            // System.out.println(spread[i]);
            ave_spread_size = ave_spread_size + spread[i];
        }

        // average spread size
        ave_spread_size = (double) ave_spread_size / (double) iter_num;

        // var
        for (int i = 0; i < iter_num; i++) {
            var = var + (spread[i] - ave_spread_size) * (spread[i] - ave_spread_size);
        }

        var = var / iter_num;
        var = Math.sqrt(var);

        Map<String, Double> rs = new HashMap<String, Double>();
        rs.put("AVE_SPREAD_SIZE", ave_spread_size);
        rs.put("VAR", var);
        return rs;
        // System.out.println(ave_spread_size+"\t"+var);
    }

    public int runICModelProcess(List<Node> seeds, double p) {
       // System.out.println("test:\t"+seeds.get(0).getNodeID());
        int spread_size = 0;
        // initial graph
        for (Node node : graph.getNodes()) {
            node.pre_status = 0;// 0 means it is inactive
        }
        for (Node node : seeds) {
            node.pre_status = 1;// 1 means it is active
        }
        // cascade
        List<Node> preactived = new ArrayList<Node>();
        List<Node> tempactived = new ArrayList<Node>();

        for (Node node : seeds) {
            preactived.add(node);
        }

        while (preactived.size() > 0) {
            for (Node source : preactived) {
                // active its neighbors
                for (Node nei : source.getFans()) {
                    if (nei.pre_status == 1)
                        continue;
                    if (Math.random() < p) {
                        nei.pre_status = 1;
                        tempactived.add(nei);
                    }
                }
            }

            // mark next step active nodes
            preactived = new ArrayList<Node>();
            for (Node node : tempactived) {
                preactived.add(node);
            }
            tempactived = new ArrayList<Node>();
        }
        for (Node node : graph.getNodes()) {
            if (node.pre_status == 1)
                spread_size++;
        }
        return spread_size;
    }

    
    public Map<String, Double> runGeneralICModel(DirectedGraph graph, List<Node> seeds, int R) {
        double ave_spread_size = 0;
        double[] spread = new double[R];
        double var = 0;
        // IC process
        for (int i = 0; i < R; i++) {
            spread[i] = runGeneralICModelProcess(graph, seeds);
            // System.out.println(spread[i]);
            ave_spread_size = ave_spread_size + spread[i];
        }

        // average spread size
        ave_spread_size = (double) ave_spread_size / (double) R;

        // var
        for (int i = 0; i < R; i++) {
            var = var + (spread[i] - ave_spread_size) * (spread[i] - ave_spread_size);
        }

        var = var / R;
        var = Math.sqrt(var);
        Map<String, Double> rs = new HashMap<String, Double>();
        rs.put("AVE_SPREAD_SIZE", ave_spread_size);
        rs.put("VAR", var);
        return rs;
        // System.out.println(p+"\t"+ave_spread_size+"\t"+var);
    }
    
    
    public int runGeneralICModelProcess(DirectedGraph graph, List<Node> seeds) {
        // System.out.println("test:\t"+seeds.get(0).getNodeID());
         int spread_size = 0;
         // initial graph
         for (Node node : graph.getNodes()) {
             node.pre_status = 0;// 0 means it is inactive
         }
         for (Node node : seeds) {
             node.pre_status = 1;// 1 means it is active
         }
         // cascade
         List<Node> preactived = new ArrayList<Node>();
         List<Node> tempactived = new ArrayList<Node>();

         for (Node node : seeds) {
             preactived.add(node);
         }

         while (preactived.size() > 0) {
             for (Node source : preactived) {
                 // active its neighbors
                 for (Node nei : source.getFans()) {
                     if (nei.pre_status == 1)
                         continue;
                     double p = source.propagation.get(nei);
                     if (Math.random() < p) {
                         nei.pre_status = 1;
                         tempactived.add(nei);
                     }
                 }
             }

             // mark next step active nodes
             preactived = new ArrayList<Node>();
             for (Node node : tempactived) {
                 preactived.add(node);
             }
             tempactived = new ArrayList<Node>();
         }
         for (Node node : graph.getNodes()) {
             if (node.pre_status == 1)
                 spread_size++;
         }
         return spread_size;
     }
    
    public int runWeightedICModelProcess(List<Node> seeds) {
        int spread_size = 0;
        // initial graph
        for (Node node : graph.getNodes()) {
            node.pre_status = 0;// 0 means it is inactive
        }
        for (Node node : seeds) {
            node.pre_status = 1;// 1 means it is active
        }

        // cascade
        List<Node> preactived = new ArrayList<Node>();
        List<Node> tempactived = new ArrayList<Node>();

        for (Node node : seeds) {
            preactived.add(node);
        }

        while (preactived.size() > 0) {
            for (Node source : preactived) {
                // active its neighbors
                for (Node nei : source.getFans()) {
                    if (nei.pre_status == 1)
                        continue;
                    if (Math.random() < 1.0 / (double) nei.getFans().size()) {
                        nei.pre_status = 1;
                        tempactived.add(nei);
                    }
                }
            }

            // mark next step active nodes
            preactived = new ArrayList<Node>();
            for (Node node : tempactived) {
                preactived.add(node);
            }
            tempactived = new ArrayList<Node>();
        }
        for (Node node : graph.getNodes()) {
            if (node.pre_status == 1)
                spread_size++;
        }
        return spread_size;
    }
}
