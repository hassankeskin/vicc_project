package fr.unice.vicc;

import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicy;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.power.PowerHost;

import java.util.List;

/**
 * Created by hassankeskin on 08/12/14.
 * @author Hassan KESKIN
 */

public class AntiAffinityObserver extends SimEntity{
    /** The custom event id, must be unique. */
    public static final int OBSERVE = 555555;

    private List<PowerHost> hosts;

    private float delay;

    public static final float DEFAULT_DELAY = 1;

    public AntiAffinityObserver(List<PowerHost> hosts) {
        super("antiAffinityObserver");
        this.hosts=hosts;
    }

    private void antiAffinityCheck(){
        for(Host h : hosts) {
            for (Vm v : h.getVmList()) {
                int currentVmGroup = v.getId() / 100;
                for (Vm v2 : h.getVmList()) {
                    if (currentVmGroup == v2.getId() / 100 && v != v2) {
                        System.out.println("La vm" + v + " et la vm" + v2 + " sont dans le meme host");
                    }
                }
            }
        }
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
                antiAffinityCheck();
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
        //I send to myself an event that will be processed in `delay` second by the method
        //`processEvent`
        send(this.getId(), delay, OBSERVE, null);
    }

    @Override
    public int getId() {
        return OBSERVE;
    }
}
