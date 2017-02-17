package fr.unice.vicc.allocationPolicies;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  @author Yolanda de la Hoz Simon
 *  @author Ignacio Uya Lassarte
 *  @author Marcos Bernal España
 *
 *  @version 1.0
 *  @since   2017-02-04
 <p/>
 * This class defines a policy where we try to respect the SLA as much as possible while also
 * trying to store the VMs in different nodes according to their IDs affinity. All VMs with an
 * id between [X00-X99] (where X is the same digit (0,1,2...)) must be on distinct nodes, balancing
 * between the 2 switches(Ml110G4 nodes connected to one and Ml110G5 to another).
 *
 * The main problem here is that when a fault tolerant schedule places two VM replicas over the same host,
 * if the host fails both VMs get lost, besides when one switch is turned off all host connected to it lost
 * the connection, and then their VMs can be considered . With this policy, the energy is slightly increased but the penalties
 * are reduced in half resulting into increasing the Revenue.
 *
 * @see AbstractAllocationPolicy
 */
public class DisasterRecoveryVmAllocationPolicy extends AbstractAllocationPolicy {

  /**
   * The default constructor from AbstractAllocationPolicy is called to build the class.
   * Then the attributes used in this class are initiated.
   *
   * @param list a list of hosts
   */
  public DisasterRecoveryVmAllocationPolicy(List<? extends Host> list) {
    super(list);
    affinityMap = new HashMap<Integer, List<Integer>>();
    affinityNodesSwitch = new HashMap<Integer, Integer[]>();
  }

  /**
   * A map with the ids of the hosts and the affinities of the VMs stored in each host.
   * At the start, all host has no VM assigned, so no affinities are stored.
   *
   * @key a Host id
   * @value a list of affinities of the VM stored in each host
   */
  private Map<Integer, List<Integer>> affinityMap;

  /**
   * A map with the ids of the hosts and the affinities of the VMs stored in each host.
   * At the start, all host has no VM assigned, so no affinities are stored.
   *
   * @key an affinity ID that belongs to a group of VMs (X00-X99)
   * @value an array of two values that represents the number of affinitied VMs connected to each
   * switch (2 switches - 2 ints values)
   */
  private Map<Integer, Integer[]> affinityNodesSwitch;


  /**
   * The hosts are connected "physically" to two switches. All Ml110G4 machines (host with 3720 MILPS) are connected
   * to switch 0 and all machines Ml110G5 (hosts with 5320 MIPS) are connected to switch 1.
   * With this simple method we determine the number from the ID of the host.
   *
   * @param h is the host used to specify the switch connected
   * @return the number of the switch connected to the host (0 or 1)
   */
  private int getHostSwitchId(Host h){
    if (h.getTotalMips() > 3721) //Ml110G4 = 2pes* x 1860 MILPS => 3720
      return 1;

    return 0;
  }

  /**
   * In order to balance the number of affinity nodes in the switches, this method provides a comparison between
   * the number of VM of the same affinity connected to each switch.
   *
   * @param h is the host used to specify if it is worthy to allocate the VM with affinity Affinity
   * @param Affinity value of the VM that is going to be allocated
   * @return true if host can be used for storing a VM with a specified affinity (its switch has less affinity VMs connected)
   */
  private boolean hostWithSwitchSuitable(Host h, int Affinity){
    Integer[] switchesAffinity = affinityNodesSwitch.get(Affinity);
    int otherSwitch = (getHostSwitchId(h) == 0) ? 1 : 0;
    return switchesAffinity[getHostSwitchId(h)] <= switchesAffinity[otherSwitch];
  }


  /**
   * It will allocate the VM in a host which VMs does not share the same Affinity taking into account if the switch
   * that is connected have connected more VMs of that affinity. Otherwise, it will look for another suitable host.
   * In order to do that, the HashMap affinityMap register the affinity of each host in order to
   * avoid having two VMs with the same affinity and the affinityNodesSwitch registers the number of VMs
   * (split by affinity) that has each switch.
   *
   * @param vm the vm to be allocated
   * @return true if the allocation was successful or false otherwise
   */
  @Override
  public boolean allocateHostForVm(Vm vm) {

    int vmAffinity = (vm.getId() == 0) ? 0 : vm.getId() / 100;
    if (affinityNodesSwitch.isEmpty() || !affinityNodesSwitch.containsKey(vmAffinity)) {
      affinityNodesSwitch.put(vmAffinity, new Integer[2]);
      affinityNodesSwitch.get(vmAffinity)[0] = 0;
      affinityNodesSwitch.get(vmAffinity)[1] = 0;
    }

    for (Host h : getHostList()) {
      if (hostWithSwitchSuitable(h, vmAffinity)) {
        if (affinityMap.isEmpty() || !affinityMap.containsKey(h.getId())) {
          if (allocateHostForVm(vm, h)) {
            affinityMap.put(h.getId(), new ArrayList<Integer>());
            affinityMap.get(h.getId()).add(vmAffinity);
            affinityNodesSwitch.get(vmAffinity)[getHostSwitchId(h)]++;
            return true;
          }
        } else {
          if (!affinityMap.get(h.getId()).contains(vmAffinity)) {
            if (allocateHostForVm(vm, h)) {
              affinityMap.get(h.getId()).add(vmAffinity);
              affinityNodesSwitch.get(vmAffinity)[getHostSwitchId(h)]++;
              return true;
            }
          }
        }
      }
    }

    return false;
  }

  /**
   * De-allocates the vm passed by parameter and remove its value from the host that was
   * storing it. Remove the entries in the Maps variables that link the vm that is going
   * to be removed with its host.
   *
   * @param vm the virtual machine to be de-allocated.
   */
  @Override
  public void deallocateHostForVm(Vm vm) {
    if (hoster.containsKey(vm.getUid()) && getHost(vm) != null) {
      Host h = getHost(vm);
      int vmAffinity = (vm.getId() == 0) ? 0 : vm.getId() / 100;

      affinityMap.get(h.getId()).remove(new Integer(vmAffinity));

      if (affinityMap.get(h.getId()).isEmpty())
        affinityMap.remove(h.getId());
      affinityNodesSwitch.get(vmAffinity)[getHostSwitchId(h)]--;

      super.deallocateHostForVm(vm);
    }
  }

}