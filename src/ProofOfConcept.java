import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.Buffer;

public class ProofOfConcept {
    public class Multithreading implements Runnable{
        int threadUsage;
        Multithreading(int number){
            threadUsage = number;
        }
        @Override
        public void run() {
            if(threadUsage == 1){

            }
        }
    }
    public static boolean isInShared(File directory, File file){
        if(file == null)
            return false;
        if(file.equals(directory))
            return true;
        return isInShared(directory, file.getParentFile());
    }

    public static void main(String args[]) throws Exception{
        File directory = new File("src\\config_neighbors.txt").getAbsoluteFile();
        BufferedReader reader = new BufferedReader(new FileReader(directory));
        //System.out.println(reader.readLine());
        File parent  = directory.getParentFile();
        File parent2 = directory.getAbsoluteFile().getParentFile();
        //System.out.println(parent2);
        File grandParent = parent2.getParentFile();
        File currentDirectory= new File(System.getProperty("user.dir"));
        System.out.println(isInShared(currentDirectory,directory));
        String s = directory.getAbsolutePath();
    }

}
