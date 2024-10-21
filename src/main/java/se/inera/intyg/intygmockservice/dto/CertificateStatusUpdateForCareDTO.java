package se.inera.intyg.intygmockservice.dto;

import lombok.Data;

@Data
public class CertificateStatusUpdateForCareDTO {

    private Intyg intyg;
    private Handelse handelse;
    private Fragor skickadeFragor;
    private Fragor mottagnaFragor;
    private HanteratAv hanteratAv;

    @Data
    public static class Intyg {

        private IntygsId intygsId;
        private Typ typ;
        private String version;
        private Patient patient;
        private SkapadAv skapadAv;

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
    }

    @Data
    public static class Handelse {

        private Handelsekod handelsekod;
        private String tidpunkt;

        @Data
        public static class Handelsekod {

            private String code;
            private String codeSystem;
            private String displayName;
        }
    }

    @Data
    public static class Fragor {

        private int totalt;
        private int ejBesvarade;
        private int besvarade;
        private int hanterade;
    }

    @Data
    public static class HanteratAv {

        private String root;
        private String extension;
    }
}