package edu.uta.cse6331;    
    import java.io.*;
    import java.util.*;
    import org.apache.hadoop.fs.Path;
    import org.apache.hadoop.conf.*;
    import org.apache.hadoop.io.*;
    import org.apache.hadoop.mapreduce.*;
    import org.apache.hadoop.util.*;
    import org.apache.hadoop.mapreduce.lib.input.*;
    import org.apache.hadoop.mapreduce.lib.output.*;
    import org.apache.hadoop.fs.FileSystem;
    import org.apache.hadoop.util.Tool;
    import org.apache.hadoop.util.ToolRunner;
 
class Elem implements Writable {
     public short tag;  // 0 for M, 1 for N
     public int index;  // one of the indexes (the other is used as a key)
     public double value;

      Elem () { }

      Elem(short t, int i, double v){
        tag=t;
        index=i;
        value=v;
      }

      public void write (DataOutput out) throws IOException {
        out.writeShort(tag);
        out.writeInt(index);
        out.writeDouble(value);
      }

        public void readFields(DataInput in) throws IOException{
            tag=in.readShort();
            index=in.readInt();
            value=in.readDouble();

        }
      }


 class Pair implements WritableComparable <Pair> {
       //Pair pair = (Pair)p;
	public int i;
       public int j;

        Pair() { }

        Pair (int a, int b){
            i=a;
            j=b;
        }

        public void write(DataOutput out) throws IOException{
            out.writeInt(i);
            out.writeInt(j);
        }

        public void readFields(DataInput in) throws IOException{
            i=in.readInt();
            j=in.readInt();
        }

        @Override
        public int compareTo(Pair p){
            Pair pair = (Pair)p;
        	
	   
	 int  diffi = i-p.i;
	 int diffj = j-p.j;

	if(i==p.i)
		return diffj;
	else
		return diffi;
	
        } 
	
	@Override
	public String toString(){
	 return (i + "," +j);
	
	}
  
    
	//@Override
	//	public int hashCode()
	//	{
	//	return Objects.hash(i,j);
	//	}
 }



public class Multiply {  // extends Configured implements Tool {

        public static class MMapper extends Mapper<Object, Text, IntWritable, Elem>{
        
        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException{
                Scanner sc = new Scanner(value.toString()).useDelimiter(",");
				int i=sc.nextInt();
				int j=sc.nextInt();
				double v= sc.nextDouble();
				short tag = (short)0;
                context.write(new IntWritable(j), new Elem(tag,i,v));
                sc.close();


            }
        }

        public static class NMapper extends Mapper<Object, Text, IntWritable, Elem>{

        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException{
                Scanner sc = new Scanner(value.toString()).useDelimiter(",");
                int j = sc.nextInt();
				int k = sc.nextInt();
				double w = sc.nextDouble();
				short tag = (short)1;
				context.write(new IntWritable(j), new Elem(tag,k,w));
                sc.close();
            }
        }


        public static class ProductReducer extends Reducer<IntWritable,Elem,Pair,DoubleWritable>{
            static Vector <Elem> M= new Vector<Elem>();
            static Vector <Elem> N = new Vector<Elem>();

            @Override
            public void reduce(IntWritable key,Iterable<Elem> values, Context context) throws IOException,InterruptedException {
                
				M.clear();
				N.clear();
				for(Elem item :values){
                    if(item.tag==0){
                        M.add(new Elem(item.tag, item.index, item.value));
                    }
                    else{
                        N.add(new Elem(item.tag, item.index, item.value));
                    }
                }

                for(Elem m : M){
                    for(Elem n: N ){
                        context.write(new Pair(m.index,n.index),new DoubleWritable(m.value*n.value));
                    }
                }
            }
        }

        public static class IdentityMap extends Mapper<Pair, DoubleWritable, Pair, DoubleWritable>{
            @Override
            public void map(Pair key, DoubleWritable value, Context context ) throws IOException, InterruptedException{
                context.write(key, value);
            }

        }

        public static class AggregationReducer extends Reducer <Pair, DoubleWritable, Pair, DoubleWritable>{

