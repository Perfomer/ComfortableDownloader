package downloader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import downloader.listeners.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utils.Entity;
import utils.Utils;

@SuppressWarnings({"WeakerAccess", "SameParameterValue"})
public abstract class CastingComfortableDownloader<InputItem extends Entity<ItemKey>, ItemKey, OutputItem extends Entity<ItemKey>>
        implements RequestReadyListener<OutputItem, ItemKey>, ItemLoadedListener<OutputItem, ItemKey> {

    private final List<OutputItem> mItems;
    private final Set<ItemKey> mLoadingItemsKeys;
    private final Set<ComfortableDownloaderRequest<OutputItem, ItemKey>> mRequests;

    protected CastingComfortableDownloader() {
        super();
        mItems = new ArrayList<>();
        mRequests = new HashSet<>();
        mLoadingItemsKeys = new HashSet<>();
    }

    @SafeVarargs
    public final void query(SuccessListener<OutputItem, ItemKey> callback, ItemKey... itemsKeys) {
        query(callback, null, itemsKeys);
    }

    @SafeVarargs
    public final void query(SuccessListener<OutputItem, ItemKey> successCallback, FailureListener<ItemKey> failureCallback, ItemKey... itemsKeys) {
        query(successCallback, failureCallback, Arrays.asList(itemsKeys));
    }

    public final void query(SuccessListener<OutputItem, ItemKey> successCallback, Collection<ItemKey> itemsKeys) {
        query(successCallback, null, itemsKeys);
    }

    public final void query(SuccessListener<OutputItem, ItemKey> successCallback, FailureListener<ItemKey> failureCallback, Collection<ItemKey> itemsKeys) {
        ComfortableDownloaderRequest<OutputItem, ItemKey> request = getRequest(itemsKeys);

        request.addSuccessCallback(successCallback);
        request.addFailureCallback(failureCallback);
        fillRequest(request);

        if (!request.isAlreadyLoaded()) {
            loadAll(filter(itemsKeys));
        }
    }

    @SafeVarargs
    public final void refresh(SuccessListener<OutputItem, ItemKey> successCallback, FailureListener<ItemKey> failureCallback, ItemKey... itemsKeys) {
        List<ItemKey> keys = Arrays.asList(itemsKeys);
        refresh(successCallback, failureCallback, keys);
    }

    @SafeVarargs
    public final void refresh(SuccessListener<OutputItem, ItemKey> successCallback, ItemKey... itemsKeys) {
        refresh(successCallback, null, itemsKeys);
    }

    public final void refresh(SuccessListener<OutputItem, ItemKey> successCallback, Collection<ItemKey> itemsKeys) {
        refresh(successCallback, null, itemsKeys);
    }

    public final void refresh(SuccessListener<OutputItem, ItemKey> successCallback, FailureListener<ItemKey> failureCallback, Collection<ItemKey> itemsKeys) {
        removeAll(itemsKeys);
        query(successCallback, failureCallback, itemsKeys);
    }

    private void removeAll(Iterable<ItemKey> itemsKeys) {
        for (ItemKey key : itemsKeys) {
            OutputItem item = Utils.find(mItems, key);
            if (item != null) mItems.remove(item);
        }
    }

    private void loadAll(Iterable<ItemKey> itemsKeys) {
        for (ItemKey key : itemsKeys) {
            loadOne(key, this);
        }
    }

    private void loadOne(ItemKey itemKey, ItemLoadedListener<OutputItem, ItemKey> listener) {
        mLoadingItemsKeys.add(itemKey);

        Callback<InputItem> callback = getResponseCallback(itemKey, listener);
        call(itemKey).enqueue(callback);
    }

    private ComfortableDownloaderRequest<OutputItem, ItemKey> getRequest(Collection<ItemKey> itemKeys) {
        for (ComfortableDownloaderRequest<OutputItem, ItemKey> request : mRequests) {
            if (request.equals(itemKeys)) return request;
        }

        ComfortableDownloaderRequest<OutputItem, ItemKey> request = new ComfortableDownloaderRequest<>(this, itemKeys);
        mRequests.add(request);

        return request;
    }

    @Override
    public void onRequestLoaded(ComfortableDownloaderRequest<OutputItem, ItemKey> request) {
        for (SuccessListener<OutputItem, ItemKey> callback : request.getSuccessCallbacks()) {
            if (callback != null) callback.onLoaded(request.getItems());
        }

        for (FailureListener<ItemKey> callback : request.getFailureCallbacks()) {
            if (callback != null) callback.onFailed(request.getFailedKeys());
        }

        request.clearCallbacks();
        mRequests.remove(request);
    }

    @Override
    public void onItemLoaded(OutputItem outputItem) {
        if (outputItem != null) {
            mLoadingItemsKeys.remove(outputItem.getEntityKey());
            mItems.add(outputItem);

            for (ItemLoadedListener<OutputItem, ItemKey> listener : mRequests) {
                listener.onItemLoaded(outputItem);
            }
        }
    }

    @Override
    public void onItemLoadFailed(Throwable reason, ItemKey itemKey) {
        if (itemKey != null) {
            mLoadingItemsKeys.remove(itemKey);

            for (ItemLoadedListener<OutputItem, ItemKey> listener : mRequests) {
                listener.onItemLoadFailed(reason, itemKey);
            }
        }
    }

    private void fillRequest(ItemLoadedListener<OutputItem, ItemKey> listener) {
        if (listener != null) {
            for (OutputItem item : mItems) {
                listener.onItemLoaded(item);
            }
        }
    }

    // Removes null, repeating, loading and loaded items from source
    private Iterable<ItemKey> filter(Iterable<ItemKey> source) {
        Collection<ItemKey> result = new HashSet<>();

        for (ItemKey key : source) {
            if (key != null && !Utils.contains(mItems, key) && !mLoadingItemsKeys.contains(key) && !result.contains(key)) {
                result.add(key);
            }
        }

        return result;
    }

    private Callback<InputItem> getResponseCallback(final ItemKey itemKey, final ItemLoadedListener<OutputItem, ItemKey> listener) {
        return new Callback<InputItem>() {
            @Override
            public void onResponse(Call<InputItem> call, Response<InputItem> response) {
                InputItem responseModel = response.body();
                if (responseModel != null) {
                    OutputItem outputItem = cast(responseModel);
                    listener.onItemLoaded(outputItem);
                    save(outputItem);
                } else {
                    onError();
                }
            }

            @Override
            public void onFailure(Call<InputItem> call, Throwable t) {
                onError();
            }

            private void onError() {
                try {
                    OutputItem localItem = read(itemKey);

                    if (localItem != null) listener.onItemLoaded(localItem);
                    else throw new Exception("Can't read item: " + itemKey.toString());
                } catch (Exception ex) {
                    listener.onItemLoadFailed(ex, itemKey);
                }
            }
        };
    }

    private Collection<OutputItem> convert(Iterable<InputItem> items) {
        Collection<OutputItem> result = new ArrayList<>();

        for (InputItem item : items) {
            result.add(cast(item));
        }

        return result;
    }

    protected abstract void save(OutputItem item);

    protected abstract OutputItem read(ItemKey itemKey);

    protected abstract Call<InputItem> call(ItemKey itemKey);

    protected abstract OutputItem cast(InputItem item);

}