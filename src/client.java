import java.util.*;
import java.net.*;
import java.io.*;

import javax.crypto.Cipher;

// step 1 : importing crypto libraries

import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import java.security.*;
import java.security.spec.*;

public class client{
    static Map<String, SecretKey> sharedKeys = Collections.synchronizedMap(new HashMap<>());
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

                                 MessageDigest sha = MessageDigest.getInstance("SHA-256");
                            byte[] hash = sha.digest(sharedSecret);

                            // System.out.println("Key established with " + otherUsername);
                            // System.out.println("Hash: " + Base64.getEncoder().encodeToString(hash));

                            // Use first 16 bytes for AES-128
                            SecretKey aesKey = new SecretKeySpec(hash, 0, 16, "AES");
                                // sharedsecret is stored per user , like this whole code runs for evry user lets say A , then all the clients coming in to establish connection with A will have a seperate key with A storred in map similar for all other clients , each have seperate map for storing their connection key, remeber AB and BA key will be same only 

                                sharedKeys.put(otherUsername,aesKey);

                            }
                            catch(Exception e){
                                e.printStackTrace();
                            }

                        }
                        else if(message.startsWith("MSG:")){
                            try{
                                String[] parts = message.split(":",3);

                                String sender = parts[1];
                                String encrypted = parts[2];

                                SecretKey key = sharedKeys.get(sender);

                                if(key == null){
                                    System.out.println("No key for " + sender);
                                    continue;
                                }

                                String decrypted = decrypt(encrypted, key);

                                System.out.println(sender + ": " + decrypted);

                            }catch(Exception e){
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

    // 🔹 Command: /msg user message
    if(message.startsWith("/msg ")){

        String[] parts = message.split(" ", 3);

        if(parts.length < 3){
            System.out.println("Usage: /msg <username> <message>");
            continue;
        }

        String receiver = parts[1];
        String actualMessage = parts[2];

        SecretKey key = sharedKeys.get(receiver);

        if(key == null){
            System.out.println("No key found for " + receiver);
            continue;
        }

        try{
            String encrypted = encrypt(actualMessage, key);
            out.println("MSG:" + receiver + ":" + encrypted);

        }catch(Exception e){
            e.printStackTrace();
        }

        continue;
    }

    // optional: broadcast fallback
    out.println(message);
}

        }catch(Exception e){
            e.printStackTrace();
        }

    }
    public static String encrypt(String message, SecretKey key) throws Exception {
    Cipher cipher = Cipher.getInstance("AES");
    cipher.init(Cipher.ENCRYPT_MODE, key);

    byte[] encrypted = cipher.doFinal(message.getBytes());

    return Base64.getEncoder().encodeToString(encrypted);
}
public static String decrypt(String encryptedMessage, SecretKey key) throws Exception {
    Cipher cipher = Cipher.getInstance("AES");
    cipher.init(Cipher.DECRYPT_MODE, key);

    byte[] decoded = Base64.getDecoder().decode(encryptedMessage);
    byte[] decrypted = cipher.doFinal(decoded);

    return new String(decrypted);
}
}