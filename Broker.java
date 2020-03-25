import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.net.*;

public class Broker extends Node{

    String ipBroker="127.0.0.1";
    int brokerPort1=5001;
    int brokerPort2=5002;


    ServerSocket ConsumerServer = null;
    ServerSocket PublisherServer=null;
    Socket connect = null;
    private DataInputStream in = null;
    List<Consumer> registerdUsers = new ArrayList<Consumer>();
    List<Publisher> registerdPublishers = new ArrayList<Publisher>();

    public void createTxt(int port1,int port2)
    {
        try {
            BufferedWriter output = new BufferedWriter(new FileWriter("src\\Broker.txt", true));

            output.write("Broker IP: "+ipBroker+
                    " Broker ports : "+Integer.toString(port1)+" , "+Integer.toString(port2)+"\n");
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
        createTxt(brokerPort1,brokerPort2);
        try {
            ConsumerServer=new ServerSocket(brokerPort1);
            System.out.println("Server:waiting for client connection....");
            Thread t1= new Thread() {
                public void run() {
                    try {
                        try {
                            PublisherServer = new ServerSocket(brokerPort2);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        while(true){
                            Socket socket2= PublisherServer.accept();
                            System.out.println("Publisher accepted");
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
                System.out.println("Consumer accepted!!");
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
