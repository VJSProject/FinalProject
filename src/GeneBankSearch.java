import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
public class GeneBankSearch {

    private static int cache,cacheSize,debugLevel;
    private static String btreeFile,queryFile;

    public static void main(String[] args) {

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
        queryFile = args[2];

        File qFile = new File(queryFile);
        try {
            Scanner scan = new Scanner(qFile);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private static void help() {
        System.out.println("Usage: java GeneBankSearch <0/1(no/with Cache)> <Btree file> <query file> [<cache size>]" +
                "[<debug level>]");
        System.exit(0);
    }
}
