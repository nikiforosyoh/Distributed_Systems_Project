import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

public class Consumer extends Node {

    public void register(Broker broker, ArtistName artistName){
    }

    public void disconnect(Broker broker, ArtistName artistName){

    }
    public void playData(ArtistName artistName, Value value){

    }

    public static void main(String[] args) throws IOException {
        Socket socket = null;
        ObjectInputStream in=null;
        ObjectOutputStream out =null;
        DataInputStream input2=null;


        try{
            socket = new Socket("127.0.0.1",5000);
            out = new ObjectOutputStream(socket.getOutputStream());
            in= new ObjectInputStream(socket.getInputStream());

            System.out.println("Consumer Connected: " + socket);
            //takes input from terminal
            input2 = new DataInputStream(System.in);

            String request = "";
            while(!request.equalsIgnoreCase("over")){
                try{
                    request = input2.readLine();
                    out.writeObject(request);

                }
                catch(IOException i){
                    System.out.println(i);
                }
            }
            socket.close();

        }
        catch(UnknownHostException u)
        {
            System.out.println(u);
        }
        catch(IOException i)
        {
            System.out.println(i);
        }

    }


}
