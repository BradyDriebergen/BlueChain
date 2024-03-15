package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.HashSet;

import blockchain.usecases.healthcare.HCTransaction;
import blockchain.usecases.healthcare.Patient;
import blockchain.usecases.healthcare.Event.Action;
import blockchain.usecases.healthcare.Events.*;
import communication.messaging.Message;
import utils.Address;

public class HCClient {
    
    private Object updateLock;
    private BufferedReader reader;
    private Address myAddress;
    private ArrayList<Address> fullNodes;
    protected boolean test;

    HashSet<HCTransaction> seenTransactions;
    // ArrayList<Event> events;

    private SimpleDateFormat formatter;

    public HCClient(Object updateLock, BufferedReader reader, Address myAddress, ArrayList<Address> fullNodes){
        this.reader = reader;
        this.updateLock = updateLock;
        this.myAddress = myAddress;
        this.fullNodes = fullNodes;

        this.seenTransactions = new HashSet<>();
        // events = new ArrayList<>();

        formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm a");
    }

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

        Appointment appointment = new Appointment(patientUID, date, location);
        appointment.setProvider(provider);
        HCTransaction newTransaction = new HCTransaction(appointment);
        byte[] signedUID = patientUID.getBytes();
        newTransaction.setSigUID(signedUID);

        submitToNodes(newTransaction);
    }

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

        Prescription prescription = new Prescription(patientUID, medication, provider, address, date, count);
        HCTransaction newTransaction = new HCTransaction(prescription);
        byte[] signedUID = patientUID.getBytes();
        newTransaction.setSigUID(signedUID);

        submitToNodes(newTransaction);
    }

    public void updateRecord() throws IOException {
        System.out.println("Updating a patient's record");
        System.out.println("Enter the patient's UID:");
        String patientUID = reader.readLine();
        System.out.println("Enter the record to update:");
        String key = reader.readLine();
        System.out.println("Enter the new value of the record:");
        String value = reader.readLine();

        // Date is the current date that the record is updated
        RecordUpdate recordUpdate = new RecordUpdate(patientUID, new Date(), key, value);
        HCTransaction newTransaction = new HCTransaction(recordUpdate);
        byte[] signedUID = patientUID.getBytes();
        newTransaction.setSigUID(signedUID);

        submitToNodes(newTransaction);
    }

    public void createNewPatient() throws IOException {
        System.out.println("Creating a new patient");
        System.out.println("Enter the patient's first name:");
        String fname = reader.readLine();
        System.out.println("Enter the patient's last name:");
        String lname = reader.readLine();
        System.out.println("Enter the patient's date of birth:");
        Date dob = new Date(Long.valueOf(reader.readLine()));

        Patient patient = new Patient(fname, lname, dob);
        CreatePatient createPatient = new CreatePatient(patient.getUID(), Action.Create_Patient, patient);
        HCTransaction newTransaction = new HCTransaction(createPatient);
        byte[] signedUID = patient.getUID().getBytes();
        newTransaction.setSigUID(signedUID);

        submitToNodes(newTransaction);
    }

    public void submitToNodes(HCTransaction transaction){
        System.out.println("Submitting transaction to nodes: ");
        for(Address address : fullNodes){
            submitTransaction(transaction, address);
        }
    }

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

    void testNetwork(int j){
        // Test for HCClient
    }

    protected void printUsage(){
        System.out.println("BlueChain Health Care Usage:");
        System.out.println("a: Create a new appointment");
        System.out.println("p: Create a new perscription");
        System.out.println("r: Update a patient's record");
        // System.out.println("c: create a new patient");
        System.out.println("u: Update full nodes");
    }
}
