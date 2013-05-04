import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class OverlapSimple {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		main3();
	}

	
	public static void main3(){
		String root1 = "E:\\Experiments\\IM\\Results\\Greedy(CELF)\\hep_10_1\\";
		String root2 = "E:\\Experiments\\IM\\Results\\Greedy(CELF)\\hep_10_2\\";
		String fileprefix = "hep_";
		String p1 = "0.004";
		String p2 = "0.008";
		int[] preseeds = readinSeeds(root1 + fileprefix + p1 +"_CELF_seeds.txt");
    	int[] afterseeds = readinSeeds(root2 + fileprefix + p2 +"_CELF_seeds.txt");
		System.out.println(p1+"\t"+p2+"\t"+getOverlapRatio(preseeds, afterseeds));
	}
	
	public static void main2(){
		String root = "E:\\Experiments\\IM\\ConsoleCode\\HEP_10.24\\";
		root = "E:\\Experiments\\IM\\Results\\Greedy(CELF)\\";
		String fileprefix = "hep_";
		
		// BA's p
//		String[] p = {"0.005","0.01","0.015","0.02","0.025","0.03","0.035","0.04","0.045","0.05","0.055","0.06",
//				"0.065","0.07","0.075","0.08","0.085","0.09","0.095","0.10","0.11","0.12","0.13","0.14","0.15","0.16",
//				"0.17","0.18","0.19","0.20","0.21","0.22","0.23","0.24","0.25","0.26","0.27","0.28","0.29","0.30"};
//		
		//hep's p
		String[] p = {"0.004","0.006","0.008","0.01","0.012","0.014","0.016","0.018","0.02",
				"0.022","0.024","0.026","0.028","0.030","0.032","0.034","0.036","0.038","0.040",
				"0.042","0.044","0.046"};
		//,"0.048", "0.05","0.06","0.065","0.07","0.075","0.08",
			//	"0.085","0.09","0.095","0.10","0.11","0.12","0.13","0.14","0.15","0.16","0.17",
			//	"0.18","0.19","0.20","0.21","0.22","0.23","0.24","0.24","0.25","0.26","0.27",
			//	"0.28","0.29","0.30"};
		
		int[] preseeds;
		int[] afterseeds;
		for(int j=0; j<17; j=j+1){
	    	for(int i=0; i<17; i++){
		    	preseeds = readinSeeds(root + fileprefix + p[i] +"_CELF_seeds.txt");
		    	afterseeds = readinSeeds(root + fileprefix + p[j] +"_CELF_seeds.txt");
	    		System.out.println(p[i]+"\t"+p[j]+"\t"+getOverlapRatio(preseeds, afterseeds));
	    	}
	    	System.out.println();
		}
	}
	
	public static int[] readinSeeds(String file){
		int[] seeds = new int[50];
		try
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line ;
            int count=0;
            line=br.readLine();
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
                try{
                	nodeid = Integer.parseInt(st.nextToken());
                }catch(Exception e){
                	e.printStackTrace();
                }
                seeds[count] = nodeid;
                count++;
            }
        }catch(Exception e){
        	e.printStackTrace();
        }
        return seeds;
	}
	
	public static double getOverlapRatio(int[] seeds1, int[] seeds2){
		int overlap = 0;
		for(int i = 0; i < seeds1.length; i++){
			if(seeds1[i] == -1) 
				System.out.println("wrong");
			for(int j= 0; j< seeds2.length; j++)
			{
				if(seeds1[i] == seeds2[j])
				{
					overlap++;
					break;
				}
			}
		}
			return (double)overlap/50.0;
	}

}
