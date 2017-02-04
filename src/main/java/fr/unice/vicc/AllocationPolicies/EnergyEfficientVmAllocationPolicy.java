package fr.unice.vicc.allocationPolicies;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;

import java.util.*;

public class EnergyEfficientVmAllocationPolicy extends AbstractAllocationPolicy {

  /**
   * The default constructor from AbstractAllocationPolicy is enough.
   *
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
    Iterator<Host> it = getHostList().iterator();
    Host bestHost = it.next();
    Host tempHost = null;
    while (it.hasNext()) {
      tempHost = it.next();
      if (tempHost.getAvailableMips() < bestHost.getAvailableMips())
        bestHost = tempHost;
    }
    return bestHost != null && allocateHostForVm(vm, bestHost);
  }

}