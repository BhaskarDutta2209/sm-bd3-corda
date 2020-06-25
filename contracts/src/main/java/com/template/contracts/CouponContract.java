package com.template.contracts;

import com.r3.corda.lib.tokens.contracts.EvolvableTokenContract;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

// ************
// * Contract *
// ************
public class CouponContract extends EvolvableTokenContract implements Contract {
    // This is used to identify our contract when building a transaction.
    public static final String ID = "com.template.contracts.CouponContract";

    // A transaction is valid if the verify() function of the contract of all the transaction's input and output states
    // does not throw an exception.
    @Override
    public void verify(LedgerTransaction tx) {}

    @Override
    public void additionalCreateChecks(@NotNull LedgerTransaction tx) {

    }

    @Override
    public void additionalUpdateChecks(@NotNull LedgerTransaction tx) {

    }

//    // Used to indicate the transaction's intent.
//    public interface Commands extends CommandData {
//        class Action implements Commands {}
//    }
}