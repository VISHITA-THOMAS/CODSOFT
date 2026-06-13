package com.currencyconverter.utils;

import com.currencyconverter.models.Currency;
import java.util.LinkedHashMap;
import java.util.Map;

public class CurrencyData {
    private static final Map<String, Currency> MAP = new LinkedHashMap<>();
    static {
        MAP.put("AED", new Currency("AED", "UAE Dirham",         "d.i"));
        MAP.put("AUD", new Currency("AUD", "Australian Dollar",  "A$"));
        MAP.put("BDT", new Currency("BDT", "Bangladeshi Taka",   "BDT"));
        MAP.put("BRL", new Currency("BRL", "Brazilian Real",     "R$"));
        MAP.put("CAD", new Currency("CAD", "Canadian Dollar",    "C$"));
        MAP.put("CHF", new Currency("CHF", "Swiss Franc",        "CHF"));
        MAP.put("CNY", new Currency("CNY", "Chinese Yuan",       "CN¥"));
        MAP.put("CZK", new Currency("CZK", "Czech Koruna",       "Kc"));
        MAP.put("DKK", new Currency("DKK", "Danish Krone",       "kr"));
        MAP.put("EGP", new Currency("EGP", "Egyptian Pound",     "E£"));
        MAP.put("EUR", new Currency("EUR", "Euro",               "€"));
        MAP.put("GBP", new Currency("GBP", "British Pound",      "£"));
        MAP.put("HKD", new Currency("HKD", "Hong Kong Dollar",   "HK$"));
        MAP.put("HUF", new Currency("HUF", "Hungarian Forint",   "Ft"));
        MAP.put("IDR", new Currency("IDR", "Indonesian Rupiah",  "Rp"));
        MAP.put("ILS", new Currency("ILS", "Israeli Shekel",     "₪"));
        MAP.put("INR", new Currency("INR", "Indian Rupee",       "₹"));
        MAP.put("JPY", new Currency("JPY", "Japanese Yen",       "¥"));
        MAP.put("KRW", new Currency("KRW", "South Korean Won",   "₩"));
        MAP.put("KWD", new Currency("KWD", "Kuwaiti Dinar",      "KD"));
        MAP.put("MXN", new Currency("MXN", "Mexican Peso",       "MX$"));
        MAP.put("MYR", new Currency("MYR", "Malaysian Ringgit",  "RM"));
        MAP.put("NGN", new Currency("NGN", "Nigerian Naira",     "₦"));
        MAP.put("NOK", new Currency("NOK", "Norwegian Krone",    "kr"));
        MAP.put("NZD", new Currency("NZD", "New Zealand Dollar", "NZ$"));
        MAP.put("PHP", new Currency("PHP", "Philippine Peso",    "₱"));
        MAP.put("PKR", new Currency("PKR", "Pakistani Rupee",    "Rs"));
        MAP.put("PLN", new Currency("PLN", "Polish Zloty",       "zl"));
        MAP.put("QAR", new Currency("QAR", "Qatari Riyal",       "QR"));
        MAP.put("RON", new Currency("RON", "Romanian Leu",       "lei"));
        MAP.put("RUB", new Currency("RUB", "Russian Ruble",      "₽"));
        MAP.put("SAR", new Currency("SAR", "Saudi Riyal",        "SR"));
        MAP.put("SEK", new Currency("SEK", "Swedish Krona",      "kr"));
        MAP.put("SGD", new Currency("SGD", "Singapore Dollar",   "S$"));
        MAP.put("THB", new Currency("THB", "Thai Baht",          "฿"));
        MAP.put("TRY", new Currency("TRY", "Turkish Lira",       "₺"));
        MAP.put("TWD", new Currency("TWD", "Taiwan Dollar",      "NT$"));
        MAP.put("UAH", new Currency("UAH", "Ukrainian Hryvnia",  "₴"));
        MAP.put("USD", new Currency("USD", "US Dollar",          "$"));
        MAP.put("VND", new Currency("VND", "Vietnamese Dong",    "₫"));
        MAP.put("ZAR", new Currency("ZAR", "South African Rand", "R"));
    }
    public static Currency[] asArray() { return MAP.values().toArray(new Currency[0]); }
    public static Currency getByCode(String code) { return MAP.get(code); }
}
