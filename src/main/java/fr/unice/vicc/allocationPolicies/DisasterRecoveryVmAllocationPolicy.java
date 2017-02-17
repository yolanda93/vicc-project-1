package fr.unice.vicc.allocationPolicies;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by marcos on 16/02/17.
 */
public class DisasterRecoveryVmAllocationPolicy extends AbstractAllocationPolicy {


  public DisasterRecoveryVmAllocationPolicy(List<? extends Host> list) {
    super(list);
    affinityMap = new HashMap<Integer, List<Integer>>();
    affinityNodesSwitch = new HashMap<Integer, Integer[]>();
  }

  @Override
  public List<Map<String, Object>> optimizeAllocation(List<? extends Vm> list) {
    //In the naive policy it returns null
    return null;
  }

  // Mapper with key as host id and value as list of VM affinity_ids
  public Map<Integer, List<Integer>> affinityMap;

  // Mapper with key affinity_ids and value as a list of the switch containing the n of affinity nodes
  public Map<Integer, Integer[]> affinityNodesSwitch;

  public int getHostSwitchId(Host h){
    if (h.getId() > 399)
      return 1;

    return 0;
  }

  private boolean hostWithSwitchSuitable(Host h, Integer[] switches){
    int otherSwitch = (getHostSwitchId(h) == 0) ? 1 : 0;
    return switches[getHostSwitchId(h)] <= switches[otherSwitch];
  }

  @Override
  public boolean allocateHostForVm(Vm vm) {

    int vmAffinity = (vm.getId() == 0) ? 0 : vm.getId() / 100;
    if (affinityNodesSwitch.isEmpty() || !affinityNodesSwitch.containsKey(vmAffinity)) {
      affinityNodesSwitch.put(vmAffinity, new Integer[2]);
      affinityNodesSwitch.get(vmAffinity)[0] = 0;
      affinityNodesSwitch.get(vmAffinity)[1] = 0;
    }

    for (Host h : getHostList()) {
      if (hostWithSwitchSuitable(h, affinityNodesSwitch.get(vmAffinity))) {
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

  //Given a Vm get its host and deallocate the vm
  @Override
  public void deallocateHostForVm(Vm vm) {
    if(hoster.containsKey(vm.getUid())) {
      Host h = hoster.remove(vm);

      if(h != null) {
        int vmAffinity = (vm.getId() == 0) ? 0 : vm.getId() / 100;

        affinityMap.get(h.getId()).remove(new Integer(vmAffinity));
        if (affinityMap.get(h.getId()).isEmpty())
          affinityMap.remove(h.getId());
        affinityNodesSwitch.get(vmAffinity)[getHostSwitchId(h)]--;
        h.vmDestroy(vm);
      }
    }
  }
}