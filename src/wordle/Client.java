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

    public Client() {
    }

    public void send() {
        openConnection();
        try {
            File file = new File("src/Resources/UserData/Scoreboard");
            fileReader = new BufferedReader(new FileReader(file));
            output.writeUTF("Send");
            String line = fileReader.readLine();
            while(line!= null) {
                try {
                    output.writeUTF(line);
                    line = fileReader.readLine();

                } catch (IOException e) {
                    System.out.println(e);
                }
            }
            output.writeUTF("End of File");
        } catch (IOException e) {
            System.out.println("There was a problem reading the file");
        }
        endConnection();
    }

    public void receive() {
        openConnection();
        try {
            output.writeUTF("Request");
        } catch (IOException e) {
            System.out.println("Problem receiving data from server");
        }
        String line = "";
        String text = "";
        while(!line.equals("End of File")) {
            try {
                line = serverIn.readUTF();
                if(!line.equals("End of File")) {
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