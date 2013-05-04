import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import DirectedGraph.*;
import java.util.*;

public class IMDivRank {

    DirectedGraph graph;

    // double p;
    double p = 0.01;

    boolean UIC = false;

    boolean WIC = false;

    Hashtable IDToLRHashtable = new Hashtable();

    public static void main(String[] args) {

        String gfile = "E:/Experiments/IMRank/karate_edgelist_1.txt";

        System.out.println("begin to read in graph");

        gfile = "E:/Experiments/IMSolutions/phy.txt";
        IMDivRank imdivr = new IMDivRank(gfile, false);
        imdivr.UIC = true;
        imdivr.p = 0.01;

        //imdivr.WIC = true;
        
        
        imdivr.setGraphModelEveryEdge();
        System.out.println("Finish to set propagations");
        System.out.println("begin to caculate score");

        // Node[] ranksDivRank = imdivr.calculateScoreDivRank(0.15);
        // Node[] ranksDivRank = imdivr.pageRank(0.15);
        // imdivr.getInfluence(ranksDivRank, ranksDivRank.length, gfile, 500);
        // Node[] ranksDivRank = imdivr.rankByDegree();
        // imdivr.getInfluence(ranksDivRank, ranksDivRank.length, gfile, 20000);
        // imr.calculateScoreTakeBackByRanking();
        // Node[] ranksPageRank = imdivr.calculateScoreTakeBackRankingSecondNeighborsGoodInitial();
        // Node[] ranksPageRank = imdivr.calculateScoreTakeBackRankingFirstNeighborsGoodInitial();
        // Node[] ranksPageRank = imdivr.calculateScoreTakeBackRankingFirstNeighborsALL();
        
        String initialKind[] = {"", "degree", "localinfluence", "competitiveinfluence", "IRIE", "randomprior"};
        String deliverKind[] = {"", "higherleaders", "highersecondleaders", "allleaders", "highestleader"};
        String finalScoreKind[] = {"", "reset", "personalized", "cycle", "cyclereset"};
        
        int index = 1;
        for(int i=1; i < initialKind.length; i++){
           Node[] ranksPageRank = imdivr.combineRanking(initialKind[i], deliverKind[index], finalScoreKind[1]);
           System.out.println(initialKind[i]+"+"+deliverKind[index]);
           imdivr.getInfluence(ranksPageRank, ranksPageRank.length, gfile, 10000);
        }
        System.out.println();
        // imr.calculateScoreTakeBackEasyRanking();
        // imr.calculateScoreTakeBackByRanking();
        // imr.calculateScoreTakeBackRanking();
        // Node[] ranks = imdivr.calculateScoreTakeBackRankingSecondNeighbors();
        // imdivr.getInfluence(ranks, ranks.length - 1, gfile, 20000);
        System.out.println("begin to save score");
        String rsfile = gfile.replace(".txt", "_rank.txt");
        imdivr.savetoFile(rsfile);
    }

    public void setGraphModel() {
        if (UIC) {
            for (Node node : graph.nodes) {
                node.nodep = p;
            }
        } else if (WIC) {
            for (Node node : graph.nodes) {
                node.nodep = 1.0 / node.getOutDegree();
            }
        } else {
            System.out.println("please set the model");
        }

    }

    public void setGraphModelEveryEdge() {
        if (UIC) {
            for (Node node : graph.nodes) {
                for (Node leader : node.getFans()) {
                    if (leader.propagation.containsKey(node))
                        continue;
                    leader.propagation.put(node, p);
                }
            }
        } else if (WIC) {
            for (Node node : graph.nodes) {
                for (Node leader : node.getFans()) {
                    if (leader.propagation.containsKey(node))
                        continue;
                    leader.propagation.put(node, 1.0 / node.getOutDegree());
                }
            }
        } else {
            System.out.println("please set the model");
        }

    }

    public void getInfluence(Node[] ranks, int k, String gfile, int R) {
        // double p = 0.3;
        // String gfile = "E:/Experiments/IMRank/karate_edgelist_1.txt";
        double influence[] = new double[ranks.length + 1];
        influence[0] = 0;
        //MultiEdgesDirectedGraph g = MultiEdgesDirectedGraph.ConstructFromUndirectedFile(gfile);
        SeedsInfluenceSpread spread = new SeedsInfluenceSpread();

        // TopkComponentsForUniformIC topk = new TopkComponentsForUniformIC();
        // double[] temp = topk.extractSeedsSG(gfile, k, p, R);

        List<Node> newseeds = new ArrayList<Node>();
        for (int j = 1; j < k + 1; j++) {
            Node seed = ranks[ranks.length - j];
            newseeds.add(ranks[ranks.length - j]);
            if(k>50){
            if (j==1 || j%5==0) {
                Map rs = spread.runGeneralICModel(graph, newseeds, R);
                double influ = Double.parseDouble(rs.get("AVE_SPREAD_SIZE").toString());
                double var = Double.parseDouble(rs.get("VAR").toString());
                influence[j] = influ;
                System.out.println(j + "\t" + seed.getNodeID() + "\t" + seed.getOutDegree() + "\t" + influence[j]
                        + "\t" + (influence[j] - influence[j - 1]) + "\t" + ranks[ranks.length - j].oldScore);
                // break;
            }
            if(j==50)
                break;
            }else{
                Map rs = spread.runGeneralICModel(graph, newseeds, R);
                double influ = Double.parseDouble(rs.get("AVE_SPREAD_SIZE").toString());
                double var = Double.parseDouble(rs.get("VAR").toString());
                influence[j] = influ;
                System.out.println(j + "\t" + seed.getNodeID() + "\t" + seed.getOutDegree() + "\t" + influence[j]
                        + "\t" + (influence[j] - influence[j - 1]) + "\t" + ranks[ranks.length - j].oldScore);
            }
        }
    }

    public void getInfluence(int[] ranks, int k, String gfile, int R) {
        // double p = 0.3;
        // String gfile = "E:/Experiments/IMRank/karate_edgelist_1.txt";
        double influence[] = new double[ranks.length + 1];
        influence[0] = 0;
        MultiEdgesDirectedGraph g = MultiEdgesDirectedGraph.ConstructFromUndirectedFile(gfile);
        SeedsInfluenceSpread spread = new SeedsInfluenceSpread(g);

        // TopkComponentsForUniformIC topk = new TopkComponentsForUniformIC();
        // double[] temp = topk.extractSeedsSG(gfile, k, p, R);

        List<Node> newseeds = new ArrayList<Node>();
        for (int j = 1; j < k + 1; j++) {
            Node seed = g.getNodeByID(ranks[ranks.length - j]);
            newseeds.add(seed);
            if (j == k) {
                Map rs = spread.getInfluenceSpread(newseeds, p, R);
                double influ = Double.parseDouble(rs.get("AVE_SPREAD_SIZE").toString());
                double var = Double.parseDouble(rs.get("VAR").toString());
                influence[j] = influ;
                System.out.println(j + "\t" + seed.getNodeID() + "\t" + seed.getOutDegree() + "\t" + influence[j]
                        + "\t" + (influence[j] - influence[j - 1]));
            }
        }
    }

    public IMDivRank(String gfile, boolean directed) {
        if (directed)
            graph = DirectedGraph.ConstructFromFile(gfile);
        else
            graph = DirectedGraph.ConstructFromUndirectedFile(gfile);
    }

    /*
     * public IMDivRank(DirectedGraph graph, double p) { this.graph = graph; this.p = p; }
     */

    /**
     * this is a variance of pagerank and divRank, it send score to all the nodes according to their ranks, which is
     * that the node ranked first achieve the scores first,
     * 
     */
    public Node[] rankByDegree() {
        // assign one unit score to every node
        List<Node> nodes = graph.getNodes();
        double[] preScore = new double[nodes.size()];
        Node[] preNode = new Node[nodes.size()];

        for (int i = 0; i < nodes.size(); i++) {
            preScore[i] = nodes.get(i).getOutDegree();
            preNode[i] = nodes.get(i);
        }
        this.quicksqrt(preScore, preNode, 0, nodes.size() - 1);
        return preNode;
    }

