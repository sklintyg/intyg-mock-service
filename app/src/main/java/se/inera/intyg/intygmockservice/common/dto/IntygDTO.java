package se.inera.intyg.intygmockservice.common.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class IntygDTO {

    private IntygsId intygsId;
    private Typ typ;
    private LocalDateTime signeringstidpunkt;
    private LocalDateTime skickatTidpunkt;
    private List<Relation> relation;
    private String version;
    private PatientDTO patient;
    private HoSPersonalDTO skapadAv;
    private List<Svar> svar;

    @Data
    public static class IntygsId {

        private String root;
        private String extension;
    }

    @Data
    public static class Typ {

        private String code;
        private String codeSystem;
        private String displayName;
    }

    @Data
    public static class Relation {

        private TypAvRelation typ;
        private IntygsId intygsId;

        @Data
        public static class TypAvRelation {

            private String code;
            private String codeSystem;
            private String displayName;
        }
    }

    @Data
    public static class Svar {

        private String id;
        private String instans;
        private List<Delsvar> delsvar;

        @Data
        public static class Delsvar {

            private String id;
            private Cv cv;
            private String value;
            private DatePeriod datePeriod;

            @Data
            public static class Cv {

                private String code;
                private String codeSystem;
                private String displayName;
            }

            @Data
            public static class DatePeriod {

                private LocalDate start;
                private LocalDate end;
            }
        }
    }
}
