package blockchain.usecases.healthcare;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class Patient {

    private String UID;

    /* Static Fields */
    private String firstName;
    private String lastName;
    private Date dob;

    /* Non-static *fields in the form of a map, with the key being something
     * like weight. We want a map because there may be many fields / records
     * a doctor may want to update the patients file with that we can't
     * predict
    */
    HashMap<String, String> fields;

    public Patient(String fName, String lName, Date dob){
        this.firstName = fName;
        this.lastName = lName;
        this.dob = dob;
        this.fields = new HashMap<String, String>();
        this.UID = UUID.randomUUID().toString().replace("-", "");
    }

    public String getUID(){
        return UID;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Date getDob() {
        return dob;
    }

    public void addField(String key, String value){
        fields.put(key, value);
    }

    public String getField(String key){
        return fields.get(key);
    }

    @Override
    public String toString() {
        return "Name: " + this.firstName + " " + this.lastName + " | DOB: " + this.dob.toString();
        // Also return the fields entered by doctors
    }
}