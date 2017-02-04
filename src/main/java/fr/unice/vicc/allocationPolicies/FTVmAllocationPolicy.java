package fr.unice.vicc.allocationPolicies;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;

import java.util.List;
import java.util.Map;

/**
 * Created by fhermeni2 on 16/11/2015.
 * This Allocation Policy will allocate any VM in the first host available
 * without any other consideration.
 */
public class FTVmAllocationPolicy extends AbstractAllocationPolicy {
  /**
   * Being very basic, the Naive policy doesn't add any new attribute and can
   * safely use its parent's ones.
   *
   * @param list
   */
  public FTVmAllocationPolicy(List<? extends Host> list) {
    super(list);
  }


  /**
   * The naive policy is static, meaning that it will not reallocate the vms
   * after the initial allocation, so this method will always return null
   *
   * @param list list of virtual machines.
   * @return always null since there are no dynamic optimizations to be made.
   */
  @Override
  public List<Map<String, Object>> optimizeAllocation(List<? extends Vm> list) {
    //For the naive policy it returns null
    return null;
  }

  /**
   * This method will find a host for the provided VM.
   * In the case of the Naive Policy, we just need to find the first VM
   * available, so we will iterate over all the VM trying to allocate one
   * of them.
   * If successful, the method will return true. If all
   * the hosts are unavailable for allocation, it will return false.
   *
   * @param vm the virtual machine to be allocated.
   * @return true if vm was successful allocated, false otherwise.
   */
  @Override
  public boolean allocateHostForVm(Vm vm) {
    for (Host h : getHostList()) {
      if (!h.vmCreate(vm)) continue;
      hoster.put(vm.getUid(), h);
      return true;
    }
    return false;
  }

  /**
   * This method will find a host for the provided VM.
   * In the case of the Naive Policy, we just need to find the first VM
   * available, so we will iterate over all the VM trying to allocate one
   * of them.
   * If  successful, the method will return true. If all
   * the hosts are unavailable for allocation, it will return false.
   *
   * @param vm the virtual machine to be allocated.
   * @return true if vm was successful allocated, false otherwise.
   */
  @Override
  public boolean allocateHostForVm(Vm vm, Host host) {
    if (!host.vmCreate(vm))
      return false;
    hoster.put(vm.getUid(), host);
    return true;
  }

  /**
   * Given a Virtual Machine, deallocate it from wherever is allocated.
   *
   * @param vm the Virtual Machine to deallocate
   */
  @Override
  public void deallocateHostForVm(Vm vm) {
    hoster.remove(vm.getUid()).vmDestroy(vm);
  }
}