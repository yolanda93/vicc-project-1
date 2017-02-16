package fr.unice.vicc.allocationPolicies_merge;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;

import java.util.List;

/**
 * Created by fhermeni2 on 16/11/2015.
 * This Allocation Policy will allocate any VM in the first host available
 * without any other consideration.
 */
public class NaiveVmAllocationPolicy extends AbstractAllocationPolicy {
  /**
   * Being very basic, the Naive policy doesn't add any new attribute and can
   * safely use its parent's ones.
   * @param list
   */
  public NaiveVmAllocationPolicy(List<? extends Host> list) {
    super(list);
  }

  /**
   * This method will find a host for the provided VM.
   * In the case of the Naive Policy, we just need to find the first VM
   * available, so we will iterate over all the VM trying to allocate one
   * of them.
   * If successful, the method will return true. If all
   * the hosts are unavailable for allocation, it will return false.
   * @param vm the virtual machine to be allocated.
   * @return true if vm was successful allocated, false otherwise.
   */
  @Override
  public boolean allocateHostForVm(Vm vm) {
    for (Host h : getHostList()) {
      if (!allocateHostForVm(vm,h))
        continue;
      return true;
    }
    return false;
  }
}