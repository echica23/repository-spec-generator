# repository-spec-generator

Java リポジトリのソースコードから仕様書（Excel）を生成する CLI ツールです。

## 前提条件

- Java 17 以上
- `JAVA_HOME` が設定されていること（Windows の `mvnw.cmd` 実行時に必要）

## ビルド

Maven Wrapper を同梱しているため、Maven のグローバルインストールは不要です。

### Windows

```cmd
mvnw.cmd clean package
```

### Linux / macOS

```bash
chmod +x mvnw
./mvnw clean package
```

ビルド成果物は `target/repository-spec-generator-1.0.0-SNAPSHOT.jar` に出力されます。

## 実行

```bash
java -jar target/repository-spec-generator-1.0.0-SNAPSHOT.jar \
  --srcRoot <ソースルート> \
  --out <出力Excelパス> \
  --repository <リポジトリ名> \
  --template <テンプレートExcelパス>
```
