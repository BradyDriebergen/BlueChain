package blockchain.usecases.healthcare.Events;

import java.util.Date;

import blockchain.usecases.healthcare.Event;

public class Prescription extends Event {

    // Ask whether or not the Date is current or future.

    private Date date;
    private int perscribedCount;
    private String medication;
    private String provider;
    private String address;
    
    public int getPerscribedCount() {
        return perscribedCount;
    }

    public String getProvider() {
        return provider;
    }

    // Put this in the constructor.
    public void setProvider(String provider) {
        this.provider = provider;
    }

    public Date getDate() {
        return date;
    }

    public String getMedication() {
        return medication;
    }

    public String getAddress() {
        return address;
    }

    public Prescription(String patientUID, String medication, String provider, String address, Date date, int perscribedCount) {
        super(patientUID, Action.Prescription);
        this.date = date;
        this.perscribedCount = perscribedCount;
        this.provider = provider;
        this.address = address;
        this.medication = medication;
    }
    
}
