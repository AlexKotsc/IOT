package maredowell.util;

import maredowell.chord.Node;

import java.util.TimerTask;

/**
 * Created by AlexKotsc on 04-03-2015.
 */
public class StabilizeTask extends TimerTask {

    private Node n;

    public StabilizeTask(Node n){
        this.n = n;
    }

    public void run(){
        if(n.isConnected()) {
            //System.out.println("\n------------ Stabilizing ... ------------");
            n.stabilize();
            //System.out.println("------------ Stabilizing DONE ------------");
        }
    }
}
