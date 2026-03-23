import java.net.*;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.io.*;
import java.util.*;

import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class server{

    // public static List<ClientHandler> clients=new ArrayList<>();
    public static List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<>());

    
        // step 4 : thread is created for that client

    public static class ClientHandler extends Thread{
        private String username;
        private String publicKey;

        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        public ClientHandler(Socket socket){
            this.socket=socket;

            try {
                // step 5 : listen to message 
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(),true);

            }catch(IOException e){
                e.printStackTrace();
            }
        }

        public void sendMessage(String message){
            out.println(message);
        }

        @Override 
        public void run(){
            try{
                String message;
                out.println("Enter the username: ");
                username=in.readLine();

                System.out.println(username +" joined the chat");
                server.broadcast(username + " has joined the chat", this);
                
                        // step 6 : broadcast the message to every client 
                while((message = in.readLine()) != null){
                    System.out.println("[" + username + "] → " + message);

                    
                        // generating fake key
                        KeyPair kp=genFakeKeyPair();
                        SecretKey fakeKey=genFakePrivateKey(kp,genFakePublicKey(kp));

                        byte[] keyBytes = fakeKey.getEncoded();
                        String base64FakeKey = Base64.getEncoder().encodeToString(keyBytes);

                        System.out.println("AES Key (Base64): " + base64FakeKey);

                    if(message.startsWith("KEY:")){


                        this.publicKey = message;
                        synchronized(clients){
                            for(ClientHandler client : clients){
                                if(client != this){
                                    client.sendMessage(message); // send RAW
                                    if(client.publicKey != null){
                                        this.sendMessage(client.publicKey);
                                    }
                                }

                            }
                        }
                        continue;
                    }
                    if(message.startsWith("MSG:")){
                        String[] parts = message.split(":",3);
                        String receiver = parts[1];

                        synchronized(clients){
                            for(ClientHandler client : clients){
                                if(client.username.equals(receiver)){
                                    client.sendMessage("MSG:" + username + ":" + parts[2]);
                                    client.sendMessage(message);

                                    break;
                                }
                            }
                        }
                        continue;
                    }
                    // if(message.startsWith("MSG:")){

                    //     String[] parts = message.split(":",3);
                    //     String receiver = parts[1];
                    //     String encrypted = parts[2];

                    //     // ⚠️ ATTACK: Modify ciphertext (simulate MITM)
                    //     if(encrypted.length() > 4){
                    //         encrypted = encrypted.substring(0, encrypted.length()-2) + "AA";
                    //     }

                    //     synchronized(clients){
                    //         for(ClientHandler client : clients){
                    //             if(client.username.equals(receiver)){
                    //                 client.sendMessage("MSG:" + username + ":" + encrypted);
                    //                 break;
                    //             }
                    //         }
                    //     }
                    //     continue;
                    // }


                    // normal messages
                    // for everyone
                    String fullMessage = "[" + username + "]: " + message;

                    server.broadcast(fullMessage, this);

                }

            }catch (IOException e){
                System.out.println("Client disconnected");

            }
            finally{
                try{
                    socket.close();
                }catch(IOException e){
                    e.printStackTrace();
                }
                synchronized(clients){
                    clients.remove(this);
                }
                System.out.println(username+" left the chat");
            }
        }
    }

            // step 6 : broadcast the message to every client 
    public static void broadcast(String message ,ClientHandler sender ){


        synchronized(clients){
            for(ClientHandler client:clients){
                if(client != sender){
                    client.sendMessage(message);
                }

            }
        }

    }
    public static void main(String[] args){
        int port =1234;

        // step 1 : start the server 
        try(ServerSocket serverSocket=new ServerSocket(port)){
            System.out.println("Server started on "+ port);

            while(true){
                // step 2 : wait for clients 
                Socket socket=serverSocket.accept();
                System.out.println("Client connected....");
                

        // step 3 : client connects 

                ClientHandler clientHandler =new ClientHandler(socket);
                    synchronized(clients){
                        clients.add(clientHandler);
                    }                
                        // step 5 : listen to message 
                clientHandler.start();
            }


        }catch(IOException e){
            e.printStackTrace();
        }

    }
    public static String genFakePublicKey(KeyPair keyPair){

        try{
        KeyAgreement keyAgree = KeyAgreement.getInstance("DH");
        keyAgree.init(keyPair.getPrivate());

        // step 3 : Send public key
        byte[] publicKeyBytes = keyPair.getPublic().getEncoded();
        String publicKeyBase64=Base64.getEncoder().encodeToString(publicKeyBytes);
        return publicKeyBase64;
        }catch(Exception e){
            throw new RuntimeException("Failed to generate public key", e);
        }
    }
    
    public static KeyPair genFakeKeyPair(){

        try{
        KeyPairGenerator keyGen= KeyPairGenerator.getInstance("DH");
        keyGen.initialize(2048);

        KeyPair keyPair = keyGen.generateKeyPair();

        return keyPair;
        }catch(Exception e){
            throw new RuntimeException("Failed to generate key Pair", e);
        }


    }   
    public static SecretKey genFakePrivateKey(KeyPair keyPair,String keyBase64){
                            try{
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

                            // Use first 16 bytes for AES-128
                            SecretKey aesKey = new SecretKeySpec(hash, 0, 16, "AES");
                            return aesKey;

                            }
                            catch(Exception e){
                                 throw new RuntimeException("Failed to generate shared secret key", e);
                            }
    }
}
        // step 1 : start the server 
        // step 2 : wait for clients 
        // step 3 : client connects 
        // step 4 : thread is created for that client
        // step 5 : listen to message 
        // step 6 : broadcast the message to every client 
