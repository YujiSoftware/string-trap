---
marp: true
author: "@YujiSoftware"
title: "String クラスの罠を理解して、最高のパフォーマンスを引き出そう！"
description: "JJUG CCC 2026 Fall のセッション資料です。"
image: "https://yuji.software/emoji/img/card.png"
theme: gaia
style: |
  :root {
    --color-background: #fff;
    --color-foreground: #000;
    --color-highlight: #1a0dab;
    --color-dimmed: #888;
    font-size: 40px;
    line-height: 1.2;
  }

  /** 詰め気味に微調整 **/
  section > ul, marp-pre {
    margin: 0.5em 0;
  }
  h1 {
    margin-bottom: 0.5em;
  }

  section.lead img {
    border-radius: 50%;
    vertical-align: middle;
  }
  
  img[alt~="center"] {
    display: block;
    margin: 0 auto;
  }

  .info {
    border: 2px solid #dee2e6;
    background-color: #cff4fc;
    color: #052c65;
    padding: 0.5em;
    border-radius: 10px;
    margin: 10px 0;
  }
  .info::before {
    content: "📝";
  }
  .warn {
    border: 2px solid #e3e6de;
    background-color: #fcf9cf;
    color: #052c65;
    padding: 0.5em;
    border-radius: 10px;
    margin: 0.5em 0;
  }
  .warn::before {
    content: "⚠️";
  }

  span.emphasis {
    color: red;
    font-weight: bold;
  }
  span.underline {
    text-decoration: underline;
  }
  span.supplement {
    font-size: 36px;
  }
---

<!-- _class: lead -->

# String クラスの罠を理解して、
# 最高のパフォーマンスを
# 引き出そう！

![width:150px height:150px](img/photo.jpg) @YujiSoftware

---

# 概要

- Java で String クラス（文字列）は、基本的にパフォーマンス（実行速度）を気にせずに使える
- しかし、それでも遅くなるときがある
  - どういうときなのか
  - どうして遅くなるのか
  - どうすればいいのか

<div class="info">
本日のセッション資料 <br>
https://yuji.software/string-trap に置いてあります。
</div>

---

# 注意事項

- このセッションでは「速くなる書き方」と「遅くなる書き方」について解説します。

<hr>

- 遅い = 悪い **ではない**
- 実行頻度の低いコードは遅くても困らない
  - 例：ユニットテスト、初期化処理
  - これらは、遅くなる書き方でも 1ms も変わらない
- **一律に速くなる書き方を強制しないこと！**

---

# 速くなる書き方が必要な例

```java
for (int i = 0; i < 10000; i++) {
  for (int j = 0; j < 10000; j++) {
    // 処理
  } 
}
```
- 1万回の二重ループ
  - ループ内の実行回数は **1億回**
- ループ1回が 100ns 遅くなると、**全体では 10秒遅くなる**

<div class="info">
実行速度と計算回数の意識が大事
</div>

---

# 文字列の作り方による違い

- \+ 演算子による文字列結合の場合
- StringBuilder クラスを使った場合
- String.format メソッドを使った場合

どれが**速い？遅い？**
遅いというのは、どれぐらい？

---

# \+ 演算子による文字列結合

- メリット
  - 書きやすい
- **一度に**文字列を結合するなら、これがベスト

```java
public String example(String key, int value) {
  return key + ":" + value;
}
```

これはどう動く？

---

# \+ 演算子による文字列結合の実装

1. 全体の長さを計算
2. ちょうど収まる長さの byte 配列を作成
3. 各要素を文字列として表現し、必要な領域に詰め込む

<div class="info">
Java 9 以降の OpenJDK コンパイラの場合<br>
（Java 8 までは、StringBuilder に変換される）
</div>

---

# \+ 演算子による文字列結合の実装

- key = "foo", value = 12 の場合

![center](img/string-concat.drawio.svg)

---

# \+ 演算子の罠

- 分割すると、ちょっと遅くなる
  - 横に長くなってくると、文を分けたくなるが…

```java
String row = id + "," + name + "," + age + "," + address + …
```
↓
```java
String row = id + ",";
row += name + ",";
row += age + ",";
…
```
---

