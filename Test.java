import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Test {
    static TestHashing test=new TestHashing();
    static ReadMp3Files readMp3Files=new ReadMp3Files();
    static ArrayList<String> brokerips=new ArrayList<String>();
    static ArrayList<String>  artistnames=new ArrayList<String>();
    static ArrayList<String> HashForBrokers=new ArrayList<String>();
    static ArrayList<String> HashForArtists=new ArrayList<String>();
    ArrayList<String> KeyVersion4=new ArrayList<String>();

    public static void main(String[]args)throws NoSuchAlgorithmException {

        ArrayList<String>  artistnames=new ArrayList<String>();
        String brokerip1="127.0.0.1";//prepei na parw to ip tou broker
        int brokerPort1=4356;
        String brokerip2="127.0.0.2";
        int brokerPort2=4326;
        String brokerip3="127.0.0.3";
        int brokerPort3=4328;
        String brokerip4="127.0.0.4";
        int brokerPort4=4372;
        String brokerHash=null;
        String artistHash=null;

        brokerips.add(brokerip1+Integer.toString(brokerPort1));
        brokerips.add(brokerip2+Integer.toString(brokerPort2));
        brokerips.add(brokerip3+Integer.toString(brokerPort3));
        //brokerips.add(brokerip4+Integer.toString(brokerPort4));

        for(int i=0;i<brokerips.size();i++) {
            System.out.println(brokerips.get(i));
            brokerHash = test.getMd5(brokerips.get(i));
            HashForBrokers.add(brokerHash);
            System.out.println("Broker ip after hash is: " + brokerHash+" is for broker:"+brokerips.get(i));
        }
        Collections.sort(brokerips);//sorting
        System.out.println("The size of array artistsName is :"+readMp3Files.ca.size());
        System.out.println("The size of array artistsName is :"+readMp3Files.CountArtists("C:\\Hellag\\AUEB\\2019_2020(final)\\spring\\distributed_systems\\projects\\dataset1\\dataset1\\Test"));
        artistnames=readMp3Files.CountArtists("C:\\Hellag\\AUEB\\2019_2020(final)\\spring\\distributed_systems\\projects\\dataset1\\dataset1\\Test");

        System.out.println(artistnames.get(0));
        Collections.sort(artistnames);
        System.out.println(artistnames.get(0));


        for(int j=0;j<artistnames.size();j++){
            artistHash=test.getMd5(artistnames.get(j));
            HashForArtists.add(artistHash);
            System.out.println("Artists after hash is: " + artistHash+" is for "+artistnames.get(j));

        }



        test.compareHashes(HashForBrokers,HashForArtists);
        System.out.println(test.KeysForBrokers);


        Collections.sort(HashForBrokers);//sorting

        for(int k=0;k<brokerips.size();k++) {
            test.KeyVersion2=test.compareHashesVersion2(HashForBrokers.get(k), HashForArtists, artistnames);
            System.out.println(test.KeyVersion2 +"for broker:"+k+ " with Hash Code :"+HashForBrokers.get(k));//logiko giati einai sto idio list

        }
        //Theorhtika stelnw thn lista keyVersion2 ston Publisher?


    }
}
