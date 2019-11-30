package Go;

import GUI.ClientGUI;

import java.util.Arrays;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
       // System.out.println( "Hello World!" );
        ClientGUI gui = new ClientGUI();
        int[][] stones = new int[3][2];
        stones[0][0] = 1;
        stones[0][1] = 2;
        stones[1][0] = 3;
        stones[1][1] = 4;
        stones[2][0]=5;
        stones[2][1]=6;
        String cos = Arrays.toString(stones[0])+"-"+Arrays.toString(stones[1])+"-"+Arrays.toString(stones[2])+";";
        System.out.println(cos);
        String[] rzedy = cos.split(";");
        String[] pojedyncze = rzedy[0].split("-");
        System.out.println(rzedy[0]);
        System.out.println(pojedyncze[0]);




    }
}
