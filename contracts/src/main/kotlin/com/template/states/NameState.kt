package com.template.states

import com.template.contracts.NameContract
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.ContractState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party

@BelongsToContract(NameContract::class)
data class NameState(val name: String,
                     val lastname : String,
                     val party: Party,
                     val linearId: UniqueIdentifier) : ContractState {

    override val participants: List<AbstractParty> = listOf(party);
    //To change initializer of created properties use File | Settings | File Templates.

}