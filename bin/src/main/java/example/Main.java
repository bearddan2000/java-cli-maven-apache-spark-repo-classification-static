package example;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.*;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;

import scala.Tuple2;

public class Main {

    private static final Pattern SPACE = Pattern.compile(",");

    public static void main(String[] args) throws Exception {
        SparkConf sparkConf = new SparkConf().setAppName("JavaWordCount")
            .setMaster("local");
        JavaSparkContext ctx = new JavaSparkContext(sparkConf);
        JavaRDD<String> lines = ctx.textFile("/root/src/main/resources/manifest.csv", 1);

        JavaRDD<String> words = lines.flatMap(s -> Arrays.asList(SPACE.split(s)).iterator());
        JavaPairRDD<String, Integer> wordAsTuple = words.mapToPair(word -> new Tuple2<>(word, 1));
        JavaPairRDD<String, Integer> wordWithCount = wordAsTuple.reduceByKey((Integer i1, Integer i2)->i1 + i2);
        List<Tuple2<String, Integer>> output = wordWithCount.collect();
        for (Tuple2<?, ?> tuple : output) {
             System.out.println(tuple._1() + ": " + tuple._2());
        }
        ctx.stop();
    }

}
