import java.util.ArrayList;

public class MusicFile {
    //fields
    protected String trackName;
    //protected String artistName;
    protected ArtistName artistName;
    protected String albumInfo;
    protected String genre;
    protected byte[] musicFileExtract;//byte [] ???

    public MusicFile(String trackName, ArtistName artistName, String albumInfo, String genre, byte[] musicFileExtract){
        this.trackName=trackName;
        this.artistName=artistName;//??
        this.albumInfo=albumInfo;
        this.genre=genre;
        for(int i=0;i<musicFileExtract.length;i++){
            this.musicFileExtract[i]=musicFileExtract[i];
        }
    }
}
