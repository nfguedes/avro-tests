package org.accenture;

import javax.naming.spi.DirStateFactory.Result;

import org.accenture.common.ExampleUtils;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.metrics.Counter;
import org.apache.beam.sdk.metrics.Distribution;
import org.apache.beam.sdk.metrics.Metrics;
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

public class Schema1 {
  
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
      return input.toString();
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
    
    PCollection<KV<String, Long>>  bigTableEntries = p.apply("ReadBigTable", 
    Read.from(CloudBigtableIO.read(
    new CloudBigtableScanConfiguration.Builder()
    .withProjectId("ewx-acn")
    .withInstanceId("veeinstance-hdd")
    .withTableId("myloadcurves_avro_value_enum_null")
    .build())));

    // print results from table:
    //.apply("ParseToString" , MapElements.via(new ParseResultToString()))
    //.apply("WRITETOFILE", TextIO.write().to(options.getOutput()));

    p.run().waitUntilFinish();
  }
  
  public static void main(String[] args) {
    WordCountOptions options =
    PipelineOptionsFactory.fromArgs(args).withValidation().as(WordCountOptions.class);
    runPipeline(options);
  }
}
