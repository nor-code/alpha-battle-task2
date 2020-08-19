###Условие
В топике Kafka RAW_PAYMENTS находятся данные по платежам пользователей. 
Брокер развернут на виртуальной машине в docker контейнере и доступен по IP:29092 снаружи.

Перед началом выполнения задания необходимо поднять контейнер с Kafka.

Для запуска:
```
mkdir task2
cd task2
wget https://raw.githubusercontent.com/evgenyshiryaev/alfa-battle-resources/master/task2/data.txt
wget https://raw.githubusercontent.com/evgenyshiryaev/alfa-battle-resources/master/task2/docker-compose.yml
wget https://raw.githubusercontent.com/evgenyshiryaev/alfa-battle-resources/master/task2/start.sh
bash start.sh
```
Скрипт развернет необходимую инфраструктуру и зальет данные в топик.

Для остановки (в папке task2):
```docker-compose down```

* IP - внешний IP виртуальной машины.
* Все ресурсы лежат тут: https://github.com/evgenyshiryaev/alfa-battle-resources/tree/master/task2

Формат одной записи представлен ниже:

```key1:{"ref":"U030306190000188", "categoryId":1, "userId":"XAABAA", "recipientId":"XA3SZV", "desc":"Платеж за услуги", "amount":10.0}```

где key1 - ключ записи.
Все остальное после двоеточия является данными по платежу.

#### Необходимо реализовать следующую логику:
- Вычитать все данные из топика Kafka.
- Выполнить агрегацию данных для возможности отображения аналитики по платежам пользователей.
- Реализовать REST API для доступа к аналитике по пользователям. Приложение должно быть доступно по порту 8081.

#### Уточнения
- Данные уже будут присутствовать в топике RAW_PAYMENTS. Дополнительно никаких данных во время тестирования поступать не будет.

- Анализ должен производится на тех данных, которые будут вычитаны из топика.

- Следует учесть, что данные из Kafka могут приходить битыми.

- Дополнительное использование различных хранилищ и подходы к реализации аналитики данных остаются на усмотрение разработчика.

### Дополнительная информация
- Т.к. данные для анализа уже находятся в топике, необходимо лишь развернуть приложение, которое вычитает их и произведет анализ. Возможны случаи, когда потребуется перечитать данные заново (приложение не проходит автотесты, либо участник что-то не учел).

#### Возможны два способа:
- Запускать приложение каждый раз с новым уникальным consumer-group-id. В этом случае при правильной конфигурации консьюмера приложение будет перечитывать топик с данными каждый раз с начала. Вручную скинуть оффсеты для топика для заданной consumer-group. Либо, для простоты, возможно скинуть все оффсеты для всех групп, воспользовавшись следующей командой

``` docker exec -i broker kafka-consumer-groups --bootstrap-server broker:9092 --all-groups --all-topics --reset-offsets --to-earliest --execute ```

- Рестартовать контейнеры с инфраструктурой кафки скриптом start.sh, как было описано вначале. Желательно при этом остановить все консьюмеры, чтобы не возникало неожиданных эффектов в дальнейшем при работе

Пример Swagger описания ожидаемого интерфейса

Swagger описание ожидаемого интерфейса приведено в файле api-swagger.json в папке ресурсов задания.
Задачи

### Пример данных в топике:

``` key1:{"ref":"ref1", "categoryId":1, "userId":"User_1", "recipientId":"User_2", "desc":"Тестовый платеж_1", "amount":10.0}
key2:{"ref":"ref2", "categoryId":2, "userId":"User_1", "recipientId":"User_2", "desc":"Тестовый платеж_2", "amount":350.56}
key3:{"ref":"ref3", "categoryId":1, "userId":"User_1", "recipientId":"User_2", "desc":"Тестовый платеж_3", "amount":700.0}
key4:{"ref":"ref4", "categoryId":3, "userId":"User_1", "recipientId":"User_2", "desc":"Тестовый платеж_4", "amount":5.99}
key5:{"ref":"ref5", "categoryId":1, "userId":"User_1", "recipientId":"User_2", "desc":"Тестовый платеж_5", "amount":10.0}
key6:{"ref":"ref6", "categoryId":2, "userId":"User_2", "recipientId":"User_3", "desc":"Тестовый платеж_6", "amount":350.56}
key7:{"ref":"ref7", "categoryId":1, "userId":"User_1", "recipientId":"User_2", "desc":"Тестовый платеж_7", "amount":890.0}
key8:{"ref":"ref8", "categoryId":3, "userId":"User_3", "recipientId":"User_2", "desc":"Тестовый платеж_8", "amount":35.99}
key9:{"ref":"ref9", "categoryId":1, "userId":"User_1", "recipientId":"User_2", "desc":"Тестовый платеж_9", "amount":890.0}
key10:{"ref":"ref10", "categoryId":3, "userId":"User_3", "recipientId":"User_2", "desc":"Тестовый платеж_10", "amount":35.9910}
key11:{"ref":"ref11", "categoryId":1, "userId":"User_1", "recipientId":"User_2", "desc":"Тестовый платеж_11", "amount":10.0}
key12:{"ref":"ref12", "categoryId":2, "userId":"User_2", "recipientId":"User_3", "desc":"Тестовый платеж_12", "amount":350.56}
key13:{"ref":"ref13", "categoryId":1, "userId":"User_1", "recipientId":"User_2", "desc":"Тестовый платеж_13", "amount":10.0}
key14:{"ref":"ref14", "categoryId":2, "userId":"User_2", "recipientId":"User_3", "desc":"Тестовый платеж_14", "amount":350.56}
key15:{"ref":"ref15", "categoryId":4, "userId":"User_1", "recipientId":"User_4", "desc":"Тестовый платеж_15", "amount":15.00}
```

