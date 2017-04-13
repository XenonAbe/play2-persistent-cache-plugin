# play2-persistent-cache-plugin (module)
Persistent Cache Module for Playframework

Playframeworkで永続キャッシュを使用するためのモジュールです。  
(Playframework 2.5.ｘ 対応)

Playframeworkには[キャッシュのしくみ](https://www.playframework.com/documentation/2.5.x/JavaCache)が備わっていますが、機能は最低限に抑えられており永続的なキャッシュができません(Playを再起動するとキャッシュはすべて消えてしまう)。

Playframeworkのキャッシュ機構に用いられている[EHCache](http://www.ehcache.org)に永続キャッシュの機能はあるので、それをこのプラグインで使用しています。

## 永続キャッシュモジュールのインポート
build.sbt の libraryDependencies に 次のように追加します
```scala
libraryDependencies ++= Seq(
  "twinkle-persistent-cache" % "twinkle-persistent-cache_2.11" % "2.5.5"
)

resolvers += "Maven Repository on Github" at "http://xenonabe.github.io/play2-persistent-cache-plugin/"
```

また、application.confにモジュール追加の設定をします
必要に応じてキャッシュの名前を指定してください
(複数インスタンスでのバッティングを防ぐため)
```conf
play.modules.enabled += "twinkle.cache.PersistentCacheModule"

play.cache.persistent.name = "Foo"
```

## 永続キャッシュプラグイン API の使用
インジェクションでインスタンスを取得してください
```java
＠Inject
Provider<PersistentCacheApi> persistentCacheProvider

void foo() {
  PersistentCacheApi persistentCache = persistentCacheProvider.get();
}
```

キャッシュの保存
```java
// Cache for 2 hours
persistentCache.set("item.key", item, 60 * 60 * 2);
```

キャッシュの取出し
```java
(MyItem) item = (MyItem)persistentCache.get("item.key");
```

キャッシュの削除
```java
persistentCache.remove("item.key");
```

保存されているキャッシュのキーをすべて取得
```java
List<String> keys = persistentCache.getKeys();
```

## 永続キャッシュプラグイン の設定
デフォルトのキャッシュの保存先はjava.io.tmpdirです(EHCacheのデフォルト)。

保存先を変更するにはjava.io.tmpdirを設定するか、あるいは conf フォルダに persistentEhcache.xml を作成して配置し、pathを指定してください。
```xml
<diskStore path="/path/to/store/data"/>
```

このモジュールのデフォルト設定は[persistentEhcache-default.xml](conf/persistentEhcache-default.xml)にあります。

参考リンク <http://ehcache.org/documentation/2.8/get-started/storage-options#DiskStore>

