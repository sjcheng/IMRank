import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.HashMap;
import java.util.Iterator;

public class TopkSolutions {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        GreedyInfluenceAll(34, 100000);
        //GreedyInfluence();
        // SGMain();
        // GenerateSolutions();
        // CompareMain();
    }

    /*
     * for near high influence nodes, add edges by man hand
     */

    public static void GreedyInfluence() {
        double p = 0.1;
        String gfile = "E:/Experiments/IMRank/karate_edgelist_1.txt";
        double influence[] = new double[35];
        influence[0] = 0;
        for (int i = 34; i <= 34; i++) {
            MultiEdgesDirectedGraph g = MultiEdgesDirectedGraph.ConstructFromUndirectedFile(gfile);
            SeedsInfluenceSpread spread = new SeedsInfluenceSpread(g);

            TopkComponentsForUniformIC topk = new TopkComponentsForUniformIC();
            double[] temp = topk.extractSeedsSG(gfile, i, p, 40000);

            List<Node> newseeds = new ArrayList<Node>();
            for (int j = 0; j < i; j++) {
                newseeds.add(g.getNodeByID((int) temp[j]));
                System.out.println(j + "\t" + (int) temp[j] + "\t" + g.getNodeByID((int) temp[j]).getOutDegree());
            }

            Map rs = spread.getInfluenceSpread(newseeds, p);
            double influ = Double.parseDouble(rs.get("AVE_SPREAD_SIZE").toString());
            double var = Double.parseDouble(rs.get("VAR").toString());

            influence[i] = influ;
            System.out.println(i + "\t" + influence[i] + "\t" + var + "\t" + (influence[i] - influence[i - 1]));
        }
    }

    public static void GreedyInfluenceAll(int k, int R) {
        double p = 0.05;
        String gfile = "E:/Experiments/IMRank/karate_edgelist_1.txt";
        double influence[] = new double[35];
        influence[0] = 0;
        MultiEdgesDirectedGraph g = MultiEdgesDirectedGraph.ConstructFromUndirectedFile(gfile);
        SeedsInfluenceSpread spread = new SeedsInfluenceSpread(g);

        TopkComponentsForUniformIC topk = new TopkComponentsForUniformIC();
        double[] temp = topk.extractSeedsSG(gfile, k, p, R);

        List<Node> newseeds = new ArrayList<Node>();
        for (int j = 1; j < k+1; j++) {
            newseeds.add(g.getNodeByID((int) temp[j - 1]));
            Map rs = spread.getInfluenceSpread(newseeds, p);
            double influ = Double.parseDouble(rs.get("AVE_SPREAD_SIZE").toString());
            double var = Double.parseDouble(rs.get("VAR").toString());
            influence[j] = influ;
            System.out.println(j + "\t" + (int) temp[j-1] + "\t" + g.getNodeByID((int) temp[j-1]).getOutDegree() + "\t"
                    + influence[j] + "\t" + var + "\t" + (influence[j] - influence[j - 1]));

        }
        // System.out.println(i + "\t" + influence[i] +"\t"+var +"\t" + (influence[i]-influence[i-1]));
        // }
    }

    public static void CompareMain() {
        String netprefix = "BA_1000_3_1";
        String prefix = "TrueAss18000";// "TrueDisAss100000";//////

        String solutionFile1 = "C:/IMSolutions_Rs/" + netprefix + "_" + prefix + "_P_0.01_UIC_SG_R100000.txt";
        // String solutionFile2 = "C:/IMSolutions_Rs/SF_1000_r2_TrueAss30000_rewire100_0_P_0.01_UIC_SG_R100000.txt";

        int[] k = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20 };
        double p = 0.01;

        int[] rewire = { 5, 10, 15, 20, 25, 30, 35, 40, 45, 50 };

        for (int i = 0; i < rewire.length; i++) {
            for (int c = 0; c < k.length; c++) {
                double[] values = new double[6];
                for (int j = 0; j < 6; j++)
                    values[j] = 0;
                Compare(solutionFile1, "C:/IMSolutions_Rs/" + netprefix + "_" + prefix + "_rewire" + rewire[i]
                        + "_0_P_" + p + "_UIC_SG_R100000.txt", k[c], values);
                Compare(solutionFile1, "C:/IMSolutions_Rs/" + netprefix + "_" + prefix + "_rewire" + rewire[i]
                        + "_1_P_" + p + "_UIC_SG_R100000.txt", k[c], values);
                Compare(solutionFile1, "C:/IMSolutions_Rs/" + netprefix + "_" + prefix + "_rewire" + rewire[i]
                        + "_2_P_" + p + "_UIC_SG_R100000.txt", k[c], values);
                Compare(solutionFile1, "C:/IMSolutions_Rs/" + netprefix + "_" + prefix + "_rewire" + rewire[i]
                        + "_3_P_" + p + "_UIC_SG_R100000.txt", k[c], values);
                Compare(solutionFile1, "C:/IMSolutions_Rs/" + netprefix + "_" + prefix + "_rewire" + rewire[i]
                        + "_4_P_" + p + "_UIC_SG_R100000.txt", k[c], values);
                /*
                 * Compare(solutionFile1, "C:/IMSolutions_Rs/SF_1000_r2_" + prefix + "_rewire" + rewire[i] + "_5_P_" + p +
                 * "_UIC_SG_R100000.txt", k[c], values); Compare(solutionFile1, "C:/IMSolutions_Rs/SF_1000_r2_" + prefix +
                 * "_rewire" + rewire[i] + "_6_P_" + p + "_UIC_SG_R100000.txt", k[c], values); Compare(solutionFile1,
                 * "C:/IMSolutions_Rs/SF_1000_r2_" + prefix + "_rewire" + rewire[i] + "_7_P_" + p +
                 * "_UIC_SG_R100000.txt", k[c], values); Compare(solutionFile1, "C:/IMSolutions_Rs/SF_1000_r2_" + prefix +
                 * "_rewire" + rewire[i] + "_8_P_" + p + "_UIC_SG_R100000.txt", k[c], values); Compare(solutionFile1,
                 * "C:/IMSolutions_Rs/SF_1000_r2_" + prefix + "_rewire" + rewire[i] + "_9_P_" + p +
                 * "_UIC_SG_R100000.txt", k[c], values);
                 */
                System.out.print(k[c] + "\t" + rewire[i] + "\t");
                // for (int j = 0; j < 6; j++)
                // System.out.print(values[j] / 5 + "\t");
                System.out.print(values[0] / 5 + "\t" + values[1] / 5 + "\t" + values[3] / 5 + "\t" + values[4] / 5);
                System.out.println();
            }
        }

        System.out.println("begin to compare p");
        double[] ps = { 0.01, 0.02, 0.03, 0.04, 0.05, 0.06, 0.07, 0.08, 0.09, 0.1 };
        for (int i = 0; i < ps.length; i++) {
            for (int c = 0; c < k.length; c++) {
                double[] values = new double[6];
                for (int j = 0; j < 6; j++)
                    values[j] = 0;
                Compare(solutionFile1, "C:/IMSolutions_Rs/" + netprefix + "_" + prefix + "_P_" + ps[i]
                        + "_UIC_SG_R100000.txt", k[c], values);

                System.out.print(k[c] + "\t" + ps[i] + "\t");
                // for (int j = 0; j < 6; j++)
                // System.out.print(values[j] + "\t");
                System.out.print(values[0] / 5 + "\t" + values[1] / 5 + "\t" + values[3] / 5 + "\t" + values[4] / 5);
                System.out.println();
            }

        }
    }

    public static void GenerateSolutions() {
        String root = "E:/Experiments/IMSolutions/networksN100/";
        root = "E:/Experiments/IMSolutions/networksIncrease/";
        root = "E:/Experiments/IMSolutions/";
        String ba = "ba_n500_d6_r1.txt";
        ba = "SF_1000_6.txt";
        String er = "er_n500_d6.txt";
        String karate = "karate_edgelist_0.txt";
        String filename = root + karate;
        // int k = 2;
        double p = 0.05;
        int R = 10000;
        int colNum = 100;
        int[] Rs = { 20000 };// { 10, 20, 40, 60, 80, 100, 200, 300, 400, 500, 600, 700, 800, 900, 1000,
        // int[] n = { 500 };
        int[] k = { 34 };// { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 }; // { 2, 5, 10 };

        for (int i = 0; i < Rs.length; i++) {
            // 以价值为中心的发展观
            // ba = "SF_" + n[i] + "_6.txt";
            // filename = root + ba;
            for (int j = 0; j < k.length; j++) {

                generateSolutions(filename, k[j], p, Rs[i], colNum);

            }
        }

        // generateSolutions(filename, k, p, 20, colNum);

        // String SFr1 = "SF_n500_d10_r1_0";
        // String SFr06 = "SF_n500_d10_r0.6_0";
        // String ER = "n500_d10";

        // String selectNet = SFr1;
        // int k1 = 10;
        // String statfile = "E:/Experiments/IMSolutions/networks/"+selectNet+"_k20_p0.02_R100.txt";

        // int[] Rs = { 20, 60, 100, 140, 180, 220, 260, 300 };
        // for (int i = 0; i < n.length; i++) {
        // for (int j = 0; j < Rs.length; j++) {
        // String file = root + "SF_" + n[i] + "_6_k2_p0.05_R" + Rs[j] + "_10000times.txt";
        // String file = root + "SF_" + n[i] + "_6_k" + k1 + "_p0.05_R" + Rs[j] + "_10000times.txt";
        // System.out.println(Rs[j] + "\t" + entropy(file));
        // }
        // System.out.println();
        // }

        /*
         * String statfile = "E:/Experiments/IMSolutions/networksN100/ba_n500_d6_r1_k" + k1 + "_p0.05_R20.txt"; //
         * ba_n500_d6_r1_k6_p0.05_R20 statistic(statfile); statfile =
         * "E:/Experiments/IMSolutions/networksN100/ba_n500_d6_r1_k" + k1 + "_p0.05_R40.txt"; statistic(statfile);
         * statfile = "E:/Experiments/IMSolutions/networksN100/ba_n500_d6_r1_k" + k1 + "_p0.05_R60.txt";
         * statistic(statfile); statfile = "E:/Experiments/IMSolutions/networksN100/ba_n500_d6_r1_k" + k1 +
         * "_p0.05_R80.txt"; statistic(statfile); statfile = "E:/Experiments/IMSolutions/networksN100/ba_n500_d6_r1_k" +
         * k1 + "_p0.05_R100.txt"; statistic(statfile); statfile =
         * "E:/Experiments/IMSolutions/networksN100/ba_n500_d6_r1_k" + k1 + "_p0.05_R120.txt"; statistic(statfile);
         * statfile = "E:/Experiments/IMSolutions/networksN100/ba_n500_d6_r1_k" + k1 + "_p0.05_R140.txt";
         * statistic(statfile); statfile = "E:/Experiments/IMSolutions/networksN100/ba_n500_d6_r1_k" + k1 +
         * "_p0.05_R160.txt"; statistic(statfile); statfile = "E:/Experiments/IMSolutions/networksN100/ba_n500_d6_r1_k" +
         * k1 + "_p0.05_R180.txt"; statistic(statfile); statfile =
         * "E:/Experiments/IMSolutions/networksN100/ba_n500_d6_r1_k" + k1 + "_p0.05_R200.txt"; statistic(statfile);
         */
        /*
         * k1 = 8; String statfile = "E:/Experiments/IMSolutions/networksN100/ba_n500_d6_r1_k" + k1 + "_p0.05_R20.txt";
         * System.out.println(entropy(statfile)); statfile = "E:/Experiments/IMSolutions/networksN100/ba_n500_d6_r1_k" +
         * k1 + "_p0.05_R40.txt"; System.out.println(entropy(statfile)); statfile =
         * "E:/Experiments/IMSolutions/networksN100/ba_n500_d6_r1_k" + k1 + "_p0.05_R60.txt";
         * System.out.println(entropy(statfile)); statfile = "E:/Experiments/IMSolutions/networksN100/ba_n500_d6_r1_k" +
         * k1 + "_p0.05_R80.txt"; System.out.println(entropy(statfile)); statfile =
         * "E:/Experiments/IMSolutions/networksN100/ba_n500_d6_r1_k" + k1 + "_p0.05_R100.txt";
         * System.out.println(entropy(statfile)); statfile = "E:/Experiments/IMSolutions/networksN100/ba_n500_d6_r1_k" +
         * k1 + "_p0.05_R120.txt"; System.out.println(entropy(statfile)); statfile =
         * "E:/Experiments/IMSolutions/networksN100/ba_n500_d6_r1_k" + k1 + "_p0.05_R140.txt";
         * System.out.println(entropy(statfile)); statfile = "E:/Experiments/IMSolutions/networksN100/ba_n500_d6_r1_k" +
         * k1 + "_p0.05_R160.txt"; System.out.println(entropy(statfile)); statfile =
         * "E:/Experiments/IMSolutions/networksN100/ba_n500_d6_r1_k" + k1 + "_p0.05_R180.txt";
         * System.out.println(entropy(statfile)); statfile = "E:/Experiments/IMSolutions/networksN100/ba_n500_d6_r1_k" +
         * k1 + "_p0.05_R200.txt"; System.out.println(entropy(statfile));
         */
    }

    public static void exhaustion(String graphFile, double p) {
        int[] opt = { 3, 0, 1, 2, 10, 58, 17, 34 };
    }

    public static void SGMain() {
        TopkSolutions topk = new TopkSolutions();
        String filepre = "";
        String sf6file = "SF_n100_d6_r1";
        String sf10file = "SF_n100_d10_r1";
        String hepfile = "hep";
        String phyfile = "phy";
        String karate = "karate_edgelist_0";
        filepre = karate;
        String root = "E:/Experiments/IMSolutions/";
        String graphFile = root + filepre + ".txt";
        graphFile = root + filepre + "/" + filepre + ".txt";
        // graphFile = root + filepre + ".txt";
        double p = 0.1;
        String solutionFile = ""; // "E:/newcode2/max_influence/hep_P_0.01_UIC_SG.txt";
        int[] k = { 10 };
        // int[] k = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20 };// { 1, 2, 3, 4, 5, 6, 7,
        // 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 50 };
        int[] R = { 10000 };// { 20, 40, 60, 80, 100, 120, 150, 200, 300, 400, 500, 600, 700, 800, 900, 1000, 1200,
        // 1500, 2000, 3000, 4000, 5000, 6000, 7000, 8000, 9000, 10000, 12000, 15000, 20000 };//
        // int[] R = {100};
        MultiEdgesDirectedGraph g = MultiEdgesDirectedGraph.ConstructFromUndirectedFile(graphFile);
        SeedsInfluenceSpread spread = new SeedsInfluenceSpread(g);

        for (int i = 0; i < k.length; i++) {
            Solution[] storeSolutions = new Solution[300000];
            for (int j = 0; j < R.length; j++) {
                // System.out.print(k[i] + "\t" + R[j] + "\t");
                solutionFile = "";
                if (R[j] < 100)
                    solutionFile = root + filepre + "/" + filepre + "_P_" + p + "_UIC_SG_R0" + R[j] + ".txt";
                else
                    solutionFile = root + filepre + "/" + filepre + "_P_" + p + "_UIC_SG_R" + R[j] + ".txt";

                // System.out.print("\n");
                topk.statRank(solutionFile, k[i]);
                // topk.everyRank(solutionFile, k[i]);
                // Solution[] solutions = topk.processSG(g, spread, storeSolutions, p, solutionFile, k[i], R[j]);
                // String outfile = "E:/newcode2/max_influence/"+"hep_k"+k[i]+"_R"+R[j]+"500times.txt";

                // String outfile = root + filepre + "/stat_" + filepre + "_P_" + p + "_UIC_SG_R" + R[j] + "_k_" + k[i]
                // + ".txt";
                // String outfile = solutionFile.replace(".txt", "_k"+k[i]+".txt");
                // topk.processSGNodes(graphFile, p, solutionFile, k[i], R[j], 1.0);
                // topk.printSolutions(solutions, outfile, k[i], p, R[j]);
                // System.out.println(entropy(outfile) + "\t" + aveInfluence(outfile));
            }
        }
    }

    public void statRank(String solutionFile, int rank) {

        HashMap<Integer, Integer> nodeIndex = new HashMap();

        double[][] ranks = new double[10000][101];//
        for (int i = 0; i < 10000; i++) {
            ranks[i][0] = -1;
            for (int j = 1; j < 101; j++) {
                ranks[i][j] = 0;
            }
        }

        // int index = 0;
        int countNum = 0;

        try {

            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(solutionFile))));
            String line;
            int nodeindex = 0;
            while ((line = br.readLine()) != null) {

                line = line.trim();
                if (line.length() == 0) {
                    System.out.println("line length is zero");
                    continue;
                }

                countNum++;

                StringTokenizer st = new StringTokenizer(line);

                // R= 3000 0.080000 ids: 0 3 1 2 10 58 64 11 34 28 5 87 86 63 68 74 79 30 14 85 8 97 15 94 41 78 98 46
                // 56 47 59 82 91 69 88 83 95 38 99 67 77 93 54 96 92 66 45 89 27 55

                st.nextToken();
                int tempR = Integer.parseInt(st.nextToken());

                st.nextToken();
                st.nextToken();

                int temprank = 1;

                while (st.hasMoreTokens()) {

                    int tempID = Integer.parseInt(st.nextToken());
                    if (nodeIndex.containsKey(tempID)) {
                        int tempIndex = nodeIndex.get(tempID);
                        ranks[tempIndex][temprank]++;
                        ranks[tempIndex][0]++;
                    } else {
                        nodeIndex.put(tempID, nodeindex);
                        ranks[nodeindex][temprank]++;
                        ranks[nodeindex][0] = 1;
                        nodeindex++;
                    }
                    temprank++;
                    if (temprank > rank)
                        break;
                }

            }
            br.close();

            // statistic every nodes average rank
            Iterator iter = nodeIndex.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                int nodeID = (Integer) entry.getKey();
                int index = (Integer) entry.getValue();
                double averRank = 0;
                for (int i = 1; i < 101; i++) {
                    averRank += ranks[index][i] * i;
                }
                averRank = (double) averRank / (double) ranks[index][0];
                double var = 0;
                for (int i = 1; i < 101; i++) {
                    if (ranks[index][i] > 0)
                        var += ranks[index][i] * (averRank - i) * (averRank - i);
                }
                var = (double) var / (double) ranks[index][0];
                var = Math.sqrt(var);
                System.out.println(nodeID + "\t" + averRank + "\t" + var + "\t" + (double) ranks[index][0]
                        / (double) countNum);
            }

        } catch (IOException exp) {
            System.out.println(exp);
        }
    }

    public void everyRank(String solutionFile, int rank) {

        HashMap<Integer, Integer> nodeIndex = new HashMap();

        double[] ranks = new double[10000];//
        for (int i = 0; i < 10000; i++) {
            ranks[i] = -1;
        }

        int countNum = 0;

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(solutionFile))));
            String line;
            int nodeindex = 0;
            while ((line = br.readLine()) != null) {

                line = line.trim();
                if (line.length() == 0) {
                    System.out.println("line length is zero");
                    continue;
                }

                countNum++;

                StringTokenizer st = new StringTokenizer(line);

                // R= 3000 0.080000 ids: 0 3 1 2 10 58 64 11 34 28 5 87 86 63 68 74 79 30 14 85 8 97 15 94 41 78 98 46
                // 56 47 59 82 91 69 88 83 95 38 99 67 77 93 54 96 92 66 45 89 27 55

                st.nextToken();
                int tempR = Integer.parseInt(st.nextToken());

                st.nextToken();
                st.nextToken();

                int temprank = 1;

                while (st.hasMoreTokens()) {
                    int tempID = Integer.parseInt(st.nextToken());
                    if (temprank == rank) {
                        if (nodeIndex.containsKey(tempID)) {
                            int tempIndex = nodeIndex.get(tempID);
                            ranks[tempIndex]++;
                            // ranks[tempIndex]++;
                        } else {
                            nodeIndex.put(tempID, nodeindex);
                            // ranks[nodeindex]++;
                            ranks[nodeindex] = 1;
                            nodeindex++;
                        }
                        break;
                    }
                    temprank++;
                }

            }
            br.close();

            // statistic every nodes in those solutions
            Iterator iter = nodeIndex.entrySet().iterator();
            System.out.print("rank\t" + rank);
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                int nodeID = (Integer) entry.getKey();
                int index = (Integer) entry.getValue();
                double freq = 0;

                // for (int i = 1; i < 101; i++) {
                freq = (double) ranks[index] / (double) countNum;
                // }
                System.out.print("\t" + (nodeID + 1) + "(" + freq + ")");
            }
            System.out.println();

        } catch (IOException exp) {
            System.out.println(exp);
        }
    }

    public void influenceFromPre(Solution[] storeSolutions, Solution instance, MultiEdgesDirectedGraph g,
            SeedsInfluenceSpread spread, double p) {
        // double influence = 0;
        int count = 0;
        for (int i = 0; i < storeSolutions.length; i++) {
            if (storeSolutions[i] == null)
                break;
            count++;
            if (storeSolutions[i].theSame(instance)) {
                instance.setInfluence(storeSolutions[i].getInfluence());
                instance.setVar(storeSolutions[i].getVar());
                return;
            }
        }

        List<Node> newseeds = new ArrayList<Node>();
        for (int j = 0; j < instance.getValues().length; j++)
            newseeds.add(g.getNodeByID(instance.getValue(j)));

        // Map rs = spread.getInfluenceSpread(newseeds, p);
        // instance.setInfluence(Double.parseDouble(rs.get("AVE_SPREAD_SIZE").toString()));
        // instance.setVar(Double.parseDouble(rs.get("VAR").toString()));
        instance.setInfluence(1);
        instance.setVar(1);
        storeSolutions[count] = instance;
    }

    public Solution[] processSG(MultiEdgesDirectedGraph g, SeedsInfluenceSpread spread, Solution[] storeSolutions,
            double p, String solutionFile, int k, int R) {

        Solution[] solutions = new Solution[10000];
        int count = 0;
        int combineCount = 0;
        try {
            // R= 20 0.089000 ids: 326 639 559 287 359 267 1775 80 156 566 1955 8 111 705 422 535 562 1162 214 608 221
            // 646 5682 12 26 553 741 88 75 128 1257 1292 152 192 226 236 1827 1 14 100 2497 8991 131 66 354 602 3683
            // 1547 417 636
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(solutionFile))));
            String line;
            while ((line = br.readLine()) != null) {

                line = line.trim();
                if (line.length() == 0) {
                    System.out.println("line length is zero");
                    continue;
                }

                StringTokenizer st = new StringTokenizer(line);

                st.nextToken();
                int tempR = Integer.parseInt(st.nextToken());
                if (tempR != R)
                    continue;
                st.nextToken();
                st.nextToken();
                Solution solution = new Solution(k);

                for (int i = 0; i < k; i++) {
                    solution.setLocationValue(i, Integer.parseInt(st.nextToken()));
                }

                // check whether this solution exist
                int index = -1;
                for (int i = 0; i < combineCount; i++) {
                    if (solutions[i] == null)
                        break;
                    if (solutions[i].theSame(solution)) {
                        index = i;
                        break;
                    }
                }
                if (index == -1) {
                    solutions[combineCount] = solution;
                    solutions[combineCount].setCount(1);
                    combineCount++;
                    // System.out.println(combineCount+"-th solution" );
                } else {
                    solutions[index].setCount(solutions[index].getCount() + 1);
                }
                count++;
                // if(count == 100)
                // break;
            }
            br.close();

            for (int i = 0; i < combineCount; i++) {
                solutions[i].setFreq((double) solutions[i].getCount() / (double) count);
                influenceFromPre(storeSolutions, solutions[i], g, spread, p);
                // System.out.println(totalScore / R);
                // List<Node> newseeds = new ArrayList<Node>();
                // for (int j = 0; j < k; j++)
                // newseeds.add(g.getNodeByID(solutions[i].getValue(j)));

                // Map rs = spread.getInfluenceSpread(newseeds, p);
                // solutions[i].setInfluence(Double.parseDouble(rs.get("AVE_SPREAD_SIZE").toString()));
                // solutions[i].setVar(Double.parseDouble(rs.get("VAR").toString()));
                // solutions[i].setInfluence(1);
                // solutions[i].setVar(1);

                // System.out.println(i+"-th solution: I="+solutions[i].getInfluence()+"\tvar="+solutions[i].getVar());
            }
        } catch (IOException exp) {
            System.out.println(exp);
        }
        return solutions;

    }

    public double[][] processSGNodes(String graphFile, double p, String solutionFile, int k, int R, double filter) {

        double[][] nodes = new double[10000][3];// node id, freq, real freq

        for (int i = 0; i < 10000; i++) {
            nodes[i][0] = -1;
            nodes[i][1] = -1;
            nodes[i][2] = -1;
        }
        MultiEdgesDirectedGraph g = MultiEdgesDirectedGraph.ConstructFromUndirectedFile(graphFile);
        SeedsInfluenceSpread spread = new SeedsInfluenceSpread(g);
        Solution[] storeSolutions = new Solution[100000];
        Solution[] solutions = processSG(g, spread, storeSolutions, p, solutionFile, k, R);
        int count = 0;
        int combineCount = 0;

        for (int i = 0; i < solutions.length; i++) {
            if (solutions[i] == null)
                break;
            for (int j = 0; j < k; j++) {
                int tempNode = solutions[i].getValue(j);
                int index = -1;
                for (int c = 0; c < combineCount; c++) {
                    if (tempNode == nodes[c][0]) {
                        index = c;
                        break;
                    }
                }
                if (index == -1) {
                    nodes[combineCount][0] = tempNode;
                    nodes[combineCount][1] = 1;
                    nodes[combineCount][2] = solutions[i].getFreq();
                    combineCount++;
                } else {
                    nodes[index][1]++;
                    nodes[index][2] += solutions[i].getFreq();
                }
            }
            count++;
        }

        int filterNum = 0;
        for (int i = 0; i < combineCount; i++) {
            if (nodes[i][2] >= filter)
                filterNum++;
        }
        // System.out.println(filterNum);
        return nodes;
    }

    public static double entropy(String solutionFile) {
        TopkSolutions start = new TopkSolutions();
        double entropy = 0;
        Solution[] solutions = start.getSolutions(solutionFile);
        double averPerf = 0;
        for (int i = 0; i < solutions.length; i++) {
            if (solutions[i] == null)
                break;
            double temp1 = Math.log(solutions[i].getFreq()) / Math.log(2);
            entropy = entropy + solutions[i].getFreq() * temp1;
            averPerf = averPerf + solutions[i].getFreq() * solutions[i].getInfluence();
            // System.out.println(i+"-th --> " + entropy + "\t" + solutions[i].getFreq() + "\t" +
            // Math.log(solutions[i].getFreq()) + "\t" + Math.log(2)
            // +"\t"+Math.log(solutions[i].getFreq())/Math.log(2));
        }
        entropy = -entropy;
        return entropy;
    }

    public static double aveInfluence(String solutionFile) {
        TopkSolutions start = new TopkSolutions();
        // double entropy = 0;
        Solution[] solutions = start.getSolutions(solutionFile);
        double averPerf = 0;
        for (int i = 0; i < solutions.length; i++) {
            if (solutions[i] == null)
                break;
            // double temp1 = Math.log(solutions[i].getFreq()) / Math.log(2);
            // entropy = entropy + solutions[i].getFreq() * temp1;
            averPerf = averPerf + solutions[i].getFreq() * solutions[i].getInfluence();
            // System.out.println(i+"-th --> " + entropy + "\t" + solutions[i].getFreq() + "\t" +
            // Math.log(solutions[i].getFreq()) + "\t" + Math.log(2)
            // +"\t"+Math.log(solutions[i].getFreq())/Math.log(2));
        }
        // entropy = -entropy;
        return averPerf;
    }

    public static double compilations(String solutionFile) {
        TopkSolutions start = new TopkSolutions();
        double entropy = 0;
        Solution[] solutions = start.getSolutions(solutionFile);
        int[] ids = new int[300];
        for (int i = 0; i < solutions.length; i++) {
            if (solutions[i] == null)
                break;
            // double temp1 = Math.log(solutions[i].getFreq()) / Math.log(2);
            // entropy = entropy + solutions[i].getFreq() * temp1;
            // System.out.println(i+"-th --> " + entropy + "\t" + solutions[i].getFreq() + "\t" +
            // Math.log(solutions[i].getFreq()) + "\t" + Math.log(2)
            // +"\t"+Math.log(solutions[i].getFreq())/Math.log(2));
        }
        entropy = -entropy;
        return entropy;
    }

    public static void generateSolutions(String filename, int k, double p, int R, int colNum) {
        // String filename = "E:/Experiments/IMSolutions/networks/SF_n500_d10_r1_0.txt";

        TopkSolutions start = new TopkSolutions();
        Solution[] solutions = start.getSolutions(filename, k, p, R, colNum);
        start.printSolutions(solutions, filename, k, p, R);
    }

    public static void statistic(String solutionFile) {
        // Solution[] solutions = new Solution[100];
        // String solutionFile = "E:/Experiments/IMSolutions/networks/SF_n500_d10_r0.6_0_k5_p0.02_R20.txt";
        String edges_file = solutionFile.replace(".txt", "_edges.txt");
        String edges2_file = solutionFile.replace(".txt", "_Alledges.txt");
        String nodes_file = solutionFile.replace(".txt", "_nodes.txt");
        TopkSolutions start = new TopkSolutions();

        // statistic the importance of every nodes and the correlation of every two nodes
        start.generateNet(solutionFile, edges_file, nodes_file, edges2_file);
        String distanceSimi_file = solutionFile.replace(".txt", "_disSimilar.txt");
        start.generateDistanceSimilarity(solutionFile, distanceSimi_file);
        start.generateDistanceSimilaritySstar(solutionFile, distanceSimi_file);
        // statistic the distance and the solution value

    }

    public void generateDistanceSimilarity(String filename, String outfile) {
        Solution[] solutions = getSolutions(filename);
        int k = solutions[0].getValues().length;
        // the max distance is k
        int[] distance = new int[k + 1];
        double[] diff = new double[k + 1];
        double[] maxDiff = new double[k + 1];
        double[] minDiff = new double[k + 1];

        for (int i = 0; i < k + 1; i++) {
            distance[i] = 0;
            diff[i] = 0;
            maxDiff[i] = -1;
            minDiff[i] = Integer.MAX_VALUE;
        }
        int count = 0;
        for (int i = 0; i < solutions.length - 1; i++) {
            if (solutions[i] == null)
                break;
            for (int j = i + 1; j < solutions.length; j++) {
                if (solutions[j] == null)
                    break;
                int diffNum = solutions[i].diffNum(solutions[j]);

                /*
                 * if(diffNum==0){ for(int c=0; c<solutions[i].getValues().length; c++)
                 * System.out.print(solutions[i].getValue(c)+"\t"); System.out.println(); for(int c=0; c<solutions[j].getValues().length;
                 * c++) System.out.print(solutions[j].getValue(c)+"\t"); System.out.println();
                 * System.out.println("strange! 0"); }
                 */

                distance[diffNum]++;
                double tempDiff = Math.abs(solutions[i].getInfluence() - solutions[j].getInfluence());
                diff[diffNum] += Math.abs(solutions[i].getInfluence() - solutions[j].getInfluence());
                if (tempDiff > maxDiff[diffNum])
                    maxDiff[diffNum] = tempDiff;
                if (tempDiff < minDiff[diffNum])
                    minDiff[diffNum] = tempDiff;
                count++;
            }
        }

        double meanInflu = 0;
        int count2 = 0;
        for (int i = 0; i < solutions.length; i++) {
            if (solutions[i] == null)
                break;
            meanInflu += solutions[i].getInfluence();
            count2++;
        }
        meanInflu = meanInflu / count2;
        // distance VS frequence
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outfile)));
            for (int i = 1; i < k + 1; i++) {
                // System.out.println(i+"\t"+distance[i]+"\t"+count);
                if (distance[i] == 0)
                    continue;
                System.out.println("d=\t" + i + "\tmean=\t" + (double) diff[i] / (double) distance[i] + "\t"
                        + (double) diff[i] / ((double) distance[i] * meanInflu) + "\tmax=\t" + maxDiff[i] + "\t"
                        + maxDiff[i] / meanInflu + "\tmin=\t" + minDiff[i] + "\t" + minDiff[i] / meanInflu
                        + "\tfreq=\t" + (double) distance[i] / (double) (count));
                out.println("d=\t" + i + "\tmean=\t" + (double) diff[i] / (double) distance[i] + "\t"
                        + (double) diff[i] / ((double) distance[i] * meanInflu) + "\tmax=\t" + maxDiff[i] + "\t"
                        + maxDiff[i] / meanInflu + "\tmin=\t" + minDiff[i] + "\t" + minDiff[i] / meanInflu
                        + "\tfreq=\t" + (double) distance[i] / (double) (count));
            }
            out.println();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println();

    }

    public void generateDistanceSimilaritySstar(String filename, String outfile) {
        Solution[] solutions = getSolutions(filename);
        int k = solutions[0].getValues().length;
        // the max distance is k
        int[] distance = new int[k + 1];
        double[] diff = new double[k + 1];
        double[] freq = new double[k + 1];

        double[] maxDiff = new double[k + 1];
        double[] minDiff = new double[k + 1];
        double[] diffRate = { 0.01, 0.02, 0.04, 0.06, 0.08, 0.10 };
        double[] diffRateCount = new double[diffRate.length];

        for (int i = 0; i < diffRateCount.length; i++)
            diffRateCount[i] = 0;

        for (int i = 0; i < k + 1; i++) {
            distance[i] = 0;
            diff[i] = 0;
            maxDiff[i] = -1;
            minDiff[i] = Integer.MAX_VALUE;
            freq[i] = 0;
        }

        int maxId = 0;
        double maxSolution = 0;
        for (int i = 0; i < solutions.length; i++) {
            if (solutions[i] == null)
                break;
            if (solutions[i].getInfluence() > maxSolution) {
                maxId = i;
                maxSolution = solutions[i].getInfluence();
            }

        }

        int count = 0;

        for (int j = 0; j < solutions.length; j++) {
            if (solutions[j] == null)
                break;
            if (j == maxId)
                continue;

            int diffNum = solutions[maxId].diffNum(solutions[j]);

            double tempDiff = Math.abs(solutions[maxId].getInfluence() - solutions[j].getInfluence());

            boolean flag = true;
            for (int c = 0; c < diffRateCount.length; c++) {
                if (tempDiff <= diffRate[c] * maxSolution) {
                    diffRateCount[c]++;
                    flag = false;
                    break;
                }
            }
            // if(flag) continue;

            distance[diffNum]++;
            freq[diffNum] += solutions[j].getFreq();
            diff[diffNum] += tempDiff;
            if (tempDiff > maxDiff[diffNum])
                maxDiff[diffNum] = tempDiff;
            if (tempDiff < minDiff[diffNum])
                minDiff[diffNum] = tempDiff;
            count++;
        }

        // distance VS frequence
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outfile, true)));
            System.out.println("compare to S*");
            for (int i = 1; i < k + 1; i++) {
                if (distance[i] == 0)
                    continue;
                System.out.println("d=\t" + i + "\tmean=\t" + (double) diff[i] / (double) distance[i] + "\t"
                        + (double) diff[i] / ((double) distance[i] * maxSolution) + "\tmax=\t" + maxDiff[i] + "\t"
                        + maxDiff[i] / maxSolution + "\tmin=\t" + minDiff[i] + "\t" + minDiff[i] / maxSolution
                        + "\tfreq=\t" + (double) distance[i] / (double) (count) + "\t" + freq[i]);
                out.println("d=\t" + i + "\tmean=\t" + (double) diff[i] / (double) distance[i] + "\t"
                        + (double) diff[i] / ((double) distance[i] * maxSolution) + "\tmax=\t" + maxDiff[i] + "\t"
                        + maxDiff[i] / maxSolution + "\tmin=\t" + minDiff[i] + "\t" + minDiff[i] / maxSolution
                        + "\tfreq=\t" + (double) distance[i] / (double) (count) + "\t" + freq[i]);
            }

            out.println();
            System.out.println();

            for (int i = 0; i < diffRate.length; i++) {
                out.print(diffRate[i] + "\t");
                System.out.print(diffRate[i] + "\t");
            }
            out.println();
            System.out.println();
            for (int i = 0; i < diffRate.length; i++) {
                out.print(diffRateCount[i] + "\t");
                System.out.print(diffRateCount[i] + "\t");
            }
            out.println();
            System.out.println();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println();

    }

    public static void Compare(String solutionFile1, String solutionFile2, int k, double[] values) {
        TopkSolutions topk = new TopkSolutions();
        Solution[] solution1 = topk.getSolutions(solutionFile1, k);
        Solution[] solution2 = topk.getSolutions(solutionFile2, k);
        int maxDiff = -1;
        int minDiff = k;
        double averDiff = 0;
        int maxRankDiff = -1;
        int minRankDiff = k;
        double averRankDiff = 0;

        int len1 = 0;
        int len2 = 0;
        for (int i = 0; i < solution1.length; i++) {

            if (solution1[i] == null)
                break;
            len1++;
            len2 = 0;
            for (int j = 0; j < solution2.length; j++) {

                if (solution2[j] == null)
                    break;
                len2++;
                int diff = solution1[i].diffNum(solution2[j], k);
                averDiff += diff;
                if (diff > maxDiff)
                    maxDiff = diff;
                if (diff < minDiff)
                    minDiff = diff;

                int diffRank = solution1[i].diffRankNum(solution2[j], k);
                if (diffRank > 0) {
                    // System.out.println();
                }
                averRankDiff += diffRank;
                if (diffRank > maxRankDiff)
                    maxRankDiff = diffRank;
                if (diffRank < minRankDiff)
                    minRankDiff = diffRank;
            }
        }
        averDiff = averDiff / (len1 * len2);
        averRankDiff = averRankDiff / (len1 * len2);
        values[0] += minDiff;
        values[1] += averDiff;
        values[2] += maxDiff;
        values[3] += minRankDiff;
        values[4] += averRankDiff;
        values[5] += maxRankDiff;
        // System.out.println(solutionFile1+"\t"+solutionFile2+"\t"+minDiff+"\t"+averDiff+"\t"+maxDiff+"\t"+minRankDiff+"\t"+averRankDiff+"\t"+maxRankDiff);

    }

    public Solution[] getSolutions(String solutionFile, int k) {
        Solution[] solutions = new Solution[10000];
        int count = 0;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(solutionFile))));
            String line;
            while ((line = br.readLine()) != null) {
                // System.out.println(line);
                line = line.trim();
                if (line.length() == 0) {
                    System.out.println("line length is zero");
                    continue;
                }
                StringTokenizer st = new StringTokenizer(line);
                for (int i = 0; i < 4; i++)
                    st.nextToken();
                Solution solution = new Solution(k);

                for (int i = 0; i < k; i++) {
                    solution.setLocationValue(i, Integer.parseInt(st.nextToken()));
                }
                solutions[count] = solution;
                count++;
            }
            br.close();

        } catch (IOException exp) {
            System.out.println(exp);
        }
        return solutions;
    }

    public Solution[] getSolutions(String solutionFile) {
        Solution[] solutions = new Solution[10000];
        int count = 0;
        int k = 0;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(solutionFile))));
            String line;
            while ((line = br.readLine()) != null) {
                // System.out.println(line);
                line = line.trim();
                if (line.length() == 0) {
                    System.out.println("line length is zero");
                    continue;
                }

                if (line.contains("k")) {
                    StringTokenizer st = new StringTokenizer(line);
                    k = Integer.parseInt(st.nextToken().replace("k=", ""));
                    continue;
                }

                StringTokenizer st = new StringTokenizer(line);
                for (int i = 0; i < 1; i++)
                    st.nextToken();
                Solution solution = new Solution(k);

                for (int i = 0; i < k; i++) {
                    solution.setLocationValue(i, Integer.parseInt(st.nextToken()));
                }
                st.nextToken();
                solution.setInfluence(Double.parseDouble(st.nextToken()));
                st.nextToken();
                st.nextToken();
                // st.nextToken();
                // solution.setVar(Double.parseDouble(st.nextToken()));
                // solution.setVar(Double.parseDouble(st.nextToken().replace("Var=", "")));
                st.nextToken();
                solution.setCount(Integer.parseInt(st.nextToken()));
                st.nextToken();
                solution.setFreq(Double.parseDouble(st.nextToken()));
                solutions[count] = solution;
                count++;
                // if(count == 100)
                // break;
            }
            br.close();

        } catch (IOException exp) {
            System.out.println(exp);
        }
        return solutions;
    }

    public void generateNet(String filename, String outfile1, String outfile2, String outfile3) {

        int[][] edges = new int[200000][3];
        int[][] nodes = new int[200000][2];
        int[][] alledges = new int[200000][3];
        for (int i = 0; i < 200000; i++) {
            edges[i][0] = -1;
            edges[i][1] = -1;
            edges[i][2] = 0;
            nodes[i][0] = -1;
            nodes[i][1] = 0;
            alledges[i][0] = -1;
            alledges[i][1] = -1;
            alledges[i][2] = 0;
        }

        int count = 0;
        int k = 0;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filename))));
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.length() == 0) {
                    System.out.println("line length is zero");
                    continue;
                }

                if (line.contains("k")) {
                    StringTokenizer st = new StringTokenizer(line);
                    k = Integer.parseInt(st.nextToken().replace("k=", ""));
                    continue;
                }

                StringTokenizer st = new StringTokenizer(line);
                for (int i = 0; i < 1; i++)
                    st.nextToken();
                int[] nodesList = new int[k];
                for (int i = 0; i < k; i++) {
                    nodesList[i] = Integer.parseInt(st.nextToken());
                    insertNode(nodesList[i], nodes);
                }
                for (int i = 0; i < k - 1; i++) {
                    for (int j = i + 1; j < k; j++) {
                        insertEdge(nodesList[i], nodesList[j], edges);
                    }
                }
                count++;
            }
            br.close();
            // System.out.println("Number of line:" + count);

            PrintWriter out1 = new PrintWriter(new BufferedWriter(new FileWriter(outfile1)));
            PrintWriter out2 = new PrintWriter(new BufferedWriter(new FileWriter(outfile2)));
            PrintWriter out3 = new PrintWriter(new BufferedWriter(new FileWriter(outfile3)));

            for (int i = 0; i < 200000; i++) {
                if (edges[i][0] == -1)
                    break;
                out1.println(edges[i][0] + "\t" + edges[i][1] + "\t" + (double) edges[i][2] / (double) count + "\t"
                        + getTheoryP(edges[i][0], nodes, count) * getTheoryP(edges[i][1], nodes, count));
            }

            for (int i = 0; i < 200000; i++) {
                if (nodes[i][0] == -1)
                    break;
                out2.println(nodes[i][0] + "\t" + (double) nodes[i][1] / (double) count);
            }

            for (int i = 0; i < 200000; i++) {
                if (nodes[i][0] == -1)
                    break;
                for (int j = i + 1; j < 200000; j++) {
                    if (nodes[j][0] == -1)
                        break;
                    int index = getPraticalP(nodes[i][0], nodes[j][0], edges);
                    int praticalCount = 0;
                    double praticalRatio = 0;
                    if (index != -1) {
                        praticalCount = edges[index][2];
                        praticalRatio = (double) edges[index][2] / (double) count;
                    }
                    out3.println(nodes[i][0] + "\t" + nodes[j][0] + "\t" + praticalCount + "\t" + praticalRatio + "\t"
                            + getTheoryP(nodes[i][0], nodes, count) * getTheoryP(nodes[j][0], nodes, count));
                }
            }

            out1.close();
            out2.close();
            out3.close();
        } catch (IOException exp) {
            System.out.println(exp);
        }

    }

    public int getPraticalP(int node1, int node2, int[][] edges) {
        int v1 = node1;
        int v2 = node2;
        if (v1 > v2) {
            v1 = node2;
            v2 = node1;
        }
        int index = -1;
        for (int i = 0; i < edges.length; i++) {
            if (edges[i][0] == -1)
                break;
            if (edges[i][0] == v1 && edges[i][1] == v2) {
                index = i;
                break;
            }
        }
        return index;
    }

    public static void insertNode(int node, int[][] nodes) {
        boolean flag = false;
        int i;
        for (i = 0; i < 200000; i++) {
            if (nodes[i][0] == -1)
                break;
            if (nodes[i][0] == node) {
                flag = true;
                nodes[i][1]++;
                break;
            }
        }
        if (!flag) {
            nodes[i][0] = node;
            nodes[i][1]++;
        }
    }

    public static double getTheoryP(int node, int[][] nodes, int count) {

        for (int i = 0; i < 200000; i++) {
            if (nodes[i][0] == -1)
                break;
            if (nodes[i][0] == node) {
                return (double) nodes[i][1] / (double) count;
            }

        }
        return 0;
    }

    public static void insertEdge(int node1, int node2, int[][] edges) {

        int v1 = node1;
        int v2 = node2;
        if (v1 > v2) {
            v1 = node2;
            v2 = node1;
        }

        boolean flag = false;
        int i = 0;
        for (i = 0; i < 200000; i++) {
            if (edges[i][0] == -1)
                break;
            if (v1 == edges[i][0] && v2 == edges[i][1]) {
                edges[i][2]++;
                flag = true;
                break;
            }
        }

        if (!flag) {
            edges[i][0] = v1;
            edges[i][1] = v2;
            edges[i][2]++;
        }
    }

    // get k solutions
    public Solution[] getSolutions(String filename, int k, double p, int R, int solNum) {
        String outfile = filename.replace(".txt", "P_" + p + "_UIC_SG_R" + R + ".txt");
        // get solutions
        Solution[] solutions = new Solution[solNum];
        try {

            TopkComponentsForUniformIC topk = new TopkComponentsForUniformIC();
            int realSolNum = 0;
            int nosolutionNum = 0;
            int realtrytime = 0;
            for (int i = 0; i < solNum; i++) {
                realtrytime++;
                double[] temp = topk.extractSeedsSG(filename, k, p, R);
                Solution solution = new Solution(k);
                PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outfile, true)));
                out.print("R=\t" + R + "\t0.00000\tids:\t");
                for (int j = 0; j < k; j++) {
                    solution.setLocationValue(j, (int) temp[j]);
                    // R= 2000 0.030000 ids: 33 0 32 1 25 16 27 4 3 26 28 11 9 5 24 12 17 21 14 15 20 22 18 29 7 30 19
                    // 10 6 13 8 23 31 2
                    out.print((int) temp[j] + "\t");
                }
                out.println();
                // check if this solution exists
                out.close();
                boolean exist = false;
                for (int j = 0; j < realSolNum; j++) {
                    // if (solutions[j].theSame(solution)) {
                    if (false) {
                        exist = true;
                        solutions[j].setCount(solutions[j].getCount() + 1);
                        break;
                    }
                }
                if (exist) {
                    nosolutionNum++;
                    System.out.println("no:\t" + nosolutionNum);
                    // i--;
                    // if (nosolutionNum > 500)
                    // break;
                    continue;
                }

                MultiEdgesDirectedGraph g = MultiEdgesDirectedGraph.ConstructFromUndirectedFile(filename);
                SeedsInfluenceSpread spread = new SeedsInfluenceSpread(g);
                // System.out.println(totalScore / R);
                List<Node> newseeds = new ArrayList<Node>();
                for (int j = 0; j < k; j++)
                    newseeds.add(g.getNodeByID(solution.getValue(j)));

                // Map rs = spread.getInfluenceSpread(newseeds, p);
                // solution.setInfluence(Double.parseDouble(rs.get("AVE_SPREAD_SIZE").toString()));
                // solution.setVar(Double.parseDouble(rs.get("VAR").toString()));

                solutions[realSolNum] = solution;
                realSolNum++;
                nosolutionNum = 0;
                System.out.println("find " + realSolNum + "-th solution");
            }
            // statistic freq

            for (int i = 0; i < solutions.length; i++) {
                if (solutions[i] == null)
                    break;
                solutions[i].setFreq((double) solutions[i].getCount() / (double) realtrytime);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return solutions;
    }

    public void printSolutions(Solution[] solutions, String outfile, int k, double p, int R) {
        // String outfile = filename.replace(".txt", "_p" + p + "_R" + R + "_k" + k + ".txt");

        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outfile)));
            out.println("k=" + k + "\tp=" + p + "\tR=" + R);

            for (int i = 0; i < solutions.length; i++) {
                if (solutions[i] == null)
                    continue;
                // System.out.print("ids:\t");
                out.print("ids:\t");
                for (int j = 0; j < solutions[i].getValues().length; j++) {
                    // System.out.print(solutions[i].getValue(j) + "\t");
                    out.print(solutions[i].getValue(j) + "\t");
                }
                // System.out.print("I=\t" + solutions[i].getInfluence() + "\t");
                // System.out.print("Var=\t" + solutions[i].getVar() + "\t");
                // System.out.print("Count=\t" + solutions[i].getCount() + "\t");
                // System.out.println("Freq=\t" + solutions[i].getFreq());
                out.print("I=\t" + solutions[i].getInfluence() + "\t");
                out.print("Var=\t" + solutions[i].getVar() + "\t");
                out.print("Count=\t" + solutions[i].getCount() + "\t");
                out.println("Freq=\t" + solutions[i].getFreq());
            }

            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void statisticSolutions(Solution[] solutions) {
        for (int i = 0; i < solutions.length; i++) {
            if (solutions[i] == null)
                continue;
            System.out.print("ids:\t");
            for (int j = 0; j < solutions[i].getValues().length; j++)
                System.out.print(solutions[i].getValue(j) + "\t");
            System.out.print("I=\t" + solutions[i].getInfluence() + "\t");
            System.out.println("Var=\t" + solutions[i].getVar());
        }
    }

    class Solution {
        int[] solution;

        double influence;

        double var;

        int count = 0;

        double freq = 0;

        public Solution(int k) {
            solution = new int[k];
            count = 1;
        }

        public void setLocationValue(int loc, int id) {
            solution[loc] = id;
        }

        public void setValue(int[] ids) {
            for (int i = 0; i < solution.length; i++) {
                solution[i] = ids[i];
            }
        }

        public void setInfluence(double influence) {
            this.influence = influence;
        }

        public void setVar(double var) {
            this.var = var;
        }

        public int getValue(int loc) {
            return solution[loc];
        }

        public int getCount() {
            return count;
        }

        public double getFreq() {
            return freq;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public void setFreq(double freq) {
            this.freq = freq;
        }

        public int[] getValues() {
            return solution;
        }

        public double getInfluence() {
            return influence;
        }

        public double getVar() {
            return var;
        }

        // true means this solution and s is the same
        public boolean theSame(Solution s) {
            for (int i = 0; i < solution.length; i++) {
                boolean flag = false;
                for (int j = 0; j < s.getValues().length; j++) {
                    if (s.getValue(j) == solution[i]) {
                        flag = true;
                        break;
                    }
                }
                if (!flag)
                    return false;
            }
            return true;
        }

        // the number of different ids
        public int diffNum(Solution s) {
            int count = 0;
            for (int i = 0; i < solution.length; i++) {
                boolean flag = false;
                for (int j = 0; j < s.getValues().length; j++) {
                    if (s.getValue(j) == solution[i]) {
                        flag = true;
                        break;
                    }
                }
                if (!flag)
                    count++;
            }
            return count;
        }

        // the number of different ids, only the top-k
        public int diffNum(Solution s, int k) {
            int count = 0;
            for (int i = 0; i < k; i++) {
                boolean flag = false;
                for (int j = 0; j < k; j++) {
                    if (s.getValue(j) == solution[i]) {
                        flag = true;
                        break;
                    }
                }
                if (!flag)
                    count++;
            }
            return count;
        }

        // the number of different ranks
        public int diffRankNum(Solution s, int k) {
            int count = 0;
            for (int i = 0; i < k; i++) {
                if (s.getValue(i) == solution[i])
                    continue;
                count++;
            }
            return count;
        }
    }
}
