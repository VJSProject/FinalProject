
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.ArrayList;

public class GeneBankCreateBtree {

	private final static int BLOCKSIZE = 4096;
	private final static int NODEMETADATA = Integer.BYTES*3;			//Integers storing degree, number of nodes, and object size
	private final static int OBJECTDATA = Long.BYTES + Integer.BYTES;	//Long storing key, integer storing
	private final static int POINTERSIZE = Integer.BYTES;				//Integer pointing to a nodes offset on file
	
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
    	//107259  81 1024
    	//
    	
    	parseArgs(args);
    	
    	//Creates BTree
    	if(degree == 0) {
    		if(debugLevel >= 0)
    			System.err.print("Calculating optimal degree... ");
    		tree = new BTree<Long>(calculateOptimalDegree());
    		if(debugLevel >= 0)
    			System.err.println("found degree: " + tree.getDegree());
    	}
    	else {
    		if(debugLevel >= 0)
    			System.err.println("Using degree: " + degree);
    		tree = new BTree<Long>(degree);
    	}
    	
    	if(debugLevel >= 0)
			System.err.println("Using key length: " + seqLength);
    	
    	//Turns on cache
    	if(usingCache)
    	{
    		if(debugLevel >= 0)
    			System.err.println("Using cache size: " + cacheSize);
    		tree.enableCache(cacheSize);
    	}
    	
    	if(debugLevel >= 0)
    		System.err.print("Reading file \""+ gbkFile + "\"... ");
    	//parses DNA sequences from file
        dnaStrings = createLongString(gbkFile);
        //If not dna string found in file
        if(dnaStrings.isEmpty()){
			System.err.println("No DNA string found in file: " + gbkFile);
			System.exit(0);
		}
        //creates keys from each DNA sequence
        keys = new ArrayList<Long>();
        for(String s: dnaStrings)
        	keys.addAll(createKeyValues(s, seqLength));
        if(debugLevel >= 0)
        	System.err.print("Building tree... ");
        long start = System.currentTimeMillis();
        //write tree to binary
        tree.buildTree(keys);        
        btreeFile = gbkFile + ".btree.data."+seqLength+"."+degree;
        tree.writeLongsToBinary(btreeFile);
        
		if(debugLevel >= 0)
		{
			System.err.println("Done.");
			System.err.println("Tree build-time: " + (System.currentTimeMillis()-start));
			System.err.println("Number of nodes in tree: "+tree.getNumNodes());
			System.err.println("Number of keys in tree: "+tree.getInOrderObjectArray().size());
		}
		
		if(debugLevel == 1)
		{
			dumpFile();
			System.err.println("Dump file created.");
		}
    }

    private static void dumpFile() {
		try {
			FileWriter writer = new FileWriter(new File("dump"));
			PrintWriter printer = new PrintWriter(writer);
			
			ArrayList<TreeObject<Long>> objects = tree.getInOrderObjectArray();
			for(TreeObject<Long> o: objects)
			{
				String key = toDnaString(o.getKey());
				int frequency = o.getFrequency();
				printer.printf("%s: %d%n", key, frequency);	
			}
			printer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}			
		
	}

	private static String toDnaString(Long key) {

		String dna = "";
		String keyString = key.toString();
		while(keyString.length() < seqLength*2)
		{
			keyString = "0" + keyString;
		}
		for(int i = 0; i < keyString.length()-1; i = i+2)
		{
			dna += keyString.substring(i, i+2);
			dna = dna.replaceAll("00", "a");
	        dna = dna.replaceAll("01", "c");
	        dna = dna.replaceAll("10", "g");
	        dna = dna.replaceAll("11", "t");
		}
		return dna;
	}

	/**
     * Creates DNA strings from file
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
    		if(i+size <= dnaString.length()){
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

    /**
     * calculates the optimal degree given disk block size of 4096
     * @return
     */
    private static int calculateOptimalDegree() {
    	// metaData + pointers + objects <= 4096
    	// 12 + 4(2t+1) + 12(2t-1) <= 4096
    	// 4 + 32t <= 4096
    	int optimalDegree = (BLOCKSIZE - NODEMETADATA - POINTERSIZE + OBJECTDATA) / ((POINTERSIZE*2) + (OBJECTDATA*2));
		return optimalDegree;
	}
    
    /**
     * parses main method arguments
     * @param args
     */
    private static void parseArgs(String[] args)
    {
    	if(args.length >= 4 && args.length <= 6){
        	try {
        		/* Cache usage arguments */
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
	            else if(args.length == 5)
	            {
	            	debugLevel = Integer.parseInt(args[4]);
	            	if(debugLevel < 0 || debugLevel > 1)
	            		throw new NumberFormatException("Incorrect Usage (0 or 1 required in debug parameter)");
	            }
        	} catch (NumberFormatException e) {
        		System.err.println(e.getMessage());
        		help();
        	}
        }
        else
        {
        	help();
        }
    }
    
    private static void help() {
        System.err.println("Usage:");
        System.err.println("GeneBankCreateBtree <0/1(no/with Cache)> <degree> <gbk file> <sequence length> [<cache size>]" +
                " [<debug level>]");
        System.exit(0);
    }
}

