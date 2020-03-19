import java.util.ArrayList;
import java.util.List;

public class Broker extends Node{

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
