package org.accenture;

import java.util.HashMap;
import java.util.Map;

import org.apache.avro.reflect.Nullable;
import org.apache.beam.sdk.coders.AvroCoder;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.coders.DefaultCoder;
import org.apache.beam.sdk.coders.SnappyCoder;
import org.apache.beam.sdk.util.CoderUtils;

@DefaultCoder(AvroCoder.class)
public class LoadCurveWithEnumValue {
  private enum ClassTou {
    A_mais_CONSUMO_VAZIO,
    A_mais_CONSUMO_SUPER_VAZIO,
    A_mais_CONSUMO_PONTA,
    A_mais_CONSUMO_CHEIAS,
    A_menos_PRODUCAO_VAZIO,
    A_menos_PRODUCAO_SUPER_VAZIO,
    A_menos_PRODUCAO_PONTA,
    A_menos_PRODUCAO_CHEIAS
  }
  
  private enum EstRule {
    No_estimation_rule_was_applied,
    estimation_rule_1_power_down_status,
    estimation_rule_1_unable_to_be_estimated_due_to_lack_of_history,
    estimation_rule_2_one_missing_period,
    estimation_rule_2_unable_to_be_estimated_due_to_lack_of_history,
    estimation_rule_3_two_to_twelve_missing_periods_known_read,
    estimation_rule_3_unable_to_be_estimated_due_to_lack_of_history,
    estimation_rule_4_two_to_twelve_missing_periods_unknown_read,
    estimation_rule_4_unable_to_be_estimated_due_to_lack_of_history,
    estimation_rule_5_more_than_twelve_missing_periods_known_read,
    estimation_rule_5_unable_to_be_estimated_due_to_lack_of_history,
    estimation_rule_6_more_than_twelve_missing_periods_unknown_read_with_homologous_period,
    estimation_rule_6_unable_to_be_estimated_due_to_lack_of_history,
    estimation_rule_7_more_than_twelve_missing_periods_unknown_read_without_homologous_period,
    estimation_rule_7_unable_to_be_estimated_due_to_lack_of_history
  }
  
  private enum Status {
    Valido,
    Suspeito,
    Invalido,
    Inativo
  }
  
  private enum SubStatus {
    calculado,
    estimado,
    editado,
    editado_com_estimativa
  }
  
  @Nullable private float consumption;
  @Nullable private float originalConsumption;
  @Nullable private ClassTou classTou;
  @Nullable private EstRule estRule;
  @Nullable private Status statusLoadCurve;
  @Nullable private SubStatus substatusLoadCurve;
  @Nullable private Status checkPast;
  @Nullable private Status checkFutu;
  @Nullable private Status checkDigits;
  @Nullable private Status compareSumOfLC;
  @Nullable private Status checkStatus;
  @Nullable private Status compareConsumptionMax;
  @Nullable private Status compareConsumptionMin;
  @Nullable private Status valdateNightProd;
  @Nullable private Status validateReadingStatus;
  
  public LoadCurveWithEnumValue() {}
}
