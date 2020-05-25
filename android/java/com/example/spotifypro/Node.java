package com.example.spotifypro;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;


public class Node {
    //WE MUST CHANGE IT EVERY TIME!!!!
    private static final int N=1; //Num of brokers

    public static final int getN(){
        return N;
    }

    //receive broker's information
    public void init(String BrokerIp, int BrokerPort, String[][] availableBrokers){
        Socket socket ;
        ObjectInputStream in;
        ObjectOutputStream out;

        try{
            socket = new Socket(BrokerIp, BrokerPort);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            //System.out.println("Connected: " + socket);

            //take brokers' information
            out.writeObject("brokers");
            out.flush();

            availableBrokers = getBrokerInfo(in, availableBrokers);
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

    public void disconnect(Socket socket){
        try {
            socket.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    //get  ip, port of every broker
    public static String[][] getBrokerInfo(ObjectInputStream input, String[][] availableBrokers) throws IOException, ClassNotFoundException {
        for (int i=0; i<N; i++){
            for (int j=0; j<2; j++) {
                availableBrokers[i][j] =(String) input.readObject();
            }
        }
        return availableBrokers;
    }

}