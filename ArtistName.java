import java.io.Serializable;
import java.security.NoSuchAlgorithmException;

public class ArtistName implements Serializable {

    private static final long serialVersionUID = -2374023814140121509L;
    protected String artistName;
    protected int key;

    public ArtistName(String artistName) throws NoSuchAlgorithmException {
        this.artistName=artistName;
        this.key= calculateArtistHash(this.artistName);
    }

    public int calculateArtistHash(String artistName)throws NoSuchAlgorithmException {
        Hash hashArtist=new Hash();
        int key= Integer.parseInt(hashArtist.getMd5(artistName));

        return key;
    }

    public String getArtistName(){
        return artistName;
    }
    public int getKey(){
        return key;
    }

    //public void myString(){
    //    System.out.println(artistName +"  ,  "+key);

    //}


}
