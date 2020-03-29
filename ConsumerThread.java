import java.io.*;
import java.net.Socket;

public class ConsumerThread extends Thread{
    Socket connection;
    ObjectInputStream in;
    ObjectOutputStream out;
    int key;
    String request= "";
    Broker broker;

    public ConsumerThread(Socket socket,int key, Broker broker){
        connection=socket;
        this.key=key;
        this.broker=broker;

    }

    public void run(){
        try {
            out = new ObjectOutputStream(connection.getOutputStream());
            in = new ObjectInputStream(connection.getInputStream());

            while (true){
                this.request = (String) in.readObject();
                broker.setRequest(this.request);
                broker.setNewRequest(true);
                System.out.println(broker.getNewRequest());
                System.out.println(connection.getInetAddress().getHostAddress() + "> "  + this.request);
            }

        } catch (IOException | ClassNotFoundException  e) {
            //e.printStackTrace();
            System.out.println("Consumer disconnected! --> " + connection.getInetAddress().getHostAddress());
        }
    }

}
