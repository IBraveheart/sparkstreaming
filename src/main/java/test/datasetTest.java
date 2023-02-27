package test;

import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import static org.apache.spark.sql.functions.col;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Akang
 * @create 2022-11-28 9:30
 */
public class datasetTest {
    public static void main(String[] args) {
        // 构建执行环境
        SparkSession spark = SparkSession.builder().master("local[*]").getOrCreate();

        List<Tuple2<String,Integer>> data = new ArrayList<>() ;
        data.add(new Tuple2<>("tom",1)) ;
        data.add(new Tuple2<>("jack",2)) ;

        StructType schema = new StructType() ;
        Dataset<Integer> dataset = spark.createDataset(Arrays.asList(1, 2, 3, 4, 5, 6, 7), Encoders.INT());

        dataset.schema().add("price", DataTypes.StringType,true) ;



        dataset.where("value > 2").show();
        dataset.select(col("value").plus(1)).show();

        dataset.show();

    }
}
