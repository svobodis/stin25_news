async function fetchNews() {
    const stockInput = document.getElementById("stocks").value;
    const stockNames = stockInput.split(",").map(s => s.trim()).filter(s => s);
    const minArticles = parseInt(document.getElementById("minArticles").value);

if (isNaN(minArticles) || minArticles < 1) {
  alert("Zadejte platný počet článků (alespoň 1).");
  return;
}

    const allowNegative = document.getElementById("allowNegative").checked;
  
    const response = await fetch(
      `/liststock?minArticles=${minArticles}&allowNegative=${allowNegative}`,
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify(stockNames)
      }
    );
  
    const result = await response.json();
    document.getElementById("output").textContent = JSON.stringify(result, null, 2);
  }
  