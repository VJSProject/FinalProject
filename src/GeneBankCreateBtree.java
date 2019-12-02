
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.ArrayList;

public class GeneBankCreateBtree {

    private static String dnaString, filename;
    private static int cache, degree, seqLength, cacheSize, debugLevel;
    private static BTree<Object> tree;

    public static void main(String[] args) throws FileNotFoundException {

        if(args.length < 4){
            help();
        }

        if(Integer.parseInt(args[0]) > 1){
            System.out.println("Incorrect Usage( 0 or 1 required)");
            help();
        }

        if(args[1].equals('0')){
//            If degree is zero, we need to find degree
        }
        if(args.length == 6){
            cacheSize = Integer.parseInt(args[4]);
            debugLevel = Integer.parseInt(args[5]);
        }
        cache = Integer.parseInt(args[0]);
        degree = Integer.parseInt(args[1]);
        filename = args[2];
        seqLength = Integer.parseInt(args[3]);

//        reading file to parse DNA code
        dnaString = createLongString(filename);
        long[] keys = createKeyValues(dnaString,seqLength);
        System.out.println(Arrays.toString(keys));
        tree = new BTree<>(degree);
        for(Long i : keys){
            TreeObject leaf = new TreeObject(i);
            tree.insertObject(leaf);

        }
        System.out.println(tree.toString());
    }

    /**
     * Reads in file name and parse the DNA string into one single string
     * @param filename
     * @return
     */
    private static String createLongString(String filename) {
        String bigString = "";
        try {
            boolean dnaSection = false;
            int i = 1;
            Scanner sc = new Scanner(new File(filename));
            while(sc.hasNext()){
                if(sc.nextLine().contains("ORIGIN")){
                    dnaSection = true;
                    while(dnaSection){
                        String line = sc.nextLine();
                        if(line.contains("//")){
                            dnaSection =false;
                        }else{
                            String str = line.replaceAll("(\\d|\\s)","");
                            bigString += str;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bigString;
    }

    /**
     * Takes a DNA string and splits it into key length and adds it to a long array.
     * @param dnaString
     * @param key
     * @return
     */
    private static long[] createKeyValues(String dnaString, int key) {
        int arrLength = dnaString.length() - key;
        long[] tokens = new long[arrLength];
    	for(int i = 0; i< dnaString.length(); i++) {
    		int size = key;
    		if(i+size < dnaString.length()){
    		    String dnaSubstring = dnaString.substring(i, i + size);
    		    if(!dnaSubstring.contains("n")) {
                    String hold = "";
                    hold = dnaSubstring.replaceAll("[aA]", "00");
                    hold = hold.replaceAll("[cC]", "01");
                    hold = hold.replaceAll("[gG]", "10");
                    hold = hold.replaceAll("[tT]", "11");

                    tokens[i] = Long.parseLong(hold,2);
                }
    		}
    	}
        return tokens;
    }

    private static void help() {
        System.out.println("Usage:");
        System.out.println("GeneBankCreateBtree <0/1(no/with Cache)> <degree> <gbk file> <sequence length> [<cache size>]" +
                " [<cache size>]");
        System.exit(0);
    }
}

