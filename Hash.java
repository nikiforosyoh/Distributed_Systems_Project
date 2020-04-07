import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash {

    //implementation of md5 algorithm for hashing broker IP+port and artistNames
    public static String getMd5(String message) throws NoSuchAlgorithmException {
        MessageDigest md;
        md = MessageDigest.getInstance("MD5");
        md.update(message.getBytes());
        byte[] messageDigest = md.digest();
        BigInteger big = new BigInteger(1, messageDigest).mod(BigInteger.valueOf(100));//οταν το χρησιμοποιώ δεν μου εμφανιζει τα ιδια κλειδια

        String myHash = big.toString();
        return myHash;

    }

}