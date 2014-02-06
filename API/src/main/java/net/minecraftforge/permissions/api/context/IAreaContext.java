package net.minecraftforge.permissions.api.context;

import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public interface IAreaContext {
    boolean overlapsWith(IAreaContext context);
    boolean contains(IAreaContext area);
    boolean contains(ILocationContext loc);
    List<IBlockLocationContext> getLocations();
}
