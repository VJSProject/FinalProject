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
        File bFile = new File(btreeFile);
        try {
            Scanner scan = new Scanner(qFile);
            while(scan.hasNextLine()){
                String sequence = scan.next();
                Scanner scan2 = new Scanner(bFile);
                while(scan2.hasNextLine()){
                    String[] btreeLine = new String[2];
                    for(int i = 0; i<2; i++){
                        btreeLine[i] = scan2.next();
                    }
                    if(btreeLine[0]==sequence){
                        System.out.println(btreeLine.toString());
                        break;
                    }
                    else{
                        scan2.nextLine();
                    }   
                }     
                scan2.close();           
            }
            scan.close();
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
