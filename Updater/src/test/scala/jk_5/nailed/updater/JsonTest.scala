package jk_5.nailed.updater

import com.google.gson.Gson
import jk_5.nailed.updater.json.LibraryList
import jk_5.nailed.updater.json.RestartLevel
import org.junit.Assert
import org.junit.Before
import org.junit.Test

/**
 * No description given
 *
 * @author jk-5
 */
class JsonTest {

  private var gson: Gson = null

  @Before def setup() {
    this.gson = LibraryList.serializer
  }

  @Test def testEnumSerialization() {
    for (level <- RestartLevel.values) {
      Assert.assertEquals("\"" + level.name.toLowerCase + "\"", gson.toJson(level))
    }
  }

  @Test def testEnumDeserialization() {
    for (level <- RestartLevel.values) {
      Assert.assertEquals(level, gson.fromJson("\"" + level.name.toLowerCase + "\"", classOf[RestartLevel]))
    }
  }

  @Test def testEnumDeserializationAcceptsUppercase() {
    for (level <- RestartLevel.values) {
      Assert.assertEquals(level, gson.fromJson("\"" + TestUtils.shuffleCase(level.name.toLowerCase) + "\"", classOf[RestartLevel]))
    }
  }
}