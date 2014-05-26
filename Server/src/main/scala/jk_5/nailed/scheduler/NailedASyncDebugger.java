package jk_5.nailed.scheduler;

/**
 * No description given
 *
 * @author jk-5
 */
public class NailedASyncDebugger {

    private NailedASyncDebugger next = null;
    private final int expiry;
    private final Class<? extends Runnable> clazz;

    NailedASyncDebugger(final int expiry, final Class<? extends Runnable> clazz) {
        this.expiry = expiry;
        this.clazz = clazz;
    }

    final NailedASyncDebugger getNextHead(final int time) {
        NailedASyncDebugger next, current = this;
        while(time > current.expiry && (next = current.next) != null){
            current = next;
        }
        return current;
    }

    final NailedASyncDebugger setNext(final NailedASyncDebugger next) {
        return this.next = next;
    }

    StringBuilder debugTo(final StringBuilder string) {
        for(NailedASyncDebugger next = this; next != null; next = next.next){
            string.append(next.clazz.getName()).append('@').append(next.expiry).append(',');
        }
        return string;
    }
}
