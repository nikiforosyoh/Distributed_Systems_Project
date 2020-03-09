import java.util.ArrayList;
import java.util.List;

public interface Broker extends Node{
    List<Consumer> registerdUsers = new ArrayList<Consumer>();
    List<Publisher> registerdPublishers = new ArrayList<Publisher>();

    public void calculateKeys();
    public Publisher acceptConection(Publisher p);
    public Consumer acceptConection(Consumer c);
    public void notifyPublisher(String s);
    public void pull(ArtistName a);
}
