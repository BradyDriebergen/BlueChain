package blockchain.usecases.healthcare.Events;

import java.util.Date;

import blockchain.usecases.healthcare.Event;

public class Appointment extends Event {

    private Date date;
    private String location;
    private String provider;
    
    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public Date getDate() {
        return date;
    }

    public String getLocation() {
        return location;
    }

    public Appointment(String patientUID, Date date, String location) {
        super(patientUID, Action.Appointment);
        this.date = date;
        this.location = location;
    }
}