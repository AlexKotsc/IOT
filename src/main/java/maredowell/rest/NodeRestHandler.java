package maredowell.rest;


import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import maredowell.chord.Node;
import maredowell.impl.SparkInfo;
import maredowell.util.NodeInfo;
import maredowell.util.dbSpark;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * Created by AlexKotsc on 22-02-2015.
 */

@Path("/")
public class NodeRestHandler {

    @Context
    private ServletContext context;

    private Node myNode;
    private Boolean created = false;

    @GET
    public Response showSite(@Context HttpServletRequest req){
        refreshNode();

        if(created == false){
            return Response.status(500).entity("Node not created, <a href='/create'>create here</a>").build();
        }

        StringBuilder resBuilder = new StringBuilder();

        resBuilder.append("<!doctype html><html lang='en'><head><meta charset='utf-8'>");
        resBuilder.append("<title>Chord@" + myNode.getAddress().getHostName() +":" + myNode.getAddress().getPort() + "</title>");
        resBuilder.append("<link rel='stylesheet' href='http://alexkotsc.tk/chord/css/unsemantic-grid-responsive.css' />");
        resBuilder.append("<link rel='stylesheet' href='http://alexkotsc.tk/chord/css/style.css' />");
        //resBuilder.append("<link rel='stylesheet' href='" + req.getContextPath() + "/src/main/webapp/css/stylesheets/unsemantic-grid-responsive.css' />");
        //resBuilder.append("<link rel='stylesheet' href='" + req.getContextPath() + "/src/main/webapp/css/style.css'>");
        resBuilder.append("<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js\"></script>");
        resBuilder.append("</head><body><div class='grid_container'><div id='header' class='prefix-40 grid-20 suffix-40'><h1>Chord-node</h1>");
        resBuilder.append("</div><div id='topchordcont' class='prefix-40 grid-20 suffix-40 chordcont'><p><b>Node information</b></p>");
        resBuilder.append("<p>JSON: " + myNode.getNodeInfo().toJSON() + "</p>");
        resBuilder.append("<p id='pID'>ID: " + myNode.getHash() + "</p>");
        resBuilder.append("<p>Address: " + myNode.getNodeInfo().getAddressString() + "</p>");
        resBuilder.append("</div><div id='predbtn' class='prefix-30 grid-10 sidebtn'>");
        if(myNode.getPredecessor()!=null){
            resBuilder.append("<a target='_blank' href='http://" + myNode.getPredecessor().getAddressString() + "'><p>&lt;&lt; Predecessor</p></a>");

        } else {
            resBuilder.append("<p>&lt;&lt; Predecessor</p>");
        }

        resBuilder.append("</div><div  id='middlechordcont' class='grid-20 chordcont'>");
         resBuilder.append("<p><b>Fingertable</b></p><table><thead><tr>" +
                "<th>Identifier</th><th>Internet Protocol Address</th></tr></thead><tbody>");

        for(Map.Entry<Integer, NodeInfo> e : myNode.getFingers().entrySet()){
            if(e.getValue()!=null) {
                resBuilder.append("<tr><td>" + NodeInfo.getNextKey(myNode.getHash(), e.getKey()) + "</td><td>" + e.getValue().getAddressString() + "</td></tr>");
            }
        }

        //System.out.println(myNode.getFingers());

        resBuilder.append("</tbody></table>");

        resBuilder.append("<p><b>Keys</b></p><table><thead><tr><th>Identifier</th><th>Status</th></tr></thead><tbody>");

        for(Map.Entry<Integer, SparkInfo> e : myNode.getKeys().entrySet()){
            if(e.getValue()!=null) {

                Double temperature = 0.0;

                String requestURL = e.getValue().sparkURL();

                /*String requestURL = "http://" + e.getValue().getAddress() + "/temperature?access_token=" + e.getValue().getAccess_token();*/

                try {
                    System.out.println("Requesting: " + requestURL);
                    HttpResponse<JsonNode> jsonResponse = Unirest.get(requestURL).asJson();
                    //HttpResponse<Json> stringResponse = Unirest.get(requestURL).asJson();

                    if(jsonResponse.getStatus()==200){
                        System.out.println("Got spark");
                        temperature = (Double) jsonResponse.getBody().getObject().get("result");
                    }
            /*NodeInfo temp = NodeInfo.fromJSON(stringResponse.getBody());
            return temp;*/
                } catch (UnirestException f) {
                    f.printStackTrace();
                }

                resBuilder.append("<tr>" +
                                    "<td>" + e.getValue().getHash() + "</td>" +
                        "<td>" + temperature + "</td>" +
                                    "</tr>");
            }
        }

        //System.out.println(myNode.getFingers());

        resBuilder.append("</tbody></table>");

        resBuilder.append("</div><div id='succbtn' class='grid-10 suffix-30 sidebtn'>");
        resBuilder.append("<a target='_blank' href='http://" + myNode.getSuccessor().getAddressString() + "'><p>Successor &gt;&gt;</p></a>");
        resBuilder.append("</div><div id='bottomchordcont' class='prefix-40 grid-20 suffix-40 chordcont'>" +
                "<p><b>Find node</b></p><form id='findform'>" +
                "<input type='text' placeholder='Input node ID' name='id' id='id'></input>" +
                "<input id='search' type='button' value='Search'/></form>");

        resBuilder.append("<script type='text/javascript'>" +
                "$(function(){" +
                    "$(\"#search\").click(function(){" +
                        "window.location.href = \"http://" + myNode.getNodeInfo().getAddressString() + "/\" + $('#id').val();" +
                    "});" +
                "});" +
                "</script>");

        resBuilder.append("<p><b>Join ring</b></p>" +
                "<form action='join' method='POST'>" +
                "<input type='text' placeholder='Address' name='ip' id='ip'></input>" +
                "<input type='submit' value='Join'/>" +
                "</form>");

        resBuilder.append("<p><b>Set successor</b></p>" +
                "<form action='succ' method='POST'>" +
                "<input type='text' placeholder='JSON string' name='node' id='node'></input>" +
                "<input type='submit' value='Set'/>" +
                "</form>");
        resBuilder.append("<p><b>Set predecessor</b></p>" +
                "<form action='pred' method='POST'>" +
                "<input type='text' placeholder='JSON string' name='node' id='node'></input>" +
                "<input type='submit' value='Set'/>" +
                "</form>");

        resBuilder.append("</div></div></body></html>");
        return Response.status(200).entity(resBuilder.toString()).build();
    }

