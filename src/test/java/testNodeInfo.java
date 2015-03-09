import maredowell.util.NodeInfo;

/**
 * Created by AlexKotsc on 05-03-2015.
 */
public class testNodeInfo {
    public static void main(String[] args){
        testNodeInfo t = new testNodeInfo();

        t.runTest();
    }

    public void runTest(){
        int i = 10;
        int j = 1;
        int k = 12;

        System.out.println(NodeInfo.inbetween(i,j,k));
    }

    public testNodeInfo(){

    }
}
