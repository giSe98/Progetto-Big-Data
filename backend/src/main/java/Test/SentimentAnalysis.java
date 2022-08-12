package Test;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

    public class SentimentAnalysis {
        private ArrayList<String> tweets;
        private String tweet;

        public SentimentAnalysis(ArrayList<String> tweets){
            this.tweets=tweets;
        }

        public SentimentAnalysis(){

        }

        public String analysis(String text){
            String sentiment="";
            StanfordCoreNLP stanfordCoreNLP = Pipeline.getPipeline();
            CoreDocument coreDocument = new CoreDocument(text);
            stanfordCoreNLP.annotate(coreDocument);
            List<CoreSentence> sentences = coreDocument.sentences();
            for(CoreSentence sentence : sentences) {
                sentiment = sentence.sentiment();
            }
            return sentiment;
        }

        public static void main(String[] args) throws IOException, InterruptedException {
//            String sentiment="";
//            StanfordCoreNLP stanfordCoreNLP = Test.Pipeline.getPipeline();
//            //String text = "Oh dear So #novax is now under investigation by his own country *and* Spain, and Canada, the US and others have taken note of the Australian brouhaha... I suspect his 'oh, I'm unvaxxed but I'm safe' shpiel is going to become a *lot* harder in the future.";
//            String text = "When mfs who got the vaccine sicker than the ones who didn't get it";
//            //String text = "I don’t understand the hype about the vaccine";
//            CoreDocument coreDocument = new CoreDocument(text);
//            stanfordCoreNLP.annotate(coreDocument);
//            List<CoreSentence> sentences = coreDocument.sentences();
//            for(CoreSentence sentence : sentences) {
//                sentiment = sentence.sentiment();
//
//            }
//            System.out.println("SENTIMENT: " + sentiment + " -> ");

            String j = "{\n" +
                    "    \"data\":{\n" +
                    "       \"attachments\":{\n" +
                    "          \n" +
                    "       },\n" +
                    "       \"author_id\":\"2653541890\",\n" +
                    "       \"context_annotations\":[\n" +
                    "          {\n" +
                    "             \"domain\":{\n" +
                    "                \"id\":\"10\",\n" +
                    "                \"name\":\"Person\",\n" +
                    "                \"description\":\"Named people in the world like Nelson Mandela\"\n" +
                    "             },\n" +
                    "             \"entity\":{\n" +
                    "                \"id\":\"1070711260117839872\",\n" +
                    "                \"name\":\"Mehdi Hasan\",\n" +
                    "                \"description\":\"British journalist\"\n" +
                    "             }\n" +
                    "          },\n" +
                    "          {\n" +
                    "             \"domain\":{\n" +
                    "                \"id\":\"94\",\n" +
                    "                \"name\":\"Journalist\",\n" +
                    "                \"description\":\"A journalist like 'Anderson Cooper'\"\n" +
                    "             },\n" +
                    "             \"entity\":{\n" +
                    "                \"id\":\"1070711260117839872\",\n" +
                    "                \"name\":\"Mehdi Hasan\",\n" +
                    "                \"description\":\"British journalist\"\n" +
                    "             }\n" +
                    "          },\n" +
                    "          {\n" +
                    "             \"domain\":{\n" +
                    "                \"id\":\"123\",\n" +
                    "                \"name\":\"Ongoing News Story\",\n" +
                    "                \"description\":\"Ongoing News Stories like 'Brexit'\"\n" +
                    "             },\n" +
                    "             \"entity\":{\n" +
                    "                \"id\":\"1220701888179359745\",\n" +
                    "                \"name\":\"COVID-19\"\n" +
                    "             }\n" +
                    "          }\n" +
                    "       ],\n" +
                    "       \"conversation_id\":\"1491445667692089344\",\n" +
                    "       \"created_at\":\"2022-02-09T16:15:31.000Z\",\n" +
                    "       \"entities\":{\n" +
                    "          \"mentions\":[\n" +
                    "             {\n" +
                    "                \"start\":3,\n" +
                    "                \"end\":15,\n" +
                    "                \"username\":\"mehdirhasan\",\n" +
                    "                \"id\":\"130557513\"\n" +
                    "             }\n" +
                    "          ]\n" +
                    "       },\n" +
                    "       \"geo\":{\n" +
                    "          \n" +
                    "       },\n" +
                    "       \"id\":\"1491445667692089344\",\n" +
                    "       \"lang\":\"en\",\n" +
                    "       \"possibly_sensitive\":false,\n" +
                    "       \"public_metrics\":{\n" +
                    "          \"retweet_count\":6003,\n" +
                    "          \"reply_count\":0,\n" +
                    "          \"like_count\":0,\n" +
                    "          \"quote_count\":0\n" +
                    "       },\n" +
                    "       \"referenced_tweets\":[\n" +
                    "          {\n" +
                    "             \"type\":\"retweeted\",\n" +
                    "             \"id\":\"1490903295078301696\"\n" +
                    "          }\n" +
                    "       ],\n" +
                    "       \"reply_settings\":\"everyone\",\n" +
                    "       \"source\":\"Twitter for iPad\",\n" +
                    "       \"text\":\"RT @mehdirhasan: The daily average for Covid deaths in this country is still above 2 and a half *thousand.*\\n\\n2.5k Americans a day. A day.…\"\n" +
                    "    },\n" +
                    "    \"includes\":{\n" +
                    "       \"users\":[\n" +
                    "          {\n" +
                    "             \"created_at\":\"2014-06-29T15:20:40.000Z\",\n" +
                    "             \"description\":\"student of life, collector of wisdoms, artist, soil diva, and I love a funny story.  keep laughing, she who laughs, lasts!\",\n" +
                    "             \"id\":\"2653541890\",\n" +
                    "             \"location\":\"Olympia, WA\",\n" +
                    "             \"name\":\"WIKI\",\n" +
                    "             \"profile_image_url\":\"https://pbs.twimg.com/profile_images/800221050306842626/e_1TQbEJ_normal.jpg\",\n" +
                    "             \"protected\":false,\n" +
                    "             \"public_metrics\":{\n" +
                    "                \"followers_count\":24,\n" +
                    "                \"following_count\":295,\n" +
                    "                \"tweet_count\":3794,\n" +
                    "                \"listed_count\":0\n" +
                    "             },\n" +
                    "             \"url\":\"\",\n" +
                    "             \"username\":\"Makeroomforthe1\",\n" +
                    "             \"verified\":false\n" +
                    "          },\n" +
                    "          {\n" +
                    "             \"created_at\":\"2010-04-07T16:57:33.000Z\",\n" +
                    "             \"description\":\"Host, @mehdihasanshow on @MSNBC and NBC's @peacockTV\",\n" +
                    "             \"entities\":{\n" +
                    "                \"url\":{\n" +
                    "                   \"urls\":[\n" +
                    "                      {\n" +
                    "                         \"start\":0,\n" +
                    "                         \"end\":23,\n" +
                    "                         \"url\":\"https://t.co/KdzXP2RNQ4\",\n" +
                    "                         \"expanded_url\":\"http://www.facebook.com/mrmehdihasan\",\n" +
                    "                         \"display_url\":\"facebook.com/mrmehdihasan\"\n" +
                    "                      }\n" +
                    "                   ]\n" +
                    "                },\n" +
                    "                \"description\":{\n" +
                    "                   \"mentions\":[\n" +
                    "                      {\n" +
                    "                         \"start\":6,\n" +
                    "                         \"end\":21,\n" +
                    "                         \"username\":\"mehdihasanshow\"\n" +
                    "                      },\n" +
                    "                      {\n" +
                    "                         \"start\":25,\n" +
                    "                         \"end\":31,\n" +
                    "                         \"username\":\"MSNBC\"\n" +
                    "                      },\n" +
                    "                      {\n" +
                    "                         \"start\":42,\n" +
                    "                         \"end\":52,\n" +
                    "                         \"username\":\"peacockTV\"\n" +
                    "                      }\n" +
                    "                   ]\n" +
                    "                }\n" +
                    "             },\n" +
                    "             \"id\":\"130557513\",\n" +
                    "             \"location\":\"Washington DC\",\n" +
                    "             \"name\":\"Mehdi Hasan\",\n" +
                    "             \"pinned_tweet_id\":\"1062706401804455937\",\n" +
                    "             \"profile_image_url\":\"https://pbs.twimg.com/profile_images/1300148948162818050/DrdcJWH1_normal.jpg\",\n" +
                    "             \"protected\":false,\n" +
                    "             \"public_metrics\":{\n" +
                    "                \"followers_count\":1165027,\n" +
                    "                \"following_count\":3526,\n" +
                    "                \"tweet_count\":111881,\n" +
                    "                \"listed_count\":7343\n" +
                    "             },\n" +
                    "             \"url\":\"https://t.co/KdzXP2RNQ4\",\n" +
                    "             \"username\":\"mehdirhasan\",\n" +
                    "             \"verified\":true\n" +
                    "          }\n" +
                    "       ],\n" +
                    "       \"tweets\":[\n" +
                    "          {\n" +
                    "             \"attachments\":{\n" +
                    "                \n" +
                    "             },\n" +
                    "             \"author_id\":\"130557513\",\n" +
                    "             \"context_annotations\":[\n" +
                    "                {\n" +
                    "                   \"domain\":{\n" +
                    "                      \"id\":\"10\",\n" +
                    "                      \"name\":\"Person\",\n" +
                    "                      \"description\":\"Named people in the world like Nelson Mandela\"\n" +
                    "                   },\n" +
                    "                   \"entity\":{\n" +
                    "                      \"id\":\"1070711260117839872\",\n" +
                    "                      \"name\":\"Mehdi Hasan\",\n" +
                    "                      \"description\":\"British journalist\"\n" +
                    "                   }\n" +
                    "                },\n" +
                    "                {\n" +
                    "                   \"domain\":{\n" +
                    "                      \"id\":\"94\",\n" +
                    "                      \"name\":\"Journalist\",\n" +
                    "                      \"description\":\"A journalist like 'Anderson Cooper'\"\n" +
                    "                   },\n" +
                    "                   \"entity\":{\n" +
                    "                      \"id\":\"1070711260117839872\",\n" +
                    "                      \"name\":\"Mehdi Hasan\",\n" +
                    "                      \"description\":\"British journalist\"\n" +
                    "                   }\n" +
                    "                },\n" +
                    "                {\n" +
                    "                   \"domain\":{\n" +
                    "                      \"id\":\"123\",\n" +
                    "                      \"name\":\"Ongoing News Story\",\n" +
                    "                      \"description\":\"Ongoing News Stories like 'Brexit'\"\n" +
                    "                   },\n" +
                    "                   \"entity\":{\n" +
                    "                      \"id\":\"1220701888179359745\",\n" +
                    "                      \"name\":\"COVID-19\"\n" +
                    "                   }\n" +
                    "                }\n" +
                    "             ],\n" +
                    "             \"conversation_id\":\"1490903295078301696\",\n" +
                    "             \"created_at\":\"2022-02-08T04:20:19.000Z\",\n" +
                    "             \"entities\":{\n" +
                    "                \n" +
                    "             },\n" +
                    "             \"geo\":{\n" +
                    "                \n" +
                    "             },\n" +
                    "             \"id\":\"1490903295078301696\",\n" +
                    "             \"lang\":\"en\",\n" +
                    "             \"possibly_sensitive\":false,\n" +
                    "             \"public_metrics\":{\n" +
                    "                \"retweet_count\":6003,\n" +
                    "                \"reply_count\":2844,\n" +
                    "                \"like_count\":24440,\n" +
                    "                \"quote_count\":612\n" +
                    "             },\n" +
                    "             \"reply_settings\":\"everyone\",\n" +
                    "             \"source\":\"Twitter Web App\",\n" +
                    "             \"text\":\"The daily average for Covid deaths in this country is still above 2 and a half *thousand.*\\n\\n2.5k Americans a day. A day.\\n\\nAnd yet some say it's time to move on from the pandemic. It's 'peaked.' Back to normal. Off ramps. Etc. \\n\\nHow did we normalize mass death in this country?\"\n" +
                    "          }\n" +
                    "       ]\n" +
                    "    },\n" +
                    "    \"matching_rules\":[\n" +
                    "       {\n" +
                    "          \"id\":\"1491443852439347201\",\n" +
                    "          \"tag\":\"covid\"\n" +
                    "       }\n" +
                    "    ]\n" +
                    " }";
            JSONObject jj = new JSONObject(j);
            JSONObject o = jj.getJSONObject("includes");
//            int c = Integer.parseInt(jj.getJSONObject("includes").getJSONArray("tweets").getJSONObject(0).getJSONObject("public_metrics").getString("like_count"));
//            System.out.println(o.getJSONArray("tweets").getJSONObject(0).getJSONObject("public_metrics").getString("like_count"));
//            System.out.println(c);
            System.out.println(jj.getJSONObject("data").has("referenced_tweets"));
        }
    }