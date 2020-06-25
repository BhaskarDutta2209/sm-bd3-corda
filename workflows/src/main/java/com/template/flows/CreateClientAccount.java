package com.template.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.r3.corda.lib.accounts.contracts.states.AccountInfo;
import com.r3.corda.lib.accounts.workflows.flows.CreateAccount;
import com.r3.corda.lib.accounts.workflows.flows.ShareAccountInfo;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.identity.Party;

import java.util.Collections;

// ******************
// * Initiator flow *
// ******************
@InitiatingFlow
@StartableByRPC
public class CreateClientAccount extends FlowLogic<String> {

    private final String accountName;
    private final Party companyName;

    public CreateClientAccount(String accountName, Party companyName) {
        this.accountName = accountName;
        this.companyName = companyName;
    }

    @Suspendable
    @Override
    public String call() throws FlowException {
        // Initiator flow logic goes here.
        try {
            StateAndRef<AccountInfo> accountInfoStateAndRef = (StateAndRef<AccountInfo>) subFlow(new CreateAccount(accountName));
            subFlow(new ShareAccountInfo(accountInfoStateAndRef, Collections.singletonList(companyName)));
            return "Success";
        } catch (Exception exp) {
            return "Creation Fail";
        }
    }
}
