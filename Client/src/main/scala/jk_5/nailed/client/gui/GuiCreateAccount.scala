package jk_5.nailed.client.gui

import net.minecraft.client.gui.{GuiButton, GuiTextField, GuiScreen}
import org.lwjgl.input.Keyboard
import java.util
import jk_5.nailed.client.network.ClientNetworkHandler
import jk_5.nailed.network.NailedPacket
import jk_5.nailed.network.NailedPacket.{LoginResponse, FieldStatus}
import jk_5.nailed.util.ChatColor

/**
 * No description given
 *
 * @author jk-5
 */
class GuiCreateAccount(previous: GuiLogin) extends GuiScreen {

  var usernameField: GuiTextField = null
  var emailField: GuiTextField = null
  var passwordField: GuiTextField = null
  var passwordConfirmField: GuiTextField = null
  var fullNameField: GuiTextField = null

  var registerButton: GuiButton = null
  var backButton: GuiButton = null

  var enableRegister = true

  val inputs = new Array[GuiTextField](5)
  val info = Array.fill[String](5)("")
  val status = Array.fill[Int](5)(-1)

  override def initGui(){
    Keyboard.enableRepeatEvents(true)
    val list = this.buttonList.asInstanceOf[util.List[GuiButton]]
    list.clear()
    list.add({this.registerButton = new GuiButton(0, this.width / 2 - 101, 210, 98, 20, "Register");this.registerButton})
    list.add({this.backButton = new GuiButton(1, this.width / 2 + 3, 210, 98, 20, "Back");this.backButton})

    this.usernameField = new GuiTextField(this.fontRendererObj, this.width / 2 - 100, 60, 200, 20)
    this.usernameField.setMaxStringLength(32767)
    this.usernameField.setFocused(true)
    this.emailField = new GuiTextField(this.fontRendererObj, this.width / 2 - 100, 90, 200, 20)
    this.emailField.setMaxStringLength(32767)
    this.fullNameField = new GuiTextField(this.fontRendererObj, this.width / 2 - 100, 120, 200, 20)
    this.fullNameField.setMaxStringLength(32767)
    this.passwordField = new GuiTextField(this.fontRendererObj, this.width / 2 - 100, 150, 200, 20) //TODO: masked
    this.passwordField.setMaxStringLength(32767)
    this.passwordConfirmField = new GuiTextField(this.fontRendererObj, this.width / 2 - 100, 180, 200, 20) //TODO: masked
    this.passwordConfirmField.setMaxStringLength(32767)

    inputs(0) = this.usernameField
    inputs(1) = this.emailField
    inputs(2) = this.fullNameField
    inputs(3) = this.passwordField
    inputs(4) = this.passwordConfirmField
  }

  override def drawScreen(x: Int, y: Int, delta: Float){
    this.drawDefaultBackground()
    this.drawCenteredString(this.fontRendererObj, "Create a new Nailed account", this.width / 2, 20, 0xFFFFFFFF)
    this.drawString(this.fontRendererObj, "Username", this.width / 2 - 155, 65, 0xFFFFFFFF)
    this.drawString(this.fontRendererObj, "Email", this.width / 2 - 155, 95, 0xFFFFFFFF)
    this.drawString(this.fontRendererObj, "Name", this.width / 2 - 155, 125, 0xFFFFFFFF)
    this.drawString(this.fontRendererObj, "Password", this.width / 2 - 155, 155, 0xFFFFFFFF)
    this.drawString(this.fontRendererObj, "Password", this.width / 2 - 155, 185, 0xFFFFFFFF)

    this.drawString(this.fontRendererObj, this.info(0), this.width / 2 + 105, 65, 0xFFFFFFFF)
    this.drawString(this.fontRendererObj, this.info(1), this.width / 2 + 105, 95, 0xFFFFFFFF)
    this.drawString(this.fontRendererObj, this.info(2), this.width / 2 + 105, 125, 0xFFFFFFFF)
    this.drawString(this.fontRendererObj, this.info(3), this.width / 2 + 105, 155, 0xFFFFFFFF)
    this.drawString(this.fontRendererObj, this.info(4), this.width / 2 + 105, 185, 0xFFFFFFFF)

    this.usernameField.drawTextBox()
    this.emailField.drawTextBox()
    this.fullNameField.drawTextBox()
    this.passwordField.drawTextBox()
    this.passwordConfirmField.drawTextBox()
    super.drawScreen(x, y, delta)
  }

  override def actionPerformed(button: GuiButton){
    if(button.enabled){
      button.id match {
        case 0 =>
          enableRegister = false
          ClientNetworkHandler.sendPacketToServer(new NailedPacket.CreateAccount(this.usernameField.getText, this.emailField.getText, this.fullNameField.getText, this.passwordField.getText))
        case 1 => mc.displayGuiScreen(previous)
      }
    }
  }

  override def keyTyped(c: Char, i: Int){
    this.usernameField.textboxKeyTyped(c, i)
    this.emailField.textboxKeyTyped(c, i)
    this.fullNameField.textboxKeyTyped(c, i)
    this.passwordField.textboxKeyTyped(c, i)
    this.passwordConfirmField.textboxKeyTyped(c, i)
    if(i == Keyboard.KEY_TAB){
      if(this.usernameField.isFocused){
        this.usernameField.setFocused(false)
        this.emailField.setFocused(true)
      }else if(this.emailField.isFocused){
        this.emailField.setFocused(false)
        this.fullNameField.setFocused(true)
      }else if(this.fullNameField.isFocused){
        this.fullNameField.setFocused(false)
        this.passwordField.setFocused(true)
      }else if(this.passwordField.isFocused){
        this.passwordField.setFocused(false)
        this.passwordConfirmField.setFocused(true)
      }
    }else if(i == Keyboard.KEY_RETURN) {
      this.actionPerformed(this.registerButton)
    }

    for(i <- 0 until this.inputs.length){
      val f = this.inputs(i)
      if(f != null && f.isFocused){
        ClientNetworkHandler.sendPacketToServer(new NailedPacket.FieldStatus(i, f.getText, 0))
      }
    }
  }

  override def mouseClicked(i: Int, i2: Int, i3: Int){
    super.mouseClicked(i, i2, i3)
    this.usernameField.mouseClicked(i, i2, i3)
    this.emailField.mouseClicked(i, i2, i3)
    this.fullNameField.mouseClicked(i, i2, i3)
    this.passwordField.mouseClicked(i, i2, i3)
    this.passwordConfirmField.mouseClicked(i, i2, i3)
  }

  override def updateScreen(){
    super.updateScreen()
    this.usernameField.updateCursorCounter()
    this.emailField.updateCursorCounter()
    this.fullNameField.updateCursorCounter()
    this.passwordField.updateCursorCounter()
    this.passwordConfirmField.updateCursorCounter()

    this.registerButton.enabled = info(0).contains("OK") && info(1).contains("OK") && info(2).contains("OK") && info(3).contains("OK") && info(4).contains("OK") && enableRegister
  }

  def onFieldStatus(status: FieldStatus){
    this.info(status.field) = ChatColor.getByChar(status.status.toChar) + "" + status.content
    if(status.status.toChar == 'c') return
    if(status.field == 3 || status.field == 4){
      if(this.passwordField.getText != this.passwordConfirmField.getText){
        info(4) = ChatColor.RED + "Does not match password above"
      }
    }
  }

  def onResponse(response: LoginResponse){
    enableRegister = true
    response.state match {
      case 0 => mc.displayGuiScreen(null)
    }
  }

  override def onGuiClosed() = Keyboard.enableRepeatEvents(false)
}
