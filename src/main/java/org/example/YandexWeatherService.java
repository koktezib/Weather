package org.example;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Класс **YandexWeatherService** предназначен для получения метеорологических данных
 * с сервиса Яндекс Погоды и вывода их на экран. Класс содержит след. функционал:
 *
 * - Отправляет HTTP-запрос к API Яндекс Погоды с заданными координатами и параметрами.
 * - Выводит полный ответ от сервиса в формате JSON.
 * - Извлекает и выводит текущую температуру из раздела `fact`.
 * - Вычисляет и выводит среднюю температуру за указанный период на основе прогноза.
 *
 * Примечание: Для работы требуется действительный API-ключ Яндекс Погоды.
 */
public class YandexWeatherService {

    public static void main(String[] args) throws IOException, InterruptedException {
        String apiKey = "ee0121f0-6c04-400e-8222-38597bd3f8e4";
        String lat = "55.75"; // Широта
        String lon = "37.62"; // Долгота
        int limit = 5; // Количество дней для прогноза

        String url = String.format(
                "https://api.weather.yandex.ru/v2/forecast?lat=%s&lon=%s&limit=%d",
                lat, lon, limit
        );

        // Создание клиента и запроса
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("X-Yandex-Weather-Key", apiKey)
                .build();

        // Отправкам запроса и получение ответа
        HttpResponse<String> response = client.send(
                request, HttpResponse.BodyHandlers.ofString()
        );

        // Вывод ответа в формате JSON
        String responseBody = response.body();
        System.out.println("Полный JSON ответ:");
        System.out.println(responseBody);

        // Парсинг JSON-ответа
        JSONObject jsonObject = new JSONObject(responseBody);

        // Извлечение и вывод текущей температуры
        JSONObject fact = jsonObject.getJSONObject("fact");
        int currentTemp = fact.getInt("temp");
        System.out.println("Текущая температура: " + currentTemp + "°C");

        // Вычисление средней температуры за указанный период
        JSONArray forecasts = jsonObject.getJSONArray("forecasts");
        int sumTemp = 0;
        int count = 0;

        for (int i = 0; i < forecasts.length(); i++) {
            JSONObject forecast = forecasts.getJSONObject(i);
            JSONObject parts = forecast.getJSONObject("parts");
            JSONObject dayPart = parts.getJSONObject("day");

            // Проверка наличие среднего значения температуры
            if (dayPart.has("temp_avg")) {
                int dayTemp = dayPart.getInt("temp_avg");
                sumTemp += dayTemp;
                count++;
            }
        }

        if (count > 0) {
            double avgTemp = sumTemp / (double) count;
            System.out.println("Средняя температура за " + count + " дней: " + avgTemp + "°C");
        } else {
            System.out.println("Не удалось вычислить среднюю температуру за указанный период.");
        }
    }
}
