package jk_5.nailed.map.script.api;

import com.google.common.collect.Lists;
import jk_5.nailed.api.scripting.ILuaAPI;
import jk_5.nailed.api.scripting.ILuaContext;
import jk_5.nailed.map.script.IAPIEnvironment;
import jk_5.nailed.map.script.ScriptingMachine;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public class OSApi implements ILuaAPI {

    private IAPIEnvironment apiEnvironment;
    private ScriptingMachine machine;
    private List<Timer> timers;
    private List<Alarm> alarms;
    private double clock;
    private double time;
    private int rebootTimer;
    private int nextTimerToken;
    private int nextAlarmToken;

    public OSApi(IAPIEnvironment _environment){
        this.apiEnvironment = _environment;
        this.machine = _environment.getMachine();
        this.nextTimerToken = 0;
        this.nextAlarmToken = 0;
        this.rebootTimer = 60;
    }

    public String[] getNames(){
        return new String[]{"os"};
    }

    public void startup(){
        this.timers = Lists.newArrayList();
        this.alarms = Lists.newArrayList();
        this.clock = 0.0D;
        this.time = this.machine.getMachine().getTimeOfDay();
    }

    public void advance(double _dt){
        synchronized(this.timers){
            this.clock += _dt;

            Iterator it = this.timers.iterator();
            while(it.hasNext()){
                Timer t = (Timer) it.next();
                t.timeLeft -= _dt;
                if(t.timeLeft <= 0.0D){
                    queueLuaEvent("timer", t.token);
                    it.remove();
                }
            }

        }

        synchronized(this.alarms){
            double timeLast = this.time;
            double timeNow = time;
            if(timeNow < timeLast){
                timeNow += 24.0D;
            }

            List<Alarm> finishedAlarms = null;
            Iterator it = this.alarms.iterator();
            while(it.hasNext()){
                Alarm al = (Alarm) it.next();
                double t = al.time;
                if(t < timeLast){
                    t += 24.0D;
                }
                if(timeNow >= t){
                    if(finishedAlarms == null){
                        finishedAlarms = Lists.newArrayList();
                    }
                    finishedAlarms.add(al);
                    it.remove();
                }
            }

            if(finishedAlarms != null){
                Collections.sort(finishedAlarms);
                it = finishedAlarms.iterator();
                while(it.hasNext()){
                    Alarm al = (Alarm) it.next();
                    queueLuaEvent("alarm", al.token);
                }
            }
        }

        if(this.rebootTimer > 0){
            this.rebootTimer -= 1;
        }
    }

    public void shutdown(){
        synchronized(this.timers){
            this.timers.clear();
        }

        synchronized(this.alarms){
            this.alarms.clear();
        }
    }

    public String[] getMethodNames(){
        return new String[]{
                "queueEvent",
                "startTimer",
                "setAlarm",
                "shutdown",
                "reboot",
                "computerID",
                "getComputerID",
                "clock",
                "getTicks"
        };
    }

    public Object[] callMethod(ILuaContext context, int method, Object[] args)
            throws Exception{
        switch(method){
            case 0:
                if((args.length == 0) || (args[0] == null) || (!(args[0] instanceof String))){
                    throw new Exception("Expected string");
                }
                queueLuaEvent((String) args[0], trimArray(args, 1));
                return null;
            case 1:
                if((args.length != 1) || (args[0] == null) || (!(args[0] instanceof Double))){
                    throw new Exception("Expected number");
                }
                double timer = (Double) args[0];
                synchronized(this.timers){
                    this.timers.add(new Timer(timer, this.nextTimerToken));
                }
                return new Object[]{this.nextTimerToken++};
            case 2:
                if((args.length != 1) || (args[0] == null) || (!(args[0] instanceof Double))){
                    throw new Exception("Expected number");
                }
                double time = (Double) args[0];
                if((time < 0.0D) || (time > 24.0D)){
                    throw new Exception("Number out of range");
                }
                synchronized(this.alarms){
                    this.alarms.add(new Alarm(time, this.nextAlarmToken));
                }
                return new Object[]{this.nextAlarmToken++};
            case 3:
                this.apiEnvironment.shutdown();
                return null;
            case 4:
                this.apiEnvironment.reboot();
                this.rebootTimer = 60;
                return null;
            case 5:
            case 6:
                return new Object[]{getMachineID()};
            case 7:
                synchronized(this.timers){
                    return new Object[]{this.clock};
                }
            case 8:
                return new Object[]{this.apiEnvironment.getMachine().getMachine().getWorld().getTotalWorldTime()};

            /*case 8:
                synchronized(this.alarms){
                    return new Object[]{Double.valueOf(this.time)};
                }

            case 9:
                synchronized(this.alarms){
                    return new Object[]{Integer.valueOf(this.machine.getDay())};
                }*/
        }
        return null;
    }

    private void queueLuaEvent(String event, Object... args){
        this.apiEnvironment.queueEvent(event, args);
    }

    private Object[] trimArray(Object[] array, int skip){
        return Arrays.copyOfRange(array, skip, array.length);
    }

    private int getMachineID(){
        return this.apiEnvironment.getMachineID();
    }

    private class Alarm implements Comparable<Alarm> {
        double time;
        int token;

        Alarm(double _time, int _token){
            this.time = _time;
            this.token = _token;
        }

        public int compareTo(Alarm o){
            double t = this.time;
            if(t < OSApi.this.time){
                t += 24.0D;
            }
            double ot = o.time;
            if(ot < OSApi.this.time){
                ot += 24.0D;
            }
            if(this.time < o.time)
                return -1;
            if(this.time > o.time){
                return 1;
            }
            return 0;
        }
    }

    private static class Timer {
        double timeLeft;
        int token;

        Timer(double _timeLeft, int _token){
            this.timeLeft = _timeLeft;
            this.token = _token;
        }
    }
}
