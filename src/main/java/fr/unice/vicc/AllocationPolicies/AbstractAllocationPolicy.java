package fr.unice.vicc.allocationPolicies;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: ignacio
 * Date: 04/02/2017.
 * This class is a wrapper for the real policies. There are a number of methods common
 * for every policy, namely the getters, setters and constructor. They are mostly the
 * same in every case, so it makes no sense to repeat code in each policy.
 * <p/>
 * While this changes could have been made into the "VmAllocationPolicy" class
 * which this class itself extends from, we didn't want to change the wrapper that
 * interacts directly with the Factory, and we decided to instead make a package with all
 * our changes.
 *
 * @see VmAllocationPolicy
 */
public abstract class AbstractAllocationPolicy extends VmAllocationPolicy {

  /**
   * A map with the Uid of a vm and the reference to the host where it is allocated
   *
   * @key a VM Uid (obtained by means of VM.getUid(userId,vmId))
   * @value a host (the one where the VM is allocated)
   */
  protected Map<String, Host> hoster;

  /**
   * The default constructor for all the AllocationPolicies.
   *
   * @param list a list of hosts that will be managed by this policy.
   */
  public AbstractAllocationPolicy(List<? extends Host> list) {
    super(list);
    this.hoster = new HashMap<>();
  }

  /**
   * Method that will re-instantiate the object with a new list of
   * hosts.
   *
   * @param hostList the new list of hosts to be managed by this policy.
   */
  @Override
  protected void setHostList(List<? extends Host> hostList) {
    super.setHostList(hostList);
    hoster = new HashMap<>();
  }

  /**
   * Getter for the host. Given a VM, find where it is allocated.
   *
   * @param vm The virtual machine to find.
   * @return The host where it is allocated
   */
  @Override
  public Host getHost(Vm vm) {
    return hoster.get(vm.getUid());
  }

  /**
   * Getter for the host. Given the ID of a VM and the ID of the user,
   * find where it is allocated. Both these parameters form the unique ID
   * of a VM by means of the class function VM.getUid(userId, vmId)
   *
   * @param vmId   The id of a virtual machine to find.
   * @param userId The id of an user.
   * @return The host where it is allocated
   * See also {@link Vm#getUid(int, int)} method.
   */
  @Override
  public Host getHost(int vmId, int userId) {
    return hoster.get(Vm.getUid(userId, vmId));
  }

  /**
   * Used if the policy is dynamic , meaning that the VMs can be reallocated to a different
   * host.
   * If the policy is static (so VM cannot be reallocated dynamically) it will always return null.
   *
   * @param list list of virtual machines to reallocate.
   * @return new list with Uid,host if dynamic, null if static.
   */
  @Override
  public List<Map<String, Object>> optimizeAllocation(List<? extends Vm> list) {
    return null;
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
   * @return true if vm was successful allocated and false otherwise.
   */
  @Override
  public boolean allocateHostForVm(Vm vm, Host host) {
    if (!host.vmCreate(vm))
      return false;
    hoster.put(vm.getUid(), host);
    return true;
  }

  /**
   * De-allocates the vm passed by parameter.
   *
   * @param vm the virtual machine to be de-allocated.
   * @throws NullPointerException at runtime if the vm was not previously allocated.
   */
  @Override
  public void deallocateHostForVm(Vm vm) {
    hoster.get(vm.getUid()).vmDestroy(vm);
  }

}
