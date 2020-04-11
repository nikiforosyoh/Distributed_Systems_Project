import java.io.*;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class ConsumerThread extends Thread{

    private static int N; //Num of brokers
    private Socket connection;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private String requestArtist;
    private String requestSong;
    private List<ConsumerThread> registeredUsers;
    private static ArrayList<ArrayList<ArtistName>> publisherArtists;

    HashMap<ArtistName, Info> art_to_pub;//artist name -> publisher
    HashMap<Info, PublisherThread> pub_to_pubThread ;//publisher -> publisherThread

    LinkedBlockingQueue<MusicFile> chunkQueue = new LinkedBlockingQueue<MusicFile>();

    public ConsumerThread(final int N,Socket socket, List<ConsumerThread> registeredUsers, ArrayList<ArrayList<ArtistName>> publisherArtists,HashMap<ArtistName, Info> art_to_pub, HashMap<Info, PublisherThread> pub_to_pubThread){
        this.N=N;
        connection=socket;
        this.registeredUsers=registeredUsers;
        this.publisherArtists=publisherArtists;
        this.art_to_pub=art_to_pub;
        this.pub_to_pubThread=pub_to_pubThread;
    }

    public void run(){
        try {
            out = new ObjectOutputStream(connection.getOutputStream());
            in = new ObjectInputStream(connection.getInputStream());

            while (true){

                String input = (String) in.readObject();
                //sos
                //System.out.println(connection.getInetAddress().getHostAddress() + "> "  + data);
                //System.out.println(connection.getPort() + "> "  + input);


                if(input.equalsIgnoreCase("brokers")){
                    sendBrokerInfo(out);
                    connection.close();

                    synchronized(registeredUsers) {
                        //remove this thread from Consumers threads list
                        registeredUsers.remove(this);
                    }
                    return;
                }

                if (input.equalsIgnoreCase("artist names")){

                    for (ArrayList<ArtistName> array : publisherArtists) {
                        for (ArtistName a : array){
                            out.writeObject(a.getArtistName());
                            out.flush();
                        }
                    }

                    out.writeObject("end");
                    out.flush();

                    connection.close();

                    synchronized(registeredUsers) {
                        //remove this thread from Consumers threads list
                        registeredUsers.remove(this);
                    }
                    return;

                }

                this.requestArtist = input;
                this.requestSong = (String) in.readObject();
                Request request = new Request(requestArtist, requestSong, this );

                System.out.println("Consumer connected! --> " + connection.getInetAddress().getHostAddress());

                Info publisher = art_to_pub.get(new ArtistName(requestArtist));
                PublisherThread thread = pub_to_pubThread.get(publisher);

                //pass request to publisher thread
                thread.addRequest(request);

                //sos
                System.out.println(connection.getInetAddress().getHostAddress() + " (Consumer Thread)> " + request.getRequestArtist() + " - " + request.getRequestSong());


                //waits for chunks
                MusicFile chunk = chunkQueue.take();

                System.out.println("(Consumer Thread)> chunks received from publisher thread");

                //if chunk is not fake
                if(chunk.getTotalChunks()>0){
                    out.writeObject("Found");
                    out.flush();

                    //send chunks
                    out.writeObject(chunk);
                    out.flush();

                    while(chunk.getTotalChunks()>0){
                        chunk=chunkQueue.take();
                        out.writeObject(chunk);
                        System.out.println("SENT: " + chunk.getChunkNumber());
                        out.flush();
                    }
                    System.out.println("(Consumer Thread)> all chunks sent");

                }
                else{
                    out.writeObject("Not Found");
                    out.flush();
                }



            }

        } catch (IOException | ClassNotFoundException | InterruptedException | NoSuchAlgorithmException e) {
            //e.printStackTrace();
            System.out.println("Consumer disconnected! --> " + connection.getInetAddress().getHostAddress());
            synchronized(registeredUsers) {
                //remove this thread from consumer threads list
                registeredUsers.remove(this);
            }
            return;
        }
    }

    public void addChunks(MusicFile chunk){
        this.chunkQueue.add(chunk);
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

            for (int i=0; i<N; i++){
                line = reader.readLine();

                ip=line.trim().substring(line.indexOf(":")+1,line.indexOf(",")).trim();
                line = line.trim().substring(line.indexOf(",")+1);
                line = line.trim().substring(line.indexOf(",")+1);
                port = line.substring(line.indexOf(":")+1).trim();
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
        ConsumerThread that = (ConsumerThread) o;
        return Objects.equals(connection, that.connection) &&
                Objects.equals(in, that.in) &&
                Objects.equals(out, that.out) &&
                Objects.equals(requestArtist, that.requestArtist) &&
                Objects.equals(requestSong, that.requestSong) &&
                Objects.equals(registeredUsers, that.registeredUsers) &&
                Objects.equals(art_to_pub, that.art_to_pub) &&
                Objects.equals(pub_to_pubThread, that.pub_to_pubThread) &&
                Objects.equals(chunkQueue, that.chunkQueue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(connection, in, out, requestArtist, requestSong, registeredUsers, art_to_pub, pub_to_pubThread, chunkQueue);
    }
}
