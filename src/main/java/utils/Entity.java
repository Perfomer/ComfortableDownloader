package utils;

public interface Entity<Key> {

    Key getEntityKey();

    void setEntityKey(Key key);

}