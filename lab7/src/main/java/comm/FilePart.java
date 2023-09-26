package comm;

import java.io.Serializable;

public record FilePart(PLACE place, int index, String filename, String line) implements Serializable {
    public enum PLACE {
        START,
        MID,
        END;
    }
}

