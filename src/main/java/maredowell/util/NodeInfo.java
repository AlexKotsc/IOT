package maredowell.util;


import maredowell.chord.Node;
import org.json.JSONObject;

import java.net.InetSocketAddress;

/**
 * Created by AlexKotsc on 23-02-2015.
 */
public class NodeInfo {

    private InetSocketAddress address;

    public NodeInfo(Node n){
        this.address = n.getAddress();
    }

    public NodeInfo(InetSocketAddress address){
        this.address = address;
    }

    public String toJSON(){
        return "{" +
                "\"hash\":\"" + getHash(address) + "\", " +
                "\"address\":\"" + address.getAddress().getHostAddress() + "\", " +
                "\"port\":\"" + address.getPort() + "\"" +
                "}";
    }

    public static NodeInfo fromJSON(String s){
        //System.out.println("JSONString: " + s);
        JSONObject jsonNode = new JSONObject(s);

        return new NodeInfo(new InetSocketAddress((String) jsonNode.get("address"), Integer.parseInt((String) jsonNode.get("port"))));
    }

    public static int getHash(InetSocketAddress address){
        return (Math.abs(address.hashCode()));
    }

    public int getHash(){
        return getHash(address);
    }

    public static int getNextKey(int i, int j){

        int tempj = (int) Math.pow(2, j-1);

        if(i+tempj < 0){
            return (i+tempj)+Integer.MAX_VALUE;
        }

        return (i+tempj);


/*
        if((~(i ^ tempj) & (i ^ (i + tempj))) < 0){
            return (int) ((i+tempj)-(Math.pow(2,31)-1));
        }

        return (i + tempj);*/
    }

    public InetSocketAddress getAddress() {return address;}

    public String getAddressString(){
        return address.getAddress().getHostAddress() + ":" + address.getPort();
    }

    public static Boolean inbetween(int i, int j, int key){
        if(i > j){
          return (key > i || key < j);
        }
        return (key > i && key < j);
    }
}
