package jk_5.nailed.client.scripting;

import jk_5.nailed.client.network.ClientNetworkHandler;
import jk_5.nailed.map.script.ScriptPacket;
import jk_5.nailed.map.script.Terminal;

/**
 * No description given
 *
 * @author jk-5
 */
public class MachineSynchronizer extends TerminalSynchronizer {

    public MachineSynchronizer(){
        this(Terminal.WIDTH, Terminal.HEIGHT);
    }

    public MachineSynchronizer(int terminalWidth, int terminalHeight){
        super(terminalWidth, terminalHeight);
        this.terminal = new Terminal(terminalWidth, terminalHeight);
    }

    public void pressKey(char ch, int keycode){
        ScriptPacket packet = new ScriptPacket.ClientKeyTyped(keycode, ch);
        ClientNetworkHandler.sendPacketToServer(packet);
    }

    public void typeString(String text){
        ScriptPacket packet = new ScriptPacket.ClientStringTyped(text);
        ClientNetworkHandler.sendPacketToServer(packet);
    }

    public void clickMouse(int charX, int charY, int button){
        ScriptPacket packet = new ScriptPacket.ClientMouseClicked(charX, charY, button);
        ClientNetworkHandler.sendPacketToServer(packet);
    }

    public void fireEvent(String event){
        ScriptPacket packet = new ScriptPacket.ClientEvent(event);
        ClientNetworkHandler.sendPacketToServer(packet);
    }

    public void shutdown(){
        ScriptPacket packet = new ScriptPacket.Interrupt(2);
        ClientNetworkHandler.sendPacketToServer(packet);
    }

    public void reboot(){
        ScriptPacket packet = new ScriptPacket.Interrupt(1);
        ClientNetworkHandler.sendPacketToServer(packet);
    }

    public void terminate(){
        ScriptPacket packet = new ScriptPacket.Interrupt(0);
        ClientNetworkHandler.sendPacketToServer(packet);
    }
}
