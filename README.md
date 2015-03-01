# play2-persistent-cache-plugin
Persistent Cache Module for Playframework

Playframeworkで永続キャッシュを使用するためのプラグインです。

Playframeworkには[キャッシュのしくみ](https://www.playframework.com/documentation/2.2.x/JavaCache)が備わっていますが、機能は最低限に抑えられており永続的なキャッシュができません(Playを再起動するとキャッシュはすべて消えてしまう)。

Playframeworkのキャッシュ機構に用いられている[EHCache](http://www.ehcache.org)に永続キャッシュの機能はあるので、それをこのプラグインで使用しています。

## 永続キャッシュプラグインのインポート
build.sbt の libraryDependencies に 次のように追加します
```scala
libraryDependencies ++= Seq(
  "play2-persistent-cache" % "play2-persistent-cache_2.11" % "1.0.0"
)

resolvers += "Maven Repository on Github" at "http://xenonabe.github.io/play2-persistent-cache-plugin/"
```

## 永続キャッシュプラグイン API の使用
キャッシュの保存
```java
// Cache for 2 hours
PersistentCache.set("item.key", item, 60 * 60 * 2);
```

キャッシュの取出し
```java
(MyItem) item = (MyItem)PersistentCache.get("item.key");
```

キャッシュの削除
```java
PersistentCache.remove("item.key");
```

保存されているキャッシュのキーをすべて取得
```java
List<String> keys = PersistentCache.getKeys();
```

## 永続キャッシュプラグイン の設定
デフォルトのキャッシュの保存先はjava.io.tmpdirです(EHCacheのデフォルト)。

保存先を変更するにはjava.io.tmpdirを設定するか、あるいは conf フォルダに persistentEhcache.xml を作成して配置し、pathを指定してください。
```xml
<diskStore path="/path/to/store/data"/>
```

このプラグインのデフォルトは[persistentEhcache-default.xml](conf/persistentEhcache-default.xml)となっています。

参考リンク <http://ehcache.org/documentation/2.8/get-started/storage-options#DiskStore>

