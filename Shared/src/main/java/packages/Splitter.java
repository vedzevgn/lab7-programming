package packages;

import java.util.Arrays;
import java.util.concurrent.RecursiveTask;

import static java.lang.Math.ceil;

public class Splitter extends RecursiveTask<Packet[]> {

    private byte[] toSend;

    public Splitter(byte[] inputArray) {
        toSend = inputArray;
    }

    @Override
    protected Packet[] compute() {
        Packet[] packets;
        int packagesAmount = (int) ceil((double) toSend.length / (double) Packet.dataSize);
        byte[] dataToSend = Arrays.copyOf(toSend, Packet.dataSize * packagesAmount);

        packets = new Packet[packagesAmount];
        for (int i = 0; i < packagesAmount; i++) {
            packets[i] = new Packet(
                    Arrays.copyOfRange(dataToSend, Packet.dataSize * i, Packet.dataSize * (i + 1)), i, packagesAmount);
        }
        return packets;
    }

    public byte[] getToSend() {
        return toSend;
    }
}
