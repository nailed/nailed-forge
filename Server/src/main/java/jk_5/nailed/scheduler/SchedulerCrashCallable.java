package jk_5.nailed.scheduler;

import cpw.mods.fml.common.*;

import jk_5.nailed.api.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class SchedulerCrashCallable implements ICrashCallable {

    @Override
    public String getLabel() {
        return "Scheduler";
    }

    @Override
    public String call() throws Exception {
        return NailedAPI.getScheduler().toString();
    }
}
