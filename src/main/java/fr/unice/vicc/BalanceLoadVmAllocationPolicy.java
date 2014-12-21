package fr.unice.vicc;

import org.cloudbus.cloudsim.VmAllocationPolicy;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.Host;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hakankeskin on 17/12/14.
 */
public class BalanceLoadVmAllocationPolicy extends VmAllocationPolicy{
    //To track the Host for each Vm. The string is the unique Vm identifier, composed by its id and its userId
    private Map<String, Host> vmTable;

    public BalanceLoadVmAllocationPolicy(List<? extends Host> list) {
        super(list);
        vmTable = new HashMap<>();
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

        Host meilleureHote = null;
        double meilleureMips = 0;

            for (Host h : getHostList()) {
                double mipsCourant = h.getAvailableMips();
                if(mipsCourant > meilleureMips){
                    meilleureHote = h;
                    meilleureMips = mipsCourant;
                }
            }
            if (meilleureHote.vmCreate(vm)) {
                vmTable.put(vm.getUid(), meilleureHote);
                return true;
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
