import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.topology.TopologyBuilder;

public class TwitterMainTopology {
    public static void main(String[] args) {
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("TwitterSpout", new TwitterSpout());
        builder.setBolt("TwitterBolt", new TwitterBolt()).shuffleGrouping("TwitterSpout", "stream");

        Config config = new Config();
        config.put(Config.TOPOLOGY_BOLTS_WINDOW_LENGTH_DURATION_MS,10000);   //lunghezza della topologia

        try (LocalCluster cluster = new LocalCluster()) {
            cluster.submitTopology("Twitter", config, builder.createTopology());
            Thread.sleep(600000); // wait [param] ms
            cluster.killTopology("Twitter");
            cluster.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
