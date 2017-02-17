package fr.unice.vicc.allocationPolicies;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 *  This class implements the Fault-tolerance for standalone VMs policy. In this policy we assure the tolerance for all VMs
 *  that has an residual 1 of modulus 10.
 *
 *  @author Yolanda de la Hoz Simon
 *  @author Ignacio Uya Lassarte
 *  @author Marcos Bernal España
 *
 *  @version 1.0
 *  @since   2017-02-04
 */
public class FaultToleranceVmAllocationPolicy extends AbstractAllocationPolicy {

  /**
   * A map with the UIDs of the VMs and the auxiliaries hosts used in case the main host fails.
   *
   * @key the UID of a VM
   * @value the auxiliary host used as an immediately replacement
   */
  protected Map<String, Host> auxiliarHosterForFaults;

  /**
   * A map with the ids of the auxiliar host and the specs reserved for the vm that reserved the host
   *
   * @key the ID of the auxiliary host
   * @value the reserved specs of the auxiliary host. ROM, RAM and MIPS
   */
  protected Map<Integer, Long[]> specsOfAuxiliarHost;

  /**
   * The default constructor from AbstractAllocationPolicy is called to build the class.
   * Then the attributes used in this class are initiated.
   *
   * @param list a list of hosts
   */
  public FaultToleranceVmAllocationPolicy(List<? extends Host> list) {
    super(list);
    auxiliarHosterForFaults =  new HashMap<String, Host>();
    specsOfAuxiliarHost = new HashMap<Integer, Long[]>();
  }


  private Host getHost(int ID){
    Host aim_h = null;
    for(Host h : getHostList()){
      if( h.getId() == ID )
        aim_h =  h;
    }
    return aim_h;
  }


  /**
   * This method will find a host for the provided VM.
   * In this policy, the host have to be check in order to detect if some resources were allocated before
   * as an auxiliary host.
   *
   * @param vm the virtual machine to be allocated.
   * @return true if vm was successful allocated and false otherwise.
   */
  @Override
  public boolean allocateHostForVm(Vm vm, Host host) {
    if (specsOfAuxiliarHost.containsKey(host.getId())) {
      long aux_storage = specsOfAuxiliarHost.get(host.getId())[0];
      long aux_ram = specsOfAuxiliarHost.get(host.getId())[1];
      long aux_mips = specsOfAuxiliarHost.get(host.getId())[2];
      if (host.getStorage() < vm.getSize() + aux_storage &&
          host.getRamProvisioner().getAvailableRam() < vm.getRam() + aux_ram &&
          host.getAvailableMips() < vm.getCurrentRequestedTotalMips() + aux_mips)
        return false;
    }
    return super.allocateHostForVm(vm, host);
  }



  private boolean allocateAUXILIARSpaceInHost(Vm vm){
    for(Host h : getHostList()) {
      if(specsOfAuxiliarHost.containsKey(h.getId()))
        continue;
      if(h.getStorage() > vm.getSize() && h.getRam() > vm.getRam()) {
        specsOfAuxiliarHost.put(h.getId(), new Long[3]);
        specsOfAuxiliarHost.get(h.getId())[0] = vm.getSize();
        specsOfAuxiliarHost.get(h.getId())[1] = Long.valueOf(vm.getCurrentRequestedRam());
        specsOfAuxiliarHost.get(h.getId())[2] = Long.valueOf((long)vm.getCurrentRequestedMaxMips());
        auxiliarHosterForFaults.put(vm.getUid(), h);
        return true;
      }
    }
    return false;
  }



  /**
   * It will allocate the VM in a host which resources are not full as a common node or as an auxiliary host.
   * Once an VMs that has been assured as fault tolerance is stored in a host, another suitable host is looked to be
   * ready in case of failure. In case of failure we remove the unused variable and update the maps attributes.
   *
   * @param vm the vm to be allocated
   * @return true if the allocation was successful or false otherwise
   */
  @Override
  public boolean allocateHostForVm(Vm vm) {
    if (hoster.containsKey(vm.getUid())) {
      hoster.remove(vm.getUid());

      if (vm.getId() % 10 == 1 && vm.getId() % 10 == 0) {
        Host aux_h = auxiliarHosterForFaults.get(vm.getUid());

        if (!super.allocateHostForVm(vm, aux_h)) {
          System.out.println("Error tipo 4 \n");
          return false;
        }

        auxiliarHosterForFaults.remove(vm.getUid());
        specsOfAuxiliarHost.remove(aux_h.getId());

        if (!allocateAUXILIARSpaceInHost(vm)) {
          System.out.println("Error tipo 3 \n");
          return false;
        }
        return true;
      }
      allocateHostForVm(vm);

    } else {
      for (Host h : getHostList()) {
        if (!allocateHostForVm(vm, h))
          continue;
        if (vm.getId() % 10 == 0 || vm.getId() % 10 == 1)
          if (!allocateAUXILIARSpaceInHost(vm)) {
            System.out.println("Error tipo 2 \n");
            return false;
          }
        return true;
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
      if(auxiliarHosterForFaults.containsKey(vm.getUid())) {
        Host aux_h = auxiliarHosterForFaults.remove(vm.getUid());
        specsOfAuxiliarHost.remove(aux_h.getId());
      }
      super.deallocateHostForVm(vm);
    }
  }

}