package blockchain.usecases.healthcare.Events;


import blockchain.usecases.healthcare.Event;
import blockchain.usecases.healthcare.Patient;

public class CreatePatient extends Event {

    Patient patient;

    public CreatePatient(String patientUID, Action action, Patient patient) {
        super(patientUID, action);
        this.patient = patient;
    }

    public Patient getPatient() {
        return patient;
    }
    
}
