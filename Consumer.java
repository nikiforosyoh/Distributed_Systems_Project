import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;


public class Consumer extends Node {

    private static String BrokerIp;
    private static int BrokerPort;
    private static String[][] availableBrokers = new String[3][2]; //broker1: brokerIP, Integer.parseInt(brokerPort)
    private static ArrayList<ArrayList<String>> artists = new ArrayList<ArrayList<String>>();

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        Consumer cons = new Consumer("127.0.0.1", 5000);
        cons.initialization();
        cons.openConsumer();
    }

    public void initialization() throws NoSuchAlgorithmException {

        System.out.print("Consumer ");
        init(BrokerIp,BrokerPort,availableBrokers);
        Socket socket;
        ObjectInputStream in;
        ObjectOutputStream out;

        //makes a list of each artist of a broker
        try {
            for (int i=0; i<3; i++) {
                socket = new Socket(availableBrokers[i][0], Integer.parseInt(availableBrokers[i][1]));
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());

                out.writeObject("artist names");
                out.flush();

                ArrayList<String> a = new ArrayList<>();
                artists.add(a);
                String name = (String) in.readObject();

                while (!(name.equalsIgnoreCase("end"))) {

                    artists.get(i).add(name);
                    name = (String) in.readObject();
                }

                //print artist names
                System.out.println("\n---Broker " + i + " ---");
                for (String b : artists.get(i)){
                    System.out.println(b);
                }

                in.close();
                out.close();
                socket.close();

            }

        } catch (UnknownHostException u) {
            System.out.println(u);
        } catch (IOException i) {
            System.out.println(i);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void openConsumer() throws IOException {

        ObjectInputStream in;
        DataInputStream input; //takes input from terminal
        ObjectOutputStream out;
        Socket socket;
        String requestArtist;
        String requestSong;
        Boolean artistFound = false;

        do {
            ArrayList<byte[]> chunkQueue = new ArrayList<>();//queue for chunks
            input = new DataInputStream(System.in);
            requestArtist = input.readLine();

            try {
                //search all artist lists for this artist name
                for (int i = 0; i < 3; i++) {
                    for (String a : artists.get(i)) {

                        //if artist name is found open connection with the responsible broker
                        if (requestArtist.equalsIgnoreCase(a)) {
                            artistFound = true;
                            socket = new Socket(availableBrokers[i][0], Integer.parseInt(availableBrokers[i][1]));
                            out = new ObjectOutputStream(socket.getOutputStream());
                            in = new ObjectInputStream(socket.getInputStream());

                            System.out.println("Consumer Connected: " + socket);

                            //sends user's request
                            out.writeObject(requestArtist.trim());
                            out.flush();

                            requestSong = input.readLine();
                            out.writeObject(requestSong.trim());
                            out.flush();
                            String response=(String) in.readObject();

                            if(response.equalsIgnoreCase("Found")) {

                                System.out.println("----------");
                                System.out.println("Song found");
                                //then takes the song from broker
                                MusicFile chunk=(MusicFile) in.readObject();
                                chunkQueue.add(chunk.getMusicFileExtract());
                                System.out.println("received: " + chunk.getChunkNumber());

                                for (int ch = 0; ch < chunk.getTotalChunks()-1; ch++) {

                                    chunk = (MusicFile) in.readObject();
                                    System.out.println("received: " + chunk.getChunkNumber());
                                    chunkQueue.add(chunk.getMusicFileExtract());

                                }

                                ReadMp3Files readMp3Files = new ReadMp3Files();
                                byte[] mp3File = readMp3Files.recreateFile(chunkQueue);

                                FileOutputStream stream = new FileOutputStream(requestSong +"_new.mp3");
                                stream.write(mp3File);

                                System.out.println("Song received successfully! ");

                            }
                            else if(response.equalsIgnoreCase("Not Found")){
                                System.out.println("The song doesn't exist!");
                            }else{
                                System.out.println("Problem");
                            }

                            socket.close();
                            break;
                        }

                    }
                }
                if(!artistFound) {
                    System.out.println("Sorry.. There are no songs of this Artist..");
                    System.out.println("Try an other one..");
                }
                artistFound = false;

            } catch (IOException e) {
                e.printStackTrace();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }while(!requestArtist.equalsIgnoreCase("over"));

    }

    public Consumer(String BrokerIp, int BrokerPort){
        this.BrokerIp=BrokerIp;
        this.BrokerPort=BrokerPort;
    }

}
