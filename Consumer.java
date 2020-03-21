import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.*;

public class Consumer extends Node {
     Socket requestSocket = null;
     ObjectOutputStream out = null;
     ObjectInputStream in = null;
     String message;
     public String brokerIP="127.0.0.1";
     public int brokerPort = 4321;

     public static void main(String[] args) {
          new Consumer().startConsumer();
     }

     public void startConsumer() {

          try {
               requestSocket = new Socket(brokerIP, brokerPort);

               out = new ObjectOutputStream(requestSocket.getOutputStream());
               in = new ObjectInputStream(requestSocket.getInputStream());

               try {

                    while (true){
                         message = (String) in.readObject();
                         System.out.println("Broker> " + message);

                         out.writeObject("Testing Consumer..");
                         out.flush();

                         // out.writeObject("bb");
                         //out.flush();
                    }

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

               super.disconnect(requestSocket);
          }
     }

     public void register(Broker broker, ArtistName artistName){}

     /*
     public void disconnect(Broker broker, ArtistName artistName){
          try {
               in.close();
               out.close();
               requestSocket.close();
          } catch (IOException ioException) {
               ioException.printStackTrace();
          }
     }
     */

     public void playData(ArtistName artistName, Value value){}
}
