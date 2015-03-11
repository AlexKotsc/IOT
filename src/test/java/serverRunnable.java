import maredowell.chord.TestServer;

/**
 * Created by AlexKotsc on 11-03-2015.
 */
public class serverRunnable implements Runnable {

    TestServer ts = null;
    int port;

    public serverRunnable(int port){
        this.port = port;
    }

    @Override
    public void run() {
        if(ts == null) {
            this.ts = new TestServer(port);
        }
        while(ts.getServlet()!=null) {
            System.out.println(ts.getServlet().toString());
        }
    }
}
