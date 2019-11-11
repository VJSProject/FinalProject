
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class GeneBankCreateBtree {

    private static String dnaSequence;

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
// reading file to parse DNA code//issue when there are two lines of DNA
        try {
            boolean dnaSection = false;
            Scanner sc = new Scanner(new File(args[2]));
            while(sc.hasNext()){
                if(sc.nextLine().contains("ORIGIN")){
                    dnaSection = true;
                    while(dnaSection){
                        String line = sc.nextLine();
                        if(line.contains("//")){
                            dnaSection =false;
                        }else{
                            String str = line.replaceAll("\\d","");
                            String str2 = str.replaceAll("\\s", "");
                            String[] tokens = str2.split("(?<=\\G.{" + args[3] + "})");
                            System.out.println(Arrays.toString(tokens));
                        }
                    }
                }
//                if(sc.nextLine() == "ORIGIN"){
//                    dnaSequence = sc.nextLine();
//                    System.out.println(dnaSequence);
//                    System.exit(0);
//                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void help() {
        System.out.println("Please follow the below format");
        System.out.println("GeneBankCreateBtree <0/1(no/with Cache)> <degree> <gbk file> <sequence length> [<cache size>]" +
                " [<cache size>]");
        System.exit(0);
    }
}

