import org.apache.spark.mllib.linalg.distributed.RowMatrix
import org.apache.spark.mllib.linalg._
import org.apache.spark.{SparkConf, SparkContext}

object SparkWordCount { 
   def main(args: Array[String]) { 

		val conf = new SparkConf().setAppName("SVD")
    	val sc = new SparkContext(conf)

		val inputData = sc.textFile("hdfs://localhost:54310/user/hduser/bda_in/tfidf_l2.tsv").map{ line =>
		  val parts = line.split("\t")
		  (parts(0).toLong, parts(1).toInt, parts(2).toDouble)
		}

		val nCol = inputData.map(_._2).distinct().count().toInt

		// Construct rows of the RowMatrix
		val dataRows = inputData.groupBy(_._1).map[(Long, Vector)]{ row =>
		  val (indices, values) = row._2.map(e => (e._2, e._3)).unzip
		  (row._1, new SparseVector(nCol, indices.toArray, values.toArray))
		}

		// Compute 20 largest singular values and corresponding singular vectors
		val svd = new RowMatrix(dataRows.map(_._2).persist()).computeSVD(20, computeU = true)

		// Write results to hdfs
		val V = svd.V.toArray.grouped(svd.V.numRows).toList.transpose
		sc.makeRDD(V, 1).zipWithIndex()
		  .map(line => line._2 + "\t" + line._1.mkString("\t")) // make tsv line starting with column index
		  .saveAsTextFile("hdfs://localhost:54310/user/hduser/bda_in/output/right_singular_vectors_l2")

		svd.U.rows.map(row => row.toArray).zip(dataRows.map(_._1))
		  .map(line => line._2 + "\t" + line._1.mkString("\t")) // make tsv line starting with row index
		  .saveAsTextFile("hdfs://localhost:54310/user/hduser/bda_in/output/left_singular_vectors_l2")

		sc.makeRDD(svd.s.toArray, 1)
		  .saveAsTextFile("hdfs://localhost:54310/user/hduser/bda_in/output/singular_values_l2")
	}
}
