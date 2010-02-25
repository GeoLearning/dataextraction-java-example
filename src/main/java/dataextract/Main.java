package dataextract;

import java.util.Map;
import java.util.TreeMap;

public class Main {
    private static final String BASE_URL = "https://services.geolearning.com/dataextraction";
    private static final String DOMAIN = "your_domain";
    private static final String USERNAME = "your_username";
    private static final String PASSWORD = "your_password";

    public static void main(String[] args) {
        Resource resource = new Resource(BASE_URL + "/" + DOMAIN, USERNAME, PASSWORD);
        Csv csv = resource.getCsvFor("users");

        System.out.println("Found " + csv.size() + " records");

        if(csv.size() > 0) {
            System.out.println("The first row is:");
            Map<String, String> row = new TreeMap<String, String>(csv.row(0));

            for(Map.Entry<String, String> entry : row.entrySet()) {
                System.out.println("  " + entry.getKey() + " = " + entry.getValue());
            }
        }
    }
}
