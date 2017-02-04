package fr.unice.vicc.AllocationPolicies;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: ignacio
 * Date: 04/02/2017.
 */
public abstract class AbstractAllocationPolicy extends VmAllocationPolicy {

    /** The map to track the server that host each running VM.
     * We have decided to store the UID of the VM instead of the VM itself since it
     * */
    protected Map<String,Host> hoster;

    public AbstractAllocationPolicy(List<? extends Host> list) {
        super(list);
        this.hoster = new HashMap<>();
    }

    @Override
    protected void setHostList(List<? extends Host> hostList) {
        super.setHostList(hostList);
        hoster = new HashMap<>();
    }

    //Given a Vm get its host
    @Override
    public Host getHost(Vm vm) {
        return hoster.get(vm.getUid());
    }

    //Given a VmId and userId get its host
    @Override
    public Host getHost(int vmId, int userId) {
        return hoster.get(Vm.getUid(userId,vmId));
    }
}
