import java.net.*;
import java.io.*;
import java.util.*;

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
                                    break;
                                }
                            }
                        }
                        continue;
                    }


                    // normal messages
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
}
        // step 1 : start the server 
        // step 2 : wait for clients 
        // step 3 : client connects 
        // step 4 : thread is created for that client
        // step 5 : listen to message 
        // step 6 : broadcast the message to every client 

