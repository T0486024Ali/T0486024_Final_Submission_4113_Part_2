import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.time.LocalDate;
import java.util.InputMismatchException;

// A class that holds data for a single journey.
class JourneyDetails {

    private int journeyId;
    private int fromZone;
    private int toZone;
    private String timeBand;
    private String passengerType;
    private LocalDate journeyDate;
    private int zonesCrossed;
    private double baseFare;
    private double discountedFare;
    private double chargedFare;

    // Constructor only takes raw journey input.
    public JourneyDetails(int journeyId, int fromZone, int toZone,
                          String timeBand, String passengerType, double baseFare, LocalDate journeyDate) {

        this.journeyId = journeyId;
        this.fromZone = fromZone;
        this.toZone = toZone;
        this.timeBand = timeBand;
        this.passengerType = passengerType;
        this.baseFare = baseFare;
        this.journeyDate = journeyDate;
        this.zonesCrossed = Math.abs(toZone - fromZone) + 1;
    }

    public JourneyDetails() {
        this.journeyId = 0;
        this.fromZone = 1;
        this.toZone = 1;
        this.timeBand = "off-peak";
        this.passengerType = "adult";
        this.baseFare = 0;
        this.journeyDate = LocalDate.now();
    }

    // Setters used internally by Journey class.
    public void setDiscountedFare(double discountedFare) {
        this.discountedFare = discountedFare;
    }

    public void setChargedFare(double chargedFare) {
        this.chargedFare = chargedFare;
    }

    // Getters.
    public double getBaseFare() {
        return baseFare;
    }

    public double getDiscountedFare() {
        return discountedFare;
    }

    public int getJourneyId() {
        return journeyId;
    }

    public int getFromZone() {
        return fromZone;
    }

    public int getToZone() {
        return toZone;
    }

    public String getTimeBand() {
        return timeBand;
    }

    public String getPassengerType() {
        return passengerType;
    }

    public double getChargedFare() {
        return chargedFare;
    }

    public LocalDate getJourneyDate()  {
        return journeyDate;
    }

    public void setBaseFare(double baseFare) {
        this.baseFare = baseFare;
    }

    public String toCSV() {
        return journeyId + "," + fromZone + "," + toZone + "," + timeBand + "," + passengerType + "," + baseFare  + "," + journeyDate;
    }

    // Displays full journey details.
    public void displayDetails() {
        System.out.println("Journey ID: " + journeyId + " " +
                "Starting zone: " + fromZone + " " +
                "Destination zone: " + toZone + " " +
                "Time band: " + timeBand + " " +
                "Passenger type: " + passengerType + " " +
                "Zones crossed: " + zonesCrossed + " " +
                "Base fare: £" + String.format("%.2f",baseFare) + " " +
                "Discounted fare: £" + String.format("%.2f",discountedFare) + " " +
                "Charged fare: £" + String.format("%.2f",chargedFare) + " " +
                "Journey date: " + journeyDate);
    }
}

// A class that adds a journey.
class AddJourney {

    private final List<JourneyDetails> journeyList;
    private final DisplayTotalByPassengerType totals;

    public AddJourney(List<JourneyDetails> journeyList,  DisplayTotalByPassengerType totals) {
        this.journeyList = journeyList;
        this.totals = totals;
    }

    // Adds a journey object and computes all fares internally.
    public void addJourney(JourneyDetails journeyDetails) {

        double discountedFare = applyDiscount(journeyDetails, journeyDetails.getBaseFare());

        journeyDetails.setDiscountedFare(discountedFare);

        journeyList.add(journeyDetails);
        totals.recalculateTotals();
        System.out.println("Journey added successfully.");
    }

    // Ensures journey IDs are unique to avoid ambiguity when adding, removing or undoing journeys.
    public boolean journeyIdExists(int id) {
        for (JourneyDetails journey : journeyList) {
            if (journey.getJourneyId() == id) {
                return true;
            }
        }
        return false;
    }

    // Applies discount based on passenger type.
    public double applyDiscount(JourneyDetails journeyDetails, double baseFare) {

        double discountRate = 0;

        if (journeyDetails.getPassengerType().equalsIgnoreCase("student")) {
            discountRate = 0.25;
        } else if (journeyDetails.getPassengerType().equalsIgnoreCase("child")) {
            discountRate = 0.50;
        } else if (journeyDetails.getPassengerType().equalsIgnoreCase("senior citizen")) {
            discountRate = 0.30;
        } else if (journeyDetails.getPassengerType().equalsIgnoreCase("adult")) {
            discountRate = 0.0;
        } else {
            System.out.println("Invalid passenger type");
        }
        return baseFare * (1 - discountRate);
    }
}

