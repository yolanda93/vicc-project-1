package fr.unice.vicc.allocationPolicies;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fhermeni2 on 16/11/2015.
 */
public class AntiAffinityVmAllocationPolicy extends AbstractAllocationPolicy {


  public AntiAffinityVmAllocationPolicy(List<? extends Host> list) {
    super(list);
  }

  @Override
  public List<Map<String, Object>> optimizeAllocation(List<? extends Vm> list) {
    //In the naive policy it returns null
    return null;
  }

  public Map<Integer, List<Integer>> affinityMap = new HashMap<Integer, List<Integer>>();


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

  //Given a Vm get its host and deallocate the vm
  @Override
  public void deallocateHostForVm(Vm vm) {
        if(hoster.containsKey(vm.getUid())) {
            Host h = getHost(vm);


          if(h != null) {
            int vmAffinity = (vm.getId() == 0) ? 0 : vm.getId() / 100;

            affinityMap.get(h.getId()).remove(new Integer(vmAffinity));
            if (affinityMap.get(h.getId()).isEmpty())
              affinityMap.remove(h.getId());
            h.vmDestroy(vm);
            hoster.remove(vm.getUid());
          }
        }
    }
}