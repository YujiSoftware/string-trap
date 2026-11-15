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
    margin: 10px 0;
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

# 注意事項

- このセッションでは String クラスに関する「速くなる書き方」と「遅くなる書き方」について解説します。

<hr>

- 遅い != **悪い** ではない
- 実行頻度の低いコードは遅くても困らない
  - 例：ユニットテスト、初期化処理
  - これらは、遅くなる書き方でも 1ms も変わらない
- **一律に速くなる書き方を強制しないこと！**

---

# 文字列の作り方

- \+ 演算子による文字列結合
- StringBuilder クラスを使う
- String#formatted メソッドを使う

どれが速い？遅い？

---

# 前提：String の構造

- 特徴
  - 不変クラス（= 文字列の変更は作り直し）
- 主な中身
  - byte 配列
    - LATIN1 の文字**のみ**なら、1文字1バイトで扱う
    - UTF-16 の文字を含むなら、1文字2バイトで扱う
  - エンコーディング識別子
    - LATIN1 または UTF-16 のどちらか

---

# 前提：String の構造

TODO: 図で解説

---

# \+ 演算子による文字列結合

- 一番よく使う書き方
- メリット
  - 書きやすい
  - **一度に**文字列を結合するなら、これが最速

```java
public String example(String key, int value) {
  return key + ": " + value;
}
```

どういう処理になっているのか？

---

# \+ 演算子による文字列結合の実装

1. 全体の長さを計算
    - エンコーディング<span class="supplement">（LATIN1 or UTF-16）</span>を確認
2. ちょうど収まる長さの byte 配列を作成
3. 配列に文字を格納

TODO: 図

---

# \+ 演算子の罠

- 分割すると遅くなる
  - 横に長くなると、処理を分けたくなるが…

```java
String text = key + ": " + value + " [a = " + a + ", b = "  + b + "]";
```
↓

```java
String text = key + ": " + value;
text += "["
text += "a = " + a + ",";
text += "b = " + b;
text += "]";
```

---

# 分割した際の動き

TODO: 図

<div class="warn">
+= での文字列結合は要注意！
</div>

---

# StringBuilder クラスを使う

- 文字列を編集するためのクラス
  - 内部でどういう動きになっているのか？
  - \+ 演算子と比べて、どっちのほうがいいのか？

```java
public String example(String key, int value) {
  var sb = new StringBuilder();
  sb.append(key).append(": ").append(value);

  return sb.toString();
}
```

---

# StringBuilder 文字列結合の動き

1. new StringBuilder()
    - バッファ（長さ16のbyte 配列）を作成
2. append(key)
    - バッファに追加
3. toString()
    - バッファを元に文字列を作成

---

# StringBuilder 文字列結合の動き

TODO: 図

---

# \+ 演算子との使い分け

- 一度に結合できるなら \+ 演算子が最速
  - StirngBuilder だと、少し遅い
- 一度に結合できないなら、StringBuilder が最速
  - ループで処理する場合など

```java

```

どのぐらいの差があるのか？

---

# \+ 演算子 vs StringBuilder




<div class="warn">
+ 演算子の文字列結合の連続は、二次関数的に遅くなる
</div>

---

# StringBuilder の罠

- 初期のバッファサイズが小さい
- 予測でいいので、収まるサイズを指定する
  - 少し速くなる
  - メモリ使用量も減る

```java
TODO
```

---

# String#formatted

- メリット
  - 柔軟なフォーマットの指定が可能
  - 結果がわかりやすい
- String.format とは、書き方が違うだけで処理は同じ

```java
public String example(String key, int value) {
  return "%s: %d".formatted(key, value);
}
```

---

# 実装

---

# String#formatted の罠

- \+演算子と比較して、とにかく遅い
  - 先程の例だと、なんと**300倍遅い！**
- 速度が重要な場面では使用を避ける
  - 数字の桁を揃えるぐらいなら、気合でなんとかなる
    - AIに頼めばやってくれる

---

# String メソッドの罠

- 置換処理
  - replace と replaceAll
- 検索処理
  - contains と matches

---

<!-- _class: lead -->

# String クラスの罠を理解して、
# 最高のパフォーマンスを
# 引き出そう！

![width:150px height:150px](img/photo.jpg) @YujiSoftware
