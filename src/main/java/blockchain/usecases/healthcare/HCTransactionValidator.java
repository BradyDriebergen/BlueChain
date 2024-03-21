/**
 * This class is used to validate the transaction before it is added to the blockchain. It does so
 * by checking the event of the transaction and making sure all the data is not null. If the data is
 * null, then the transaction is not valid and will not be added to the blockchain.
 * 
 * @date 03-20-2021
 */

package blockchain.usecases.healthcare;

import blockchain.TransactionValidator;
import blockchain.usecases.healthcare.Events.*;

public class HCTransactionValidator extends TransactionValidator {


    /**
     * This method validates the transaction before it is added to the blockchain. It does so by
     * checking the event of the transaction and making sure all the data is not null. If the data is
     * null, then the transaction is not valid and will not be added to the blockchain.
     * @param objects The objects to validate.
     * @return True if the transaction is valid, false otherwise.
     */
    @Override
    public boolean validate(Object[] objects) {
        HCTransaction transaction = (HCTransaction) objects[0];

        if(transaction.getEvent().getAction().name().equals("Appointment")){ // If the event is an appointment
            Appointment appointment = (Appointment) transaction.getEvent();

            // Checks to see if any data is null
            if (appointment.getProvider() == null) { return false; }
            if (appointment.getLocation() == null) { return false; }
            if (appointment.getDate() == null) { return false; }

        } else if (transaction.getEvent().getAction().name().equals("Prescription")){ // If the event is a prescription
            Prescription prescription = (Prescription) transaction.getEvent();

            // Checks to see if any data is null
            if (prescription.getDate() == null) { return false; }
            if (prescription.getMedication() == null) { return false; }
            if (prescription.getProvider() == null) { return false; }
            if (prescription.getAddress() == null) { return false; }
            if (prescription.getPerscribedCount() == 0) { return false; }


        } else if (transaction.getEvent().getAction().name().equals("Record_Update")) { // If the event is a record update
            RecordUpdate recordUpdate = (RecordUpdate) transaction.getEvent();

            // Checks to see if any data is null
            if (recordUpdate.getDate() == null) { return false; }
            if (recordUpdate.getKey() == null) { return false; }
            if (recordUpdate.getValue() == null) { return false; }

        } else if (transaction.getEvent().getAction().name().equals("Create_Patient")) {
            CreatePatient createPatient = (CreatePatient) transaction.getEvent();

            // Checks to see if any data is null
            if (createPatient.getPatient().getUID() == null) { return false; }
            if (createPatient.getPatient().getFirstName() == null) { return false; }
            if (createPatient.getPatient().getLastName() == null) { return false; }
            if (createPatient.getPatient().getDob() == null) { return false; }
        }

        //HCTransaction transaction = new HCTransaction(event);        
        return true;
    }

    // public boolean isFullString() {

    // }
    
}
