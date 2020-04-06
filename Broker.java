import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Broker extends Node {

    String BrokerIP;
    int ConsumersPort;
    int PublishersPort;
    int key; //Hash(IP+port)
    String requestArtist; //input Artist from Consumer
    String requestSong; //input Songs from Consumer
    Queue<Request> requestQueue = new LinkedList<>();//queue for consumer requests
    Queue<MusicFile> chunkQueue = new LinkedList<>();//queue for chunks

    boolean newRequest = false; //notify for new Consumer request
    Broker b= this;
    boolean newResponse;
    boolean found;


    ServerSocket ConsumerServer = null;
    ServerSocket PublisherServer=null;
    Socket connectCon = null;
    private DataInputStream in = null;
    List<ConsumerThread> registeredUsers = new ArrayList<ConsumerThread>();
    List<PublisherThread> registeredPublishers = new ArrayList<PublisherThread>();
    private static ArrayList<ArrayList<ArtistName>> publisherArtists = new ArrayList<ArrayList<ArtistName>>();


    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        Broker broker=new Broker("127.0.0.1",5004
                                                      ,5005 ); //des to txt prin baleis tis port!!!!!!! <-----
        broker.calculateKeys();
        System.out.println("broker key: " + broker.key);
        broker.openServer();

    }

    public Broker(){}
    public Broker(String BrokerIP, int ConsumersPort, int PublishersPort ){
        this.BrokerIP=BrokerIP;
        this.ConsumersPort= ConsumersPort;
        this.PublishersPort=PublishersPort;
    }

    public void openServer(){

        //createTxt(ConsumersPort,PublishersPort);
        try {
            ConsumerServer=new ServerSocket(ConsumersPort);
            System.out.println("Broker> waiting for connection...");
            System.out.println("Available Consumer Port: "+ConsumersPort);
            System.out.println("Available Publisher Port: "+PublishersPort);

            Thread t1= new Thread() {
                public void run() {
                    try {

                        PublisherServer = new ServerSocket(PublishersPort);

                        //thread that handles Publishers
                        while(true){
                            Socket connectPub= PublisherServer.accept();

                            //sos
                            //System.out.println("Publisher connected! --> " + connectPub.getInetAddress().getHostAddress());
                            System.out.println("Publisher connected! --> " + connectPub.getPort());
                            PublisherThread pt=new PublisherThread(connectPub, key, registeredPublishers, b);
                            registeredPublishers.add(pt);
                            pt.start();
                        }

                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            t1.start();

            //thread that handles Consumers
            while(true) {
                connectCon = ConsumerServer.accept();
                //sos
                //System.out.println("Consumer connected! --> " + connectCon.getInetAddress().getHostAddress());
                System.out.println("Consumer connected! --> " + connectCon.getPort());
                ConsumerThread ct=new ConsumerThread(connectCon,registeredUsers,this);
                registeredUsers.add(ct);
                ct.start();

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Hash(IP+port) to calculate broker's key
    public void calculateKeys() throws NoSuchAlgorithmException {
        Hash hash = new Hash();
        key = Integer.parseInt( hash.getMd5(BrokerIP + Integer.toString(PublishersPort)) );
    }

    public void setRequestArtist(String requestArtist){
        this.requestArtist=requestArtist;
    }

    public String getRequestArtist(){
        return this.requestArtist;
    }

    public void setNewRequest(boolean newRequest) {
        this.newRequest = newRequest;
    }

    public boolean getNewRequest(){
        return newRequest;
    }

    public void setRequestSong(String requestSong){
        this.requestSong=requestSong;
    }

    public String getRequestSong(){
        return this.requestSong;
    }

    public void setNewResponse(boolean response){
        this.newResponse=response;
    }

    public boolean getNewResponse(){
        return newResponse;
    }

    public void setFound(boolean f){
        this.found=f;
    }

    public boolean getFound(){
        return found;
    }

    //methods for synchronization of requests
    public void addToRequestQueue(Request request){
        this.requestQueue.add(request);
    }

    public Request removeFromRequestQueue(){
        return requestQueue.poll();
    }

    public Request peekFromRequestQueue(){
        return requestQueue.peek();
    }

    //methods for synchronization of chunks ..synchronized
    public void addToChunkQueue(MusicFile chunk){
        this.chunkQueue.add(chunk);
    }

    public MusicFile removeFromChunkQueue(){
        return chunkQueue.poll();
    }

    public MusicFile peekFromChunkQueue(){
        return chunkQueue.peek();
    }

    public void setArtistList(ArrayList<ArtistName> pubArt){
        publisherArtists.add(pubArt);
    }

    public ArrayList<ArrayList<ArtistName>> getArtistList(){
        return publisherArtists;
    }

    public void pull(ArtistName a){}

    public void createTxt(int ConsumersPort,int PublishersPort) {
        try {
            BufferedWriter output = new BufferedWriter(new FileWriter("src\\Broker.txt", true));

            output.write("Broker IP: " + BrokerIP +
                    "\t,Publisher Port: " + PublishersPort +
                    "\t,Consumer Port: " + ConsumersPort + "\n");
            output.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}