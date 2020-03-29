import java.io.*;
import java.net.Socket;

public class ConsumerThread extends Thread{
    Socket connection;
    ObjectInputStream in;
    ObjectOutputStream out;
    int key;

    public ConsumerThread(Socket socket,int key){
        connection=socket;
        this.key=key;
    }

    public void run(){
        try {
            out = new ObjectOutputStream(connection.getOutputStream());
            in = new ObjectInputStream(connection.getInputStream());

            while (true){
                String request=(String) in.readObject();
                System.out.println(connection.getInetAddress().getHostAddress() + "> "  + request);

            }

        } catch (IOException | ClassNotFoundException  e) {
            //e.printStackTrace();
            System.out.println("Consumer disconnected! --> " + connection.getInetAddress().getHostAddress());
        }
    }


}
