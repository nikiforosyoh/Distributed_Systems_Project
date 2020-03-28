import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Hash {
    //Kanei hash tous brokers
    //kanei hash tous artists
    //kanei hashMap tous brokers gia na kserooume poios broker einai se poio hash
    //kanei hashMap tous artists gia ton idio logo
    //kanei sortarisma ta hashes twn brokers
    //sugrkrinei to kathe hash twn brokers me ta hashes twn artists kai to apotelesma ginetai save se ena array
    //sugrkrinei to kathe stoixeio toy array(einai key kapoiou stoixeiou tou hashMap) me to hashmap twn artists gia na dei pou antistoixei kai kanei save se allon pinaka


     //variables i need
     ArrayList<String> brokerHashOnly=new ArrayList<String>();
     ArrayList<String>  artistnames=new ArrayList<String>();
     ArrayList<String>  artistHashOnly=new ArrayList<String>();
     Map<String, String> brokerHashAndids = new HashMap<>();
     Map<String, String> artistHashAndids = new HashMap<>();
     //ArrayList<String>keyForBrokers=new ArrayList<String>();
    ArrayList<String>keyForBrokers1=new ArrayList<String>();
    ArrayList<String>keyForBrokers2=new ArrayList<String>();
    ArrayList<String>keyForBrokers3=new ArrayList<String>();
    ArrayList<String> FinalKey1=new ArrayList<String>();
    ArrayList<String> FinalKey2=new ArrayList<String>();
    ArrayList<String> FinalKey3=new ArrayList<String>();

     //0.
     public ArrayList<String> readArtists(){
         ReadMp3Files readMp3Files=new ReadMp3Files();
         System.out.println("The size of array artistsName is :"+readMp3Files.CountArtists("Songs"));
         artistnames=readMp3Files.CountArtists("Songs");
         return artistnames;
     }
     //1.
     public ArrayList<String> calculateBrokerHash(String brokerIp,int brokerPort,String brokername)throws NoSuchAlgorithmException {
         TestHashing testhashBroker=new TestHashing();

         String brokerIpAndPort=brokerIp+Integer.toString(brokerPort);
         String BrokerHash=testhashBroker.getMd5(brokerIpAndPort);
         brokerHashOnly.add(BrokerHash);
         brokerHashAndids.put(BrokerHash,brokername);
         Collections.sort(brokerHashOnly);//gt den ulopoieitai ????
         return brokerHashOnly;

     }
     //2.
     public ArrayList<String> calculateArtistHash(ArrayList<String>Artists)throws NoSuchAlgorithmException{
         TestHashing testHashArtist=new TestHashing();
         for(int ar=0; ar<Artists.size();ar++){
             String ArtistHash=testHashArtist.getMd5(Artists.get(ar));
             artistHashOnly.add(ArtistHash);
             artistHashAndids.put(ArtistHash,Artists.get(ar));
             System.out.println("ArtistHashes :"+ ArtistHash + " for ArtistName :"+Artists.get(ar));
             if (Artists.get(ar) == null) {
                 Artists.remove(ar);
             }
         }
         return artistHashOnly;
     }
     //3.
     //5.Problem,πως θα μου γυρναει 3 πινακες ?
    //Για την ωρα ,γυρναει εναν πινακα με Hash artists
     public ArrayList<String> compareHashes(String br,ArrayList<String> art){
             //int resultHash = art.compareTo(br);//36>4??? to pairnei san string?
         ArrayList<String>keyForBrokers=new ArrayList<String>();

             int br2 = Integer.parseInt(br);
             for(int j=0;j<art.size();j++) {
                 int art2 = Integer.parseInt(art.get(j));
                 if (br2 >=art2) {
                     System.out.println("Broker take this Artist");
                     // System.out.println(broker);
                     System.out.println(art.get(j));
                     keyForBrokers.add(art.get(j));
                     //art.remove(j);//meta pairnei thn thesh

                 } else if (br2 < art2) {
                     System.out.println("Broker doesn't take this Artist");
                     System.out.println(art.get(j));
                     //problem,giati einai orath se olous men,alla den theloume na allazei

                 } else if(br2==art2){
                     System.out.println("------");

                 }
             }
             art.removeAll(keyForBrokers);

         return keyForBrokers;
     }

     public ArrayList<String> compareHashesWithNames(Map<String,String> artHashId,ArrayList<String> keys){
         ArrayList<String> FinalKey=new ArrayList<String>();
         for(int i=0;i<keys.size();i++){
             for (Map.Entry<String, String> entry : artHashId.entrySet()) {
                 String k = entry.getKey();
                 String v = entry.getValue();
                 if(k.equals(keys.get(i))) FinalKey.add(entry.getValue());
             }
         }
         return FinalKey;
     }

     //testing
     public static void main(String[]args){
         Hash h=new Hash();
         h.readArtists();
         String broker1="192.128.78.10";
         String brokername1="First broker";
         int brokerport1=5000;
         String broker2="192.246.10.555";
         String brokername2="Second broker";
         int brokerport2=4367;
         String broker3="192.128.0.7";
         String brokername3="Second broker";
         int brokerport3=5005;
         try {
             h.calculateBrokerHash(broker1,brokerport1,brokername1);
             h.calculateBrokerHash(broker2,brokerport2,brokername2);
             h.calculateBrokerHash(broker3,brokerport3,brokername3);
             System.out.println(h.brokerHashOnly);
             h.calculateArtistHash(h.artistnames);

             System.out.println(h.brokerHashAndids);
             System.out.println(h.artistnames);
             System.out.println(h.artistHashOnly);
             System.out.println(h.artistHashAndids);
             String maxBroker=h.brokerHashOnly.get(h.brokerHashOnly.size()-1);
             System.out.println(maxBroker);

             h.keyForBrokers1=h.compareHashes(h.brokerHashOnly.get(0),h.artistHashOnly);
             System.out.println(h.artistHashOnly+" first");
             h.keyForBrokers2=h.compareHashes(h.brokerHashOnly.get(1),h.artistHashOnly);
             System.out.println(h.artistHashOnly+" second");
             h.keyForBrokers3=h.compareHashes(h.brokerHashOnly.get(2),h.artistHashOnly);
             System.out.println(h.artistHashOnly+" third");


             h.FinalKey1=h.compareHashesWithNames(h.artistHashAndids,h.keyForBrokers1);
             h.FinalKey2=h.compareHashesWithNames(h.artistHashAndids,h.keyForBrokers2);
             h.FinalKey2=h.compareHashesWithNames(h.artistHashAndids,h.keyForBrokers3);

             System.out.println(h.keyForBrokers1+" what?");
             System.out.println(h.keyForBrokers2+" what??");
             System.out.println(h.keyForBrokers3+" wpp???");

             System.out.println(h.FinalKey1+" key1");
             System.out.println(h.FinalKey2+" key2");
             System.out.println(h.FinalKey3+" key3");




         } catch (NoSuchAlgorithmException e) {
             e.printStackTrace();
         }




     }



}
