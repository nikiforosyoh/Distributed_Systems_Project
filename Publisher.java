import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Publisher extends Node
{

    private static ArrayList<ArtistName> artists = new ArrayList<ArtistName>();
    private int keysCount=0;
    private static String[][] availableBrokers = new String[3][3]; //broker1: brokerIP, brokerPort -> Integer.parseInt(); , Integer.parseInt(broker keys);
    char start;
    char end;
    public static void main(String args[]){
        Publisher pub=new Publisher('A', 'N');
        pub.initialization();
        pub.openPublisher();

    }

    public Publisher(char start, char end){
        this.start=Character.toLowerCase(start);
        this.end=Character.toLowerCase(end);
    }


    public void initialization(){
        Socket socket = null;
        ObjectInputStream in=null;
        ObjectOutputStream out =null;

        try{
            socket = new Socket("127.0.0.1",5001);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            System.out.println("Publisher Connected: " + socket);

            out.writeObject("brokers");
            out.flush();

            availableBrokers = getBrokerInfo(in, availableBrokers );
            System.out.println("Brokers' information received");
            in.close();
            out.close();
            socket.close();

        }
        catch(UnknownHostException u) {
            System.out.println(u);
        }
        catch(IOException i) {
            System.out.println(i);
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void openPublisher(){

        //three threads for connections with all three brokers


        for(int i=0;i<3;i++) {

            final int j=i;
            Thread t1 = new Thread() {
                public void run() {
                    Socket socket = null;
                    ObjectInputStream in = null;
                    ObjectOutputStream out = null;
                    DataInputStream input2 = null;

                    try {
                        socket = new Socket(availableBrokers[j][0], Integer.parseInt(availableBrokers[j][1]));
                        out = new ObjectOutputStream(socket.getOutputStream());
                        in = new ObjectInputStream(socket.getInputStream());

                        System.out.println("Publisher Connected: " + socket);

                        //takes input from terminal
                        input2 = new DataInputStream(System.in);

                        String line="key";
                        out.writeObject(line);
                        out.flush();

                        if (line.equalsIgnoreCase("key")) {
                            String key = (String) in.readObject();
                            availableBrokers[j][2]=key;
                            keysCount++;
                        }

                        //print stored info for brokers
                        /*
                        if(j==2){
                            System.out.println("BrokersIp \tPorts \tkeys ");
                            for (int k=0; k<3; k++){
                                for (int l=0; l<3; l++){
                                    System.out.print(availableBrokers[k][l]);
                                    System.out.print("\t");
                                }
                                System.out.print("\n");
                            }
                        }


                         */

                        while (true) {
                            try {

                                out.writeObject("next");
                                out.flush();

                                String request = (String) in.readObject();
                                System.out.println("Consumer's request: " + request);

                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                        }

                    } catch (UnknownHostException u) {
                        System.out.println(u);
                    } catch (IOException i) {
                        System.out.println(i);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            };
            t1.start();
        }
        while(true){
            System.out.print("");
            if(keysCount==3){
                availableBrokers=sorting(availableBrokers);
                break;
            }


        }
        System.out.println("BrokersIp \tPorts \tkeys ");
        for (int k=0; k<3; k++){
            for (int l=0; l<3; l++){
                System.out.print(availableBrokers[k][l]);
                System.out.print("\t");
            }
            System.out.print("\n");
        }

    }


    public void getBrokerList(){}

    public Broker hashTopic(ArtistName artistname){return null; }

    public void push(ArtistName artistname, Value value){}

    public void notifyFailure(Broker broker){}

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
    public String[][] sorting(String[][] a) {
        String temp;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3 - i - 1; j++)
                if (Integer.parseInt(a[j][2]) > Integer.parseInt(a[j + 1][2])) {
                    temp = a[j][2];
                    a[j][2] = a[j+1][2];
                    a[j+1][2] = temp;
                    temp = a[j][1];
                    a[j][1] = a[j+1][1];
                    a[j+1][1] = temp;
                    temp = a[j][0];
                    a[j][0] = a[j+1][0];
                    a[j+1][0] = temp;
                }

        }
        return a;
    }
}