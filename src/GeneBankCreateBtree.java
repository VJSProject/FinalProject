
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.ArrayList;

public class GeneBankCreateBtree {

    private static ArrayList<String> dnaStrings;
    private static ArrayList<Long> keys;

    /* user passed arguments */
    private static boolean usingCache;
	  private static int degree;
	  private static String gbkFile;
	  private static int seqLength;
	  private static int cacheSize;
	  private static int debugLevel = -1;
	
  	private static String btreeFile;
	  private static BTree<Long> tree;
	
    public static void main(String[] args) throws FileNotFoundException {

    	long start = System.currentTimeMillis();
    	parseArgs(args);
    	
    	//Creates BTree
    	if(degree == 0) {
    		if(debugLevel == 0)
    			System.out.print("Calculating optimal degree... ");
    		tree = new BTree<Long>(calculateOptimalDegree());
    		if(debugLevel == 0)
    			System.out.println("found degree: " + tree.getDegree());
    	}
    	else {
    		if(debugLevel == 0)
    			System.out.println("Using degree: " + degree);
    		tree = new BTree<Long>(degree);
    	}
    	
    	//Turns on cache
    	if(usingCache)
    	{
    		if(debugLevel == 0)
    			System.out.println("Using cache size: " + cacheSize);
    		tree.enableCache(cacheSize);
    	}
    	
    	//parses DNA sequences from file
        dnaStrings = createLongString(gbkFile);
        //creates keys from each DNA sequence
        keys = new ArrayList<Long>();
        for(String s: dnaStrings)
        {
        	keys.addAll(createKeyValues(s, seqLength));
        }
        
        tree.buildTree(keys);
        System.out.println(tree.getInOrderNodeArray());
        
        btreeFile = gbkFile + ".btree.data."+seqLength+"."+degree;
        tree.writeLongsToBinary(btreeFile, 8);
        
		if(debugLevel == 0)
		{
			System.out.println("Done.");
			System.out.println("Runtime: " + (System.currentTimeMillis()-start));
		}

    /**
     * 
     * @param filename
     * @return
     */
	private static ArrayList<String> createLongString(String filename) {
        ArrayList<String> sequences = new ArrayList<String>();

        String currentSequence = "";
        try {
            boolean dnaSection = false;
            Scanner sc = new Scanner(new File(filename));            
            while(sc.hasNext())
            {
                if(sc.nextLine().contains("ORIGIN"))
                {
                    dnaSection = true;
                    while(dnaSection){
                        String line = sc.nextLine();
                        if(line.contains("/")) {
                        	sequences.add(currentSequence);
                            currentSequence = "";
                            dnaSection = false;
                        } 
                        else {

                            String str = line.replaceAll("(\\d|\\s)","");
                            currentSequence += str;
                        }
                    }
                }
            }
            sc.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error: File not found");
        }
        return sequences;
    }

    /**
     * Takes a DNA string and splits it into key length and adds it to a long arraylist.
     * @param dnaString
     * @param size
     * @return
     */
    private static ArrayList<Long> createKeyValues(String dnaString, int size) {
    	ArrayList<Long> tokens = new ArrayList<Long>();
    	for(int i = 0; i< dnaString.length(); i++) {
    		if(i+size < dnaString.length()){
    		    String dnaSubstring = dnaString.substring(i, i + size);
    		    if(!dnaSubstring.contains("n")) {
                    String hold = "";
                    hold = dnaSubstring.replaceAll("[aA]", "00");
                    hold = hold.replaceAll("[cC]", "01");
                    hold = hold.replaceAll("[gG]", "10");
                    hold = hold.replaceAll("[tT]", "11");
              
                    tokens.add(Long.parseLong(hold));
             }
    		}
    	}
        return tokens;
    }

    private static int calculateOptimalDegree() {
		return degree;
		// TODO Auto-generated method stub
		
	}
    
    /**
     * parses main method arguments
     * @param args
     */
    private static void parseArgs(String[] args)
    {
    	if(args.length >= 4 && args.length <= 6){
        	try {
        		/* Cache usage args */
	            if(Integer.parseInt(args[0]) == 0)
	            	usingCache = false;
	            else if(Integer.parseInt(args[0]) == 1 && args.length > 4)
	            	usingCache = true;
	            else
	            	throw new NumberFormatException("Incorrect Usage (0 or 1 required in cache parameter. Cache size required if using cache.)");
	            if(usingCache)
	            	cacheSize = Integer.parseInt(args[4]);
	            
	            degree = Integer.parseInt(args[1]);
	            gbkFile = args[2];
	            seqLength = Integer.parseInt(args[3]);
	            if(seqLength < 1 || seqLength > 31)
	            	throw new NumberFormatException("Error: sequence length should be within the range 1-31");
	            
	            
	            
	            if(args.length == 6)
	            {
	            	debugLevel = Integer.parseInt(args[5]);
	            	if(debugLevel < 0 || debugLevel > 1)
	            		throw new NumberFormatException("Incorrect Usage (0 or 1 required in debug parameter)");
	            }
        	} catch (NumberFormatException e) {
        		System.out.println(e.getMessage());
        		help();
        	}
        }
        else
        {
        	help();
        }
    }
    
    private static void help() {
        System.out.println("Usage:");
        System.out.println("GeneBankCreateBtree <0/1(no/with Cache)> <degree> <gbk file> <sequence length> [<cache size>]" +
                " [<debug level>]");
        System.exit(0);
    }
}

