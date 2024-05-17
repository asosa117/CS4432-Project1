import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        Scanner n = new Scanner(System.in);  // Create a Scanner object
        System.out.println("Enter Buffer Pool Size:");

        String buffPoolSz = n.nextLine();  // Read user input

        //create bufferPool and initialize it with n size
        Frame[] buffers = new Frame[0];
        BufferPool d = new BufferPool(buffers);
        d.initialize(Integer.parseInt(buffPoolSz));

        System.out.println("The program is ready for the next command:");

        Scanner com = new Scanner(System.in);  // Create a Scanner object
        while(com.hasNext()){
            String command = com.next();
            //GET FUNCTION
            if(command.equals("GET")){
                int num = Integer.parseInt(com.next());
                System.out.println(d.GET(num));
            }
            //SET FUNCTION
            if(command.equals("SET")){
                int num = Integer.parseInt(com.next());
                String newContent = com.nextLine().substring(2) + ".";
                System.out.println(d.SET(num, newContent));
            }
            //PIN FUNCTION
            if(command.equals("PIN")){
                int num = Integer.parseInt(com.next());
                System.out.println(d.PIN(num));
            }
            //UNPIN FUNCTION
            if(command.equals("UNPIN")){
                int num = Integer.parseInt(com.next());
                System.out.println(d.UNPIN(num));
            }
        }
    }


}