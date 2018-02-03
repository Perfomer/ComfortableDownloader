package downloader.listeners;

import utils.Entity;

import java.util.List;

@FunctionalInterface
public interface SuccessListener<Item extends Entity<ItemKey>, ItemKey> {

    void onLoaded(List<Item> items);

}