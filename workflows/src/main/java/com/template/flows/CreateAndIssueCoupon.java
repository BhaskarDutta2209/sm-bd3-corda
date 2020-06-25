package com.template.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.r3.corda.lib.accounts.contracts.states.AccountInfo;
import com.r3.corda.lib.accounts.workflows.UtilitiesKt;
import com.r3.corda.lib.accounts.workflows.flows.RequestKeyForAccount;
import com.r3.corda.lib.tokens.contracts.states.NonFungibleToken;
import com.r3.corda.lib.tokens.contracts.types.IssuedTokenType;
import com.r3.corda.lib.tokens.contracts.types.TokenPointer;
import com.r3.corda.lib.tokens.contracts.utilities.TransactionUtilitiesKt;
import com.r3.corda.lib.tokens.workflows.flows.rpc.CreateEvolvableTokens;
import com.r3.corda.lib.tokens.workflows.flows.rpc.IssueTokens;
import com.template.states.Coupon;
import net.corda.core.contracts.TransactionState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.identity.AnonymousParty;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;

import java.util.Arrays;
import java.util.UUID;

// ******************
// * Initiator flow *
// ******************
@InitiatingFlow
@StartableByRPC
public class CreateAndIssueCoupon extends FlowLogic<String> {

    private final String couponName;
    private final String value;
    private final String description;
    private final String receiver;

    public CreateAndIssueCoupon(String couponName, String value, String description, String receiver) {
        this.couponName = couponName;
        this.value = value;
        this.description = description;
        this.receiver = receiver;
    }

    @Suspendable
    @Override
    public String call() throws FlowException {
        // Initiator flow logic goes here.

        Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);

        UniqueIdentifier id = new UniqueIdentifier();

        Coupon myCoupon = new Coupon(id,couponName,value,description,getOurIdentity());

        TransactionState transactionState = new TransactionState(myCoupon, notary);

        SignedTransaction stx = subFlow(new CreateEvolvableTokens(transactionState));

        AccountInfo receiverAccountInfo = UtilitiesKt.getAccountService(this).accountInfo(receiver).get(0).getState().getData();

        AnonymousParty receiverAccount = subFlow(new RequestKeyForAccount(receiverAccountInfo));

        TokenPointer couponPointer = myCoupon.toPointer(myCoupon.getClass());

        IssuedTokenType issuedTokenType = new IssuedTokenType(getOurIdentity(),couponPointer);

        NonFungibleToken nonFungibleToken = new NonFungibleToken(issuedTokenType, receiverAccount, new UniqueIdentifier(),
                TransactionUtilitiesKt.getAttachmentIdForGenericParam(couponPointer));
        SignedTransaction sgnTx = subFlow(new IssueTokens(Arrays.asList(nonFungibleToken)));

        return "Issued coupon of "+couponName+" to "+receiver+" txn Id "+sgnTx.getId().toString();
    }
}
