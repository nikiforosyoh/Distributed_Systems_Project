import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.NotSupportedException;
import com.mpatric.mp3agic.UnsupportedTagException;
import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import java.io.File;
import java.io.IOException;
import java.lang.String;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class readMp3Files
{
    public static void main(String args[]) throws UnsupportedTagException, InvalidDataException, IOException
    {
        String path = "C:\\Users\\DELL\\Desktop\\Distributed Systems\\Songs";

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

        int ca = CountArtists(path);
        System.out.println("Artist count: " + ca);
        System.out.println("Song count: " + sc);
    }

    public static int CountArtists(String pathToMp3Files)
    {
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

                if (!Artists.contains(artist))
                {
                    Artists.add(artist);
                    ArtistCount++;
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (InvalidDataException e)
        {
            e.printStackTrace();
        }
        catch (UnsupportedTagException e)
        {
            e.printStackTrace();
        }

        return ArtistCount;
    }

}
