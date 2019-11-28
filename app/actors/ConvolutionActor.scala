package actors

import akka.actor.{AbstractActor, Actor, Props}
import akka.actor.typed.Signal
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
//import java.util._
//import org.apache.spark.api.java.JavaSparkContext
//import org.apache.spark.SparkConf
//import org.apache.spark.api.java.JavaRDD
import breeze.linalg.{DenseMatrix, DenseVector}

object ConvolutionActor {
  def props = Props[ConvolutionActor]

  case class convolute(kernel: String);

}

class ConvolutionActor extends Actor {

  val conf = new SparkContext("spark://104.198.164.25:7077", "abrakkadabra")

  import ConvolutionActor.convolute

  def receive = {
    case convolute(kernel: String) =>
      val image: DenseMatrix[Int] = DenseMatrix( Array(8, 5, 8, 1, 6, 8, 7), Array(9, 9, 2, 8, 2, 7, 8),
        Array(2, 9, 4, 9, 7, 3, 2), Array(6, 9, 8, 7, 3, 1, 5), Array(3, 5, 6, 4, 1, 4, 7))
      val kernel : DenseMatrix[Int] = DenseMatrix(Array(3, 2, 2), Array(1, 1, 3), Array(3, 1, 2))
      val m : Int = image.rows + kernel.rows - 1;

      val imageCopy: DenseMatrix[Int] = DenseMatrix.zeros(m, m)
      val kernelCopy: DenseMatrix[Int] = DenseMatrix.zeros(m, m)

      // Fill
      for( i <- 0 until image.rows; j <- 0 until image.cols){
        imageCopy(i, j) = image.apply(i, j)
      }

      for( i <- 0 until kernel.rows; j <- 0 until kernel.cols){
        kernelCopy(i, j) = kernel.apply(i, j)
      }

      val result : DenseMatrix[Int] = DenseMatrix.zeros(m, m)
      for (j <- 0 until m){
        for (k <- 0 until m){
          var sum: Int = 0;
          for (p <-0 to j){
            for (q <- 0 to k ){
              val k = kernelCopy.apply(p, q)
              println(j - p )
              val i = imageCopy.apply(j - p, k - q)
              sum = sum + k * i
            }
          }
          result(j, k) = sum
        }
      }
    sender() ! ", I'm an actor using Spark! The first element of the RDD is "
     //+ String.valueOf(kernel)
    //this
  }
}

//object ConvolutionActor {
//  def props = Props.create(classOf[ConvolutionActor])
//}
//
//class ConvolutionActor() extends AbstractActor {
//  val conf = new SparkContext("spark://35.232.205.36:7077",
//    "abrakkadabra")
//  //  SparkSession.builder()
//  //    .appName("SparkSample")
//  //    .master()
//  //    .getOrCreate()
//  //  new SparkConf(true).setAppName //
//  //  "MyApp".setMaster("spark://35.232.205.36:7077")
//  //  sc = new JavaSparkContext(conf)
//  //  sc.close()
//  //  private var sc = null
//
//  override def onSignal: PartialFunction[Signal, Behavior[Nothing]] = {
//    case PostStop =>
//      context.log.info("IoT Application stopped")
//      this
//  }
//
//  @Override override def createReceive = receiveBuilder.`match`(classOf[Nothing], (s: P) => {
//    def foo(s: P) = { // conv
//      //                    Matrix image = Matrices.dense(7, 7, new double[] {8, 5, 8, 1, 6, 8, 7, 9, 9, 2, 8, 2, 7, 8,
//      //                            2, 9, 4, 9, 7, 3, 2, 6, 9, 8, 7, 3, 1, 5, 3, 5, 6, 4, 1, 4, 7});
//      //                    Matrix kernel = Matrices.dense(3, 3, new double[] {3, 2,2 , 1,1,3, 3,1,2});
//      //                    long m = image.numRows() + kernel.numRows() - 1;
//      //                    int[][] result = new int[(int) m][(int) m];
//      //                    for(int j = 0; j < m; j++){
//      //                        for(int k = 0; k < m; k++){
//      //                            int sum = 0;
//      //                            for(int p = 0; p< j+1; p++){
//      //                                for (int q = 0; q < k+1; q++){
//      //                                    sum += kernel.apply(p, q) * image.apply(j-p, k-q);
//      //                                }
//      //                            }
//      //                            result[j][k] = sum;
//      //                        }
//      //                    }
//      sender.tell(s + ", I'm an actor using Spark! The first element of the RDD is ", //+ String.valueOf(kernel)
//        self)
//    }
//
//    foo(s)
//  }).build
//}