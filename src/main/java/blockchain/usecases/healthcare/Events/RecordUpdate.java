package blockchain.usecases.healthcare.Events;

import java.util.Date;

import blockchain.usecases.healthcare.Event;

public class RecordUpdate extends Event{

    private Date date;
    private String key;
    private String value;

    public RecordUpdate(String patientUID, Date date, String key, String value) {
        super(patientUID, Action.Record_Update);
        this.date = date;
        this.key = key;
        this.value = value;
    }
    
    public Date getDate() {
        return date;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
