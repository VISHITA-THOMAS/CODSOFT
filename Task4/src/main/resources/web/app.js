const CURRENCIES = [
  "AED","ARS","AUD","BDT","BRL","CAD","CHF","CNY","CZK","DKK",
  "EGP","EUR","GBP","HKD","HUF","IDR","ILS","INR","JPY","KRW",
  "MXN","MYR","NGN","NOK","NZD","PHP","PKR","PLN","RUB","SAR",
  "SEK","SGD","THB","TRY","TWD","UAH","USD","VND","ZAR"
];

function populateSelects() {
  const fromSel = document.getElementById("from");
  const toSel   = document.getElementById("to");
  CURRENCIES.forEach(c => {
    fromSel.add(new Option(c, c));
    toSel.add(new Option(c, c));
  });
  fromSel.value = "USD";
  toSel.value   = "INR";
}

function swapCurrencies() {
  const from = document.getElementById("from");
  const to   = document.getElementById("to");
  [from.value, to.value] = [to.value, from.value];
}

async function convert() {
  const amount = document.getElementById("amount").value;
  const from   = document.getElementById("from").value;
  const to     = document.getElementById("to").value;

  const resultDiv = document.getElementById("result");
  const errorDiv  = document.getElementById("error");
  const loader    = document.getElementById("loader");

  resultDiv.style.display = "none";
  errorDiv.style.display  = "none";
  loader.style.display    = "block";

  try {
    const res  = await fetch(`/api/convert?from=${from}&to=${to}&amount=${amount}`);
    const data = await res.json();

    loader.style.display = "none";

    if (data.error) {
      errorDiv.textContent = "Error: " + data.error;
      errorDiv.style.display = "block";
      return;
    }

    document.getElementById("resultAmount").textContent =
      `${parseFloat(data.amount).toLocaleString()} ${data.from}  =  ${parseFloat(data.result).toLocaleString(undefined,{minimumFractionDigits:2,maximumFractionDigits:2})} ${data.to}`;

    document.getElementById("resultRate").textContent =
      `1 ${data.from} = ${parseFloat(data.rate).toFixed(6)} ${data.to}`;

    resultDiv.style.display = "block";
  } catch (e) {
    loader.style.display = "none";
    errorDiv.textContent = "Network error. Make sure the Java server is running.";
    errorDiv.style.display = "block";
  }
}

document.getElementById("amount").addEventListener("keydown", e => {
  if (e.key === "Enter") convert();
});

populateSelects();
