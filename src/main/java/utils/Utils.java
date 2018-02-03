package utils;

import java.util.Collection;

public class Utils {

    public static <Item extends Entity<ItemKey>, ItemKey> Item find(Collection<Item> items, ItemKey key) {
        for (Item item : items) {
            if (item.getEntityKey().equals(key)) return item;
        }

        return null;
    }

    public static <Item extends Entity<ItemKey>, ItemKey> boolean contains(Collection<Item> items, ItemKey key) {
        return find(items, key) != null;
    }

}