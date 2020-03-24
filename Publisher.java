import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Publisher extends Node
{
    public void getBrokerList(){}

    public Broker hashTopic(ArtistName artistname){return null; }

    public void push(ArtistName artistname, Value value){}

    public void notifyFailure(Broker broker){}

    public static void main(String[] args){
        Socket socket = null;
        ObjectInputStream input=null;
        ObjectOutputStream out =null;
        DataInputStream input2=null;


        try{
            socket = new Socket("127.0.0.1",5001);
            out = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());

            System.out.println("Connected");
            //takes input from terminal
            input2 = new DataInputStream(System.in);
            String line = "";
            while(!line.equals("Over")){
                try{
                    line = input2.readLine();
                    out.writeObject(line);

                }
                catch(IOException i){
                    System.out.println(i);
                }
            }

        }
        catch(UnknownHostException u)
        {
            System.out.println(u);
        }
        catch(IOException i)
        {
            System.out.println(i);
        }
        //String to read message from input tab
        String line = "";
        //keep reading until "Over" is displayed on the screen
        while(!line.equals("Over")){
            try{
                line = input.readLine();
                out.writeUTF(line);

            }
            catch(IOException i){
                System.out.println(i);
            }
        }


    }
}
