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

public class IMRank {

    DirectedGraph graph;

    double p;

    Hashtable IDToLRHashtable = new Hashtable();

    public static void main(String[] args) {
        
        double e = Math.pow(Math.E, -1.61);
        
        String gfile = "E:/Experiments/IMRank/karate_edgelist_1.txt";
        String rsfile = "E:/Experiments/IMRank/karate_edgelist_1_IMRank2.txt";
        System.out.println("begin to read in graph");
        double p = 0.05;
        IMRank imr = new IMRank(gfile, p, false);
        System.out.println("begin to caculate score");  
        imr.calculateScore();
         //imr.calculateScoreTakeBackEasyRanking();
        // imr.calculateScoreTakeBackByRanking();
        //imr.calculateScoreTakeBackRanking();
        Node[] ranks = imr.calculateScoreTakeBackRankingSecondNeighbors();
        imr.getInfluence(ranks, ranks.length-1, gfile, p, 20000);
        System.out.println("begin to save score");
        imr.savetoFile(rsfile);
    }

    public void getInfluence(Node[] ranks, int k, String gfile, double p, int R){
        //double p = 0.3;
        //String gfile = "E:/Experiments/IMRank/karate_edgelist_1.txt";
        double influence[] = new double[ranks.length+1];
        influence[0] = 0;
        MultiEdgesDirectedGraph g = MultiEdgesDirectedGraph.ConstructFromUndirectedFile(gfile);
        SeedsInfluenceSpread spread = new SeedsInfluenceSpread(g);

       // TopkComponentsForUniformIC topk = new TopkComponentsForUniformIC();
       // double[] temp = topk.extractSeedsSG(gfile, k, p, R);

        List<Node> newseeds = new ArrayList<Node>();
        for (int j = 1; j < k+1; j++) {
            Node seed = g.getNodeByID(ranks[ranks.length-j].getNodeID());
            newseeds.add(seed);
            Map rs = spread.getInfluenceSpread(newseeds, p);
            double influ = Double.parseDouble(rs.get("AVE_SPREAD_SIZE").toString());
            double var = Double.parseDouble(rs.get("VAR").toString());
            influence[j] = influ;
            System.out.println(j + "\t" + seed.getNodeID() + "\t" + seed.getOutDegree() + "\t"
                    + influence[j] + "\t" + var + "\t" + (influence[j] - influence[j - 1]));

        }
    }
    
    public IMRank(String gfile, double p, boolean directed) {
        if (directed)
            graph = DirectedGraph.ConstructFromFile(gfile);
        else
            graph = DirectedGraph.ConstructFromUndirectedFile(gfile);
        this.p = p;
    }

    public IMRank(DirectedGraph graph, double p) {
        this.graph = graph;
        this.p = p;
    }

    /**
     * in this calculateScore, the take back mechanism is not used
     * 
     */
    public void calculateScore() {
        // assign one unit score to every node
        List<Node> nodes = graph.getNodes();
        for (Node node : nodes) {
            node.rspre = 1;
        }

        // diffusion to stable state
        double error = 10000;
        double error_threshold = 0.01;
        int step = 1;
        while (error > error_threshold) {
            System.out.println("step:\t" + step);
            for (Node node : nodes) {
                
                int leadersNum = node.getLeaders().size();
                double remain = Math.pow(1 - p, leadersNum);
                List<Node> leaders = node.getLeaders();

                double partsend = (1 - remain) / leadersNum;
                for (Node leader : leaders) {
                    if (leader.rspre < node.rspre) {
                        remain += partsend;
                    } else {
                        leader.rsafter += partsend * node.rspre;
                    }
                }

                node.rsafter += remain * node.rspre;
            }

            // caculate error
            error = 0;
            for (Node node : nodes) {
                // node.rsafter = node.rsafter + p + other;
                error = error + Math.abs(node.rsafter - node.rspre);
            }

            error = error / (double) graph.getGraphSize();

            for (Node node : nodes) {
                node.rspre = node.rsafter;
                node.rsafter = 0;
                System.out.println(node.getNodeID() + "\t" + node.rspre);
            }

            step++;
        }

        System.out.println("Done!");
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
            
            if (error / nodes.size() < error_bound || step==100){
                return ranknodes;
                //break;
            }
            else
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
                //this.quicksqrt(ranks, media, 0, count - 1);

                double remain = 1;
                double totalSend = 0;
                //double remainScore = node.oldScore;
                for (int i = 0; i < count; i++) {
                    // if (rank[i] > node.rank)
                    // break;
                    if (media[i] == null) {//directed leaders
                        double send = p * remain * node.oldScore;
                        secondleaders[i].oldScore += send;
                        secondleaders[i].getFromFans.put(node, send);
                        System.out.println(node.getNodeID() + "\tsend to\t" + secondleaders[i].getNodeID() + "\t" + send + "\tscores");
                        remain = remain * (1 - p);
                        totalSend += send;
                    }else{
                        //indirected leaders
                        double scoreFromMedia = 0;
                        if(secondleaders[i].getFromFans.containsKey(media[i]))
                        {             
                            scoreFromMedia = secondleaders[i].getFromFans.get(media[i]);
                        }
                        //double send = p*p*remain*(node.oldScore-scoreFromMedia);
                        double send = p*p*remain*(node.oldScore);
                        System.out.println(node.getNodeID() + "\tsend to\t" + secondleaders[i].getNodeID() + "\t" + send + "\tscores");
                        secondleaders[i].getFromFans.put(node, send);
                        remain = remain*(1-p*p);
                        totalSend += send;
                    }
                }
                System.out.print(node.getNodeID() + "\t" + node.oldScore);
                //node.oldScore = remain * node.oldScore;
                node.oldScore = node.oldScore - totalSend;
                System.out.println("\t--->\t" + node.oldScore);
            }
            System.out.println("next step");
            step++;
        }

        //System.out.println("Done!");
        //return null;
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

            Object tempO2 = other2[i];
            other2[i] = other2[j];
            other2[j] = tempO2;
            
            quicksqrt(array, other, other2, s, j - 1);
            quicksqrt(array, other, other2,  j + 1, t);

        }

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
