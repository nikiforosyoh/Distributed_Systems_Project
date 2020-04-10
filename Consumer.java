import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;


public class Consumer extends Node {

    private static String BrokerIp;
    private static int BrokerPort;
    private static String[][] availableBrokers = new String[3][2]; //broker1: brokerIP, Integer.parseInt(brokerPort)
    private static ArrayList<ArrayList<String>> artists = new ArrayList<ArrayList<String>>();

    private HashMap<String,Info> brokerArtists = new HashMap<String,Info>();

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        Consumer cons = new Consumer("127.0.0.1", 5000);
        cons.initialization();
        cons.openConsumer();
    }

    public void initialization(){

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
                Info broker = new Info(availableBrokers[i][0], Integer.parseInt(availableBrokers[i][1]));
                while (!(name.equalsIgnoreCase("end"))) {

                    brokerArtists.put(name.toLowerCase(), broker);

                    artists.get(i).add(name);
                    name = (String) in.readObject();
                }

                //print artist names
                System.out.println("\n---- Broker " + (i+1) + " ----");
                for (String b : artists.get(i)){
                    System.out.println(b);
                }

                in.close();
                out.close();
                socket.close();

            }
            System.out.println("-----------------\n");

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

        do {
            ArrayList<byte[]> chunkList = new ArrayList<>();//queue for chunks
            input = new DataInputStream(System.in);
            System.out.print("Artist name: ");
            requestArtist = input.readLine();

            if (requestArtist.equalsIgnoreCase("over")) {
                break;
            }

            try {
                //search for this artist name
                if (brokerArtists.containsKey(requestArtist.toLowerCase())) {

                    Info broker = brokerArtists.get(requestArtist.toLowerCase());
                    //connect with the responsible broker
                    socket = new Socket(broker.getIP(), broker.getPort());
                    out = new ObjectOutputStream(socket.getOutputStream());
                    in = new ObjectInputStream(socket.getInputStream());

                    System.out.println("Consumer Connected: " + socket);

                    //sends user's request
                    out.writeObject(requestArtist.trim());
                    out.flush();

                    System.out.print("Song title: ");
                    requestSong = input.readLine();
                    out.writeObject(requestSong.trim());
                    out.flush();
                    String response = (String) in.readObject();

                    if (response.equalsIgnoreCase("Found")) {

                        System.out.println("-----------------");
                        System.out.println("Song found");

                        //takes the song from broker
                        MusicFile chunk = (MusicFile) in.readObject();
                        chunkList.add(chunk.getMusicFileExtract());
                        System.out.println("received: " + chunk.getChunkNumber() + " chunk");
                        for (int ch = 0; ch < chunk.getTotalChunks() - 1; ch++) {

                            chunk = (MusicFile) in.readObject();
                            System.out.println("received: " + chunk.getChunkNumber() + " chunk");
                            chunkList.add(chunk.getMusicFileExtract());

                        }

                        byte[] mp3File = recreateFile(chunkList);

                        FileOutputStream stream = new FileOutputStream(requestSong + "(new).mp3");
                        stream.write(mp3File);

                        System.out.println("Song received successfully! ");
                        System.out.println("-----------------");

                    } else if (response.equalsIgnoreCase("Not Found")) {
                        System.out.println("The song doesn't exist!");
                    } else {
                        System.out.println("Oops! A problem occurred. Try again..");
                    }

                    in.close();
                    out.close();
                    socket.close();
                   
                }else{
                    System.out.println("Sorry.. There are no songs of this Artist..");
                    System.out.println("Try an other one..");
                }



            } catch(IOException e){
                e.printStackTrace();
            } catch(NumberFormatException e){
                e.printStackTrace();
            } catch(ClassNotFoundException e){
                e.printStackTrace();
            }


        }while(true);

    }

    public Consumer(String BrokerIp, int BrokerPort){
        this.BrokerIp=BrokerIp;
        this.BrokerPort=BrokerPort;
    }

    //recreate mp3 file
    public byte[] recreateFile(ArrayList<byte[]> ChunkList) {
        int chunkSize = 524288;
        int lastChunkSize = ChunkList.get(ChunkList.size()-1).length;
        int numOfBytes = (ChunkList.size()-1)*chunkSize + ChunkList.get(ChunkList.size()-1).length;

        byte[] Mp3ByteArray = new byte[numOfBytes];

        int indexOfChunk = 1;
        int indexOfByte = 0;
        for (byte[] chunk : ChunkList) {
            if(indexOfChunk < ChunkList.size()){
                for(int i = 0 ; i < chunkSize ; i++){
                    Mp3ByteArray[indexOfByte] = chunk[i];
                    indexOfByte++;
                }
            }else{
                for(int i = 0 ; i < lastChunkSize ; i++){
                    Mp3ByteArray[indexOfByte] = chunk[i];
                    indexOfByte++;
                }
            }
            indexOfChunk++;
        }

        return Mp3ByteArray;
    }

}
