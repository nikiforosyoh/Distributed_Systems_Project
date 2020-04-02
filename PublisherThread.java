import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class PublisherThread extends Thread{
    Socket connection;
    ObjectInputStream in;
    ObjectOutputStream out;
    int key;
    boolean sendNext=true;
    List<PublisherThread> registeredPublishers;
    Broker broker;
    private static ArrayList<ArtistName> brokerArtists = new ArrayList<ArtistName>();

    public PublisherThread(Socket socket, int key, List<PublisherThread> registeredPublishers, Broker broker){
        connection=socket;
        this.key=key;
        for (PublisherThread publisherThread : this.registeredPublishers = registeredPublishers) {

        }
        ;
        this.broker=broker;
    }


    public void run(){

        try {
            out = new ObjectOutputStream(connection.getOutputStream());
            in = new ObjectInputStream(connection.getInputStream());

            while (true){
                String data=(String) in.readObject();
                //sos
                //System.out.println(connection.getInetAddress().getHostAddress() + "> "  + data);
                System.out.println(connection.getPort() + "> "  + data);

                if(data.equalsIgnoreCase("brokers")){
                    sendBrokerInfo(out);
                    connection.close();

                    synchronized(registeredPublishers) {
                        //remove this thread from publisher threads list
                        registeredPublishers.remove(this);
                    }
                    return;

                }
                if (data.equalsIgnoreCase("key")){
                    out.writeObject(Integer.toString(key));
                    out.flush();
                }
                if (data.equalsIgnoreCase("artist names")){
                    brokerArtists = (ArrayList<ArtistName>) in.readObject();
                    broker.setArtistList(brokerArtists);

                }
                if (data.equalsIgnoreCase("next")) {

                    while(true) {
                        System.out.print("");
                        if (broker.getNewRequest()) {
                            synchronized(broker) {

                                for(int i=0;i<broker.getArtistList().size();i++){
                                    for (ArtistName art : broker.getArtistList().get(i)){
                                        if( (art.getArtistName().equalsIgnoreCase(broker.getRequestArtist()))) {
                                            if (this == registeredPublishers.get(i)) {
                                                broker.setNewRequest(false);
                                                out.writeObject(broker.getRequestArtist());
                                                out.flush();
                                                out.writeObject(broker.getRequestSong());
                                                out.flush();
                                                System.out.println("requested: " + broker.getRequestArtist() + "  , " + broker.getRequestSong());
                                                String found=(String) in.readObject();
                                                if(found.equalsIgnoreCase("Found")) {
                                                    broker.setFound(true);
                                                    int numOfChunks = (int) in.readObject();
                                                    broker.setNumOfChunks(numOfChunks);
                                                    System.out.println("Num of chunks received : "+broker.getNumOfChunks());
                                                    /*
                                                    for (int ch = 0; ch < numOfChunks; ch++) {
                                                        MusicFile chunk = (MusicFile) in.readObject();
                                                    }
                                                    */
                                                }
                                                else{
                                                    broker.setFound(false);
                                                }

                                                broker.setNewResponse(true);//o publisher apanthse
                                                sendNext=false;
                                            }
                                        }
                                    }
                                }
                                if(sendNext){
                                    out.writeObject("null");
                                    out.flush();
                                    out.writeObject("null");
                                    out.flush();
                                }
                                else {
                                    sendNext=true;
                                }
                            }
                            break;
                        }
                    }
                }

            }
        } catch (IOException e) {
            //e.printStackTrace();
            System.out.println("Publisher disconnected! --> " + connection.getInetAddress().getHostAddress());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
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

}