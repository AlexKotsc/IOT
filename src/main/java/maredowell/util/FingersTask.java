package maredowell.util;

import maredowell.chord.Node;

import java.util.TimerTask;

/**
 * Created by AlexKotsc on 04-03-2015.
 */
public class FingersTask extends TimerTask {

    private Node n;

    public FingersTask(Node n){
        this.n = n;
    }

    public void run(){
        //if(n.isConnected()) {
            //System.out.println("\n------------ Fixing fingers ... ------------");
            n.fixFingers();
            //System.out.println("------------ Fixing fingers DONE ------------");
        //}
    }
}
