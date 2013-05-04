import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.List;
import java.util.ArrayList;
import java.util.Vector;

public class MultiEdgesDirectedGraph {

    List<Node> nodes;

    Hashtable IDToIndexHashtable = new Hashtable();

    Vector<Integer> ids;

    public int getGraphSize() {
        return nodes.size();
    }

    public int getGraphEdgeNum() {
        int count = 0;
        for (Node node : nodes) {
            count = count + node.getLeaders().size();
        }
        // count = count/2;
        return count;
    }

    public MultiEdgesDirectedGraph() {
        nodes = new ArrayList<Node>();

    }

    public int NumberofNodes() {
        return nodes.size();
    }

    public boolean findDirectedEdge(Node src, Node des) {
        return src.hasLeader(des);
    }

    public boolean addNode(Node v) {
        boolean res = nodes.add(v);
        // ids.add(n.getNodeID());

        if (res) {
            IDToIndexHashtable.put(v.getNodeID(), nodes.size() - 1);
        }
        return res;
        // return nodes.add(v);
    }

    private int index(int id) {
        if (IDToIndexHashtable.containsKey(new Integer(id)))
            return (Integer) IDToIndexHashtable.get(id);

        return -1;
    }

    public boolean removeNode(Node n) {
        if (!nodes.contains(n))
            return false;
        List<Node> temp1 = n.getLeaders();
        for (int i = 0; i < temp1.size(); i++) {
            Node nei = temp1.get(i);
            nei.deleteFan(n);
        }

        List<Node> temp2 = n.getFans();
        for (int i = 0; i < temp2.size(); i++) {
            Node nei = temp2.get(i);
            nei.deleteLeader(n);
        }
        
        IDToIndexHashtable.put(n.id, -1);
        n=null;
//        if(nodes.remove(n)){
//            this.IDToIndexHashtable.remove(n.id);
//            return true;
//        }
        return true;
        //return nodes.remove(n);
    }

    // public double getBCByID(int id){
    // if(!IDToBCHashtable.containsKey(id))
    // {
    // System.out.println("there is no this node");
    // return -1;
    // }else{
    // return (Double)IDToBCHashtable.get(id);
    // }
    // }

    // public int getCorenessByID(int id){
    // if(!IDToCorenessHashtable.containsKey(id))
    // {
    // System.out.println("there is no this node");
    // return -1;
    // }else{
    // return (Integer)IDToCorenessHashtable.get(id);
    // }
    // }
    //	 
    public Node getNodeByID(int id) {
        if (!IDToIndexHashtable.containsKey(id)) {
            System.out.println("there is no this node");
            return null;
        }

        int index = (Integer) IDToIndexHashtable.get(id);
        if (index >= 0) {
            return nodes.get(index);
        } else {
            return null;
        }
    }

    public Node getNodeByIndex(int index) {
        return nodes.get(index);
    }

