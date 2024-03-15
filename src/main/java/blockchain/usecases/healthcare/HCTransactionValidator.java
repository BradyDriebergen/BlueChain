package blockchain.usecases.healthcare;

import blockchain.TransactionValidator;
import blockchain.usecases.healthcare.Events.*;

public class HCTransactionValidator extends TransactionValidator {

    @Override
    public boolean validate(Object[] objects) {
        HCTransaction transaction = (HCTransaction) objects[0];

        if(transaction.getEvent().getAction().name().equals("Appointment")){
            Appointment appointment = (Appointment) transaction.getEvent();
            // if(appointment.getTime())

            // Checks to see if any data is null
            if (appointment.getPatientUID() == null) { return false; }
            if (appointment.getProvider() == null) { return false; }
            if (appointment.getLocation() == null) { return false; }
            if (appointment.getDate() == null) { return false; }


            // Make sure they don't get pass the client and fill something in that cannnot be allowed.

        } else if (transaction.getEvent().getAction().name().equals("Prescription")){
            Prescription prescription = (Prescription) transaction.getEvent();

            // Checks to see if any data is null
            if (prescription.getPatientUID() == null) { return false; }
            if (prescription.getDate() == null) { return false; }
            if (prescription.getMedication() == null) { return false; }
            if (prescription.getProvider() == null) { return false; }
            if (prescription.getAddress() == null) { return false; }
            if (prescription.getPerscribedCount() == 0) { return false; }


        } else if (transaction.getEvent().getAction().name().equals("Record_Update")) {
            RecordUpdate recordUpdate = (RecordUpdate) transaction.getEvent();

            // Checks to see if any data is null
            if (recordUpdate.getPatientUID() == null) { return false; }
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
