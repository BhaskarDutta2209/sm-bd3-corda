package com.template.states;

import com.r3.corda.lib.tokens.contracts.states.EvolvableTokenType;
import com.template.contracts.CouponContract;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.Party;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

// *********
// * State *
// *********
@BelongsToContract(CouponContract.class)
public class Coupon extends EvolvableTokenType {

    private UniqueIdentifier linearId;
    private String couponName;
    private String value;
    private String description;
    private Party issuer;

    public Coupon(UniqueIdentifier linearId, String couponName, String value, String description, Party issuer) {
        this.linearId = linearId;
        this.couponName = couponName;
        this.value = value;
        this.description = description;

        this.issuer = issuer;
    }

//    @Override
//    public List<AbstractParty> getParticipants() {
//        return Arrays.asList();
//    }

    @Override
    public int getFractionDigits() {
        return 0;
    }

    @NotNull
    @Override
    public List<Party> getMaintainers() {
        return Arrays.asList(this.getIssuer());
    }

    @NotNull
    @Override
    public UniqueIdentifier getLinearId() {
        return linearId;
    }

    public String getCouponName() {
        return couponName;
    }

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public Party getIssuer() {
        return issuer;
    }
}