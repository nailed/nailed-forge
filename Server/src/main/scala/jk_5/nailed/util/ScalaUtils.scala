package jk_5.nailed.util

import java.util.Locale

/**
 * No description given
 *
 * @author jk-5
 */
object ScalaUtils {

  def caseInsensitiveMatch(input: String)(cb: (String) => Unit) = cb(input.toLowerCase(Locale.US))
  //def caseInsensitiveMatch[T](input: String)(cb: (String) => T): T = cb(input.toLowerCase(Locale.US))
}
