package fr.unice.vicc.allocationPolicies;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;

import java.util.List;
import java.util.Collections;
import java.util.Comparator;

/**
 *  This class implements the load balancing policy following the worst fit algorithm.
 *
 *  @author Yolanda de la Hoz Simon
 *  @author Ignacio Uya Lassarte
 *  @author Marcos Bernal España
 *
 *  @version 1.0
 *  @since   2017-02-04
 */
public class WorstFitBalancingVmAllocationPolicy extends AbstractAllocationPolicy {

    public WorstFitBalancingVmAllocationPolicy(List<? extends Host> list) {
        super(list);
    }

    /**
     * This method allocate the virtual machine in a host machine following
     * the Worst Fit algorithm. This algorithm allocates first the vm in the host with more
     * resources available for this vm, regarding the both RAM and mips.
     *
     * @param vm This is the virtual machine to be allocated
     * @return boolean This returns false if there is no enough resources in the datacenter to host the vm
     */
    @Override
    public boolean allocateHostForVm(Vm vm) {

        Collections.sort(this.<Host>getHostList(), new Comparator<Host>() {
            @Override
            public int compare(Host h1, Host h2) {
                // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                return  (h1.getMaxAvailableMips()  + h1.getRam()) > (h2.getMaxAvailableMips()  + h2.getRam()) ? -1 : ((h1.getMaxAvailableMips()  + h1.getRam()) < (h2.getMaxAvailableMips()  + h2.getRam()) ? 1 : 0);
            }
        });

        for (Host h : getHostList()) {  // allocate the vm within the first host with enough resources
            if (h.vmCreate(vm)) {
                this.hoster.put(vm.getUid(), h);
                return true;
            }
        }
        return false;
    }

}
