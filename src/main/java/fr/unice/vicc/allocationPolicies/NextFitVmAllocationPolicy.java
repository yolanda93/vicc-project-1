package fr.unice.vicc.allocationPolicies;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.power.PowerHost;

import java.util.List;
import java.util.Map;

/**
 * Author: ignacio
 * Date: 04/02/2017.
 */
public class NextFitVmAllocationPolicy extends AbstractAllocationPolicy {
  public NextFitVmAllocationPolicy(List<PowerHost> hosts) {
    super(hosts);
  }

  @Override
  public boolean allocateHostForVm(Vm vm) {
    return false;
  }

  @Override
  public boolean allocateHostForVm(Vm vm, Host host) {
    return false;
  }

  @Override
  public List<Map<String, Object>> optimizeAllocation(List<? extends Vm> list) {
    return null;
  }

  @Override
  public void deallocateHostForVm(Vm vm) {

  }
}
