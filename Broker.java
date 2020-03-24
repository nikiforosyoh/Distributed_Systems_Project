import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.net.*;

public class Broker extends Node{
    ServerSocket ConsumerServer = null;
    ServerSocket PublisherServer=null;
    Socket connect = null;
    private DataInputStream in = null;
    List<Consumer> registerdUsers = new ArrayList<Consumer>();
    List<Publisher> registerdPublishers = new ArrayList<Publisher>();

    public Broker(){}
    public Broker(Socket socket){
        this.connect=socket;
    }


    public void openServer(){
        try {
            ConsumerServer=new ServerSocket(5000);
            System.out.println("Server:waiting for client connection....");
            Thread t1= new Thread() {
                public void run() {
                    try {
                        try {
                            PublisherServer = new ServerSocket(5001);

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
