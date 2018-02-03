package downloader.listeners;

import downloader.ComfortableDownloaderRequest;
import utils.Entity;

public interface RequestReadyListener<Item extends Entity<ItemKey>, ItemKey> {

    void onRequestLoaded(ComfortableDownloaderRequest<Item, ItemKey> request);

}