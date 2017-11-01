package edu.uta.cse6331

import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import scala.io.Source
import org.apache.spark.SparkContext._

@SerialVersionUID(123L)
case class Element ( indexi:Int,indexj: Int, value: Double )
      extends Serializable {}



object Multiply {
	def main(args: Array[ String ]) {
	  
	  
		val conf = new SparkConf().setAppName("Multiply")
		conf.setMaster("local[2]")
    val sc = new SparkContext(conf)

    val M = sc.textFile(args(0)).map( line => { val a = line.split(",")
                                                Element(a(0).toInt,a(1).toInt,a(2).toDouble) } )
    val N = sc.textFile(args(1)).map( line => { val a = line.split(",")
    	 	                                          Element(a(0).toInt,a(1).toInt,a(2).toDouble) } )
    	
    val M_ = M.map({ case Element(i, j, v) => (j, (i, v)) })
    val N_ = N.map({ case Element(j, k, w) => (j, (k, w)) })

    val productEntries = M_
    .join(N_)
    .map({ case (_, ((i, v), (k, w))) => ((i, k), (v * w)) })
    .reduceByKey(_ + _)
    //.map({ case ((i, k), sum) => i+" "+ k + " " + sum })
    .map({ case ((i, k), sum) => (i, k, sum) })
    
    //val res = e.map( e => (e._2,e) ).join(d.map( d => (d._2,d) ))
     //           .map { case (k,(e,d)) => e._1+" "+d._1 }

 
     productEntries.collect()
     productEntries.sortBy({ case (i, k, sum) => (i,k)})
     
     val ordered = productEntries.map({case (i,k,sum) => i+" " + k+ " "+sum })
     
   //  ordered.saveAsTextFile(args(2))
	ordered.collect().foreach(println)     

 //	sc.stop()


	}

}
