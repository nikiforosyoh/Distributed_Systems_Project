import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;

import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Files.size;

public class Mp3Manipulation
{
    public static void main(String args[]) throws IOException
    {
        int chunkSize = 524288;

        ArrayList<byte[]> ChunkList = new ArrayList<>();

        File file = new File("C://Users//DELL//Desktop//Distributed Systems//Songs//Apex.mp3");

        Path path = Paths.get("C://Users//DELL//Desktop//Distributed Systems//Songs//Apex.mp3");

        long file_size = size(path);

        System.out.println("The file has " +file_size/1024+ " Kilo Bytes in it");

        byte[] Mp3ByteArray = readAllBytes(path);

        System.out.println(Mp3ByteArray[1]);

        System.out.println("Byte Array size: " + Mp3ByteArray.length);

        int lastChunkSize = Mp3ByteArray.length%(chunkSize);
        int numOfChunks = Mp3ByteArray.length/(chunkSize);

        System.out.println("Size of last Chunk: " + lastChunkSize);
        System.out.println("Number of chunks for this file: " + numOfChunks);

        int index = 0;
        for (int i = 0 ; i <= numOfChunks ; i++)
        {

            if( i < numOfChunks)
            {
                byte[] chunk = new byte[chunkSize];
                for(int j = 0 ; j < chunkSize ; j++)
                {
                    chunk[j] = Mp3ByteArray[index];
                    index++;
                }
                ChunkList.add(chunk);
            }
            else
            {
                byte[] chunk = new byte[lastChunkSize];
                for(int j = 0 ; j < lastChunkSize ; j++)
                {
                    chunk[j] = Mp3ByteArray[index];
                    index++;
                }
                ChunkList.add(chunk);
            }

        }

        System.out.println(ChunkList.size());
        System.out.println(ChunkList.get(19).length);

        byte[] newMp3ByteArray = recreateFile(ChunkList);

        System.out.println(Arrays.equals(newMp3ByteArray,Mp3ByteArray));

        FileOutputStream stream = new FileOutputStream("C://Users//DELL//Desktop//Distributed Systems//Distributed_Systems_Project//src//MP3ARRAYFILE.mp3");
        stream.write(newMp3ByteArray);

    }

    public static byte[] recreateFile(ArrayList<byte[]> ChunkList)
    {
        int chunkSize = 524288;
        int lastChunkSize = ChunkList.get(ChunkList.size()-1).length;
        int numOfBytes = (ChunkList.size()-1)*chunkSize + ChunkList.get(ChunkList.size()-1).length;

        byte[] Mp3ByteArray = new byte[numOfBytes];

        int indexOfChunk = 1;
        int indexOfByte = 0;
        for (byte[] chunk : ChunkList)
        {
            if(indexOfChunk < ChunkList.size())
            {
                for(int i = 0 ; i < chunkSize ; i++)
                {
                    Mp3ByteArray[indexOfByte] = chunk[i];
                    indexOfByte++;
                }
            }
            else
            {
                for(int i = 0 ; i < lastChunkSize ; i++)
                {
                    Mp3ByteArray[indexOfByte] = chunk[i];
                    indexOfByte++;
                }
            }
            indexOfChunk++;
        }

        return Mp3ByteArray;
    }
}
