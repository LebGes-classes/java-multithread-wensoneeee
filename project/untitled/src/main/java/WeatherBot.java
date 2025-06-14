import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.json.JSONObject;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;


public class WeatherBot extends TelegramLongPollingBot {
    private final String botToken = "7666430732:AAEWeYUrD0ZqGdLVx9Aet37m3EOjLnDbGRk";//ключ тг
    private final String apiKey = "83d2d459aef92b1bcd2e0d8ae63a39f0"; // апи погоды
    private final ExecutorService executor = Executors.newFixedThreadPool(10); // всего максимум 10 потоков

    @Override
    public String getBotUsername() {
        return "быстрый прогноз погоды!"; // имя бота
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {//проверка на наличие НЕпустого соо
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            if (messageText.startsWith("/weather")) {
                String[] parts = messageText.split(" ");
                if (parts.length < 2) {//если название неправильное - просит заново
                    sendMessage(chatId, "Пожалуйста, укажите город: /weather <город>");
                    return;
                }
                String city = parts[1];

                // задача запускается в отдельном потоке
                executor.submit(() -> {                 //вывод текста для отображения многопоточности
                    System.out.println(Thread.currentThread().getName() + " начал поиск погоды для " + city);
                    try {
                        String weather = getWeather(city);
                        sendMessage(chatId, weather);//вывод погоды
                    } catch (Exception e) {
                        sendMessage(chatId, "Ошибка при получении погоды: " + e.getMessage());
                    }                                               //вывод соо для конца потока
                    System.out.println(Thread.currentThread().getName() + " завершил поиск погоды для " + city);
                });
            } else {            //если что-то не так, просим заново
                sendMessage(chatId, "Используйте команду /weather <город>");
            }
        }
    }

    private String getWeather(String city) throws Exception {
        String urlString = "http://api.openweathermap.org/data/2.5/weather?q=" + city +
                "&appid=" + apiKey + "&units=metric&lang=ru";
        URL url = new URL(urlString);//превращает ссылку выше в url
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();//создает коннекшн
        conn.setRequestMethod("GET");//показывает, что данные ПОЛУЧАЮТСЯ
        conn.connect();//соединяется с сервером

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {//если ответ не 200(успех), то выводим ошибку
            throw new RuntimeException("HTTP ошибка: " + responseCode);
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();//считывание всех данных на вход
        String line;//построчное считывание!
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();//после обязательно закроем поток

        JSONObject json = new JSONObject(response.toString());//превращение из Json в привычный текстовый вариант
        String cityName = json.getString("name");
        JSONObject main = json.getJSONObject("main");
        double temp = main.getDouble("temp");
        String description = json.getJSONArray("weather").getJSONObject(0).getString("description");

        return String.format("Погода в %s:\nТемпература: %.1f°C\nОписание: %s", cityName, temp, description);
    }

    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();//отправка сообщения
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            WeatherBot bot = new WeatherBot();
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(bot);
            System.out.println("Бот запущен!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}