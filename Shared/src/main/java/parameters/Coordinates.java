package parameters;

import java.io.Serializable;

/**
 * Contains numeric parameters of MusicBand's coordinates
 */

public class Coordinates implements Serializable {
    private static final long serialVersionUID = 2906642554793891381L;
    private Double x; //Максимальное значение поля: 229, Поле не может быть null
    private Float y; //Поле не может быть null

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Float getY() {
        return y;
    }

    public void setY(Float y) {
        this.y = y;
    }
}
