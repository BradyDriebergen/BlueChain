/**
 * This class represents the client for the health care use case. It is responsible for prompting the user for
 * intput and then creating the appropriate event and submitting it to the blockchain via transaction. Each event's
 * parameters needs to be filled in or it won't validate and refuse to be added to the blockchain. After the user
 * creates an event, the client will represent the event back to the user verifying that it was added.
 * 
 * @date 03-20-2024
 */

package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.HashSet;

import blockchain.usecases.healthcare.Event;
import blockchain.usecases.healthcare.HCTransaction;
import blockchain.usecases.healthcare.Patient;
import blockchain.usecases.healthcare.Events.*;
import communication.messaging.Message;
import communication.messaging.Messager;
import me.tongfei.progressbar.ProgressBar;
import utils.Address;
import utils.merkletree.MerkleTreeProof;

public class HCClient {
    
    private Object updateLock;
    private BufferedReader reader;
    private Address myAddress;
    private ArrayList<Address> fullNodes;
    protected boolean test;

    HashSet<HCTransaction> seenTransactions;
    // List that contains all the events that have been created.
    ArrayList<Event> events;
    ArrayList<Patient> patients;


    private SimpleDateFormat formatter;


    /**
     * Constructs a HCClient instance. Initializes the client with the given parameters.
     * @param updateLock The lock for multithreading.
     * @param reader The reader for user input.
     * @param myAddress The address of the client.
     * @param fullNodes The address list of full nodes to use.
     */
    public HCClient(Object updateLock, BufferedReader reader, Address myAddress, ArrayList<Address> fullNodes){
        this.reader = reader;
        this.updateLock = updateLock;
        this.myAddress = myAddress;
        this.fullNodes = fullNodes;

        this.seenTransactions = new HashSet<>();
        this.events = new ArrayList<Event>();
        this.patients = new ArrayList<Patient>();

        formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm a");
    }

    /**
     * Creates a new appointment. Prompts the user for the patient's UID, the appointment's date, 
     * location, and provider. It then creates a transaction containing the appointment event and
     * submits it to the full nodes.
     * @throws IOException Thrown if there is an error reading user input.
     * @throws ParseException Thrown if there is an error parsing the date.
     */
    public void createAppointment() throws IOException, ParseException {
        formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm a");
        
        System.out.println("Creating a new appointment");
        System.out.println("Enter the patient's UID:");
        String patientUID = reader.readLine();
        System.out.println("Enter the appointment's date (dd-MM-yyyy HH:mm am/pm):");
        String strDate = reader.readLine();
        Date date = formatter.parse(strDate);
        System.out.println("Enter the appointment's location:");
        String location = reader.readLine();
        System.out.println("Enter the appointment's provider:");
        String provider = reader.readLine();

        System.out.println("\n--APPOINTMENT CREATED--");
        System.out.println("Appointment info:");
        System.out.println("Patient UID: " + patientUID);
        System.out.println("Appointment date: " + date);
        System.out.println("Location: " + location);
        System.out.println("Provider: " + provider);

        Appointment appointment = new Appointment(date, location, provider);
        events.add(appointment);
        HCTransaction newTransaction = new HCTransaction(appointment, patientUID);
        byte[] signedUID = patientUID.getBytes();
        newTransaction.setSigUID(signedUID);

        submitToNodes(newTransaction);
    }

    /**
     * Creates a new perscription. Prompts the user for the patient's UID, the perscription's date,
     * medication, perscribed count, provider, and address. It then creates a transaction containing
     * the perscription event and submits it to the full nodes.
     * @throws IOException Thrown if there is an error reading user input.
     * @throws ParseException Thrown if there is an error parsing the date.
     */
    public void createPerscription() throws IOException, ParseException{
        formatter = new SimpleDateFormat("dd-MM-yyyy");

        System.out.println("Creating a new perscription");
        System.out.println("Enter the patient's UID:");
        String patientUID = reader.readLine();
        System.out.println("Enter the perscription date (dd-MM-yyyy):");
        String strDate = reader.readLine();
        Date date = formatter.parse(strDate);
        System.out.println("Enter the perscription's medication:");
        String medication = reader.readLine();
        System.out.println("Enter the perscription's perscribed count:");
        int count = Integer.valueOf(reader.readLine());
        System.out.println("Enter the perscription's provider:");
        String provider = reader.readLine();
        System.out.println("Enter address of the issued perscription:");
        String address = reader.readLine();

        System.out.println("\n--PERSCRIPTION CREATED--");
        System.out.println("Perscription info:");
        System.out.println("Patient UID: " + patientUID);
        System.out.println("Perscription date: " + date);
        System.out.println("Medication: " + medication);
        System.out.println("Perscribed count: " + count);
        System.out.println("Provider: " + provider);
        System.out.println("Address: " + address);

        Prescription prescription = new Prescription(medication, provider, address, date, count);
        events.add(prescription);
        HCTransaction newTransaction = new HCTransaction(prescription, patientUID);
        byte[] signedUID = patientUID.getBytes();
        newTransaction.setSigUID(signedUID);

        submitToNodes(newTransaction);

        
    }

