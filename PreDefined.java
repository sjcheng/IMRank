import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.*;
import java.util.StringTokenizer;

public class PreDefined {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		main3();
//		 String filename="E:\\Experiments\\IM\\networks\\BA_1000_2_1.txt";
//		 //filename = "C:\\testMultiEdges.txt";
//		 double p = 0.09;
//		 int inter_num = 10000;
//		 run(filename, p, inter_num);
//		 System.out.println(runVar(filename, p, inter_num));
		 
	}
	public static void main2(){
		String filename="E:\\Experiments\\IM\\networks\\BA_1000_2_1.txt";
		 //filename = "C:\\testMultiEdges.txt";
		 double[] p = {0.005, 0.01, 0.015, 0.02, 0.025, 0.03, 0.035, 0.04, 0.045, 0.05, 0.055,
				 0.06, 0.065, 0.07, 0.075, 0.08, 0.085, 0.09, 0.095, 0.10, 0.11, 0.12, 0.13, 0.14,
				 0.15, 0.16, 0.17, 0.18, 0.19, 0.20, 0.21, 0.22, 0.23, 0.24, 0.25, 0.26, 0.27, 0.28, 0.29, 0.30
		 };
		 int inter_num = 2000;
		 for(int i=0;i<p.length; i++){
	    	 run(filename, p[i], inter_num);
	    	 System.out.println(p[i]+"\t"+runVar(filename, p[i], inter_num));
		 }
	}
	
	public static void main3(){
		String filename="E:\\Experiments\\IM\\networks\\BA_1000_3_1.txt";
		 //filename = "C:\\testMultiEdges.txt";
		 double[] p = {0.005, 0.008,  0.01, 0.015, 0.02, 0.025, 0.03, 0.035, 0.04, 0.045, 0.05, 0.055,
				 0.06, 0.065, 0.07, 0.075, 0.08, 0.085, 0.09, 0.095, 0.10, 0.11, 0.12, 0.13, 0.14,
				 0.15, 0.16, 0.17, 0.18, 0.19, 0.20, 0.21, 0.22, 0.23, 0.24, 0.25, 0.26, 0.27, 0.28, 0.29, 0.30
		 };
		 int inter_num = 2000;
		 for(int i=0;i<p.length; i++){
	    	 run(filename, p[i], inter_num);
	    	 //System.out.println(p[i]+"\t"+runEntropy(filename, p[i], inter_num));
		 }
	}
	
	public static double getEntropy(int[] values){
		double entropy = 0;
		int maxSize = 0;
		
		for(int i=0; i<values.length; i++){
			if(maxSize < values[i])
				maxSize = values[i];
		}
		
		double[] newValues = new double[maxSize];
		for(int i=0; i<values.length; i++){
			if(values[i] == 0) 
				System.out.println(i+"\t"+"wrong");
			newValues[values[i]-1] = newValues[values[i]-1] + 1;
		}
		for(int i=0; i<newValues.length; i++){
			newValues[i] = (double)newValues[i]/(double)values.length;
		}
		
		for(int i=0; i<newValues.length; i++){
			if(newValues[i]==0)
				continue;
			entropy = entropy + newValues[i]*log(newValues[i],2.0);
		}
		return -entropy;
	}
	
	protected static double log(double value, double base) 
    {
    	return Math.log(value) / Math.log(base);
    }
	
	public static double runVar(String filename, double p, int inter_num){
		double ave_var = 0;
		for(int i=0; i<inter_num-1; i++){
			String file1 = filename.replace(".txt", "_del_"+p+"_"+i+".txt");
			String file2 = filename.replace(".txt", "_del_"+p+"_"+(i+1)+".txt");
			ave_var = ave_var + var(file1, file2);
		}
		return ave_var/(inter_num-1);
	}
	
	public static double runEntropy(String filename, double p, int inter_num){
		//System.out.println(filename+"\t"+p);
		double ave_entropy = 0;
		for(int i=0; i<inter_num-1; i++){
			
			String file1 = filename.replace(".txt", "_del_"+p+"_"+i+".txt");
			//System.out.println(file1);
			double entropy = entropy(file1);
			ave_entropy = ave_entropy + entropy;
			try{
				File f = new File(file1);
				f.delete();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return ave_entropy/inter_num;
	}
	
	
	public static void run(String filename, double p, int inter_num){
	     double ave_entropy = 0;
	     for(int i = 0; i < inter_num; i++){
	    	 //delete the edges whose related propagtion small than p
	    	 MultiEdgesDirectedGraph g  = MultiEdgesDirectedGraph.ConstructFromUndirectedFile(filename);
	    	 List<Node> removeFans = null;
	    	 for(Node node: g.getNodes()){
	    		 removeFans = new ArrayList<Node>();
	    		 try{
	         		 for(Node fan: node.getFans()){
	     			    if(node.getNodeID() > fan.getNodeID())
	     			    	continue;
	     			    if(Math.random() > p){
	     			    	removeFans.add(fan);
	     			    }
	        		 }
	    		 }catch(Exception e){
	    			 e.printStackTrace();
	    		 }
	    		 for(Node removeFan: removeFans){
	    			 //System.out.println("remove edge: "+ node.id+"\t"+removeFan.id);
	    			 node.fans.remove(removeFan);
	    			 node.leaders.remove(removeFan);
	    			 removeFan.leaders.remove(node);
	    			 removeFan.fans.remove(node);
	    		 }
	    	 }
	    	 
	    	 //statistic the distribution of components 
	    	 cacluateComponents(g);
	    	 double entropy = entropy(g);
			 ave_entropy = ave_entropy + entropy;
	    	 //System.out.println(p+"\t"+runEntropy(filename, p[i], inter_num));
	    	 
//	    	 String outfile = filename.replace(".txt", "_del_"+p+"_"+i+".txt");
//	 		 saveTempFile(g, outfile);
	    	 //System.out.println("");
			 
	    	 g = null;
	     }
	     System.out.println(p+"\t"+ave_entropy/inter_num);
	}
	

	public static void cacluateComponents(DirectedGraph g){
		int maxsize = 0;
		int maxMapID = 0;
		int[] sizeofcomponent = new int[g.getGraphSize()+1];
		
		//initial
		for(Node node: g.nodes){
			node.componentID = -1;
		}
		
		//stat
		List<Node> restnodes = new ArrayList<Node>();
		for(Node node: g.nodes){
			restnodes.add(node);
		}
		
		int componentID = 0;
		while(restnodes.size() > 0){
			componentID ++;
			
            //BFS
			List<Node> onecomponent = new ArrayList<Node>();
			List<Node> previsit = new ArrayList<Node>();
			List<Node> temp = new ArrayList<Node>();
			previsit.add(restnodes.get(0));
			while(previsit.size()>0){
				for(Node node: previsit){
					for(Node fan: node.fans){
						if(onecomponent.contains(fan) || temp.contains(fan))
							continue;
						temp.add(fan);
					}
				}
				for(Node node: previsit)
					onecomponent.add(node);
				previsit = new ArrayList<Node>();
				for(Node node: temp){
					previsit.add(node);
				}
				temp = new ArrayList<Node>();
			}
			
			for(Node node: onecomponent){
				node.componentID = componentID;
				restnodes.remove(node);
			}
			
			sizeofcomponent[componentID] = onecomponent.size();
			if(onecomponent.size() > maxsize)
			{
				maxsize = onecomponent.size();
				maxMapID = componentID;
			}
		}
		//System.out.println(componentID+"\t"+maxsize+"\t"+"\t");
		//System.out.println();
//		for(int i=1; i< componentID+1; i++){
//			System.out.println(sizeofcomponent[i]);
//		}
		
		int[] tempSize = new int[componentID];
		for(int i = 1; i < componentID+1; i++){
			tempSize[i-1] = sizeofcomponent[i];
		}
		Arrays.sort(tempSize);
		for(int i = 0; i < componentID; i++){
			if(tempSize[componentID-i-1]==1) break;
			//System.out.println(tempSize[componentID-i-1]);
		}
	}
	
	public static void cacluateComponents(MultiEdgesDirectedGraph g){
		int maxsize = 0;
		int maxMapID = 0;
		int[] sizeofcomponent = new int[g.getGraphSize()+1];
		
		//initial
		for(Node node: g.nodes){
			node.componentID = -1;
		}
		
		//stat
		List<Node> restnodes = new ArrayList<Node>();
		for(Node node: g.nodes){
			restnodes.add(node);
		}
		
		int componentID = 0;
		while(restnodes.size() > 0){
			componentID ++;
			
            //BFS
			List<Node> onecomponent = new ArrayList<Node>();
			List<Node> previsit = new ArrayList<Node>();
			List<Node> temp = new ArrayList<Node>();
			previsit.add(restnodes.get(0));
			while(previsit.size()>0){
				for(Node node: previsit){
					for(Node fan: node.fans){
						if(onecomponent.contains(fan) || temp.contains(fan))
							continue;
						temp.add(fan);
					}
				}
				for(Node node: previsit)
					onecomponent.add(node);
				previsit = new ArrayList<Node>();
				for(Node node: temp){
					previsit.add(node);
				}
				temp = new ArrayList<Node>();
			}
			
			for(Node node: onecomponent){
				node.componentID = componentID;
				restnodes.remove(node);
				node.pre_component_size = onecomponent.size();
			}
			
			sizeofcomponent[componentID] = onecomponent.size();
			if(onecomponent.size() > maxsize)
			{
				maxsize = onecomponent.size();
				maxMapID = componentID;
			}
		}
		//System.out.println(componentID+"\t"+maxsize+"\t"+"\t");
		//System.out.println();
//		for(int i=1; i< componentID+1; i++){
//			System.out.println(sizeofcomponent[i]);
//		}
		
		int[] tempSize = new int[componentID];
		for(int i = 1; i < componentID+1; i++){
			tempSize[i-1] = sizeofcomponent[i];
		}
		
		Arrays.sort(tempSize);
//		for(int i = 0; i < componentID; i++){
//			if(tempSize[componentID-i-1]==1) break;
//			System.out.println(tempSize[componentID-i-1]);
//		}

	}
	
	public static void saveTempFile(MultiEdgesDirectedGraph g, String file){
		try{
           PrintWriter outrs = new PrintWriter(new BufferedWriter(new FileWriter(file)));
           for(Node node: g.nodes){
        	   if(node.pre_component_size == 0)
        		   System.out.println("wrong");
//        	   if(node.id == 0)
//        		   System.out.println(node.pre_component_size);
        	   outrs.println(node.getNodeID()+"\t"+node.pre_component_size);
           }
           outrs.close();
	   }catch(Exception e){
		   e.printStackTrace();
	   }
	}
	
	public static double entropy(String file1){
		int[] values1 = readindelG(file1);
		return getEntropy(values1);
	}
	
	public static double entropy(MultiEdgesDirectedGraph g){
		int[] values1 = new int[g.nodes.size()];
		//System.out.println(values1.length+"\t"+g.nodes.size());
		for(int i=0; i<g.nodes.size(); i++)
		{
			//System.out.println(g.nodes.get(i).id);
			values1[i] = g.nodes.get(i).pre_component_size;
		}
		return getEntropy(values1);
	}
	
	public static double var(String file1, String file2){
		double var = 0;
		int[] values1 = readindelG(file1);
		int[] values2 = readindelG(file2);
		for(int i=0;i<values1.length;i++){
			double temp = Math.abs(values1[i]-values2[i]);
			var = var + temp*temp;
		}
		var = var/values1.length;
		var = Math.sqrt(var);
		try{
			File f = new File(file1);
			f.delete();
		}catch(Exception e){
			e.printStackTrace();
		}
		return var;
	}
	
	public static int[] readindelG(String file){
		int[] values = new int[1000];
		try
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line ;
            int count=0;
            //line=br.readLine();
            while((line=br.readLine())!=null)
            {
             
                line = line.trim() ;
                if(line.length()==0)
                {
                    System.out.println("line length is zero");
                   continue;
                }
                //line = line.replace('-',' ');
                StringTokenizer st = new StringTokenizer(line);
                int nodeid = -1;
                int size = -1;
                try{
                	nodeid = Integer.parseInt(st.nextToken());
                	size = Integer.parseInt(st.nextToken());
                }catch(Exception e){
                	e.printStackTrace();
                }
                values[nodeid] = size;
                count++;
            }
            br.close();
        }catch(Exception e){
        	e.printStackTrace();
        }
        return values;
	}
}
