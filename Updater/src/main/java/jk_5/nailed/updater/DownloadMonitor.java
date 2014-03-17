package jk_5.nailed.updater;

import javax.swing.*;

/**
 * No description given
 *
 * @author jk-5
 */
@SuppressWarnings("unused")
public class DownloadMonitor {

    private final ProgressMonitor monitor;

    public DownloadMonitor(){
        monitor = new ProgressMonitor(null, "Nailed-Updater", "Checking for updates...", 0, 1);
        monitor.setMillisToPopup(0);
        monitor.setMillisToDecideToPopup(0);
    }

    public void setMaximum(int max){
        monitor.setMaximum(max);
    }

    public void setNote(String note){
        monitor.setNote(note);
    }

    public void setProgress(int progress){
        monitor.setProgress(progress);
    }

    public void close(){
        monitor.close();
    }
}
