import java.io.*;
import java.net.Socket;
import java.util.List;

public class ConsumerThread extends Thread{
    Socket connection;
    ObjectInputStream in;
    ObjectOutputStream out;
    int key;
    String request= "";
    Broker broker;
    List<ConsumerThread> registeredUsers;

    public ConsumerThread(Socket socket,int key, List<ConsumerThread> registeredUsers, Broker broker){
        connection=socket;
        this.key=key;
        this.registeredUsers=registeredUsers;
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
                System.out.println(connection.getInetAddress().getHostAddress() + "> "  + this.request);
            }

        } catch (IOException | ClassNotFoundException  e) {
            //e.printStackTrace();
            System.out.println("Consumer disconnected! --> " + connection.getInetAddress().getHostAddress());
            synchronized(registeredUsers) {
                //remove this thread from consumer threads list
                registeredUsers.remove(this);
            }
            return;

        }
    }

}