// A class that lists all the journeys.
class ListJourneys {


    private final List<JourneyDetails> journeyList;

    public ListJourneys(List<JourneyDetails> journeyList) {
        this.journeyList = journeyList;
    }

    // Displays all journeys.
    public void displayJourneys() {

        if (journeyList.isEmpty()) {
            System.out.println("No journeys recorded.");
            return;
        }

        for (JourneyDetails journey : journeyList) {
            journey.displayDetails();
        }
    }
}

// A class that filters all the journeys.
class FilterJourneys {

    private final List<JourneyDetails> journeyList;

    public FilterJourneys(List<JourneyDetails> journeyList) {
        this.journeyList = journeyList;
    }

    // Filters journeys by the passenger type.
    public void filterByPassengerType(String type) {
        for (JourneyDetails journeyDetails : journeyList) {
            if (journeyDetails.getPassengerType().equalsIgnoreCase(type)) {
                journeyDetails.displayDetails();
            }
        }
    }

    // Filters journeys by the time band.
    public void filterByTimeBand(String timeBand) {
        for (JourneyDetails journeyDetails : journeyList) {
            if (journeyDetails.getTimeBand().equalsIgnoreCase(timeBand)) {
                journeyDetails.displayDetails();
            }
        }
    }

    // Filters journeys by the starting zone.
    public void filterByStartingZone(int startingZone) {
        for (JourneyDetails journeyDetails : journeyList) {
            if (journeyDetails.getFromZone() == startingZone) {
                journeyDetails.displayDetails();
            }
        }
    }

    // Filters journeys by the destination zone.
    public void filterByDestinationZone(int destinationZone) {
        for (JourneyDetails journeyDetails : journeyList) {
            if (journeyDetails.getToZone() == destinationZone) {
                journeyDetails.displayDetails();
            }
        }
    }

    // Filters journeys by the journey date.
    public void filterByJourneyDate(LocalDate date) {
        for (JourneyDetails journeyDetails : journeyList) {
            if (journeyDetails.getJourneyDate().equals(date)) {
                journeyDetails.displayDetails();
            }
        }
    }
}

// A class that removes a specific journey using the journey id.
class RemoveJourney {

    private final List<JourneyDetails> journeyList;
    private JourneyDetails lastRemovedJourney = null;
    private final DisplayTotalByPassengerType totals;

    public RemoveJourney(List<JourneyDetails> journeyList, DisplayTotalByPassengerType totals) {
        this.journeyList = journeyList;
        this.totals = totals;
    }

    public JourneyDetails removeJourneyById(int id) {

        for (int i = 0; i < journeyList.size(); i++) {
            if (journeyList.get(i).getJourneyId() == id) {

                lastRemovedJourney = journeyList.get(i);
                journeyList.remove(i);

                // recalculate totals after removing
                totals.recalculateTotals();


                System.out.println("Journey removed successfully.");
                return lastRemovedJourney;
            }
        }

        System.out.println("No journey found with that ID.");
        lastRemovedJourney = null;
        return null;
    }

    // A method to get the last removed journey in order to help in undoing this journey later in the UndoJourney class.
    public JourneyDetails getLastRemovedJourney() {
        return lastRemovedJourney;
    }

    /* A method that clears the last removed journey to avoid undoing the same journey in the UndoJourney class
    twice if the user pressed the choice of undoing this journey two times after each other. */
    public void clearLastRemovedJourney() {
        lastRemovedJourney = null;
    }
}

// A class that undo the last removed journey in the journeys list.
class UndoJourney {

    private final List<JourneyDetails> journeyList;
    private final DisplayTotalByPassengerType totals;

    public UndoJourney(List<JourneyDetails> journeyList, DisplayTotalByPassengerType totals) {
        this.journeyList = journeyList;
        this.totals = totals;
    }

    public void undoLastRemoval(RemoveJourney remover) {

        JourneyDetails journey = remover.getLastRemovedJourney();

        if (journey == null) {
            System.out.println("Nothing to undo.");
            return;
        }

        journeyList.add(journey);
        remover.clearLastRemovedJourney();

        // A method that recalculates all the passenger types’ totals after restoring a journey.
        totals.recalculateTotals();

        System.out.println("Last removed journey has been restored.");
    }
}

