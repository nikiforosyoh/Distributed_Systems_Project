import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.UnsupportedTagException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class Publisher extends Node {

    private ReadMp3Files readMp3Files = new ReadMp3Files();
    private static ArrayList<ArtistName> artists = new ArrayList<ArtistName>();
    private static ArrayList<ArrayList<String>> songsOfArtists = new ArrayList<ArrayList<String>>(); //Songs of all songs of each artist
    private static String[][] availableBrokers = new String[3][3]; //broker1: brokerIP, brokerPort -> Integer.parseInt(); , Integer.parseInt(broker keys);
    private char start; //to split artists to publishers
    private char end;
    private int BrokerPort;
    private String BrokerIp;
    private ServerSocket PublisherServer=null;
    private String pubIp;
    private int pubPort;
    //list of artists for each broker
    private static ArrayList<ArtistName> broker0Artists = new ArrayList<ArtistName>();
    private static ArrayList<ArtistName> broker1Artists = new ArrayList<ArtistName>();
    private static ArrayList<ArtistName> broker2Artists = new ArrayList<ArtistName>();


    public static void main(String args[]) throws NoSuchAlgorithmException, InvalidDataException, IOException, UnsupportedTagException {
        Publisher pub=new Publisher('k', 'z', "127.0.0.1", 4090, "127.0.0.1", 2008);//kathe fora allazoyme to pub port
        System.out.println(Character.toUpperCase(pub.start) + "-" + Character.toUpperCase(pub.end) );
        pub.initialization();
        pub.exchangeInfo();
        pub.openPublisher();

    }

    public Publisher(char start, char end, String BrokerIp, int BrokerPort, String pubIp, int serverPort){
        this.start=Character.toLowerCase(start);
        this.end=Character.toLowerCase(end);
        this.BrokerIp=BrokerIp;
        this.BrokerPort=BrokerPort;
        this.pubIp = pubIp;
        this.pubPort=serverPort;
    }

    //fill artists ArrayList
    //receive broker's information
    public void initialization() throws NoSuchAlgorithmException, InvalidDataException, IOException, UnsupportedTagException {

        for ( String a : readMp3Files.getPublisherArtistList(start, end)){
            ArtistName newArtist = new ArtistName(a);
            artists.add(newArtist);
        }

        songsOfArtists = readMp3Files.getListSongs("Songs",readMp3Files.getPublisherArtistList(start, end));

        System.out.print("Publisher ");
        init(BrokerIp,BrokerPort,availableBrokers);

    }

    //client part
    public void exchangeInfo() throws IOException {
        Socket socket;
        ObjectInputStream in=null;
        ObjectOutputStream out=null;

        for (int i = 0; i < 3; i++) {
            try {
                socket = new Socket(availableBrokers[i][0], Integer.parseInt(availableBrokers[i][1]));
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());
                System.out.println("Publisher Connected: " + socket);
                //take keys from brokers
                out.writeObject("key");
                out.flush();
                String key = (String) in.readObject();
                availableBrokers[i][2] = key;

                in.close();
                out.close();
                socket.close();

            } catch (IOException u) {
                System.out.println(u);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        //sort keys
        availableBrokers = sortKeys(availableBrokers);
        //distribute artists to brokers depending on hash(artistName) and hash(IP+port)
        distributeArtists();
        for (int i = 0; i < 3; i++) {

            try {
                socket = new Socket(availableBrokers[i][0], Integer.parseInt(availableBrokers[i][1]));
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());
                System.out.println("Publisher Connected: " + socket);

                //send artist to brokers
                out.writeObject("artist names");
                out.flush();
                sendArtists(socket, out);

                //Send Publisher's info
                out.writeObject(pubIp);
                out.flush();
                out.writeObject(pubPort);
                out.flush();

                in.close();
                out.close();
                socket.close();

            } catch (IOException u) {
                System.out.println(u);
            }
        }

    }

    //server part
    public void openPublisher() throws IOException {

        PublisherServer = new ServerSocket(pubPort);

        while (true) {

            Socket brokerRequest = PublisherServer.accept();
            //sos
            //System.out.println("Publisher connected! --> " + connectPub.getInetAddress().getHostAddress());
            Thread requestListener = new Thread() {
                public void run() {
                    System.out.println("THREAD CREATED");
                    try {
                        ObjectInputStream in;
                        ObjectOutputStream out;

                        out = new ObjectOutputStream(brokerRequest.getOutputStream());
                        in = new ObjectInputStream(brokerRequest.getInputStream());

                        String requestArtist = (String) in.readObject();
                        String requestSong = (String) in.readObject();

                        System.out.println("Consumer's Artist request: " + requestArtist);
                        System.out.println("Consumer's Song request: " + requestSong);
                        boolean songExists = false;
                        for (int x = 0; x < artists.size(); x++) {
                            if (artists.get(x).getArtistName().equalsIgnoreCase(requestArtist)) {
                                for (String a : songsOfArtists.get(x)) {
                                    if (requestSong.equalsIgnoreCase(a)) {
                                        System.out.println("Song found!!!");
                                        songExists = true;
                                        out.writeObject("Found");
                                        out.flush();
                                        //sends chunks to broker
                                        push(requestSong, out);
                                        break;
                                    }
                                }
                            }
                        }
                        if (!songExists) {
                            System.out.println("Song not found!!!");
                            out.writeObject("Not Found");
                            out.flush();
                        }
                        in.close();
                        out.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (UnsupportedTagException e) {
                        e.printStackTrace();
                    } catch (InvalidDataException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                }//run
            };

            requestListener.start();
        }//while true
    }


    //distribute artists to brokers depending on hash(artistName) and hash(IP+port)
    private static void distributeArtists() {

        for ( ArtistName artist : artists) {

            if (artist.getKey() < Integer.parseInt(availableBrokers[0][2]) || artist.getKey() >= Integer.parseInt(availableBrokers[2][2]) ) {
                // o 1os
                broker0Artists.add(artist);
            } else if (artist.getKey() < Integer.parseInt(availableBrokers[1][2])) {
                // o 2os
                broker1Artists.add(artist);
            } else if (artist.getKey() < Integer.parseInt(availableBrokers[2][2])) {
                // o 3os
                broker2Artists.add(artist);
            }
        }

    }

    //sends chunks to broker
    public void push(String requestSong, ObjectOutputStream out ) throws InvalidDataException, IOException, UnsupportedTagException {
        ArrayList<MusicFile> musicFiles = new ArrayList<MusicFile>();
        musicFiles = readMp3Files.getMusicFiles(requestSong);

        int totalChunks = musicFiles.size();

        for(MusicFile m: musicFiles){
            m.setTotalChunks(totalChunks);
            out.writeObject(m);
            out.flush();
            System.out.println("SENT: " + m.getChunkNumber());
        }
        System.out.println("ALL SENT");
    }

    //sent Artists to brokers
    public void sendArtists(Socket socket, ObjectOutputStream out) throws IOException {

        //sos
        //if(availableBrokers[0][0].equalsIgnoreCase(socket.getInetAddress().getHostAddress()) )

        if(availableBrokers[0][1].equalsIgnoreCase(String.valueOf(socket.getPort())) ) {
            out.writeObject(broker0Artists);
            out.flush();
        }
        if(availableBrokers[1][1].equalsIgnoreCase(String.valueOf(socket.getPort())) ) {
            out.writeObject(broker1Artists);
            out.flush();
        }
        if(availableBrokers[2][1].equalsIgnoreCase(String.valueOf(socket.getPort())) ) {
            out.writeObject(broker2Artists);
            out.flush();
        }
    }

    //sort keys in availableBrokers array
    public static String[][] sortKeys(String[][] a) {
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