# 分割した際の動き

![](img/string-copy.drawio.svg)

<div class="warn">
+= で結合すると、その度にコピーが発生してしまう
</div>

---

# 参考：どのぐらい差になるのか

- 自宅PCで、10個の変数を , 区切りで結合する処理を計測
  - 一度にまとめた場合： 49.839 (±0.160) ns/op
  - 一つずつ分けた場合： 88.198 (±0.321) ns/op

→ 分割すると、実行時間が **1.77倍** に伸びた

<div class="info">
環境による差異もあるので、数値はあくまで参考程度
</div>

---

# StringBuilder クラスを使う

- 文字列を編集するためのクラス
  - 内部でどういう動きになっているのか？
  - \+ 演算子と比べて、どのぐらい性能がいいのか？

```java
public String example(String key, int value) {
  var sb = new StringBuilder();
  sb.append(key).append(":").append(value);

  return sb.toString();
}
```

---

# StringBuilder 文字列結合の動き

1. new StringBuilder()
    - 16文字分の容量のバッファを作成
2. .append(...)
    - バッファに追加
3. .toString()
    - バッファを元に文字列を作成

---

# StringBuilder 文字列結合の動き

![center](img/string-builder.drawio.svg)

---

# バッファが足りないとき

1. **約2倍**の容量に拡張する（正確には2倍＋2）
2. 中身をコピー
3. 新しい配列に文字を格納

![center](img/string-builder2.drawio.svg)

---

# String の \+ 演算子との使い分け

- 一度に結合**できる**なら \+ 演算子を使う
- 一度に結合**できない**なら、基本的に StringBuilder を使う
  - ループで処理する場合など

どのぐらいの差があるのか？
（\+ 演算子もだいぶ速くなったけど…？）

---

# 参考：String vs StringBuilder

- 自宅PCで、8列のデータをカンマ区切りで結合するサンプルコードを計測（ns/op）

|行数|100|1,000|10,000|100,000|
|:--|--:|--:|--:|--:|
|String|26,403|2,406,795|315,674,831|36,596,801,877|
|Builder|6,453|65,769|700,336|8,275,654|
|比率|4|37|451|4,422|

<div class="warn">
String の + 演算子結合の連続は、急激に遅くなる
</div>

---

# StringBuilder の罠

- **初期のバッファサイズが小さい**（16文字分）
- **コンストラクタで全体のサイズを指定したほうがいい**
  - 指定する値は、予測でも構わない
  - コピーが減るので、少し速くなる
    - 特に、文字列が長くなる場合は効果がある

```java
var sb = new StringBuilder(rows.length * 128);
```

---

# StringBuilder の良くない使い方

- **Bad:** \+演算子と併用する（約10%ぐらい遅くなる）
- **Good:** .append(…) のみを使う

```java
var csv = new StringBuilder();
for (String[] row : rows) {
    // Bad
    csv.append(row[0] + "," + row[1] + "," + row[2] + "\r\n");
    // Good
    csv.append(row[0]).append(",");
    csv.append(row[1]).append(",");
    csv.append(row[2]).append("\r\n");
}
```



---

# String.format / String#formatted

- メリット
  - 柔軟な書式の指定が可能
  - できあがる文字列がわかりやすい
- String.formatted とは、書き方が違うだけで処理は同じ

```java
public String example(String key, int value) {
  return String.format("%s: %d", key, value);
  -- or -- 
  return "%s: %d".formatted(key, value);
}
```

---

# 実装

- Formatter クラスに移譲している
```java
return new Formatter().format(format, args).toString();
```

1. new Formtter(): 
    - 内部で StringBuilder を作成
2. format(format, args)
    - 引数の format をパース
    - 変換しつつ StringBuilder に追加
4. toString()

---

# 実装

![center](img/string-format.drawio.svg)

---

# String.format の罠

- \+ 演算子や StringBuilder に比べて、**遅い**
  - 主にパースによる影響
- ただし、**新しいバージョンではかなり改善されている**
  - 逆に言うと、古い Java を使っているなら要注意

---

