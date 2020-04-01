import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class PublisherThread extends Thread{
    Socket connection;
    ObjectInputStream in;
    ObjectOutputStream out;
    int key;
    List<PublisherThread> registeredPublishers;
    Broker broker;
    private static ArrayList<ArtistName> brokerAstists = new ArrayList<ArtistName>();

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
                    brokerAstists = (ArrayList<ArtistName>) in.readObject();
                    broker.setArtistList(brokerAstists);
                    /*
                    for (ArtistName a : brokerAstists){
                        System.out.println(a.getArtistName());
                        System.out.println(a.getKey());
                    }
                    */
                }


                if (data.equalsIgnoreCase("next")) {

                    //print artist list of each broker
                    /*
                    for (ArrayList<ArtistName> array : broker.getArtistList()) {
                        for (ArtistName a : array){
                            System.out.println(a.getArtistName());
                            System.out.println(a.getKey());
                        }
                    }
                    */

                    while(true) {
                        //System.out.println("xaxax");
                        System.out.print("");
                        if (broker.getNewRequest()) {
                            System.out.println("in if");
                            out.writeObject(broker.getRequestArtist());
                            out.flush();
                            out.writeObject(broker.getRequestSong());
                            out.flush();
                            System.out.println("requested: " + broker.getRequestArtist() +"  , " + broker.getRequestSong());
                            broker.setNewRequest(false);
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