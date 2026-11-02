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

---

<!-- _class: lead -->

# String クラスの罠を理解して、
# 最高のパフォーマンスを
# 引き出そう！

![width:150px height:150px](img/photo.jpg) @YujiSoftware