    /*
     * PageRank
     */
    public Node[] pageRank(double d) {

        // assign one unit score to every node
        List<Node> nodes = graph.getNodes();
        for (Node node : nodes) {
            node.rspre = 1;
        }

        // diffusion to stable state
        double error = 10000;
        double error_threshold = 0.00002;
        int step = 1;
        while (error > error_threshold) {
            System.out.println("step: " + step + " error: " + error);
            // int count = 0;
            double other = 0;
            for (Node node : nodes) {
                if (node.getOutDegree() == 0) {
                    double partscore = (1 - d) * node.rspre / (double) graph.getGraphSize();
                    other = other + partscore;
                    continue;
                }
                int under = node.getOutDegree();
                List<Node> leaders = node.getLeaders();

                double partscore = (1 - d) * node.rspre / (double) under;

                for (Node leader : leaders)
                    leader.rsafter = leader.rsafter + partscore;
            }

            // caculate error
            error = 0;
            for (Node node : nodes) {
                node.rsafter = node.rsafter + d + other;
                error = error + Math.abs(node.rsafter - node.rspre);
            }

            error = error / (double) graph.getGraphSize();

            for (Node node : nodes) {
                node.rspre = node.rsafter;
                node.rsafter = 0;
            }

            step++;
        }

        double[] preScore = new double[nodes.size()];
        Node[] preNode = new Node[nodes.size()];
        for (int i = 0; i < nodes.size(); i++) {
            preScore[i] = nodes.get(i).rspre;
            preNode[i] = nodes.get(i);
        }
        this.quicksqrt(preScore, preNode, 0, nodes.size() - 1);
        return preNode;

    }

    public void setInitialRanks(String kind) {
        if (kind.equals("degree")) {

            for (Node node : graph.nodes){
                node.newScore = node.getOutDegree();
            }

        } else if (kind.equals("localinfluence")) {

            for (Node node : graph.nodes) {
                Map<Node, Double> temp = new HashMap<Node, Double>();
                for (Node fan : node.getFans()) {
                    if (temp.containsKey(fan)) {
                        double p = node.propagation.get(fan);
                        double remain = temp.get(fan);
                        node.newScore += p * remain;
                        temp.put(fan, (1 - p) * remain);
                    } else {
                        double p = node.propagation.get(fan);
                        node.newScore += p;
                        temp.put(fan, 1 - p);
                    }
                }
            }

        } else if (kind.equals("competitiveinfluence")) {

            for (Node node : graph.nodes) {
                // every node send score to its leaders according to its influence ability
                List<Node> leaders = node.getLeaders();
                double remain = 1;
                double totalP = 0;
                for (Node leader : leaders) {
                    double p = leader.propagation.get(node);
                    remain = remain * (1 - p);
                    totalP += p;
                }

                for (Node leader : leaders) {
                    double p = leader.propagation.get(node);
                    leader.newScore += (1 - remain) * (p / totalP);
                }

                node.newScore += remain;
            }

        } else if (kind.equals("IRIE")) {

            for (Node node : graph.nodes) {
                node.oldScore = 1.0;
                node.newScore = 0;
            }
            double d = 0.7;

            int step = 20;
            while (step > 0) {
                for (Node node : graph.nodes) {
                    for (Node fan : node.getFans()) {
                        double p = node.propagation.get(fan);
                        node.newScore += p * fan.oldScore;
                    }
                    node.newScore = 1 + d * node.newScore;
                }
                for (Node node : graph.nodes) {
                    node.oldScore = node.newScore;
                    node.newScore = 0;
                }
                step--;
            }

            for (Node node : graph.nodes) {
                node.newScore =  node.oldScore;
                node.oldScore = 1.0;
            }
            
        } else if (kind.equals("randomprior")) {
            // random to set a piror to each leaders
            for (Node node : graph.nodes) {

                List<Node> templeaders = new ArrayList<Node>();
                for (Node leader : node.getLeaders())
                    templeaders.add(leader);
                double remain = 1;
                while (templeaders.size() > 0) {
                    int index = (int) (Math.random() * templeaders.size());
                    double p = templeaders.get(index).propagation.get(node);
                    templeaders.get(index).newScore += remain * p;
                    templeaders.remove(index);
                    remain = (1 - p) * remain;
                }
                node.newScore += remain;
            }
        } else {
            System.out.println("wrong initial rank");
        }

    }

    public void setDeliverScores(String kind, Node[] ranknodes) {
        if (kind.equals("higherleaders")) {

            // only the first leaders
            for (Node node : ranknodes) {
                // get all its leaders, including the second leaders
                Node[] leaders = new Node[30000];
                double[] ranks = new double[30000];
                int count = 0;

                // the 1-leaders
                for (Node leader : node.getLeaders()) {
                    if (leader.rank > node.rank)
                        continue;
                    leaders[count] = leader;
                    ranks[count] = leader.rank;
                    count++;
                }

                this.quicksqrt(ranks, leaders, 0, count - 1);

                double remain = 1;
                double totalSend = 0;

                for (int i = 0; i < count; i++) {
                    double p = leaders[i].propagation.get(node);
                    double send = p * (node.newScore - totalSend);
                    leaders[i].newScore += send;
                    leaders[i].getFromFans.put(node, send);
                    remain = remain * (1 - p);
                    totalSend += send;
                }
                node.newScore = node.newScore - totalSend;
            }

        } else if (kind.equals("highersecondleaders")) {
            // first leaders + second leaders
            for (Node node : ranknodes) {
                // get all its leaders, including the second leaders
                Node[] secondleaders = new Node[30000];
                double[] ranks = new double[30000];
                Node[] media = new Node[30000];
                int count = 0;

                // the 1-leaders
                for (Node leader : node.getLeaders()) {
                    if (leader.rank > node.rank)
                        continue;
                    secondleaders[count] = leader;
                    media[count] = null;
                    ranks[count] = leader.rank;
                    count++;
                }

                // the 2-leaders
                for (Node leader : node.getLeaders()) {
                    if (leader.rank < node.rank)
                        continue;
                    for (Node secondleader : leader.getLeaders()) {
                        if (secondleader.rank > node.rank)
                            continue;
                        if (secondleader.equals(node))
                            continue;
                        secondleaders[count] = secondleader;
                        media[count] = leader;
                        ranks[count] = secondleader.rank;
                        count++;
                    }
                }

                this.quicksqrt(ranks, media, secondleaders, 0, count - 1);

                double remain = 1;
                double totalSend = 0;

                for (int i = 0; i < count; i++) {

                    if (media[i] == null) {// direct leaders
                        double p = secondleaders[i].propagation.get(node);
                        double send = p * (node.newScore - totalSend);
                        secondleaders[i].newScore += send;
                        secondleaders[i].getFromFans.put(node, send);
                        remain = remain * (1 - p);
                        totalSend += send;
                    } else { // indirected leaders
                        double scoreFromMedia = 0;
                        if (node.getFromFans.containsKey(media[i])) {
                            scoreFromMedia = node.getFromFans.get(media[i]);
                        }
                        double p1 = secondleaders[i].propagation.get(media[i]);
                        double p2 = media[i].propagation.get(node);
                        double send = p1 * p2 * (node.newScore - totalSend - scoreFromMedia);
                        secondleaders[i].newScore += send;
                        secondleaders[i].getFromFans.put(node, send);
                        totalSend += send;
                    }
                }
                node.newScore = node.newScore - totalSend;
            }

        } else if (kind.equals("allleaders")) {

            // all the leaders
            for (Node node : ranknodes) {
                // get all its leaders
                Node[] leaders = new Node[30000];
                double[] ranks = new double[30000];
                int count = 0;

                // the 1-leaders
                for (Node leader : node.getLeaders()) {
                    // if (leader.rank > node.rank)
                    // continue;
                    leaders[count] = leader;
                    ranks[count] = leader.rank;
                    count++;
                }

                this.quicksqrt(ranks, leaders, 0, count - 1);

                //double remain = 1;
                double totalSend = 0;

                for (int i = 0; i < count; i++) {
                    double p = leaders[i].propagation.get(node);
                    double send = p * (node.oldScore - totalSend);
                    leaders[i].newScore += send;
                    leaders[i].getFromFans.put(node, send);
                    //remain = remain * (1 - p);
                    totalSend += send;
                }
                node.newScore += node.oldScore - totalSend;
            }

        } else if (kind.equals("highestleader")) {

            // give score to only one neighbor with highest score
            for (Node node : ranknodes) {
                Node highestNei = node;
                for (Node leader : node.getLeaders()) {
                    if (highestNei.rank > leader.rank)
                        highestNei = leader;
                }

                if (highestNei.equals(node)){
                    node.newScore += node.oldScore;
                    continue;
                }
                double p = highestNei.propagation.get(node);
                double send = p * node.oldScore;
                highestNei.newScore += send;
                highestNei.getFromFans.put(node, send);
                node.newScore += node.oldScore - send;
            }

        } else {
            System.out.println("wrong score rules");
        }

    }