// A class the sorts all the journeys from lowest to highest.
class SortJourneys {

    private final List<JourneyDetails> journeyList;

    public SortJourneys(List<JourneyDetails> journeyList) {
        this.journeyList = journeyList;
    }

    // MANUAL SORT: compares TWO objects taken from the list.
    public void sortJourneysByFare() {
        for (int i = 0; i < journeyList.size() - 1; i++) {
            for (int j = i + 1; j < journeyList.size(); j++) {

                JourneyDetails FirstJourney = journeyList.get(i);
                JourneyDetails SecondJourney = journeyList.get(j);

                if (FirstJourney.getChargedFare() > SecondJourney.getChargedFare()) {
                    journeyList.set(i, SecondJourney);
                    journeyList.set(j, FirstJourney);
                }
            }
        }
        System.out.println("Journeys sorted by fare (lowest to highest).");
    }
}

/* A class that displays a daily summary for all the journeys,
including the total number of journeys and the most expensive journey.
 */
class DisplaySummary {

    private final List<JourneyDetails> journeyList;

    public DisplaySummary(List<JourneyDetails> journeyList) {
        this.journeyList = journeyList;
    }

    // Displays daily summary.
    public void displayDailySummary() {

        if (journeyList.isEmpty()) {
            System.out.println("No journeys recorded today.");
            return;
        }

        int totalJourneys = journeyList.size();
        double totalCost = 0;
        JourneyDetails mostExpensive = journeyList.get(0);

        for (JourneyDetails journey : journeyList) {
            totalCost += journey.getChargedFare();
            if (journey.getChargedFare() > mostExpensive.getChargedFare()) {
                mostExpensive = journey;
            }
        }
        System.out.println("----- Daily Summary -----");
        System.out.println("Total journeys: " + totalJourneys);
        System.out.println("Total cost charged: £" + String.format("%.2f",totalCost));
        System.out.println("Average cost per journey: £" + String.format("%.2f",(totalCost / totalJourneys)));
        System.out.println("Most expensive journey ID: " +
                mostExpensive.getJourneyId() +
                " (£" + String.format("%.2f",mostExpensive.getChargedFare()) + ")");
    }
}

// A class that counts the number of peak, offPeak journeys, and journeys involved in each zone.
class CategoryCounts {

    private final List<JourneyDetails> journeyList;

    public CategoryCounts(List<JourneyDetails> journeyList) {
        this.journeyList = journeyList;
    }

    // Displays peak and off-peak counts.
    public void displayTimeBandCounts() {

        int peak = 0;
        int offPeak = 0;

        for (JourneyDetails Journey : journeyList) {
            if (Journey.getTimeBand().equalsIgnoreCase("peak")) {
                peak++;
            } else {
                offPeak++;
            }
        }

        System.out.println("Peak journeys: " + peak);
        System.out.println("Off-peak journeys: " + offPeak);
    }

    // Displays how many journeys involved each zone (zones 1–5 only).
    public void displayZoneCounts() {

        int[] zoneCounts = new int[6]; // Index 1–5 represent zones.

        for (JourneyDetails Journey : journeyList) {
            zoneCounts[Journey.getFromZone()]++;
            zoneCounts[Journey.getToZone()]++;
        }

        System.out.println("Zone involvement counts:");
        for (int zone = 1; zone <= 5; zone++) {
            if (zoneCounts[zone] > 0) {
                System.out.println("Zone " + zone + ": " + zoneCounts[zone]);
            }
        }
    }
}

// A class that resets all the journeys in a specific day.
class ResetDay {

    private final List<JourneyDetails> journeyList;
    private final DisplayTotalByPassengerType totals;

    public ResetDay(List<JourneyDetails> journeyList,
                    DisplayTotalByPassengerType totals) {
        this.journeyList = journeyList;
        this.totals = totals; // <-- the SAME instance used in main
    }

    // Resets all journeys.
    public void resetJourneys(boolean Confirmed) {

        if (!Confirmed) {
            System.out.println("Reset cancelled.");
            return;
        }
        totals.resetTotals();
        journeyList.clear();
        System.out.println("All journeys have been reset.");
    }
}

// A class that displays totals by passenger type.
class DisplayTotalByPassengerType {

    private final List<JourneyDetails> journeyList;

    // Running totals per passenger type (for daily cap logic)
    private double adultTotal = 0;
    private double studentTotal = 0;
    private double childTotal = 0;
    private double seniorTotal = 0;

