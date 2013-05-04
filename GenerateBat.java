
public class GenerateBat {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		generateJavaForTopk();
		
	}

	public static void generateJavaForTopk(){
		String filename = "E:\\Experiments\\IM\\networks\\hep.txt";
		int[] k_rank = {21};
		int snapshot_number = 400;
		double[] p = {0.004, 0.006, 0.008, 0.01, 0.012, 0.014, 0.016, 0.018, 0.020,
				0.022, 0.024, 0.026, 0.028, 0.030,
				0.032, 0.034, 0.036, 0.038, 0.040,
				0.042, 0.044, 0.046, 0.048, 0.050,
				0.052, 0.054, 0.056, 0.060};
		int k = 50;
		
		for(int i = 0; i < p.length; i++){
			for(int j=0; j<10; j++)
		    	System.out.println("java -Xmx2048m -classpath . TopkComponents hep.txt 50 "+p[i]+" "+k_rank[0]+" 500");
		}
		
	}
	
	public static void main1(){
		int[] m = {2,3,4,5,6,7,8,9,10};
		int sample = 20;
		int n = 1000;
		String[] p = {"0.005","0.01","0.015","0.02","0.025","0.03","0.035","0.04","0.045","0.05","0.055","0.06","0.065","0.07","0.075","0.08","0.085","0.09","0.095","0.10","0.11","0.12","0.13","0.14","0.15","0.16","0.17","0.18","0.19","0.20","0.21","0.22","0.23","0.24","0.24","0.25","0.26","0.27","0.28","0.29","0.30"};
		for(int i=0; i<m.length; i++){
	    	for(int j = 1; j < 2; j++){
	     		String filename_pre = "BA_"+n+"_"+m[i]+"_"+j;
	    		//print network info
	     		System.out.println("Graph::BuildFromFile(\""+filename_pre+".txt\");");
	     		
	     		//print test info
	     		for(int k=0; k<20; k++){
	     	    	String file_seed = filename_pre+"_"+p[k]+"_MG_seeds.txt";
	     	    	String file_time = filename_pre+"_"+p[k]+"_MG_time.txt";
	     	    	String file_spread = filename_pre+"_"+p[k]+"_MG_Spread.txt";
	     	    	System.out.println("runICbyMixedGreedy("+p[k]+",\""+file_seed+"\",\""+file_time+"\",\""+file_spread+"\");");
	     		}
    		}
		}
	}
	
	public static void main2(){
		String[] p = {"0.004","0.006","0.008","0.01","0.012","0.014","0.016","0.018","0.02","0.022","0.024","0.026","0.028","0.03","0.032","0.034","0.036","0.038","0.04","0.042","0.044","0.046","0.048", "0.05","0.06","0.065","0.07","0.075","0.08","0.085","0.09","0.095","0.10","0.11","0.12","0.13","0.14","0.15","0.16","0.17","0.18","0.19","0.20","0.21","0.22","0.23","0.24","0.24","0.25","0.26","0.27","0.28","0.29","0.30"};
 		String filename_pre = "hep";
		System.out.println("Graph::BuildFromFile(\""+filename_pre+".txt\");");
		for(int k=0; k<p.length; k++){
	     		
	     		//print test info
	     	
//	     	    	String file_seed = filename_pre+"_"+p[k]+"_MG_seeds.txt";
//	     	    	String file_time = filename_pre+"_"+p[k]+"_MG_time.txt";
//	     	    	String file_spread = filename_pre+"_"+p[k]+"_MG_Spread.txt";
	     	    	//System.out.println("runICbyMixedGreedy("+p[k]+",\""+file_seed+"\",\""+file_time+"\",\""+file_spread+"\");");
	     	String file_seed =   filename_pre+"_"+p[k]+"_CELF_seeds.txt"; 
	     	String file_time =  filename_pre+"_"+p[k]+"_CELF_time.txt";
	     	String file_spread = filename_pre+"_"+p[k]+"_CELF_Spread.txt";
			System.out.println("runICbyGreedy("+p[k]+",\""+file_seed+"\",\""+file_time+"\",\""+file_spread+"\");");
		}
	}
	
}
