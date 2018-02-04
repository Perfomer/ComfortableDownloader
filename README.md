# Comfortable Downloader
**Comfortable Downloader is the very simple and light-weight library representive an abstract loader based on <i>[Retrofit 2](https://github.com/square/retrofit)</i>.**

You tired of doing a million callbacks, giant `Retrofit.Callback` interface implementations? `Comfortable Downloader` offers really short way to load several objects that have identifiers.

## Features:
|  | Feature | Description |
| --- | --- | --- |
| 👍 | **One callback for all items** | Pass several id's to the `query()` method and `Comfortable Downloader` will asynchronously load each item for you. You will be notified when the download is complete. |
| 👍 | **Automatic caching** | If item id was already loaded, than you will receive it immidiately without requesting from the network. If at some point you need to preventive download item directly from the server, you can use `refresh()` method. |
| 👍 | **Automatic saving into the local database** | Each item will be saved to local database after loading. |
| 👍 | **Automatic reading from the local database** | If there are some problems with loading (e.g. no internet connection), item can be read from local database. |
| 👍 | **Automatic casts** | In case if you load from server just a model-object, `ComfortableDownloader` can cast it to the needed type. |
| 👍 | **No memory leaks** | After loading finished each callback will be cleared, moreover cached items will continue to be availiable. |

## Installation
### Step 1
Add repository to your `gradle.build` file __(project module)__
```gradle
repositories {
  jcenter()
}
```
### Step 2
Add dependency to your `gradle.build` file __(app module)__
```gradle
dependencies {
   compile 'com.github.perfomer:comfortable-downloader:LATEST_VERSION'
}
```
See the last release version here: [[LATEST_VERSIONS]](https://github.com/Perfomer/ComfortableDownloader/releases).

## Usage
### Step 1
Implement `Entity<Key>` interface in your model-object:
```java
public class UserModel implements Entity<String> {

  private String mId;

  ...
  
  @Override
  public void getEntityKey() {
    return mId;
  }
  
  @Override
  public void setEntityKey(String key) {
    mId = key;
  }
  
}
```
### Step 2
Create your downloader class extending `ComfortableDownloader<InputItem extends Entity<ItemKey>, ItemKey>`:
```java
public class UserDownloader extends ComfortableDownloader<UserModel, String> {

  ...
  
}
```

### Step 3
Implement 3 simple methods in your downloader class. As a rule, they are all one-line.
- `call()` method inits a request call for each item, that you want to download. This method have to return a function from your `Retrofit` API-interface.
```java
@Override
protected Call<UserModel> call(String id) {
  return MyApplication.getApi().getUserById(id);
}
```
- `save()` method calls after each item downloading so that you can save it into the local database.
```java
@Override
protected void save(UserModel item) {
  //saving item into the database
}
```
- `read()` method calls when there are some problems with loading item from the Internet, so `ComfortableDownloader` try to give you this item at least from the local database.
```java
@Override
protected void read(String id) {
  //reading item from the database
}
```
### Step 4
Create a downloader object:
```java
UserDownloader userDownloader = new UserDownloader();
```
And just `query` needed items. This method taskes the following arguments:

| Argument Type | Optional | Description |
| --- | :---: | --- |
| `SuccessListener<UserModel, String>` | ❌ | Callback will notify you when all items are loaded and give you loaded items. |
| `FailureListener<String>` | ✔️ | Callback will notify you when some of items aren't loaded and give you Map<String, Throwable> with keys and failure reasons for each key. |
| `String...` or `Collection<String>` | ❌ | The list of keys that you want to download. It can be vararg or collection. |

If you use Java 8 or higher you can use lambdas:
```java
userDownloader.query(userModels -> doAnythingYouWant(userModels), "405", "406", "407", "admin#012");
```
```java
userDownloader.query(this::doAnythingYouWant, "405", "406", "407", "admin#012");
```
If your Java version lower than 8, you can use anonymous class:
```java
userDownloader.query(new SuccessListener<UserModel, String>() {
    @Override
    public void onLoaded(@NonNull List<UserModel> userModels) {
      doAnythingYouWant(userModels);
    }
}, "405", "406", "407", "admin#012");
```

## Additional
### Automatic casts
In case if you load from server just a model-object, `ComfortableDownloader` can cast it to needed type. You have to extend `CastingComfortableDownloader<InputItem extends Entity<ItemKey>, ItemKey, OutputItem extends Entity<ItemKey>>`. This class is almost identical with `ComfortableDownloader` class, there's only one additional method. For example:
```java
public class UserDownloader extends CastingComfortableDownloader<UserModel, String, User> {

  ...
  
  protected User cast(UserModel model) {
    return new User(model);
  }
  
  ...
  
}
```
### Refreshing cached data
You need to refresh cached data? No problem, you can use `refresh()` method, that have the same arguments like a `query()` method.

## Communication
- If you found a bug, please [open an Issue](https://github.com/Perfomer/ComfortableDownloader/issues).
- If you have a feature request, please [open an Issue](https://github.com/Perfomer/ComfortableDownloader/issues).
- If you want to contribute, please [submit a Pull request](https://github.com/Perfomer/ComfortableDownloader/pulls).

## License
```
MIT License

Copyright (c) 2018 Denis Balchugov

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
