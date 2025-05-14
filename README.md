# 📈 STIN25 News API – Komunikace s burzou

Tento dokument popisuje, jak externí burzovní systémy mohou komunikovat s backendem aplikace pro hodnocení akcií na základě aktuálních zpráv.

## 🌐 URL endpoint

https://stin25news-production.up.railway.app/liststock/rating

zprávy obdrzí json od burzy -> name a date -> zpravy vyhodnotí vstupy a pokud projdou tak pošlou zpět i s rating 

curl -X POST "https://stin25news-production.up.railway.app/liststock/rating" \
  -H "Content-Type: application/json" \
  -d '[
    { "name": "ASML", "date": 1715702400 },
    { "name": "AAPL", "date": 1715702400 }
  ]'

[{"name":"ASML","date":1715702400,"rating":1},
{"name":"AAPL","date":1715702400,"rating":2}],                 


burza následně pošle json ještě s sell, kde zprávy vyhodnocí zda se má daná akcie prodat nebo koupit ->
máme potrfolio kde ukládáme/bereme akcie

curl -X POST "https://stin25news-production.up.railway.app/liststock/salestock" \
  -H "Content-Type: application/json" \
  -d '[
    { "name": "AAPL", "date": 1715702400, "rating": 2, "sell": 0 },
    { "name": "TSLA", "date": 1715702400, "rating": 1, "sell": 1 },
    { "name": "GOOG", "date": 1715702400, "rating": 0, "sell": 0 },
    { "name": "NFLX", "date": 1715702400, "rating": -1, "sell": 1 }
  ]'
["Akcie AAPL již je v portfoliu, nekoupeno","Nelze prodat akcii TSLA – není v portfoliu","Akcie GOOG již je v portfoliu, nekoupeno","Prodaná akcie: NFLX"]%    

curl https://stin25news-production.up.railway.app/liststock/portfolio            
  
["GOOG","AAPL"]


