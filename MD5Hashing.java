import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.io.*;

import javax.xml.bind.DatatypeConverter;
public class MD5Hashing {
	static HashMap<String, String> tests= new HashMap<String, String>();
	//private  MessageDigest md;
	
	public static String getMd5(String artistname)throws NoSuchAlgorithmException{
		MessageDigest md = null;
		md=MessageDigest.getInstance("MD5");
		 // digest() method is called to calculate message digest 
         //  of an input digest() return array of byte 
		//???
		md.update(artistname.getBytes());
		 byte[] messageDigest=md.digest();
		/*String myHash = (String)DatatypeConverter.printHexBinary(messageDigest).toUpperCase();
	     System.out.println(myHash);*/
		 BigInteger big = new BigInteger(1, messageDigest);//.mod(BigInteger.valueOf(10));,οταν το χρησιμοποιώ δεν μου εμφανιζει τα ιδια κλειδια 
		 
	     String myHash = big.toString();

		 return myHash;

	}
	// Function to sort map by Key 
    public static void sortbykey() 
    { 
        ArrayList<String> sortedKeys = 
                    new ArrayList<String>(tests.keySet()); 
          
        Collections.sort(sortedKeys);  
  
        // Display the TreeMap which is naturally sorted 
        for (String x : sortedKeys)  
            System.out.println("Key = " + x +  
                        ", Value = " + tests.get(x));      
    } 
	//for testing
	public static void main(String []args) throws Exception{
		System.out.println("Test for md5 algorithm ");
		//HashMap<String, String> tests= new HashMap<String, String>();
		File file=new File("C:\\Hellag\\AUEB\\2019_2020(final)\\spring\\distributed_systems\\projects\\MD5HashingTest.txt");
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String Artistname;
			//String brokerip;
			
			
			while ((Artistname = br.readLine()) != null){
			//System.out.println("My hashCode is :"+getMd5(Artistname));
			tests.put(getMd5(Artistname),Artistname);
		    }
			
		    // System.out.println(tests);
		     System.out.println("The sum of artists is :" +tests.size());
		     sortbykey();
		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	 }
	

}
