import com.sun.org.apache.xpath.internal.operations.Bool;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class ConsumerThread extends Thread{
    Socket connection;
    ObjectInputStream in;
    ObjectOutputStream out;
    String requestArtist;
    String requestSong;
    List<ConsumerThread> registeredUsers;
    private static ArrayList<ArrayList<ArtistName>> publisherArtists;

    HashMap<ArtistName, PublisherInfo> art_to_pub;//artist name -> publisher
    HashMap<PublisherInfo, PublisherThread> pub_to_pubThread ;//publisher -> publisherThread

    LinkedBlockingQueue<MusicFile> chunkQueue = new LinkedBlockingQueue<MusicFile>();

    public ConsumerThread(Socket socket, List<ConsumerThread> registeredUsers, ArrayList<ArrayList<ArtistName>> publisherArtists,HashMap<ArtistName, PublisherInfo> art_to_pub, HashMap<PublisherInfo, PublisherThread> pub_to_pubThread){
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
                System.out.println(connection.getPort() + "> "  + input);


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
                Request request = new Request(requestArtist, requestSong);

                PublisherInfo publisher = art_to_pub.get(requestArtist);
                PublisherThread thread = pub_to_pubThread.get(publisher);

                thread.addRequest(request, this);

                //sos
                //System.out.println(connection.getInetAddress().getHostAddress() + "> "  + this.request);
                //System.out.println(connection.getPort() + "> " + this.requestArtist);

                MusicFile chunk = chunkQueue.take();

                if(chunk.getTotalChunks()>0){
                    out.writeObject("Found");
                    out.flush();
                    //System.out.println("Consumerthread numOfchunks: " + chunkQueue.peek().getTotalChunks());

                    Iterator<MusicFile> iterator = chunkQueue.iterator();

                    while(iterator.hasNext()) {
                        chunk = iterator.next();
                        out.writeObject(chunk);
                        out.flush();
                        iterator.remove();
                    }
                    //System.out.println("Queue size: " + chunkQueue.size());

                    //System.out.println("SENT");
                }
                else{
                    out.writeObject("Not Found");
                    out.flush();
                }

            }

        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            //e.printStackTrace();
            System.out.println("Consumer disconnected! --> " + connection.getInetAddress().getHostAddress());
            synchronized(registeredUsers) {
                //remove this thread from consumer threads list
                registeredUsers.remove(this);
            }
            return;
        }
    }

    public void addChunks(LinkedBlockingQueue<MusicFile> chunkQueue){
        this.chunkQueue = chunkQueue;
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

}
