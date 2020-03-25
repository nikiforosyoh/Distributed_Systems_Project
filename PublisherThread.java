import java.io.*;
import java.net.Socket;

public class PublisherThread extends Thread{
    Socket connection;
    ObjectInputStream in;
    ObjectOutputStream out;

    public PublisherThread(Socket socket){
        connection=socket;
    }

    public void run(){


        try {
            out = new ObjectOutputStream(connection.getOutputStream());
            in = new ObjectInputStream(connection.getInputStream());


            while (true){
                String data=(String) in.readObject();
                System.out.println(connection.getInetAddress().getHostAddress() + "> "  + data);

                if(data.equalsIgnoreCase("brokers")){
                    String[] brokers= ReadBrokerTxt();
                    for (int i=0; i<3; i++){
                        out.writeObject(brokers[i]);
                    }

                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    //load file
    private static String[] ReadBrokerTxt() {
        File f = null;
        BufferedReader reader = null;
        String line;
        String[] brokers = new String[3];

        String broker1 = "";
        String broker2 = "";
        String broker3 = "";

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

            broker1 = line;
            line = reader.readLine();
            broker2 = line;
            line = reader.readLine();
            broker3 = line;

            brokers[0] = broker1;
            brokers[1] = broker2;
            brokers[2] = broker3;

        }catch (IOException e) {
            System.out.println	("Error reading line ...");
        }

        try {
            reader.close();
        }
        catch (IOException e) {
            System.err.println("Error closing file.");
        }
        return brokers;
    }

}