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
            socket = new Socket("127.0.0.1",5005);
            out = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());

            System.out.println("Publisher Connected: " + socket);

            //takes input from terminal
            input2 = new DataInputStream(System.in);
            String line = "";
            while(!line.equals("Over")){
                try{
                    line = input2.readLine();
                    out.writeObject(line);

                    if(line.equalsIgnoreCase("brokers")) {
                        System.out.println("Broker> ");
                        String data = "";
                        for (int i=0; i<3; i++){
                            data = (String) input.readObject();
                            System.out.println("\t" + data);

                        }
                    }

                }
                catch(IOException i){
                    System.out.println(i);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
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


    }
}
