/**
 * Created by AlexKotsc on 05-03-2015.
 */
public class testOverflow {

    public static void main(String[] args){
        System.out.println("Overflow: " + (Integer.MAX_VALUE + 1));

        System.out.println((Math.pow(2, 31) - 1) == Integer.MAX_VALUE);
    }
}
