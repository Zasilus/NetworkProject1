import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.SortedMap;

public class p2p{
    private static Thread thread1;
    Socket neighbor1Socket;
    Socket neighbor2Socket;
    private static Thread welcome;
    private static Thread input;
    private static Thread sending;
    private static Thread query;
    private static Thread hit;
    static ArrayList<Socket> connections = new ArrayList<Socket>();
    /**
     * This is the main method
     * @param args
     * @throws Exception
     */
    public static void main(String args[]) throws Exception {
        ServerSocket welcomeSocket;
        welcomeSocket = new ServerSocket(50241);
        System.out.println("The socket was created");
        welcome = new Thread(){
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
        welcome.run();
        hit = new Thread(){
            @Override
            public void run() {
                super.run();
            }
        };

        query = new Thread(){
            @Override
            public void run() {
                super.run();
            }
        };


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

//    public void get(File file) {
//        if(isInShared(shared,file)||isInShared(obtained,file)){
//            System.out.println("File is already downloaded!");
//        }
//        else{
//
//        }
//    }

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