    public DisplayTotalByPassengerType(List<JourneyDetails> journeyList) {
        this.journeyList = journeyList;
    }

    // A method that helps us to get all the daily caps of all the passenger types
    private double getDailyCap(String passengerType) {
        switch(passengerType.toLowerCase()) {
            case "student": return 6.00;
            case "child": return 4.00;
            case "senior citizen": return 7.00;
            case "adult": return 8.00;
            default: return 0;
        }
    }

    // Recalculate totals and update each journey's charged fare
    public void recalculateTotals() {

        // Reset totals before recalculation
        adultTotal = studentTotal = childTotal = seniorTotal = 0;

        // Loop through each journey in order
        for (JourneyDetails journey : journeyList) {
            double discountedFare = journey.getDiscountedFare();  // Already calculated per journey
            double remainingCap = getDailyCap(journey.getPassengerType()) - getRunningTotal(journey.getPassengerType());

            // Charged fare = min(discountedFare, remaining cap left)
            double chargedFare = Math.min(discountedFare, remainingCap);

            // Set the journey's charged fare
            journey.setChargedFare(chargedFare);

            // Add to the running total
            addToRunningTotal(journey.getPassengerType(), chargedFare);
        }
    }

    // A method that gets the running total for a passenger type
    public double getRunningTotal(String passengerType) {
        switch(passengerType.toLowerCase()) {
            case "student": return studentTotal;
            case "child": return childTotal;
            case "senior citizen": return seniorTotal;
            case "adult": return adultTotal;
            default: return 0;
        }
    }

    // A method that adds the total of each student to the running total.
    public void addToRunningTotal(String passengerType, double fare) {
        switch(passengerType.toLowerCase()) {
            case "student": studentTotal += fare; break;
            case "child": childTotal += fare; break;
            case "senior citizen": seniorTotal += fare; break;
            case "adult": adultTotal += fare; break;
        }
    }

    // Display totals per passenger type
    public void displayTotalsByPassengerType() {
        System.out.println("Adult Total: £" + String.format("%.2f", adultTotal));
        System.out.println("Student Total: £" + String.format("%.2f", studentTotal));
        System.out.println("Child Total: £" + String.format("%.2f", childTotal));
        System.out.println("Senior citizen Total: £" + String.format("%.2f", seniorTotal));
    }

    // Reset all totals
    public void resetTotals() {
        adultTotal = studentTotal = childTotal = seniorTotal = 0;
    }
}

// A class that writes all the journeys to a CSV file.
class JourneyFileWriter {
    public static void writeStudentsToFile(String fileName, List<JourneyDetails> journeyList) {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(fileName));
            writer.println("journeyID,fromZone,toZone,timeBand,passengerType,baseFare,journeyDate");
            for(JourneyDetails journeyDetails: journeyList){
                writer.println(journeyDetails.toCSV());
            }
            writer.close();
            System.out.println("journeys data successfully written to file");
        }
        catch (IOException e){
            System.out.println("Error writing to file!");
        }
    }
}

// A class that reads all the journeys from a CSV file.
class JourneyFileReader {
    public static List<JourneyDetails> readStudentsFromFile(String fileName) {
        List<JourneyDetails> journeyList = new ArrayList<>();

        try{
            File file = new File(fileName);
            Scanner scanner = new Scanner(file);

            if (scanner.hasNextLine()){
                scanner.nextLine();
            }

            while (scanner.hasNextLine()){
                String line = scanner.nextLine();
                String data[] = line.split(",");

                int journeyId = Integer.parseInt(data[0]);
                int fromZone = Integer.parseInt(data[1]);
                int toZone = Integer.parseInt(data[2]);
                String timeBand = data[3];
                String passengerType = data[4];
                double baseFare = Double.parseDouble(data[5]);
                LocalDate journeyDate = LocalDate.parse(data[6]);

                JourneyDetails journeyDetails = new JourneyDetails(
                        journeyId,
                        fromZone,
                        toZone,
                        timeBand,
                        passengerType,
                        baseFare,
                        journeyDate
                );
                journeyList.add(journeyDetails);
            }
            scanner.close();
        }
        catch (FileNotFoundException e){
            System.out.println("Error -> file not found!");
        }
        catch (NumberFormatException e){
            System.out.println("Error -> Invalid number in file!");
        }
        return journeyList;
    }
}

