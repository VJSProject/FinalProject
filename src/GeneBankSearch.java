import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
public class GeneBankSearch {

    private static int cache,debugLevel;
    private static int cacheSize = 0;
    private static String btreeFile,queryFile;
    private static BTree<Long> readTree;
    private static int seqLength;

    public static void main(String[] args) throws FileNotFoundException {

        if(args.length > 5 || args.length < 3 || Integer.parseInt(args[0]) > 1){
            help();
        }
        
        //all arguments given
        if(args.length == 5){
            debugLevel = Integer.parseInt(args[4]);
            cacheSize = Integer.parseInt(args[3]);
        }
        
        //some arguments omitted
        if(args.length == 4){
            if(Integer.parseInt(args[0])==0){ //no cache
                 debugLevel = Integer.parseInt(args[3]);
            }
            if(Integer.parseInt(args[0])==1){ //yes cache
                 cacheSize = Integer.parseInt(args[3]);
                 debugLevel = 0;
            }
        }

        //no cache or debug level given
        if(args.length == 3){
            debugLevel = 0;
        }
        
        
        btreeFile = args[1];
        readTree = new BTree<Long>(1);
        readTree.readFromBinary(btreeFile);
        //if using cache
        if(cacheSize != 0)
        {
        	readTree.enableCache(cacheSize);
        }

        queryFile = args[2];

        File qFile = new File(queryFile);
        try {
            Scanner qScan = new Scanner(qFile);
            while(qScan.hasNextLine()){
                String sequence = qScan.next();
                seqLength = sequence.length();
                String seqBinary = sequence.replaceAll("[aA]", "00");
                seqBinary = seqBinary.replaceAll("[cC]", "01");
                seqBinary = seqBinary.replaceAll("[gG]", "10");
                seqBinary = seqBinary.replaceAll("[tT]", "11");

                int feq = readTree.BTreeSearch(Long.parseLong(seqBinary));
                System.out.print(sequence + ": ");
                if(feq < 0){
                    System.out.println("Pattern Not Found");
                }
                else{
                    System.out.println(feq);
                }
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
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
