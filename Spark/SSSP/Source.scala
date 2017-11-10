package edu.uta.cse6331

import org.apache.spark.SparkContext
import org.apache.spark.SparkConf

import org.apache.spark.SparkContext._


import scala.collection.mutable.Map


object Source {
	def main(args: Array[ String ]) {
		val conf = new SparkConf().setAppName("Multiply")
		conf.setMaster("local[2]")
    val sc = new SparkContext(conf)

    var distances: collection.mutable.Map[Int, Int] = Map()
    distances(0) = 0

		
		val edges = sc.textFile(args(0)).map(
        line => { 
          val a = line.split(",")
          (a(0).toInt, a(1).toInt, a(2).toInt) 
          } 
        )

    
     def updateDistances(arg_tuples:(Int, Int, Int), distances:Map[Int, Int]): (Int, Int, Int) = {
		  val (source, dist, dest) = arg_tuples
		  if (!distances.contains(source)){
		    distances(source) = Int.MaxValue
		  }
		  if (!distances.contains(dest)){
		    distances(dest) = Int.MaxValue
		  }
		  if( distances(source)!= Int.MaxValue){
		  if (distances(dest) > distances(source) + dist){
		    distances.update(dest, distances(source) + dist)
		  } 
		  }
		 
		  arg_tuples
		}
		 var j=0;
		 while(j<4)
		 {
		      var modified_edges = edges.collect.foreach(x => updateDistances(x, distances))
		      j+=1
		 }
		
		distances.foreach{case(key,value)=>
		  if(value==Int.MaxValue)
		        distances-=key  
		}
		
		val sorted=distances.toSeq.sortBy(distances=>distances._1)
    
    sorted.foreach {case (key, value) => println (key + " " + value)}
    
    // sc.parallelize(distances.toSeq).saveAsTextFile(args(1))
		sc.parallelize(distances.toSeq).saveAsTextFile(args(1))
     sc.stop()
     

 
	}
	

}
