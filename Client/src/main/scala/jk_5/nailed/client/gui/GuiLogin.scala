package jk_5.nailed.client.gui

import net.minecraft.client.gui.{GuiButton, GuiTextField, GuiScreen}
import org.lwjgl.input.Keyboard
import java.util

/**
 * No description given
 *
 * @author jk-5
 */
class GuiLogin extends GuiScreen {

  var usernameField: GuiTextField = null
  var passwordField: GuiTextField = null
  var loginButton: GuiButton = null
  var registerButton: GuiButton = null
  var cancelButton: GuiButton = null

  override def initGui(){
    Keyboard.enableRepeatEvents(true)
    val list = this.buttonList.asInstanceOf[util.List[GuiButton]]
    list.clear()
    list.add({this.loginButton = new GuiButton(0, this.width / 2 - 100, this.height / 4 + 84, 65, 20, "Login");this.loginButton})
    list.add({this.registerButton = new GuiButton(1, this.width / 2 - 35, this.height / 4 + 84, 65, 20, "Register");this.registerButton})
    list.add({this.cancelButton = new GuiButton(2, this.width / 2 + 30, this.height / 4 + 84, 65, 20, "Cancel");this.cancelButton})

    this.usernameField = new GuiTextField(this.fontRendererObj, this.width / 2 - 100, 60, 200, 20)
    this.usernameField.setMaxStringLength(32767)
    this.usernameField.setFocused(true)
    this.passwordField = new GuiTextField(this.fontRendererObj, this.width / 2 - 100, 90, 200, 20) //TODO: masked
    this.passwordField.setMaxStringLength(32767)
  }

  override def drawScreen(x: Int, y: Int, delta: Float){
    this.drawDefaultBackground()
    this.drawCenteredString(this.fontRendererObj, "Log in to your nailed account", this.width / 2, 20, 0xFFFFFFFF)
    this.drawString(this.fontRendererObj, "Username", this.width / 2 - 155, 65, 0xFFFFFFFF)
    this.drawString(this.fontRendererObj, "Password", this.width / 2 - 155, 95, 0xFFFFFFFF)
    this.usernameField.drawTextBox()
    this.passwordField.drawTextBox()
    super.drawScreen(x, y, delta)
  }

  override def actionPerformed(button: GuiButton){
    if(button.enabled){
      button.id match {
        case 0 => //Login
        case 1 => //Register
        case 2 => //Cancel
      }
    }
  }

  override def keyTyped(c: Char, i: Int){
    this.usernameField.textboxKeyTyped(c, i)
    this.passwordField.textboxKeyTyped(c, i)
    if(i == Keyboard.KEY_TAB){
      if(this.usernameField.isFocused){
        this.usernameField.setFocused(false)
        this.passwordField.setFocused(true)
      }
    }else if(i == Keyboard.KEY_RETURN) {
      this.actionPerformed(this.loginButton)
    }
  }

  override def mouseClicked(i: Int, i2: Int, i3: Int){
    super.mouseClicked(i, i2, i3)
    this.usernameField.mouseClicked(i, i2, i3)
    this.passwordField.mouseClicked(i, i2, i3)
  }

  override def updateScreen(){
    super.updateScreen()
    this.usernameField.updateCursorCounter()
    this.passwordField.updateCursorCounter()
  }

  override def onGuiClosed() = Keyboard.enableRepeatEvents(false)
}