# ЗАДАЧИ

# 1 Разбиение данных по категориям
Необходимо выполнить разбиение данных платежей по категориям (поле categoryId).
По каждой категории платежа пользователя необходимо подсчитать минимальную сумму, максимальную сумму и общую сумму в категории.
Кроме того требуется отобразить общую сумму платежей пользователя по всем категориям.

```
GET http://IP:8081/admin/health
200

Пример ответа:
{"status":"UP"}
GET http://IP:8081/analytic
200

Возвращает аналитику платежей по всем пользователям

Пример ответа:
[
  {
    "userId": "User_3",
    "totalSum": 71.981,
    "analyticInfo": {
      "3": {
        "min": 35.99,
        "max": 35.991,
        "sum": 71.981
      }
    }
  },
  {
    "userId": "User_2",
    "totalSum": 1051.68,
    "analyticInfo": {
      "2": {
        "min": 350.56,
        "max": 350.56,
        "sum": 1051.68
      }
    }
  },
  {
    "userId": "User_1",
    "totalSum": 2891.55,
    "analyticInfo": {
      "1": {
        "min": 10,
        "max": 890,
        "sum": 2520
      },
      "2": {
        "min": 350.56,
        "max": 350.56,
        "sum": 350.56
      },
      "3": {
        "min": 5.99,
        "max": 5.99,
        "sum": 5.99
      },
      "4": {
        "min": 15,
        "max": 15,
        "sum": 15
      }
    }
  }
]
```
Ключи структуры analyticInfo являются Id категорий платежей.
GET http://IP:8081/analytic/{userId}
- 200
- 404 + тело {"status":”user not found"} (если пользователь не найден)

Получение аналитики по конкретному пользователю

Пример запроса:
```
GET http://IP:8081/analytic/User_1
Пример ответа:

{
  "userId": "User_1",
  "totalSum": 2891.55,
  "analyticInfo": {
    "1": {
      "min": 10,
      "max": 890,
      "sum": 2520
    },
    "2": {
      "min": 350.56,
      "max": 350.56,
      "sum": 350.56
    },
    "3": {
      "min": 5.99,
      "max": 5.99,
      "sum": 5.99
    },
    "4": {
      "min": 15,
      "max": 15,
      "sum": 15
    }
  }
}
```

# 2 Статистика
Требуется уметь отображать статистику категорий по каждому пользователю. 
Статистика:
- самая частая категория трат
- самая редкая категория трат
- категория с наибольшей суммой
- категория с наименьшей суммой

```
GET http://IP:8081/analytic/{userId}/stats
- 200
- 404 + тело {"status":”user not found"}  (если пользователь не найден)
Получение статистики категорий по пользователю.

Пример запроса:
GET http://IP:8081/analytic/User_1/stats
Пример ответа:
{
  "oftenCategoryId": 1,
  "rareCategoryId": 2,
  "maxAmountCategoryId": 1,
  "minAmountCategoryId": 3
}
```

# 3 Шаблоны платежей
Требуется реализовать логику выделения шаблонов платежей из потока данных. 
Платеж считается шаблонным по следующим критериям:
- У платежей совпадают величина, категория, от кого и кому платеж был проведен (recipientId и userId соответственно)
- Такие платежи повторяется три и более раза

Реализовать интерфейс, по которому можно будет получать шаблоны платежей пользователя. 

```
GET http://IP:8081/analytic/{userId}/templates
- 200
- 404 + тело {"status":”user not found"} (если пользователь не найден)
Получение информации по платежам, которые были выделены как шаблонные для данного пользователя.

Пример запроса:
GET http://IP:8081/analytic/User_1/templates
Пример ответа:
[
  {
    "recipientId": "User_2",
    "categoryId": 1,
    "amount": 10
  }
]
```