import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.net.*;

public class Broker extends Node{

    String ipBroker="127.0.0.1";
    int ConsumersPort=5000;
    int PublishersPort=5001;


    ServerSocket ConsumerServer = null;
    ServerSocket PublisherServer=null;
    Socket connect = null;
    private DataInputStream in = null;
    List<Consumer> registerdUsers = new ArrayList<Consumer>();
    List<Publisher> registerdPublishers = new ArrayList<Publisher>();

    public void createTxt(int ConsumersPort,int PublishersPort)
    {
        try {
            BufferedWriter output = new BufferedWriter(new FileWriter("src\\Broker.txt", true));

            output.write("Broker IP: "+ipBroker+
                    "\t,Broker Port: "+Integer.toString(PublishersPort)+"\n");
            output.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Broker(){}
    public Broker(Socket socket){
        this.connect=socket;
    }


    public void openServer(){
        createTxt(ConsumersPort,PublishersPort);
        try {
            ConsumerServer=new ServerSocket(ConsumersPort);
            System.out.println("Broker> waiting for connection...");
            System.out.println("Available Consumer Port: "+ConsumersPort);
            System.out.println("Available Publisher Port: "+PublishersPort);

            Thread t1= new Thread() {
                public void run() {
                    try {

                        PublisherServer = new ServerSocket(PublishersPort);

                        while(true){
                            Socket socket2= PublisherServer.accept();
                            System.out.println("Publisher accepted! --> " + socket2.getInetAddress().getHostAddress());
                            PublisherThread pt=new PublisherThread(socket2);
                            pt.start();
                        }

                    }
                     catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            t1.start();

            while(true) {
                connect = ConsumerServer.accept();
                System.out.println("Consumer accepted! --> " + connect.getInetAddress().getHostAddress());
                ConsumerThread ct=new ConsumerThread(connect);
                ct.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void calculateKeys(){}
    public Publisher acceptConection(Publisher p){ return null; }
    public Consumer acceptConection(Consumer c){return null; }
    public void notifyPublisher(String s){}
    public void pull(ArtistName a){}


    public static void main(String[] args) throws IOException {
        Broker broker=new Broker();
        broker.openServer();

    }

}
