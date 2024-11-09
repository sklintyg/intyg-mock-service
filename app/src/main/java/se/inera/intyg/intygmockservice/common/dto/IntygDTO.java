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
    private Patient patient;
    private SkapadAv skapadAv;
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
    public static class Patient {

        private PersonId personId;
        private String fornamn;
        private String efternamn;
        private String postadress;
        private String postnummer;
        private String postort;

        @Data
        public static class PersonId {

            private String root;
            private String extension;
        }
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
    public static class SkapadAv {

        private PersonalId personalId;
        private String fullstandigtNamn;
        private String forskrivarkod;
        private Befattning befattning;
        private Enhet enhet;

        @Data
        public static class PersonalId {

            private String root;
            private String extension;
        }

        @Data
        public static class Befattning {

            private String code;
            private String codeSystem;
            private String displayName;
        }

        @Data
        public static class Enhet {

            private EnhetsId enhetsId;
            private Arbetsplatskod arbetsplatskod;
            private String enhetsnamn;
            private String postadress;
            private String postnummer;
            private String postort;
            private String telefonnummer;
            private String epost;
            private Vardgivare vardgivare;

            @Data
            public static class EnhetsId {

                private String root;
                private String extension;
            }

            @Data
            public static class Arbetsplatskod {

                private String root;
                private String extension;
            }

            @Data
            public static class Vardgivare {

                private VardgivareId vardgivareId;
                private String vardgivarnamn;

                @Data
                public static class VardgivareId {

                    private String root;
                    private String extension;
                }
            }
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
