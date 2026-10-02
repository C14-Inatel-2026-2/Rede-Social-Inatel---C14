package Sockets;

import java.net.Socket;

public class MockSocket extends Socket {

    public boolean fechado = true;
    public boolean desconectado = true;

    @Override
    public boolean isClosed()
    {
        return fechado ;
    }
    @Override
    public boolean isConnected()
    {return desconectado;}
}
