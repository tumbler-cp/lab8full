package comm;

import java.io.Serializable;

public enum Signal implements Serializable {
    COMMAND,
    TEXT,
    LOGIN,
    ACCEPT,
    DECLINE,
    REG,
    TEMP_CONN,
    ALLOC,
    TABLE,
    CLOSING,
    EXIT
}
