package com.template.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.states.Coupon;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.utilities.ProgressTracker;

import java.util.List;
import java.util.stream.Collectors;

// ******************
// * Initiator flow *
// ******************
@InitiatingFlow
@StartableByRPC
public class ListIssuedCoupons extends FlowLogic<String> {

    @Suspendable
    @Override
    public String call() throws FlowException {
        // Initiator flow logic goes here.
        QueryCriteria criteria = new QueryCriteria.VaultQueryCriteria(Vault.StateStatus.UNCONSUMED);
        List<StateAndRef<Coupon>> issuedCoupons = getServiceHub().getVaultService().queryBy(Coupon.class,criteria).getStates();
        List<String> coupons = issuedCoupons.stream().map(it -> it.getState().getData().getCouponName()).collect(Collectors.toList());

        String allCoupons = "Issued coupons are: ";
        if(coupons.size() == 0) {
            allCoupons = "No Coupon is issued";
        } else {
            for(String item : coupons) {
                allCoupons = allCoupons + item;
            }
        }

        return allCoupons;
    }
}
