package wordle;


import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class Client {
    private Socket socket = null;
    private DataOutputStream output = null;
    private BufferedReader fileReader = null;
    private DataInputStream serverIn = null;
    private boolean DEBUG;

    /**
     * Constructor for the Client Class
     */
    public Client(boolean debug) {
        this.DEBUG = debug;
    }

    /**
     * Sends data to the server
     * @param fileName Name of the file the data corresponds to
     * @param data Data being sent to the server
     * @author Carson Meredith
     */
    public void send(String fileName, String data) {
        openConnection();
        if(DEBUG) System.out.println("Opening connection to server");
        try {
            output.writeUTF("Send " + fileName);
            if(DEBUG) System.out.println("Sending data from " + fileName);
            output.writeUTF(data);
            output.writeUTF("End of File " + fileName);
        } catch (IOException e) {
            System.out.println("There was a problem sending data from " + fileName);
        }
        endConnection();
        if(DEBUG) System.out.println("Closing connection to server");
    }

    /**
     * Receives data from the server
     * @param fileName file being written to
     * @author Carson Meredith
     */
    public void receive(String fileName) {
        openConnection();
        if(DEBUG) System.out.println("Opening connection to server");
        try {
            output.writeUTF("Request " + fileName);
            if(DEBUG) System.out.println("Requesting " + fileName);
        } catch (IOException e) {
            System.out.println("Problem receiving data from server");
        }
        String line = "";
        String text = "";
        if(DEBUG) System.out.println("Receiving data from " + fileName);
        while(!line.equals("End of File " + fileName)) {
            try {
                line = serverIn.readUTF();
                if(!line.equals("End of File " + fileName)) {
                    text += line + "\n";
                }
            } catch (IOException e) {
                System.out.println("Error receiving data from server");
            }
        }
        if(DEBUG) System.out.println("Writing data from " + fileName);
        try {
            Files.write(Paths.get("src/Resources/UserData/" + fileName),text.getBytes());
        } catch(IOException e) {
            System.out.println("There was a problem writing to " + fileName);
        }
        endConnection();
    }

    private void postRequest() {
        HttpURLConnection http = null;
        try {
            URL url = new URL("https://www.example.com/login");
            URLConnection con = url.openConnection();
            http = (HttpURLConnection)con;
            http.setRequestMethod("POST"); // PUT is another valid option
            http.setDoOutput(true);
            Map<String,String> arguments = new HashMap<String, String>() {{
                put("User", "paginik");
                put("Game_Number", "69");
                put("Target","EARTH");
                put("Number_of_guesses","4");
                put("Win","Yes");
                put("Guesses","BOOBY EARTH");
            }};
            StringJoiner sj = new StringJoiner("&");
            try {
                for(Map.Entry<String,String> entry : arguments.entrySet())
                    sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "="
                            + URLEncoder.encode(entry.getValue(), "UTF-8"));
            }catch (UnsupportedEncodingException e) {

            }
            byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
            int length = out.length;

            http.setFixedLengthStreamingMode(length);
            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            http.connect();
            try(OutputStream os = http.getOutputStream()) {
                os.write(out);
            }
        } catch (IOException e) {
            System.out.println(e);
        }

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
                socket = new Socket("10.205.168.164",5000);
                //socket = new Socket("192.168.0.135",5000);
                output = new DataOutputStream(socket.getOutputStream());
                serverIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            } catch (IOException e) {
                System.out.println("There was a problem connecting");
            }
    }
}