// A class that writes all the user profiles to a JSON file
class ProfileFileReader {
    private final Gson gson;
    private String fileName;
    public  ProfileFileReader() {
        gson = new GsonBuilder().setPrettyPrinting().create();
        fileName = "profiles.json";
    }
    public List<Profile> loadProfiles() {

        List<Profile> loadProfiles = new ArrayList<>();
        try {
            FileReader reader = new FileReader(fileName);
            Type profileListType = new TypeToken<ArrayList<Profile>>() {}.getType();
            loadProfiles = gson.fromJson(reader, profileListType);
            reader.close();
            if (loadProfiles == null) {
                loadProfiles = new ArrayList<>();
            }
        }
        catch (Exception e) {
            System.err.println("No existing file found. Starting with empty list");
        }
        return loadProfiles;
    }
}

// A class that reads all the user profiles from a JSON file.
class ProfileFileWriter {
    private final List<Profile> profiles;
    private final Gson gson;
    private String fileName;
    public  ProfileFileWriter(List<Profile> profiles) {
        gson = new GsonBuilder().setPrettyPrinting().create();
        this.profiles = profiles;
        fileName = "profiles.json";
    }
    public void saveProfiles() {
        try {
            FileWriter writer = new FileWriter(fileName);
            gson.toJson(profiles, writer);
            writer.close();
            System.out.println("Profiles saved to file.");
        }
        catch (Exception e) {
            System.err.println("Error saving to file");
            e.printStackTrace();
        }
    }
}

// A class that sets the user profile for the rider user role.
class Profile {
    private String name;
    private String defaultPayment;

    public Profile(String name, String defaultPayment) {
        this.name = name;
        this.defaultPayment = defaultPayment;
    }

    public Profile() {
        name = "Ali";
        defaultPayment = "Visa";
    }

    public void showProfile() {
        System.out.println("Profile name: " + name);
        System.out.println("Profile default payment method: " + defaultPayment);
    }
}

// A class that sets all the caps of all the passenger types.
class Config {
    private final List<JourneyDetails> journeyList;

    public Config(List<JourneyDetails> journeyList) {
        this.journeyList = journeyList;
    }

    public void setBaseFare (double baseFare, AddJourney addJourney, DisplayTotalByPassengerType totals) {
        if (journeyList.isEmpty()){
            System.out.println("No Journeys found to modify the base fare for them by the admin!");
            return;
        }
        for (JourneyDetails journeyDetails: journeyList){
            journeyDetails.setBaseFare(baseFare);
            double discounted = addJourney.applyDiscount(journeyDetails, baseFare);
            journeyDetails.setDiscountedFare(discounted);
            System.out.println("Base fare updated in 0 or many journeys to: " + baseFare);
        }
        totals.recalculateTotals();
    }
}

// A class that identifies the rider user role.
class Rider {
    private final List<Profile> profiles;

    public Rider(List<Profile> profiles) {
        this.profiles = profiles;
    }

    public void addProfile(Profile profile){
        profiles.add(profile);
    }

    public void displayProfiles() {
        if (profiles.isEmpty()) {
            System.out.println("No profiles found!");
            return;
        }
        for (Profile profile: profiles){
            profile.showProfile();
        }
    }
}

// A class that identifies the admin user role.
class Admin {
    private String userName;
    private String password;
    private Config config;

    public Admin(String userName, String password, Config config) {
        this.userName = userName;
        this.password = password;
        this.config = config;
    }

    /* A method that allows the admin to log in to the system with his  correct username and password to access a
    specific menu that allows him to add, update, or delete fare rules, discounts, daily caps, and peak time windows */
    public boolean login(String inputUserName, String inputPassword) {
        return userName.equals(inputUserName) && password.equals(inputPassword);
    }

    // A method that modifies the base fare of 1 or multiple journeys.
    public void modifyBaseFare(Config config, double newFare, AddJourney addJourney, DisplayTotalByPassengerType totals) {
        config.setBaseFare(newFare, addJourney, totals);
    }
}

// Main application class.
public class CityRideLiteApp {

