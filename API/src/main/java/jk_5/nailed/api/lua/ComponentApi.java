package jk_5.nailed.api.lua;

/**
 * No description given
 *
 * @author jk-5
 */
public abstract class ComponentApi {

    public static ComponentApi instance = null;

    public static Component newComponent(final Object owner) {
        if(instance != null){
            return instance.createNewComponent(owner);
        }
        return null;
    }

    protected abstract Component createNewComponent(final Object owner);
}
