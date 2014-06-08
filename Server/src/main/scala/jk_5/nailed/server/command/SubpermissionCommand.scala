package jk_5.nailed.server.command

/**
 * No description given
 *
 * @author jk-5
 */
trait SubpermissionCommand {

  def registerPermissions(owner: String)
  def hasPermission(sender: String, args: Array[String]): Boolean
}
