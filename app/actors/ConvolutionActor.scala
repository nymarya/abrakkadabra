package actors
import org.apache.spark.SparkContext
import akka.actor.AbstractActor
import akka.actor.Props
import akka.actor.typed.Signal
//import java.util._
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.SparkConf
import org.apache.spark.api.java.JavaRDD
//import org.apache.spark.mllib.linalg.Matrix;
//import org.apache.spark.mllib.linalg.Matrices;
object ConvolutionActor {
  def props = Props.create(classOf[ConvolutionActor])
}

class ConvolutionActor() extends AbstractActor {
  val conf = new SparkContext("spark://35.232.205.36:7077",
  "abrakkadabra")
//  SparkSession.builder()
//    .appName("SparkSample")
//    .master()
//    .getOrCreate()
//  new SparkConf(true).setAppName //
//  "MyApp".setMaster("spark://35.232.205.36:7077")
//  sc = new JavaSparkContext(conf)
//  sc.close()
//  private var sc = null

  override def onSignal: PartialFunction[Signal, Behavior[Nothing]] = {
    case PostStop =>
      context.log.info("IoT Application stopped")
      this
  }

  @Override override def createReceive = receiveBuilder.`match`(classOf[Nothing], (s: P) => {
    def foo(s: P) = { // conv
      //                    Matrix image = Matrices.dense(7, 7, new double[] {8, 5, 8, 1, 6, 8, 7, 9, 9, 2, 8, 2, 7, 8,
      //                            2, 9, 4, 9, 7, 3, 2, 6, 9, 8, 7, 3, 1, 5, 3, 5, 6, 4, 1, 4, 7});
      //                    Matrix kernel = Matrices.dense(3, 3, new double[] {3, 2,2 , 1,1,3, 3,1,2});
      //                    long m = image.numRows() + kernel.numRows() - 1;
      //                    int[][] result = new int[(int) m][(int) m];
      //                    for(int j = 0; j < m; j++){
      //                        for(int k = 0; k < m; k++){
      //                            int sum = 0;
      //                            for(int p = 0; p< j+1; p++){
      //                                for (int q = 0; q < k+1; q++){
      //                                    sum += kernel.apply(p, q) * image.apply(j-p, k-q);
      //                                }
      //                            }
      //                            result[j][k] = sum;
      //                        }
      //                    }
      sender.tell(s + ", I'm an actor using Spark! The first element of the RDD is ", //+ String.valueOf(kernel)
        self)
    }

    foo(s)
  }).build
}