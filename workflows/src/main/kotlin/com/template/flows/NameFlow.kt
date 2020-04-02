package com.template.flows

import co.paralleluniverse.fibers.Suspendable
import com.template.contracts.NameContract
import com.template.states.NameState
import net.corda.core.contracts.Command
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.flows.*
import net.corda.core.node.AppServiceHub
import net.corda.core.node.services.CordaService
import net.corda.core.node.services.ServiceLifecycleEvent
import net.corda.core.serialization.SingletonSerializeAsToken
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder

@InitiatingFlow
@StartableByRPC
@StartableByService
class NameFlow(): FlowLogic<SignedTransaction>() {

    @Suspendable
    override fun call() : SignedTransaction {

        val notary = serviceHub.networkMapCache.notaryIdentities.first()
        val command = Command(NameContract.Commands.Create(), listOf(ourIdentity).map { it.owningKey })
        val namestate = NameState("","lastname",ourIdentity, UniqueIdentifier())

        val txBuilder = TransactionBuilder(notary)
                .addOutputState(namestate, NameContract.ID)
                .addCommand(command)
        txBuilder.verify(serviceHub)
        val tx = serviceHub.signInitialTransaction(txBuilder)

        return subFlow(FinalityFlow(tx))
    }

}

@CordaService
class MyCordaService(private val appServiceHub: AppServiceHub) : SingletonSerializeAsToken() {

    init {
        appServiceHub.register { processEvent(it) }

    }

    private fun processEvent(event: ServiceLifecycleEvent) {
        // Lifecycle event handling code including full use of serviceHub
        when (event) {
            ServiceLifecycleEvent.STATE_MACHINE_STARTED -> {
                appServiceHub.startFlow(NameFlow())
            }
            else -> {
                // Process other types of events
            }
        }
    }


}