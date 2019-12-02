package actors

import akka.actor.{AbstractActor, Actor, Props}
import akka.actor.typed.Signal
import messages.Matrices
import org.apache.spark.{Accumulator, SparkConf, SparkContext}
import org.apache.spark.SparkContext._
import org.apache.spark.rdd.RDD
import org.apache.spark.util.{AccumulatorV2, DoubleAccumulator}
//import java.util._
//import org.apache.spark.api.java.JavaSparkContext
//import org.apache.spark.SparkConf
//import org.apache.spark.api.java.JavaRDD
import breeze.linalg.{DenseMatrix, DenseVector}
import breeze.signal._
import org.apache.spark.mllib.linalg.distributed.{BlockMatrix, CoordinateMatrix, MatrixEntry}

object ConvolutionActor {
  def props = Props[ConvolutionActor]

}

class ConvolutionActor extends Actor {
  val conf = new SparkConf().setAppName("abrakkadabra").setMaster("spark://34.69.245.207:7077")
    .set("spark.driver.allowMultipleContexts", "true")

  val sc = new SparkContext(conf)
  def func(x: Int, ac: DoubleAccumulator) = {
      ac.add(x)
  }

  def receive = {
    case matrices: Matrices =>
      println("spark")

//      val image: DenseMatrix[Int] = DenseMatrix( matrices.matrix : _)
//      val kernel : DenseMatrix[Int] = DenseMatrix(matrices.kernel : _)
//      val m : Int = image.rows + kernel.rows - 1;
//
//      val imageCopy: DenseMatrix[Int] = DenseMatrix.zeros(m, m)
//      val kernelCopy: DenseMatrix[Int] = DenseMatrix.zeros(m, m)
//
//      // Fill
//      for( i <- 0 until image.rows; j <- 0 until image.cols){
//        imageCopy(i, j) = image.apply(i, j)
//      }
//
//      for( i <- 0 until kernel.rows; j <- 0 until kernel.cols){
//        kernelCopy(i, j) = kernel.apply(i, j)
//      }
//
//      val result : DenseMatrix[Int] = DenseMatrix.zeros(m, m)
//      for (j <- 0 until m){
//        for (k <- 0 until m){
//          var sum: Int = 0;
//          for (p <-0 to j){
//            for (q <- 0 to k ){
//              val k = kernelCopy.apply(p, q)
//              println(j - p )
//              val i = imageCopy.apply(j - p, k - q)
//              sum = sum + k * i
//            }
//          }
//          result(j, k) = sum
//        }
//      }

    val entries: RDD[MatrixEntry] =  sc.parallelize(matrices.kernel.zipWithIndex.map{ case (v, i) =>
                                                          v.zipWithIndex.map{ case (x, j) =>  MatrixEntry(i, j , x)}}
      .flatMap(_.toSeq))
    // Create a CoordinateMatrix from an RDD[MatrixEntry].
    val coordMat: CoordinateMatrix = new CoordinateMatrix(entries)
    // Transform the CoordinateMatrix to a BlockMatrix
    val matA: BlockMatrix = coordMat.toBlockMatrix().cache()

    val entries1: RDD[MatrixEntry] =  sc.parallelize(matrices.matrix.zipWithIndex.map{ case (v, i) =>
      v.zipWithIndex.map{ case (x, j) =>  MatrixEntry(i, j , x)}}.flatMap(_.toSeq))
    // Create a CoordinateMatrix from an RDD[MatrixEntry].
    val coordMat1: CoordinateMatrix = new CoordinateMatrix(entries1)
    // Transform the CoordinateMatrix to a BlockMatrix
    val matB: BlockMatrix = coordMat1.toBlockMatrix().cache()

    val result = matA.multiply(matB)
    println(result)
    sender() ! result
    //this
  }
}