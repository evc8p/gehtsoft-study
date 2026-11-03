1. Метод checkRequest() (из CustomWebServer) package-private дял тестов
2. Нет тестов на POST в checkRequest() (из CustomWebServer) из-за сильного допущения в методе.
3. В checkRequest() (из CustomWebServer) Accept проверяется по подстроке - допущение
4. В main() ApplicationContext есть CustomWebServer virtualServer = (CustomWebServer). Думаю, лучше сделать интерфейс Server и аннотацией от него наследовать: дать выбор пользователю: либо аннотацией, либо реализация интерфейса.
