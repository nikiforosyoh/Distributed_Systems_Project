import java.io.Serializable;
import java.util.Objects;

public class Request implements Serializable {
    private String requestArtist;
    private String requestSong;
    private ConsumerThread thread;

    public Request(String requestArtist, String requestSong, ConsumerThread thread){
        this.requestArtist = requestArtist;
        this.requestSong = requestSong;
        this.thread=thread;
    }

    public String getRequestArtist(){
        return requestArtist;
    }

    public String getRequestSong(){
        return requestSong;
    }

    public ConsumerThread getThread(){
        return thread;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Request request = (Request) o;
        return requestArtist.equalsIgnoreCase(request.requestArtist) &&
                requestSong.equalsIgnoreCase(request.requestSong);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestArtist.toLowerCase(), requestSong.toLowerCase());
    }
}
