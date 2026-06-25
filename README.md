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
Apache POI / JavaParser などの依存ライブラリをすべて同梱した実行可能 JAR（fat JAR）です。

## 実行

### fat JAR（推奨）

依存ライブラリを別途用意する必要はありません。

#### Windows

```cmd
java -jar target\repository-spec-generator-1.0.0-SNAPSHOT.jar --srcRoot C:\work\project\src\main --out output
```

#### Linux / macOS

```bash
java -jar target/repository-spec-generator-1.0.0-SNAPSHOT.jar \
  --srcRoot /path/to/project/src/main \
  --out output
```

### オプション

| オプション | 必須 | 説明 |
|---|---|---|
| `--srcRoot` | はい | ソースルート（例: `src/main` または `sample-src`） |
| `--out` | いいえ | 出力ディレクトリ（既定: `output`） |
| `--repository` | いいえ | 対象 Repository 名（例: `Book`） |
| `--template` | いいえ | Excel テンプレートパス（未指定時は `template/default.xlsx`） |

### 開発時（Maven exec）

```bash
mvnw.cmd exec:java "-Dexec.args=--srcRoot sample-src --out output"
```

### 実行例（全オプション）

```bash
java -jar target/repository-spec-generator-1.0.0-SNAPSHOT.jar \
  --srcRoot <ソースルート> \
  --out <出力ディレクトリ> \
  --repository <リポジトリ名> \
  --template <テンプレートExcelパス>
```
