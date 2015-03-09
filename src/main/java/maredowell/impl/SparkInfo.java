package maredowell.impl;

import maredowell.util.NodeInfo;

import java.net.*;

/**
 * Created by AlexKotsc on 05-03-2015.
 */
public class SparkInfo {

    String address;
    String access_token;
    InetSocketAddress ip;

    public SparkInfo(String address, String access_token){
        this.address = address;
        this.access_token = access_token;

            try {
                ip = new InetSocketAddress(InetAddress.getByName(address), 80);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }

    }

    public String getAddress() {
        return address;
    }

    public String getAccess_token() {
        return access_token;
    }

    public InetSocketAddress getIP(){
        return ip;
    }

    public int getHash(){
        return NodeInfo.getHash(ip);
    }
}