    public void setFinalScore(String kind) {
        if (kind.equals("reset")) {
            for (Node node : graph.nodes){
            node.oldScore = node.newScore;
            node.newScore = 1;
            }
        } else if (kind.equals("personalized")) {
            
            for (Node node : graph.nodes){
                node.oldScore = node.newScore;
                node.newScore = 1 + node.oldScore;
            }
        } else if (kind.equals("cycle")) {
            for (Node node : graph.nodes){
                node.oldScore = node.newScore;
                node.newScore = 0;
              }
            //do nothing
        }else if(kind.equals("cyclereset")){
            for (Node node : graph.nodes){
                node.oldScore = node.newScore;
            }
        } else{
            System.out.println("wrong final score setting");
        }

    }

    /**
     * this is a variance of pagerank and divRank, it send score to all the nodes according to their ranks, which is
     * that the node ranked first achieve the scores first,
     * 
     */
    public Node[] calculateScoreDivRank(double d) {
        // assign one unit score to every node
        List<Node> nodes = graph.getNodes();
        for (Node node : nodes) {
            node.rspre = 1;
        }

        // diffusion to stable state
        double error = 10000;
        double error_threshold = 0.001;
        int step = 1;
        double[] preScore = new double[nodes.size()];
        Node[] preNode = new Node[nodes.size()];

        while (true) {
            System.out.println("step:\t" + step);

            // to rank by their present scores
            if (step == 1) {
                // at the initial state, the rank of all the node are the same
                for (Node node : nodes)
                    node.rank = 1;
            } else {
                // in other cases, rank by their scores

                for (int i = 0; i < nodes.size(); i++) {
                    preScore[i] = nodes.get(i).rspre;
                    preNode[i] = nodes.get(i);
                }
                this.quicksqrt(preScore, preNode, 0, nodes.size() - 1);
                if (error < error_threshold) {
                    return preNode;
                }
                for (int i = 0; i < nodes.size(); i++)
                    preNode[i].rank = nodes.size() - i;
            }

            double other = 0;
            for (Node node : nodes) {
                if (node.getLeaders().size() == 0) {
                    double partscore = (1 - d) * node.rspre / (double) graph.getGraphSize();
                    other = other + partscore;
                    continue;
                }
                // only send (1-d)*Score to nodes with higher nodes than itself
                List<Integer> TrueLeaders = new ArrayList<Integer>();
                // double totalp = 0;
                for (int i = 0; i < node.getLeaders().size(); i++) {
                    if (node.rank >= node.getLeaders().get(i).rank) {
                        TrueLeaders.add(i);
                    }
                }
                if (TrueLeaders.size() == 0) {
                    double partscore = (1 - d) * node.rspre / (double) graph.getGraphSize();
                    other = other + partscore;
                    continue;
                }
                double sendScore = node.rspre * (1 - d) / TrueLeaders.size();
                for (Integer index : TrueLeaders) {
                    Node leader = node.getLeaders().get(index);
                    leader.rsafter += sendScore;
                }

            }

            error = 0;
            for (Node node : nodes) {
                node.rsafter = node.rsafter + d + other;
                error = error + Math.abs(node.rsafter - node.rspre);
            }

            error = error / (double) graph.getGraphSize();
            System.out.println("error:\t" + error);
            for (Node node : nodes) {
                node.rspre = node.rsafter;
                node.rsafter = 0;
                System.out.println(node.getNodeID() + "\t" + node.rspre);
            }

            step++;
        }
        // System.out.println("Done!");
    }

    /**
     * in this calculateScore, the take back mechanism is used
     */
    public void calculateScoreTakeBack() {
        // assign one unit score to every node
        List<Node> nodes = graph.getNodes();
        for (Node node : nodes) {
            node.rspre = 1;
            node.oldScore = 1;
        }

        // diffusion to stable state
        double error = 10000;
        double error_threshold = 0.00002;
        int step = 1;
        // while (error > error_threshold) {
        while (step < 2000) {
            System.out.println("step:\t" + step);
            for (Node node : nodes) {
                // check its fans w, if w's score is equal, return w by 1/2 score received from w, if w's score is
                // larger, return all the score, if w's score is small, do nothing
                List<Node> fans = node.getFans();
                for (Node fan : fans) {
                    boolean back = false;
                    if (fan.oldScore > node.oldScore)
                        back = true;
                    if (fan.oldScore < node.oldScore)
                        back = false;
                    if (fan.oldScore == node.oldScore)
                        if (Math.random() > 0.5)
                            back = true;
                        else
                            back = false;

                    // back is true means return the score to this fan
                    // back is false means get more influence from this fan

                    if (back) {
                        // return the score
                        if (node.getFromFans.get(fan) == null)
                            node.getFromFans.put(fan, 0.0);
                        double presend = (Double) node.getFromFans.get(fan);
                        if (presend == 0)
                            continue; // if this fan has been stop to send score, skip it
                        // else, return the score to it
                        fan.newRecieve += presend;
                        node.getFromFans.put(fan, 0.0);
                        node.newback += presend;
                    } else {
                        // continue to grap scores from this neighbor
                        double remain = Math.pow(1 - p, fan.getLeaders().size());
                        double partScore = (1 - remain) / fan.getLeaders().size();
                        if (node.getFromFans.get(fan) == null)
                            node.getFromFans.put(fan, 0.0);
                        double diff = partScore - (Double) node.getFromFans.get(fan);
                        if (diff == 0)
                            continue;
                        node.newRecieve += diff;
                        node.getFromFans.put(fan, partScore);
                        fan.newsend += partScore;
                    }

                }

            }

            for (Node node : nodes) {
                // if(node.newRecieve == 0 && node.newback == 0 && node.newsend==0)
                // continue;
                node.oldScore = node.oldScore + node.newRecieve - node.newback - node.newsend;
                System.out.println(node.getNodeID() + "\t" + node.oldScore);
                node.newRecieve = 0;
                node.newback = 0;
                node.newsend = 0;
            }

            // caculate error
            // error = 0;
            // for (Node node : nodes) {
            // node.rsafter = node.rsafter + p + other;
            // error = error + Math.abs(node.rsafter - node.rspre);
            // }

            // error = error / (double) graph.getGraphSize();

            // for (Node node : nodes) {
            // node.oldScore = node.oldScore + node.newRecieve -node.newback;
            // node.rspre = node.rsafter;
            // node.rsafter = 0;

            // }

            step++;
        }

        System.out.println("Done!");
    }

