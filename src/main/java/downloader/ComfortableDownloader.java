package downloader;

import utils.Entity;

public abstract class ComfortableDownloader<InputItem extends Entity<ItemKey>, ItemKey>
        extends CastingComfortableDownloader<InputItem, ItemKey, InputItem> {

    @Override
    protected InputItem cast(InputItem inputItem) {
        return inputItem;
    }

}