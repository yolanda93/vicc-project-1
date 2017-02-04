package fr.unice.vicc.allocationPolicies;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;

import java.util.List;

/**
 *  This class implements the load balancing policy following the next fit and worst fit algorithms.
 *  Then both algorithms are compared in terms of reducing the SLA violation.
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
     * This method is used to add two integers. This is
     * a the simplest form of a class method, just to
     * show the usage of various javadoc Tags.
     * @param vm This is the ---
     * @return List<Map<String, Object>> This returns ----.
     */
    @Override
    public boolean allocateHostForVm(Vm vm) {
        return false;
    }

    /**
     * This method is used to add two integers. This is
     * a the simplest form of a class method, just to
     * show the usage of various javadoc Tags.
     * @param host This is the ---
     * @return List<Map<String, Object>> This returns ----.
     */
    @Override
    public boolean allocateHostForVm(Vm vm, Host host) {
        return false;
    }

    /**
     * This method is used to add two integers. This is
     * a the simplest form of a class method, just to
     * show the usage of various javadoc Tags.
     * @param v This is the ---
     * @return List<Map<String, Object>> This returns ----.
     */
    @Override
    public void deallocateHostForVm(Vm v) {
        //get the host and remove the vm
    }
}