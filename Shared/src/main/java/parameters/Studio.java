package parameters;

import java.io.Serializable;

/**
 * Contains name of MusicBand's studio's
 */

public class Studio implements Serializable {
    private static final long serialVersionUID = 2906642554793891381L;
    private String name; //Поле может быть null

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
