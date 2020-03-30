import java.io.*;
import java.net.Socket;
import java.util.List;

public class PublisherThread extends Thread{
    Socket connection;
    ObjectInputStream in;
    ObjectOutputStream out;
    int key;
    List<PublisherThread> registeredPublishers;
    Broker broker;

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
                System.out.println(connection.getInetAddress().getHostAddress() + "> "  + data);

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

                if (data.equalsIgnoreCase("next")) {
                    while(true) {
                        System.out.print("");
                        if (broker.getNewRequest()) {
                            out.writeObject(broker.getRequest());
                            out.flush();
                            broker.setNewRequest(false);
                            break;
                        }
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Publisher disconnected! --> " + connection.getInetAddress().getHostAddress());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    //load file
    private static void sendBrokerInfo(ObjectOutputStream out) {
        File f = null;
        BufferedReader reader = null;
        String line;
        String[] brokers = new String[3];

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
            line = reader.readLine();

            brokers[0] = line;
            line = reader.readLine();
            brokers[1] = line;
            line = reader.readLine();
            brokers[2] = line;

            for (int i=0; i<3; i++){
                out.writeObject(brokers[i]);
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
