<p align="center">
  <img src="https://www.corda.net/wp-content/uploads/2016/11/fg005_corda_b.png" alt="Corda" width="500">
</p>

# Corda Service 4.4 

This project will help you to create State when nodes wake up. 

### NameFlow.kt 
    
This is simple flow that create state. 

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
        
 ### NameFlow.kt
 
 This is corda Service that will be called when nodes wake up.
 
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
    
    
### Run the ./runnodes 
    
    And check the 
    run vaultQuery contractStateType: com.template.states.NameState


State is initialise there. 

       