package jk_5.nailed.client.serverlist;

import net.minecraft.client.multiplayer.ServerData;

/**
 * No description given
 *
 * @author jk-5
 */
public class NailedServerData extends ServerData {

    public NailedServerData(String ip){
        super("Nailed", ip);
    }

    @Override
    public boolean func_147408_b(){
        return true;
    }
}