    /**
     * in this calculateScore, the take back mechanism is used, ranking
     */
    public void calculateScoreTakeBackEasyRanking() {
        // assign one unit score to every node
        List<Node> nodes = graph.getNodes();
        for (Node node : nodes) {
            node.rspre = 1;
            node.oldScore = 1;
        }

        // diffusion to stable state
        double error = 10000;
        double error_threshold = 0.00002;
        int step = 1;
        // while (error > error_threshold) {
        while (step < 2000) {
            System.out.println("step:\t" + step);
            for (Node node : nodes) {
                // check its fans w, if w's score is equal, return w by 1/2 score received from w, if w's score is
                // larger, return all the score, if w's score is small, do nothing
                List<Node> fans = node.getFans();
                for (Node fan : fans) {
                    boolean back = false;
                    if (fan.oldScore > node.oldScore)
                        back = true;
                    if (fan.oldScore < node.oldScore)
                        back = false;
                    if (fan.oldScore == node.oldScore)
                        if (Math.random() > 0.5)
                            back = true;
                        else
                            back = false;

                    // back is true means return the score to this fan
                    // back is false means get more influence from this fan

                    if (back) {
                        // return the score
                        if (node.getFromFans.get(fan) == null)
                            node.getFromFans.put(fan, 0.0);
                        double presend = (Double) node.getFromFans.get(fan);
                        if (presend == 0)
                            continue; // if this fan has been stop to send score, skip it
                        // else, return the score to it
                        fan.newRecieve += presend;
                        node.getFromFans.put(fan, 0.0);
                        node.newback += presend;
                    } else {
                        // continue to grap scores from this neighbor
                        double remain = Math.pow(1 - p, fan.getLeaders().size());
                        double partScore = (1 - remain) / fan.getLeaders().size();
                        if (node.getFromFans.get(fan) == null)
                            node.getFromFans.put(fan, 0.0);
                        double diff = partScore - (Double) node.getFromFans.get(fan);
                        if (diff == 0)
                            continue;
                        node.newRecieve += diff;
                        node.getFromFans.put(fan, partScore);
                        fan.newsend += partScore;
                    }

                }

            }

            for (Node node : nodes) {
                // if(node.newRecieve == 0 && node.newback == 0 && node.newsend==0)
                // continue;
                node.oldScore = node.oldScore + node.newRecieve - node.newback - node.newsend;
                System.out.println(node.getNodeID() + "\t" + node.oldScore);
                node.newRecieve = 0;
                node.newback = 0;
                node.newsend = 0;
            }

            // caculate error
            // error = 0;
            // for (Node node : nodes) {
            // node.rsafter = node.rsafter + p + other;
            // error = error + Math.abs(node.rsafter - node.rspre);
            // }

            // error = error / (double) graph.getGraphSize();

            // for (Node node : nodes) {
            // node.oldScore = node.oldScore + node.newRecieve -node.newback;
            // node.rspre = node.rsafter;
            // node.rsafter = 0;

            // }

            step++;
        }

        System.out.println("Done!");
    }

    /**
     * in this calculateScore, the take back mechanism is used, and give more score to the node with higher score
     */

    public void calculateScoreTakeBackByRanking() {
        // assign one unit score to every node
        List<Node> nodes = graph.getNodes();
        for (Node node : nodes) {
            node.rspre = 1;
            node.oldScore = 1;
        }

        // initial
        for (Node node : nodes) {
            // every node send score to its leaders average
            List<Node> leaders = node.getLeaders();
            double remain = Math.pow(1 - p, leaders.size());
            double send = (1 - remain) * node.oldScore / leaders.size();
            System.out.println(node.getNodeID() + "\tsend to leaders(" + leaders.size() + ")\t" + send);
            for (Node leader : leaders) {
                leader.getFromFans.put(node, send);
                leader.newRecieve += send;
                node.newsend += send;
            }
        }

        for (Node node : nodes) {
            node.oldScore = node.oldScore + node.newRecieve - node.newback - node.newsend;
            System.out.println(node.getNodeID() + "\t" + node.oldScore);
            node.newRecieve = 0;
            node.newback = 0;
            node.newsend = 0;

        }

        // diffusion to stable state
        // double error = 10000;
        // double error_threshold = 0.00002;
        int step = 1;
        // while (error > error_threshold) {
        while (step < 2000) {
            System.out.println("step:\t" + step);
            for (Node node : nodes) {
                // check its fans w, if w's score is equal, return w by 1/2 score received from w, if w's score is
                // larger, return all the score, if w's score is small, do nothing
                List<Node> fans = node.getFans();

                for (Node fan : fans) {
                    boolean back = false;
                    if (fan.oldScore > node.oldScore)
                        back = true;
                    if (fan.oldScore < node.oldScore)
                        back = false;
                    if (fan.oldScore == node.oldScore)
                        if (Math.random() > 0.5)
                            back = true;
                        else
                            back = false;

                    // back is true means return the score to this fan
                    // back is false means get more influence from this fan

                    if (back) {
                        // return the score
                        if (node.getFromFans.get(fan) == null)
                            node.getFromFans.put(fan, 0.0);
                        double presend = (Double) node.getFromFans.get(fan);
                        if (presend == 0)
                            continue; // if this fan has been stop to send score, skip it
                        // else, return the score to it
                        fan.newRecieve += presend;
                        node.getFromFans.put(fan, 0.0);
                        node.newback += presend;
                    } else {
                        // continue to grap scores from this neighbor
                        // double remain = Math.pow(1-p, fan.getLeaders().size());
                        // double partScore = (1-remain)/fan.getLeaders().size();
                        // if(node.getFromFans.get(fan)==null)
                        // node.getFromFans.put(fan, 0.0);
                        // double diff = partScore - (Double)node.getFromFans.get(fan);
                        // if(diff==0)
                        // continue;
                        // node.newRecieve += diff;
                        // node.getFromFans.put(fan, partScore);
                        // fan.newsend += partScore;
                    }

                }

            }

            for (Node node : nodes) {
                // check its leader w, if w's score is equal, grap from w by 1/2 score received from w, if w's score is
                // larger, send it new score by rank
                List<Node> leaders = node.getLeaders();
                double scorediff[] = new double[leaders.size()];
                Node templeaders[] = new Node[leaders.size()];
                int count = 0;
                for (Node leader : leaders) {
                    scorediff[count] = leader.oldScore - node.oldScore;
                    templeaders[count] = leader;
                    count++;
                }

                quicksqrt(scorediff, templeaders, 0, leaders.size() - 1);
                // Arrays.sort(scorediff);
                double remain = 1;
                for (int k = leaders.size() - 1; k >= 0; k--) {
                    if (scorediff[k] < 0)
                        break;

                    if (scorediff[k] == 0)
                        if (Math.random() > 0.5)
                            continue;

                    double newsend = node.oldScore * p * remain;
                    remain = remain * (1 - p);
                    double presend = 0;
                    // if(!templeaders[k].getFromFans.containsKey(node))
                    // templeaders[k].getFromFans.put(node, 0.0);
                    presend = templeaders[k].getFromFans.get(node);
                    templeaders[k].getFromFans.put(node, newsend);
                    templeaders[k].newRecieve += newsend - presend;
                    node.newsend += newsend - presend;
                    System.out.println(node.getNodeID() + "\tsend new score\t" + newsend + "(" + (newsend - presend)
                            + ")" + "\tto\t" + templeaders[k].getNodeID());
                }
            }

            for (Node node : nodes) {
                // if(node.newRecieve == 0 && node.newback == 0 && node.newsend==0)
                // continue;
                node.oldScore = node.oldScore + node.newRecieve - node.newback - node.newsend;
                System.out.println(node.getNodeID() + "\t" + node.oldScore);
                node.newRecieve = 0;
                node.newback = 0;
                node.newsend = 0;
            }

            step++;
        }

        System.out.println("Done!");
    }

    /**
     * in this calculateScore, the take back mechanism is used, and give more score to the node with higher score
     */

