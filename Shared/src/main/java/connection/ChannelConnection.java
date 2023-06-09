package connection;

import packages.Packet;
import packages.PacketManager;

import java.io.IOException;
import java.io.Serializable;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Arrays;

public class ChannelConnection implements Connection{
    private static final long CONNECTION_TIMEOUT = 10000;

    public static final int STANDARD_PORT = 1616;

    public static final int PACKAGE_SIZE = Packet.packageSize;

    //    private String host;
    private InetAddress host;
    private int port;

    private final DatagramChannel datagramChannel;

    public ChannelConnection(String host, int port) throws UnknownHostException {
        this.host = InetAddress.getByName(host);
        this.port = port;
        try {
            datagramChannel = DatagramChannel.open();
            datagramChannel.configureBlocking(false);
            bindChannel(null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void bindChannel(SocketAddress local) throws IOException {
        datagramChannel.bind(local);
    }

    @Override
    public void send(Serializable obj) {
        try {
            Packet[] packets = PacketManager.splitObject(obj);
            ByteBuffer byteBuffer;

            SocketAddress address = new InetSocketAddress(host, port);

            for(int i = 0; i < packets.length; i++) {
                byteBuffer = ByteBuffer.wrap(PacketManager.serialize(packets[i]));
                datagramChannel.send(byteBuffer, address);
                //System.out.println("отправлено(канал)");
            }
        } catch (IOException io) {
            io.printStackTrace();
        }

    }

    Integer ATTEMPTS_COUNT = 3;

    public Serializable receive() {
        ATTEMPTS_COUNT--;
        long timeoutChecker = System.currentTimeMillis() + CONNECTION_TIMEOUT;
        Serializable object;
        ByteBuffer emptyByteBuffer = ByteBuffer.allocate(PACKAGE_SIZE);
        try {
            Packet[] packets = new Packet[0];
            ByteBuffer byteBuffer = ByteBuffer.allocate(PACKAGE_SIZE);

            int counter = 0;
            int packagesAmount = 1;
            do {
                datagramChannel.receive(byteBuffer);
                if (byteBuffer.compareTo(emptyByteBuffer) == 0) {
                    continue;
                }
                byteBuffer = byteBuffer.flip();

                Packet packet = (Packet) PacketManager.deserialize(byteBuffer.array());
                if(counter == 0) {
                    packagesAmount = packet.getAmount();
                    packets = new Packet[packagesAmount];
                }
                packets[counter] = packet;
                counter++;
            } while (counter != packagesAmount && (timeoutChecker - System.currentTimeMillis()) >= 0);
            if (!Arrays.equals(packets, new Packet[0])) {
                object = PacketManager.assemble(packets);
            } else {
                throw new IOException("Таймаут ответа от сервера.");
            }
        } catch (IOException io) {
            System.out.println(io.getMessage());
            return null;
        }
        return object;
    }
}