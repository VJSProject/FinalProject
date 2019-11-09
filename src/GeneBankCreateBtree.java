import java.io.File;

public class GeneBankCreateBtree {

    public static void main(String[] args){

        if(args.length < 4){
            help();
        }
        if(Integer.parseInt(args[0]) > 1){
            System.out.println("Incorrect Usage( 0 or 1 required)");
            help();
        }
        System.out.println("here");
    }

    private static void help() {
        System.out.println("Please follow the below format");
        System.out.println("GeneBankCreateBtree <0/1(no/with Cache)> <degree> <gbk file> <sequence length> [<cache size>]" +
                " [<cache size>]");
        System.exit(0);
    }
}

