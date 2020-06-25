package com.template.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.r3.corda.lib.accounts.contracts.states.AccountInfo;
import com.r3.corda.lib.accounts.workflows.UtilitiesKt;
import com.r3.corda.lib.tokens.contracts.states.NonFungibleToken;
import com.template.states.Coupon;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

// ******************
// * Initiator flow *
// ******************
@InitiatingFlow
@StartableByRPC
public class ViewCoupons extends FlowLogic<String> {

    private final String whoAmI;

    public ViewCoupons(String whoAmI) {
        this.whoAmI = whoAmI;
    }

    @Suspendable
    @Override
    public String call() throws FlowException {
        // Initiator flow logic goes here.
        AccountInfo myAccount = UtilitiesKt.getAccountService(this).accountInfo(whoAmI).get(0).getState().getData();
        UUID id = myAccount.getIdentifier().getId();
        QueryCriteria.VaultQueryCriteria criteria = new QueryCriteria.VaultQueryCriteria().withExternalIds(Arrays.asList(id));

        //Coupon
        List<StateAndRef<NonFungibleToken>> couponList = getServiceHub().getVaultService().queryBy(NonFungibleToken.class,criteria).getStates();
        List<String> myCouponIDs = couponList.stream().map(it -> it.getState().getData().getTokenType().getTokenIdentifier()).collect(Collectors.toList());
        List<String> cpList = myCouponIDs.stream().map(it -> {
            UUID uuid = UUID.fromString(it);
            QueryCriteria.LinearStateQueryCriteria queryCriteria =
                    new QueryCriteria.LinearStateQueryCriteria(null, Arrays.asList(uuid),null, Vault.StateStatus.UNCONSUMED);
            StateAndRef<Coupon> stateAndRef = getServiceHub().getVaultService().queryBy(Coupon.class,queryCriteria).getStates().get(0);
            String cpnName = stateAndRef.getState().getData().getCouponName();
            return cpnName;
        }).collect(Collectors.toList());

        String coupons = "\n I have coupons(s) for: ";
        if(couponList.size() == 0) {
            coupons = "I donot own any coupon";
        } else {
            for(String item : cpList) {
                coupons = coupons + item;
            }
        }

        return coupons;
    }
}
