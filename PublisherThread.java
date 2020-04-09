import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;

public class PublisherThread extends Thread{
    private Socket connection;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private int key;
    private List<PublisherThread> registeredPublishers;
    private static ArrayList<ArrayList<ArtistName>> publisherArtists;
    private static ArrayList<ArtistName> brokerArtists = new ArrayList<ArtistName>();
    private String pubIp;
    private int pubPort;
    private LinkedBlockingQueue<Request> requestQueue = new LinkedBlockingQueue<Request>();//queue for consumer requests

    private HashMap<ArtistName, PublisherInfo> art_to_pub ;//artist name -> publisher
    private HashMap<PublisherInfo, PublisherThread> pub_to_pubThread;//publisher -> publisherThread

    public PublisherThread(Socket socket, int key, List<PublisherThread> registeredPublishers, ArrayList<ArrayList<ArtistName>> publisherArtists,HashMap<ArtistName, PublisherInfo> art_to_pub, HashMap<PublisherInfo, PublisherThread> pub_to_pubThread){
        connection=socket;
        this.key=key;
        this.registeredPublishers=registeredPublishers;
        this.publisherArtists = publisherArtists;
        this.art_to_pub=art_to_pub;
        this.pub_to_pubThread=pub_to_pubThread;

    }

    public void run(){

        try {
            out = new ObjectOutputStream(connection.getOutputStream());
            in = new ObjectInputStream(connection.getInputStream());

            //initialization server part
            while (true) {
                String data = (String) in.readObject();
                //sos
                //System.out.println(connection.getInetAddress().getHostAddress() + "> "  + data);
                System.out.println(connection.getPort() + "> " + data);

                if (data.equalsIgnoreCase("brokers")) {
                    sendBrokerInfo(out);
                    connection.close();

                    synchronized (registeredPublishers) {
                        //remove this thread from publisher threads list
                        registeredPublishers.remove(this);
                    }
                    return;

                }
                if (data.equalsIgnoreCase("key")) {
                    out.writeObject(Integer.toString(key));
                    out.flush();

                    connection.close();

                    synchronized (registeredPublishers) {
                        //remove this thread from publisher threads list
                        registeredPublishers.remove(this);
                    }
                    return;

                }
                if (data.equalsIgnoreCase("artist names")) {

                    //sos
                    //System.out.println("Publisher connected! --> " + connectPub.getInetAddress().getHostAddress());
                    //System.out.println("Publisher connected! --> " + connection.getPort());

                    //Artist names
                    brokerArtists = (ArrayList<ArtistName>) in.readObject();
                    publisherArtists.add(brokerArtists);

                    //Publisher's info
                    pubIp = (String) in.readObject();
                    pubPort = (int) in.readObject();
                    PublisherInfo publisher = new PublisherInfo(pubIp,pubPort);

                    //Artist to Publisher HashMap
                    for(ArtistName artist : brokerArtists){
                        art_to_pub.put(artist, publisher);
                    }
                    pub_to_pubThread.put(publisher , this);
                    break;
                }

            }

            //close connection as server
            in.close();
            out.close();
            connection.close();

            //client part
            while(true) {
                System.out.print("");

                //waits here for a request to come
                Request request = requestQueue.take();
                //sos
                //System.out.println(connection.getInetAddress().getHostAddress() + "> "  + data);
                //System.out.println(connection.getPort() + "> " + request.getRequestArtist() + " - " + request.getRequestSong());

                requestHandle(request);
            }//while true

        } catch (IOException e) {
            //e.printStackTrace();
            System.out.println("Publisher disconnected! --> " + connection.getInetAddress().getHostAddress());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void requestHandle(Request request){

        Thread workingThread = new Thread(() -> {

            System.out.println("THREAD CREATED");
            LinkedBlockingQueue<MusicFile> chunkQueue = new LinkedBlockingQueue<MusicFile>();
            Socket pubSocket;
            ObjectInputStream in;
            ObjectOutputStream out;
            try {
                pubSocket = new Socket(pubIp, pubPort);
                out = new ObjectOutputStream(pubSocket.getOutputStream());
                in = new ObjectInputStream(pubSocket.getInputStream());

                out.writeObject(request.getRequestArtist());
                out.flush();
                out.writeObject(request.getRequestSong());
                out.flush();
                System.out.println("requested: " + request.getRequestArtist() + "  , " + request.getRequestSong());

                String found = (String) in.readObject();

                if (found.equalsIgnoreCase("Found")) {
                    MusicFile chunk = (MusicFile) in.readObject();
                    System.out.println("Num of chunks: " + chunk.getTotalChunks());
                    chunkQueue.add(chunk);
                    System.out.println("Received: " + chunk.getChunkNumber() + " chunk");

                    for (int ch = 0; ch < chunk.getTotalChunks() - 1; ch++) {
                        chunk = (MusicFile) in.readObject();
                        chunkQueue.add(chunk);
                        System.out.println("Received: " + chunk.getChunkNumber() + " chunk");
                    }
                    //Disconnect Publisher
                    in.close();
                    out.close();
                    pubSocket.close();

                } else if (found.equalsIgnoreCase("Not Found")) {
                    System.out.println("SONG NOT FOUND");
                    chunkQueue.add(new MusicFile(-1));

                }else{
                    System.out.println("PROBLEM");
                }

                System.out.println("Chunk Queue size: " + chunkQueue.size());
                request.getThread().addChunks(chunkQueue);
                System.out.println("chunks passed to ConsumerThread");


            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });//thread
        workingThread.start();

    }

    public void addRequest( Request request){
        requestQueue.add(request);
    }

    //load file
    private static void sendBrokerInfo(ObjectOutputStream out) {
        File f = null;
        BufferedReader reader = null;
        String ip;
        String port;
        String line;

        try {
            f = new File("src\\Broker.txt");
        } catch (NullPointerException e) {
            System.err.println("File not found.");
        }

        try {
            reader = new BufferedReader(new FileReader(f));
        } catch (FileNotFoundException e) {
            System.err.println("Error opening file!");
        }

        try {

            for (int i=0; i<3; i++){
                line = reader.readLine();

                ip=line.trim().substring(line.indexOf(":")+1,line.indexOf(",")).trim();
                line = line.trim().substring(line.indexOf(",")+1);
                port = line.trim().substring(line.indexOf(":")+1,line.indexOf(",")).trim();

                out.writeObject(ip);
                out.flush();
                out.writeObject(port);
                out.flush();
            }

        }catch (IOException e) {
            System.out.println	("Error reading line ...");
        }

        try {
            reader.close();
        }
        catch (IOException e) {
            System.err.println("Error closing file.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PublisherThread thread = (PublisherThread) o;
        return key == thread.key &&
                pubPort == thread.pubPort &&
                Objects.equals(connection, thread.connection) &&
                Objects.equals(in, thread.in) &&
                Objects.equals(out, thread.out) &&
                Objects.equals(registeredPublishers, thread.registeredPublishers) &&
                Objects.equals(pubIp, thread.pubIp) &&
                Objects.equals(requestQueue, thread.requestQueue) &&
                Objects.equals(art_to_pub, thread.art_to_pub) &&
                Objects.equals(pub_to_pubThread, thread.pub_to_pubThread);
    }

    @Override
    public int hashCode() {
        return Objects.hash(connection, in, out, key, registeredPublishers, pubIp, pubPort, requestQueue, art_to_pub, pub_to_pubThread);
    }
}