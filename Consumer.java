import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;


public class Consumer extends Node {

    private static String BrokerIp;
    private static int BrokerPort;
    private static String[][] availableBrokers = new String[3][2]; //broker1: brokerIP, Integer.parseInt(brokerPort)
    private static ArrayList<ArrayList<String>> artists = new ArrayList<ArrayList<String>>();


    public void register(Broker broker, ArtistName artistName){
    }

    public void disconnect(Broker broker, ArtistName artistName){

    }
    public void playData(ArtistName artistName, Value value){

    }

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        Consumer cons = new Consumer("127.0.0.1", 5000);
        cons.initialization();
        cons.openConsumer();
    }

    public void initialization() throws NoSuchAlgorithmException {

        System.out.print("Consumer ");
        init(BrokerIp,BrokerPort,availableBrokers);
        Socket socket = null;
        ObjectInputStream in = null;
        ObjectOutputStream out = null;
        DataInputStream input2 = null;

        try {
            for (int i=0; i<3; i++) {
                socket = new Socket(availableBrokers[i][0], Integer.parseInt(availableBrokers[i][1]));
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());

                out.writeObject("artist names");
                out.flush();

                ArrayList<String> a = new ArrayList<>();
                artists.add(a);
                String name = (String) in.readObject();


                while (!(name.equalsIgnoreCase("end"))) {

                    artists.get(i).add(name);
                    name = (String) in.readObject();
                }

                //print artist names
                System.out.println("\n---Broker " + i + " ---");
                for (String b : artists.get(i)){
                    System.out.println(b);
                }

                in.close();
                out.close();
                socket.close();

            }

        } catch (UnknownHostException u) {
            System.out.println(u);
        } catch (IOException i) {
            System.out.println(i);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
    public Consumer(String BrokerIp, int BrokerPort){
        this.BrokerIp=BrokerIp;
        this.BrokerPort=BrokerPort;
    }

    public void openConsumer() throws IOException {

        for (int i=0; i<3; i++) {

            Socket socket = null;
            ObjectInputStream in = null;
            ObjectOutputStream out = null;
            DataInputStream input2 = null;


            try {
                socket = new Socket(availableBrokers[i][0], Integer.parseInt(availableBrokers[i][1]));
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());

                System.out.println("Consumer Connected: " + socket);
                //takes input from terminal
                input2 = new DataInputStream(System.in);

                String request = "";
                while (!request.equalsIgnoreCase("over")) {
                    try {
                        request = input2.readLine();
                        out.writeObject(request);

                    } catch (IOException u) {
                        System.out.println(u);
                    }
                }
                socket.close();

            } catch (UnknownHostException u) {
                System.out.println(u);
            } catch (IOException e) {
                System.out.println(e);
            }
        }

    }

    //get  ip, port of every broker
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
