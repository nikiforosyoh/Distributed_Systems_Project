import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ConsumerThread extends Thread{
    Socket connection;
    ObjectInputStream in;
    ObjectOutputStream out;
    String requestArtist;
    String requestSong;
    Broker broker;
    List<ConsumerThread> registeredUsers;

    public ConsumerThread(Socket socket, List<ConsumerThread> registeredUsers, Broker broker){
        connection=socket;
        this.registeredUsers=registeredUsers;
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

                    synchronized(registeredUsers) {
                        //remove this thread from Consumers threads list
                        registeredUsers.remove(this);
                    }
                    return;
                }

                if (data.equalsIgnoreCase("artist names")){

                    for (ArrayList<ArtistName> array : broker.getArtistList()) {
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

                    this.requestArtist = data;
                    broker.setRequestArtist(this.requestArtist);

                    this.requestSong = (String) in.readObject();
                    broker.setRequestSong(this.requestSong);


                    //sos
                    //System.out.println(connection.getInetAddress().getHostAddress() + "> "  + this.request);
                    //System.out.println(connection.getPort() + "> " + this.requestArtist);

                    broker.setNewRequest(true);

            }

        } catch (IOException | ClassNotFoundException  e) {
            //e.printStackTrace();
            System.out.println("Consumer disconnected! --> " + connection.getInetAddress().getHostAddress());
            synchronized(registeredUsers) {
                //remove this thread from consumer threads list
                registeredUsers.remove(this);
            }
            return;

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
