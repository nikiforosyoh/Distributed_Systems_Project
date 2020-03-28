import com.sun.xml.internal.ws.api.model.wsdl.WSDLOutput;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class Publisher extends Node
{
    static String ipBroker1="127.0.0.1";
    static  int port1=5001;
    static  String ipBroker2="127.0.0.1";
    static int port2=5003;
    static String ipBroker3="127.0.0.1";
    static int port3=5005;


    private static String[][] availableBrokers = new String[3][2]; //broker1: brokerIP, brokerPort -> Integer.parseInt();

    public void startPublisher(){
        Socket socket1 = null;
        DataInputStream input21=null;
        ObjectInputStream input1=null;
        ObjectOutputStream out1 =null;
        try{
            socket1 = new Socket(ipBroker1,port1);

            out1 = new ObjectOutputStream(socket1.getOutputStream());
            input1 = new ObjectInputStream(socket1.getInputStream());

            System.out.println("Publisher Connected: " + socket1);

            //takes input from terminal
            input21 = new DataInputStream(System.in);
            String line = "";

            Thread t1= new Thread() {
                public void run() {
                    System.out.println("in second thread");
                    try {
                        Socket socket2 = new Socket(ipBroker2,port2);
                        ObjectOutputStream out2 = new ObjectOutputStream(socket2.getOutputStream());
                        ObjectInputStream input2 = new ObjectInputStream(socket2.getInputStream());
                        String line2="";
                        DataInputStream input22 = new DataInputStream(System.in);

                        Thread t2= new Thread() {
                            public void run() {
                                System.out.println("In 3rd thread! ");
                                try {
                                    Socket socket3 = new Socket(ipBroker3,port3);
                                    ObjectOutputStream out3 = new ObjectOutputStream(socket3.getOutputStream());
                                    ObjectInputStream input3 = new ObjectInputStream(socket3.getInputStream());
                                    String line3="";
                                    DataInputStream input23 = new DataInputStream(System.in);
                                    while(true){
                                        line3 = input23.readLine();
                                        out3.writeObject(line3);
                                        System.out.println(input3.readObject());

                                        if(line3.equalsIgnoreCase("brokers")) {
                                            availableBrokers = getBrokerInfo(input3, availableBrokers );
                                            System.out.println("Brokers' information received");
                                        }

                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (ClassNotFoundException e) {
                                    e.printStackTrace();
                                }

                            }
                        };
                        t2.start();
                        System.out.println("vghke apto 2o thread!!");

                        while(true){
                            line2 = input22.readLine();
                            out2.writeObject(line2);
                            System.out.println(input2.readObject());

                            if(line2.equalsIgnoreCase("brokers")) {
                                availableBrokers = getBrokerInfo(input2, availableBrokers );
                                System.out.println("Brokers' information received");
                            }

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                }
            };
            t1.start();



            while(true){
                try{
                    line = input21.readLine();
                    out1.writeObject(line);
                    System.out.println(input1.readObject());

                    if(line.equalsIgnoreCase("brokers")) {
                        availableBrokers = getBrokerInfo(input1, availableBrokers );
                        System.out.println("Brokers' information received");

                    }
                }
                catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }

        }
        catch(UnknownHostException u)
        {
            System.out.println(u);
        }
        catch(IOException i)
        {
            System.out.println(i);
        }


    }

    public static String[][] getBrokerInfo(ObjectInputStream input, String[][] availableBrokers) throws IOException, ClassNotFoundException {
        String info = "";
        for (int i=0; i<3; i++){
            info = (String) input.readObject();
            System.out.println(info);
            availableBrokers[i][0] = info.trim().substring(info.indexOf(":")+1,info.indexOf(",")).trim();
            info = info.trim().substring(info.indexOf(",")+1);
            availableBrokers[i][1] = info.trim().substring(info.indexOf(":")+1).trim();

        }

        return availableBrokers;
    }

    public static void main(String[] args){
        Publisher pub= new Publisher();
        pub.startPublisher();
    }
}
