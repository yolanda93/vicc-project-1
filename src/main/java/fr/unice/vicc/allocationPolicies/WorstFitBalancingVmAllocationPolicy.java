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
                double m1 = computeMetric(h1,1);
                double m2 = computeMetric(h2,1);
                // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                return  m1 > m2 ? -1 : (m1 < m2) ? 1 : 0;
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

    /**
     * This is an auxiliar method that implements different evaluation metrics to be tested
     *
     * @param host The host that it is going to be evaluated.
     * @return double This returns the result of the evaluation metric on the available resources
     */
    public double computeMetric(Host host, int metric){
        double result=0;
        switch (metric){
            case 1: result =  host.getMaxAvailableMips()  + host.getRam();
                break;
            case 2: result =  host.getAvailableMips()  + host.getRam();
                break;
            case 3: result =  0.7*host.getMaxAvailableMips()  + 0.3*host.getRam();
                break;
            case 4: result =  0.3*host.getMaxAvailableMips()  + 0.7*host.getRam();
                break;
        }
        return result;
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