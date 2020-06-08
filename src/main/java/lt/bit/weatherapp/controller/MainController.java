package lt.bit.weatherapp.controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TimeZone;
import javax.transaction.Transactional;
import lt.bit.weatherapp.model.Temp;
import lt.bit.weatherapp.repository.TempRepository;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class MainController {

    private final String API_KEY = "climacell.co API KEY";
    // Vilnius coordinates according to https://www.latlong.net/place/vilnius-lithuania-8277.html
    private final float LAT = 54.687157f;
    private final float LON = 25.279652f;
    private final String UNIT_SYSTEM = "si";

    @Autowired
    private TempRepository tempRepository;

    @RequestMapping
    public String mainPage(ModelMap modelMap) {

        Map<Object, Object> map;
        List<Map<Object, Object>> dataPoints1 = new ArrayList<>();
        List<List<Map<Object, Object>>> list = new ArrayList<>();

        String xValue;
        int yValue;

        List<Temp> dataList = tempRepository.findAllSorted();
        for (int i = 0; i < dataList.size(); i++) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

            yValue = dataList.get(i).getTemp();
            xValue = sdf.format(dataList.get(i).getObstime());
            map = new HashMap<>();
            map.put("label", xValue);
            map.put("y", yValue);
            dataPoints1.add(map);
        }
        list.add(dataPoints1);

        modelMap.addAttribute("dataPointsList", list);
        return "chart";
    }

    @Scheduled(fixedRate = 3600000)
    public void getDataEveryHour() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        Temp lastTempRecord = tempRepository.findFirstByOrderByObstimeDesc();
        if (lastTempRecord != null) {
            String recordDate = lastTempRecord.getObstime().toString().substring(0, 10);
            LocalDate dateFrom = LocalDate.parse(recordDate);
            LocalDate dateNow = LocalDate.now();
            if (!dateFrom.equals(dateNow)) {
                while (!dateFrom.equals(dateNow)) {
                    getData(dateFrom.toString(), dateFrom.plusDays(1).toString());
                    System.out.println("Updating data");
                    dateFrom = dateFrom.plusDays(1);
                }
            } else {
                String from = sdf.format(lastTempRecord.getObstime());
                String to = "now";
                getData(from, to);
                System.out.println("Hourly update from last record " + from + " to " + to);
            }
        }
    }

    @RequestMapping("/getMonthData")
    public String getMonthData() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        LocalDate dateNow = LocalDate.now();
        LocalDate date = LocalDate.now().minusDays(30);
        
        while (!date.equals(dateNow)) {
            System.out.println("Getting data from " + date + " to " + date.plusDays(1));
            getData(date.toString(), date.plusDays(1).toString());
            date = date.plusDays(1);
        }

        Temp lastTempRecord = tempRepository.findFirstByOrderByObstimeDesc();
        String from = sdf.format(lastTempRecord.getObstime());
        String to = "now";
        getData(from, to);
        return "redirect:/";
    }

    @Transactional
    public void getData(String from, String to) {
        String startTimeSuffix = "T12:00:00Z";
        String endTimeSuffix = "T12:00:00Z";
        LocalDate dateNow = LocalDate.now();
        if (from.contains("Z")) {
            startTimeSuffix = "";
        }
        if ((from.equals(dateNow.toString()) && from.contains("Z")) || to.equals("now")) {
            to = "now";
            endTimeSuffix = "";
        }
        try {
            JSONParser parser = new JSONParser();
            String requestString = "https://api.climacell.co/v3/weather/historical/station?"
                    + "lat=" + LAT + "&lon=" + LON + "&unit_system=" + UNIT_SYSTEM + "&start_time=" + from + startTimeSuffix + "&end_time=" + to + endTimeSuffix + "&fields=temp";
            JSONArray jsonArray = (JSONArray) parser.parse(jsonGetRequest(requestString));

            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                JSONObject tempObject = (JSONObject) jsonObject.get("temp");
                JSONObject timeObject = (JSONObject) jsonObject.get("observation_time");

                ZonedDateTime zp = ZonedDateTime.parse((String) timeObject.get("value"));
                Date date = Date.from(zp.toInstant());

                Temp temp = new Temp();
                temp.setTemp(Integer.valueOf(tempObject.get("value").toString()));
                temp.setObstime(date);
                Temp existing = tempRepository.findByTime(date);
                if (existing == null) {
                    tempRepository.save(temp);
                }
            }
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
    }

    private String streamToString(InputStream inputStream) {
        String text = new Scanner(inputStream, "UTF-8").useDelimiter("\\Z").next();
        return text;
    }

    public String jsonGetRequest(String urlQueryString) {
        String json = null;
        try {
            URL url = new URL(urlQueryString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("charset", "utf-8");
            connection.setRequestProperty("apikey", API_KEY);
            connection.connect();
            InputStream inStream = connection.getInputStream();
            json = streamToString(inStream);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return json;
    }
}
