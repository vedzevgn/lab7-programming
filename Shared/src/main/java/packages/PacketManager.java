package packages;

import java.io.*;
import java.util.Arrays;

import static java.lang.Math.*;

public class PacketManager {

    public static Packet[] splitObject(Serializable object) {
        Packet[] packets;
        try {
            ByteArrayOutputStream byteOS = new ByteArrayOutputStream();
            ObjectOutputStream objOS = new ObjectOutputStream(byteOS);
            objOS.writeObject(object);
            byte[] data = byteOS.toByteArray();

            int packagesAmount = (int) ceil((double) data.length / (double) Packet.dataSize);
            byte[] dataToSend = Arrays.copyOf(data, Packet.dataSize * packagesAmount);

            packets = new Packet[packagesAmount];

            for(int i = 0; i < packagesAmount; i++) {
                packets[i] = new Packet(Arrays.copyOfRange(dataToSend, Packet.dataSize * i, Packet.dataSize * (i + 1)), i, packagesAmount);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return packets;
    }

    public static byte[] serialize(Serializable packet) {
        byte[] dataToSend;
        try {
            ByteArrayOutputStream byteOS = new ByteArrayOutputStream();
            ObjectOutputStream objOS = new ObjectOutputStream(byteOS);
            objOS.writeObject(packet);
            dataToSend = byteOS.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return dataToSend;
    }

    public static Serializable deserialize(byte[] byteObject) {
        Serializable out;
        try {
            ByteArrayInputStream byteOS = new ByteArrayInputStream(byteObject);
            ObjectInputStream objIS = new ObjectInputStream(byteOS);
            out = (Serializable) objIS.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return out;
    }

    public static Serializable assemble(Packet[] packets) {
        byte[] byteObject = new byte[Packet.dataSize * packets.length];

        for(int i = 0; i < packets.length; i++) {
            byte[] packetData = packets[i].getData();

            for(int j = 0; j < Packet.dataSize; j++) {
                byteObject[i * Packet.dataSize + j] = packetData[j];
            }
        }

        return deserialize(byteObject);
    }
}