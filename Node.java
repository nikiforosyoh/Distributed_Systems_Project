
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Node {
    //fields
    List<Broker> brokers=new ArrayList<Broker>();

    //receive broker's information
    public void init(String BrokerIp, int BrokerPort, String[][] availableBrokers){
        Socket socket = null;
        ObjectInputStream in=null;
        ObjectOutputStream out =null;

        try{
            socket = new Socket(BrokerIp, BrokerPort);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            System.out.println("Connected: " + socket);

            out.writeObject("brokers");
            out.flush();

            availableBrokers = getBrokerInfo(in, availableBrokers );
            System.out.println("Brokers' information received");
            in.close();
            out.close();
            socket.close();
        }
        catch(UnknownHostException u) {
            System.out.println(u);
        }
        catch(IOException i) {
            System.out.println(i);
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void disconnect(Socket requestSocket){
        try {

            requestSocket.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    //get  ip, port of every broker
    public static String[][] getBrokerInfo(ObjectInputStream input, String[][] availableBrokers) throws IOException, ClassNotFoundException {
        String info = "";
        for (int i=0; i<3; i++){
            for (int j=0; j<2; j++) {
                availableBrokers[i][j] = (String) input.readObject();
            }
        }
        return availableBrokers;
    }


}
