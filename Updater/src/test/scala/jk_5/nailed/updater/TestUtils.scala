package jk_5.nailed.updater

import org.junit.Ignore

/**
 * No description given
 *
 * @author jk-5
 */
@Ignore object TestUtils {

  def shuffleCase(input: String): String = {
    val chars: Array[Char] = input.toCharArray
    for(i <- 0 until chars.length){
      if(i % 2 == 0) chars(i) = Character.toUpperCase(chars(i))
    }
    new String(chars)
  }
}
