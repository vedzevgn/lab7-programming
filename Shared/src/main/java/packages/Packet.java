package packages;

import java.io.Serializable;
import java.util.Arrays;

public class Packet implements Serializable {
    public static final int dataSize = 1024;
    public static final int packageSize = dataSize + 111;

    private final int serialNumber;
    private final int packagesAmount;

    private byte[] data = new byte[dataSize];

    public Packet(int serialNumber, int packagesAmount) {
        this.serialNumber = serialNumber;
        this.packagesAmount = packagesAmount;
    }

    public Packet(byte[] data, int serialNumber, int packagesAmount) {
        this.data = Arrays.copyOf(data, dataSize);
        this.serialNumber = serialNumber;
        this.packagesAmount = packagesAmount;
    }

    public byte[] getData() {
        return data;
    }

    public int getNumber() {
        return serialNumber;
    }

    public int getAmount() {
        return packagesAmount;
    }
}
