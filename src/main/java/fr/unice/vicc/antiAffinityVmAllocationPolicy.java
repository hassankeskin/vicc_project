package fr.unice.vicc;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hassankeskin on 08/12/14.
 */
public class antiAffinityVmAllocationPolicy extends VmAllocationPolicy {

    //To track the Host for each Vm. The string is the unique Vm identifier, composed by its id and its userId
    private Map<String, Host> vmTable;
    private int currentVMGroup;
    private int previewVMGroup;
    private int antiAffinityCounter;

    public antiAffinityVmAllocationPolicy(List<? extends Host> list) {
        super(list);
        vmTable = new HashMap<>();
        antiAffinityCounter = 0;
        currentVMGroup =0;
        previewVMGroup =0;
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
        //First fit algorithm, run on the first suitable node
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (Host h : getHostList()) {
            currentVMGroup = vm.getId()/100;
            System.out.println("previewVMGroup: "+previewVMGroup+" currentVMGroup: "+currentVMGroup);
            System.out.println("vm ID: " + vm.getId() + " h id: " + h.getId());
            if(vmTable.isEmpty()){
                if (h.vmCreate(vm)) {
                    //track the host
                    vmTable.put(vm.getUid(), h);
                    previewVMGroup = 0;
                    System.out.println("Id Vm : "+vm.getId());
                    return true;
                }
            }else{
                if(h.getVmList().size()==0){
                    if (h.vmCreate(vm)) {
                        System.out.println("Creation de la vm"+vm.getId()+" dans le host"+h.getId());
                        //track the host
                        vmTable.put(vm.getUid(), h);
                        previewVMGroup = currentVMGroup;
                        antiAffinityCounter =0;
                        return true;
                    }else{
                        System.out.println("... mais host saturee");
                    }
                }else {
                    for (int i = 0; i < h.getVmList().size(); i++) {
                        Vm vmInCurrentHost = h.getVmList().get(i);
                        if (vmInCurrentHost.getId() / 100 != currentVMGroup) {
                            antiAffinityCounter++;
                        }
                    }
                    if (antiAffinityCounter == h.getVmList().size()) {
                        System.out.println("antiAffinityCounter OK");
                        if (h.vmCreate(vm)) {
                            System.out.println("Creation de la vm" + vm.getId() + " dans le host" + h.getId());
                            //track the host
                            vmTable.put(vm.getUid(), h);
                            previewVMGroup = currentVMGroup;
                            antiAffinityCounter = 0;
                            return true;
                        } else {
                            System.out.println("... mais host saturee deja "+h.getVmList()+" vm sur le host");
                        }
                    } else {
                        antiAffinityCounter = 0;
                    }
                }

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