    /**
     * Updates a patient's record. Prompts the user for the patient's UID, the record to update, and
     * the new value of the record. It then creates a transaction containing the record update event
     * and submits it to the full nodes.
     * @throws IOException Thrown if there is an error reading user input.
     */
    public void updateRecord() throws IOException {
        System.out.println("Updating a patient's record");
        System.out.println("Enter the patient's UID:");
        String patientUID = reader.readLine();
        System.out.println("Enter the record to update:");
        String key = reader.readLine();
        System.out.println("Enter the new value of the record:");
        String value = reader.readLine();

        System.out.println("\n--RECORD UPDATED--");
        System.out.println("Record info:");
        System.out.println("Patient UID: " + patientUID);
        System.out.println("Record to Update: " + key);
        System.out.println("New value: " + value);

        // Date is the current date that the record is updated
        RecordUpdate recordUpdate = new RecordUpdate(new Date(), key, value);
        events.add(recordUpdate);
        HCTransaction newTransaction = new HCTransaction(recordUpdate, patientUID);
        byte[] signedUID = patientUID.getBytes();
        newTransaction.setSigUID(signedUID);

        submitToNodes(newTransaction);
    }

    // Might not be necessary, requires consultation.
    public void createNewPatient() throws IOException {
        System.out.println("Creating a new patient");
        System.out.println("Enter the patient's first name:");
        String fname = reader.readLine();
        System.out.println("Enter the patient's last name:");
        String lname = reader.readLine();
        System.out.println("Enter the patient's date of birth:");
        Date dob = new Date(Long.valueOf(reader.readLine()));

        Patient patient = new Patient(fname, lname, dob);

        patients.add(patient);

        System.out.println("Patient successfully created. Patient UID: " + patient.getUID());
    }

    public void showPatientDetails() throws IOException {
        System.out.println("Enter the patient's UID:");
        String patientUID = reader.readLine();

        //updatePatientDetails();

        for(Patient patient : patients){
            if(patient.getUID().equals(patientUID)){
                HashMap<String, String> fields = patient.getFields();
                ArrayList<Event> patientEvents = patient.getEvents();

                System.out.println("Patient details:");
                System.out.println("First name: " + patient.getFirstName());
                System.out.println("Last name: " + patient.getLastName());
                System.out.println("Date of birth: " + patient.getDob());

                for(String key : fields.keySet()){
                    System.out.println(key + ": " + fields.get(key));
                }

                System.out.println("\nEVENTS:");

                for (Event event : events) {
                    //Print out all events.
                }

                return;
            }
        }

        System.out.println("Patient not found.");
    }

    public void updatePatientDetails(MerkleTreeProof mtp) throws IOException {
        synchronized(updateLock){

            HCTransaction transaction = (HCTransaction) mtp.getTransaction();

            for(HCTransaction existingTransaction : seenTransactions){
                if(existingTransaction.equals(transaction)){
                    return;
                }
            }
            
            seenTransactions.add(transaction);

            if(!mtp.confirmMembership()){
                System.out.println("Could not validate tx in MerkleTreeProof" );
                return;
            }
    
            for(Patient patient : patients){ 
                if (patient.getUID().equals(transaction.getPatientUID()))
                    if (transaction.getEvent() instanceof RecordUpdate){
                        RecordUpdate recordUpdate = (RecordUpdate) transaction.getEvent();
                        patient.addField(recordUpdate.getKey(), recordUpdate.getValue());
                    } else if (transaction.getEvent() instanceof Prescription){
                        Prescription prescription = (Prescription) transaction.getEvent();
                        patient.addEvent(prescription);
                    } else if (transaction.getEvent() instanceof Appointment){
                        Appointment appointment = (Appointment) transaction.getEvent();
                        patient.addEvent(appointment);
                    }
                }
            }

            if(!this.test) System.out.println("\nFull node has update. Updating patients..." );
            //if(!this.test) printAccounts();
    }

    /**
     * Updates the list of full nodes we are communicating with in the network.
     * @throws IOException If an I/O error occurs.
     */
    public void submitToNodes(HCTransaction transaction){
        System.out.println("Submitting transaction to nodes: ");
        for(Address address : fullNodes){
            submitTransaction(transaction, address);
        }
    }

