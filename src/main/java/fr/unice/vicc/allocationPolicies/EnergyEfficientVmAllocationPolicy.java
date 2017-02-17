package fr.unice.vicc.allocationPolicies;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;

import java.util.*;


/**
 *  @author Yolanda de la Hoz Simon
 *  @author Ignacio Uya Lassarte
 *  @author Marcos Bernal España
 *
 *  @version 1.0
 *  @since   2017-02-04
 *
 * This class implements an allocation policy with the objective to save as much energy
 * as possible. To see how we acomplish this check the "allocate" method.
 * @
 * @see AbstractAllocationPolicy
 */
public class EnergyEfficientVmAllocationPolicy extends AbstractAllocationPolicy {

  /**
   * The default constructor from AbstractAllocationPolicy is enough.
   * @param list a list of hosts
   */
  public EnergyEfficientVmAllocationPolicy(List<? extends Host> list) {
    super(list);
  }

  /**
   * Allocates a vm trying to save as much energy as possible (thus using the least amount of
   * physical machines) so it will allocate the vm in the most used machines disregarding SLA
   * agreements. This will provide some energy savings at the cost of a hefty reimbursement due
   * to not fulfilling said SLA agreement. In total it will probably be more costly than just trying
   * to avoid SLA infringement.
   *
   * @param vm the vm to allocate
   * @return true if the vm was successfully allocated, false otherwise
   * @throws NullPointerException if there are no hosts in the internal Host List.
   */
  public boolean allocateHostForVm(Vm vm) {
    List<Host> hostList = getHostList();
    Collections.sort(hostList, new Comparator<Host>() {
      @Override
      public int compare(Host o1, Host o2) {
        return (int)(o1.getAvailableMips() - o2.getAvailableMips());
      }
    });
    for (Host host : hostList) {
      if (!allocateHostForVm(vm, host)) {
        continue;
      }
      return true;
    }
    return false;
  }
}