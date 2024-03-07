package tcpclient;
import java.net.*;
import java.io.*;

public class TCPClient {
    
    public TCPClient(){
    }

    public byte[] askServer(String hostname, int port, byte[] toServerBytes) throws IOException {

        // New socket. 
        Socket SocketCLient = new Socket(hostname, port);

        // Utmatning
        ByteArrayOutputStream NewDynamicRCVBuffer = new ByteArrayOutputStream();
        
        // The socket server needs to get in touch with
        SocketCLient.getOutputStream().write(toServerBytes);

        // Get the input stream from socket
        InputStream inputStream = SocketCLient.getInputStream();

        //fixed length for the response
        byte[] FixedServerBuffer = new byte[1024];

        //Input length 
        int lengthOfUserBuffer;

        while((lengthOfUserBuffer = inputStream.read(FixedServerBuffer)) != -1){
            NewDynamicRCVBuffer.write(FixedServerBuffer, 0, lengthOfUserBuffer);
        }
        SocketCLient.close();

        return NewDynamicRCVBuffer.toByteArray();
    }
}