    /**
     * Submits a transaction to the given address.
     * @param transaction The transaction to submit.
     * @param address The address to submit the transaction to.
     */
    public void submitTransaction(HCTransaction transaction, Address address){
        try {
            Socket s = new Socket(address.getHost(), address.getPort());
            OutputStream out = s.getOutputStream();
            ObjectOutputStream oout = new ObjectOutputStream(out);
            Message message = new Message(Message.Request.ADD_TRANSACTION, transaction);
            oout.writeObject(message);
            oout.flush();
            Thread.sleep(1000);
            s.close();
            if(!this.test) System.out.println("Full node: " + address);
        } catch (IOException e) {
            System.out.println("Full node at " + address + " appears down.");
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * TEST METHOD. Adds an appointment to the events list.
     * @param provider The name of the appointment provider
     */
    public void testAddAppointment(String provider) {
        synchronized(updateLock) {
            
            Appointment newAppointment = new Appointment(new Date(), "Generic Ave.", provider);
            
            for(Event event : events){
                if(event.equals(newAppointment)){
                    System.out.println("Appointment already exists.");
                    return;
                }
            }

            Messager.sendOneWayMessage(new Address(fullNodes.get(0).getPort(), fullNodes.get(0).getHost()),
            new Message(Message.Request.ALERT_HC_WALLET), myAddress);
        }
    }

    /**
     * TEST METHOD. Adds a perscription to the events list.
     * @param provider The name of the perscription provider
     */
    public void testAddPerscription(String provider) {
        synchronized(updateLock) {
            
            Prescription newPerscription = new Prescription("Generic medication", provider, "Generic Ave", new Date(), 1);
            
            for(Event event : events){
                if(event.equals(newPerscription)){
                    System.out.println("Perscription already exists.");
                    return;
                }
            }

            Messager.sendOneWayMessage(new Address(fullNodes.get(0).getPort(), fullNodes.get(0).getHost()),
            new Message(Message.Request.ALERT_HC_WALLET), myAddress);
        }
    }

    /**
     * TEST METHOD. Adds a record update to the events list.
     * @param key The key of the record to update
     */
    public void testUpdateRecord(String key) {
        synchronized(updateLock) {
            
            RecordUpdate newRecord = new RecordUpdate(new Date(), key, key);
            
            for(Event event : events){
                if(event.equals(newRecord)){
                    System.out.println("Record already exists.");
                    return;
                }
            }
            

            Messager.sendOneWayMessage(new Address(fullNodes.get(0).getPort(), fullNodes.get(0).getHost()),
            new Message(Message.Request.ALERT_HC_WALLET), myAddress);
        }
    }

    protected void testSubmitTransaction(Event event, String patientUID) {

        HCTransaction newTransaction = new HCTransaction(event, patientUID);
        newTransaction.setSigUID(patientUID.getBytes());

        for(Address address : fullNodes){
            submitTransaction(newTransaction, address);
        }
    }

    /**
     * TEST METHOD. Tests the network by adding a number of appointments, perscriptions, and record updates
     * to the events list and then checking if they were added to the blockchain.
     * @param j The number of events to add to the list.
     */
    void testNetwork(int j){
        System.out.println("Beginning Test");
        try {     
            Patient patient = new Patient("John", "Doe", new Date());
            
            ProgressBar pb = new ProgressBar("Test", j);
            pb.start(); // the progress bar starts timing
            pb.setExtraMessage("Testing..."); // Set extra message to display at the end of the bar
            
            for(int i = 0; i < j; i++){
                    testSubmitTransaction(new Appointment(new Date(), "TEST", "Provider " + i), patient.getUID());
                    Thread.sleep(500);
                    pb.step();
            }

            pb.stop(); // stops the progress bar
            System.out.println("Sleeping wallet for last minute updates...");
            Thread.sleep(100000);

            // Make sure that the ledger matches the added events.

            if(patient.getEvents().size() == j) {
                System.out.println("\n*********************Test passed.*********************");
            }else{
                System.out.println("\n*********************Test Failed*********************");
            }

            System.out.println(patient.getEvents().size() + " events added.");

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Prints the user menu to the client.
     */
    protected void printUsage(){
        System.out.println("BlueChain Health Care Usage:");
        System.out.println("a: Create a new appointment");
        System.out.println("p: Create a new perscription");
        System.out.println("r: Update a patient's record");
        System.out.println("c: create a new patient");
        System.out.println("s: Show patient details");
        System.out.println("u: Update full nodes");
    }
}
