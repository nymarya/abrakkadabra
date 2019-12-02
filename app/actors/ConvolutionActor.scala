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
//      val image: DenseVector[Int] = DenseVector( 8, 5, 8, 1, 6, 8, 7, 9, 9, 2, 8, 2, 7, 8,
//        2, 9, 4, 9, 7, 3, 2, 9, 2, 9, 7, 1, 9, 5, 6, 9, 8, 7, 3, 1, 5,
//        3, 5, 6, 4, 1, 4, 7)
//      val kernel : DenseVector[Int] = DenseVector(3, 2, 2, 1, 1, 3, 3, 1, 2)


//      val image: DenseMatrix[Int] = DenseMatrix( Array(8, 5, 8, 1, 6, 8, 7), Array(9, 9, 2, 8, 2, 7, 8),
//        Array(2, 9, 4, 9, 7, 3, 2), Array( 9, 2, 9, 7, 1, 9, 5), Array(6, 9, 8, 7, 3, 1, 5),
//        Array(1, 9, 9, 7, 1, 4, 6), Array(3, 5, 6, 4, 1, 4, 7))
//      val kernel : DenseMatrix[Int] = DenseMatrix(Array(3, 2, 2), Array(1, 1, 3), Array(3, 1, 2))
//
//      val m : Int = image.rows + kernel.rows - 1
//
////      val imageaa = sc.parallelize(image.toArray)
//
//      val imageCopy: DenseMatrix[Int] = DenseMatrix.zeros(m, m)
//      val kernelCopy: DenseMatrix[Int] = DenseMatrix.zeros(m, m)
//
//      // Fill
//      for( i <- 0 until image.rows; j <- 0 until image.rows){
//        imageCopy(i, j) = image.apply(i, j)
//      }
//
//      for( i <- 0 until kernel.rows; j <- 0 until kernel.rows){
//        kernelCopy(i, j) = kernel.apply(i, j)
//      }
//
////      val sum = sc.doubleAccumulator("acc")
//      val result : DenseMatrix[Double] = DenseMatrix.zeros(m, m)
//      val rangeJ = 0 until m
//      val result = sc.parallelize(0 until m).map(
//        j => {
//        for( k <- 0 until m) {
//            var sum = 0
//            for( p <- 0 to j) {
//
//                for(q <- 0 to k){
//                  val ker = kernelCopy.apply(p, q)
//                  val im = imageCopy.apply(j - p, k - q)
//                  sum += (ker * im)
//                }
//              }
//
//             sum
//          }
//        }).collect()


//      val image_height = image.rows
//      val image_width = image.cols
//      val local_filter_height = kernel.rows
//      val local_filter_width = kernel.cols
//      val padded = BDM.zeros[Double](image_height + 2 * (filter_height/2),
//        image_width + 2* (filter_width/2))
//      for (i <- 0 until image_height) {
//        for (j <- 0 until image_width) {
//          padded(i + (filter_height / 2), j + (filter_height / 2)) = image(i, j)
//        }
//      }
//      val convolved = BDM.zeros[Double](image_height -local_filter_height + 1 + 2 *
//        (filter_height/2), image_width - local_filter_width + 1 + 2 * (filter_width/2))
//      for (i <- 0 until convolved.rows) {
//        for (j <- 0 until convolved.cols) {
//          var aggregate = 0.0
//          for (k <- 0 until local_filter_height) {
//            for (l <- 0 until local_filter_width) {
//              aggregate += padded(i + k, j + l) * filter(k, l)
//            }
//          }
//          convolved(i, j) = aggregate
//        }
//      }
//
//      val result = convolve(image, kernel)
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