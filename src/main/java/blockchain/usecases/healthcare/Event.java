package blockchain.usecases.healthcare;

import java.io.Serializable;

public abstract class Event implements Serializable{

    public enum Action {
        Appointment,
        Prescription,
        Record_Update,
        Create_Patient
    }

    private String patientUID;
    private Action action;

    public String getPatientUID() {
        return patientUID;
    }

    public Action getAction() {
        return action;
    }

    public Event(String patientUID, Action action){
        this.patientUID = patientUID;
        this.action = action;
    }
}
