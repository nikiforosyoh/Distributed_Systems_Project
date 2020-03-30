import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.net.*;

public class Broker extends Node{

    String BrokerIP;
    int ConsumersPort;
    int PublishersPort;
    int key; //Hash(IP+port)
    String request; //input from Consumer
    boolean newRequest = false; //notify for new Consumer request
    Broker b= this;

    ServerSocket ConsumerServer = null;
    ServerSocket PublisherServer=null;
    Socket connectCon = null;
    private DataInputStream in = null;
    List<ConsumerThread> registeredUsers = new ArrayList<ConsumerThread>();
    List<PublisherThread> registeredPublishers = new ArrayList<PublisherThread>();

    public static void main(String[] args) throws IOException {
        Broker broker=new Broker("127.0.0.1",5004,5005 );
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
                            calculateKeys();
                            System.out.println("Publisher connected! --> " + connectPub.getInetAddress().getHostAddress());
                            System.out.println("broker key: " + key);
                            PublisherThread pt=new PublisherThread(connectPub, key, registeredPublishers, b);
                            registeredPublishers.add(pt);
                            pt.start();
                        }

                    }
                    catch (IOException | NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                }
            };
            t1.start();

            //thread that handles Consumers
            while(true) {
                connectCon = ConsumerServer.accept();
                System.out.println("Consumer connected! --> " + connectCon.getInetAddress().getHostAddress());
                ConsumerThread ct=new ConsumerThread(connectCon,key,registeredUsers, this);
                registeredUsers.add(ct);
                ct.start();

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Hash(IP+port) to calculate broker's key
    public void calculateKeys() throws NoSuchAlgorithmException {
        TestHashing hash = new TestHashing();
        key = Integer.parseInt( hash.getMd5(BrokerIP + Integer.toString(PublishersPort)) );
    }

    public void setRequest(String request){
        this.request=request;
    }

    public String getRequest(){
        return this.request;
    }

    public void setNewRequest(boolean newRequest) {
        this.newRequest = newRequest;
    }

    public boolean getNewRequest(){
        return newRequest;
    }


    public Publisher acceptConection(Publisher p){
        return null;
    }

    public Consumer acceptConection(Consumer c){
        return null;
    }

    public void notifyPublisher(String s){}

    public void pull(ArtistName a){}

    public void createTxt(int ConsumersPort,int PublishersPort) {
        try {
            BufferedWriter output = new BufferedWriter(new FileWriter("src\\Broker.txt", true));

            output.write("Broker IP: "+BrokerIP+
                    "\t,Broker Port: "+PublishersPort+"\n");
            output.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}