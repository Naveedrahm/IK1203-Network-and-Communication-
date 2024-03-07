package tcpclient;
import java.io.*;
import java.net.*;


public class TCPClient {

    boolean shutdown;
    Integer timeout;
    Integer limit;

    public TCPClient(boolean shutdown, Integer timeout, Integer limit) {
        this.limit = limit;
        this.shutdown = shutdown;
        this.timeout = timeout;
    }

    public byte[] askServer(String hostname, int port, byte [] toServerBytes) throws IOException {
        // New socket. 
        Socket SocketCLient = new Socket(hostname, port);

        //fixed length for the response
        byte[] FixedServerBuffer = new byte[1024];
        // Utmatning
        ByteArrayOutputStream NewDynamicRCVBuffer = new ByteArrayOutputStream();

        
        // The socket server needs to get in touch with
        SocketCLient.getOutputStream().write(toServerBytes);

        // Get the input stream from socket
        InputStream inputStream = SocketCLient.getInputStream();


        //Input length 
        int lengthOfUserBuffer;
        int TotBytesRCV = 0;

		if (this.shutdown) {
			SocketCLient.shutdownOutput();
		}
		if(this.timeout != null) {
			SocketCLient.setSoTimeout(this.timeout);
		}

		// int limit = (this.limit == null) ? 0 : this.limit;

        try{
            while((lengthOfUserBuffer = inputStream.read(FixedServerBuffer)) != -1){
                // NewDynamicRCVBuffer.write(FixedServerBuffer, 0, lengthOfUserBuffer);
                // int countLeng;
                if (limit != null && TotBytesRCV + lengthOfUserBuffer > limit) {
                    int remainingBytes = limit - TotBytesRCV;
                    NewDynamicRCVBuffer.write(FixedServerBuffer, 0, remainingBytes);
                    break; // Exit the loop since the limit is reached
                }
                
                NewDynamicRCVBuffer.write(FixedServerBuffer, 0, lengthOfUserBuffer);
    
                // Update the total bytes received
                TotBytesRCV += lengthOfUserBuffer;
            }
        }catch(Exception e){
            throw new ConnectException(e.getMessage());
        }
        finally{
            SocketCLient.close();
        }
        
        SocketCLient.close();
        return NewDynamicRCVBuffer.toByteArray();
    }
}