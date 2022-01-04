import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.utils.Utils;

public class TwitterMainTopology {
    public static void main(String[] args) {
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("TwitterSpout", new TwitterSpout());
        builder.setBolt("TwitterBolt", new TwitterBolt()).shuffleGrouping("TwitterSpout");

        Config config = new Config();
        //config.put(Config.Topology_BOLT_W);
        config.setDebug(false);

        try (LocalCluster cluster = new LocalCluster()) {
            cluster.submitTopology("Twitter", config, builder.createTopology());
            Utils.sleep(600000); // wait [param] ms
            cluster.killTopology("Twitter");
            cluster.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
