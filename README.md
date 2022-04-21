# 診療報酬点数表
## 概要
* [公開サイト](http://tensuhyo.html.xdomain.jp/)
* [GitHub内ホームページ](https://saka1029.github.io/tensuhyo/data/web/)

## 開発環境の準備
開発はVSCodeを使用していますが、これが必須というわけではありません。JDK(Java)とMaven、それとテキストエディタがあれば十分です。


### このプロジェクトのクローン
このプロジェクトのクローンを作成します。


### プロジェクトのビルド
pom.xmlの定義にしたがって、ソースコードをコンパイルし、
targetディレクトリの下に`tensuhyo-1.0.jar`を作成します。
これは以下のMavenコマンドで行います。
```
mvn clean package
```

### 依存ライブラリのダウンロード
pom.xmlで定義した依存するライブラリのJarファイルをlibディレクトリの下にダウンロードします。
これは以下のMavenコマンドで行います。
```
mvn dependency:copy-dependencies -DoutputDirectory=lib
```

## HTML/PDF生成

|STEP|処理内容|
|----|--------|
| k0 |施設基準PDF変換|
| k1 |施設基準HTML生成|
| i0 |医科PDF変換|
| i1 |医科HTML生成|
| s0 |歯科PDF変換|
| s1 |歯科HTML生成|
| t0 |調剤PDF変換|
| t1 |調剤HTML生成|

### ディレクトリ構成
```
  プロジェクトディレクトリ
      +---data
          +---in
          |   +---04 (令和4年)
          |       +---k (施設基準)
          |       |   +---pdf
          |       |   |   +---告示.pdf
          |       |   |   +---通知.pdf
          |       |   +---txt
          |       |       +---kokuji.txt
          |       |       +---tuti.txt
          |       |       +---告示.txt
          |       |       +---通知.txt
          |       +---i (医科)
          |       |   +---pdf
          |       |   +---txt
          |       +---s (歯科)
          |       |   +---pdf
          |       |   +---txt
          |       +---t (調剤)
          |           +---pdf
          |           +---txt
          +---web
              +---04 (令和4年)
          |       +---k (施設基準)
          |       |   +---index.html
          |       |   +---.....
          |       +---i (医科)
          |       |   +---index.html
          |       |   +---.....
          |       +---s (歯科)
          |       |   +---index.html
          |       |   +---.....
          |       +---t (調剤)
          |       |   +---index.html
          |       |   +---.....
```
### 生成順序
### PDFファイルのダウンロード
### テキストファイルへの変換
### 変換されたテキストファイルのコピー
### HTML/PDFの生成
### index.htmlの編集

## ファイルのアップロード
環境変数FTP_CONFIGにアップロード先のホスト名、FTPユーザ名、パスワードを設定します。
Windowsの場合は以下のように設定します。
````
set FTP_CONFIG=HOST USER PASSWORD
``

## カスタマイズ
# Google Analyticsトラッキングコードの変更
Google Analyticsのトラッキングコードはリソース
saka1029.tensuhyo.generator.GoogleAnalyticsTrackingCode.txt
をコピーしているので、これを変更すればよい。