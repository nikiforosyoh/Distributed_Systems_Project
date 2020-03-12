public interface Publisher_Interface
{
    public void getBrokerList();

    public Broker hashTopic(ArtistName artistname);

    public void push(ArtistName artistname, Value value);

    public void notifyFailure(Broker broker);

}

