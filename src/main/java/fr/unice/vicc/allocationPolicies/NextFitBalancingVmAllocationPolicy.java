package fr.unice.vicc.allocationPolicies;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;

import java.util.List;
import java.util.Map;

/**
 *  This class implements the load balancing policy following the next fit.
 *
 *  @author Yolanda de la Hoz Simon
 *  @author Ignacio Uya Lassarte
 *  @author Marcos Bernal España
 *
 *  @version 1.0
 *  @since   2017-02-04
 */
public class NextFitBalancingVmAllocationPolicy extends AbstractAllocationPolicy {

    int pointer; // pointer to the last host with a vm allocated
    public NextFitBalancingVmAllocationPolicy(List<? extends Host> list) {
        super(list);
    }

    /**
     * This method allocate the virtual machine in a host machine following
     * the Next Fit algorithm. This algorithm allocates the vm closer to the last host used.
     *
     * @param vm This is the virtual machine to be allocated
     * @return boolean This returns false if there is no enough resources in the datacenter to host the vm
     */
    @Override
    public boolean allocateHostForVm(Vm vm) {
        List<Host> hostList = getHostList();
        for(int i=0; i<hostList.size(); i++) {
          Host host = hostList.get(pointer);
          if (host.vmCreate(vm)) { // check if there is enough resources
            this.hoster.put(vm.getUid(), host);
            return true;
          }
          if (pointer >= hostList.size() - 1) // Init pointer
            pointer = 0;
          else
            pointer++;
        }
        return false;
    }
  
    /**
     * This method allocates the virtual machine in a specified host.
     * For this purpose, first it checks if there is enough resources in
     * the host machine and it tries to allocate the virtual machine.
     *
     * @param vm The virtual machine to be allocated
     * @param host The host where the vm is allocated
     * @return boolean This returns false if there is no enough resources in the host machine
     */
    @Override
    public boolean allocateHostForVm(Vm vm, Host host) {
        if (host.vmCreate(vm)) { // Check if there is enough resources
            this.hoster.put(vm.getUid(), host);
            return true;
        }
        return false;
    }

    /**
     * This method removes the vm from the host where it is running
     */
    @Override
    public void deallocateHostForVm(Vm v) {
        this.hoster.get(v.getUid()).vmDestroy(v);
    }
}
}