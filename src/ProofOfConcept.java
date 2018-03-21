import java.io.File;

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
    public static void main(String args[]){
        File directory = new File("config_negighbors.txt");
        File parent  = directory.getParentFile();
        if(parent== null){
            System.out.println("WHY ARE YOU NULL");
        }
        System.out.println(parent.getAbsolutePath());
        File trueDirectory = new File("C:\\Users\\Owner\\Documents\\Case Western\\Sophmore Year\\Networks\\");
        System.out.println(isInShared(trueDirectory,directory));
        String s = directory.getAbsolutePath();
        System.out.println(s);
    }

}
