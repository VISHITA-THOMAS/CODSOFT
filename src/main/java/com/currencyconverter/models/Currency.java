package com.currencyconverter.models;

public class Currency {
    private final String code, name, symbol;
    public Currency(String code, String name, String symbol) {
        this.code = code; this.name = name; this.symbol = symbol;
    }
    public String getCode()   { return code;   }
    public String getName()   { return name;   }
    public String getSymbol() { return symbol; }
    @Override public String toString() { return code + "  -  " + name; }
}
