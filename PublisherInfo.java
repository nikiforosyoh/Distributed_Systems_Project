import java.io.Serializable;
import java.util.Objects;

public class PublisherInfo implements Serializable {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PublisherInfo that = (PublisherInfo) o;
        return port == that.port &&
                Objects.equals(IP, that.IP);
    }

    @Override
    public int hashCode() {
        return Objects.hash(IP, port);
    }
}
