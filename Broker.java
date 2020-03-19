import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.*;

public class Broker extends Node{

    private Socket socket;

    List<Consumer> registerdUsers = new ArrayList<Consumer>();
    List<Publisher> registerdPublishers = new ArrayList<Publisher>();

    public void calculateKeys(){
    
    }
    public Publisher acceptConection(Publisher p){ 
        
        return null;
    }
    public Consumer acceptConection(Consumer c){
        
        return null;
    }
    public void notifyPublisher(String s){
    
    }
    public void pull(ArtistName a){
    
    }
    
}
