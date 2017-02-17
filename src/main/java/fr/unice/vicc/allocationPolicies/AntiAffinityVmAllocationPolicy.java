package fr.unice.vicc.allocationPolicies;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Marcos
 * Date: 17/02/2017.
 <p/>
 * This class defines a policy where we try to respect the SLA as much as possible while also
 * trying to store the VMs in different nodes acording to their IDs. All Vms with an id between [X00-X99]
 * must be on distinct nodes, where X is the same digit (0,1,2...).
 *
 * The main problem here is that when a fault tolerant schedule places two VM replicas over the same host,
 * if the host fails both VMs get lost. With this policy, the energy is slightly increased but the penalties
 * are reduced in half resulting into increasing the Revenue.
 *
 * @see AbstractAllocationPolicy
 */
public class AntiAffinityVmAllocationPolicy extends AbstractAllocationPolicy {

  /**
   * The default constructor from AbstractAllocationPolicy is enough.
   *
   * @param list a list of hosts
   */
  public AntiAffinityVmAllocationPolicy(List<? extends Host> list) {
    super(list);
  }

  /**
   * A map with the ids of the hosts and the affinities of the VMs stored in each host.
   * At the start, all host has no VM assigned, so no affinities are stored.
   *
   * @key a Host id
   * @value a list of affinities of the VM stored in each host
   */
  public Map<Integer, List<Integer>> affinityMap = new HashMap<Integer, List<Integer>>();

  /**
   * It will allocate the VM in a host which VMs does not share the same Affinity.
   * In order to do that, a the HashMap affinityMap register the affinity of each host in order to
   * avoid having two VMs with the same affinity.
   *
   * @param vm the vm to be allocated
   * @return true if the allocation was successful or false otherwise
   */
  @Override
  public boolean allocateHostForVm(Vm vm) {

    int vmAffinity = (vm.getId() == 0) ? 0 : vm.getId() / 100;

    for (Host h : getHostList()) {
      if (affinityMap.isEmpty() || !affinityMap.containsKey(h.getId())) {
        if (allocateHostForVm(vm, h)) {
          affinityMap.put(h.getId(), new ArrayList<Integer>());
          affinityMap.get(h.getId()).add(vmAffinity);
          return true;
        }
      } else {
        if (!affinityMap.get(h.getId()).contains(vmAffinity)) {
          if (allocateHostForVm(vm, h)) {
            affinityMap.get(h.getId()).add(vmAffinity);
            return true;
          }
        }
      }
    }

    return false;
  }

  /**
   * De-allocates the vm passed by parameter and remove its value from the host that was
   * storing it.
   *
   * @param vm the virtual machine to be de-allocated.
   * @throws NullPointerException at runtime if the vm was not previously allocated.
   */
  @Override
  public void deallocateHostForVm(Vm vm) {
        if(hoster.containsKey(vm.getUid())) {
            Host h = getHost(vm);


          if(h != null) {
            int vmAffinity = (vm.getId() == 0) ? 0 : vm.getId() / 100;

            affinityMap.get(h.getId()).remove(new Integer(vmAffinity));
            if (affinityMap.get(h.getId()).isEmpty())
              affinityMap.remove(h.getId());

            super.deallocateHostForVm(vm);
          }
        }
    }
}