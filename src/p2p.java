import java.io.*;
import java.net.*;
import java.util.ArrayList;


//My Welcomeport is 50240
//My neighbor connection ports are 50241 and 50242;
//File transfer welcome port is 50243
//Neighbors will recognize neighbor Connections through the message "n(localportNumber)
//FileTransfers will be initiated from port 50243 to the welcoming port

public class p2p{
    private static Thread thread1;
    private static Socket[] outGoingNeighbors = new Socket[2];
    private static Thread welcome;
    private static Thread heartbeat;
    private static Thread neighbor1;
    private static Thread neighbor2;
    private static ServerSocket neighborSocket;
    private static boolean connected = false;
    private static ServerSocket fileTransferSocket;
    private static Socket[] inComingNeighbors = new Socket[2];
    private static File shared = new File("shared").getAbsoluteFile();
    private static File obtained = new File("obtained").getAbsoluteFile();
    private static int queryMultiplier = 0;
    private static int queriesSent = 0;

    static ArrayList<Socket> connections = new ArrayList<Socket>();
    /**
     * This is the main method
     * @param args
     * @throws Exception
     */

    public static void main(String args[]) throws Exception {
        System.out.println("Starting . . .");
        try {
            neighborSocket = new ServerSocket(50240);
            System.out.println("The welcome socket was successfully started");
        }catch(IOException e) {
            System.out.println("An IO exception occurred while creating the welcoming socket");
            e.printStackTrace();
        }
        welcome = new Thread(){
            @Override
            public void run() {

                try{
                    while (true) {
                        connections.add(neighborSocket.accept());
                        BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connections.get(connections.size()-1).getInputStream()));
                        System.out.println("Connection accepted from " + connections.get(connections.size()-1).getInetAddress().toString());
                        String id = inFromClient.readLine();
                        //Checking to see if neighbors are online if yes we accept their request and open a thread for the neighbor
                        if(id.equalsIgnoreCase("50421")||id.equalsIgnoreCase("50242")){
                            if(inComingNeighbors[0] == null){
                                inComingNeighbors[0] = connections.get(connections.size()-1);
                                neighbor1.run();
                            }
                            else if(inComingNeighbors[1] == null){
                                inComingNeighbors[1] = connections.get(connections.size()-1);
                                neighbor2.run();
                            }
                        }
                    }
                }catch(IOException e){
                    System.out.println("The IO Exception occurred in the accepting the connection from other sockets");
                }
                super.run();
            }
        };
        welcome.run();
        BufferedReader userInputs = new BufferedReader(new InputStreamReader(System.in));
        String command = userInputs.readLine();
        if(command.equalsIgnoreCase("connect")){
            outGoingNeighbors = connect();
            connected = true;
            if(connected){
                command = userInputs.readLine();
                if(command.charAt(3)== ' '){
                    String[] getCommand = command.split(" ");
                    String fileName = getCommand[1];
                    query(fileName,outGoingNeighbors[0],outGoingNeighbors[1]);
                }
            }
        }

        neighbor1 = new Thread(){
            DataOutputStream out;
            BufferedReader in;
            @Override
            public void run(){
                try {
                    in = new BufferedReader(new InputStreamReader(inComingNeighbors[0].getInputStream()));
                    out = new DataOutputStream(inComingNeighbors[0].getOutputStream());
                    String command;
                    while ((command = in.readLine()) != null) {
                        if(command.charAt(0)=='Q'){
                            p2p.forwardQuery(inComingNeighbors[0],inComingNeighbors[1],command,fileTransferSocket);
                        }else if(command.charAt(0)=='R'){
                            p2p.recieveResponse(command,inComingNeighbors[1]);
                        }
                    }
                }catch(IOException e){
                    System.out.println("Opening the stream reader has failed you suck at coding");
                }

                super.run();
            }
        };
        neighbor2 = new Thread(){
            DataOutputStream out;
            BufferedReader in;
            @Override
            public void run() {
                try {
                    in = new BufferedReader(new InputStreamReader(inComingNeighbors[1].getInputStream()));
                    out = new DataOutputStream(inComingNeighbors[1].getOutputStream());
                    String command;
                    while ((command = in.readLine()) != null) {
                        if(command.charAt(0)=='Q'){
                            p2p.forwardQuery(inComingNeighbors[1],inComingNeighbors[0],command,fileTransferSocket);
                        }else if(command.charAt(0)=='R'){
                            p2p.recieveResponse(command,inComingNeighbors[0]);
                        }
                    }
                }catch(IOException e){
                    System.out.println("Opening the stream reader has failed you suck at coding");
                }

                super.run();
            }
        };
        heartbeat = new Thread(){
            @Override
            public void run() {
                super.run();
            }
        };





        Thread hit = new Thread(){
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
                DataOutputStream outToServer = new DataOutputStream(neighbor[0].getOutputStream());
                outToServer.writeBytes(Integer.toString(neighbor[0].getLocalPort()));
            }catch (IOException e){
                System.out.println("Attempt to connect to" +  neighbor1address.toString() + "has failed");
            }
            try {
                neighbor[1] = new Socket(neighbor2address, portNo2, host, 50242);
                System.out.println("Attempt to connect to" +  neighbor2address.toString() + "has succeeded!");
                DataOutputStream outToServer = new DataOutputStream(neighbor[1].getOutputStream());
                outToServer.writeBytes(Integer.toString(neighbor[1].getLocalPort()));
            }catch (IOException e){
                System.out.println("Attempt to connect to" +  neighbor2address.toString() + "has failed");
            }
        } catch (Exception e) {
            System.out.println("File reading has failed!");
        }
        return neighbor;
    }
    public static Socket fileTransfer(String responseMessage){
        String[]firstSplit = responseMessage.split(";");
        String[]secondSplit = firstSplit[1].split(" ");
        return null;

    }


    private static InetAddress getPublicHostName() throws Exception{
        URL whatismyip = new URL("http://checkip.amazonaws.com");
        BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
        String ip = in.readLine();
        InetAddress host = InetAddress.getByName(ip);
        return host;
    }

    public static void query(String fileName, Socket neighbor1, Socket neighbor2){
        try {
            boolean have = false;
            BufferedReader fileReader = new BufferedReader((new FileReader("config_sharing.txt")));
            String readLine;
            int numOfLines = 0;
            String[] docArrs = new String[10];
            while((readLine = fileReader.readLine())!=null){
                docArrs[numOfLines]=readLine;
                numOfLines++;
                if(readLine.equalsIgnoreCase(fileName)){
                    have = true;
                }
            }
            if(have){
                System.out.println("You already have the book!");
            }else{
                int queryId = queryMultiplier + queriesSent * 6;
                String queryProtocol = "Q:" + queryId + ";" + fileName;
                try {
                    DataOutputStream neighbor1out = new DataOutputStream(neighbor1.getOutputStream());
                    DataOutputStream neighbor2out = new DataOutputStream(neighbor2.getOutputStream());
                    neighbor1out.writeBytes(queryProtocol);
                    neighbor2out.writeBytes(queryProtocol);
                    System.out.println("Sending a query for " + fileName + "to my peers!");
                    queriesSent++;
                }catch(IOException e){
                    System.out.println("The creation of DataOutputStreams for the query has failed!");
                }
            }
        }catch(IOException e){
            System.out.println("config File sharing is not found!");
        }
    }


    public static void forwardQuery(Socket incoming, Socket outGoing, String query, ServerSocket welcomeSocket){
        String[] querySplitting = query.split(":");
        String[] idFileName = querySplitting[1].split(";");
        int queryId = Integer.parseInt(idFileName[0]);
        String fileName = idFileName[0];
        InetAddress localHost = welcomeSocket.getInetAddress();
        File queriedFile = new File("shared//"+fileName);
        boolean have = false;
        try {
            BufferedReader fileReader = new BufferedReader((new FileReader("config_sharing.txt")));
            String readLine;
            int numOfLines = 0;
            String[] docArrs = new String[10];
            while ((readLine = fileReader.readLine()) != null) {
                docArrs[numOfLines] = readLine;
                numOfLines++;
                if (readLine.equalsIgnoreCase(fileName)) {
                    have = true;
                }
            }
        }catch(Exception e){
            System.out.println("Cannot find config_sharing file");
        }
        if(have){
            int portNumber = welcomeSocket.getLocalPort();
            try {
                localHost = getPublicHostName();
            }catch (Exception e){
                System.out.println("public IP address of host not found");
            }
            String response = "R:" + queryId + ";" + localHost + ":" + "50243"+";"+fileName;
            try {
                DataOutputStream incomingOut = new DataOutputStream(incoming.getOutputStream());
                incomingOut.writeBytes(response);
            }catch(IOException e){
                System.out.println("Attempt to return response to query has failed due to problems in obtaining socket output stream");
            }

        }else{
            try {
                DataOutputStream outGoingOut = new DataOutputStream(outGoing.getOutputStream());
                outGoingOut.writeBytes(query);
            }catch(IOException e){
                System.out.println("Attempt to forward query has failed");
            }
        }
    }

    public static String[] recieveResponse(String response, Socket outGoing){
        String[]splittingResponse = response.split(";");
        String[]obtainingQueryID = splittingResponse[0].split(":");
        int queryID = Integer.parseInt(obtainingQueryID[1]);
        int checkMine = queryID%6;
        if(checkMine == queryMultiplier){
            String[]socketInfo  = splittingResponse[1].split(":");
            return socketInfo;
        }else{
            try {
                DataOutputStream outGoingOut = new DataOutputStream(outGoing.getOutputStream());
                outGoingOut.writeBytes(response);
            }catch(IOException e){
                System.out.println("Error during recieveResponse");
            }
        }
        return null;
    }

    public static Socket initiateFileTransfer(String []socketInfo){
        try {
            InetAddress destinationIP = InetAddress.getByName(socketInfo[0]);
            int portNumber = Integer.parseInt(socketInfo[1]);
            InetAddress localHost = getPublicHostName();
            Socket fileTransferSocket = new Socket(destinationIP,portNumber,localHost,50243);
            return fileTransferSocket;
        }catch(Exception e){
            System.out.println("Invalid hostName found for initiating filetransfer feelsbad man");
            return null;
        }
    }
    public static void sendFile(Socket openedSocket, String fileName){
        File file = new File("shared//"+fileName);
        FileInputStream fileInputer = null;
        BufferedInputStream bufferedInputer = null;
        DataOutputStream writer = null;
        byte[] fileByteArray = new byte [(int)file.length()];
        try {
            fileInputer = new FileInputStream(file);
            bufferedInputer = new BufferedInputStream(fileInputer);
            bufferedInputer.read(fileByteArray,0,fileByteArray.length);
            writer = new DataOutputStream(openedSocket.getOutputStream());
            System.out.println("Attemping to send out file to peer!");
            writer.write(fileByteArray);
            writer.flush();
            System.out.println("File sent successfully");
            bufferedInputer.close();
            writer.close();
            fileInputer.close();
        }catch (Exception e){
            System.out.println("File not found!");
        }
    }

    public static void recieveFile(Socket socket,String fileName){
        BufferedOutputStream inFromServer;
        int bytesRead;
        int current = 0;
        try{
            InputStream socketInput = socket.getInputStream();
            byte[] fileArray = new byte[6022386];
            inFromServer = new BufferedOutputStream(new FileOutputStream("obtained//"+fileName));
            bytesRead=socketInput.read(fileArray,0,fileArray.length);
            current = bytesRead;
            do {
                bytesRead = socketInput.read(fileArray,current,(fileArray.length-current));
                if(bytesRead>=0)current +=bytesRead;
            }while(bytesRead > -1);
            inFromServer.write(fileArray,0,current);
            inFromServer.flush();
            System.out.println("File " + fileName + "has been recieved and stored in obtained folder");
        }catch(IOException e){

        }
    }




    public static void get(File file) {

    }

    public static boolean isInShared(File directory, File file){
        if(file == null)
            return false;
        if(file.equals(directory))
            return true;
        return isInShared(directory, file.getParentFile());
    }

    public void leave()throws Exception{
        outGoingNeighbors[1].close();
        outGoingNeighbors[0].close();
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
