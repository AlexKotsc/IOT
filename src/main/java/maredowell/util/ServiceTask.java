package maredowell.util;


import maredowell.chord.Node;

import java.util.TimerTask;

/**
 * Created by AlexKotsc on 24-02-2015.
 */
public class ServiceTask extends TimerTask {

    private Node n;

    public ServiceTask(Node n){
        this.n = n;
    }

    public void run(){
        if(n.isConnected()) {

            n.stabilize();
            try {
                wait(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            n.fixFingers();
            try {
                wait(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            n.checkPredecessor();
        }
    }
}
