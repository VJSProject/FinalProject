public class GeneBankSearch {

    private static int cache,cacheSize,debugLevel;
    private static String btreeFile,queryFile;

    public static void main(String[] args) {

        if(args.length > 5 || args.length < 3 || Integer.parseInt(args[0]) > 1){
            help();
        }

        if(args.length == 5){
            debugLevel = Integer.parseInt(args[4]);
        }

        btreeFile = args[1];
        queryFile = args[2];

    }

    private static void help() {
        System.out.println("Usage: java GeneBankSearch <0/1(no/with Cache)> <Btree file> <query file> [<cache size>]" +
                "[<debug level>]");
        System.exit(0);
    }
}
