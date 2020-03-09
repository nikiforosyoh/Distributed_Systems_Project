public interface Publisher_Interface
{
    public void getBrokerList();

    public Broker hashTopic(ArtistName);

    public void push(ArtistName, Value);

    public void notifyFailure(Broker);

}

