package jk_5.nailed.client.serverlist;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.ServerListEntryNormal;
import net.minecraft.client.renderer.Tessellator;

/**
 * No description given
 *
 * @author jk-5
 */
public class NailedServerEntry extends ServerListEntryNormal {

    private final FontRenderer fontRenderer;
    private final GuiMultiplayer guiMultiplayer;
    private final NailedServerData serverData;

    public NailedServerEntry(GuiMultiplayer owner, NailedServerData serverData){
        super(owner, serverData);
        this.guiMultiplayer = owner;
        this.serverData = serverData;
        this.fontRenderer = Minecraft.getMinecraft().fontRenderer;
    }

    @Override
    public void drawEntry(int var1, int x, int y, int var4, int var5, Tessellator tessellator, int var7, int var8, boolean var9){
        super.drawEntry(var1, x, y, var4, var5, tessellator, var7, var8, var9);
    }

    @Override
    public boolean mousePressed(int var1, int var2, int var3, int var4, int var5, int var6){
        return super.mousePressed(var1, var2, var3, var4, var5, var6);
    }

    @Override
    public void mouseReleased(int var1, int var2, int var3, int var4, int var5, int var6){
        super.mouseReleased(var1, var2, var3, var4, var5, var6);
    }
}
