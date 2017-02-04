package fr.unice.vicc.allocationPolicies;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Vm;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Author: ignacio
 * Date: 04/02/2017.
 * <p/>
 * This class defines a policy where we try to respect the SLA as much as possible while also
 * trying to save some money on power consumption. The main problem here is that infringing the
 * SLA agreement is way more expensive than the cost of the electricity, so in most cases it is
 * better to focus on the SLA completely. On the other hand, small improvements on the energy
 * costs can significantly reduce the total energy spent but, as previously stated, the cost of
 * this energy is much lower than the reimbursement of the breaching the SLA agreement.
 */
public class GreedyVmAllocationPolicy extends AbstractAllocationPolicy {
  public static final double TOLERANCE_PERCENTAGE = 0.9;

  /**
   * The default constructor from AbstractAllocationPolicy is enough.
   *
   * @param list a list of hosts
   */
  public GreedyVmAllocationPolicy(List<? extends Host> list) {
    super(list);
  }


  /**
   * Mixes the allocation policy of EnergyEfficintVmAllocation and NoViolationsVmAllocation
   * It will try to allocate the VM to the most used host, but will only do so if the mips cost
   * of the vm is times "TOLERANCE_PERCENTAGE" (85) is less than the MIPS available in the host.
   *
   * @param vm the vm to be allocated
   * @return true if the allocation was successful or false otherwise
   */
  public boolean allocateHostForVm(Vm vm) {
    List<Host> hostList = getHostList();
    Collections.sort(hostList, new Comparator<Host>() {
      @Override
      public int compare(Host o1, Host o2) {
        return (int) (o1.getAvailableMips() - o2.getAvailableMips());
      }
    });

    for (Host host : hostList)
      for (Pe processingElem : host.getPeList()) {
        if (vm.getMips() * TOLERANCE_PERCENTAGE < processingElem.getPeProvisioner().getAvailableMips()
            && allocateHostForVm(vm, host))
          return true;
      }
    return false;
  }
}
