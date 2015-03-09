package maredowell.util;

import maredowell.chord.Node;

import java.util.TimerTask;

/**
 * Created by AlexKotsc on 04-03-2015.
 */
public class PredecessorTask extends TimerTask {

    private Node n;

    public PredecessorTask(Node n){
        this.n = n;
    }

    public void run(){
        if(n.isConnected()) {
            System.out.println("\n------------ Checking predecessors ... ------------");
            n.checkPredecessor();
            System.out.println("------------ Checking predecessors DONE ------------");
        }
    }
}
