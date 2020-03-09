public interface Consumer {
     public void register(Broker broker, ArtistName artistName);
     public void disconnect(Broker broker, ArtistName artistName);
     public void playData(ArtistName artistName, Value value);
}
