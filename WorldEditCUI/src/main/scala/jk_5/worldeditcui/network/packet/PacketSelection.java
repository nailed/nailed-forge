package jk_5.worldeditcui.network.packet;

import jk_5.worldeditcui.render.*;
import jk_5.worldeditcui.render.region.*;

import scala.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class PacketSelection extends Packet {

    @Override
    protected void setupArguments() {
        this.setNumberOfArguments(1, 1);
    }

    @Override
    public void process() throws PacketException {
        if(this.getStringArgument(0).equals("cuboid")){
            WERenderer$.MODULE$.selection_$eq(Some.<Region>apply(new CuboidRegion()));
        }else if (this.getStringArgument(0).equals("polygon2d")){
            WERenderer$.MODULE$.selection_$eq(Some.<Region>apply(new PolygonRegion()));
        }else if (this.getStringArgument(0).equals("ellipsoid")){
            WERenderer$.MODULE$.selection_$eq(Some.<Region>apply(new EllipsoidRegion()));
        }else if (this.getStringArgument(0).equals("cylinder")){
            WERenderer$.MODULE$.selection_$eq(Some.<Region>apply(new CylinderRegion()));
        }else{
            throw new PacketException("Invalid selection type. Must be cuboid|polygon2d|ellipsoid|cylinder");
        }
    }
}
