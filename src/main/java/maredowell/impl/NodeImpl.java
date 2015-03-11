package maredowell.impl;


import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import maredowell.chord.Node;
import maredowell.util.*;

import java.net.InetSocketAddress;
import java.util.*;

/**
 * Created by AlexKotsc on 22-02-2015.
 */
public class NodeImpl implements Node {

    private NodeInfo successor = null;
    private NodeInfo predecessor = null;
    private InetSocketAddress address = null;
    private HashMap<Integer, NodeInfo> fingers;
    private int next = 0;

    private HashMap<Integer, SparkInfo> keys;

    private boolean connected = false;


    @Override
    public void create() {
        successor = getNodeInfo();

        fingers = new HashMap<Integer, NodeInfo>(31);

        //Timer t = new Timer("ServiceTask", true);

        Timer stabilizet = new Timer("StabilizeTask", true);
        Timer predecessort = new Timer("PredecessorTask", true);
        Timer fixfingerst = new Timer("FingersTask", true);
        Timer persistdatat = new Timer("PersistDataTask", true);

        stabilizet.schedule(new StabilizeTask(this), 1000, 10000);
        fixfingerst.schedule(new FingersTask(this), 10000, 1000);
        predecessort.schedule(new PredecessorTask(this), 20000, 60000);
        persistdatat.schedule(new PersistDataTask(this), 1000, 60000);

        //t.schedule(new ServiceTask(this), 1000, 30000);

        keys = new HashMap<Integer, SparkInfo>();
    }

    @Override
    public void join(NodeInfo n) {



        System.out.println("\n--------- Joining ... ---------");
        predecessor = null;

        String requestURL = "http://" + n.getAddressString() + "/" + getHash();
        System.out.println("Requesting: " + requestURL);

        try {
            HttpResponse<String> stringResponse = Unirest.get(requestURL).asString();
            NodeInfo temp = NodeInfo.fromJSON(stringResponse.getBody());
            successor = temp;
            connected = true;
        } catch (UnirestException e) {
            e.printStackTrace();
        }

        System.out.println("\n--------- Joining DONE ---------");

        stabilize();

    }

