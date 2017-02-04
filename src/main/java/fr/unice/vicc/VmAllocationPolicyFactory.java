package fr.unice.vicc;

import fr.unice.vicc.allocationPolicies.*;
import org.cloudbus.cloudsim.VmAllocationPolicy;
import org.cloudbus.cloudsim.power.PowerHost;

import java.util.List;

/**
 * @author Fabien Hermenier
 */
public class VmAllocationPolicyFactory {

  /**
   * Return the VMAllocationPolicy associated to id
   *
   * @param id    the algorithm identifier
   * @param hosts the host list
   * @return the selected algorithm
   */
  VmAllocationPolicy make(String id, List<PowerHost> hosts) {

    switch (id) {

      case "antiaffinity":
        //return new AntiAffinityVmAllocationPolicy(hosts);
      case "dr":
        //return new FTVmAllocationPolicy(hosts);
      case "ft":
        //return new FTVmAllocationPolicy(hosts);
      case "nextFit":
        //return new NextFitVmAllocationPolicy(hosts);
      case "worstFit":
        //return new NaiveVmAllocationPolicy(hosts);
      case "greedy":
        //return new NaiveVmAllocationPolicy(hosts);
      case "naive":
        return new NaiveVmAllocationPolicy(hosts);
      case "noViolations":
        return new NoViolationsVmAllocationPolicy(hosts);
      case "energy":
        return new EnergyEfficientVmAllocationPolicy(hosts);
    }
    throw new IllegalArgumentException("No such policy '" + id + "'");
  }
}
