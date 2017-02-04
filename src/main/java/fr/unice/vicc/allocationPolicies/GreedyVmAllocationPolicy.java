package fr.unice.vicc.allocationPolicies;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Vm;

import java.util.List;

/**
 * Author: ignacio
 * Date: 04/02/2017.
 * <p/>
 * This class defines a policy where the SLA is respected to the max. in order to
 * not lose money having to reimburse the users because the the VMs didn't have enough
 * MIPS available to fulfill the service contract.
 */
public class GreedyVmAllocationPolicy extends AbstractAllocationPolicy {

  /**
   * The default constructor from AbstractAllocationPolicy is enough.
   *
   * @param list a list of hosts
   */
  public GreedyVmAllocationPolicy(List<? extends Host> list) {
    super(list);
  }


  /**
   * Allocates a vm into the host if it has any PE (Processing Component) with enough MIPS
   * to satisfy the demand of the VM. It doesn't take into account any other metric, so it will
   * most likely miss-manage the use of the hosts leaving resources unused.
   *
   * @param vm the vm to be allocated
   * @return true if the allocation was successful or false otherwise
   */
  public boolean allocateHostForVm(Vm vm) {
    for (Host host : getHostList())
      for (Pe processingElem : host.getPeList())
        if (vm.getMips() < processingElem.getPeProvisioner().getAvailableMips())
          return allocateHostForVm(vm, host);
    return false;
  }
}
