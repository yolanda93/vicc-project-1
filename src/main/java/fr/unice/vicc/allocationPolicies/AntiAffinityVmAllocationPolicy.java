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
    for (Host h : getHostList()) {
      if (allocateHostForVm(vm, h) == true)
        return true;
    }

    return false;
  }


  @Override
  public boolean allocateHostForVm(Vm vm, Host h) {

    int vmAffinity = (vm.getId() == 0) ? 0 : vm.getId() / 100;

    //System.out.print("Host:"+ h.getId() + " Vm:" + vm.getId() + " Aff:"+vmAffinity +"\n");

    if (h.vmCreate(vm)) {
      if (affinityMap.isEmpty() || !affinityMap.containsKey(h.getId())) {
        hoster.put(vm.getUid(), h);
        affinityMap.put(h.getId(), new ArrayList<Integer>());
        affinityMap.get(h.getId()).add(vmAffinity);
        return true;
      } else {
        if (!affinityMap.get(h.getId()).contains(vmAffinity)) {
          hoster.put(vm.getUid(), h);
          affinityMap.get(h.getId()).add(vmAffinity);
          return true;
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
            h.vmDestroy(vm);
          }
        }
    }
}