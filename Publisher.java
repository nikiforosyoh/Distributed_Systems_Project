import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class Publisher extends Node implements  Serializable{

    private static ArrayList<ArtistName> artists = new ArrayList<ArtistName>();
    private int keysCount=0;
    private static String[][] availableBrokers = new String[3][3]; //broker1: brokerIP, brokerPort -> Integer.parseInt(); , Integer.parseInt(broker keys);
    char start; //to split artists to publishers
    char end;
    int BrokerPort;
    String BrokerIp;
    //list of artists for each broker
    private static ArrayList<ArtistName> broker0Astists = new ArrayList<ArtistName>();
    private static ArrayList<ArtistName> broker1Astists = new ArrayList<ArtistName>();
    private static ArrayList<ArtistName> broker2Astists = new ArrayList<ArtistName>();
    boolean ready=false;


    public static void main(String args[]) throws NoSuchAlgorithmException {
        Publisher pub=new Publisher('m', 'z', "127.0.0.1", 5001);
        pub.initialization();
        pub.openPublisher();

    }

    public Publisher(char start, char end, String BrokerIp, int BrokerPort){
        this.start=Character.toLowerCase(start);
        this.end=Character.toLowerCase(end);
        this.BrokerIp=BrokerIp;
        this.BrokerPort=BrokerPort;
    }

    //fill artists ArrayList
    //receive broker's information
    public void initialization() throws NoSuchAlgorithmException {

        ReadMp3Files readMp3Files = new ReadMp3Files();

        for ( String a : readMp3Files.getPublisherArtistList(start, end)){
            ArtistName newArtist = new ArtistName(a);
            artists.add(newArtist);
        }

        //print all artists of this Publisher
        /*
        for (ArtistName a : artists){
            System.out.println(a.getArtistName());
        }
        */

        System.out.print("Publisher ");
        init(BrokerIp,BrokerPort,availableBrokers);

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
                    //DataInputStream input2 = null;

                    try {
                        socket = new Socket(availableBrokers[j][0], Integer.parseInt(availableBrokers[j][1]));
                        out = new ObjectOutputStream(socket.getOutputStream());
                        in = new ObjectInputStream(socket.getInputStream());

                        System.out.println("Publisher Connected: " + socket);

                        //takes input from terminal
                        //input2 = new DataInputStream(System.in);

                        String line="key";
                        out.writeObject(line);
                        out.flush();

                        if (line.equalsIgnoreCase("key")) {
                            String key = (String) in.readObject();
                            availableBrokers[j][2]=key;
                            keysCount++;
                        }

                        while(true){
                            System.out.print("");
                            if(ready){
                                out.writeObject("artist names");
                                out.flush();
                                sendArtists(socket, out);
                                break;
                            }

                        }


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

        //sort keys
        while(true){
            System.out.print("");
            if(keysCount==3){
                availableBrokers=sortKeys(availableBrokers);
                //distribute artists to brokers depending on hash(artistName) and hash(IP+port)
                distributeArtists();
                ready= true;
                break;
            }
        }
        //print available Broker's info: IP, port, key
        /*
        System.out.println("BrokersIp \tPorts \tkeys ");
        for (int k=0; k<3; k++){
            for (int l=0; l<3; l++){
                System.out.print(availableBrokers[k][l]);
                System.out.print("\t");
            }
            System.out.print("\n");
        }
        */

    }

    //distribute artists to brokers depending on hash(artistName) and hash(IP+port)
    private void distributeArtists() {

        for ( ArtistName artist : artists) {

            if (artist.getKey() < Integer.parseInt(availableBrokers[0][2]) || artist.getKey() >= Integer.parseInt(availableBrokers[2][2]) ) {
                // o 1os
                broker0Astists.add(artist);
            } else if (artist.getKey() < Integer.parseInt(availableBrokers[1][2])) {
                // o 2os
                broker1Astists.add(artist);
            } else if (artist.getKey() < Integer.parseInt(availableBrokers[2][2])) {
                // o 3os
                broker2Astists.add(artist);
            }
        }

        /*
        System.out.println("\n 1st broker:");
        for (ArtistName a : broker0Astists){
            System.out.println(a.getArtistName());
        }
        System.out.println("\n 2nd broker:");
        for (ArtistName a : broker1Astists){
            System.out.println(a.getArtistName());
        }
        System.out.println("\n 3rd broker:");
        for (ArtistName a : broker2Astists){
            System.out.println(a.getArtistName());
        }
        */

    }

    public void getBrokerList(){}

    public Broker hashTopic(ArtistName artistname){return null; }

    public void push(ArtistName artistname, Value value){}

    public void notifyFailure(Broker broker){}

    //sent Artists to brokers
    public void sendArtists(Socket socket, ObjectOutputStream out) throws IOException {

        //sos
        //if(availableBrokers[0][0].equalsIgnoreCase(socket.getInetAddress().getHostAddress()) )

        if(availableBrokers[0][1].equalsIgnoreCase(String.valueOf(socket.getPort())) ) {
            out.writeObject(broker0Astists);
            out.flush();
        }
        if(availableBrokers[1][1].equalsIgnoreCase(String.valueOf(socket.getPort())) ) {
            out.writeObject(broker1Astists);
            out.flush();
        }
        if(availableBrokers[2][1].equalsIgnoreCase(String.valueOf(socket.getPort())) ) {
            out.writeObject(broker2Astists);
            out.flush();
        }
    }

    //sort keys in availableBrokers array
    public String[][] sortKeys(String[][] a) {
        String temp;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3 - i - 1; j++)
                if (Integer.parseInt(a[j][2]) > Integer.parseInt(a[j + 1][2])) {
                    for (int k = 0; k < 3; k++) {
                        temp = a[j][k];
                        a[j][k] = a[j + 1][k];
                        a[j + 1][k] = temp;
                    }
                }
        }
        return a;
    }

}