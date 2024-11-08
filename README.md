## Как попасть в Swagger

1. Запустите приложение
2. Перейдите по ссылке: http://localhost:8080/swagger-ui/index.html#

## Авторизация и аутентификация

Каждый токен в системе = сессия пользователя

### Общий процесс

Когда пользователь приходит в систему идет проверка на то, является ли эндпоинт разрешенным  
Если да, то пользователь получает к нему доступ
Если нет, то нет то:

1. Достаем хэдер 'Authorization' из запроса, если он пуст выдаем 401
2. Если заполнен, то достаем оттуда токен
3. Идет запрос к базе данных на проверку того, хранится ли там данный токен
4. Если хранится, то проверяем его время жизни, если оно истекло, то 401
5. Если не истекло, то достаем userId из токена и получаем пользователя из бд
6. Преобразуем сущность User в UserDto и зашиваем в контекст UsernamePasswordAuthenticationToken с нашим пользователем
7. Далее пользователь ходит в системе под своим аккаунтом

```plantuml
@startuml
actor User
participant "Система" as System
participant "База данных" as Database

User -> System: Запрос к эндпоинту
alt Эндпоинт разрешен
    System --> User: Доступ к эндпоинту
else Эндпоинт не разрешен
    System -> System: Проверка хедера 'Authorization'
    alt Хедер 'Authorization' пуст
        System --> User: 401 Unauthorized
    else Хедер заполнен
        System -> System: Извлечение токена из хедера
        System -> Database: Запрос на наличие токена
        alt Токен не найден в базе
            System --> User: 401 Unauthorized
        else Токен найден
            System -> System: Проверка срока действия токена
            alt Срок действия истек
                System --> User: 401 Unauthorized
            else Срок действия не истек
                System -> Database: Запрос на получение пользователя по userId
                Database --> System: User
                System -> System: Преобразование User в UserDto
                System -> System: Запись UserDto в контекст (UsernamePasswordAuthenticationToken)
                System --> User: Доступ к системе под аккаунтом
            end
        end
    end
end
@enduml
```

### Регистрация или авторизация

В случае регистрации мы создаем пользователя и тут же генерируем для него AccessToken, который мы возвращаем  
В случае авторизации, мы сразу проверяем данные пользователя, если все верно, то после этого генерируем и возвращаем
токен

```plantuml
@startuml
actor User
participant "Система" as System
participant "База данных" as Database

== Регистрация ==
User -> System: Запрос на регистрацию (данные пользователя)
System -> Database: Проверка наличия пользователя
alt Пользователь существует
    System --> User: 400 BadRequest (Пользователь уже существует)
else Пользователь не существует
    System -> Database: Создание нового пользователя
    Database --> System: Успешное создание
    System -> System: Генерация AccessToken
    System --> User: Возврат AccessToken
end

== Авторизация ==
User -> System: Запрос на авторизацию (логин и пароль)
System -> Database: Проверка данных пользователя
alt Данные некорректны
    System --> User: 401 Unauthorized
else Данные корректны
    System -> System: Генерация AccessToken
    System --> User: Возврат AccessToken
end

@enduml
```