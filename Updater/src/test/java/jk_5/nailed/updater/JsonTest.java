package jk_5.nailed.updater;

import com.google.gson.Gson;
import jk_5.nailed.updater.json.Library;
import jk_5.nailed.updater.json.RestartLevel;
import jk_5.nailed.updater.json.serialization.LibraryListSerializer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * No description given
 *
 * @author jk-5
 */
public class JsonTest {

    private Gson gson;

    @Before
    public void setup(){
        this.gson = LibraryListSerializer.serializer;
    }

    @Test
    public void testEnumSerialization(){
        Assert.assertEquals("\"game\"", gson.toJson(RestartLevel.GAME));
        Assert.assertEquals("\"launcher\"", gson.toJson(RestartLevel.LAUNCHER));
        Assert.assertEquals("\"nothing\"", gson.toJson(RestartLevel.NOTHING));
    }

    @Test
    public void testEnumDeserialization(){
        Assert.assertEquals(RestartLevel.GAME, gson.fromJson("\"game\"", RestartLevel.class));
        Assert.assertEquals(RestartLevel.LAUNCHER, gson.fromJson("\"launcher\"", RestartLevel.class));
        Assert.assertEquals(RestartLevel.NOTHING, gson.fromJson("\"nothing\"", RestartLevel.class));
    }

    @Test
    public void testEnumDeserializationAcceptsUppercase(){
        Assert.assertEquals(RestartLevel.GAME, gson.fromJson("\"GaMe\"", RestartLevel.class));
        Assert.assertEquals(RestartLevel.LAUNCHER, gson.fromJson("\"lAunChEr\"", RestartLevel.class));
        Assert.assertEquals(RestartLevel.NOTHING, gson.fromJson("\"NOThinG\"", RestartLevel.class));
    }

    @Test
    public void testLibrarySerialization(){
        Library lib = new Library();
        lib.rev = 0;
        lib.restart = RestartLevel.NOTHING;
        lib.name = "name";
        lib.location = "location";
        lib.destination = "dest";

        Assert.assertEquals(
                "{\n" +
                        "  \"rev\": 0,\n" +
                        "  \"destination\": \"dest\",\n" +
                        "  \"location\": \"location\",\n" +
                        "  \"restart\": \"nothing\"\n" +
                        "}", this.gson.toJson(lib));
    }

    @Test
    public void testLibraryDeserialization(){
        Library lib = this.gson.fromJson("{\n" +
                "  \"rev\": 0,\n" +
                "  \"destination\": \"dest\",\n" +
                "  \"location\": \"location\",\n" +
                "  \"restart\": \"nothing\"\n" +
                "}", Library.class);

        Assert.assertNotNull(lib);
        Assert.assertEquals(0, lib.rev);
        Assert.assertEquals("dest", lib.destination);
        Assert.assertEquals("location", lib.location);
        Assert.assertEquals(RestartLevel.NOTHING, lib.restart);
        Assert.assertNull(lib.name);
    }
}
