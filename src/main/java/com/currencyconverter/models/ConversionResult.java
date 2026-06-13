package com.currencyconverter.models;

public class ConversionResult {
    private final double amount, convertedAmount, exchangeRate;
    private final Currency fromCurrency, toCurrency;
    private final String timestamp;

    public ConversionResult(double amount, double convertedAmount,
                            Currency from, Currency to,
                            double exchangeRate, String timestamp) {
        this.amount = amount; this.convertedAmount = convertedAmount;
        this.fromCurrency = from; this.toCurrency = to;
        this.exchangeRate = exchangeRate; this.timestamp = timestamp;
    }

    public double   getAmount()          { return amount; }
    public double   getConvertedAmount() { return convertedAmount; }
    public Currency getFromCurrency()    { return fromCurrency; }
    public Currency getToCurrency()      { return toCurrency; }
    public double   getExchangeRate()    { return exchangeRate; }
    public String   getTimestamp()       { return timestamp; }

    public String getFormattedResult() {
        return String.format("%s %.2f   =   %s %.4f",
                fromCurrency.getSymbol(), amount,
                toCurrency.getSymbol(), convertedAmount);
    }

    public String getRateInfo() {
        return String.format("1 %s  =  %s %.6f     |     Rate as of %s",
                fromCurrency.getCode(), toCurrency.getSymbol(),
                exchangeRate, timestamp);
    }
}