    @GET @Path("create")
    public Response initializeNode(@Context HttpServletRequest req){
        refreshNode();

        //System.out.println("Received CREATE request...");

        if(created == false){

            initNode(req);

            return Response.status(200).entity("Node created at <a href='http://" + req.getLocalAddr() + ":" + req.getLocalPort() + "'>" + req.getRemoteAddr() + ":" + req.getLocalPort() + "</a>").build();
        } else {
            return Response.status(200).entity("Node already created at <a href='http://" + req.getLocalAddr() + ":" + req.getLocalPort() + "'>" + req.getRemoteAddr() + ":" + req.getLocalPort() + "</a>").build();
        }

    }

    @GET @Path("{id}")
    public Response findID(@PathParam("id") String id){
        refreshNode();
        //System.out.println("Received findID request...");

        if(created == false){
            return Response.status(500).entity("Node not created, <a href='/create'>create here</a>").build();
        }

        if(id.matches("\\d{1,10}")){

            int tempID = Integer.parseInt(id);

            if(tempID == myNode.getHash()){
                return Response.status(200).entity(myNode.getSuccessor().toJSON()).build();
            }
            /*if(NodeInfo.getHash(myNode.findSuccessor(id).getAddress())) {
                return Response.status(500).entity("Node with ID: " + id + " could not be found").build();
            }*/
            NodeInfo temp = myNode.findSuccessor(tempID);
            if(temp!=null) {
                return Response.status(200).entity(temp.toJSON()).build();
            }
            return Response.status(500).entity("Could not find successor, something went wrong").build();

        } else {
            return Response.status(500).entity("Invalid URI").build();
        }

       // return Response.status(200).entity("Trying to find: " + id).build();
    }

    @GET @Path("pred")
    public Response getPredecessor(){
        refreshNode();
        //System.out.println("Received PRED request...");
        if(myNode.getPredecessor()!=null) {
            return Response.status(200).entity(myNode.getPredecessor().toJSON()).build();
        }
        return Response.status(500).entity("Could not find predecessor").build();
    }

