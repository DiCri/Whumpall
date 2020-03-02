package it.manueldicriscito.whumpall;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DiCriTimer {
    public static final boolean TIMER_ON = true;
    public static final boolean TIMER_OFF = false;
    private class TimeSaved {
        String name;
        long time;
        int num;
    }
    private long startTime = 0;
    private boolean state = false;

    private List<TimeSaved> saves = new ArrayList<TimeSaved>();
    public DiCriTimer() {
        this.state = TIMER_OFF;
        startTime = System.currentTimeMillis();
    }
    public void start() {
        if(this.state==TIMER_OFF) {
            startTime = System.currentTimeMillis();
            state = TIMER_ON;
        }
    }
    public void reset() {
        startTime = System.currentTimeMillis();
    }
    public long get() {
        return (state == TIMER_OFF ? 0 : System.currentTimeMillis()-startTime);
    }
    public void stop() {
        state = TIMER_OFF;
    }
    public boolean isON() {
        return state == TIMER_ON;
    }
    public void save(String name) {
        TimeSaved timeSaved = new TimeSaved();
        timeSaved.name = name;
        timeSaved.time = get();
        timeSaved.num = 0;
        for(int k=0; k<saves.size(); k++) {
            if(timeSaved.name.equals(saves.get(k).name)) timeSaved.num++;
        }
        saves.add(timeSaved);

    }
    public float getLastSaved(String name) {
        if(saves.size()<1) return 0;
        int k = saves.size()-1;
        while(!saves.get(k).name.equals(name)) {
            if(k==0) return 0;
            k--;
        }
        return saves.get(k).time;
    }
    public void deleteAllSaved() {
        saves.clear();
    }

    public void dispose() {
        deleteAllSaved();
    }
}
