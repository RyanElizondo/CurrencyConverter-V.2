import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Scanner;
import org.json.JSONObject;
import java.net.URL;

public class currencyConverter {
    public static void main(String[] args) throws IOException{

        HashMap<Integer, String> currencyCode = new HashMap<Integer, String>();

        //Default Currency Codes
        currencyCode.put(1, "USD");
        currencyCode.put(2, "CAD");
        currencyCode.put(3, "EUR");
        currencyCode.put(4, "HKD");
        currencyCode.put(5, "INR");

        int from, to;
        String convertFrom, convertTo, convertFromString, convertToString;
        Double amount;
        boolean continueProgram = true;


        Scanner readChoice = new Scanner(System.in); //allows you to read user input

        while(continueProgram) { //while continueProgram is true keep running

            //Intro
            System.out.println("Welcome to Squish Convert V.2!");

            //currency you wish to convert from
            System.out.println("Enter currency you wish to covert from: ");
            System.out.println("1:USD \t 2:CAD \t 3:EUR \t 4:HKD \t 5:INR \t 6:Other" );
            from = readChoice.nextInt();
            while (from < 1 || from > 6) { //checks for valid input and allows user to correct a typo
                System.out.println("Please enter a valid currency (1-5)");
                System.out.println("1:USD \t 2:CAD \t 3:EUR \t 4:HKD \t 5:INR \t 6:Other");
                from = readChoice.nextInt();
            }
            if(from == 6){ //allows user to input currency of choice if the option is not available in the default menu
                System.out.println("Enter the 3 character currency code you will like to convert from (Must be in all CAPS)");
                convertFromString = readChoice.next(); //takes string inputted by user
                convertFrom = convertFromString; //assigns string to convertFrom
            }
            else{
                convertFrom = currencyCode.get(from); //if using default codes it assigns convertFrom to the value of
                //the key given
            }



            //currency you wish to convert too
            System.out.println("Enter currency you wish to convert to: ");
            System.out.println("1:USD \t 2:CAD \t 3:EUR \t 4:HKD \t 5:INR \t 6:Other");
            to = readChoice.nextInt();
            while (to < 1 || to > 6) {
                System.out.println("Please enter a valid currency (1-5)");
                System.out.println("1:USD \t 2:CAD \t 3:EUR \t 4:HKD \t 5:INR");
                to = readChoice.nextInt();
            }
            if(to == 6){
                System.out.println("Enter the 3 character currency code you will like to convert from (Must be in all CAPS)");
                convertToString = readChoice.next();
                convertTo = convertToString;
            }
            else{
                convertTo = currencyCode.get(to);
            }

            //Amount you wish to convert
            System.out.println("Enter amount you wish to convert:");
            amount = readChoice.nextDouble();
            while (amount < 0) {
                System.out.println("Amount can not be negative. Please re-enter amount:");
                amount = readChoice.nextDouble();
            }

            sendHttpGETRequest(convertFrom, convertTo, amount); //http request

            System.out.println(" ");
            System.out.println("Do you wish to convert more currency? 1:Yes or Any other integer:No?");
            if(readChoice.nextInt() != 1){ //if user inputs 2 or any other integer program will stop
                continueProgram = false;
            }

        }

        System.out.println("Thanks for using Squish Convert V.2 :)"); //Goodbye message

    }

    private static void sendHttpGETRequest(String convertFrom, String convertTo, double amount) throws IOException {

        DecimalFormat f = new DecimalFormat("00.00"); //adjusts conversion results to this format
        String GET_URL = "https://v6.exchangerate-api.com/v6/7d20d4932f7305a978100fd5/pair/" + convertFrom + "/" + convertTo; //API url in string format
        //establishes connection to api
        URL url = new URL(GET_URL); //creates instance of a URL from the string representation shown above
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        //HttpURLConnection - retrieve information of any HTTP URL such as header information, status code, response code etc
        //url.openConnection - opens the connection to specified URL and URLConnection instance that represents a connection to the remote object referred by the URL
        httpURLConnection.setRequestMethod("GET"); //Used to set the request method. Default is GET
        int responseCode = httpURLConnection.getResponseCode(); //Used to retrieve the response status from server.

        if(responseCode == HttpURLConnection.HTTP_OK){ //connection successful
            BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            //BufferedReader - Reads text from a character-input stream, buffering characters so as to provide
            // for the efficient reading of characters, arrays, and lines. The buffer size may be specified, or the default
            // size may be used. The default is large enough for most purposes.

            //InputStreamReader - An InputStreamReader is a bridge from byte streams to character streams: It reads bytes and decodes them into characters using a
            // specified charset . The charset that it uses may be specified by name or may be given explicitly, or the platform's default charset may be accepted.

            //getInputStream - getInputStream() method gets the input stream of the subprocess. The stream obtains data piped from the standard output stream of
            // the process represented by this Process object.

            String inputLine;
            StringBuffer response = new StringBuffer();  //StringBuffer - StringBuffer is a peer class of String that provides much of the functionality of strings.
            //The string represents fixed-length, immutable character sequences while StringBuffer represents growable and writable character sequences

            while((inputLine = in.readLine()) != null){ //while there is still stuff to read keep reading it
                response.append(inputLine);
            }in.close();

            JSONObject obj = new JSONObject(response.toString()); //creates new JSON object
            Double exchangeRate = obj.getDouble("conversion_rate"); //grabs double value under the key conversion_rate in JSON file
            System.out.println();
            System.out.println(f.format(amount) + " " + convertFrom + " = " + f.format(amount*exchangeRate) + " " + convertTo); //conversion result

        }
        else{
            System.out.println("Get request failed"); //if anything fails this is the output
        }
    }
}