    @POST @Path("join")
    public Response joinRing(@FormParam("ip") String s, @Context HttpServletRequest req){
        refreshNode();
        //System.out.println("Received JOIN request...");

        String[] result = s.split(":");

        try {
            myNode.join(new NodeInfo(new InetSocketAddress(InetAddress.getByName(result[0]), Integer.parseInt(result[1]))));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        saveNode();

        return Response.status(200).entity("Joined node").build();
    }


    @POST @Path("notify")
    public Response notify(@FormParam("node") String n){
        refreshNode();
        //System.out.println("Received NOTIFY request...");
        NodeInfo temp = NodeInfo.fromJSON(n);

        myNode.notify(temp);
        saveNode();

        return Response.status(200).entity("Was notified by: " + temp.toJSON()).build();
    }

    @POST @Path("succ")
    public Response setSuccessor(@FormParam("node") String n){
        refreshNode();
        NodeInfo temp = NodeInfo.fromJSON(n);

        myNode.setSuccessor(temp);
        saveNode();

        return Response.status(200).entity("Successor was set").build();
    }

    @POST @Path("pred")
    public Response setPredecessor(@FormParam("node") String n){
        refreshNode();
        NodeInfo temp = NodeInfo.fromJSON(n);

        myNode.setPredecessor(temp);
        saveNode();

        return Response.status(200).entity("Predecessor was set").build();
    }

    /*@POST @Path("spark")
    public Response addSpark(@QueryParam("address") String addr, @QueryParam("access_token") String access_token){
        refreshNode();

        myNode.addSpark(new SparkInfo(addr, access_token));

        saveNode();
        return Response.status(201).entity("Spark was added").build();
    }*/

    @GET @Path("spark/{id}")
    public Response displaySparkData(@PathParam("id") String id) {
        refreshNode();

        System.out.println("Looking up data for core with id: " + id);

        int sparkid = Integer.valueOf(id);

        if (myNode.getKeys().containsKey(sparkid)) {
            StringBuilder sBuilder = new StringBuilder();

            dbSpark myDB = new dbSpark();

            ResultSet mySet = myDB.execQ("SELECT * FROM `coreinfo` WHERE `id` = " + sparkid);

            if(mySet != null){
                System.out.println("Got resultset!");
            }

            sBuilder.append("<html>" +
                    "<head>" +
                    "<script type=\"text/javascript\" src=\"https://www.google.com/jsapi?autoload=" +
                    "{'modules':[{'name':'visualization', 'version':'1', 'packages':['corechart']}]}\"></script>" +
                    "<script type=\"text/javascript\">google.setOnLoadCallback(drawChart);" +
                    "function drawChart() {var data = google.visualization.arrayToDataTable(" +
                    "[['Tid', 'Temperatur'] ");

            try {
                while(mySet.next()){
                    sBuilder.append(",['" + mySet.getTimestamp("time").getTime() + "', " + mySet.getDouble("value") + "]");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }


            sBuilder.append("]); " +
                    "var options = {title: 'Company Performance', curveType: 'function', legend: { position: 'bottom' }};" +
                    "var chart = new google.visualization.LineChart(document.getElementById('curve_chart'));" +
                    "chart.draw(data, options);" +
                    "}</script>" +
                    "</head>" +
                    "<body>" +
                    "<div id=\"curve_chart\" style=\"width: 900px; height: 500px\"></div>" +
                    "</body>" +
                    "</html>");

            return Response.status(200).entity(sBuilder.toString()).build();

        }


        return Response.status(500).entity("Could not find spark").build();
    }

    @POST @Path("spark")
    public Response testSparkPost(String addr){
        refreshNode();

        String[] arr = addr.split("&");
        /*String address = arr[0].split("=")[1];*/
        String access_token = arr[0].split("=")[1];
        String deviceID = arr[1].split("=")[1];

        SparkInfo tempSpark = new SparkInfo(access_token, deviceID);

        System.out.println("DeviceID: " + deviceID + "\nAccess Token: " + access_token);

        myNode.addSpark(tempSpark);

        saveNode();
        return Response.status(200).entity("Spark was added").build();
    }

    private void initNode(HttpServletRequest req) {
        refreshNode();
        myNode.setAddress(new InetSocketAddress(req.getRemoteAddr(),req.getLocalPort()));
        myNode.create();
        created = true;
        saveNode();
    }

    private void refreshNode(){
        myNode = (Node) context.getAttribute("node");
        created = (Boolean) context.getAttribute("created");
    }

    private void saveNode() {
        context.setAttribute("node", myNode);
        context.setAttribute("created", created);
    }
}
