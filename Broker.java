import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.*;

public class Broker extends Node implements Runnable, Serializable{
    List<Consumer> registerdUsers = new ArrayList<Consumer>(){};
    List<Publisher> registerdPublishers = new ArrayList<Publisher>();
    ServerSocket providerSocket = null;
    Socket connection = null;
    String message = null;
    String brokerIp;
    int brokerPort;

    public static void main(String[] args) {

        new Broker().openServer();
    }

    @Override
    public void run() {

        try {
            ObjectOutputStream out = new ObjectOutputStream(connection.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(connection.getInputStream());

            out.writeObject("Connection successful!");
            out.flush();

            do {
                try {

                    message = (String) in.readObject();
                    System.out.println(connection.getInetAddress().getHostAddress() + ">" + message);

                } catch (ClassNotFoundException classnot) {
                    System.err.println("Data received in unknown format");
                }
            } while (!message.equals("bb"));

            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openServer() {

        try {

            providerSocket = new ServerSocket(4321);
            System.out.println("Server is open!");

           while (true) {
                connection = providerSocket.accept();
                System.out.println("accept connection");
                Thread thread= new Thread(new Broker(connection));
                System.out.println("after thread");
                thread.start();
                System.out.println("after start");
                //thread.join();
               //System.out.println("after join");
            }//while


        } //try
        catch (IOException ioException) {
            ioException.printStackTrace();
        }

    }//openServer

    public Broker(Socket connection){
        this.connection=connection;
    }

    public Broker(String brokerIp, int brokerPort){
        this.brokerIp=brokerIp;
        this.brokerPort=brokerPort;
    }


    public Broker() {}

    public void calculateKeys(){}
    public Publisher acceptConection(Publisher p){
        return null;
    }
    public Consumer acceptConection(Consumer c){
        return null;
    }
    public void notifyPublisher(String s){}
    public void pull(ArtistName a){}

    public void setBr(){}
}
