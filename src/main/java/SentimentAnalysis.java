import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.util.List;

    public class SentimentAnalysis {
        public static void main(String[] args) {
            StanfordCoreNLP stanfordCoreNLP = Pipeline.getPipeline();
            //String text = "Oh dear So #novax is now under investigation by his own country *and* Spain, and Canada, the US and others have taken note of the Australian brouhaha... I suspect his 'oh, I'm unvaxxed but I'm safe' shpiel is going to become a *lot* harder in the future.";
            String text = "When mfs who got the vaccine sicker than the ones who didn't get it";
            //String text = "I don’t understand the hype about the vaccine";
            CoreDocument coreDocument = new CoreDocument(text);
            stanfordCoreNLP.annotate(coreDocument);
            List<CoreSentence> sentences = coreDocument.sentences();
            for(CoreSentence sentence : sentences) {
                String sentiment = sentence.sentiment();
                System.out.println("SENTIMENT: " + sentiment + " -> " + sentence);
            }
        }
    }