    public static void main(String[] args) {

        List<JourneyDetails> journeyList = new ArrayList<>();
        List<Profile>  profiles = new ArrayList<>();
        Scanner sc = new Scanner(System.in);

        Rider rider = new Rider(profiles);
        int i = 0;
        while (i < 10) {
            String name = "";
            boolean validName = false;
            while (!validName) {
                try {
                    System.out.println("Profile name: ");
                    name = sc.nextLine();
                    validName = true;
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a valid input for the profile name: ");
                    sc.nextLine();
                }
            }

            String defaultPayment = "";
            boolean validPaymentMethod = false;
            while (!validPaymentMethod) {
                try {
                    System.out.println("Profile payment method: ");
                    defaultPayment = sc.nextLine();
                    validPaymentMethod = true;
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a valid input for the profile payment method: ");
                    sc.nextLine();
                }
            }
            i++;
            Profile profile = new Profile(name,defaultPayment);
            rider.addProfile(profile);
        }

        String userName = "";
        boolean validUserName = false;
        while (!validUserName) {
            try {
                System.out.println("Admin username: ");
                userName = sc.nextLine();
                validUserName = true;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid input for the admin username: ");
                sc.nextLine();
            }
        }

        String password = "";
        boolean validPassword = false;
        while (!validPassword) {
            try {
                System.out.println("Admin password: ");
                password = sc.nextLine();
                validPassword = true;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid input for the admin password: ");
                sc.nextLine();
            }
        }

        String inputUserName = "";
        boolean inputValidUserName = false;
        while (!inputValidUserName) {
            try {
                System.out.println("Admin input username: ");
                inputUserName = sc.nextLine();
                inputValidUserName = true;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid input for the admin username: ");
                sc.nextLine();
            }
        }

        String inputPassword = "";
        boolean inputValidPassword = false;
        while (!inputValidPassword) {
            try {
                System.out.println("Admin input password: ");
                inputPassword = sc.nextLine();
                inputValidPassword = true;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid input for the admin password: ");
                sc.nextLine();
            }
        }

        double configBaseFare = 0;
        boolean validConfigBaseFare = false;
        while (!validConfigBaseFare) {
            try {
                System.out.println("Config base fare:");
                configBaseFare = sc.nextDouble();
                sc.nextLine();
                validConfigBaseFare = true;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid input for the config base fare.");
                sc.nextLine();
            }
        }

        Config config = new Config(journeyList);
        Admin admin = new Admin(userName,password,config);

        int userRole = 0;
        boolean validUserRole = false;
        while (!validUserRole) {
            try {
                System.out.println("The user role: ");
                userRole = sc.nextInt();
                while (!(userRole == 1) &&
                        !(userRole == 2)) {
                    System.out.println("Please enter a valid user role: ");
                    userRole = sc.nextInt();
                }
                sc.nextLine();
                validUserRole = true;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. User role must be an integer.");
                sc.nextLine();
            }
        }

        if (userRole == 1) {
            rider.displayProfiles();
        }
        else if (userRole == 2) {
            System.out.println("The user role of this program is admin");
        }

        ListJourneys journeys = new ListJourneys(journeyList);
        FilterJourneys journeysFiltered = new FilterJourneys(journeyList);
        SortJourneys sortedJourneys = new SortJourneys(journeyList);
        DisplaySummary journeysSummary = new DisplaySummary(journeyList);
        CategoryCounts count = new CategoryCounts(journeyList);
        DisplayTotalByPassengerType totals = new DisplayTotalByPassengerType(journeyList);
        AddJourney journey1 = new AddJourney(journeyList,totals);
        RemoveJourney removedJourney = new RemoveJourney(journeyList,totals);
        UndoJourney journey2 = new UndoJourney(journeyList,totals);
        ResetDay journeysReset = new ResetDay(journeyList, totals);
        ProfileFileWriter profileFileWriter = new ProfileFileWriter(profiles);
        ProfileFileReader profileFileReader = new ProfileFileReader();

        boolean running = true;

        while (running) {

            System.out.println("\n --- CityRide Lite ---");
            System.out.println("1. Add journey");
            System.out.println("2. List journeys");
            System.out.println("3. Filter journeys by the passenger type");
            System.out.println("4. Filter journeys by the starting zone");
            System.out.println("5. Filter journeys by the destination zone");
            System.out.println("6. Filter journeys by the journey date");
            System.out.println("7. Filter journeys by the time band");
            System.out.println("8. Remove a journey");
            System.out.println("9. Undo the last removed journey");
            System.out.println("10. Sort journeys by the fare");
            System.out.println("11. Daily summary");
            System.out.println("12. Category counts");
            System.out.println("13. Reset day");
            System.out.println("14. Display totals by passenger type");
            System.out.println("15. write all the journeys to a CSV file");
            System.out.println("16. read all the journeys from a CSV file");
            System.out.println("17. write all the profiles to a JSON file");
            System.out.println("18. Read all the profiles from a JSON file");
            System.out.println("19. Modify the base fare of 0 or many journeys by the admin");
            System.out.println("20. Exit");
            System.out.println("Choose option: ");

            int choice = 0;
            boolean validChoice = false;
            while (!validChoice) {
                try {
                    System.out.println("The choice:");
                    choice = sc.nextInt();
                    sc.nextLine();
                    validChoice = true;
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Choice must be an integer.");
                    sc.nextLine();
                }
            }

            switch (choice) {

                case 1:
                    int journeyId = 0;
                    boolean validJourneyId = false;

                    while (!validJourneyId) {
                        try {
                            System.out.println("Journey ID:");
                            journeyId = sc.nextInt();
                            sc.nextLine();
                            if (journey1.journeyIdExists(journeyId)) {
                                System.out.println("This Journey ID already exists. Please enter a unique ID.");
                            } else {
                                validJourneyId = true;
                            }
                        } catch (InputMismatchException e) {
                            System.out.println("Invalid input. Journey ID must be an integer.");
                            sc.nextLine();
                        }
                    }

                    int fromZone = 0;
                    boolean validFromZone = false;

                    while (!validFromZone) {
                        try {
                            System.out.println("From zone (1–5):");
                            fromZone = sc.nextInt();
                            validFromZone = true;
                        } catch (InputMismatchException e) {
                            System.out.println("Invalid input. Please enter a number.");
                            sc.nextLine();
                        }
                    }

                    while (fromZone <= 0 || fromZone > 5) {
                        System.out.println("Please enter a valid input for the starting zone (1 to 5): ");
                        fromZone = sc.nextInt();
                    }

                    int toZone = 0;
                    boolean validToZone = false;

                    while (!validToZone) {
                        try {
                            System.out.println("To zone (1–5):");
                            toZone = sc.nextInt();
                            validToZone = true;
                        } catch (InputMismatchException e) {
                            System.out.println("Invalid input. Please enter a number.");
                            sc.nextLine();
                        }
                    }

                    while (toZone <= 0 || toZone > 5) {
                        System.out.println("Please enter a valid input for the destination zone (1 to 5): ");
                        toZone = sc.nextInt();
                    }

                    sc.nextLine();

                    String passengerType = "";
                    boolean validPassengerType = false;

                    while (!validPassengerType) {
                        try {
                            System.out.println("Passenger type (Adult / Student / Child / Senior Citizen):");
                            passengerType = sc.nextLine().trim().toLowerCase();
                            while (!passengerType.equalsIgnoreCase("adult") &&
                                    !passengerType.equalsIgnoreCase("student") &&
                                    !passengerType.equalsIgnoreCase("child") &&
                                    !passengerType.equalsIgnoreCase("senior citizen")) {
                                System.out.println("Please enter a valid passenger type: ");
                                passengerType = sc.nextLine().trim().toLowerCase();
                            }
                            validPassengerType = true;
                        } catch (InputMismatchException e) {
                            System.out.println("Invalid input. Please enter a valid input for the passenger type: ");
                            sc.nextLine();
                        }
                    }

                    String timeBand = "";
                    boolean validTimeBand = false;

                    while (!validTimeBand) {
                        try {
                            System.out.println("Time band (Peak / Off-Peak):");
                            timeBand = sc.nextLine().trim().toLowerCase();
                            while (!timeBand.equalsIgnoreCase("peak") &&
                                    !timeBand.equalsIgnoreCase("off-peak")) {

                                System.out.println("Please enter peak or off-peak: ");
                                timeBand = sc.nextLine();
                            }
                            validTimeBand = true;
                        } catch (InputMismatchException e) {
                            System.out.println("Invalid input. Please enter a valid time band: ");
                            sc.nextLine();
                        }
                    }

                    // Convert String → Enum
                    CityRideDataset.TimeBand band;

                    if (timeBand.equalsIgnoreCase("peak")) {
                        band = CityRideDataset.TimeBand.PEAK;
                    } else {
                        band = CityRideDataset.TimeBand.OFF_PEAK;
                    }

                    double baseFare = CityRideDataset
                            .getBaseFare(fromZone, toZone, band)
                            .doubleValue();

                    LocalDate journeyDate = null;
                    boolean validDate = false;

                    while (!validDate) {
                        try {
                            System.out.println("Journey date (YYYY-MM-DD):");
                            String dateInput = sc.nextLine();
                            journeyDate = LocalDate.parse(dateInput);
                            validDate = true;
                        } catch (InputMismatchException e) {
                            System.out.println("Invalid input. Please enter a valid input for the date: ");
                            sc.nextLine();
                        } catch (Exception e) {
                            System.out.println("Invalid date format. Use YYYY-MM-DD.");
                        }
                    }

                    JourneyDetails journey = new JourneyDetails(
                            journeyId,
                            fromZone,
                            toZone,
                            timeBand,
                            passengerType,
                            baseFare,
                            journeyDate
                    );

                    journey1.addJourney(journey);
                    break;

                case 2:
                    journeys.displayJourneys();
                    break;

                case 3:
                    System.out.println("Passenger type to filter: ");
                    journeysFiltered.filterByPassengerType(sc.nextLine());
                    break;

                case 4:
                    System.out.println("Starting zone to filter: ");
                    journeysFiltered.filterByStartingZone(sc.nextInt());
                    break;
                case 5:
                    System.out.println("Destination zone to filter: ");
                    journeysFiltered.filterByDestinationZone(sc.nextInt());
                    break;

                case 6:
                    System.out.println("Journey date to filter: ");
                    String dateInput = sc.nextLine();
                    LocalDate dateOfJourney = LocalDate.parse(dateInput);
                    journeysFiltered.filterByJourneyDate(dateOfJourney);
                    break;
                case 7:
                    System.out.println("Time band to filter: ");
                    journeysFiltered.filterByTimeBand(sc.nextLine());
                    break;

                case 8:
                    System.out.println("Journey ID to remove: ");
                    removedJourney.removeJourneyById(sc.nextInt());
                    sc.nextLine();
                    break;

                case 9:
                    journey2.undoLastRemoval(removedJourney);
                    break;

                case 10:
                    sortedJourneys.sortJourneysByFare();
                    break;

                case 11:
                    journeysSummary.displayDailySummary();
                    break;

                case 12:
                    count.displayTimeBandCounts();
                    count.displayZoneCounts();
                    break;

                case 13:
                    System.out.println("Confirm reset (y/n): ");
                    journeysReset.resetJourneys(sc.nextLine().equalsIgnoreCase("y"));
                    break;

                case 14:
                    totals.recalculateTotals();
                    totals.displayTotalsByPassengerType();
                    break;

                case 15:
                    if (userRole == 1) {
                        JourneyFileWriter.writeStudentsToFile("journeys.csv", journeyList);
                    }
                    else {
                        System.out.println("The user cannot write the journeys to a CSV file");
                    }
                    break;

                case 16:
                    if (userRole == 1) {
                        List<JourneyDetails> imported = JourneyFileReader.readStudentsFromFile("journeys.csv");
                        journeyList.clear(); // Clears all the journeys in the journeyList.
                        journeyList.addAll(imported);
                        for (JourneyDetails j : journeyList) {
                            double discounted = journey1.applyDiscount(j, j.getBaseFare());
                            j.setDiscountedFare(discounted);
                        }
                        totals.recalculateTotals();
                        System.out.println("Journeys loaded from file.");
                    }
                    else {
                        System.out.println("The user cannot read the journeys from a CSV file");
                    }
                    break;

                case 17:
                    if (userRole == 1) {
                        profileFileWriter.saveProfiles();
                    }
                    else {
                        System.out.println("The user cannot write the profiles to a JSON file");
                    }
                    break;

                case 18:
                    if (userRole == 1) {
                        List<Profile> imported = profileFileReader.loadProfiles();
                        profiles.clear(); // Clears all the journeys in the journeyList.
                        profiles.addAll(imported);
                        for (Profile profile : profiles) {
                            profile.showProfile();
                        }
                        System.out.println("Journeys loaded from file.");
                    }
                    else {
                        System.out.println("The user cannot read the profiles from a JSON file");
                    }
                    break;

                case 19:
                    if (userRole == 2) {
                        if (admin.login(inputUserName, inputPassword)) {
                            admin.modifyBaseFare(config, configBaseFare,journey1, totals);
                        } else {
                            System.out.println("The admin cannot log in with this password and username to modify the base fare");
                        }
                    }
                    else {
                        System.out.println("The user is not an admin");
                    }
                    break;

                case 20:
                    running = false;
                    System.out.println("Exiting CityRide Lite.");
                    break;

                default:
                    System.out.println("Invalid option.");
            }
        }
        sc.close();
    }
}