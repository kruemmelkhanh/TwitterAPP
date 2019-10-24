/**
 * A Twitter APP that uses the twitter API to find tweet about a specific hashtag
 * and how many people are using the hashtag.
 *
 * @author Quoc-Khanh Truong
 * @version 1.0
 */
package twitterapp;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

public class App {
    private JButton search;
    private JPanel panel1;
    private JLabel count;
    private JLabel userNum;
    private JTextField textField1;
    private JButton clear;

    HashSet<String> user = new HashSet<>();


    public App() {


        search.addActionListener(new ActionListener() {

            public Date getTwitterDate(String date) throws ParseException {
                /**
                 Takes the Twitter date format and change it so getTime() can be used
                 */
                final String TWITTER = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TWITTER);
                simpleDateFormat.setLenient(true);
                return simpleDateFormat.parse(date);
            }

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                /**
                 * Setting up the Auth for the Twitter API
                 */
                ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
                configurationBuilder.setJSONStoreEnabled(true);
                configurationBuilder.setDebugEnabled(true).setOAuthConsumerKey("f7hqx9AAktnEW4WY91Ic3OEjc")
                        .setOAuthConsumerSecret("qeKRwuKRGCKLIYuL8qJmRj4BjD53RHoJw5eV7SttLpnphBMsos")
                        .setOAuthAccessToken("1186919571464343552-qgzv5Paxf4t5GfM7oHtY2FdSb0qnHP")
                        .setOAuthAccessTokenSecret("ZciHnBtmvQQubBXfPC3YmhO8fmv7BClWTgU3VkKFDcLiq");

                TwitterFactory ft = new TwitterFactory(configurationBuilder.build());
                twitter4j.Twitter twitter = ft.getInstance();

                boolean timeCheck = true;
                int tweetCount = 0;
                long nowTime = new Date().getTime();
                long endTime = nowTime - 300000;
                String searchText = textField1.getText();
                QueryResult result;

                /**
                 * To check if the text input is empty to avoid an empty call to the API
                 */
                if (searchText.equals("")) {
                    searchText = "IoT";
                }

                Query query = new Query("#" + searchText);
                query.setCount(100);


                try {
                    while (timeCheck) {
                        result = twitter.search(query);
                        List<Status> tweets = result.getTweets();
                        for (Status tweet : tweets) {
                            /**
                             * Parse Status to JSON
                             * Get time of the tweet to compare if its in the valid
                             */
                            String rawJSON = TwitterObjectFactory.getRawJSON(tweet);
                            JSONObject JSON_complete = new JSONObject(rawJSON);
                            String tweetDate = JSON_complete.getString("created_at");
                            long tweetDateInMilliseconds = getTwitterDate(tweetDate).getTime();

                            if (tweetDateInMilliseconds > endTime) {
                                user.add(JSON_complete.getString("user"));
                                tweetCount++;
                            } else {
                                timeCheck = false;
                            }

                        }
                    }
                    String lastCount = (tweetCount + " tweets in the last 5min about #" + searchText);
                    count.setText(lastCount);
                    String userNumber = (user.size() + " Users are talking about #" + searchText);
                    userNum.setText(userNumber);
                    Thread.sleep(500);
                } catch (TwitterException | ParseException | InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });


        clear.addActionListener(new ActionListener() {
            /**
             Clearing user count and textfield
             */

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                user.clear();
                textField1.setText("");
                count.setText("");
                userNum.setText("");

            }
        });
    }


    public static void main(String[] args) {
        /**
         * General options for the window of the APP
         */
        JFrame frame = new JFrame("MyApp");
        frame.setContentPane(new App().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setSize(600, 450);
        frame.setVisible(true);
    }

}
