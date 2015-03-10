package maredowell.impl;

import maredowell.util.NodeInfo;

import java.net.*;

/**
 * Created by AlexKotsc on 05-03-2015.
 */
public class SparkInfo {

    String address;
    String access_token;
    String deviceID;
    InetSocketAddress ip;

    /*public SparkInfo(String address, String access_token){
        this.address = address;
        this.access_token = access_token;

            try {
                ip = new InetSocketAddress(InetAddress.getByName(address), 80);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }

    }*/

    public SparkInfo(String access_token, String deviceID){
        this.access_token = access_token;
        this.deviceID = deviceID;

        URL tURL = null;
        try {
            tURL = new URL(sparkURL());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        ip = new InetSocketAddress(tURL.getHost(), 80);
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

    public String getDeviceID (){return deviceID;}

    public int getHash(){
        if(ip == null) { System.out.println("IP is null"); }
            return NodeInfo.getHash(ip);

    }

    public String sparkURL(){
        return "https://api.spark.io/v1/devices/" + deviceID + "/temperature?access_token=" + access_token;
    }
}