    public void calculateScoreTakeBackRanking() {
        // assign one unit score to every node
        List<Node> nodes = graph.getNodes();
        for (Node node : nodes) {
            node.rspre = 1;
            node.oldScore = 1;
        }

        // initial
        for (Node node : nodes) {
            // every node send score to its leaders average
            List<Node> leaders = node.getLeaders();
            double remain = Math.pow(1 - p, leaders.size());
            double send = (1 - remain) * node.oldScore / leaders.size();
            System.out.println(node.getNodeID() + "\tsend to leaders(" + leaders.size() + ")\t" + send);
            for (Node leader : leaders) {
                leader.getFromFans.put(node, send);
                leader.newRecieve += send;
                node.newsend += send;

            }
        }

        for (Node node : nodes) {
            node.oldScore = node.oldScore + node.newRecieve - node.newsend;
        }

        int step = 1;
        double compareoldrankscores[] = new double[nodes.size()];
        for (int i = 0; i < nodes.size(); i++)
            compareoldrankscores[i] = 1;
        double newrankscores[] = new double[nodes.size()];
        double error_bound = 0.001;

        while (true) {

            double oldrankscores[] = new double[nodes.size()];
            for (int i = 0; i < nodes.size(); i++) {
                if (step == 1)
                    newrankscores[i] = nodes.get(i).getOutDegree();
                else
                    newrankscores[i] = nodes.get(i).oldScore;

            }

            // compare
            double error = 0;
            // compare the new rankscores vs the old rankscores
            for (int i = 0; i < nodes.size(); i++) {
                System.out.println(nodes.get(i).getNodeID() + "\t" + newrankscores[i] + "\t" + compareoldrankscores[i]);
                error += Math.abs(newrankscores[i] - compareoldrankscores[i]);
            }

            if (error / nodes.size() < error_bound)
                break;
            else
                System.out.println(error / nodes.size());

            // store score to oldrankscores
            Node ranknodes[] = new Node[nodes.size()];
            for (int i = 0; i < nodes.size(); i++) {
                compareoldrankscores[i] = newrankscores[i];
                oldrankscores[i] = newrankscores[i];
                ranknodes[i] = nodes.get(i);
            }

            this.quicksqrt(oldrankscores, ranknodes, 0, nodes.size() - 1);
            for (int i = 0; i < nodes.size(); i++)
                ranknodes[i].rank = nodes.size() - i;

            // from the lowest nodes to calculate the scores of each nodes

            System.out.println("step:\t" + step);
            for (Node node : ranknodes)
                node.oldScore = 1;

            for (Node node : ranknodes) {

                // get all its leaders
                List<Node> leaders = node.getLeaders();
                double rank[] = new double[leaders.size()];
                Node templeaders[] = new Node[leaders.size()];
                for (int i = 0; i < leaders.size(); i++) {
                    rank[i] = leaders.get(i).rank;
                    templeaders[i] = leaders.get(i);
                }
                this.quicksqrt(rank, templeaders, 0, leaders.size() - 1);

                double remain = 1;
                for (int i = 0; i < leaders.size(); i++) {
                    if (rank[i] > node.rank)
                        break;
                    templeaders[i].oldScore += p * remain * node.oldScore;
                    remain = remain * (1 - p);
                }
                node.oldScore = remain * node.oldScore;
            }

            step++;
        }

        System.out.println("Done!");
    }

    public Node[] calculateScoreTakeBackRankingFirstNeighborsGoodInitial() {
        // assign one unit score to every node
        List<Node> nodes = graph.getNodes();
        for (Node node : nodes) {
            node.rspre = 1;
            node.oldScore = 1;
        }

        // initial 1
        for (Node node : nodes) {
            // every node send score to its leaders average
            List<Node> leaders = node.getLeaders();
            double remain = Math.pow(1 - p, leaders.size());
            double send = (1 - remain) * node.oldScore / leaders.size();
            System.out.println(node.getNodeID() + "\tsend to leaders(" + leaders.size() + ")\t" + send);
            for (Node leader : leaders) {
                leader.getFromFans.put(node, send);
                leader.newRecieve += send;
                node.newsend += send;
            }
        }

        for (Node node : nodes) {
            node.oldScore = node.oldScore + node.newRecieve - node.newsend;
        }

        int step = 1;
        double compareoldrankscores[] = new double[nodes.size()];
        for (int i = 0; i < nodes.size(); i++)
            compareoldrankscores[i] = 1;
        double newrankscores[] = new double[nodes.size()];
        double error_bound = 0.000001;

        while (true) {

            double oldrankscores[] = new double[nodes.size()];
            double totalScores = 0;
            for (int i = 0; i < nodes.size(); i++) {
                if (step == 1)
                    newrankscores[i] = nodes.get(i).getOutDegree();
                else
                    newrankscores[i] = nodes.get(i).oldScore;
                totalScores += newrankscores[i];
            }

            System.out.println("step:\t" + step + "\ttotalScore=\t" + totalScores);

            // compare
            double error = 0;
            // compare the new rankscores vs the old rankscores
            for (int i = 0; i < nodes.size(); i++) {
                // System.out.println(nodes.get(i).getNodeID() + "\t" + newrankscores[i] + "\t" +
                // compareoldrankscores[i]);
                error += Math.abs(newrankscores[i] - compareoldrankscores[i]);
            }

            // store score to oldrankscores
            Node ranknodes[] = new Node[nodes.size()];
            for (int i = 0; i < nodes.size(); i++) {
                compareoldrankscores[i] = newrankscores[i];
                oldrankscores[i] = newrankscores[i];
                ranknodes[i] = nodes.get(i);
            }

            this.quicksqrt(oldrankscores, ranknodes, 0, nodes.size() - 1);

            // print out the top-50
            for (int i = 1; i <= 50; i++) {
                System.out.println(i + "\t" + ranknodes[nodes.size() - i].getNodeID() + "\t"
                        + oldrankscores[nodes.size() - i]);
            }
            if (error / nodes.size() < error_bound || step == 40) {
                return ranknodes;
                // break;
            } else {
                System.out.println(step + ":\t" + error / nodes.size());
            }

            for (int i = 0; i < nodes.size(); i++)
                ranknodes[i].rank = nodes.size() - i;

            // from the lowest nodes to calculate the scores of each nodes

            // System.out.println("step:\t" + step);
            for (Node node : ranknodes)
                node.oldScore = 1;// 2013.05.02

            for (Node node : ranknodes) {

                // get all its leaders, including the second leaders
                Node[] secondleaders = new Node[30000];
                double[] ranks = new double[30000];
                Node[] media = new Node[30000];
                int count = 0;

                // the 1-leaders
                for (Node leader : node.getLeaders()) {
                    if (leader.rank > node.rank)
                        continue;
                    secondleaders[count] = leader;
                    media[count] = null;
                    ranks[count] = leader.rank;
                    count++;
                }

                // the 2-leaders
                /*
                 * for (Node leader : node.getLeaders()) { if (leader.rank < node.rank) continue; for (Node secondleader :
                 * leader.getLeaders()) { if (secondleader.rank > node.rank) continue; if (secondleader.equals(node))
                 * continue; secondleaders[count] = secondleader; media[count] = leader; ranks[count] =
                 * secondleader.rank; count++; } }
                 */
                // double rank[] = new double[count];
                // Node templeaders[] = new Node[count];
                this.quicksqrt(ranks, media, secondleaders, 0, count - 1);
                // this.quicksqrt(ranks, media, 0, count - 1);

                double remain = 1;
                double totalSend = 0;
                // double remainScore = node.oldScore;

                for (int i = 0; i < count; i++) {
                    // if (rank[i] > node.rank)
                    // break;

                    if (media[i] == null) {// direct leaders
                        // double send = p * remain * node.oldScore;//2013.5.2
                        double send = p * (node.oldScore - totalSend);
                        secondleaders[i].oldScore += send;
                        secondleaders[i].getFromFans.put(node, send);
                        // System.out.println(node.getNodeID() + "\tsend to\t" + secondleaders[i].getNodeID() + "\t" +
                        // send + "\tscores");
                        remain = remain * (1 - p);
                        totalSend += send;
                        // if(secondleaders[i].getNodeID() == 17544)
                        // System.out.println("Null \t"+node.getNodeID() + "\tsend to\t" + secondleaders[i].getNodeID()
                        // + "\t" + send + "\tscores");
                    } else {
                        // indirected leaders
                        double scoreFromMedia = 0;
                        if (node.getFromFans.containsKey(media[i])) {
                            scoreFromMedia = node.getFromFans.get(media[i]);
                        }
                        // double send = p*p*remain*(node.oldScore-scoreFromMedia);//2013.5.2
                        // double send = p * p * remain * (node.oldScore);
                        double send = p * p * (node.oldScore - totalSend - scoreFromMedia);
                        secondleaders[i].oldScore += send;
                        // System.out.println(node.getNodeID() + "\tsend to\t" + secondleaders[i].getNodeID() + "\t" +
                        // send + "\tscores");
                        // if(secondleaders[i].getNodeID() == 17544)
                        // System.out.println("Media \t" + node.getNodeID() + "\tsend to\t" +
                        // secondleaders[i].getNodeID() + "\t" + send + "\tscores");
                        secondleaders[i].getFromFans.put(node, send);
                        // remain = remain * (1 - p * p); 2013.5.2
                        totalSend += send;
                    }
                }
                // System.out.println(node.getNodeID() + "\t" + node.oldScore);
                // node.oldScore = remain * node.oldScore;
                node.oldScore = node.oldScore - totalSend;
                // System.out.println("\t--->\t" + node.oldScore);
            }
            System.out.println("next step");
            step++;
        }

        // System.out.println("Done!");
        // return null;
    }

