package edu.uta.cse6331;
import java.io.*;
import java.util.*;
import org.apache.hadoop.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

class Vertex implements Writable {

	public short tag;
	public long group;
	public long VID;
	public Vector<Long> adjacent=new Vector<Long>();
	public int total;

	public Vertex(){
	}

	public Vertex(short t,long g,long vid,Vector<Long> adj,int tot){
				tag=t;
				group=g;
				VID=vid;
				total=tot;
				adjacent=adj;
	}

     
	public Vertex(short t,long g){
				tag=t;
				group=g;
	}

	public void write(DataOutput out) throws IOException{
		
		// TODO Auto-generated method stub
		
			out.writeShort(tag);
			out.writeLong(group);
			out.writeLong(VID);
			out.writeInt(total);
			for(Long item: adjacent)
			
			out.writeLong(item);
			
	}

	public void readFields( DataInput  in) throws IOException{
		
		
			// TODO Auto-generated method stub
		
			tag=in.readShort();
			group=in.readLong();
			
			VID=in.readLong();
			total=in.readInt();
					
					// if(tag ==0) {
					// 	adjacent = new Vector<Long>();
					// 	for(int i = 0; i< total; i++) {
					// 		adjacent.add(in.readLong());
					// 	}
					// }

					
			if(tag==0){
				int i=0;
				adjacent = new Vector<Long>();
				while(i<total){
					adjacent.add(in.readLong());
					i++;
				}
			}
	}
	
}
public class Graph {
	
	public static class FirstMapper extends Mapper<Object,Text,LongWritable,Vertex> {
		
		@Override
		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			
			Scanner sc = new Scanner(value.toString()).useDelimiter(",");
			long vid = sc.nextLong();
			
			Vector<Long> adj = new Vector<Long>();
			int total = 0;
			
			while(sc.hasNext()) {
				adj.add(sc.nextLong());
				total++;
				
			}
			context.write(new LongWritable (vid), new Vertex((short)0,vid, vid,adj, total));
			System.out.println(total);
			
			sc.close();
			
		}
	}
	
	public static class SecondMapper extends Mapper<LongWritable, Vertex, LongWritable, Vertex> {
		@Override
		public void map(LongWritable key, Vertex value, Context context) throws IOException, InterruptedException {
			
			context.write(new LongWritable(value.VID), value);
			
			for(long v : value.adjacent)
				context.write(new LongWritable(v), new Vertex((short)1,value.group));			
		}
	}
	
	public static class SecondReducer extends Reducer<LongWritable, Vertex, LongWritable, Vertex> {
		@Override
		public void reduce(LongWritable key, Iterable<Vertex> val, Context context) throws IOException, InterruptedException {
			
			Vertex vert = new Vertex();
			
			long min = Long.MAX_VALUE;
			
			
			for(Vertex v :val) {
				if(v.tag == 0) 
					vert = v;
				min = Math.min(min, v.group);
			}
			
			context.write(new LongWritable(min), new Vertex((short)0, min, key.get(),vert.adjacent,vert.adjacent.size() ) );
			
			
		}
	}
	
	public static class FinalMapper extends Mapper<LongWritable, Vertex, LongWritable, IntWritable> {
		@Override
		
		public void map(LongWritable key, Vertex val, Context con) throws IOException, InterruptedException {
			con.write(key, new IntWritable(1));
			
		}
		
		
	}
	
	public static class FinalReducer extends Reducer <LongWritable , IntWritable, LongWritable, Text> {
		int sum;
		@Override
		
		public void reduce(LongWritable key, Iterable<IntWritable> values, Context con) throws IOException, InterruptedException {
			
			sum = 0;
			for(IntWritable item :values) 
				sum+= item.get();
			
			
			con.write(null, new Text(key + "," + sum));
			
		}


	}
	
	
	public static void main(String[] args) throws Exception { // extends Configured implements Tool {
		Configuration conf1 = new Configuration();
		
		Job FirstJob = Job.getInstance(conf1);
		FirstJob.setJobName("FirstJob");
        
		FirstJob.setJarByClass(Graph.class);
		FirstJob.setNumReduceTasks(0);
        
		FirstJob.setInputFormatClass(TextInputFormat.class);
		FirstJob.setOutputFormatClass(SequenceFileOutputFormat.class);
        
		//Output Key class
		FirstJob.setOutputKeyClass(LongWritable.class);
		FirstJob.setOutputValueClass(Vertex.class);
        
		// From Mapper to Reducer
		FirstJob.setMapOutputKeyClass(LongWritable.class);
		FirstJob.setMapOutputValueClass(Vertex.class);
        
		
		FirstJob.setMapperClass(FirstMapper.class);
		
        FileInputFormat.setInputPaths(FirstJob,new Path(args[0]));
        SequenceFileOutputFormat.setOutputPath(FirstJob,new Path(args[1]+"/f0"));
        
        FirstJob.waitForCompletion(true);
        for(int i=0; i < 5; i++)
        {
        			Configuration conf2 = new Configuration();
        			Job SecondJob = Job.getInstance(conf2);
        			
        			SecondJob.setJobName("SecondJob");
        			SecondJob.setJarByClass(Graph.class);
        			
        			SecondJob.setInputFormatClass(SequenceFileInputFormat.class);
        			SecondJob.setOutputFormatClass(SequenceFileOutputFormat.class);
        			
        			SecondJob.setOutputKeyClass(LongWritable.class);
        			SecondJob.setOutputValueClass(Vertex.class);
        			
        			SecondJob.setMapOutputKeyClass(LongWritable.class);
        			SecondJob.setMapOutputValueClass(Vertex.class);
        			
        			
        			
        			SecondJob.setMapperClass(SecondMapper.class);
        			SecondJob.setReducerClass(SecondReducer.class);
        			
				SequenceFileInputFormat.setInputPaths(SecondJob,new Path(args[1]+"/f"+i));
				SequenceFileOutputFormat.setOutputPath(SecondJob,new Path(args[1]+"/f"+(i+1)));
				
				SecondJob.waitForCompletion(true);
        }
        
        Configuration confg = new Configuration();
        Job ThirdJob = Job.getInstance(confg);
        ThirdJob.setJobName("ThirdJob");
        ThirdJob.setJarByClass(Graph.class);
        
        ThirdJob.setInputFormatClass(SequenceFileInputFormat.class);
        ThirdJob.setOutputFormatClass(TextOutputFormat.class);
        
        ThirdJob.setOutputKeyClass(LongWritable.class);
        ThirdJob.setOutputValueClass(Text.class);
        ThirdJob.setMapOutputKeyClass(LongWritable.class);
        ThirdJob.setMapOutputValueClass(IntWritable.class);
        
       
        ThirdJob.setMapperClass(FinalMapper.class);
        ThirdJob.setReducerClass(FinalReducer.class);
        
        SequenceFileInputFormat.setInputPaths(ThirdJob,new Path(args[1]+"/f5"));
        TextOutputFormat.setOutputPath(ThirdJob,new Path(args[2]));
        
        ThirdJob.waitForCompletion(true);
		
		
	}
	

}

