package wordle;


import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Client {
    private Socket socket = null;
    private DataOutputStream output = null;
    private BufferedReader fileReader = null;
    private DataInputStream serverIn = null;

    /**
     * Constructor for the Client Class
     */
    public Client() {
    }

    /**
     * Sends data to the server
     * @author Carson Meredith
     */
    public void send() {
        openConnection();
        try {
            output.writeUTF("Send");
            File file = new File("src/Resources/UserData/Scoreboard");
            fileReader = new BufferedReader(new FileReader(file));
            String line = fileReader.readLine();
            while(line!= null) {
                try {
                    output.writeUTF(line);
                    line = fileReader.readLine();

                } catch (IOException e) {
                    System.out.println(e);
                }
            }
            output.writeUTF("End of File 1");
            fileReader.close();
            file = new File("src/Resources/UserData/GlobalData");
            fileReader = new BufferedReader(new FileReader(file));
            line = fileReader.readLine();
            while(line!= null) {
                try {
                    output.writeUTF(line);
                    line = fileReader.readLine();

                } catch (IOException e) {
                    System.out.println(e);
                }
            }
            output.writeUTF("End of File 2");
        } catch (IOException e) {
            System.out.println("There was a problem reading the file");
        }
        endConnection();
    }

    /**
     * Receives data from the server
     * @author Carson Meredith
     */
    public void receive() {
        openConnection();
        try {
            output.writeUTF("Request");
        } catch (IOException e) {
            System.out.println("Problem receiving data from server");
        }
        String line = "";
        String text = "";
        while(!line.equals("End of File 1")) {
            try {
                line = serverIn.readUTF();
                if(!line.equals("End of File 1")) {
                    text += line + "\n";
                }
            } catch (IOException e) {
                System.out.println("Error receiving data from server");
            }
        }
        try {
            Files.write(Paths.get("src/Resources/UserData/Scoreboard"),text.getBytes());
        } catch(IOException e) {
            System.out.println("aloha");
        }
        text = "";
        while(!line.equals("End of File 2")) {
            try {
                line = serverIn.readUTF();
                if(!line.equals("End of File 2")) {
                    text += line + "\n";
                }
            } catch (IOException e) {
                System.out.println("Error receiving data from server");
            }
        }
        try {
            Files.write(Paths.get("src/Resources/UserData/GlobalData"),text.getBytes());
        } catch(IOException e) {
            System.out.println("aloha");
        }
        endConnection();
    }

    private void endConnection() {
        try {
            socket.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private void openConnection() {
            try {
                socket = new Socket("192.168.0.135",5000);
                output = new DataOutputStream(socket.getOutputStream());
                serverIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            } catch (IOException e) {
                System.out.println("There was a problem connecting");
            }
    }
}