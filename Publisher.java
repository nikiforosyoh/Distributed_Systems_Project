import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class Publisher extends Node
{
    private static String[][] availableBrokers = new String[3][3]; //broker1: brokerIP, brokerPort -> Integer.parseInt(); , Integer.parseInt(broker keys);

    public String artists = "A-N";

    public static void main(String args[]){
        Publisher pub=new Publisher();
        pub.initialization();
        pub.openPublisher();

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
                        // Thread.sleep(1000);
                        if (line.equalsIgnoreCase("key")) {
                            String key = (String) in.readObject();
                            availableBrokers[j][2]=key;
                            System.out.println("broker key: " + key);

                        }

                        while (true) {
                            try {
                                line = new String(input2.readLine());
                                //String line= "kamilopardaleis";
                                if(line.equals("Over")){break;}
                                out.writeObject(line);
                                out.flush();
                               // Thread.sleep(1000);
                                if (line.equalsIgnoreCase("key")) {
                                    String key = (String) in.readObject();
                                    availableBrokers[j][2]=key;
                                    System.out.println("broker key: " + key);

                                }

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
}
