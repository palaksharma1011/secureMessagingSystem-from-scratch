import java.util.*;
import java.net.*;
import java.io.*;

// step 1 : importing crypto libraries

import javax.crypto.KeyAgreement;

import java.security.*;
import java.security.spec.*;

public class client{
    static Map<String, byte[]> sharedKeys = Collections.synchronizedMap(new HashMap<>());
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

            // step 2: generate DH key pair
            KeyPairGenerator keyGen= KeyPairGenerator.getInstance("DH");
            keyGen.initialize(2048);

            KeyPair keyPair = keyGen.generateKeyPair();
            KeyAgreement keyAgree = KeyAgreement.getInstance("DH");
            keyAgree.init(keyPair.getPrivate());

            // step 3 : Send public key
            byte[] publicKeyBytes = keyPair.getPublic().getEncoded();
            String publicKeyBase64=Base64.getEncoder().encodeToString(publicKeyBytes);
                        


            // thread 1 - receive message 
            new Thread(()->{
                try{
                    String message;
                    while((message = in.readLine())!=null){

                        if(message.startsWith("KEY:")){
                             
                            try{
                                String[] parts=message.split(":",3);

                                String otherUsername = parts[1];
                                String keyBase64 = parts[2];

                                byte[] keyBytes = Base64.getDecoder().decode(keyBase64);

                                KeyFactory keyFactory = KeyFactory.getInstance("DH");
                                X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
                                PublicKey otherPublicKey = keyFactory.generatePublic(keySpec);

                                
                                // NEW CHANGE: creating new keyAgreement (fresh state)
                                KeyAgreement ka=KeyAgreement.getInstance("DH");
                                ka.init(keyPair.getPrivate());

                                ka.doPhase(otherPublicKey,true);
                                byte[] sharedSecret=ka.generateSecret();

                                // sharedsecret is stored per user , like this whole code runs for evry user lets say A , then all the clients coming in to establish connection with A will have a seperate key with A storred in map similar for all other clients , each have seperate map for storing their connection key, remeber AB and BA key will be same only 

                                sharedKeys.put(otherUsername,sharedSecret);

                            // Proof
                            MessageDigest sha = MessageDigest.getInstance("SHA-256");
                            byte[] hash = sha.digest(sharedSecret);

                            System.out.println("Key established with " + otherUsername);
                            System.out.println("Hash: " + Base64.getEncoder().encodeToString(hash));

                            }
                            catch(Exception e){
                                e.printStackTrace();
                            }

                        }

                        else{
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
            out.println("KEY:"+username+":"+publicKeyBase64);

            // thread 2 - send message 

            String message;
while((message = userInput.readLine()) != null){

    if(message.equalsIgnoreCase("/keys")){
        System.out.println("---- Shared Keys ----");

        synchronized(sharedKeys){
            for(Map.Entry<String, byte[]> entry : sharedKeys.entrySet()){
                String user = entry.getKey();
                byte[] key = entry.getValue();

                try{
                    MessageDigest sha = MessageDigest.getInstance("SHA-256");
                    byte[] hash = sha.digest(key);

                    String hashBase64 = Base64.getEncoder().encodeToString(hash);

                    System.out.println(user + " → " + hashBase64);

                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }

        System.out.println("---------------------");
        continue; // 🚨 DO NOT send to server
    }

    out.println(message);
}

        }catch(Exception e){
            e.printStackTrace();
        }

    }
}