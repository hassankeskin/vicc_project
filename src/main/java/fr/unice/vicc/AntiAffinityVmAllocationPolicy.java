package fr.unice.vicc;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hassankeskin on 08/12/14.
 * @author Hassan KESKIN
 */
public class AntiAffinityVmAllocationPolicy extends VmAllocationPolicy {

    //To track the Host for each Vm. The string is the unique Vm identifier, composed by its id and its userId
    private Map<String, Host> vmTable;
    private int currentVMGroup;
    private boolean antiAffinity;

    public AntiAffinityVmAllocationPolicy(List<? extends Host> list) {
        super(list);
        vmTable = new HashMap<>();
        antiAffinity = true;
        currentVMGroup =0;
    }

    public Host getHost(Vm vm) {
        // We must recover the Host which hosting Vm
        return this.vmTable.get(vm.getUid());
    }

    public Host getHost(int vmId, int userId) {
        // We must recover the Host which hosting Vm
        return this.vmTable.get(Vm.getUid(userId, vmId));
    }

    public boolean allocateHostForVm(Vm vm, Host host) {
        if (host.vmCreate(vm)) {
            //the host is appropriate, we track it
            vmTable.put(vm.getUid(), host);
            return true;
        }
        return false;
    }

    public boolean allocateHostForVm(Vm vm) {
        for (Host h : getHostList()) {
            currentVMGroup = vm.getId()/100;
                for (Vm v : h.getVmList()) {
                    if (v.getId() / 100 == currentVMGroup) {
                        antiAffinity = false;
                    }
                }
                if (antiAffinity && h.vmCreate(vm)) {
                    vmTable.put(vm.getUid(), h);
                    return true;
                }else{
                    antiAffinity=true;
                }
        }
        return false;
    }

    public void deallocateHostForVm(Vm vm,Host host) {
        vmTable.remove(vm.getUid());
        host.vmDestroy(vm);
    }

    @Override
    public void deallocateHostForVm(Vm v) {
        //get the host and remove the vm
        vmTable.get(v.getUid()).vmDestroy(v);
    }

    public static Object optimizeAllocation() {
        return null;
    }

    @Override
    public List<Map<String, Object>> optimizeAllocation(List<? extends Vm> arg0) {
        //Static scheduling, no migration, return null;
        return null;
    }
}
