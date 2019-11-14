
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class GeneBankCreateBtree {

    private static String dnaSequence;
    private static String dnaString;

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
// reading file to parse DNA code
        dnaString = createLongString(args[2]);
        System.out.println(dnaString);

    }

    private static String createLongString(String filename) {
        String bigString = "";
        try {
            boolean dnaSection = false;
            int i = 1;
            Scanner sc = new Scanner(new File(filename));
            while(sc.hasNext()){
                if(sc.nextLine().contains("ORIGIN")){
                    bigString += "DNA" + i + " ";
                    i++;
                    dnaSection = true;
                    while(dnaSection){
                        String line = sc.nextLine();
                        if(line.contains("//")){
                            bigString += "\n";
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

    private static void help() {
        System.out.println("Please follow the below format");
        System.out.println("GeneBankCreateBtree <0/1(no/with Cache)> <degree> <gbk file> <sequence length> [<cache size>]" +
                " [<cache size>]");
        System.exit(0);
    }
}

