package blockchain.usecases.healthcare;

import blockchain.Transaction;
import utils.Hashing;

public class HCTransaction extends Transaction {

    private Event event;


    public HCTransaction(Event event){
        this.event = event;
        // Create unique identifier for the transaction assigned to UID
        UID = Hashing.getSHAString(event.getPatientUID() + event.getAction().name() + System.currentTimeMillis());
    }

    public Event getEvent() {
        return event;
    }

    @Override
    public String toString() {
        // Set the string representation of the transaction
        return event.getAction().name() + " for " + event.getPatientUID();
    }    
}