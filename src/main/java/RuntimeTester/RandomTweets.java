package RuntimeTester;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//TODO: Improve keyword searching

/**
 * <bold>Tweet Randomizer</bold>
 * <p>
 * An extension of the java.util.random which adds useful methods for generation of objects relating to assignment 4 of COMP250 at McGill University.
 *
 * @author Sasha Aleshchenko
 * @version 1.0
 */
public class RandomTweets extends Random {
    //URLs
    private static final String namesURL = "https://raw.githubusercontent.com/TheBigSasha/COMP250_Final_Debugger/fb8c2f8b65e7d90a41f7320ed044d3772db79555/src/COMP250_A4_W2020/Put%20Your%20java%20files%20here.txt";
    private final File songLyrics = new File("src/COMP250_A4_W2020/songdata.csv");
    private static final String songDataURL = "https://raw.githubusercontent.com/TheBigSasha/COMP250_Final_Debugger/fb8c2f8b65e7d90a41f7320ed044d3772db79555/src/COMP250_A4_W2020/songdata.csv";
    private final ArrayList<String> beeMovieScript = new ArrayList<>();
    private final ArrayList<String> songLyric = new ArrayList<>();
    private final ArrayList<String> pastUsers = new ArrayList<>();
    private static final String mostCommonWordsURL = "https://raw.githubusercontent.com/first20hours/google-10000-english/master/20k.txt";
    private static final String beeMovieURL = "https://web.njit.edu/~cm395/theBeeMovieScript/";
    //Parameters
    private static final int songFactor = 3;
    private static final int SEARCH_RECURSION_LIMIT = 2200;
    //Databases
    private final ArrayList<String> names = new ArrayList<String>();
    private final ArrayList<String> mostCommonWords = new ArrayList<>();
    //Local Files
    private File Lastnames = new File("src/COMP250_A4_W2020/Put Your java files here.txt");

    /**
     * An enhanced randomizer for Comp 250 A4 Winter 2020 with specified seed
     */
    public RandomTweets(long seed) {
        super(seed);
        initializeDatabases();
    }

    /**
     * An enhanced randomizer for Comp 250 A4 Winter 2020
     */
    public RandomTweets() {
        super();
        initializeDatabases();
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in); //TODO: Add user menu to get random lines
        RandomTweets test = new RandomTweets();
        System.out.println("I can sing you a song with the command 'sing' and I can sing about something specific if you include 'about:*your topic*'");

