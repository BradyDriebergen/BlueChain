package communication;

import node.Node;
import utils.Address;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import communication.*;
import communication.messaging.Message;
import communication.messaging.Messager;
import communication.messaging.Message.Request;

/**
 * Attempts to establish bidirectional connection to specified amount of peers
 */
public class ClientConnection extends Thread {
    private final Node node;
    private final ArrayList<Address> globalPeers;

    public ClientConnection(Node node, ArrayList<Address> globalPeers) throws SocketException {
        this.node = node;
        this.globalPeers = globalPeers;
        setPriority(NORM_PRIORITY - 1);
    }

    public void run() {
        if (node.getLocalPeers().size() < node.getMaxPeers()) {
            for (Address address : globalPeers) {
                if (node.getLocalPeers().size() >= node.getMaxPeers()){
                    break;
                }
                if (node.eligibleConnection(address, false)) {                        
                    Message messageReceived = Messager.sendTwoWayMessage(address, new Message(Request.REQUEST_CONNECTION, node.getAddress()), node.getAddress());
                    if (messageReceived.getRequest().equals(Message.Request.ACCEPT_CONNECTION)) {
                        node.establishConnection(address);
                        if (node.getLocalPeers().size() == node.getMinConnections()) {
                            return;
                        }
                    }
                }
            }
        }
    }

    
}
