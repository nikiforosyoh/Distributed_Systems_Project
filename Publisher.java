import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class Publisher extends Node
{
    private static String[][] availableBrokers = new String[3][3]; //broker1: brokerIP, brokerPort -> Integer.parseInt(); , broker keys

    public String artists = "A-N";

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
            socket = new Socket("127.0.0.1",5003);
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
                        availableBrokers = getBrokerInfo(input, availableBrokers );
                        System.out.println("Brokers' information received");

                        //Printing availableBrokers Array
                        /*
                        for(int i=0; i<3; i++){
                            System.out.println(availableBrokers[i][0]);
                            System.out.println(availableBrokers[i][1]);
                        }
                        */
                    }

                    if(line.equalsIgnoreCase("key")){
                        String key = (String) input.readObject();
                        System.out.println("broker key: " + key );

                    }

                }
                catch (ClassNotFoundException e) {
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

    public static String[][] getBrokerInfo(ObjectInputStream input, String[][] availableBrokers) throws IOException, ClassNotFoundException {
        String info = "";
        for (int i=0; i<3; i++){
            info = (String) input.readObject();

            availableBrokers[i][0] = info.trim().substring(info.indexOf(":")+1,info.indexOf(",")).trim();
            info = info.trim().substring(info.indexOf(",")+1);
            availableBrokers[i][1] = info.trim().substring(info.indexOf(":")+1).trim();

        }

        return availableBrokers;
    }
}
