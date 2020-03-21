import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FindArtistsSongs
{
    public static void main(String args[])
    {
        String path  = "C:\\Users\\DELL\\Desktop\\Distributed Systems\\Songs";
        int removablePart = path.length()+1;

        ArrayList<String> SongNames = new ArrayList<String>();
        try (Stream<Path> walk = Files.walk(Paths.get(path)))
        {
            List<String> result = walk.filter(Files::isRegularFile)
                    .map(Path::toString).collect(Collectors.toList());

            for (int i = 0 ; i < result.size() ; i++)
            {
                SongNames.add(result.get(i).substring(removablePart, result.get(i).length()-4));
            }

            for(int i = 0 ; i < SongNames.size() ; i++)
            {
                System.out.println(SongNames.get(i));
            }

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }
}
