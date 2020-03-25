import java.io.*;
import java.net.Socket;

public class ConsumerThread extends Thread{
    Socket connection;
    ObjectInputStream in;
    ObjectOutputStream out;


    public ConsumerThread(Socket socket){
        connection=socket;
    }

    public void run(){
        try {
            out = new ObjectOutputStream(connection.getOutputStream());
            in = new ObjectInputStream(connection.getInputStream());



            while (true){
                String data=(String) in.readObject();
                System.out.println(connection.getInetAddress().getHostAddress() + "> "  + data);
            }


        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


}