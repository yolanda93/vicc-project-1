package fr.unice.vicc.allocationPolicies;

import java.util.List;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Vm;

/**
 * Author: ignacio
 * Date: 04/02/2017.
 */
public class NoViolationsVmAllocationPolicy extends AbstractAllocationPolicy {

  /**
   * The default constructor from AbstractAllocationPolicy is enough.
   * @param list
   */
  public NoViolationsVmAllocationPolicy(List<? extends Host> list) {
    super(list);
  }

  public boolean allocateHostForVm(Vm vm, Host host) {
    if (host.vmCreate(vm)) {
      //the host is appropriate, we track it
      hoster.put(vm.getUid(), host);
      return true;
    }
    return false;
  }

  public boolean allocateHostForVm(Vm vm) {

    for (Host h : getHostList()) {
      boolean suitableHost = false;
      for (Pe processingElem : h.getPeList()) {
        if (vm.getMips() < processingElem.getPeProvisioner().getAvailableMips()) {
          suitableHost = true;
          break;
        }
      }

      if (suitableHost) {
        if (h.vmCreate(vm)) {
          hoster.put(vm.getUid(), h);
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public void deallocateHostForVm(Vm v) {
    //get the host and remove the vm
    hoster.get(v.getUid()).vmDestroy(v);
  }

}
