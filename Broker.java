import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class Broker extends Node {

    private String BrokerIP;
    private int ConsumersPort;
    private int PublishersPort;
    private int key; //Hash(IP+port)
    private LinkedBlockingQueue<Request> requestQueue = new LinkedBlockingQueue<Request>();//queue for consumer requests
    private HashMap<ArtistName, PublisherInfo> art_to_pub = new HashMap<ArtistName, PublisherInfo>();//artist name -> publisher
    private HashMap<PublisherInfo, PublisherThread> pub_to_pubThread = new HashMap<PublisherInfo, PublisherThread>();//publisher -> publisherThread

    private static ArrayList<ArrayList<ArtistName>> publisherArtists = new ArrayList<ArrayList<ArtistName>>();

    private ServerSocket ConsumerServer = null;
    private ServerSocket PublisherServer=null;
    private Socket connectCon = null;
    private List<ConsumerThread> registeredUsers = new ArrayList<ConsumerThread>();
    private List<PublisherThread> registeredPublishers = new ArrayList<PublisherThread>();


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

                        //thread that handles Publisher
                        while(true){
                            Socket connectPub= PublisherServer.accept();

                            //sos
                            //System.out.println("Publisher connected! --> " + connectPub.getInetAddress().getHostAddress());
                            System.out.println("Publisher connected! --> " + connectPub.getPort());
                            PublisherThread pt = new PublisherThread(connectPub, key, registeredPublishers, publisherArtists,art_to_pub, pub_to_pubThread );
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

            //thread that handles Consumer
            while(true) {
                connectCon = ConsumerServer.accept();
                //sos
                //System.out.println("Consumer connected! --> " + connectCon.getInetAddress().getHostAddress());
                System.out.println("Consumer connected! --> " + connectCon.getPort());
                ConsumerThread ct=new ConsumerThread(connectCon,registeredUsers,publisherArtists, art_to_pub, pub_to_pubThread );
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