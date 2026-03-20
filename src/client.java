import java.util.*;
import java.net.*;
import java.io.*;

// step 1 : importing crypto libraries

import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;

import java.security.*;
import java.security.spec.*;

public class client{
    public static void main(String[] args){
        Map<String, SecretKey> sharedKeys = new HashMap<>();

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

            boolean keyEstablished = false;
            // step 2: generate DH key pair
            KeyPairGenerator keyGen= KeyPairGenerator.getInstance("DH");
            keyGen.initialize(2048);

            KeyPair keyPair = keyGen.generateKeyPair();
            KeyAgreement keyAgree = KeyAgreement.getInstance("DH");
            keyAgree.init(keyPair.getPrivate());

            // step 3 : Send public key
            byte[] publicKeyBytes = keyPair.getPublic().getEncoded();
            String publicKeyBase64=Base64.getEncoder().encodeToString(publicKeyBytes);
                        
            final KeyAgreement finalKeyAgree = keyAgree;


            // thread 1 - receive message 
            new Thread(()->{
                try{
                    String message;
                    while((message = in.readLine())!=null){

                        if(message.startsWith("KEY:")){
                             
                            try{
                                String keyBase64 = message.substring(4).trim();

                                    if(keyBase64.isEmpty()){
                                        System.out.println("Empty key ignored");
                                        return;
                                    }

                                    if(keyBase64.length() < 50){ // DH keys are long
                                        System.out.println("Invalid short key ignored");
                                        return;
                                    }
                                byte[] keyBytes = Base64.getDecoder().decode(keyBase64);

                                KeyFactory keyFactory = KeyFactory.getInstance("DH");
                                X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
                                PublicKey otherPublicKey = keyFactory.generatePublic(keySpec);

                                // generate shared secret 
                                finalKeyAgree.doPhase(otherPublicKey, true);
                                byte[] sharedSecret = finalKeyAgree.generateSecret();

                                // System.out.println("Shared key is "+sharedSecret);

                                System.out.println("Shared key Established");
                                // keyEstablished=true;

                            }
                            catch(Exception e){
                                e.printStackTrace();
                            }

                        }else{
                            System.out.println(message);
                        }


                    }
                }catch(IOException e){
                    System.out.println("Disconnected from server");
                }
            }).start();
            // take username input
            String username = userInput.readLine();
            out.println(username);   // send username first
            
            // sending to server 
            out.println("KEY established for "+username);

            // thread 2 - send message 

            String message;
            while((message=userInput.readLine())!=null){
                out.println(message);
            }

        }catch(Exception e){
            e.printStackTrace();
        }

    }
}