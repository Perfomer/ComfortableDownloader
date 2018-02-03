package downloader.listeners;

import com.dmdevelopment.inworkspace.android.util.interfaces.Entity;
import utils.Entity;

public interface ItemLoadedListener<Item extends Entity<ItemKey>, ItemKey> {

    void onItemLoaded(Item item);

    void onItemLoadFailed(Throwable reason, ItemKey itemKey);

}