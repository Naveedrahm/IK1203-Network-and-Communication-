import tcpclient.TCPClient;
import java.net.*;
import java.io.*;
import java.time.*;
import java.time.format.*;

public class HTTPAsk {
    static int BUFFERSIZE = 1024;

    public static void main(String[] args) {
        try {
            ServerSocket takeInSocket = new ServerSocket(Integer.parseInt(args[0]));

            while (true) {
                Socket socket = takeInSocket.accept();
                OutputStream outputStream = socket.getOutputStream(); //************************************'' */

                // Client
                byte[] fromClientBuffer = new byte[BUFFERSIZE];
                int fromClientLength = socket.getInputStream().read(fromClientBuffer);
                // Response for client
                String clientMess = new String(fromClientBuffer, 0, fromClientLength);

                // Parsing
                String[] urLSeperate = clientMess.split("\\s+");

                if (urLSeperate[0].equals("GET") && urLSeperate.length >= 2) {
                    String uri = urLSeperate[1];
                    String[] urISeperate = uri.split("\\?");

                    if (urISeperate[0].equals("/ask") && urISeperate.length == 2) {
                        String parts = urISeperate[1];

                        String[] partSeperate = parts.split("\\&");
                        String hostname = null;
                        Integer port = 0;   /******************** */
                        byte[] webName = new byte[0];
                        Integer limit = null;   /*************** */
                        boolean shutD = false;
                        Integer timeO = null;   /********** */

                        for (String i : partSeperate) {
                            String[] finalString = i.split("\\=");
                            String iD = finalString[0];
                            String theMatch = finalString[1];

                            switch (iD) {
                                case "hostname":
                                    hostname = theMatch;
                                    break;
                                case "limit":
                                    limit = Integer.parseInt(theMatch);
                                    break;
                                case "port":
                                    port = Integer.parseInt(theMatch);
                                    break;
                                case "string":
                                    webName = theMatch.getBytes();
                                    break;
                                case "shutdown":
                                    shutD = theMatch.equals("true");
                                    break;
                                case "timeout":
                                    timeO = Integer.parseInt(theMatch);
                                    break;
                            }
                        }

                        String httpResponse;
                        try {
                            if (hostname == null || port == 0) {
                                httpResponse = "HTTP/1.1 400 Bad Request\r\n\r\n";
                                outputStream.write(httpResponse.getBytes());
                                throw new IllegalArgumentException();
                            

                            } else {
                                TCPClient tcpClient = new TCPClient(shutD, timeO, limit);
                                byte[] responseFromServer = tcpClient.askServer(hostname, port, webName);
    
                                httpResponse = "HTTP/1.1 200 OK\r\n" + 
                                "\r\n" + 
                                new String(responseFromServer);
                                outputStream.write(httpResponse.getBytes());
                            }
                        } catch (IllegalArgumentException e) {
                            httpResponse = "HTTP/1.1 400 Bad Request\r\n\r\n"; /*****************ändra till lämplig error */
                            outputStream.write(httpResponse.getBytes());
                        } catch (ConnectException e) {
                            httpResponse = "HTTP/1.1 404 Not Found\r\n\r\n"; /******** */
                            outputStream.write(httpResponse.getBytes());
                        }catch(SocketTimeoutException e) {
                            httpResponse = "HTTP/1.1 408 Request Timeout\r\n\r\n";
                            outputStream.write(httpResponse.getBytes());
                        }catch (IOException e) {
                            httpResponse = "HTTP/1.1 500 Internal Server Error\r\n\r\n";
                            outputStream.write(httpResponse.getBytes());
                        }
                    } else {
                        // If not "\ask" then do what error something
                        String httpResponse = "HTTP/1.1 404 Not Found\r\n\r\n"; /***** */
                        outputStream.write(httpResponse.getBytes());
                    }
                } else {
                    // If not "GET" then do something error.
                    String httpResponse = "HTTP/1.1 400 Bad Request\r\n\r\n"; /******** */
                    outputStream.write(httpResponse.getBytes());
                }

                socket.close();
            }
        } catch (IOException e) {
          e.toString();
        } catch (NumberFormatException e) {
             e.toString();
        } catch (ArrayIndexOutOfBoundsException e) {
            e.toString();
        }
    }
}