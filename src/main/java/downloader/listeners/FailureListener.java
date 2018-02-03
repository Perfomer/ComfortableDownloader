package downloader.listeners;

import java.util.Map;

@FunctionalInterface
public interface FailureListener<ItemKey> {

    void onFailed(Map<ItemKey, Throwable> failedItems);

}