package downloader;

import downloader.listeners.FailureListener;
import downloader.listeners.ItemLoadedListener;
import downloader.listeners.RequestReadyListener;
import downloader.listeners.SuccessListener;
import utils.Entity;
import utils.EntitySet;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("WeakerAccess")
public class ComfortableDownloaderRequest<Item extends Entity<ItemKey>, ItemKey> implements ItemLoadedListener<Item, ItemKey> {

    private Set<ItemKey> mKeys;
    private Map<ItemKey, Throwable> mFailedKeys;

    private final EntitySet<Item, ItemKey> mItems;

    private final Set<SuccessListener<Item, ItemKey>> mSuccessCallbacks;
    private final Set<FailureListener<ItemKey>> mFailureCallbacks;

    private final RequestReadyListener<Item, ItemKey> mReadyListener;

    ComfortableDownloaderRequest(RequestReadyListener<Item, ItemKey> requestReadyListener, Collection<ItemKey> itemKeys) {
        this.mItems = new EntitySet<>();
        this.mKeys = new HashSet<>();
        this.mFailedKeys = new HashMap<>();
        this.mSuccessCallbacks = new HashSet<>();
        this.mFailureCallbacks = new HashSet<>();
        this.mReadyListener = requestReadyListener;

        for (ItemKey key : itemKeys) {
            if (key != null) mKeys.add(key);
        }
    }

    public EntitySet<Item, ItemKey> getItems() {
        return mItems;
    }

    public Set<ItemKey> getKeys() {
        return mKeys;
    }

    public void setKeys(Set<ItemKey> keys) {
        mKeys = keys;
    }

    public Map<ItemKey, Throwable> getFailedKeys() {
        return mFailedKeys;
    }

    public void setFailedKeys(Map<ItemKey, Throwable> failedKeys) {
        this.mFailedKeys = failedKeys;
    }

    public Set<SuccessListener<Item, ItemKey>> getSuccessCallbacks() {
        return mSuccessCallbacks;
    }

    public Set<FailureListener<ItemKey>> getFailureCallbacks() {
        return mFailureCallbacks;
    }

    public void addSuccessCallback(SuccessListener<Item, ItemKey> callback) {
        mSuccessCallbacks.add(callback);
    }

    public void addFailureCallback(FailureListener<ItemKey> callback) {
        mFailureCallbacks.add(callback);
    }

    public void setItems(Collection<Item> items) {
        mItems.clear();
        mItems.addAll(items);
    }

    boolean isAlreadyLoaded() {
        int readyItemsCount = mItems.size() + mFailedKeys.size();
        return readyItemsCount == mKeys.size();
    }

    public void clearCallbacks() {
        mFailureCallbacks.clear();
        mSuccessCallbacks.clear();
    }

    public boolean equals(Collection<ItemKey> keys) {
        if (mKeys.size() != keys.size()) return false;

        for (ItemKey key : keys) {
            if (!mKeys.contains(key)) return false;
        }

        return true;
    }

    @Override
    public void onItemLoaded(Item item) {
        if (mKeys.contains(item.getEntityKey())) {
            mItems.add(item);
            if (isAlreadyLoaded()) mReadyListener.onRequestLoaded(this);
        }
    }

    @Override
    public void onItemLoadFailed(Throwable reason, ItemKey itemKey) {
        if (mKeys.contains(itemKey)) {
            mFailedKeys.put(itemKey, reason);
            if (isAlreadyLoaded()) mReadyListener.onRequestLoaded(this);
        }
    }

}