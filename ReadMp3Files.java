import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.NotSupportedException;
import com.mpatric.mp3agic.UnsupportedTagException;
import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.String;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.file.Files.readAllBytes;

public class ReadMp3Files
{
    ArrayList<String> artistsname = new ArrayList<>();
    ArrayList<String> ca = new ArrayList<>();

    public ArrayList<String> Read() throws UnsupportedTagException, InvalidDataException, IOException {
        String path = "Songs";

        Stream<Path> walk = Files.walk(Paths.get(path));
        List<String> result = walk.filter(Files::isRegularFile).map(Path::toString).collect(Collectors.toList());
        int sc = 0;
        for(int i = 0 ; i <result.size() ; i++)
        {
            File file = new File(result.get(i));
            Mp3File mp3file = new Mp3File(file);


            if (mp3file.hasId3v2Tag())
            {
                ID3v2 id3v2Tag = mp3file.getId3v2Tag();
                System.out.println("This is id3v2");
                System.out.println("Song Name: " + file.getName().substring(0, file.getName().length()-4));
                System.out.println("Artist: " + id3v2Tag.getArtist());
                System.out.println("Album: " + id3v2Tag.getAlbum());
                System.out.println("Genre: " + id3v2Tag.getGenre() + "\n");
            }
            sc++;
        }

        ca = getArtistsList(path);
        System.out.println("Artist count: " + ca);
        System.out.println("Song count: " + sc);
        return ca;
    }

    public ArrayList<String> getListOfPaths(String path) throws IOException {

        Stream<Path> walk = Files.walk(Paths.get(path));
        List<String> result = walk.filter(Files::isRegularFile).map(Path::toString).collect(Collectors.toList());

        ArrayList<String> ListOfPaths = new ArrayList<>();

        for(String songPath : result)
        {
            ListOfPaths.add(songPath);
        }

        return ListOfPaths;
    }

    public static ArrayList<String> getArtistsList(String pathToMp3Files) {
        int ArtistCount = 0;
        ArrayList<String> Artists = new ArrayList<String>();

        try (Stream<Path> walk = Files.walk(Paths.get(pathToMp3Files)))
        {
            List<String> result = walk.filter(Files::isRegularFile).map(Path::toString).collect(Collectors.toList());

            for (int i = 0 ; i < result.size() ; i++)
            {
                Mp3File mp3file = new Mp3File(result.get(i));
                String artist = "";

                if (mp3file.hasId3v1Tag())
                {
                    artist = mp3file.getId3v1Tag().getArtist();
                }
                else if (mp3file.hasId3v2Tag())
                {
                    artist = mp3file.getId3v2Tag().getArtist();
                }

                if (!Artists.contains(artist) && !artist.equals(""))
                {
                    Artists.add(artist);
                    ArtistCount++;
                }
            }
        }
        catch (IOException | InvalidDataException | UnsupportedTagException e )
        {
            e.printStackTrace();
        }

        return Artists;
    }

    //returns songs of each artists
    public ArrayList<ArrayList<String>> getListSongs(String path, ArrayList <String> listOfArtists) throws IOException, InvalidDataException, UnsupportedTagException
    {
        ArrayList <ArrayList <String>> listOfArtistSongs = new ArrayList<>();

        ArrayList <String> listOfPaths = getListOfPaths(path);

        int i = 0;

        for(String artist : listOfArtists)
        {
            ArrayList<String> thisArtistSongList = new ArrayList<>();

            for(String songPath : listOfPaths)
            {
                File file = new File(songPath);
                Mp3File thisSong = new Mp3File(file);
                ID3v2 thisSongTags = thisSong.getId3v2Tag();


                if(artist.equalsIgnoreCase((String)thisSongTags.getArtist()))
                {
                    thisArtistSongList.add(file.getName().substring(0, file.getName().length()-4));
                }
                i++;
            }

            listOfArtistSongs.add(thisArtistSongList);
        }

        return listOfArtistSongs;
    }

