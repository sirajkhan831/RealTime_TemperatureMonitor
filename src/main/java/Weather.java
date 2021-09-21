import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Scanner;

public class Weather {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Welcome to the real time temperature monitoring tool");
        Gson gson = new Gson();
        System.out.print("Enter your city's name to get it's temperature data : ");
        String city = new Scanner(System.in).nextLine();
        var urlGeo = "https://api.opencagedata.com/geocode/v1/json?q="+city+"&key=a238b3abbc004c6198a4b83580fd7a4f";
        var requestGeo = HttpRequest.newBuilder().GET().uri(URI.create(urlGeo)).build();
        var clientGeo = HttpClient.newBuilder().build();
        HttpResponse<String> response1 = clientGeo.send(requestGeo, HttpResponse.BodyHandlers.ofString());
        JsonObject jsonObject = gson.fromJson(response1.body(), JsonObject.class);
        JsonElement lat = jsonObject.get("results").getAsJsonArray().get(0).getAsJsonObject().get("bounds").getAsJsonObject().get("northeast").getAsJsonObject().get("lat");
        JsonElement lng = jsonObject.get("results").getAsJsonArray().get(0).getAsJsonObject().get("bounds").getAsJsonObject().get("northeast").getAsJsonObject().get("lng");
        String latString = lat.getAsString().substring(0, 4);
        String lngString = lng.getAsString().substring(0, 4);
        System.out.println("Searching for city "+city+" ("+latString + "N " + lngString+"E) (may take a few seconds depending upon server load)");
        var url = "https://www.7timer.info/bin/astro.php?lon="+lngString+"&lat="+latString+"&ac=0&unit=metric&output=json";
        var request = HttpRequest.newBuilder().GET().uri(URI.create(url)).build();
        var client = HttpClient.newBuilder().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonObject object = gson.fromJson(response.body(), JsonObject.class);
        Calendar current = Calendar.getInstance();
        int hour = current.get(Calendar.HOUR_OF_DAY);
        int round = (hour / 3) * 3;
        if ((hour - round) > 1) {
            round += 3;
        }
        int index = 0;
        switch (round) {
            case 3 -> index = 0;
            case 6 -> index = 1;
            case 9 -> index = 2;
            case 12 -> index = 3;
            case 15 -> index = 4;
            case 18 -> index = 5;
            case 21 -> index = 6;
            case 24 -> index = 7;
        }
        System.out.println("The temperature of "+city+" at " + LocalTime.now().format(DateTimeFormatter.ISO_TIME).substring(0, 5) + " is -> " + object.get("dataseries").getAsJsonArray().get(index).getAsJsonObject().get("temp2m") + " degree celsius.");
        System.out.println("To view the graphical data press 1 To exit press 2");
        if (new Scanner(System.in).nextInt() == 1)
        {

            Desktop.getDesktop().browse(URI.create("http://www.7timer.info/bin/astro.php?lon="+lngString+"&lat="+latString+"&ac=0&lang=en&unit=metric&output=internal&tzshift=0"));
        }
        else System.exit(1);
    }
}
