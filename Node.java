
import com.sun.corba.se.pept.broker.Broker;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
//or import java.util.*;
public class Node {
    //fields
    List<Broker> brokers=new ArrayList<Broker>();

    //methods
    public void init(int x){}

    public List<Broker> getBrokers(){
       return null;   
    
    }
    public void connect(){}

    public void disconnect(Socket requestSocket){
        try {

            requestSocket.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public void updateNodes(){}

}
