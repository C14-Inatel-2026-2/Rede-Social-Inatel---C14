package servidor;

import br.inatel.redesocial.servidor.ServidorFactoryImp;
import br.inatel.redesocial.servidor.ServidorHandler;
import br.inatel.redesocial.servidor.ServidorSocket;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TestServidorFactoryImp {

    private final ServidorFactoryImp factory = new ServidorFactoryImp();

    @Test
    void criaBufferedReaderParaSocket() throws Exception {
        Socket socket = mock(Socket.class);
        InputStream inputStream = mock(InputStream.class);
        when(socket.getInputStream()).thenReturn(inputStream);
        assertNotNull(factory.criarBufferReader(socket));
        verify(socket).getInputStream();
    }

    @Test
    void propagaIOExceptionAoCriarBufferedReader() throws Exception {
        Socket socket = mock(Socket.class);
        when(socket.getInputStream()).thenThrow(new IOException("falha"));
        assertThrows(IOException.class, () -> factory.criarBufferReader(socket));
    }

    @Test
    void criaPrintWriterParaSocket() throws Exception {
        Socket socket = mock(Socket.class);
        OutputStream outputStream = mock(OutputStream.class);
        when(socket.getOutputStream()).thenReturn(outputStream);
        PrintWriter writer = factory.criarPrintWriter(socket);
        assertNotNull(writer);
        verify(socket).getOutputStream();
    }

    @Test
    void propagaIOExceptionAoCriarPrintWriter() throws Exception {
        Socket socket = mock(Socket.class);
        when(socket.getOutputStream()).thenThrow(new IOException("falha"));
        assertThrows(IOException.class, () -> factory.criarPrintWriter(socket));
    }

    @Test
    void criaServidorSocketComSocketEFactory() throws Exception {
        Socket socket = mock(Socket.class);
        when(socket.getInputStream()).thenReturn(mock(InputStream.class));
        when(socket.getOutputStream()).thenReturn(mock(OutputStream.class));
        ServidorSocket servidorSocket = factory.criarServidorSocket(socket, factory);
        assertNotNull(servidorSocket);
        assertNotNull(servidorSocket.getEscritor());
        verify(socket).getInputStream();
        verify(socket).getOutputStream();
    }

    @Test
    void criaServidorHandler() {
        ServidorHandler handler = factory.criarServidorHandler(
                mock(Socket.class), new HashSet<>(), factory
        );
        assertInstanceOf(ServidorHandler.class, handler);
    }
}