    @Override
    public void stabilize() {
        String requestURL = "http://" + getSuccessor().getAddressString() + "/pred";
        //NodeInfo temp = getSuccessor();
        NodeInfo temp = null;

        //System.out.println("Requesting: " + requestURL);

        try {
            HttpResponse<String> stringResponse = Unirest.get(requestURL).asString();
            if(stringResponse.getStatus()==200) {
                temp = NodeInfo.fromJSON(stringResponse.getBody());
                //System.out.println("Got: " + temp.toJSON());
                if(NodeInfo.inbetween(getHash(), getSuccessor().getHash(), temp.getHash())){
                    System.out.println("Stabilize - Updated successor");
                    successor = temp;
                }
                /*if(temp.getHash() > getHash() && temp.getHash() < getSuccessor().getHash()) {

                }*/
            }
        } catch (UnirestException e) {
            e.printStackTrace();
        }

        requestURL = "http://" + getSuccessor().getAddressString() + "/notify";

        //System.out.println("Requesting: " + requestURL);

        try {
            HttpResponse<String> stringResponse = Unirest.post(requestURL).field("node", getNodeInfo().toJSON()).asString();

            if(stringResponse.getStatus()==200){
                System.out.println("Stabilize - Successfully notified node");
            }

        } catch (UnirestException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void notify(NodeInfo n) {
        System.out.println("\nNotify received: " + n.toJSON());
        if(getPredecessor() == null){
            predecessor = n;
            return;
        }

        if(NodeInfo.inbetween(getPredecessor().getHash(), getHash(), n.getHash())){
            predecessor = n;
        }
        /*if(getPredecessor() == null || (n.getHash() > getPredecessor().getHash()) && n.getHash() < getHash()){
            predecessor = n;
        }*/

        connected = true;
    }

    @Override
    public void fixFingers() {
        next = next + 1;
        if(next > 31){
            next = 1;
        }

        int nextKey = NodeInfo.getNextKey(getHash(), next);
        //int nextKey = getHash() + (int) (Math.pow(2, (next-1)));
        //System.out.println("Updating entry " + next + ", finding " + nextKey);
        fingers.put(next, findSuccessor(nextKey));
    }

    @Override
    public void checkPredecessor() {

        if(getPredecessor()==null){
            return;
        }

        String requestURL = "http://" + getPredecessor().getAddressString();

        System.out.println("Requesting: " + requestURL);

        try {
            HttpResponse<String> stringResponse = Unirest.get(requestURL).asString();
            if(stringResponse.getStatus()!=200){
                predecessor = null;
                System.out.println("Predecessor dead");
            }

        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }

    @Override
    public NodeInfo findSuccessor(int key) {

        if(NodeInfo.inbetween(getHash(), getSuccessor().getHash(), key) || key == getSuccessor().getHash()){
            return getSuccessor();
        }

        NodeInfo n = closestPreceding(key);

        if(n.getHash() == getHash()){
            return getNodeInfo();
        }

        String requestURL = "http://" + n.getAddressString() + "/" + key;

        try {
            HttpResponse<String> stringResponse = Unirest.get(requestURL).asString();
            return NodeInfo.fromJSON(stringResponse.getBody());
            /*NodeInfo temp = NodeInfo.fromJSON(stringResponse.getBody());
            return temp;*/
        } catch (UnirestException e) {
            e.printStackTrace();
        }

        return null;

        /*
        else {

            NodeInfo tempNode = closestPreceding(key);

            if(tempNode.getHash() == getHash()){
                return getNodeInfo();
            }

            String requestURL = "http://" + tempNode.getAddressString() + "/" + key;

            try {
                HttpResponse<String> stringResponse = Unirest.get(requestURL).asString();
                NodeInfo temp = NodeInfo.fromJSON(stringResponse.getBody());
                return temp;
            } catch (UnirestException e) {
                e.printStackTrace();
            }
        }

        return null;*/
    }

    @Override
    public NodeInfo closestPreceding(int key) {
        List fingerList = new ArrayList();
        fingerList.addAll(fingers.entrySet());
        Collections.reverse(fingerList);

        for(Object e : fingerList) {
            Map.Entry<Integer, NodeInfo> node = (Map.Entry<Integer, NodeInfo>) e;

            if(NodeInfo.inbetween(getHash(), key, node.getValue().getHash())){
                return node.getValue();
            }
        }

        return getNodeInfo();
    }

    @Override
    public int getHash() {
        if(address == null){
            try {
                throw new NodeException("Address isn't set, cannot get hash");
            } catch (NodeException e) {
                System.out.println("NodeException: getHash - " + e.getMessage());
            }
        }

        return NodeInfo.getHash(address);
    }

    @Override
    public InetSocketAddress getAddress() {
        return address;
    }

    public void setAddress(InetSocketAddress address){
        this.address = address;
    }

    public NodeInfo getNodeInfo(){
        return new NodeInfo(this);
    }

    public NodeInfo getPredecessor(){
        if(predecessor != null){
            return predecessor;
        }
        return null;
    }

    public NodeInfo getSuccessor(){
        return successor;
    }

    public HashMap<Integer, NodeInfo> getFingers(){
        return fingers;
    }

    public boolean isConnected(){
        //return (!(getSuccessor().getHash()==getHash())||getPredecessor()!=null);

        return connected;
    }

    public void setSuccessor(NodeInfo n){
        successor = n;
    }

    public void setPredecessor(NodeInfo n) { predecessor = n; }

    public void addSpark(SparkInfo si){
        if(NodeInfo.inbetween(getPredecessor().getHash(), getHash(), si.getHash())){
            keys.put(si.getHash(), si);
        } else {
            NodeInfo temp = findSuccessor(si.getHash());

            if(temp.getHash() == getHash()){
                keys.put(si.getHash(), si);
                return;
            }

            String requestURL = "http://" + temp.getAddressString() + "/spark";

            try {
                HttpResponse<String> stringResponse = Unirest.post(requestURL).body("access_token=" + si.getAccess_token() + "&deviceID=" + si.getDeviceID()).asString();

                if(stringResponse.getStatus()==200){
                    System.out.println("Spark information was sent");
                }
            /*NodeInfo temp = NodeInfo.fromJSON(stringResponse.getBody());
            return temp;*/
            } catch (UnirestException e) {
                e.printStackTrace();
            }
        }
    }

    public HashMap<Integer, SparkInfo> getKeys(){
        return keys;

    }
}
