package fr.unice.vicc;


import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.power.PowerHost;

import java.util.List;

/**
 * Created by hakankeskin on 17/12/14.
 */

public class BalanceLoadObserver extends SimEntity{

    public static final int OBSERVE = 555556;

    private List<PowerHost> hosts;

    private float delay;

    public static final float DEFAULT_DELAY = 1;

    public BalanceLoadObserver(List<PowerHost> hosts) {
        this(hosts, DEFAULT_DELAY);
    }

    public  BalanceLoadObserver(List<PowerHost> hosts, float delay){
        super("balanceObserver");
        this.hosts=hosts;
        this.delay = delay;
    }

    public void processEvent(SimEvent ev) {
        //I received an event
        switch(ev.getTag()) {
            case OBSERVE: //It is my custom event
                //I must observe the datacenter

                //Observation loop, re-observe in `delay` seconds
                send(this.getId(), delay, OBSERVE, null);
        }
    }

    @Override
    public void shutdownEntity() {
        Log.printLine(getName() + " is shutting down...");
    }

    @Override
    public void startEntity() {
        Log.printLine(getName() + " is starting...");
        send(this.getId(), delay, OBSERVE, null);
    }


}
