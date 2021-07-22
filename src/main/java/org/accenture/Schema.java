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

import java.io.IOException;

import com.google.cloud.bigtable.beam.CloudBigtableIO;

import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

import org.apache.beam.sdk.util.CoderUtils;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.PrefixFilter;

import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.TableName;

import com.google.cloud.bigtable.hbase.BigtableConfiguration;
import org.apache.beam.sdk.transforms.Create;

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
      byte[] family =Bytes.toBytes("LC") ;
      byte [] qualifier = Bytes.toBytes("AC");
      return Bytes.toString(input.getValue(family,qualifier));
      
    }
  }
  
  public static class ParseByteArrayToString
  extends SimpleFunction<LoadCurveWithEnumValueBlueprint, String> {
    @Override
    public String apply(LoadCurveWithEnumValueBlueprint input) {
      return input.toString();
    }
  }
  
  static class Decodify 
  extends DoFn<Result, LoadCurveWithEnumValueBlueprint> {
    @ProcessElement
    public void processElement(@Element Result element, OutputReceiver<LoadCurveWithEnumValueBlueprint> receiver) throws CoderException {
      byte[] family =Bytes.toBytes("LC") ;
      byte [] qualifier = Bytes.toBytes("AC");
      LoadCurveWithEnumValueRemoveClassTou help2 =  CoderUtils.decodeFromByteArray(AvroCoder.of(LoadCurveWithEnumValueRemoveClassTou.class), 
      element.getValue(family,qualifier));
      LoadCurveWithEnumValueBlueprint help =  CoderUtils.decodeFromByteArray(AvroCoder.of(LoadCurveWithEnumValueBlueprint.class), 
      element.getValue(family,qualifier));
      
      System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n" + help2.equals(help));
      System.out.println("\n\n\n" + help.toString());
      System.out.println(help2.toString());
      receiver.output(help);
    }
  }

  /** Pipeline default */
  public interface SchemaOptions 
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
  
  static void runPipeline(SchemaOptions options) throws IOException {
    Pipeline p = Pipeline.create(options);
    
    byte[] TABLE_NAME = Bytes.toBytes("myloadcurves_avro_value_enum_null");
    Connection connection = BigtableConfiguration.connect("ewx-acn", "veeinstance-hdd");
    Table table = connection.getTable(TableName.valueOf(TABLE_NAME));
    String rowKey =  "00000001b152a78bb637d4c6aed23081#2020-11-20T05:45:00+00:00";
    Result getResult  = table.get(new Get(Bytes.toBytes(rowKey)));
    
    PCollection<Result> bigTableEntries = p.apply(Create.of(getResult));
    
    PCollection<LoadCurveWithEnumValueBlueprint> decodedEntries = bigTableEntries.apply("Decode", ParDo.of( new Decodify()));

    // print results from table:
    //bigTableEntries.apply("ParseToString" , MapElements.via(new ParseResultToString()))
    //decodedEntries.apply("ParseToString" , MapElements.via(new ParseByteArrayToString()))
    //.apply("WRITETOFILE", TextIO.write().to(options.getOutput()));
    
    p.run().waitUntilFinish();
  }
  
  
  public static void main(String[] args) throws IOException {
    SchemaOptions options =
    PipelineOptionsFactory.fromArgs(args).withValidation().as(SchemaOptions.class);
    runPipeline(options);
  }
}

// Sugestions to compare LoadCurveWithEnumValueBlueprint
// --> Equals (Criar classe pai  e extender ambas!)
// --> JUNIT - Passert

