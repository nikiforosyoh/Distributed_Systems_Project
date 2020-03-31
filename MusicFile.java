import java.util.ArrayList;

public class MusicFile
{
    protected String trackName;
    protected String artistName;
    protected String albumInfo;
    protected byte[] musicFileExtract;
    protected int chunkNumber;

    public MusicFile(String trackName, String artistName, String albumInfo, byte[] musicFileExtract, int chunkNumber)
    {
        this.trackName = trackName;
        this.artistName = artistName;
        this.albumInfo = albumInfo;
        this.musicFileExtract = musicFileExtract;
        this.chunkNumber = chunkNumber;
    }

    public String getTrackName()
    {
        return this.trackName;
    }

    public String getArtistName()
    {
        return this.artistName;
    }

    public String getAlbumInfo()
    {
        return this.albumInfo;
    }

    public byte[] getMusicFileExtract()
    {
        return this.musicFileExtract;
    }

    public int getChunkNumber()
    {
        return this.chunkNumber;
    }
}