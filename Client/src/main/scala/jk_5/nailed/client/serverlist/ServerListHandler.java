package jk_5.nailed.client.serverlist;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.ServerSelectionList;
import net.minecraftforge.client.event.GuiOpenEvent;

import java.util.Iterator;

/**
 * No description given
 *
 * @author jk-5
 */
public class ServerListHandler {

    private NailedServerData serverData = new NailedServerData("minecraft.kogint.tk:25566");
    private NailedServerEntry entry;
    private boolean isOpen = false;
    private GuiMultiplayer gui;
    private ServerSelectionList list;

    @SuppressWarnings("unchecked")
    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event){
        if(event.gui instanceof GuiMultiplayer){
            isOpen = true;
            this.gui = (GuiMultiplayer) event.gui;
            this.entry = new NailedServerEntry(this.gui, this.serverData);
        }else{
            isOpen = false;
            this.list = null;
        }
    }

    @SuppressWarnings("unchecked")
    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event){
        if(this.isOpen){
            if(this.gui == null){
                this.isOpen = false;
                return;
            }
            this.list = this.gui.field_146803_h;
            if(!this.list.field_148198_l.contains(entry)){
                Iterator it = this.list.field_148198_l.iterator();
                while(it.hasNext()){
                    if(it.next() instanceof NailedServerData){
                        it.remove();
                    }
                }
                this.list.field_148198_l.add(0, entry);
            }
        }
    }
}
