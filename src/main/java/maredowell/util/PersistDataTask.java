package maredowell.util;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import maredowell.chord.Node;
import maredowell.impl.SparkInfo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.TimerTask;

/**
 * Created by AlexKotsc on 10-03-2015.
 */
public class PersistDataTask extends TimerTask {

    private Node n;

    public PersistDataTask(Node n) {
        this.n = n;
    }

    @Override
    public void run() {
        if (!n.getKeys().isEmpty()) {

            try {
                Class.forName("com.mysql.jdbc.Driver");


            dbSpark db = new dbSpark();

            System.out.println("Persisting data from cores");

            for (Map.Entry<Integer, SparkInfo> e : n.getKeys().entrySet()) {
                if (e.getValue() != null) {
                    Double temperature = 0.0;


                    String requestURL = e.getValue().sparkURL();

                    try {
                        System.out.println("Requesting: " + requestURL);
                        HttpResponse<JsonNode> jsonResponse = Unirest.get(requestURL).asJson();

                        if (jsonResponse.getStatus() == 200) {

                            temperature = (Double) jsonResponse.getBody().getObject().get("result");
                            System.out.println("Got spark - " + temperature);

                            String query = "INSERT INTO  `coreinfo` (`id` , `value` , `time`) VALUES ('" + e.getValue().getHash() + "',  '" + temperature + "', CURRENT_TIMESTAMP);";

                            if(db.execU(query)>0){
                                System.out.println("Core value was added to DB");
                            }
                        }
                    } catch (UnirestException f) {
                        f.printStackTrace();
                    }
                }
            }
            //Close DB after inserting is done
            db.closeConnection();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
