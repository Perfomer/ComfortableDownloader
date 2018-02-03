package utils;

import java.util.*;

public class EntitySet<Value extends Entity<Key>, Key> implements List<Value> {

    private ArrayList<Value> mList;
    private HashSet<Key> mKeys;

    public EntitySet() {
        mList = new ArrayList<>();
        mKeys = new HashSet<>();
    }

    public EntitySet(Collection<Value> collection) {
        mList = new ArrayList<>(collection);
        mKeys = new HashSet<>();

        for (Value value : mList) {
            mKeys.add(value.getEntityKey());
        }
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return mList.containsAll(collection);
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        boolean result = false;

        Iterator<Value> iterator = mList.iterator();
        while (iterator.hasNext()) {
            Value item = iterator.next();

            if (!collection.contains(item)) {
                result = true;

                mList.remove(item);
                mKeys.remove(item.getEntityKey());
            }
        }

        return result;
    }

    @Override
    public void clear() {
        mList.clear();
        mKeys.clear();
    }

    public void update(Value item) {
        Key id = item.getEntityKey();
        int position = getPositionByKey(id);

        if (position != -1) {
            mList.remove(getByKey(id));
            mList.add(position, item);
        } else {
            mList.add(item);
        }
    }

    public void replace(Value value) {
        int pos = getPositionByKey(value.getEntityKey());

        if (pos != -1) {
            remove(pos);
            add(pos, value);
        }
    }

    @Override
    public Value set(int index, Value item) {
        Value result = null;

        if (item != null && index < mList.size()) {
            Key key = item.getEntityKey();

            if (!mKeys.contains(key)) {
                mKeys.add(key);
                result = mList.set(index, item);
            }
        }

        return result;
    }

    @Override
    public boolean addAll(int i, Collection<? extends Value> collection) {
        if (i >= collection.size()) return false;

        boolean result = false;

        Iterator<? extends Value> iterator = collection.iterator();
        int position = 0;

        while (iterator.hasNext()) {
            Value item = iterator.next();
            position++;

            if (position >= i) {
                result = true;
                add(item);
            }
        }

        return result;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean addAll(Collection<? extends Value> collection) {
        boolean result = false;

        if (collection != null) {
            for (Value item : collection) {
                result = true;
                add(item);
            }
        }

        return result;
    }

    @Override
    public boolean add(Value item) {
        if (item != null) {
            Key key = item.getEntityKey();

            if (!mKeys.contains(key)) {
                mKeys.add(key);
                mList.add(item);
                return true;
            }
        }

        return false;
    }

    @Override
    public void add(int index, Value item) {
        if (item != null && index <= mList.size()) {
            Key key = item.getEntityKey();

            if (!mKeys.contains(key)) {
                mKeys.add(key);
                mList.add(index, item);
            }
        }
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        boolean result = false;

        Iterator<Value> iterator = mList.iterator();
        while (iterator.hasNext()) {
            Value item = iterator.next();

            if (collection.contains(item)) {
                result = true;

                mList.remove(item);
                mKeys.remove(item.getEntityKey());
            }
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean remove(Object o) {
        Key entityKey = null;

        if (o != null) {
            Value item = (Value) o;
            entityKey = item.getEntityKey();
        }

        return removeByKey(entityKey);
    }

    @Override
    public Value remove(int i) {
        Value item = mList.get(i);

        mKeys.remove(item.getEntityKey());
        return mList.remove(i);
    }

    public boolean removeByKey(Key key) {
        Value item = getByKey(key);

        if (item != null) {
            mList.remove(item);
            mKeys.remove(key);
            return true;
        }

        return false;
    }

    @Override
    public Value get(int index) {
        return mList.get(index);
    }

    public Value getByKey(Key key) {
        for (Value item : mList) {
            Key entityKey = item.getEntityKey();

            if (entityKey == key || entityKey != null && entityKey.equals(key)) {
                return item;
            }
        }

        return null;
    }

    public int getPositionByKey(Key key) {
        for (int i = 0; i < mKeys.size(); i++) {
            Value item = mList.get(i);
            Key entityKey = item.getEntityKey();

            if (entityKey == key || entityKey != null && entityKey.equals(key)) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public int indexOf(Object o) {
        return mList.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return mList.lastIndexOf(o);
    }


    @Override
    public ListIterator<Value> listIterator() {
        return mList.listIterator();
    }


    @Override
    public ListIterator<Value> listIterator(int i) {
        return mList.listIterator(i);
    }


    @Override
    public List<Value> subList(int i, int i1) {
        return mList.subList(i, i1);
    }

    @Override
    public int size() {
        return mList.size();
    }

    @Override
    public boolean isEmpty() {
        return mList.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        //noinspection unchecked
        Value value = (Value) o;
        Key valueKey = value.getEntityKey();

        for (Key key : mKeys) {
            if (valueKey == key || (key != null && key.equals(valueKey))) return true;
        }

        return false;
    }

    public boolean containsKey(Key key) {
        return mKeys.contains(key);
    }

    public boolean hasNull() {
        return mKeys.contains(null);
    }


    @Override
    public Iterator<Value> iterator() {
        return mList.iterator();
    }


    @Override
    public Object[] toArray() {
        return mList.toArray();
    }


    @Override
    public <T1> T1[] toArray(T1[] t1s) {
        return mList.toArray(t1s);
    }

    public ArrayList<Value> toArrayList() {
        return new ArrayList<>(mList);
    }
}