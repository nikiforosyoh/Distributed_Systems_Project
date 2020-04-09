import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArtistName that = (ArtistName) o;
        return artistName.equalsIgnoreCase(that.artistName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(artistName.toLowerCase());
    }
}
