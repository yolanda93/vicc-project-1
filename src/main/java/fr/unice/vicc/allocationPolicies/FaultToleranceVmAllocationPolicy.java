package fr.unice.vicc.allocationPolicies;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;

import java.util.List;
import java.util.Map;

/**
 * Created by fhermeni2 on 16/11/2015.
 */
public class FaultToleranceVmAllocationPolicy extends AbstractAllocationPolicy {

    public FaultToleranceVmAllocationPolicy(List<? extends Host> list) {
        super(list);
    }

    @Override
    public List<Map<String, Object>> optimizeAllocation(List<? extends Vm> list) {
        //In the naive policy it returns null
        return null;
    }

    @Override
    public boolean allocateHostForVm(Vm vm) {
        for (Host h : getHostList()) {
            if (h.vmCreate(vm)) {
                hoster.put(vm.getUid(), h);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean allocateHostForVm(Vm vm, Host host) {
        if (host.vmCreate(vm)) {
            hoster.put(vm.getUid(), host);
            return true;
        }
        return false;
    }

    //Given a Vm get its host and deallocate the vm
    @Override
    public void deallocateHostForVm(Vm vm) {
        hoster.remove(vm.getUid()).vmDestroy(vm);
    }
}