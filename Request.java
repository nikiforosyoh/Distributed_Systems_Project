import java.io.Serializable;

public class Request implements Serializable {
    String requestArtist;
    String requestSong;

    public Request(String requestArtist, String requestSong){
        this.requestArtist = requestArtist;
        this.requestSong = requestSong;
    }

    public String getRequestArtist(){
        return requestArtist;
    }

    public String getRequestSong(){
        return requestSong;
    }
}
