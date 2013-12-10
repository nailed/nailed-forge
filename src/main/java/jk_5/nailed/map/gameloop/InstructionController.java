package jk_5.nailed.map.gameloop;

import jk_5.nailed.NailedLog;
import jk_5.nailed.map.Map;
import jk_5.nailed.map.instruction.IInstruction;
import jk_5.nailed.map.instruction.InstructionList;
import jk_5.nailed.map.instruction.TimedInstruction;
import jk_5.nailed.map.stat.StatTypeManager;
import jk_5.nailed.map.stat.types.*;
import jk_5.nailed.players.Team;
import jk_5.nailed.util.ChatColor;
import lombok.Getter;

import java.util.Iterator;

/**
 * No description given
 *
 * @author jk-5
 */
public class InstructionController extends Thread {

    @Getter private final Map map;
    @Getter private boolean running = false;
    @Getter private boolean paused = false;
    @Getter private Team winner = null;
    private final InstructionList instructions;
    private final InstructionGameController controller = new InstructionGameController(this);

    public InstructionController(Map map){
        this.map = map;
        this.setDaemon(true);
        this.setName("InstructionController-" + map.getSaveFileName());
        if(map.getMappack() != null){
            this.instructions = map.getMappack().getInstructionList().cloneList();
        }else{
            this.instructions = new InstructionList();
        }
    }

    public void setWinner(Team winner){
        this.winner = winner;
        this.running = false;
        this.stopGame();
        StatTypeManager.instance().getStatType(StatTypeGameHasWinner.class).onWin(this);
        StatTypeManager.instance().getStatType(StatTypeIsWinner.class).onWinnerSet(this, winner);
    }

    public void startGame(){
        this.start();
        StatTypeManager.instance().getStatType(StatTypeGameloopRunning.class).onStart(this);
    }

    public void stopGame(){
        this.running = false;
        StatTypeManager.instance().getStatType(StatTypeGameloopStopped.class).onEnd(this);
        StatTypeManager.instance().getStatType(StatTypeGameloopRunning.class).onEnd(this);
    }

    public void pauseGame(){
        this.paused = true;
        StatTypeManager.instance().getStatType(StatTypeGameloopPaused.class).onPause(this);
    }

    public void resumeGame(){
        this.paused = false;
        StatTypeManager.instance().getStatType(StatTypeGameloopPaused.class).onResume(this);
    }

    @Override
    public void run(){
        this.running = true;
        this.paused = false;
        IInstruction current = null;
        Iterator<IInstruction> iterator = this.instructions.iterator();
        try{
            while(this.isRunning() && this.winner == null && (this.isPaused() || iterator.hasNext())){
                current = this.isPaused() ? current : iterator.next();
                if(current != null){
                    if(current instanceof TimedInstruction){
                        int ticks = 0;
                        TimedInstruction timed = (TimedInstruction) current;
                        while(this.isRunning() && this.winner == null && (this.isPaused() || !timed.executeTimed(this.controller, ticks))){
                            if(!this.isPaused()) ticks ++;
                            Thread.sleep(1000);
                        }
                    }else{
                        current.execute(this.controller);
                    }
                }else{
                    NailedLog.warning("Current instruction is null at %s", this.map.getSaveFileName());
                }
                if(this.isPaused()) Thread.sleep(1000);
            }
        }catch(Exception e){
            this.map.broadcastChatMessage(ChatColor.RED + e.getClass().getName() + " was thrown in the game loop. Game stopped!");
            if(current != null) this.map.broadcastChatMessage(ChatColor.RED + "Current instruction: " + this.getClass().getName());
            else this.map.broadcastChatMessage(ChatColor.RED + "Current instruction seems te be null. That isn\'t supposed to happen!");
            NailedLog.severe(e, "Exception in game loop for " + this.map.getSaveFileName());
        }
        this.running = false;
        StatTypeManager.instance().getStatType(StatTypeGameloopRunning.class).onEnd(this);
        this.controller.broadcastTimeRemaining("");
    }
}
