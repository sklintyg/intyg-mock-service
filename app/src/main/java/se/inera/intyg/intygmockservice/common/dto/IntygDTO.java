package se.inera.intyg.intygmockservice.common.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class IntygDTO {

  IntygsId intygsId;
  CodeTypeDTO typ;
  LocalDateTime signeringstidpunkt;
  LocalDateTime skickatTidpunkt;
  List<RelationDTO> relation;
  String version;
  PatientDTO patient;
  HoSPersonalDTO skapadAv;
  List<SvarDTO> svar;

  @Value
  @Builder
  public static class IntygsId {

    String root;
    String extension;
  }

  @Value
  @Builder
  public static class RelationDTO {

    CodeTypeDTO typ;
    IntygsId intygsId;
  }

  @Value
  @Builder
  public static class SvarDTO {

    String id;
    String instans;
    List<DelsvarDTO> delsvar;

    @Value
    @Builder
    public static class DelsvarDTO {

      String id;
      CodeTypeDTO cv;
      String value;
      DatePeriod datePeriod;

      @Value
      @Builder
      public static class DatePeriod {

        LocalDate start;
        LocalDate end;
      }
    }
  }
}
