import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * Created by AlexKotsc on 10-03-2015.
 */
public class testDBConn {

    public static void main(String[] args){

        try {

            Class.forName("com.mysql.jdbc.Driver");

            /*Connection conn = DriverManager.getConnection("jdbc:mysql://mysql6.000webhost.com/a8153419_iot", "a8153419_iotuser", "iot123");*/

            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/core", "root", "");

            //Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/core", "iot", "iot123");

            Statement stm = conn.createStatement();

            String query = "INSERT INTO  `coreinfo` (`id` , `value` , `time`) VALUES ('1234',  '666', CURRENT_TIMESTAMP);";

            if(stm.executeUpdate(query)>0){
                System.out.println("Spark info was added to DB");
            }



            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
