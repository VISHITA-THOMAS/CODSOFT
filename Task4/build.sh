#!/bin/bash
# build.sh — compiles and packages the Currency Converter

set -e

echo "=== Compiling Java source ==="
mkdir -p out/com/currency

javac -d out src/main/java/com/currency/CurrencyServer.java

echo "=== Copying web resources ==="
mkdir -p out/web
cp src/main/resources/web/index.html out/web/
cp src/main/resources/web/style.css  out/web/
cp src/main/resources/web/app.js     out/web/

echo "=== Building JAR ==="
jar cfe CurrencyConverter.jar com.currency.CurrencyServer -C out .

echo ""
echo "Build complete!  Run with:"
echo "  java -jar CurrencyConverter.jar"
echo "Then open: http://localhost:8080"
