package com.example.spotifypro;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

public class Consumer extends Node {

    private static final int N=getN(); //Num of brokers
    private static String BrokerIp;
    private static int BrokerPort;
    private static String[][] availableBrokers = new String[N][2]; //broker1: brokerIP, Integer.parseInt(brokerPort)
    private static ArrayList<ArrayList<String>> artists = new ArrayList<ArrayList<String>>();
    private HashMap<String,Info> brokerArtists = new HashMap<String,Info>();



    public void initialization(){
        //inti is called in Connect.java
        //init(BrokerIp,BrokerPort,availableBrokers);
        Socket socket;
        ObjectInputStream in;
        ObjectOutputStream out;

        //makes a list of each artist of a broker
        try {
            for (int i=0; i<N; i++) {
                socket = new Socket(availableBrokers[i][0], Integer.parseInt(availableBrokers[i][1]));
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());

                out.writeObject("artist names");
                out.flush();

                //
                ArrayList<String> a = new ArrayList<>();
                artists.add(a);
                //
                String name = (String) in.readObject();
                Info broker = new Info(availableBrokers[i][0], Integer.parseInt(availableBrokers[i][1]));
                while (!(name.equalsIgnoreCase("end"))) {

                    //hashmap artist->broker
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

    public static String getBrokerIp() {
        return BrokerIp;
    }

    public static int getBrokerPort() {
        return BrokerPort;
    }
    public static String[][] getAvailableBrokers() {
        return availableBrokers;
    }

    public ArrayList<MusicFile> openConsumer(String requestArtist,String requestSong) throws IOException {

                ObjectInputStream in;
                //DataInputStream input; //takes input from terminal
                ObjectOutputStream out;
                Socket socket;
                ArrayList<MusicFile> chunkList = new ArrayList<>();//queue for chunks

                do {

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
                            out.writeObject(requestArtist);
                            out.flush();

                            System.out.print("Song title: ");
                            out.writeObject(requestSong);
                            out.flush();

                            //waiting for response
                            String response = (String) in.readObject();

                            if (response.equalsIgnoreCase("Found")) {

                                System.out.println("-----------------");
                                System.out.println("Song found");

                                //takes the song from broker ->to another method ?
                                MusicFile chunk = (MusicFile) in.readObject();
                                //chunkList.add(chunk.getMusicFileExtract());
                                chunkList.add(chunk);

                                //System.out.println("received: " + chunk.getChunkNumber() + " chunk");
                                for (int ch = 0; ch < chunk.getTotalChunks() - 1; ch++) {

                                    chunk = (MusicFile) in.readObject();

                                    System.out.println("received: " + chunk.getChunkNumber() + " chunk");
                                    //chunkList.add(chunk.getMusicFileExtract());
                                    chunkList.add(chunk);

                                }
                                System.out.println("For the list: "+chunkList.get(0));

                                System.out.println("Song received successfully! ");
                                System.out.println("-----------------");
                                return chunkList;

                            } else if (response.equalsIgnoreCase("Not Found")) {
                                System.out.println("The song doesn't exist!");
                            } else {
                                System.out.println("Oops! A problem occurred. Try again..");
                            }

                            in.close();
                            out.close();
                            socket.close();
                            return chunkList;

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
                return chunkList;
            }
    //response
   //an spasw to response?
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
    public ArrayList<byte[]> takeMusicFileExtraction(int number,ArrayList<MusicFile> mlist){
        ArrayList<byte[]> arraymusic=new ArrayList<>();
        arraymusic.add(mlist.get(0).getMusicFileExtract());
        for(int c=1; c<number; c++){
            arraymusic.add(mlist.get(c).getMusicFileExtract());
        }
        return arraymusic;
    }

}

