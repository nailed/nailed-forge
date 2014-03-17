package jk_5.nailed.updater;

import com.google.gson.Gson;
import jk_5.nailed.updater.json.Library;
import jk_5.nailed.updater.json.LibraryList;
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
        for(RestartLevel level : RestartLevel.values()){
            Assert.assertEquals("\"" + level.name().toLowerCase() + "\"", gson.toJson(level));
        }
    }

    @Test
    public void testEnumDeserialization(){
        for(RestartLevel level : RestartLevel.values()){
            Assert.assertEquals(level, gson.fromJson("\"" + level.name().toLowerCase() + "\"", RestartLevel.class));
        }
    }

    @Test
    public void testEnumDeserializationAcceptsUppercase(){
        for(RestartLevel level : RestartLevel.values()){
            Assert.assertEquals(level, gson.fromJson("\"" + TestUtils.shuffleCase(level.name().toLowerCase()) + "\"", RestartLevel.class));
        }
    }

    @Test
    public void testLibrarySerialization(){
        Library lib = new Library();
        lib.rev = 0;
        lib.restart = RestartLevel.NOTHING;
        lib.name = "name";
        lib.location = "location";
        lib.destination = "dest";
        lib.mod = true;

        Assert.assertEquals(
                "{\n" +
                        "  \"rev\": 0,\n" +
                        "  \"destination\": \"dest\",\n" +
                        "  \"location\": \"location\",\n" +
                        "  \"restart\": \"nothing\",\n" +
                        "  \"mod\": true\n" +
                        "}", this.gson.toJson(lib));
    }

    @Test
    public void testLibraryDeserialization(){
        Library lib = this.gson.fromJson("{\n" +
                "  \"rev\": 0,\n" +
                "  \"destination\": \"dest\",\n" +
                "  \"location\": \"location\",\n" +
                "  \"restart\": \"nothing\",\n" +
                "  \"mod\": true\n" +
                "}", Library.class);

        Assert.assertNotNull("Output should not be null", lib);
        Assert.assertEquals("Rev should equal 0", 0, lib.rev);
        Assert.assertEquals("Destination should equal \"dest\"", "dest", lib.destination);
        Assert.assertEquals("Location should equal \"location\"", "location", lib.location);
        Assert.assertEquals("RestartLevel should equal RestartLevel.NOTHING", RestartLevel.NOTHING, lib.restart);
        Assert.assertTrue("Mod should be true", lib.mod);
        Assert.assertNull("Name should not be touched", lib.name);
    }

    @Test
    public void testLibraryListSerialization(){
        LibraryList list = new LibraryList();
        Library lib = new Library();
        lib.rev = 0;
        lib.restart = RestartLevel.NOTHING;
        lib.name = "name";
        lib.location = "location";
        lib.destination = "dest";
        lib.mod = false;

        list.libraries.add(lib);

        Assert.assertEquals(
                "{\n" +
                        "  \"name\": {\n" +
                        "    \"rev\": 0,\n" +
                        "    \"destination\": \"dest\",\n" +
                        "    \"location\": \"location\",\n" +
                        "    \"restart\": \"nothing\",\n" +
                        "    \"mod\": false\n" +
                        "  }\n" +
                        "}", this.gson.toJson(list));
    }

    @Test
    public void testLibraryListSerializationMultiple(){
        LibraryList list = new LibraryList();
        Library lib = new Library();
        lib.rev = 0;
        lib.restart = RestartLevel.NOTHING;
        lib.name = "name";
        lib.location = "location";
        lib.destination = "dest";
        lib.mod = true;
        list.libraries.add(lib);

        lib = new Library();
        lib.rev = 1;
        lib.restart = RestartLevel.GAME;
        lib.name = "name2";
        lib.location = "location2";
        lib.destination = "dest2";
        lib.mod = false;
        list.libraries.add(lib);

        Assert.assertEquals(
                "{\n" +
                        "  \"name\": {\n" +
                        "    \"rev\": 0,\n" +
                        "    \"destination\": \"dest\",\n" +
                        "    \"location\": \"location\",\n" +
                        "    \"restart\": \"nothing\",\n" +
                        "    \"mod\": true\n" +
                        "  },\n" +
                        "  \"name2\": {\n" +
                        "    \"rev\": 1,\n" +
                        "    \"destination\": \"dest2\",\n" +
                        "    \"location\": \"location2\",\n" +
                        "    \"restart\": \"game\",\n" +
                        "    \"mod\": false\n" +
                        "  }\n" +
                        "}", this.gson.toJson(list));
    }

    @Test
    public void testLibraryListDeserialization(){
        LibraryList list = this.gson.fromJson("{\n" +
                "  \"name\": {\n" +
                "    \"rev\": 0,\n" +
                "    \"destination\": \"dest\",\n" +
                "    \"location\": \"location\",\n" +
                "    \"restart\": \"nothing\",\n" +
                "    \"mod\": true\n" +
                "  }\n" +
                "}", LibraryList.class);

        Assert.assertNotNull("Output should not be null", list);
        Assert.assertNotNull("LibraryList content should not be null", list.libraries);
        Assert.assertEquals("LibraryList should have 1 entry", 1, list.libraries.size());
        Library lib = list.libraries.get(0);
        Assert.assertEquals("Rev should equal 0", 0, lib.rev);
        Assert.assertEquals("Destination should equal \"dest\"", "dest", lib.destination);
        Assert.assertEquals("Location should equal \"location\"", "location", lib.location);
        Assert.assertEquals("RestartLevel should equal RestartLevel.NOTHING", RestartLevel.NOTHING, lib.restart);
        Assert.assertEquals("Name should equal \"name\"", "name", lib.name);
        Assert.assertTrue("Mod should equal true", lib.mod);
    }

    @Test
    public void testLibraryListDeserializationMultiple(){
        LibraryList list = this.gson.fromJson("{\n" +
                "  \"name\": {\n" +
                "    \"rev\": 0,\n" +
                "    \"destination\": \"dest\",\n" +
                "    \"location\": \"location\",\n" +
                "    \"restart\": \"nothing\",\n" +
                "    \"mod\": true\n" +
                "  },\n" +
                "  \"name2\": {\n" +
                "    \"rev\": 1,\n" +
                "    \"destination\": \"dest2\",\n" +
                "    \"location\": \"location2\",\n" +
                "    \"restart\": \"game\",\n" +
                "    \"mod\": false\n" +
                "  }\n" +
                "}", LibraryList.class);

        Assert.assertNotNull("Output should not be null", list);
        Assert.assertNotNull("LibraryList content should not be null", list.libraries);
        Assert.assertEquals("LibraryList should have 2 entries", 2, list.libraries.size());
        Library lib = list.libraries.get(0);
        Assert.assertEquals("Rev should equal 0", 0, lib.rev);
        Assert.assertEquals("Destination should equal \"dest\"", "dest", lib.destination);
        Assert.assertEquals("Location should equal \"location\"", "location", lib.location);
        Assert.assertEquals("RestartLevel should equal RestartLevel.NOTHING", RestartLevel.NOTHING, lib.restart);
        Assert.assertEquals("Name should equal \"name\"", "name", lib.name);
        Assert.assertTrue("Mod should equal true", lib.mod);
        lib = list.libraries.get(1);
        Assert.assertEquals("Rev should equal 1", 1, lib.rev);
        Assert.assertEquals("Destination should equal \"dest2\"", "dest2", lib.destination);
        Assert.assertEquals("Location should equal \"location2\"", "location2", lib.location);
        Assert.assertEquals("RestartLevel should equal RestartLevel.GAME", RestartLevel.GAME, lib.restart);
        Assert.assertEquals("Name should equal \"name2\"", "name2", lib.name);
        Assert.assertFalse("Mod should equal false", lib.mod);
    }
}
