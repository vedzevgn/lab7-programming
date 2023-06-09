package connection;

import packages.Packet;
import packages.PacketManager;
import packages.Splitter;
import responces.Response;

import java.io.*;
import java.net.*;
import java.util.concurrent.ForkJoinPool;

public class DatagramConnection implements Connection {

    ForkJoinPool forkJoinPool = new ForkJoinPool();
    public static final int STANDARD_PORT = 1616;

    public static final int packageSize = Packet.packageSize;

    private InetAddress host;
    private Integer port;

    private InetAddress clientHost = null;
    private Integer clientPort = null;

    private boolean isListeningPort;

    private final DatagramSocket socket;
    //private static final Logger logger = LoggerFactory.getLogger(DatagramConnection.class);

    public DatagramConnection() throws SocketException, UnknownHostException {
        this(false);
    }

    public DatagramConnection(boolean isListeningPort) throws SocketException, UnknownHostException {
        this(STANDARD_PORT, isListeningPort);
    }

    public DatagramConnection(int port) throws SocketException, UnknownHostException {
        this(port, false);
    }

    public DatagramConnection(int port, boolean isListeningPort) throws SocketException, UnknownHostException {
        this("localhost", port, isListeningPort);
    }

    public DatagramConnection(String host, int port) throws SocketException, UnknownHostException {
        this(host, port, false);
    }

    public DatagramConnection(String host, int port, boolean isListeningPort) throws SocketException, UnknownHostException {
        try {
            this.host = InetAddress.getByName(host);
            this.port = port;
            this.isListeningPort = isListeningPort;

            if (isListeningPort) {
                socket = new DatagramSocket(port);
            } else
                socket = new DatagramSocket();
        } catch (SocketException e) {
            throw new SocketException();
        } catch (UnknownHostException e) {
            throw new UnknownHostException();
        }
    }

    public void setPort(Integer port){
        this.clientPort = port;
    }

    public Integer getPort(){
        return usesPort;
    }

    @Override
    public void send(Serializable object) {
        try {
            if (clientPort != null || clientHost != null) {
                Splitter splitter = new Splitter(PacketManager.objectToBytes(object));
                ForkJoinPool forkJoinPool = new ForkJoinPool();
                Packet[] packets = forkJoinPool.invoke(splitter);

                for (Packet packet : packets) {
                    DatagramPacket datagramPacket = new DatagramPacket(PacketManager.serialize(packet), packageSize, clientHost, clientPort);
                    socket.send(datagramPacket);
                    //System.out.println("отправлено(сокет)");
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    Integer usesPort;
    @Override
    public Serializable receive() {
        Serializable object;
        try {
            Packet[] packets = new Packet[0];
            byte[] bytes = new byte[packageSize];

            int currentPacket = 0;
            int packagesCount = 1;
            do {
                DatagramPacket datagramPacket = new DatagramPacket(bytes, packageSize);
                socket.receive(datagramPacket);
                InetAddress packetHost = datagramPacket.getAddress();
                Integer packetPort = datagramPacket.getPort();
                usesPort = datagramPacket.getPort();
                Packet packet = (Packet) PacketManager.deserialize(bytes);
                if (currentPacket == 0) {
                    packagesCount = packet.getAmount();
                    packets = new Packet[packagesCount];
                        clientHost = packetHost;
                        clientPort = packetPort;
                }
                packets[currentPacket] = packet;
                //System.out.println("оп");
            } while (++currentPacket != packagesCount);
            object = PacketManager.assemble(packets);

        } catch (IOException io) {
            throw new RuntimeException(io);
        }
        System.out.println(object);
        return object;
    }
}