        while (true) {
            System.out.println("Enter 'name' for a name, 'user' for a username, 'lyric' for a lyric, 'KW:' followed by any word for a lyric with a keyword, or 'bee movie' for a line from the bee movie. Type 'tweet' for a tweet, 'at' for a tweet with at, and 'date' for a date. Not that kind, you cheeky bugger ;)");
            String choice = scanner.nextLine();
            if (choice.toLowerCase().contains("kw:")) {
                System.out.println("Getting lyric with keyword: " + choice.toLowerCase().replace("kw:", "").trim().strip());
                System.out.println(test.nextSongLyricWithKeyword(choice.toLowerCase().replace("kw:", "").trim().strip()));
            } else if (choice.toLowerCase().contains("sing")) {
                if (choice.contains("about:")) {
                    int max = 10 + test.nextInt(25);
                    for (int i = 0; i < max; i++) {
                        System.out.println(test.nextSongLyricWithKeyword(choice.substring(choice.lastIndexOf(":") + 1).trim()));
                    }
                } else {
                    int max = 10 + test.nextInt(25);
                    for (int i = 0; i < max; i++) {
                        System.out.println(test.nextSongLyric());
                    }
                }
            } else if (choice.toLowerCase().contains("bee")) {
                System.out.println(test.nextBeeMovieLine());
            } else if (choice.toLowerCase().contains("song") || choice.toLowerCase().contains("lyric")) {
                System.out.println(test.nextSongLyric());
            } else if (choice.toLowerCase().contains("name")) {
                System.out.println(test.nextName());
            } else if (choice.toLowerCase().contains("date")) {
                System.out.println(test.nextDate());
            } else if (choice.toLowerCase().contains("tweet")) {
                //System.out.println(test.nextTweet());
            } else if (choice.toLowerCase().contains("user")) {
                System.out.println(test.nextUserName());
            }/* else if (choice.toLowerCase().contains("at")) {
                System.out.println(test.nextTweet(true));
            }*/
        }
    }

    private void initializeDatabases() {
        System.out.println("[RandomTweets / InitializeDatabases] Starting database initialization, this may take a bit.");
        int passed = 4;
        try {
            initializeBeeMovie();
        } catch (IOException e) {
            System.out.println("[RandomTweets / InitializeDatabases] Failed to fetch bee movie script from internet. Check your connection.");
            passed -= 1;
        }
        try {
            initializeSongLyrics();
        } catch (IOException e) {
            System.out.println("[RandomTweets / InitializeDatabases] Failed to get song lyrics from internet. Check your connection.");
            passed -= 1;
        }
        try {
            initializeNames();
        } catch (IOException e) {
            System.out.println("[RandomTweets / InitializeDatabases] Failed to get  names from internet. Check your connection.");
            passed -= 1;
        }
        try {
            initializeCommonWords();
        } catch (IOException e) {
            passed -= 1;
            System.out.println("[RandomTweets / InitializeDatabases] Failed to get common words from internet. Check your connection.");
        }

        System.out.println("[RandomTweets / InitializeDatabases] " + passed + "/4 databases initialized!");
    }

    private void initializeNames() throws IOException {
        try {
            Scanner scanner = new Scanner(Lastnames);
            while (scanner.hasNextLine()) {
                names.add(scanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            try {
                Lastnames = new File("supportfiles/last_names.all.txt");
                Scanner scanner = new Scanner(Lastnames);
                while (scanner.hasNextLine()) {
                    names.add(scanner.nextLine());
                }
            } catch (FileNotFoundException f) {
                try {
                    Lastnames = new File("last_names.all.txt");
                    Scanner scanner = new Scanner(Lastnames);
                    while (scanner.hasNextLine()) {
                        names.add(scanner.nextLine());
                    }
                } catch (FileNotFoundException g) {
                    try {
                        Lastnames = new File("Put Your java files here.txt");
                        Scanner scanner = new Scanner(Lastnames);
                        while (scanner.hasNextLine()) {
                            names.add(scanner.nextLine());
                        }
                    } catch (FileNotFoundException h) {
                        // Make a URL to the web page
                        URL names = new URL(namesURL);
                        // Get the input stream through URL Connection
                        URLConnection con = names.openConnection();
                        InputStream is = con.getInputStream();


                        BufferedReader br = new BufferedReader(new InputStreamReader(is));

                        String line = null;
                        while ((line = br.readLine()) != null) {
                            if (line.endsWith(";") || line.strip().startsWith("#") || line.startsWith("<") || line.startsWith("/") || line.startsWith(".") || line.startsWith("{") || line.startsWith("}") || line.isBlank()) {

                            } else {
                                if (line.startsWith("- ")) {
                                    line = line.replace("- ", "");
                                }
                                this.names.add(line);
                                //System.out.println(line);
                            }
                        }
                    }
                    }
                }
            }
        }

    private void initializeBeeMovie() throws IOException {
        // Make a URL to the web page
        URL beeMovie = new URL(beeMovieURL);
        // Get the input stream through URL Connection
        URLConnection con = beeMovie.openConnection();
        InputStream is = con.getInputStream();


        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        String line = null;
        while ((line = br.readLine()) != null) {
            if (line.endsWith(";") || line.strip().startsWith("#") || line.startsWith("<") || line.startsWith("/") || line.startsWith(".") || line.startsWith("{") || line.startsWith("}") || line.isBlank()) {

            } else {
                if (line.startsWith("- ")) {
                    line = line.replace("- ", "");
                }
                beeMovieScript.add(line);
                //System.out.println(line);
            }
        }
    }

    private void initializeCommonWords() throws IOException {
        // Make a URL to the web page
        URL names = new URL(mostCommonWordsURL);
        // Get the input stream through URL Connection
        URLConnection con = names.openConnection();
        InputStream is = con.getInputStream();


        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        String line = null;
        while ((line = br.readLine()) != null) {
            if (line.endsWith(";") || line.strip().startsWith("#") || line.startsWith("<") || line.startsWith("/") || line.startsWith(".") || line.startsWith("{") || line.startsWith("}") || line.isBlank()) {

            } else {
                if (line.startsWith("- ")) {
                    line = line.replace("- ", "");
                }
                mostCommonWords.add(line);
                //System.out.println(line);
            }
        }
    }

    private void initializeSongLyrics() throws IOException {
        try {
            Scanner scanner = new Scanner(songLyrics);
            scanner.useDelimiter(",");
            int counter = 0;
            while (scanner.hasNext()) {
                String cur = scanner.next();
                if (counter % 3 == 0 && !cur.isBlank() && !cur.startsWith("/")) {
                    //System.out.println("adding a lyric to the lyrics list");

                    songLyric.addAll(Arrays.asList(cur.replaceAll("Chorus: ", "").replaceAll("\n\n", "").replaceAll("\"", "").split("\n")));
                }
                counter++;
            }
        } catch (FileNotFoundException e) {
            //System.out.println("Song Lyrics file not found");
            // Make a URL to the web page
            URL songs = new URL(songDataURL);
            // Get the input stream through URL Connection
            URLConnection con = songs.openConnection();
            InputStream is = con.getInputStream();


            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            String line = null;
            int counter = 0;
            while ((line = br.readLine()) != null) {
                if (counter % songFactor == 0 && !line.isBlank() && !line.startsWith("/")) {
                    //System.out.println("adding a lyric to the lyrics list");

                    songLyric.addAll(Arrays.asList(line.replaceAll("Chorus: ", "").replaceAll("\n\n", "").replaceAll("\"", "").split("\n")));
                }
                counter++;
            }
        }
    }

    private int nextRandInt(int i) {
        return nextInt(Math.abs(i));
    }

    private int nextBound(int bound) {
        return Math.abs(nextRandInt(bound) - 1);
    }

    /**
     * If databases are properly initialized, generates a name which is human readable. Else, a random String of characters.
     *
     * @return a name
     */
    public String nextName() {
        try {
            int numNames = nextRandInt(names.size());
            String output = names.get(numNames);
            names.remove(numNames);
            return output;

        } catch (Exception e) {

            int nameLength = nextRandInt(11);
            nameLength += 1;
            char[] nameOutChar = new char[nameLength];
            for (int i = 0; i < nameLength; i++) {
                if (nameLength <= 4) {
                    nameOutChar[i] = (char) nextRandInt(18500);
                    nameOutChar[i] += (char) nextRandInt(5000);
                } else {
                    int nameNumber = nextRandInt(90 - 65) + 65;
                    nameOutChar[i] = (char) nameNumber;
                    if (i == nameLength - 3) {
                        nameOutChar[i + 1] = (char) 85;
                    }
                }
            }
            //System.out.println(nameOut);
            return String.valueOf(nameOutChar);
        }


    }

    /**
     * Returns a vanilla tweet (no at or keyword)
     *
     * @return a tweet.
     */
    /*public Tweet nextTweet() {
        String name = nextUserName();
        pastUsers.add(name);
        return new Tweet(name, nextDate(), nextContent());
    }

    *//**
     * Generates a tweet which at's a user.
     *
     * @param a to at or not to at
     * @return a Tweet which may at a particular user.
     *//*
    public Tweet nextTweet(boolean a) {
        try {
            String name = nextUserName();
            String toAt = pastUsers.get(nextInt(pastUsers.size() - 1));
            pastUsers.add(name);
            return new Tweet(name, nextDate(), nextContent(toAt));
        } catch (Exception e) {
            return nextTweet();
        }
    }*/

    /**
     * Gets a bit of content
     *
     * @return a song lyric, bee movie quote, or random string
     */
    public String nextContent() {
        boolean choice = nextBoolean();
        if (choice = true) {
            return nextBeeMovieLine();
        } else {
            return nextSongLyric();
        }
    }

    /**
     * Gets a bit of content with a certain keyword which at's a name.
     *
     * @param toAt whom this lyric is atting
     * @return a song lyric, bee movie quote, or random string which at's the specified name
     */
    public String nextContent(String toAt) {
        return nextSongLyric(toAt);
    }

    /**
     * Gets a bit of content with a certain keyword which at's a name.
     *
     * @param toAt    whom this lyric is atting
     * @param keyword a keyword
     * @return a song lyric, bee movie quote, or random string with the specified keyword and at's the specified name
     */
    public String nextContent(String toAt, String keyword) {
        return nextSongLyric(toAt, keyword);
    }

    /**
     * Gets a song lyric
     *
     * @return a song lyric with the specified keyword.
     */
    public String nextSongLyric() {
        try {
            int toGet = nextBound(songLyric.size());
            String lyric = songLyric.get(toGet);
            if (lyric.isEmpty() || lyric.isBlank() || lyric.length() < 10) {
                return nextSongLyric();
            } else {
                return lyric;
            }
        } catch (Exception e) {
            //return "ERROR! : size of databse of Lyrics is " + songLyric.size();
            return nextBeeMovieLine();
        }
    }

    /**
     * Gets a song lyric which at's at specific person.
     *
     * @param toAt someone to at
     * @return a song lyric with the specified at.
     */
    public String nextSongLyric(String toAt) {
        try {
            int toGet = nextBound(songLyric.size());
            String lyric = songLyric.get(toGet);
            if (lyric.isEmpty() || lyric.isBlank() || lyric.length() < 10 || !lyric.contains("you ")) {
                return nextSongLyric(toAt);
            } else {
                return lyric.toLowerCase().replaceFirst("you ", "@" + toAt + " ");
            }
        } catch (Exception e) {
            //return "ERROR! : size of databse of Lyrics is " + songLyric.size();
            return nextBeeMovieLine();
        }
    }

    /**
     * Gets a song lyric which contains a specified keyword.
     *
     * @param keyWord a word which the specified lyric must contain
     * @return a song lyric with the specified keyword.
     */
    public String nextSongLyricWithKeyword(String keyWord) {
        try {
            int toGet = nextBound(songLyric.size());
            String lyric = songLyric.get(toGet);
            if (lyric.isEmpty() || lyric.isBlank() || lyric.length() < 10 || !lyric.toLowerCase().contains(keyWord)) {
                return nextSongLyricWithKeyword(keyWord, 1);
            } else {
                return lyric;
            }
        } catch (Exception e) {
            //System.out.println("[RandomTweets / Lyric Keyword Finder] Failed to find the right lyric, so I added the word to a line from the bee movie.");
            return nextBeeMovieLine() + " " + keyWord;
        }
    }

    /**
     * Gets a song lyric with a certain keyword which at's a name.
     *
     * @param toAt    whom this lyric is atting
     * @param keyWord a keyword
     * @return a song lyric with the specified keyword and at's the specified name
     */
    public String nextSongLyric(String toAt, String keyWord) {
        try {
            String lyric = nextSongLyricWithKeyword(keyWord);
            if (lyric.isEmpty() || lyric.isBlank() || lyric.length() < 10 || !(lyric.contains("you ") || lyric.contains(" you"))) {
                //System.out.println("[RandomTweets / At&Keyword Generator] Failed to replace 'you' on line " + lyric);
                return nextSongLyric(toAt, keyWord);
            } else {
                //System.out.println("[RandomTweets / At&Keyword Generator] Returning line which matches all conditions " + lyric);
                return lyric.toLowerCase().replaceFirst("you", "@" + toAt);
            }
        } catch (Exception e) {
            //return "ERROR! : size of databse of Lyrics is " + songLyric.size();
            //System.out.println("[RandomTweets / At&Keyword Generator] Failed to find good line for keyword " + keyWord + " which is atable.");
            return nextSongLyricWithKeyword(keyWord) + " @" + toAt;
        }
    }

    /**
     * Gets a song lyric which contains a specified keyword.
     *
     * @param keyWord       a word which the specified lyric must contain
     * @param numRecursions how many times this method has been recursed
     * @return a song lyric with the specified keyword.
     */
    private String nextSongLyricWithKeyword(String keyWord, int numRecursions) {
        try {
            int toGet = nextBound(songLyric.size());
            String lyric = songLyric.get(toGet);
            String regex = "\\b" + keyWord.toLowerCase() + "\\b";
            Pattern pattern = Pattern.compile(regex.toLowerCase());
            Matcher matcher = pattern.matcher(lyric.toLowerCase());
            if (lyric.isEmpty() || lyric.isBlank() || lyric.length() < 10 || !matcher.find()) {
                if (numRecursions <= songLyric.size() - 1 && numRecursions <= SEARCH_RECURSION_LIMIT) {
                    return nextSongLyricWithKeyword(keyWord, numRecursions + 1);
                } else {
                    //System.out.println("[RandomTweets / Lyric Keyword Finder] Failed to find the right lyric, so I added the word to a line from the bee movie.");
                    return nextBeeMovieLine() + " " + keyWord;
                }
            } else {
                return lyric;
            }
        } catch (Exception e) {
            //return "ERROR! : size of databse of Lyrics is " + songLyric.size();
            //System.out.println("[RandomTweets / Lyric Keyword Finder] Failed to find the right lyric, so I added the word to a line from the bee movie.");
            return nextBeeMovieLine() + " " + keyWord;
        }
    }

    /**
     * Gets a line from the Bee Movie.
     *
     * @return a line from the Bee Movie.
     */
    public String nextBeeMovieLine() {   //TODO: bee movie iterator
        try {
            int toGet = nextBound(beeMovieScript.size());
            return beeMovieScript.get(toGet);
        } catch (Exception e) {
            //return "ERROR! : size of databse of Bee Movie is " + beeMovieScript.size();
            return nextName();
        }
    }

    /**
     * Generates a date matching the format in the teacher supplied debugger
     *
     * @return a date
     */
    public String nextDate() {
        int year = 2009 + nextInt(11);
        int month = 1 + nextInt(11);
        int day = 1 + nextInt(27);
        int hour = nextInt(23);
        int minute = nextInt(59);
        int second = nextInt(59);
        StringBuilder sb = new StringBuilder();
        sb.append(year);
        sb.append("-");
        if (month < 10) {
            sb.append("0" + month);
        } else {
            sb.append(month);
        }
        sb.append("-");
        if (day < 10) {
            sb.append("0" + day);
        } else {
            sb.append(day);
        }
        sb.append(" ");
        if (hour < 10) {
            sb.append("0" + hour);
        } else {
            sb.append(hour);
        }
        sb.append(":");
        if (minute < 10) {
            sb.append("0" + minute);
        } else {
            sb.append(minute);
        }
        sb.append(":");
        if (second < 10) {
            sb.append("0" + second);
        } else {
            sb.append(second);
        }
        return sb.toString();
    }

    /**
     * Generates a username matching the format in the teacher supplied debugger (USER_*random byte*)
     *
     * @return a user name matching the teacher's specification.
     */
    public String nextUserName() {
        StringBuilder sb = new StringBuilder();
        sb.append("USER_");
        byte[] code = new byte[1];
        nextBytes(code);
        String endCode = code.toString().replace("[B@", "");
        sb.append(endCode);
        return sb.toString();
    }
/*

    */
/**
     * Generates an array of randomized tweets to user specification.
     *
     * @param howMany the number of tweets to generate
     * @param at      to at or not to at
     * @return an array of Tweet which matches the above conditions
     *//*

    public Tweet[] nextTweets(int howMany, boolean at) {

        Tweet[] output = new Tweet[howMany];
        for (int i = 0; i < output.length; i++) {
            if (at) {
                output[i] = nextTweet(true);
            } else {
                output[i] = nextTweet();
            }
        }
        return output;
    }
*/
/*

    */
/**
     * Generates a randomized tweet whose content contains a keyword and which at's a user.
     *
     * @param keyword word this tweet must contain
     * @param toAt    whom this tweet must at
     * @return a tweet that matches the conditions specified
     *//*

    public Tweet nextTweet(String keyword, String toAt) {
        String name = nextUserName();
        pastUsers.add(name);
        return new Tweet(name, nextDate(), nextContent(toAt, keyword));
    }

    */
/**
     * Generates a randomized tweet whose content contains a keyword.
     *
     * @param keyword A word this tweet must contain
     * @return a tweet that matches the conditions specified
     *//*

    public Tweet nextTweet(String keyword) {
        String name = nextUserName();
        pastUsers.add(name);
        return new Tweet(name, nextDate(), nextContent(keyword));
    }
*/

    /**
     * Gets a bit of content for a tweet which has a keyword.
     *
     * @param keyword a word this String must contain
     * @return a String, probably a song lyric, with the keyword
     */
    public String nextKeywordContent(String keyword) {
        return nextSongLyricWithKeyword(keyword);
    }

/*
    */
/**
     * Generates an array of Tweet of a specified size where each Tweet matches the following conditions:
     * <p>
     * Contains a common keyword <code>keyword</code>
     * If at = true: AT's another user (who has sent a tweet before)
     *
     * @param howMany The number of tweets to generate
     * @param at      Whether or not to include at's
     * @param keyword A keyword which each tweet must contain
     * @return an array of Tweet matching the above conditions
     *//*

    public Tweet[] nextTweets(int howMany, boolean at, String keyword) {
        Tweet[] output = new Tweet[howMany];
        for (int i = 0; i < output.length; i++) {
            if (at) {
                Tweet toAdd = nextTweet(keyword, getAttable());
                output[i] = toAdd;
                //System.out.println("Generated tweet is: " + toAdd);
            } else {
                output[i] = nextTweet(keyword);
            }
        }
        return output;
    }
*/

    /**
     * If there have been tweets made before, return a user who has tweeted. Else, return a newly generated username.
     *
     * @return a username
     */
    public String getAttable() {
        try {
            return pastUsers.get(nextInt(pastUsers.size() - 1));
        } catch (Exception e) {
            String newname = nextUserName();
            pastUsers.add(newname);
            return newname;
        }
    }

    /**
     * Generates stop words, by first querying the 10,000 most commonly used English words and then by randomizing words.
     *
     * @param length number of words
     * @return an ArrayList of words
     */
    public ArrayList<String> nextStopWords(int length) {
        if (mostCommonWords.size() > length) {
            return new ArrayList<String>(mostCommonWords.subList(0, length));
        } else {
            ArrayList<String> output = new ArrayList<>(mostCommonWords);
            if (length < output.size() + names.size()) {
                output.addAll(names.subList(0, length - mostCommonWords.size()));
            } else {
                output.addAll(names);
                for (int i = output.size(); i < length; i++) {
                    output.add(nextBeeMovieLine().split(" ")[0]);
                }
            }
            return output;
        }
    }
/*
    public Twitter nextTwitter(int numTweets, int numStopWords) {
        ArrayList<String> toAddStopWords;
        toAddStopWords = nextStopWords(numStopWords);
        ArrayList<Tweet> toAddTweets;
        toAddTweets = new ArrayList<>(Arrays.asList(nextTweets(numTweets, true)));
        return new Twitter(toAddTweets, toAddStopWords);
    }*/

    public String sing(String keyword) {
        StringBuilder output = new StringBuilder();
        int max = 10 + this.nextInt(25);
        for (int i = 0; i < max; i++) {
            output.append(this.nextSongLyricWithKeyword(keyword)).append("\n");
        }
        return output.toString();
    }

    public String sing() {
        StringBuilder output = new StringBuilder();
        int max = 10 + this.nextInt(25);
        for (int i = 0; i < max; i++) {
            output.append(this.nextSongLyric()).append("\n");
        }
        return output.toString();
    }

  /*  private ArrayList<Tweet> tweetsOf(ArrayList<String> strings) {
        ArrayList<Tweet> output = new ArrayList<>();
        for (String s : strings) {
            output.add(new Tweet(nextUserName(), nextDate(), s));
        }
        return output;
    }*/

  /*  public String nextTrend(int i, int stopWordFator) {
        Twitter t;
        switch (i) {
            case 0:
                t = new Twitter(tweetsOf(beeMovieScript), nextStopWords(beeMovieScript.size() / (stopWordFator + 1)));
                break;
            case 1:
                TwitterBenchmark tBM = new TwitterBenchmark(this);
                if (stopWordFator > 50) {
                    ArrayList<String> stopWords = new ArrayList<String>(tBM.getProfStopWords());
                    stopWords.addAll(nextStopWords(stopWordFator * 70));
                    t = new Twitter(tBM.getProfTweets(), stopWords);
                } else {
                    t = new Twitter(tBM.getProfTweets(), tBM.getProfStopWords());
                }
                break;
            case 2:
                t = new Twitter(tweetsOf(songLyric), nextStopWords(songLyric.size() / (stopWordFator * 100 + 1)));
                break;
            case 3:
                t = new Twitter(tweetsOf(new ArrayList<>(songLyric.subList(0, 98000))), nextStopWords(songLyric.size() / (stopWordFator * 50 + 1)));
                break;
            default:
                t = new Twitter(tweetsOf(beeMovieScript), nextStopWords(beeMovieScript.size() / (stopWordFator + 1)));
                break;
        }
        StringBuilder output = new StringBuilder();
        ArrayList<String> trending = t.trendingTopics();
        int counter = 0;
        for (String s : trending) {
            if (counter > 100) break;
            output.append(s).append("\n");
            counter++;
        }
        return output.toString();
    }

    public String nextTrend(String URI, int stopWordFator) throws IOException {
        URL url = new URL(URI);
        URLConnection con = url.openConnection();
        InputStream is = con.getInputStream();
        ArrayList<String> linesFromSite = new ArrayList<>(parseHTML(new BufferedReader(new InputStreamReader(is))));
        Twitter t = new Twitter(tweetsOf(linesFromSite), nextStopWords(linesFromSite.size() / (stopWordFator + 1)));
        StringBuilder output = new StringBuilder();
        ArrayList<String> trending = t.trendingTopics();
        int counter = 0;
        for (String s : trending) {
            if (counter > 100) break;
            output.append(s).append("\n");
            counter++;
        }
        return output.toString();
    }*/

    private List<String> parseHTML(Reader reader) throws IOException {
        List<String> lines = HTMLUtils.extractText(reader);
        return lines;
    }

    private static class HTMLUtils {
        private HTMLUtils() {
        }

        public static List<String> extractText(Reader reader) throws IOException {
            final ArrayList<String> list = new ArrayList<String>();

            ParserDelegator parserDelegator = new ParserDelegator();
            HTMLEditorKit.ParserCallback parserCallback = new HTMLEditorKit.ParserCallback() {
                public void handleText(final char[] data, final int pos) {
                    list.add(new String(data));
                }

                public void handleStartTag(HTML.Tag tag, MutableAttributeSet attribute, int pos) {
                }

                public void handleEndTag(HTML.Tag t, final int pos) {
                }

                public void handleSimpleTag(HTML.Tag t, MutableAttributeSet a, final int pos) {
                }

                public void handleComment(final char[] data, final int pos) {
                }

                public void handleError(final java.lang.String errMsg, final int pos) {
                }
            };
            parserDelegator.parse(reader, parserCallback, true);
            return list;
        }

        public final void main(String[] args) throws Exception {
            FileReader reader = new FileReader("java-new.html");
            List<String> lines = HTMLUtils.extractText(reader);
            for (String line : lines) {
                System.out.println(line);
            }
        }
    }
}
