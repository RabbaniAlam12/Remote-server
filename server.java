/**
 * <Remote server | Assignment 3>
 * 
 * @author: Rabbani Alam
 * @Student id: 201824422
 * 
 * This program demonstrates how simple logic can be coded on a remote server and accessed by a standard client.
*/


import java.net.Socket;
import java.net.ServerSocket;
import java.io.BufferedReader;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.*;

class server {
	private static ServerSocket serverSocket;
	private static Socket clientSocket;
	private static BufferedReader in;;
	private static PrintWriter out;
	private static String output = "";
	private static String eor = "[EOR]";
	private static String userName, userPassword;
		
	//Setting up a connection
	private static void setup() throws IOException {
		//A server socket with an OS picked port is created
		serverSocket = new ServerSocket(0);		
		toConsole("Server port is " + serverSocket.getLocalPort()); 
		
		//The server is waiting for the connection with the client.
		clientSocket = serverSocket.accept();
		
		// get the input stream and attach to a buffered reader
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        
        // get the output stream and attach to a printWriter
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        
        toConsole("Accepted connection from " 
        		+ clientSocket.getInetAddress() + " at port"
        		+ clientSocket.getPort());
        
        sendGreeting();
		
	}
	
	private static void sendGreeting() {
		appendOutput("Welcome to Qnet\n");
		appendOutput("Enter Username: ");
		toConsole("Username requested.");
		sendOutput();
	}
	
	
	private static void toConsole(String message) {
		//Displays message to the console
		System.out.println(message);
	}
	
	 // Add a line to the next message to be sent to the client
    private static void appendOutput(String line) {
        output += line + "\r";
    }
    
    // Send next message to client
    private static void sendOutput() {
        out.println( output + "[EOR]");
        out.flush();
        output = "";
    }
    
	//What happens while client and server are connected
    private static void talk() throws IOException {
    	echoClient();
    	disconnect();
    }
    
	//Sends out message to the client to enter their username and password and does a verification.
    private static void echoClient() throws IOException {
    	int count  = 0;
    	String name = "Sammy"; //Username hardcoded to Sammy
    	String password = "woof"; //password hardcoded to woof
			
    		while (((userName = in.readLine()) != null) && count != 6) {
				//The loop only works for 5 times in total and then disconnects
				count += 1;

				if (count == 6) {
			 		toConsole("Incorrect attempts");
					appendOutput("\nServer disconnected, connect to the server again.\n");
			 		disconnect();		
				}
				
				toConsole(userName);
				if (userName.equals(name)){
					appendOutput("\nUsername matched!\n");
					appendOutput("Enter Password: ");
					sendOutput();

					while(((userPassword = in.readLine()) != null) && count != 5){
						//If the username is correct then this loop only gives the user four more chances to type in the password correctly before it disconnects
						count += 1;
						toConsole(userPassword);

						if (count == 5) {
							toConsole("Incorrect attempts");
						   	appendOutput("\nServer disconnected, connect to the server again.\n");
							disconnect();		
					   }

						if (userPassword.equals(password)){
							appendOutput("\nPassword match!");
							appendOutput("\nWelcome Sammy!\n");
							appendOutput("Enter a Command (add x / remove / print): \n");
                            sendOutput();                            

							//Calling the method commands()
							commands();
						}
						else {
							toConsole("User password requested.");
							appendOutput("\nIncorrect Password, try again!\n");
							appendOutput("Enter Password ");
                            sendOutput();
						}

						sendOutput();
					}
				}

				else {
					appendOutput("Incorrect username, try again!\n");
					appendOutput("Enter Username: ");
					sendOutput();
					toConsole("Username requested");

				}
			}
        	
    		sendOutput();
    	} 
    
	/**
	 * This method performs the logic on the remote server.
	 * It can add a string to a queue;
	 * print the entire queue
	 * remove an item using FIFO!
	 */
	private static void commands() throws IOException {
		Queue<String> myList = new LinkedList<>();
		String line;
		
		while((line = in.readLine()) != null) {
			
			String[] command = line.split("\\s");

			if (command[0].equals("add")){
				myList.add(command[1]);
				toConsole(command[1] + " added");
				appendOutput("item added\n");
				appendOutput("\nEnter a Command (add x / remove / print): ");
			}

			else if (command[0].equals("print")){
				toConsole("Command received: print");
				appendOutput("\nQueue contains:\n" + myList.toString() + "\n");
				appendOutput("Enter a Command (add x / remove / print): ");
			}

			else if (command[0].equals("remove")) {
				
				if (myList.isEmpty()){
					toConsole("Command received: remove");
					toConsole("Empty Queue");
					appendOutput("\nThe queue is empty, add items to the queue.");
					appendOutput("\nEnter a Command (add x / remove / print): ");
				}

				else {
					String temp = myList.remove();
					toConsole("Command received: remove");
					appendOutput("\n" + temp + " has been removed from the queue.\n");
					appendOutput("\nEnter a Command (add x / remove / print): ");
				}

			}

			sendOutput();

		}
		
		
	}
    //Disconnects the server from the client
    private static void disconnect() throws IOException {
        out.close();
        toConsole("Disconnected.");
        System.exit(0);
    }
    
    public static void  main(String[] args) {
    	try {
    		setup();
    		talk();
    	}
    	catch(IOException ioex) {
    		toConsole("Error: " + ioex);
    	}
    }
}
