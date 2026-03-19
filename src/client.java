import java.util.*;
import java.net.*;
import java.io.*;

public class client{
    public static void main(String[] args){

        String host="localhost";
        int port=1234;

        try{
            Socket socket=new Socket(host,port);
            System.out.println("Client is connected to server");

            // input from user
            BufferedReader userInput=new BufferedReader(new InputStreamReader(System.in));
            // input from server
            BufferedReader in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // output to server
            PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
            
            // thread 1 - receive message 
            new Thread(()->{
                try{
                    String message;
                    while((message = in.readLine())!=null){
                        System.out.println(message);
                    }
                }catch(IOException e){
                    System.out.println("Disconnected from server");
                }
            }).start();
            // thread 2 - send message 

            String message;
            while((message=userInput.readLine())!=null){
                out.println(message);
            }

        }catch(IOException e){
            e.printStackTrace();
        }

    }
}