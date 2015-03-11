import maredowell.chord.Node;
import maredowell.chord.TestServer;

/**
 * Created by AlexKotsc on 11-03-2015.
 */
public class testChordRing {
    public static void main(String[] args){
       testChordRing t = new testChordRing();
    }

    public testChordRing(){
        TestServer s1, s2, s3, s4;

        Thread t1 = new Thread(new serverRunnable(8080));
        Thread t2 = new Thread(new serverRunnable(8081));
        Thread t3 = new Thread(new serverRunnable(8082));
        Thread t4 = new Thread(new serverRunnable(8083));
        t2.start();
        t1.start();
        t3.start();
        t4.start();

        while(true){

        }

    }
}