            @Override
            public void reduce(Pair key, Iterable<DoubleWritable> values, Context context) throws IOException,InterruptedException {
                
                double sum = 0.0d;
                for(DoubleWritable item : values)
                sum = sum + item.get();
                context.write(key, new DoubleWritable(sum));
            }
        }



       public static void main(String[] args) throws Exception {

	       /* // TOOLS RUNNER
        	int res =ToolRunner.run(new Configuration(), new ToolMapReduce(), args);
			System.exit(res);
      
       		}
       			@Override
       			public int run(String[] args) throws Exception {
	
			*/

	      

	      // Configuration conf = new Configuration();
	       //****** JOB 1 *********
	        Job jobOne = Job.getInstance();
	        jobOne.setJobName("Phase one");
		jobOne.setJarByClass(Multiply.class);
		// from Reducer
	        jobOne.setOutputKeyClass(Pair.class);
	        jobOne.setOutputValueClass(DoubleWritable.class);
	        //from mapper to reducer
	        jobOne.setMapOutputKeyClass(IntWritable.class);
	        jobOne.setMapOutputValueClass(Elem.class);
	        // SET resucer class
	        jobOne.setReducerClass(ProductReducer.class);
	        MultipleInputs.addInputPath(jobOne, new Path(args[0]),TextInputFormat.class, MMapper.class);
	        MultipleInputs.addInputPath(jobOne, new Path(args[1]),TextInputFormat.class, NMapper.class);
		// Output format classes		
		//jobOne.setOutputFormatClass(TextOutputFormat.class);
		jobOne.setOutputFormatClass(SequenceFileOutputFormat.class);
			
	       	Path intermediate = new Path(args[2]);
	       	FileOutputFormat.setOutputPath(jobOne, intermediate);
	       	jobOne.waitForCompletion(true);

			// ******* JOB 2 *********
			Job jobTwo = Job.getInstance();
			jobTwo.setJobName("Phase2");
			jobTwo.setJarByClass(Multiply.class);
			// from Reducer
			//jobTwo.setOutputKeyClass(Pair.class);
			//jobTwo.setOutputValueClass(DoubleWritable.class);
			// from mapper to reducer
			jobTwo.setMapOutputKeyClass(Pair.class);
			jobTwo.setMapOutputValueClass(DoubleWritable.class);

			jobTwo.setOutputKeyClass(Pair.class);
			jobTwo.setOutputValueClass(DoubleWritable.class);
			
			// From reducer
			jobTwo.setInputFormatClass(SequenceFileInputFormat.class);
			jobTwo.setOutputFormatClass(TextOutputFormat.class);
			jobTwo.setMapperClass(IdentityMap.class);
			jobTwo.setReducerClass(AggregationReducer.class);
		
			FileInputFormat.setInputPaths(jobTwo, intermediate);
			FileOutputFormat.setOutputPath(jobTwo, new Path(args[3]));
			jobTwo.waitForCompletion(true);
	

	//	FileSystem.get(conf).delete(tempDir, true);
		
		// USING TOOLS
	/*
	Job jobOne = Job.getInstance();
	jobOne.setJobName("JobOne");
	jobOne.setJarByClass(MultiplyTools.class);
	jobOne.setNumReduceTasks(1);
	jobOne.setOutputKeyClass(Pair.class);
	jobOne.setOutputValueClass(DoubleWritable.class);
	jobOne.setMapOutputKeyClass(IntWritable.class);
	jobOne.setMapOutputValueClass(Elem.class);
	jobOne.setReducerClass(ProductReducer.class);
	jobOne.setInputFormatClass(TextInputFormat.class);
	jobOne.setOutputFormatClass(SequenceFileOutputFormat.class);

	MultipleInputs.addInputPath(jobOne, new Path(args[0]), TextInputFormat.class, MMapper.class);
	MultipleInputs.addInputPath(jobOne, new Path(args[1]), TextInputFormat.class, NMapper.class);

	FileOutputFormat.setOutputPath(jobOne, new Path(args[2]));
	return jobOne.waitForCompletion(true) ? 0 : 1;
	*/
      }

    }
