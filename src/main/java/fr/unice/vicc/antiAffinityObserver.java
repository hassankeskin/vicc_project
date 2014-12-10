package fr.unice.vicc;

import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.power.PowerHost;

import java.util.List;

/**
 * Created by hassankeskin on 08/12/14.
 */

public class antiAffinityObserver extends SimEntity{
    /** The custom event id, must be unique. */
    public static final int OBSERVE = 555555;

    private List<PowerHost> hosts;

    private double peak;

    private float delay;

    public static final float DEFAULT_DELAY = 1;

    public antiAffinityObserver(List<PowerHost> hosts) {
        this(hosts, DEFAULT_DELAY);
    }

    public antiAffinityObserver(List<PowerHost> hosts, float delay) {
        super("antiAffinityObserver");
        this.hosts = hosts;
        this.delay = delay;
    }


    /**
     * Get the datacenter instantaneous power.
     * @return a number in Watts
     */
    private double getPower() {
        double p = 0;
        for (PowerHost h : hosts) {
            p += h.getPower();
        }
        return p;
    }

    /*
    * This is the central method to implement.
    * CloudSim is event-based.
    * This method is called when there is an event to deal in that object.
    * In practice: create a custom event (here it is called OBSERVE) with a unique int value and deal with it.
     */
    @Override
    public void processEvent(SimEvent ev) {
        //I received an event
        switch(ev.getTag()) {
            case OBSERVE: //It is my custom event
                //I must observe the datacenter
                double cur = getPower();
                if (cur > peak) {
                    peak = cur;
                }
                //Observation loop, re-observe in `delay` seconds
                send(this.getId(), delay, OBSERVE, null);
        }
    }

    /**
     * Get the peak power consumption.
     * @return a number of Watts
     */
    public double getPeak() {
        return peak;
    }

    @Override
    public void shutdownEntity() {
        Log.printLine(getName() + " is shutting down...");
    }

    @Override
    public void startEntity() {
        Log.printLine(getName() + " is starting...");
        //I send to myself an event that will be processed in `delay` second by the method
        //`processEvent`
        send(this.getId(), delay, OBSERVE, null);
    }

    @Override
    public int getId() {
        return OBSERVE;
    }
}
