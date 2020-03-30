import java.io.Serializable;
import java.security.NoSuchAlgorithmException;

public class ArtistName implements Serializable {
    //what is that?!
    private static final long serialVersionUID = -2374023814140121509L;
    protected String artistName;
    protected int key;

    public ArtistName(String artistName) throws NoSuchAlgorithmException {
        this.artistName=artistName;
        this.key= calculateArtistHash(this.artistName);
    }

    public int calculateArtistHash(String artistName)throws NoSuchAlgorithmException {
        TestHashing testHashArtist=new TestHashing();
        int key= Integer.parseInt(testHashArtist.getMd5(artistName));

        return key;
    }

    public String getArtistName(){
        return artistName;
    }
    public int getKey(){
        return key;
    }


}
