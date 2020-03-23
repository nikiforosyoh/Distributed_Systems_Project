import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.io.*;

import javax.xml.bind.DatatypeConverter;
public class TestHashing {
    static ArrayList<String> KeysForBrokers=new ArrayList<String>();
    static ArrayList<String> KeyVersion2=new ArrayList<String>();

    //implementation of md5 algorithm for hashing broker IP+port and artistNames
    public static String getMd5(String message) throws NoSuchAlgorithmException {
        MessageDigest md = null;
        md = MessageDigest.getInstance("MD5");
        // digest() method is called to calculate message digest
        //  of an input digest() return array of byte
        //???
        md.update(message.getBytes());
        byte[] messageDigest = md.digest();
		/*String myHash = (String)DatatypeConverter.printHexBinary(messageDigest).toUpperCase();
	     System.out.println(myHash);*/
        BigInteger big = new BigInteger(1, messageDigest).mod(BigInteger.valueOf(100));//οταν το χρησιμοποιώ δεν μου εμφανιζει τα ιδια κλειδια

        String myHash = big.toString();

        return myHash;

    }
    public static ArrayList<String> compareHashes(ArrayList<String> brokerHashes,ArrayList<String> artistHashes) {
        Test t = new Test();

        //ArrayList<String> KeysForBrokers = null;
        for (int i = 0; i < brokerHashes.size(); i++) {
            //KeysForBrokers = new ArrayList<String>();

            for (int j = 0; j < artistHashes.size(); j++) {

                int resultHash = brokerHashes.get(i).compareTo(artistHashes.get(j));
                if (resultHash < 0) {
                    System.out.println("Broker doesn't take this Artist");
                    System.out.println(brokerHashes.get(i));
                    System.out.println(artistHashes.get(j));

                } else if (resultHash > 0) {
                    System.out.println("Broker take this Artist");
                    System.out.println(brokerHashes.get(i));
                    System.out.println(artistHashes.get(j));
                    KeysForBrokers.add(artistHashes.get(j));

                } else if (resultHash == 0) {
                    System.out.println("------");
                    System.out.println(brokerHashes.get(i));
                    System.out.println(artistHashes.get(j));

                }
            }


        }

        return KeysForBrokers;
    }
    //sorting arraylists

    //allh ekdoxh
    public static ArrayList<String> compareHashesVersion2(String broker,ArrayList<String> artistHashes,ArrayList<String> ar) {

        ArrayList<String> KeyVersion3 = null;
        for (int j = 0; j < artistHashes.size(); j++) {
            KeyVersion3 = new ArrayList<String>();
            int resultHash = broker.compareTo(artistHashes.get(j));
            if (resultHash < 0) {
                System.out.println("Broker doesn't take this Artist");
                System.out.println(broker);
                System.out.println(artistHashes.get(j));

            } else if (resultHash > 0) {
                System.out.println("Broker take this Artist");
                System.out.println(artistHashes.get(j));
                //problem,giati einai orath se olous men,alla den theloume na allazei
                KeyVersion3.add(ar.get(j));


            } else if (resultHash == 0) {
                System.out.println("------");


            }

        }

        return KeyVersion3;
    }//prepei na efarmosw kukliko hashing=hashng in ds

}