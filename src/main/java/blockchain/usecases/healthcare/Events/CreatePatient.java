package blockchain.usecases.healthcare.Events;


import blockchain.usecases.healthcare.Event;
import blockchain.usecases.healthcare.Patient;

public class CreatePatient extends Event {

    Patient patient;

    public CreatePatient(Action action, Patient patient) {
        super(action);
        this.patient = patient;
    }

    public Patient getPatient() {
        return patient;
    }
    
}
