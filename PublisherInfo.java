public class PublisherInfo {
    String IP;
    int port;

    public PublisherInfo(String IP, int port){
        this.IP=IP;
        this.port=port;
    }

    public String getIP(){
        return IP;
    }
    public int getPort(){
        return port;
    }
}
