import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.*;

public class Broker extends Node{
    List<Consumer> registerdUsers = new ArrayList<Consumer>(){};
    List<Publisher> registerdPublishers = new ArrayList<Publisher>();
    ServerSocket providerSocket = null;
    Socket connection = null;
    String message = null;

    public static void main(String[] args) {

        new Broker().openServer();
    }


    public void openServer() {

        try {

            providerSocket = new ServerSocket(4321);

            while (true) {
                connection = providerSocket.accept();
                ObjectOutputStream out = new ObjectOutputStream(connection.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(connection.getInputStream());

                out.writeObject("Connection successful!");
                out.flush();

                do {
                    try {

                        message = (String) in.readObject();
                        System.out.println(connection.getInetAddress().getHostAddress() + ">" + message);

                    }
                    catch (ClassNotFoundException classnot) {
                        System.err.println("Data received in unknown format");
                    }
                } while (!message.equals("Byee!!!!"));


                in.close();
                out.close();
                //connection.close();

            }//while

        } //try
        catch (IOException ioException) {
            ioException.printStackTrace();
        }



    }//openServer

    public void calculateKeys(){}
    public Publisher acceptConection(Publisher p){
        return null;
    }
    public Consumer acceptConection(Consumer c){
        return null;
    }
    public void notifyPublisher(String s){}
    public void pull(ArtistName a){}
    
}
