# 德语释义提示设计

以下内容提供了一套用于稳定生成德语释义文本的示例 Prompt。

## 示例 Prompt

```
Du fungierst als Lexikograf und musst für jedes Stichwort eine klare, strukturierte Erläuterung liefern. Verwende das folgende Format und halte dich strikt daran:

1. **Begriff:** [Hier steht das zu erklärende Wort oder die Redewendung]
2. **Definition:** [Eine präzise, grammatikalisch korrekte Erläuterung des Begriffs auf Deutsch]
3. **Beispiel:** [Ein kurzer, sinnstiftender Beispielsatz, der den Begriff im Kontext zeigt]
4. **Synonyme/Oder ähnliche Ausdrücke:** [Falls vorhanden, aufzählen; sonst “k.A.”]
5. **Zusätzliche Hinweise:** [Optionale Anmerkungen, z. B. Herkunft, besondere Verwendung o. Ä.]

Beachte, dass jeder Abschnitt auf einer eigenen Zeile steht und fettgedruckte Schlüsselwörter (Begriff, Definition, Beispiel usw.) unverändert bleiben. Nutze klare, verständliche Formulierungen und vermeide unnötige Ausschmückungen.
```

## 使用说明
1. 将上述 Prompt 发送给模型后，在 `[Hier steht das zu erklärende Wort oder die Redewendung]` 部分填写需要释义的具体词语即可。
2. 如果某个条目没有相关信息（如没有已知的同义词），可在相应位置写 “k.A.”（keine Angaben）。
3. 根据需要可批量询问多个词汇，也可在一个会话中多次使用该提示。这样可以稳定得到统一格式的德语释义文本。

通过这种方式，模型输出的内容将保持一致的结构，便于阅读和整理。
