import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class p2p{
    private static Thread thread1;
    private static Socket[] neighbors = new Socket[2];
    private static Thread welcome;
    private static Thread input;
    private static Thread sending;
    private static Thread query;
    private static Thread hit;
    private static ServerSocket welcomeSocket;
    static ArrayList<Socket> connections = new ArrayList<Socket>();
    /**
     * This is the main method
     * @param args
     * @throws Exception
     */
    public static void main(String args[]) throws Exception {
        welcomeSocket = new ServerSocket(50240);
        System.out.println("The socket was created");
        BufferedReader userInputs = new BufferedReader(new InputStreamReader(System.in));
        String command = userInputs.readLine();
        if(command.equalsIgnoreCase("connect")){
            neighbors = connect();
        }
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
    public static Socket[] connect(){
        InetAddress host;
        String neighbor1;
        String neighbor2;
        Socket[] neighbor = new Socket[2];
        String[] neighbor1Arr;
        String[] neighbor2Arr;
        int portNo1;
        int portNo2;

        try {
            BufferedReader fileReader = new BufferedReader((new FileReader("config_neighbors.txt")));
            neighbor1 = fileReader.readLine();
            neighbor2 = fileReader.readLine();
            neighbor1Arr = neighbor1.split(" ");
            neighbor2Arr = neighbor2.split(" ");
            portNo1 = Integer.parseInt(neighbor1Arr[1]);
            portNo2 = Integer.parseInt(neighbor2Arr[1]);
            host = p2p.getPublicHostName();
            InetAddress neighbor1address = InetAddress.getByName(neighbor1Arr[0]);
            InetAddress neighbor2address = InetAddress.getByName(neighbor2Arr[0]);
            System.out.println("Attempting to connect to neighbor " + neighbor1address.toString() + " on port number " + portNo1);
            try{
                neighbor[0] = new Socket(neighbor1address,portNo1, host, 50241);
                System.out.println("Attempt to connect to" +  neighbor1address.toString() + "has succeeded!");
            }catch (IOException e){
                System.out.println("Attempt to connect to" +  neighbor1address.toString() + "has failed");
            }
            try {
                neighbor[1] = new Socket(neighbor2address, portNo2, host, 50242);
                System.out.println("Attempt to connect to" +  neighbor2address.toString() + "has succeeded!");
            }catch (IOException e){
                System.out.println("Attempt to connect to" +  neighbor2address.toString() + "has failed");
            }
        } catch (Exception e) {
            System.out.println("File reading has failed!");
        }


        //System.out.println(host.toString());


        return neighbor;
    }

    private static InetAddress getPublicHostName() throws Exception{
        URL whatismyip = new URL("http://checkip.amazonaws.com");
        BufferedReader in = new BufferedReader(new InputStreamReader(
                whatismyip.openStream()));

        String ip = in.readLine();
        InetAddress host = InetAddress.getByName(ip);
        return host;
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
        neighbors[1].close();
        neighbors[0].close();
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