    public Node[] calculateScoreTakeBackRankingFirstNeighborsALL() {
        // assign one unit score to every node
        List<Node> nodes = graph.getNodes();
        for (Node node : nodes) {
            node.rspre = 1;
            node.oldScore = 1;
        }

        // initial 1
        for (Node node : nodes) {
            // every node send score to its leaders average
            List<Node> leaders = node.getLeaders();
            double remain = Math.pow(1 - p, leaders.size());
            double send = (1 - remain) * node.oldScore / leaders.size();
            System.out.println(node.getNodeID() + "\tsend to leaders(" + leaders.size() + ")\t" + send);
            for (Node leader : leaders) {
                leader.getFromFans.put(node, send);
                leader.newRecieve += send;
                node.newsend += send;
            }
        }

        for (Node node : nodes) {
            node.oldScore = node.oldScore + node.newRecieve - node.newsend;
        }

        int step = 1;
        double compareoldrankscores[] = new double[nodes.size()];
        for (int i = 0; i < nodes.size(); i++)
            compareoldrankscores[i] = 1;
        double newrankscores[] = new double[nodes.size()];
        double error_bound = 0.000001;

        while (true) {

            double oldrankscores[] = new double[nodes.size()];
            double totalScores = 0;
            for (int i = 0; i < nodes.size(); i++) {
                if (step == 0)
                    newrankscores[i] = nodes.get(i).getOutDegree();
                else
                    newrankscores[i] = nodes.get(i).oldScore;
                totalScores += newrankscores[i];
            }

            System.out.println("step:\t" + step + "\ttotalScore=\t" + totalScores);

            // compare
            double error = 0;
            // compare the new rankscores vs the old rankscores
            for (int i = 0; i < nodes.size(); i++) {
                // System.out.println(nodes.get(i).getNodeID() + "\t" + newrankscores[i] + "\t" +
                // compareoldrankscores[i]);
                error += Math.abs(newrankscores[i] - compareoldrankscores[i]);
            }

            // store score to oldrankscores
            Node ranknodes[] = new Node[nodes.size()];
            for (int i = 0; i < nodes.size(); i++) {
                compareoldrankscores[i] = newrankscores[i];
                oldrankscores[i] = newrankscores[i];
                ranknodes[i] = nodes.get(i);
            }

            this.quicksqrt(oldrankscores, ranknodes, 0, nodes.size() - 1);

            // print out the top-50
            for (int i = 1; i <= 50; i++) {
                System.out.println(i + "\t" + ranknodes[nodes.size() - i].getNodeID() + "\t"
                        + oldrankscores[nodes.size() - i]);
            }
            if (error / nodes.size() < error_bound || step == 40) {
                return ranknodes;
                // break;
            } else {
                System.out.println(step + ":\t" + error / nodes.size());
            }

            for (int i = 0; i < nodes.size(); i++)
                ranknodes[i].rank = nodes.size() - i;

            // from the lowest nodes to calculate the scores of each nodes

            // System.out.println("step:\t" + step);
            for (Node node : ranknodes)
                node.oldScore = 1;// 2013.05.02

            for (Node node : ranknodes) {

                // get all its leaders, including the second leaders
                Node[] secondleaders = new Node[30000];
                double[] ranks = new double[30000];
                Node[] media = new Node[30000];
                int count = 0;

                // the 1-leaders
                for (Node leader : node.getLeaders()) {
                    if (leader.rank > node.rank) {
                        if (Math.random() < 1.0)
                            continue;
                    }
                    secondleaders[count] = leader;
                    media[count] = null;
                    ranks[count] = leader.rank;
                    count++;
                }

                // the 2-leaders
                /*
                 * for (Node leader : node.getLeaders()) { if (leader.rank < node.rank) continue; for (Node secondleader :
                 * leader.getLeaders()) { if (secondleader.rank > node.rank) continue; if (secondleader.equals(node))
                 * continue; secondleaders[count] = secondleader; media[count] = leader; ranks[count] =
                 * secondleader.rank; count++; } }
                 */
                // double rank[] = new double[count];
                // Node templeaders[] = new Node[count];
                this.quicksqrt(ranks, media, secondleaders, 0, count - 1);
                // this.quicksqrt(ranks, media, 0, count - 1);

                double remain = 1;
                double totalSend = 0;
                // double remainScore = node.oldScore;

                for (int i = 0; i < count; i++) {
                    // if (rank[i] > node.rank)
                    // break;

                    if (media[i] == null) {// direct leaders
                        // double send = p * remain * node.oldScore;//2013.5.2
                        double send = p * (node.oldScore - totalSend);
                        secondleaders[i].oldScore += send;
                        secondleaders[i].getFromFans.put(node, send);
                        // System.out.println(node.getNodeID() + "\tsend to\t" + secondleaders[i].getNodeID() + "\t" +
                        // send + "\tscores");
                        remain = remain * (1 - p);
                        totalSend += send;
                        // if(secondleaders[i].getNodeID() == 17544)
                        // System.out.println("Null \t"+node.getNodeID() + "\tsend to\t" + secondleaders[i].getNodeID()
                        // + "\t" + send + "\tscores");
                    } else {
                        // indirected leaders
                        double scoreFromMedia = 0;
                        if (node.getFromFans.containsKey(media[i])) {
                            scoreFromMedia = node.getFromFans.get(media[i]);
                        }
                        // double send = p*p*remain*(node.oldScore-scoreFromMedia);//2013.5.2
                        // double send = p * p * remain * (node.oldScore);
                        double send = p * p * (node.oldScore - totalSend - scoreFromMedia);
                        secondleaders[i].oldScore += send;
                        // System.out.println(node.getNodeID() + "\tsend to\t" + secondleaders[i].getNodeID() + "\t" +
                        // send + "\tscores");
                        // if(secondleaders[i].getNodeID() == 17544)
                        // System.out.println("Media \t" + node.getNodeID() + "\tsend to\t" +
                        // secondleaders[i].getNodeID() + "\t" + send + "\tscores");
                        secondleaders[i].getFromFans.put(node, send);
                        // remain = remain * (1 - p * p); 2013.5.2
                        totalSend += send;
                    }
                }
                // System.out.println(node.getNodeID() + "\t" + node.oldScore);
                // node.oldScore = remain * node.oldScore;
                node.oldScore = node.oldScore - totalSend;
                // System.out.println("\t--->\t" + node.oldScore);
            }
            System.out.println("next step");
            step++;
        }

        // System.out.println("Done!");
        // return null;
    }