    public byte[] recreateFile(ArrayList<byte[]> ChunkList) {
        int chunkSize = 524288;
        int lastChunkSize = ChunkList.get(ChunkList.size()-1).length;
        int numOfBytes = (ChunkList.size()-1)*chunkSize + ChunkList.get(ChunkList.size()-1).length;

        byte[] Mp3ByteArray = new byte[numOfBytes];

        int indexOfChunk = 1;
        int indexOfByte = 0;
        for (byte[] chunk : ChunkList) {
            if(indexOfChunk < ChunkList.size()){
                for(int i = 0 ; i < chunkSize ; i++)
                {
                    Mp3ByteArray[indexOfByte] = chunk[i];
                    indexOfByte++;
                }
            }
            else{
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

    //splits song into chunks
    public ArrayList<byte[]> splitToChunk(String songName) throws IOException {
        int chunkSize = 524288;
        String pathOfSong = "Songs\\"+songName+".mp3";

        ArrayList<byte[]> ChunkList = new ArrayList<>();
        Path path = Paths.get(pathOfSong);

        byte[] Mp3ByteArray = readAllBytes(path);

        int lastChunkSize = Mp3ByteArray.length%(chunkSize);
        int numOfChunks = Mp3ByteArray.length/(chunkSize);

        int index = 0;
        for (int i = 0 ; i <= numOfChunks ; i++) {

            if( i < numOfChunks) {
                byte[] chunk = new byte[chunkSize];
                for(int j = 0 ; j < chunkSize ; j++)
                {
                    chunk[j] = Mp3ByteArray[index];
                    index++;
                }
                ChunkList.add(chunk);
            }
            else {
                byte[] chunk = new byte[lastChunkSize];
                for (int j = 0; j < lastChunkSize; j++) {
                    chunk[j] = Mp3ByteArray[index];
                    index++;
                }
                ChunkList.add(chunk);
            }

        }

        return ChunkList;
    }



    public String[] getMusicFileAttributes(String songName) throws InvalidDataException, IOException, UnsupportedTagException {

        String pathOfSong = "Songs\\"+songName+".mp3";

        String[] MusicFileAttributes = new String[3];

        File file = new File(pathOfSong);
        Mp3File mp3file = new Mp3File(file);
        ID3v2 fileTag = mp3file.getId3v2Tag();

        MusicFileAttributes[0] = file.getName().substring(0, file.getName().length()-4);

        MusicFileAttributes[1] = fileTag.getArtist();

        if(!fileTag.getAlbum().equals("")) {
            MusicFileAttributes[2] = fileTag.getAlbum();
        } else {
            MusicFileAttributes[2] = "no album info";
        }

        return MusicFileAttributes;
    }

    public ArrayList<MusicFile> getMusicFiles(String SongName) throws InvalidDataException, IOException, UnsupportedTagException {

        ArrayList<MusicFile> MusicFileArray = new ArrayList<>();

        String[] MusicFileInfo = getMusicFileAttributes(SongName);
        ArrayList<byte[]> SongChunks = splitToChunk(SongName);

        int numOfChunks = SongChunks.size();

        for(int i = 0 ; i < numOfChunks ; i++) {
            MusicFile musicFile = new MusicFile(MusicFileInfo[0], MusicFileInfo[1], MusicFileInfo[2], SongChunks.get(i), i+1);
            MusicFileArray.add(musicFile);
        }

        return MusicFileArray;
    }

    public ArrayList<String> getPublisherArtistList(char start, char end) {
        ArrayList<String> publisherArtistList = new ArrayList<>();
        ArrayList<String> allArtistList = getArtistsList("Songs");

        for(String artist : allArtistList) {
            if(artist.toLowerCase().charAt(0) >= start && artist.toLowerCase().charAt(0) <= end) {
                publisherArtistList.add(artist);
            }
        }

        return publisherArtistList;
    }

    public static void main(String args[]) throws InvalidDataException, IOException, UnsupportedTagException {
        ReadMp3Files mp3Methods = new ReadMp3Files();
        String path = "Songs";
        /*

        ArrayList<ArrayList<String>> listOfArtistSongs = mp3Methods.getListOfArtistSongs(path);
        ArrayList <String> listOfArtists = getArtistsList(path);

        for(int i = 0 ; i < listOfArtists.size() ; i++)
        {
            System.out.println("For the artist: " + listOfArtists.get(i));
            int j = 1;
            for(String songName : listOfArtistSongs.get(i))
            {
                System.out.println("The " + j + "th song name is: "+ songName);
                j++;
            }
        }

        */

        /*

        ArrayList<byte[]> ChunkList = mp3Methods.splitToChunk("A Waltz For Naseem");
        byte[] mp3File = mp3Methods.recreateFile(ChunkList);

        FileOutputStream stream = new FileOutputStream("src//Mitsos.mp3");
        stream.write(mp3File);
         */

        /*
        String[] MusicFileInfo = mp3Methods.getMusicFileAttributes("Ambush in Rattlesnake Gulch");
        for(String info : MusicFileInfo)
        {
            System.out.println(info);
        }

         */

        ArrayList<String> allArtistList = getArtistsList(path);
        ArrayList<String> publisherArtistList1 = mp3Methods.getPublisherArtistList('a', 'm');
        ArrayList<String> publisherArtistList2 = mp3Methods.getPublisherArtistList('n', 'z');

        System.out.println(allArtistList.size());
        System.out.println(publisherArtistList1.size());
        System.out.println(publisherArtistList2.size());

    }

}
