import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.SortedMap;

public class p2p{
    private static Thread thread1;
    Socket neighbor1Socket;
    Socket neighbor2Socket;
    static ArrayList<Socket> connections = new ArrayList<Socket>();
    File shared = new File("shared/");
    File obtained = new File("obtained/");
    public static class MultiThreading implements Runnable{
        ServerSocket listenSocket;
        Socket neighbor1;
        Socket neighbor2;
        int threadPurpose;
        MultiThreading(int purpose){
            threadPurpose = purpose;
        }
        MultiThreading(ServerSocket listenSocket){
            threadPurpose = 1;
            this.listenSocket = listenSocket;
        }

        MultiThreading(Socket neighbor1, Socket neighbor2){
            threadPurpose = 3;
            this.neighbor1 = neighbor1;
            this.neighbor2 = neighbor2;
        }
        public void run() {
            //2 is for the waiting for user's requests for files from the command line
            if(threadPurpose == 2){

            }

            //3 periodically check the status of their connected peers and respond to status checks from others
            else if(threadPurpose == 3){
                try {

                }catch (Exception e){
                    System.out.println("Attempt to connect to neighbors has failed!");
                }
            }

            //4 listen for and respond to file queries from peers
            else if(threadPurpose == 4){

            }

            //5 listen for and respond to file requests from peers
            else if(threadPurpose == 5){

            }
        }
    }

    /**
     * This is the main method
     * @param args
     * @throws Exception
     */
    public static void main(String args[]) throws Exception {
        ServerSocket welcomeSocket;
        Socket neighbor[];
        System.out.println("The Host is starting!");
        welcomeSocket = new ServerSocket(50241);
        System.out.println("The socket was created");
        thread1 = new Thread(){
            @Override
            public void run() {
                try {
                    while (true) {
                        connections.add(welcomeSocket.accept());
                    }
                }catch(IOException e){
                    System.out.println("The IO Exception occurred in the accepting the connection from other sockets");
                }
                super.run();
            }
        };
        thread1.run();
        neighbor = p2p.connect();
    }

    /**
     *
     * @throws Exception
     */
    public static Socket[] connect() throws Exception{
        BufferedReader fileReader = new BufferedReader((new FileReader("config_neighbors.txt")));
        String neighbor1 = fileReader.readLine();
        String neighbor2 = fileReader.readLine();
        InetAddress host = InetAddress.getLocalHost();
        Socket[] neighbor = new Socket[2];
        String [] neighbor1Arr = neighbor1.split(" ");
        String [] neighbor2Arr = neighbor2.split(" ");
        int portNo1 = Integer.parseInt(neighbor1Arr[1]);
        int portNo2 = Integer.parseInt(neighbor2Arr[1]);
        neighbor[0] = new Socket(neighbor1Arr[0],portNo1, host, 50241);
        neighbor[1] = new Socket(neighbor2Arr[0],portNo2, host, 50242);
        return neighbor;
    }

    public void get(File file) {
        if(isInShared(shared,file)||isInShared(obtained,file)){
            System.out.println("File is already downloaded!");
        }
        else{

        }
    }

    public void requestFile(){

    }

    public static boolean isInShared(File directory, File file){
        if(file == null)
            return false;
        if(file.equals(directory))
            return true;
        return isInShared(directory, file.getParentFile());
    }

    public void leave()throws Exception{
        neighbor1Socket.close();
        neighbor2Socket.close();
    }

    public void exit(ServerSocket welcomeSocket, Socket neighbor1, Socket neighbor2, Socket connectionSocket) throws Exception{
        welcomeSocket.close();
        neighbor1.close();
        neighbor2.close();
        for(int i = 0; i < connections.size(); i++){
            connections.get(i).close();
        }
    }
}
