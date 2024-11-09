package se.inera.intyg.intygmockservice.common.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class IntygDTO {

    private IntygsId intygsId;
    private CodeTypeDTO typ;
    private LocalDateTime signeringstidpunkt;
    private LocalDateTime skickatTidpunkt;
    private List<RelationDTO> relation;
    private String version;
    private PatientDTO patient;
    private HoSPersonalDTO skapadAv;
    private List<SvarDTO> svar;

    @Data
    public static class IntygsId {

        private String root;
        private String extension;
    }

    @Data
    public static class RelationDTO {

        private CodeTypeDTO typ;
        private IntygsId intygsId;
    }

    @Data
    public static class SvarDTO {

        private String id;
        private String instans;
        private List<DelsvarDTO> delsvar;

        @Data
        public static class DelsvarDTO {

            private String id;
            private CodeTypeDTO cv;
            private String value;
            private DatePeriod datePeriod;

            @Data
            public static class DatePeriod {

                private LocalDate start;
                private LocalDate end;
            }
        }
    }
}
