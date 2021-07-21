package org.accenture;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.coders.AvroCoder;
import org.apache.beam.sdk.coders.CoderException;
import org.apache.beam.sdk.io.Read;
import org.apache.beam.sdk.io.TextIO;

import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.options.Validation.Required;
import org.apache.beam.sdk.transforms.Count;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.SimpleFunction;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;

import com.google.cloud.bigtable.beam.CloudBigtableScanConfiguration;
import com.google.cloud.bigtable.beam.CloudBigtableIO;

import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

import org.apache.beam.sdk.util.CoderUtils;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.PrefixFilter;

public class Schema {
  /** A SimpleFunction that converts a Word and Count into a printable string. */
  public static class FormatAsTextFn 
  extends SimpleFunction<KV<String, Long>, String> {
    @Override
    public String apply(KV<String, Long> input) {
      return input.getKey() + ": " + input.getValue();
    }
  }
  
  public static class ParseResultToString 
  extends SimpleFunction<Result, String> {
    @Override
    public String apply(Result input) {
      return Bytes.toString(input.getRow()) + Bytes.toString(input.value()) + ": " + input.toString();
    }
  }
  
  static class Decodify 
  extends DoFn<Result, LoadCurveWithEnumValue> {
    @ProcessElement
    public void processElement(@Element Result element, OutputReceiver<LoadCurveWithEnumValue> receiver) throws CoderException {
      // Coder<LoadCurveWithEnumValue>
      // Input: Result element 
      // Return: type: LoadCurveWithEnumValue 
      LoadCurveWithEnumValue help = CoderUtils.decodeFromByteArray(AvroCoder.of(LoadCurveWithEnumValue.class), element.getRow()) ;
      receiver.output( help );
    }
  }
  
  /** Pipeline default */
  public interface WordCountOptions 
  extends PipelineOptions {
    
    /**
    * By default, this example reads from a public dataset containing the text of King Lear. Set
    * this option to choose a different input file or glob.
    */
    @Description("Path of the file to read from")
    @Default.String("gs://apache-beam-samples/shakespeare/kinglear.txt")
    String getInputFile();
    
    void setInputFile(String value);
    
    /** Set this required option to specify where to write the output. */
    @Description("Path of the file to write to")
    @Default.String("outs")
    String getOutput();
    
    void setOutput(String value);
  }
  
  static void runPipeline(WordCountOptions options) {
    Pipeline p = Pipeline.create(options);
    
    Scan scan = new Scan();
    scan.setCacheBlocks(false);
    String s = "008fd25982229fb41a530ee930a5c267#2021-06-16T22:45:00+00:00";
    byte[] b = Bytes.toBytes(s);
    scan.setFilter(new PrefixFilter(b));
    
    PCollection<LoadCurveWithEnumValue>  bigTableEntries = p.apply("ReadBigTable", 
    Read.from(CloudBigtableIO.read(
    new CloudBigtableScanConfiguration.Builder()
    .withProjectId("ewx-acn")
    .withInstanceId("veeinstance-hdd")
    .withTableId("myloadcurves_avro_value_enum_null")
    .withScan(scan)
    .build())))
  
    .apply("Codify", ParDo.of( new Decodify()));
    // print results from table:
    //.apply("ParseToString" , MapElements.via(new ParseResultToString()));
    //bigTableEntries.apply("WRITETOFILE", TextIO.write().to(options.getOutput()));
    
    p.run().waitUntilFinish();
  }
  
  public static void main(String[] args) {
    WordCountOptions options =
    PipelineOptionsFactory.fromArgs(args).withValidation().as(WordCountOptions.class);
    runPipeline(options);
  }
}
