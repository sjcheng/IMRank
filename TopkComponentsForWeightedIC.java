import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TopkComponentsForWeightedIC {

    private boolean enableDAG = false;

    private boolean enableSaveToFile = false;

    Map<String, MultiEdgesDirectedGraph> graphs = new HashMap<String, MultiEdgesDirectedGraph>();

    // use file to store the snapshot

    /**
     * @param args
     */
    public static void main(String[] args) {
        // graph file
        String filename = "E:/Experiments/IM/networks/Hep.txt";

        TopkComponentsForWeightedIC topk = new TopkComponentsForWeightedIC();
//        topk.extractSeedsByTopk(filename, 50, 50);
//        topk.extractSeedsByTopk(filename, 50, 100);
//        topk.extractSeedsByTopk(filename, 50, 200);
        topk.extractSeedsByTopk(filename, 50, 250);
        topk.extractSeedsByTopk(filename, 50, 400);
        topk.extractSeedsByTopk(filename, 50, 500);
        topk.extractSeedsByTopk(filename, 50, 600);
    }

    /**
     * 
     * @param filename
     *        graph file
     * @param k
     *        the number of required seeds
     * @param snapshot_number
     *        number of snapshots
     */
    public void extractSeedsByTopk(String filename, int k, int snapshot_number) {

        MultiEdgesDirectedGraph g = MultiEdgesDirectedGraph.ConstructFromUndirectedFile(filename);
        List<Node> seeds = new ArrayList<Node>();
        int[] scores = new int[g.nodes.size()];

        // generate snapshot and caculate the rank and value
        int[][] records = new int[snapshot_number][g.nodes.size()];

        for (int i = 0; i < snapshot_number; i++) {
            System.out.println("start calculate " + i + " snapshot....");
            long start = System.currentTimeMillis();
            MultiEdgesDirectedGraph gtemp = MultiEdgesDirectedGraph.ConstructFromUndirectedFile(filename);
            deleteGraph(gtemp);
            calculateReachability(gtemp, scores, records[i]);
            long end = System.currentTimeMillis();
            System.out.println("finish snapshot " + i + " . Cost " + (end - start) + " ms");
            if (enableSaveToFile) {
                gtemp.saveDirectedGraphToFile(filename.replace(".txt", "_snapshot_WC_" + i + ".txt"));
            } else {
                graphs.put(filename.replace(".txt", "_snapshot_WC_" + i + ".txt"), gtemp);
            }
        }

        System.out.println("===================================");

        // record the removed node
        ArrayList<ArrayList<Integer>> removedNodes = new ArrayList<ArrayList<Integer>>();

        for (int i = 0; i < snapshot_number; i++) {
            removedNodes.add(new ArrayList<Integer>());
        }

        int totalScore = 0;

        while (seeds.size() < k) {
            // select one node
            int maxID = -1;
            double maxScore = -1;
            for (int i = 0; i < scores.length; i++) {
                if (scores[i] > maxScore) {
                    maxScore = scores[i];
                    maxID = i;
                }
            }

            seeds.add(g.getNodeByID(maxID));
            totalScore += scores[maxID];
            //System.out.println("select seed " + seeds.size() + " " + maxID);
            if (seeds.size() == k)
                break;
            // update node scores
            for (int i = 0; i < snapshot_number; i++) {
                String snapshot_graphname = filename.replace(".txt", "_snapshot_WC_" + i + ".txt");
                if (removedNodes.get(i).contains(maxID)) {
                    continue;
                }
                long start = System.currentTimeMillis();


                updateComponent(snapshot_graphname, maxID, scores, removedNodes.get(i), records[i]);
                if (records[i][maxID] != 0) {
                    System.out.println("======wrong, it should be 0======");
                    //records[i][maxID] = 0;
                }
                long end = System.currentTimeMillis();
                //System.out.println("updateComponent cost: " + (end - start) + " ms");
//                System.out.print("after  :");
//                print(records[i]);
            }
//            System.out.println("final scores :");
//            print(scores);
            if (scores[maxID] != 0) {
//                System.out.println("======force it to be 0======");
                System.out.println("wrong"  +scores[maxID]);
//                scores[maxID] = 0;  
            }
        }

        System.out.println("Average Size: " + totalScore / snapshot_number);
        System.out.print("Seeds:\t");
        for (Node seed : seeds) {
            System.out.print(seed.getNodeID() + "\t");
        }
        System.out.println();

        calculalteSeedsInfluence(g, seeds);
    }

    public void calculalteSeedsInfluence(MultiEdgesDirectedGraph g, List<Node> seeds) {
        SeedsInfluenceSpread spread = new SeedsInfluenceSpread(g);
        spread.getWCInfluenceSpread(seeds);
    }

    /**
     * BFS
     * 
     * @param tempg
     * @param seedid
     * @param isReachable
     * @return
     */
    public List<Node> getReachableOrUpstreamNodes(List<Node> seeds, boolean isReachable) {
        // if (seed == null)
        // return null;

        List<Node> resultNodes = new ArrayList<Node>();
        List<Node> previsit = new ArrayList<Node>();
        List<Node> temp = new ArrayList<Node>();

        previsit.addAll(seeds);

        while (previsit.size() > 0) {
            for (Node node : previsit) {
                if (isReachable) {
                    for (Node fan : node.fans) {
                        if (resultNodes.contains(fan) || temp.contains(fan) || previsit.contains(fan))
                            continue;
                        temp.add(fan);
                    }
                } else {
                    for (Node leader : node.leaders) {
                        if (resultNodes.contains(leader) || temp.contains(leader) || previsit.contains(leader))
                            continue;
                        temp.add(leader);
                    }
                }
            }
            for (Node node : previsit)
                resultNodes.add(node);
            previsit = new ArrayList<Node>(temp);
            temp.clear();
        }
        return resultNodes;
    }

    public void updateComponent(String filename, int seedid, int[] scores, List<Integer> removedNodes,
            int[] snapshot_record) {
        MultiEdgesDirectedGraph tempg;
        if (enableSaveToFile) {
            tempg = MultiEdgesDirectedGraph.ConstructFromDirectedFile(filename);
        } else {
            tempg = graphs.get(filename);
        }
        // find all the nodes whose score should be changed
        if (tempg.getNodeByID(seedid) == null) {
            scores[seedid]--;
            snapshot_record[seedid]--;
            // System.out.println("because " + snapshot_record[seedid]);
            return;
        }

        List<Node> seeds = new ArrayList<Node>();
        seeds.add(tempg.getNodeByID(seedid));
        List<Node> reachableNodes = getReachableOrUpstreamNodes(seeds, true);
        List<Node> upstreamNodes = getReachableOrUpstreamNodes(seeds, false);

        int DAG_reach = reachableNodes.size();

        List<Node> DAG = getDAG(reachableNodes, upstreamNodes);

        for (Node node : reachableNodes) {
            removedNodes.add(node.id);
            tempg.removeNode(node);
        }

        List<Node> otherupstreamnodes = getReachableOrUpstreamNodes(reachableNodes, false);
        
        reachableNodes.removeAll(DAG);
        otherupstreamnodes.removeAll(reachableNodes);
        otherupstreamnodes.removeAll(upstreamNodes);
        //otherupstreamnodes.remove(DAG);
        
//        System.out.print("update reachableNodes: ");
//        print(snapshot_record);
        
        for (Node node : reachableNodes) {
            scores[node.id] -= snapshot_record[node.id];
            snapshot_record[node.id] = 0;
        }

//        System.out.print("update reachableNodes: ");
//        print(snapshot_record);
//        
        // collect other upstreamnodes need to update

        
        updateOtherUpstreamNodes(otherupstreamnodes, scores, snapshot_record);

//        System.out.print("update other upstreamNodes: ");
//        print(snapshot_record);
        // delete reachable nodes and update upstreamnodes
        for (Node node : upstreamNodes) {
            scores[node.id] -= DAG_reach;
            snapshot_record[node.id] -= DAG_reach;
        }

//        System.out.print("update  upstreamNodes: ");
//        print(snapshot_record);
//        
        // for (Node node : DAG) {
        // removedNodes.add(node.id);
        // tempg.removeNode(node);
        // }

        if (enableSaveToFile) {
            tempg.saveDirectedGraphToFile(filename);
        } else {
            graphs.put(filename, tempg);
        }
        // System.out.println("Save updated Graph Cost : " + (end - start));
    }

    /**
     * Delete the edges whose related propagtion small than p
     * 
     * @param g
     */
    public void deleteGraph(MultiEdgesDirectedGraph g) {
        // initial
        for (Node node : g.getNodes()) {
            node.nodep = 1.0 / (double) node.getFans().size();
        }

        List<Node> removeFans = null;
        for (Node node : g.getNodes()) {
            removeFans = new ArrayList<Node>();
            for (Node fan : node.getFans()) {
                if (Math.random() > fan.nodep) {
                    removeFans.add(fan);
                }
            }
            for (Node removeFan : removeFans) {
                node.fans.remove(removeFan);
                removeFan.leaders.remove(node);
            }
        }
    }

    public void updateReachableNodes(List<Node> restnodes, int[] scores) {
        while (restnodes.size() > 0) {
            List<Node> seeds = new ArrayList<Node>();
            seeds.add(restnodes.get(0));
            List<Node> reachableNodes = getReachableOrUpstreamNodes(seeds, true);
            if (enableDAG) {
                List<Node> upstreamNodes = getReachableOrUpstreamNodes(seeds, false);
                List<Node> DAG = getDAG(reachableNodes, upstreamNodes);
                for (Node dag : DAG) {
                    scores[dag.getNodeID()] -= reachableNodes.size();
                    restnodes.remove(dag);
                }
            } else {
                scores[restnodes.get(0).id] -= reachableNodes.size();
                restnodes.remove(0);
            }
        }
    }

    public void updateOtherUpstreamNodes(List<Node> restnodes, int[] scores, int[] snapshot_record) {
        while (restnodes.size() > 0) {
            List<Node> seeds = new ArrayList<Node>();
            seeds.add(restnodes.get(0));
            List<Node> reachableNodes = getReachableOrUpstreamNodes(seeds, true);
            if (enableDAG) {
                List<Node> upstreamNodes = getReachableOrUpstreamNodes(seeds, false);
                List<Node> DAG = getDAG(reachableNodes, upstreamNodes);
                for (Node dag : DAG) {
                    scores[dag.getNodeID()] -= reachableNodes.size();
                    scores[dag.getNodeID()] -= (snapshot_record[dag.getNodeID()] - reachableNodes.size());
                    snapshot_record[dag.getNodeID()] = reachableNodes.size();
                    restnodes.remove(dag);
                }
            } else {
                scores[restnodes.get(0).id] -= (snapshot_record[restnodes.get(0).id] - reachableNodes.size());
                snapshot_record[restnodes.get(0).id] = reachableNodes.size();
                restnodes.remove(0);
            }
        }
    }

    public List<Node> getDAG(List<Node> reachableNodes, List<Node> upstreamNodes) {
        List<Node> DAG = new ArrayList<Node>();
        for (Node fan : reachableNodes) {
            if (upstreamNodes.contains(fan))
                DAG.add(fan);
        }
        return DAG;
    }

    /**
     * ca
     * 
     * @param afterg
     * @param scores
     */
    public void calculateReachability(MultiEdgesDirectedGraph afterg, int[] scores, int[] snapshot_record) {
        // stat
        List<Node> restnodes = new ArrayList<Node>(afterg.nodes);

        while (restnodes.size() > 0) {
            List<Node> seeds = new ArrayList<Node>();
            seeds.add(restnodes.get(0));
            List<Node> reachableNodes = getReachableOrUpstreamNodes(seeds, true);
            if (enableDAG) {
                List<Node> upstreamNodes = getReachableOrUpstreamNodes(seeds, false);
                List<Node> DAG = getDAG(reachableNodes, upstreamNodes);
                for (Node dag : DAG) {
                    scores[dag.id] += reachableNodes.size();
                    snapshot_record[dag.id] = reachableNodes.size();
                    restnodes.remove(dag);
                }
            } else {
                scores[restnodes.get(0).id] += reachableNodes.size();
                snapshot_record[restnodes.get(0).id] = reachableNodes.size();
                restnodes.remove(0);
            }
        }
    }

    public static void print(int[] array) {
        System.out.println(Arrays.toString(array));
    }
}
