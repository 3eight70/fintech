## Как попасть в Swagger
1. Запустите приложение
2. Перейдите по ссылке: http://localhost:8080/swagger-ui/index.html#

## Получение текущей ставки валюты
Для получения текущей ставки отправьте GET запрос на url: http://localhost:8080/currencies/rates/${code},
где code - символьный код валюты, например: RUB, USD и т.д

## Конвертация валюты
Для конвертации валюты отправьте POST запрос на url: http://localhost:8080/currencies/convert,
в котором должно присутствовать тело в формате json.  
Пример тела запроса:
```
{
  "fromCurrency": "RUB",
  "toCurrency": "USD",
  "amount": 100
}
```  
Пример тела ответа:
```
{
  "fromCurrency": "RUB",
  "toCurrency": "USD",
  "convertedAmount": 1.04
}
```
- fromCurrency - Конвертируемая валюта
- toCurrency - Валюта, в которую происходит конвертация
- amount - Количество конвертируемой валюты, в данном случае - 100 рублей
- convertedAmount - Количество сконвертированной валюты, в данном случае - 1.04 доллара
