
import com.sun.corba.se.pept.broker.Broker;

import java.util.ArrayList;
import java.util.List;
//or import java.util.*;
public Node {
    //fields
    List<Broker> brokers=new ArrayList<Broker>();

    //methods
    public void init(int x);
    public List<Broker> getBrokers();
    public void connect();
    public void discconnect();
    public void updateNodes();

}
