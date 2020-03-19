import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.*;

public class Publisher extends Node {
    Socket requestSocket = null;
    ObjectOutputStream out = null;
    ObjectInputStream in = null;
    String message;
    public String brokerIP="127.0.0.1";
    public int brokerPort = 4321;

    public static void main(String[] args) {
        new Publisher().startPublisher();
    }

    private void startPublisher() {
        try {
            requestSocket = new Socket(brokerIP, brokerPort);

            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());

            try {

                message = (String) in.readObject();
                System.out.println("Broker> " + message);

                out.writeObject("Testing Publisher..");
                out.flush();

                out.writeObject("Byee!!!!");
                out.flush();

            } catch (ClassNotFoundException classNot){
                System.err.println("Data received in unknown format");
            }

            in.close();
            out.close();

        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }finally {
            disconnect(requestSocket);
        }
    }

    public void getBrokerList()
    {

    }

    public Broker hashTopic(ArtistName artistname) {
        return null;
    }

    public void push(ArtistName artistname, Value value) {

    }

    public void notifyFailure(Broker broker) {

    }

  
}