    public Node[] combineRanking(String initialKind, String deliverKind, String finalScoreKind) {
        // assign one unit score to every node
        List<Node> nodes = graph.getNodes();

        int step = 1;
        Node ranknodes[] = new Node[nodes.size()];
        
        double oldscores[] = new double[nodes.size()];
        for (int i = 0; i < nodes.size(); i++){
            oldscores[i] = 1;
            nodes.get(i).oldScore =1;
            nodes.get(i).rank = 1;
        }
        double newscores[] = new double[nodes.size()];
        
        double error = Integer.MAX_VALUE;
        double error_bound = 0.000001;
        
        while (error > error_bound && step < 21) {
            
            if(step==1){
                setInitialRanks(initialKind);
            }else{
                setDeliverScores(deliverKind, ranknodes);
            }
            
            // compare oldscores vs newscores
            double newTotalScores = 0;
            double oldTotalScores = 0;
            for (int i = 0; i < nodes.size(); i++) {
                newscores[i] = nodes.get(i).newScore;
                oldscores[i] = nodes.get(i).oldScore;
                newTotalScores += newscores[i];
                oldTotalScores += oldscores[i];
                ranknodes[i] = nodes.get(i);
            }

            error = 0;
            for (int i = 0; i < nodes.size(); i++) {
                if(Math.abs(newTotalScores-oldTotalScores)>1)
                    error += Math.abs(newscores[i]/newTotalScores - oldscores[i]/oldTotalScores);
                else 
                    error += Math.abs(newscores[i] - oldscores[i]);
            }
            System.out.println("step:\t" + step + "\ttotalScore=\t" + newTotalScores+"\terror:\t"+error / nodes.size());

            //rank nodes by their newScores
            this.quicksqrt(newscores, ranknodes, 0, nodes.size() - 1);

            if(initialKind.equals("degree") && step==1){
                for(Node node: nodes)
                    node.newScore = 1;
            }
                    
            // print out the top-50 nodes
            int len = 50;
            if(nodes.size()<50)
                len = nodes.size();
            for (int i = 1; i <= len; i++) {
                //System.out.println(i + "\t" + ranknodes[nodes.size() - i].getNodeID() + "\t" + newscores[nodes.size() - i]);
            }

            for (int i = 0; i < nodes.size(); i++)
                ranknodes[i].rank = nodes.size() - i;
            
            setFinalScore(finalScoreKind);

            step++;
        }
        
        return ranknodes;
        
    }

    public Node[] calculateScoreTakeBackRankingSecondNeighborsGoodInitial() {
        // assign one unit score to every node
        List<Node> nodes = graph.getNodes();
        for (Node node : nodes) {
            node.rspre = 1;
            node.oldScore = 1;
        }

        // initial 1
        for (Node node : nodes) {
            // every node send score to its leaders average
            List<Node> leaders = node.getLeaders();
            double remain = Math.pow(1 - p, leaders.size());
            double send = (1 - remain) * node.oldScore / leaders.size();
            System.out.println(node.getNodeID() + "\tsend to leaders(" + leaders.size() + ")\t" + send);
            for (Node leader : leaders) {
                leader.getFromFans.put(node, send);
                leader.newRecieve += send;
                node.newsend += send;
            }
        }

        for (Node node : nodes) {
            node.oldScore = node.oldScore + node.newRecieve - node.newsend;
        }

        int step = 1;
        double compareoldrankscores[] = new double[nodes.size()];
        for (int i = 0; i < nodes.size(); i++)
            compareoldrankscores[i] = 1;
        double newrankscores[] = new double[nodes.size()];
        double error_bound = 0.0000001;

        while (true) {

            double oldrankscores[] = new double[nodes.size()];
            double totalScores = 0;
            for (int i = 0; i < nodes.size(); i++) {
                if (step == 0)
                    newrankscores[i] = nodes.get(i).getOutDegree();
                else
                    newrankscores[i] = nodes.get(i).oldScore;
                totalScores += newrankscores[i];
            }

            System.out.println("step:\t" + step + "\ttotalScore=\t" + totalScores);

            // compare
            double error = 0;
            // compare the new rankscores vs the old rankscores
            for (int i = 0; i < nodes.size(); i++) {
                // System.out.println(nodes.get(i).getNodeID() + "\t" + newrankscores[i] + "\t" +
                // compareoldrankscores[i]);
                error += Math.abs(newrankscores[i] - compareoldrankscores[i]);
            }

            // store score to oldrankscores
            Node ranknodes[] = new Node[nodes.size()];
            for (int i = 0; i < nodes.size(); i++) {
                compareoldrankscores[i] = newrankscores[i];
                oldrankscores[i] = newrankscores[i];
                ranknodes[i] = nodes.get(i);
            }

            this.quicksqrt(oldrankscores, ranknodes, 0, nodes.size() - 1);

            // print out the top-50
            for (int i = 1; i <= 50; i++) {
                System.out.println(i + "\t" + ranknodes[nodes.size() - i].getNodeID() + "\t"
                        + oldrankscores[nodes.size() - i]);
            }
            if (error / nodes.size() < error_bound || step == 10) {
                return ranknodes;
                // break;
            } else {
                System.out.println(step + ":\t" + error / nodes.size());
            }

            for (int i = 0; i < nodes.size(); i++)
                ranknodes[i].rank = nodes.size() - i;

            // from the lowest nodes to calculate the scores of each nodes

            // System.out.println("step:\t" + step);
            for (Node node : ranknodes)
                node.oldScore = 1;// 2013.05.02

            for (Node node : ranknodes) {

                // get all its leaders, including the second leaders
                Node[] secondleaders = new Node[30000];
                double[] ranks = new double[30000];
                Node[] media = new Node[30000];
                int count = 0;

                // the 1-leaders
                for (Node leader : node.getLeaders()) {
                    if (leader.rank > node.rank)
                        continue;
                    secondleaders[count] = leader;
                    media[count] = null;
                    ranks[count] = leader.rank;
                    count++;
                }

                // the 2-leaders
                for (Node leader : node.getLeaders()) {
                    if (leader.rank < node.rank)
                        continue;
                    for (Node secondleader : leader.getLeaders()) {
                        if (secondleader.rank > node.rank)
                            continue;
                        if (secondleader.equals(node))
                            continue;
                        secondleaders[count] = secondleader;
                        media[count] = leader;
                        ranks[count] = secondleader.rank;
                        count++;
                    }
                }

                // double rank[] = new double[count];
                // Node templeaders[] = new Node[count];

                this.quicksqrt(ranks, media, secondleaders, 0, count - 1);
                // this.quicksqrt(ranks, media, 0, count - 1);

                double remain = 1;
                double totalSend = 0;
                // double remainScore = node.oldScore;

                for (int i = 0; i < count; i++) {
                    // if (rank[i] > node.rank)
                    // break;

                    if (media[i] == null) {// direct leaders
                        // double send = p * remain * node.oldScore;//2013.5.2
                        double send = p * (node.oldScore - totalSend);
                        secondleaders[i].oldScore += send;
                        secondleaders[i].getFromFans.put(node, send);
                        // System.out.println(node.getNodeID() + "\tsend to\t" + secondleaders[i].getNodeID() + "\t" +
                        // send + "\tscores");
                        remain = remain * (1 - p);
                        totalSend += send;
                        // if(secondleaders[i].getNodeID() == 17544)
                        // System.out.println("Null \t"+node.getNodeID() + "\tsend to\t" + secondleaders[i].getNodeID()
                        // + "\t" + send + "\tscores");
                    } else {
                        // indirected leaders
                        double scoreFromMedia = 0;
                        if (node.getFromFans.containsKey(media[i])) {
                            scoreFromMedia = node.getFromFans.get(media[i]);
                        }
                        // double send = p*p*remain*(node.oldScore-scoreFromMedia);//2013.5.2
                        // double send = p * p * remain * (node.oldScore);
                        double send = p * p * (node.oldScore - totalSend - scoreFromMedia);
                        secondleaders[i].oldScore += send;
                        // System.out.println(node.getNodeID() + "\tsend to\t" + secondleaders[i].getNodeID() + "\t" +
                        // send + "\tscores");
                        // if(secondleaders[i].getNodeID() == 17544)
                        // System.out.println("Media \t" + node.getNodeID() + "\tsend to\t" +
                        // secondleaders[i].getNodeID() + "\t" + send + "\tscores");
                        secondleaders[i].getFromFans.put(node, send);
                        // remain = remain * (1 - p * p); 2013.5.2
                        totalSend += send;
                    }
                }
                // System.out.println(node.getNodeID() + "\t" + node.oldScore);
                // node.oldScore = remain * node.oldScore;
                node.oldScore = node.oldScore - totalSend;
                // System.out.println("\t--->\t" + node.oldScore);
            }
            System.out.println("next step");
            step++;
        }

        // System.out.println("Done!");
        // return null;
    }

