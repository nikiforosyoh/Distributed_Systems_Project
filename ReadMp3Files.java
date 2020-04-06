import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static java.nio.file.Files.readAllBytes;

public class ReadMp3Files {

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
        //int ArtistCount = 0;
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
                    //ArtistCount++;
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
    public ArrayList<ArrayList<String>> getListSongs(String path, ArrayList <String> listOfArtists) throws IOException, InvalidDataException, UnsupportedTagException {
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

    //returns ArrayList with chunks
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

    //returns artists of each Publisher
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

}