# 参考：バージョンごとの速度差

- String.format("%s: %d", key, value); の実行時間を測定

![center](img/formatter.png)

---

# 改善要因

- Java 16 まで、**パースに正規表現を使っているので遅かった**
- Java 17 で、単純な書式指定子（例：%d や %s）ならば
正規表現を使わない実装になった
- Java 19, 21 で細かい改善が行われた
- Java 23 で、複雑な書式指定子（例：%05d や %.2f）でも正規表現を使わない実装になった

→ 単純な結合なら、**依然として \+ 演算子のほうが良い**
→ 桁揃えなど**書式指定**のためなら、使うのはアリ？

<!--
- [\[JDK-8263038\] Optimize String.format for simple specifiers](https://bugs.openjdk.org/browse/JDK-8263038)
- [\[JDK-8278642\] Refactor java.util.Formatter](https://bugs.openjdk.org/browse/JDK-8278642)
- [\[JDK-8335252\] Reduce size of j.u.Formatter.Conversion#isValid](https://bugs.openjdk.org/browse/JDK-8335252)
[\[JDK-8316704\] Regex-free parsing of Formatter and FormatProcessor specifiers - Java Bug System](https://bugs.openjdk.org/browse/JDK-8316704)
-->

---

# まとめ

- Java で String クラス（文字列）は、基本的に速い
  - しかし、実行回数が多いときは気をつける必要がある
- 適切な文字列の使い方をすると、遅くなるのは回避できる
  - 内部実装を知っておくとハマりにくい

文字列の処理でパフォーマンスが求められたときは、このセッションのことを思い出そう！

---

<!-- _class: lead -->

# String クラスの罠を理解して、
# 最高のパフォーマンスを
# 引き出そう！

![width:150px height:150px](img/photo.jpg) @YujiSoftware

---

<!-- _class: lead -->

# 補足資料

---

# ログの場合

- Q. ログの場合は文字列結合を使ったほうがいい？
- A. いいえ
&nbsp;
- ログレベルによって、ログを出さない場合がある
  - 書式指定の場合、その場合に文字列結合をしないですむ

```java
logger.info("Key: {}, Value: {}", key, value);  // Good
----
logger.info("Key: " + key + ", Value: " + value);  // Bad
```

---

# String.format の実装について

- 毎回、書式をパースするの無駄では？
- ということで、パースした結果を内部で保持する改善提案があがった
  - [8335791: Speed ​​up j.u.Formatter & String.format by wenshao · Pull Request #20055 · openjdk/jdk](https://github.com/openjdk/jdk/pull/20055?utm_source=chatgpt.com)
  - しかし、String Template と併せて検討するということになりクローズ
- その後、String Template が**当分見送り**になってしまった…

---

# 正規表現の扱い方 (1)

- replaceAll メソッドは、内部で正規表現のコンパイルをしている
```java
public String replaceAll(String regex, String replacement) {
    return Pattern.compile(regex).matcher(this).replaceAll(replacement);
}
```

- このコンパイルが**かなり遅い**し、正規表現自体も遅い
- 正規表現マッチが不要なら **replace メソッド**を使う
  - 名前が紛らわしいが、replace メソッドも**すべて置換する**

---

# 正規表現の扱い方 (2)

- もし何度も実行するなら、Pattern クラスを定数として保持しておく

```java
private static final Pattern MASK = Pattern.compile("\\d");
---
String masked = MASK.matcher(tel).replaceAll("*");
```

- String.matches メソッドを使うときも同様

---

# 参考：ベンチマーク環境

- AMD Ryzen™ 7 5700X（8コア16スレッド）
- Ubuntu 26.04
- OpenJDK 25.0.5 (LTS)
- Java Microbenchmark Harness 1.37
  - Warmup: 1秒×5回
  - Measurement	1秒×5回
  - Fork: 2
- Code: https://github.com/YujiSoftware/string-trap/tree/main/benchmark

---

# アンケートにご協力ください

|全体アンケート|セッションアンケート|
|:--:|:--:|
|![width:400px](img/jjug_qr.png)|![width:400px](img/session_qr.png)|