    public void quicksqrt(double[] array, Object[] other, int left, int right) {

        if (left < right) {
            int i = left + 1;
            int j = right;
            int s = left;
            int t = right;

            while (true) {

                while (i < t && array[i] <= array[s])
                    i++;
                while (j > s && array[j] >= array[s])
                    j--;

                if (i < j) {
                    double temp = array[i];
                    array[i] = array[j];
                    array[j] = temp;
                    Object tempO = other[i];
                    other[i] = other[j];
                    other[j] = tempO;
                } else {
                    break;
                }

            }
            double temp = array[s];
            array[s] = array[j];
            array[j] = temp;
            Object tempO = other[s];
            other[s] = other[j];
            other[j] = tempO;

            quicksqrt(array, other, s, j - 1);
            quicksqrt(array, other, j + 1, t);

        }

    }

    public void quicksqrt(double[] array, Object[] other, Object[] other2, int left, int right) {

        if (left < right) {
            int i = left + 1;
            int j = right;
            int s = left;
            int t = right;

            while (true) {

                while (i < t && array[i] <= array[s])
                    i++;
                while (j > s && array[j] >= array[s])
                    j--;

                if (i < j) {
                    double temp = array[i];
                    array[i] = array[j];
                    array[j] = temp;

                    Object tempO = other[i];
                    other[i] = other[j];
                    other[j] = tempO;

                    Object tempO2 = other2[i];
                    other2[i] = other2[j];
                    other2[j] = tempO2;

                } else {
                    break;
                }

            }
            double temp = array[s];
            array[s] = array[j];
            array[j] = temp;
            Object tempO = other[s];
            other[s] = other[j];
            other[j] = tempO;

            Object tempO2 = other2[s];
            other2[s] = other2[j];
            other2[j] = tempO2;

            quicksqrt(array, other, other2, s, j - 1);
            quicksqrt(array, other, other2, j + 1, t);

        }

    }

    public Node[] calculateScoreTakeBackRankingSecondNeighbors() {
        // assign one unit score to every node
        List<Node> nodes = graph.getNodes();
        for (Node node : nodes) {
            node.rspre = 1;
            node.oldScore = 1;
        }
    
        // initial
        for (Node node : nodes) {
            // every node send score to its leaders average
            List<Node> leaders = node.getLeaders();
            double remain = Math.pow(1 - p, leaders.size());
            double send = (1 - remain) * node.oldScore / leaders.size();
            System.out.println(node.getNodeID() + "\tsend to leaders(" + leaders.size() + ")\t" + send);
            for (Node leader : leaders) {
                leader.getFromFans.put(node, send);
                leader.newRecieve += send;
                node.newsend += send;
    
            }
        }
    
        for (Node node : nodes) {
            node.oldScore = node.oldScore + node.newRecieve - node.newsend;
        }
    
        int step = 1;
        double compareoldrankscores[] = new double[nodes.size()];
        for (int i = 0; i < nodes.size(); i++)
            compareoldrankscores[i] = 1;
        double newrankscores[] = new double[nodes.size()];
        double error_bound = 0.001;
    
        while (true) {
    
            double oldrankscores[] = new double[nodes.size()];
            for (int i = 0; i < nodes.size(); i++) {
                if (step == 0)
                    newrankscores[i] = nodes.get(i).getOutDegree();
                else
                    newrankscores[i] = nodes.get(i).oldScore;
    
            }
    
            // compare
            double error = 0;
            // compare the new rankscores vs the old rankscores
            for (int i = 0; i < nodes.size(); i++) {
                System.out.println(nodes.get(i).getNodeID() + "\t" + newrankscores[i] + "\t" + compareoldrankscores[i]);
                error += Math.abs(newrankscores[i] - compareoldrankscores[i]);
            }
    
            // store score to oldrankscores
            Node ranknodes[] = new Node[nodes.size()];
            for (int i = 0; i < nodes.size(); i++) {
                compareoldrankscores[i] = newrankscores[i];
                oldrankscores[i] = newrankscores[i];
                ranknodes[i] = nodes.get(i);
            }
    
            this.quicksqrt(oldrankscores, ranknodes, 0, nodes.size() - 1);
    
            if (error / nodes.size() < error_bound || step == 100) {
                return ranknodes;
                // break;
            } else
                System.out.println(error / nodes.size());
    
            for (int i = 0; i < nodes.size(); i++)
                ranknodes[i].rank = nodes.size() - i;
    
            // from the lowest nodes to calculate the scores of each nodes
    
            System.out.println("step:\t" + step);
            for (Node node : ranknodes)
                node.oldScore = 1;
    
            for (Node node : ranknodes) {
    
                // get all its leaders, including the second leaders
                Node[] secondleaders = new Node[1000];
                double[] ranks = new double[1000];
                Node[] media = new Node[1000];
                int count = 0;
    
                // the 1-leaders
                for (Node leader : node.getLeaders()) {
                    if (leader.rank > node.rank)
                        continue;
                    secondleaders[count] = leader;
                    media[count] = null;
                    ranks[count] = leader.rank;
                    count++;
                }
    
                // the 2-leaders
                for (Node leader : node.getLeaders()) {
                    if (leader.rank < node.rank)
                        continue;
                    for (Node secondleader : leader.getLeaders()) {
                        if (secondleader.rank > node.rank)
                            continue;
                        if (secondleader.equals(node))
                            continue;
                        secondleaders[count] = secondleader;
                        media[count] = leader;
                        ranks[count] = secondleader.rank;
                        count++;
                    }
                }
    
                // double rank[] = new double[count];
                // Node templeaders[] = new Node[count];
    
                this.quicksqrt(ranks, secondleaders, media, 0, count - 1);
                // this.quicksqrt(ranks, media, 0, count - 1);
    
                double remain = 1;
                double totalSend = 0;
                // double remainScore = node.oldScore;
                for (int i = 0; i < count; i++) {
                    // if (rank[i] > node.rank)
                    // break;
                    if (media[i] == null) {// directed leaders
                        double send = p * remain * node.oldScore;
                        secondleaders[i].oldScore += send;
                        secondleaders[i].getFromFans.put(node, send);
                        System.out.println(node.getNodeID() + "\tsend to\t" + secondleaders[i].getNodeID() + "\t"
                                + send + "\tscores");
                        remain = remain * (1 - p);
                        totalSend += send;
                    } else {
                        // indirected leaders
                        double scoreFromMedia = 0;
                        if (secondleaders[i].getFromFans.containsKey(media[i])) {
                            scoreFromMedia = secondleaders[i].getFromFans.get(media[i]);
                        }
                        // double send = p*p*remain*(node.oldScore-scoreFromMedia);
                        double send = p * p * remain * (node.oldScore);
                        System.out.println(node.getNodeID() + "\tsend to\t" + secondleaders[i].getNodeID() + "\t"
                                + send + "\tscores");
                        secondleaders[i].getFromFans.put(node, send);
                        remain = remain * (1 - p * p);
                        totalSend += send;
                    }
                }
                System.out.print(node.getNodeID() + "\t" + node.oldScore);
                // node.oldScore = remain * node.oldScore;
                node.oldScore = node.oldScore - totalSend;
                System.out.println("\t--->\t" + node.oldScore);
            }
            System.out.println("next step");
            step++;
        }
    
        // System.out.println("Done!");
        // return null;
    }

    public void quicksqrt(double[] array, double[] other, int left, int right) {

        if (left < right) {
            int i = left + 1;
            int j = right;
            int s = left;
            int t = right;

            while (true) {

                while (i < t && array[i] <= array[s])
                    i++;
                while (j > s && array[j] >= array[s])
                    j--;

                if (i < j) {
                    double temp = array[i];
                    array[i] = array[j];
                    array[j] = temp;
                    double tempO = other[i];
                    other[i] = other[j];
                    other[j] = tempO;
                } else {
                    break;
                }

            }
            double temp = array[s];
            array[s] = array[j];
            array[j] = temp;
            double tempO = other[s];
            other[s] = other[j];
            other[j] = tempO;

            quicksqrt(array, other, s, j - 1);
            quicksqrt(array, other, j + 1, t);

        }

    }

    public void savetoFile(String rsfile) {
        List<Node> nodes = graph.getNodes();
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(rsfile)));
            for (Node node : nodes) {
                out.println(node.getNodeID() + "\t" + node.rspre);
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
