package maredowell.chord;


import maredowell.impl.SparkInfo;
import maredowell.util.NodeInfo;

import java.net.InetSocketAddress;
import java.util.HashMap;

/**
 * Created by AlexKotsc on 22-02-2015.
 */
public interface Node {
    public void create();

    public void join(NodeInfo n);

    public void stabilize();

    public void notify(NodeInfo n);

    public void fixFingers();

    public void checkPredecessor();

    public NodeInfo findSuccessor(int key);

    public NodeInfo closestPreceding(int key);

    public int getHash();

    public InetSocketAddress getAddress();

    public void setAddress(InetSocketAddress address);

    public NodeInfo getNodeInfo();

    public NodeInfo getPredecessor();

    public NodeInfo getSuccessor();

    public HashMap<Integer, NodeInfo> getFingers();

    public boolean isConnected();

    public void setSuccessor(NodeInfo temp);

    public void setPredecessor(NodeInfo temp);

    public void addSpark(SparkInfo si);

    public HashMap<Integer, SparkInfo> getKeys();
}