    public int getNodeindex(int nodeid) {
        if (IDToIndexHashtable.containsKey(new Integer(nodeid)))
            return (Integer) IDToIndexHashtable.get(nodeid);
        return -1;

        // int index = -1;
        // for(int i=0;i<nodes.size();i++)
        // {
        // if(nodeid==nodes.get(i).id) {index = i;break;}
        // }
        //		 
        // return index;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void saveDirectedGraphToFile(String filename) {
        // System.out.println(nodes.size()+"\t"+this.getGraphEdgeNum());

        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(filename)));
            out.println(this.NumberofNodes() + "\t" + this.getGraphEdgeNum());
            for (int i = 0; i < nodes.size(); i++) {
                Node src = nodes.get(i);
                List<Node> neis = src.getLeaders();
                for (Node a : neis) {
                    // if(a.getNodeID()<src.getNodeID())
                    // {
                    out.println(src.id + "\t" + a.getNodeID());
                    // System.out.println(src.id + "\t" + a.getNodeID());
                    // }
                }
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveUnDirectedGraphToFile(String filename) {
        // System.out.println(nodes.size()+"\t"+this.getGraphEdgeNum());
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(filename)));
            out.println(this.NumberofNodes() + "\t" + this.getGraphEdgeNum());
            for (int i = 0; i < nodes.size(); i++) {
                Node src = nodes.get(i);
                if(this.getNodeindex(src.getNodeID())==-1)
                    continue;
                List<Node> neis = src.getLeaders();
                for (Node a : neis) {
                    if (a.getNodeID() < src.getNodeID()) {
                        out.println(src.id + "\t" + a.getNodeID());
                        //System.out.println(src.id + "\t" + a.getNodeID());
                    }
                }
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean addDirectedEdge(DirectedEdge e) {
        Node src = e.getStartN();
        Node dest = e.getEndN();
        if (!src.addLeader(dest))
            return false;
        // if(!dest.addneibor(src)) return false;
        return true;
    }

    public boolean removeDirectEdge(DirectedEdge e) {
        Node src = e.getStartN();
        Node dest = e.getEndN();
        if (!src.deleteLeader(dest))
            return false;
        if (!dest.deleteFan(src))
            return false;
        return true;
    }

    public boolean addDirectEdge(Node src, Node dest) {
        if (!src.addLeader(dest))
            return false;
        if (!dest.addFan(src))
            return false;
        return true;
    }

    public int reciprocalLinks() {
        int num = 0;
        int len = nodes.size();
        Node node = null;
        for (int i = 0; i < len; i++) {
            node = nodes.get(i);
            int len_fans = node.getFans().size();
            Node fan = null;
            for (int j = 0; j < len_fans; j++) {
                fan = node.getFans().get(j);
                if (node.leaders.contains(fan))
                    num++;
            }
        }
        return num;
    }

    // public boolean Connected()
    // {
    // for(Node a:nodes)
    // {
    // if(a.getDegree() == 0 ) return false;
    // }
    // return true;
    // }

    // public double getRc()
    // {
    // int maxcore = getMaxcore();
    // double[] qi = new double[maxcore+1];
    // int[] shell = new int[maxcore+1];
    // //int[] eNum = new int[maxcore+1];
    // for(int i=1; i< maxcore+1; i++)
    // {
    // qi[i] = 0;
    // shell[i] = 0;
    // //eNum[i] = 0;
    // }
    // List<Node> nucleus = new ArrayList<Node>();
    // for(Node node: nodes)
    // {
    // shell[node.coreness] ++;
    // if(node.coreness == maxcore)
    // nucleus.add(node);
    // }
    //		 
    // for(Node node: nucleus)
    // {
    // List<Node> neis = node.getNeibor();
    // for(Node nei: neis)
    // qi[nei.getCoreness()]++;
    // }
    // qi[maxcore] = qi[maxcore]/2;
    //		 
    // for(int i=1;i<maxcore+1;i++)
    // {
    // qi[i] = qi[i]/(i*shell[i]);
    // }
    //		
    // double rc = 0;
    // double down = 0;
    // for(int i=1;i<maxcore+1;i++)
    // {
    // rc = rc + (maxcore-i)*qi[i];
    // down = down + (maxcore-i);
    // }
    // rc = (rc/down)*(((double)maxcore)/nucleus.size());
    // return rc;
    // }
    //	 
    // public int getMaxcore()
    // {
    // int maxcore = 0;
    // for(Node node: nodes)
    // {
    // if(node.coreness > maxcore) maxcore = node.coreness;
    // }
    // return maxcore;
    // }
    //	 
    // public void readinCoreness(String corenessFile)
    // {
    // try
    // {
    // BufferedReader br = new BufferedReader(new InputStreamReader(new
    // FileInputStream(new File(corenessFile))));
    // String line ;
    // while((line=br.readLine())!=null)
    // {
    // if(line.contains("#")) continue;
    // line = line.trim() ;
    // if(line.length()==0)
    // {
    // System.out.println("line length is zero");
    // continue;
    // }
    // line = line.replace('-',' ');
    // StringTokenizer st = new StringTokenizer(line);
    // int NodeID;
    // int degree;
    // int coreness;
    // try
    // {
    // NodeID = Integer.parseInt(st.nextToken());
    // degree = Integer.parseInt(st.nextToken());
    // coreness = Integer.parseInt(st.nextToken());
    // //if(this.getNodeByID(NodeID).getDegree()!=degree)
    // System.out.println("wrong");
    // this.getNodeByID(NodeID).setCoreness(coreness);
    // }catch(Exception ee)
    // {
    // ee.printStackTrace();
    // }
    // }
    // br.close();
    // }catch(Exception e){
    // e.printStackTrace();
    // }
    // }
    //	 
    // public void readinCorenessNew(String corenessFile)
    // {
    // try
    // {
    // BufferedReader br = new BufferedReader(new InputStreamReader(new
    // FileInputStream(new File(corenessFile))));
    // String line ;
    // while((line=br.readLine())!=null)
    // {
    // if(line.contains("#")) continue;
    // line = line.trim() ;
    // if(line.length()==0)
    // {
    // System.out.println("line length is zero");
    // continue;
    // }
    // line = line.replace('-',' ');
    // StringTokenizer st = new StringTokenizer(line);
    // int NodeID;
    // int coreness;
    // try
    // {
    // NodeID = Integer.parseInt(st.nextToken());
    // coreness = Integer.parseInt(st.nextToken());
    // //if(this.getNodeByID(NodeID).getDegree()!=degree)
    // System.out.println("wrong");
    // IDToCorenessHashtable.put(NodeID, coreness);
    // //this.getNodeByID(NodeID).setCoreness(coreness);
    // }catch(Exception ee)
    // {
    // ee.printStackTrace();
    // }
    // }
    // br.close();
    // }catch(Exception e){
    // e.printStackTrace();
    // }
    // }

    // public void readinBC(String BCFile)
    // {
    // try
    // {
    // BufferedReader br = new BufferedReader(new InputStreamReader(new
    // FileInputStream(new File(BCFile))));
    // String line ;
    // while((line=br.readLine())!=null)
    // {
    // if(line.contains("#")) continue;
    // line = line.trim() ;
    // if(line.length()==0)
    // {
    // System.out.println("line length is zero");
    // continue;
    // }
    // line = line.replace('-',' ');
    // StringTokenizer st = new StringTokenizer(line);
    // int NodeID;
    // double bc;
    // try
    // {
    // NodeID = Integer.parseInt(st.nextToken());
    // bc = Double.valueOf(st.nextToken());
    // //if(this.getNodeByID(NodeID).getDegree()!=degree)
    // System.out.println("wrong");
    // IDToBCHashtable.put(NodeID, bc);
    // //this.getNodeByID(NodeID).setCoreness(coreness);
    // }catch(Exception ee)
    // {
    // ee.printStackTrace();
    // }
    // }
    // br.close();
    // }catch(Exception e){
    // e.printStackTrace();
    // }
    // }

    public static MultiEdgesDirectedGraph ConstructFromFile(String filename) {
        return ConstructFromFile(new File(filename));
    }

    public static MultiEdgesDirectedGraph ConstructFromFile(File file) {
        MultiEdgesDirectedGraph g = new MultiEdgesDirectedGraph();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line;
            int count = 0;
            while ((line = br.readLine()) != null) {

                line = line.trim();
                if (line.length() == 0) {
                    System.out.println("line length is zero");
                    continue;
                }
                line = line.replace('-', ' ');
                StringTokenizer st = new StringTokenizer(line);

                int fromID;
                int toID;
                try {
                    fromID = Integer.parseInt(st.nextToken());

                    toID = Integer.parseInt(st.nextToken());
                } catch (Exception exp) {
                    System.out.println(line);
                    continue;
                }
                if (fromID == toID)
                    continue;
                count++;
                // int i1 = g.getNodeindex(fromID);
                int i1 = g.index(fromID);
                // int i2 = g.getNodeindex(toID);
                int i2 = g.index(toID);

                Node src, dest;
                if (i1 < 0) {
                    src = new Node(fromID, g);
                    if (g.addNode(src)) {

                    } else {
                        // System.out.println("Add src node "+src.getNodeID()+"
                        // failed in line "+line);
                    }
                } else {
                    src = g.getNodes().get(i1);
                }
                if (fromID != toID && i2 < 0) {
                    dest = new Node(toID, g);
                    if (g.addNode(dest)) {

                    } else {
                        // System.out.println("Add dest node
                        // "+dest.getNodeID()+" failed in line "+line);
                    }
                } else if (fromID == toID) {
                    dest = src;

                } else {
                    dest = g.getNodes().get(i2);
                }

                if (g.addDirectEdge(src, dest)) {
                    // System.out.println("Add edge: "+src.getNodeID()+"
                    // "+dest.getNodeID());
                } else {
                    System.out.println("Add edge: " + src.getNodeID() + " " + dest.getNodeID() + " failed");

                }

            }
            br.close();
            System.out.println("Number of line:" + count);
        } catch (IOException exp) {
            System.out.println(exp);
        }
        System.out.println(g.getGraphSize() + "\t" + g.getGraphEdgeNum());
        return g;

    }

    public static MultiEdgesDirectedGraph ConstructFromUndirectedFile(String filename) {
        File file = new File(filename);
        MultiEdgesDirectedGraph g = new MultiEdgesDirectedGraph();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line;
            int count = 0;
            line = br.readLine();// skip the first line
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.length() == 0) {
                    System.out.println("line length is zero");
                    continue;
                }
                line = line.replace('-', ' ');
                StringTokenizer st = new StringTokenizer(line);

                int fromID;
                int toID;
                try {
                    fromID = Integer.parseInt(st.nextToken());
                    toID = Integer.parseInt(st.nextToken());
                } catch (Exception exp) {
                    System.out.println(line);
                    continue;
                }
                // if(fromID==toID) continue;
                count++;
                // int i1 = g.getNodeindex(fromID);
                int i1 = g.index(fromID);
                // int i2 = g.getNodeindex(toID);
                int i2 = g.index(toID);

                Node src, dest;
                if (i1 < 0) {
                    src = new Node(fromID, g);
                    if (g.addNode(src)) {

                    } else {
                        System.out.println("Add src node " + src.getNodeID() + " failed in line " + line);
                    }
                } else {
                    src = g.getNodes().get(i1);
                }
                if (fromID != toID && i2 < 0) {
                    dest = new Node(toID, g);
                    if (g.addNode(dest)) {

                    } else {
                        System.out.println("Add dest node " + dest.getNodeID() + " failed in line " + line);
                    }
                }

                else if (fromID == toID) {
                    dest = src;

                } else {
                    dest = g.getNodes().get(i2);
                }

                if (fromID == toID)
                    continue;

                if (g.addDirectEdge(src, dest)) {
                    // System.out.println("Add edge: "+src.getNodeID()+"
                    // "+dest.getNodeID());
                } else {
                    // System.out.println("Add edge: "+src.getNodeID()+"
                    // "+dest.getNodeID()+" failed");

                }

                if (g.addDirectEdge(dest, src)) {
                    // System.out.println("Add edge: "+src.getNodeID()+"
                    // "+dest.getNodeID());
                } else {

                }
            }
            br.close();
        } catch (IOException exp) {
            System.out.println(exp);
        }
        return g;

    }

    public static MultiEdgesDirectedGraph ConstructFromDirectedFile(String filename) {
        File file = new File(filename);
        MultiEdgesDirectedGraph g = new MultiEdgesDirectedGraph();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line;
            int count = 0;
            line = br.readLine();// skip the first line
            while ((line = br.readLine()) != null) {

                line = line.trim();
                if (line.length() == 0) {
                    System.out.println("line length is zero");
                    continue;
                }
                line = line.replace('-', ' ');
                StringTokenizer st = new StringTokenizer(line);

                int fromID;
                int toID;
                try {
                    fromID = Integer.parseInt(st.nextToken());

                    toID = Integer.parseInt(st.nextToken());
                } catch (Exception exp) {
                    System.out.println(line);
                    continue;
                }
                // if(fromID==toID) continue;
                count++;
                // int i1 = g.getNodeindex(fromID);
                int i1 = g.index(fromID);
                // int i2 = g.getNodeindex(toID);
                int i2 = g.index(toID);

                Node src, dest;
                if (i1 < 0) {
                    src = new Node(fromID, g);
                    if (g.addNode(src)) {

                    } else {
                        // System.out.println("Add src node "+src.getNodeID()+"
                        // failed in line "+line);
                    }
                } else {
                    src = g.getNodes().get(i1);
                }
                if (fromID != toID && i2 < 0) {
                    dest = new Node(toID, g);
                    if (g.addNode(dest)) {

                    } else {
                        // System.out.println("Add dest node
                        // "+dest.getNodeID()+" failed in line "+line);
                    }
                } else if (fromID == toID) {
                    dest = src;

                } else {
                    dest = g.getNodes().get(i2);
                }

                if (fromID == toID)
                    continue;

                if (g.addDirectEdge(src, dest)) {
                    // System.out.println("Add edge: "+src.getNodeID()+"
                    // "+dest.getNodeID());
                } else {
                    // System.out.println("Add edge: "+src.getNodeID()+"
                    // "+dest.getNodeID()+" failed");

                }
            }
            br.close();
        } catch (IOException exp) {
            System.out.println(exp);
        }
        return g;

    }
}
