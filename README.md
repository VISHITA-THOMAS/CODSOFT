# 💱 Currency Converter

A Java Swing desktop application that converts currencies using live exchange rates.  
Opens as a **native desktop window** — not a browser, not localhost.

---

## What It Does

- Convert between 40+ world currencies (USD, EUR, INR, GBP, JPY and more)
- Fetches real-time rates from the free Frankfurter API (European Central Bank)
- Swap button to instantly reverse the currency pair
- Input validation and clear error messages
- No API key required · No external libraries · 100% pure Java

---

## How to Run

### In VS Code
1. Install **Extension Pack for Java** from the Extensions panel
2. Open the `CurrencyConverter_Fixed` folder → `File → Open Folder`
3. Open `src/main/java/com/currencyconverter/Main.java`
4. Click **Run** above the `main` method

### In Terminal
```bash
cd CurrencyConverter_Fixed
mkdir out
javac -d out src\main\java\com\currencyconverter\models\Currency.java src\main\java\com\currencyconverter\models\ConversionResult.java src\main\java\com\currencyconverter\utils\FormatUtils.java src\main\java\com\currencyconverter\utils\CurrencyData.java src\main\java\com\currencyconverter\services\ExchangeRateService.java src\main\java\com\currencyconverter\ui\ConverterPanel.java src\main\java\com\currencyconverter\ui\MainFrame.java src\main\java\com\currencyconverter\Main.java
java -cp out com.currencyconverter.Main
```

---

## Requirements

- JDK 11 or higher — download free from [adoptium.net](https://adoptium.net)
- Internet connection (for live rates)

---

## Project Structure

```
src/main/java/com/currencyconverter/
├── Main.java               ← Entry point
├── models/
│   ├── Currency.java
│   └── ConversionResult.java
├── services/
│   └── ExchangeRateService.java
├── utils/
│   ├── CurrencyData.java
│   └── FormatUtils.java
└── ui/
    ├── MainFrame.java
    └── ConverterPanel.java
```

---

## API Used

**Frankfurter** — `https://api.frankfurter.app`  
Free · Open source · No key required · Backed by the European Central Bank