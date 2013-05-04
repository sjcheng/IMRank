import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class TopkComponentsForUniformIC {

    // use file to store the snapshot

    private boolean enableSaveToFile = false;

    Map<String, Snapshot> graphs = new HashMap<String, Snapshot>();

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

        String filename = "E:/Experiments/IM/networks/Hep.txt";
        TopkComponentsForUniformIC topk = new TopkComponentsForUniformIC();
       
//        for(int i=0; i<20; i++)
//           topk.extractSeedsByFullInfo(filename, 50, 0.01, 100);
        double p =0.01;
//        for(int i=0; i<5; i++)
//            topk.extractSeedsByFullInfo(filename, 50, p, 50);
//        System.out.println();
//        for(int i=0; i<5; i++)
//            topk.extractSeedsByFullInfo(filename, 50, p, 100);
//        System.out.println();
//        for(int i=0; i<5; i++)
//            topk.extractSeedsByFullInfo(filename, 50, p, 200);
//        System.out.println();
//        for(int i=0; i<5; i++)
//            topk.extractSeedsByFullInfo(filename, 50, p, 300);
//        System.out.println();
        for(int i=0; i<5; i++)
            topk.extractSeedsByFullInfo(filename, 50, p, 200);
        System.out.println();
//        for(int i=0; i<5; i++)
//            topk.extractSeedsByFullInfo(filename, 50, 0.004, 500);
        
    }

    public void extractSeedsByFullInfo(String filename, int k, double p, int snapshot_number) {

        MultiEdgesDirectedGraph g = MultiEdgesDirectedGraph.ConstructFromUndirectedFile(filename);

        List<Node> seeds = new ArrayList<Node>();
        
        int[] scores;
        if(g.nodes.size() > 100)
         {
             scores = new int[g.nodes.size()];
          }else{
              scores = new int[300];
          }

        // readin snapshot and caculate the rank and value
        for (int i = 0; i < snapshot_number; i++) {
            // readin snapshot graph
            MultiEdgesDirectedGraph gtemp = null;
            // if (new File(filename.replace(".txt", "_snapshot_UIC_" + p +"_"+ i + ".txt")).exists()) {
            // //System.out.println("exist " + filename.replace(".txt", "_snapshot_UIC_" + i + ".txt"));
            // gtemp = MultiEdgesDirectedGraph.ConstructFromUndirectedFile(filename.replace(".txt", "_snapshot_UIC_" + p
            // +"_" + i + ".txt"));
            // } else {
            gtemp = MultiEdgesDirectedGraph.ConstructFromUndirectedFile(filename);
            deleteGraph(gtemp, p);
            // gtemp.saveDirectedGraphToFile(filename.replace(".txt", "_snapshot_UIC_" + p +"_" + i + ".txt"));
            // }

            if (enableSaveToFile) {
                caculateComponentsSize(gtemp, filename.replace(".txt", "_snapshot_UIC_" + p + "_" + i + "_comp.txt"));
            } else {
                int com_k = caculateComponents(gtemp);
                // System.out.println("generate snapshot: -----------"+ com_k);
                Snapshot snapshot = new Snapshot(com_k, gtemp);
                graphs.put(filename.replace(".txt", "_snapshot_UIC_" + p + "_" + i + "_comp.txt"), snapshot);

                // snapshot.print();
                // System.out.println("put graphs: "+ );
            }
            // add snapshot_componentname and count node value
            addComponent(filename.replace(".txt", "_snapshot_UIC_" + p + "_" + i + "_comp.txt"), scores);
            gtemp = null;
        }

        // System.out.println();
        // System.out.println("origin: ");
        // print(scores);
        double totalScore = 0;
        while (seeds.size() < k) {
            // select one node
            int maxID = -1;
            double maxScore = -1;

            for (int i = 0; i < scores.length; i++)
                if (scores[i] > maxScore) {
                    maxScore = scores[i];
                    maxID = i;
                }
            // maxID = candidates.get(maxID).id;
            seeds.add(g.getNodeByID(maxID));
            totalScore = totalScore + maxScore;
            // update node scores

            for (int i = 0; i < snapshot_number; i++) {
                updateComponent(filename.replace(".txt", "_snapshot_UIC_" + p + "_" + i + "_comp.txt"), maxID, seeds,
                        scores);
            }

        }

        for (Node seed : seeds) {
            System.out.print(seed.getNodeID()+" ");
        }
        System.out.println();
        SeedsInfluenceSpread spread = new SeedsInfluenceSpread(g);
        System.out.println(totalScore / snapshot_number);
        for(int i=50; i<k+1; i=i+50)
        {
            List<Node> newseeds = new ArrayList<Node>();
            for(int j=0; j<i; j++)
                newseeds.add(seeds.get(j));
            Map rs = spread.getInfluenceSpread(newseeds, p);
            System.out.println(snapshot_number+"\t"+newseeds.size()+"\t"+rs.get("AVE_SPREAD_SIZE")+"\t"+rs.get("VAR"));
        }
        
        g = null;
    }

    
    public double[] extractSeedsSG(String filename, int k, double p, int R) {
        double[] seedsInfluence = new double[k];
        MultiEdgesDirectedGraph g = MultiEdgesDirectedGraph.ConstructFromUndirectedFile(filename);

        List<Node> seeds = new ArrayList<Node>();
        int[] scores;
        if(g.nodes.size() > 100)
        {
            scores = new int[g.nodes.size()];
         }else{
             scores = new int[300];
         }

        // readin snapshot and caculate the rank and value
        for (int i = 0; i < R; i++) {
            // readin snapshot graph
            MultiEdgesDirectedGraph gtemp = null;
            gtemp = MultiEdgesDirectedGraph.ConstructFromUndirectedFile(filename);
            deleteGraph(gtemp, p);

            if (enableSaveToFile) {
                caculateComponentsSize(gtemp, filename.replace(".txt", "_snapshot_UIC_" + p + "_" + i + "_comp.txt"));
            } else {
                int com_k = caculateComponents(gtemp);
                // System.out.println("generate snapshot: -----------"+ com_k);
                Snapshot snapshot = new Snapshot(com_k, gtemp);
                graphs.put(filename.replace(".txt", "_snapshot_UIC_" + p + "_" + i + "_comp.txt"), snapshot);

                // snapshot.print();
                // System.out.println("put graphs: "+ );
            }
            // add snapshot_componentname and count node value
            addComponent(filename.replace(".txt", "_snapshot_UIC_" + p + "_" + i + "_comp.txt"), scores);
            gtemp = null;
        }

        // System.out.println();
        // System.out.println("origin: ");
        // print(scores);
        double totalScore = 0;
        while (seeds.size() < k) {
            // select one node
            int maxID = -1;
            double maxScore = -1;

            for (int i = 0; i < scores.length; i++)
                if (scores[i] > maxScore) {
                    maxScore = scores[i];
                    maxID = i;
                }
            // maxID = candidates.get(maxID).id;
            seeds.add(g.getNodeByID(maxID));
            seedsInfluence[seeds.size()-1] = maxID;
            totalScore = totalScore + maxScore;
            // update node scores

            for (int i = 0; i < R; i++) {
                updateComponent(filename.replace(".txt", "_snapshot_UIC_" + p + "_" + i + "_comp.txt"), maxID, seeds,
                        scores);
            }

        }

        /*for (Node seed : seeds) {
            System.out.print(seed.getNodeID()+" ");
        }*/
        //System.out.println();
        
        
        /*for(int i=50; i<k+1; i=i+50)
        {
            List<Node> newseeds = new ArrayList<Node>();
            for(int j=0; j<i; j++)
                newseeds.add(seeds.get(j));
            Map rs = spread.getInfluenceSpread(newseeds, p);
            System.out.println(R+"\t"+newseeds.size()+"\t"+rs.get("AVE_SPREAD_SIZE")+"\t"+rs.get("VAR"));
        }*/
        
        g = null;
        return seedsInfluence;
    }
    
    
    public static void print(int[] array) {
        for (int i : array) {
            System.out.print(i + " ");
        }
        System.out.println();
    }

    public void addComponent(String filename, int[] scores) {
        if (enableSaveToFile) {
            File file = new File(filename);
            boolean[] flag = new boolean[scores.length];
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                String line;
                List<Integer> ids = null;

                while ((line = br.readLine()) != null) {
                    if (line.trim().length() == 0) {
                        System.out.println("line length is zero");
                        continue;
                    }

                    ids = new ArrayList<Integer>();

                    StringTokenizer st = new StringTokenizer(line);
                    while (st.hasMoreTokens()) {
                        try {
                            int id = Integer.parseInt(st.nextToken());
                            ids.add(id);
                        } catch (Exception e) {
                            System.out.println("exp: " + e);
                        }
                    }

                    for (int id : ids) {
                        scores[id] = scores[id] + ids.size();
                        flag[id] = true;
                    }
                }
                br.close();

                // only itself
                for (int i = 0; i < scores.length; i++) {
                    if (!flag[i])
                        scores[i] = scores[i] + 1;
                }
            } catch (IOException exp) {
                System.out.println(exp);
            }
        } else {
            Snapshot snapshot = graphs.get(filename);
            // System.out.println(snapshot + " to get "+ filename);
            for (ArrayList<Integer> temp : snapshot.getSnapshot()) {
                for (int id : temp) {
                    scores[id] += temp.size();
                }
            }
        }
    }

    public void updateComponent(String filename, int seedid, List<Node> seeds, int[] scores) {
        if (enableSaveToFile) {
            File file = new File(filename);
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                String line;
                List<Integer> tempComp = null;
                int temp_id = -1;
                boolean flag = false;

                while ((line = br.readLine()) != null) {
                    if (line.trim().length() == 0) {
                        System.out.println("line length is zero");
                        continue;
                    }
                    tempComp = new ArrayList<Integer>();
                    flag = false;

                    StringTokenizer st = new StringTokenizer(line);
                    while (st.hasMoreTokens()) {
                        try {
                            temp_id = Integer.parseInt(st.nextToken());
                            tempComp.add(temp_id);
                            if (temp_id == seedid)
                                flag = true;
                        } catch (Exception exp) {
                            System.out.println("exp: " + line);
                            continue;
                        }
                    }
                    if (flag) {
                        // check seeds
                        boolean secondFlag = false;
                        for (int id : tempComp) {
                            if (id == seedid)
                                continue;
                            for (Node node : seeds) {
                                if (node.id == id) {
                                    secondFlag = true;
                                    break;
                                }
                            }
                        }

                        if (secondFlag) {
                            // System.out.println("Skip!");
                            break;
                        }
                        for (int id : tempComp) {
                            // System.out.print(id + "\t" + scores[id]+"--> ");
                            scores[id] = scores[id] - tempComp.size();
                            // System.out.println(scores[id]);
                            if (scores[id] < 0)
                                System.out.println("wrong");
                        }
                        break;
                    }
                }

                if (!flag) {
                    // System.out.print(seedid + "\t" + scores[seedid]+"--> ");
                    scores[seedid] = scores[seedid] - 1;
                    // System.out.println(scores[seedid]);
                }
                br.close();
            } catch (IOException exp) {
                System.out.println(exp);
            }
        } else {
            Snapshot snapshot = graphs.get(filename);
            for (ArrayList<Integer> temp : snapshot.getSnapshot()) {
                boolean flag = false;
                for (int id : temp) {
                    if (id == seedid) {
                        flag = true;
                    }
                }
                if (flag) {
                    // check wehter this component is update
                    boolean secondFlag = true;
                    for (Node node : seeds) {
                        if (node.id == seedid)
                            continue;
                        if (temp.contains(node.id)) {
                            secondFlag = false;
                            break;
                        }
                    }

                    if (secondFlag) {
                        // update scores
                        for (int id : temp) {
                            scores[id] = scores[id] - temp.size();
                        }
                    }
                    break;
                }
            }

        }
    }

    public void deleteGraph(MultiEdgesDirectedGraph g, double p) {
        // delete the edges whose related propagtion small than p
        List<Node> removeFans = null;
        for (Node node : g.getNodes()) {
            removeFans = new ArrayList<Node>();
            try {
                for (Node fan : node.getFans()) {
                    if (node.getNodeID() > fan.getNodeID())
                        continue;
                    if (Math.random() > p) {
                        removeFans.add(fan);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            for (Node removeFan : removeFans) {
                // System.out.println("remove edge: "+
                // node.id+"\t"+removeFan.id);
                node.fans.remove(removeFan);
                node.leaders.remove(removeFan);
                removeFan.leaders.remove(node);
                removeFan.fans.remove(node);
            }
        }
    }

    public void caculateComponentsSize(MultiEdgesDirectedGraph g) {
        // int maxMapID = 0;
        int[] sizeofcomponent = new int[g.getGraphSize() + 1];

        // initial
        for (Node node : g.nodes) {
            node.componentID = -1;
        }

        // stat
        List<Node> restnodes = new ArrayList<Node>(g.nodes);

        int componentID = 0;
        while (restnodes.size() > 0) {
            componentID++;

            // BFS
            List<Node> onecomponent = new ArrayList<Node>();
            List<Node> previsit = new ArrayList<Node>();
            List<Node> temp = new ArrayList<Node>();
            previsit.add(restnodes.get(0));
            while (previsit.size() > 0) {
                for (Node node : previsit) {
                    for (Node fan : node.fans) {
                        if (onecomponent.contains(fan) || temp.contains(fan) || previsit.contains(fan))
                            continue;
                        temp.add(fan);
                    }
                }
                for (Node node : previsit)
                    onecomponent.add(node);
                previsit = new ArrayList<Node>(temp);
                temp.clear();
            }

            for (Node node : onecomponent) {
                node.componentID = componentID;
                restnodes.remove(node);
                node.pre_component_size = onecomponent.size();
            }

            sizeofcomponent[componentID] = onecomponent.size();
        }
    }

    // string file
    public void caculateComponentsSize(MultiEdgesDirectedGraph afterg, String outfile) {

        // int[] sizeofcomponent = new int[orig.getGraphSize() + 1];

        // initial
        for (Node node : afterg.nodes) {
            node.componentID = -1;
        }

        // stat
        List<Node> restnodes = new ArrayList<Node>(afterg.nodes);

        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outfile)));
            int componentID = 0;
            while (restnodes.size() > 0) {
                componentID++;

                // BFS
                List<Node> onecomponent = new ArrayList<Node>();
                List<Node> previsit = new ArrayList<Node>();
                List<Node> temp = new ArrayList<Node>();
                previsit.add(restnodes.get(0));
                while (previsit.size() > 0) {
                    for (Node node : previsit) {
                        for (Node fan : node.fans) {
                            if (onecomponent.contains(fan) || temp.contains(fan) || previsit.contains(fan))
                                continue;
                            temp.add(fan);
                        }
                    }
                    for (Node node : previsit)
                        onecomponent.add(node);
                    previsit = new ArrayList<Node>(temp);
                    temp.clear();
                }

                // out.println("# " + onecomponent.size());
                // one line is a component
                if (onecomponent.size() == 1) {
                    // only itself
                    for (Node node : onecomponent) {
                        node.componentID = componentID;
                        restnodes.remove(node);
                        node.pre_component_size = onecomponent.size();
                    }
                } else {
                    for (Node node : onecomponent) {
                        node.componentID = componentID;
                        restnodes.remove(node);
                        node.pre_component_size = onecomponent.size();
                        out.print(node.id + "\t");
                    }
                    out.println();
                }

                // sizeofcomponent[componentID] = onecomponent.size();
            }

            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int caculateComponents(MultiEdgesDirectedGraph g) {

        int[] sizeofcomponent = new int[g.getGraphSize() + 1];

        // initial
        for (Node node : g.nodes) {
            node.componentID = -1;
        }

        // stat
        List<Node> restnodes = new ArrayList<Node>(g.nodes);

        int componentID = 0;
        while (restnodes.size() > 0) {
            componentID++;

            // BFS
            List<Node> onecomponent = new ArrayList<Node>();
            List<Node> previsit = new ArrayList<Node>();
            List<Node> temp = new ArrayList<Node>();
            previsit.add(restnodes.get(0));
            while (previsit.size() > 0) {
                for (Node node : previsit) {
                    for (Node fan : node.fans) {
                        if (onecomponent.contains(fan) || temp.contains(fan) || previsit.contains(fan))
                            continue;
                        temp.add(fan);
                    }
                }
                for (Node node : previsit)
                    onecomponent.add(node);
                previsit = new ArrayList<Node>(temp);
                temp.clear();
            }

            for (Node node : onecomponent) {
                node.componentID = componentID;
                restnodes.remove(node);
                node.pre_component_size = onecomponent.size();
            }

            sizeofcomponent[componentID] = onecomponent.size();
        }

        return componentID;

        // int[] tempSize = new int[componentID];
        // for(int i = 1; i < componentID+1; i++){
        // tempSize[i-1] = sizeofcomponent[i];
        // }
        // Set<Integer> temp = new HashSet<Integer>();
        // for (int i = 1; i < componentID; i++)
        // temp.add(sizeofcomponent[i]);
        // int[] tempSize = new int[temp.size()];
        // Iterator iter = temp.iterator();
        // int tempi = 0;
        // while (iter.hasNext()) {
        // tempSize[tempi] = (Integer) iter.next();
        // tempi++;
        // }
        //
        // Arrays.sort(tempSize);
        //
        // for (Node node : g.nodes) {
        // int rank = 0;
        // for (int i = tempSize.length - 1; i >= 0; i--) {
        // if (node.pre_component_size == tempSize[i]) {
        // rank = tempSize.length - i;
        // break;
        // }
        // }
        // node.rank = rank;
        // }
        // // System.out.println("----------------------");
    }
}
