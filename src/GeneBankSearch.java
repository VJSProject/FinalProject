import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
public class GeneBankSearch {

    private static int debugLevel;
    private static boolean usingCache;
    private static int cacheSize;
    private static String btreeFile, queryFile;
    private static BTree<Long> readTree;
    private static int seqLength;

    public static void main(String[] args) throws FileNotFoundException {
    	
    	parseArgs(args);

        readTree = new BTree<Long>(1);
        try {
        	if(debugLevel < 0)
        		System.err.println("Building tree from file...");
        	readTree.readFromBinary(btreeFile);
        } catch (FileNotFoundException e) {
        	System.err.println("Error - BTree file not found");
        	help();
        }
        //if using cache
        if(usingCache) {
        	if(debugLevel < 0)
        		System.err.println("Cache enabled with size: "+cacheSize);
        	readTree.enableCache(cacheSize);
        }

        File qFile = new File(queryFile);
        try {
            Scanner qScan = new Scanner(qFile);
            while(qScan.hasNextLine()) {
                String sequence = qScan.next();
                seqLength = sequence.length();
                String seqBinary = sequence.replaceAll("[aA]", "00");
                seqBinary = seqBinary.replaceAll("[cC]", "01");
                seqBinary = seqBinary.replaceAll("[gG]", "10");
                seqBinary = seqBinary.replaceAll("[tT]", "11");

                int feq = readTree.BTreeSearch(Long.parseLong(seqBinary));
                System.out.print(sequence.toLowerCase() + ": ");
                if(feq < 0){
                    System.out.println("Pattern Not Found");
                }
                else{
                    System.out.println(feq);
                }
            }
            qScan.close();
        } catch (FileNotFoundException e) {
        	System.err.println("Error - Query file not found");
        	help();
        } catch(NumberFormatException e) {
        	System.err.println("Error - Improper query format");
        }

    }

    private static void parseArgs(String[] args) {
    	
    	debugLevel = -1;
    	usingCache = false;
    	cacheSize = -1;
    	
    	//number of arguments out of bounds
    	if(args.length > 5 || args.length < 3 || Integer.parseInt(args[0]) > 1 || Integer.parseInt(args[0]) < 0){
            help();
        }		
    	if(Integer.parseInt(args[0]) == 1)
    		usingCache = true;
    	
    	btreeFile = args[1];
    	queryFile = args[2];
    	
    	//when using cache
    	if(usingCache)
    	{
    		if(args.length < 4 ) {
    			System.out.println("Incorrect Usage (0 or 1 required in cache parameter. Cache size required if using cache.)");
    			help();
    		}
    		
    		cacheSize = Integer.parseInt(args[3]);
    		
    		if(args.length > 4)
    			debugLevel = Integer.parseInt(args[4]);
    		
    	}
    	//when not using cache
    	else {
    		if(args.length > 4)
    			help();
    		if(args.length == 4)
    			debugLevel = Integer.parseInt(args[3]);
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
			dna = dna.replaceAll("00", "A");
	        dna = dna.replaceAll("01", "C");
	        dna = dna.replaceAll("10", "G");
	        dna = dna.replaceAll("11", "T");
		}
		return dna;
	}

    private static void help() {
        System.out.println("Usage: java GeneBankSearch <0/1(no/with Cache)> <Btree file> <query file> [<cache size>]" +
                "[<debug level>]");
        System.exit(0);
    }

}
