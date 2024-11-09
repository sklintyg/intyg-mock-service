package se.inera.intyg.intygmockservice.common.dto;

import lombok.Data;

@Data
public class HoSPersonalDTO {

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

        private Enhet.EnhetsId enhetsId;
        private Enhet.Arbetsplatskod arbetsplatskod;
        private String enhetsnamn;
        private String postadress;
        private String postnummer;
        private String postort;
        private String telefonnummer;
        private String epost;
        private Enhet.Vardgivare vardgivare;

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

            private Enhet.Vardgivare.VardgivareId vardgivareId;
            private String vardgivarnamn;

            @Data
            public static class VardgivareId {

                private String root;
                private String extension;
            }
        }
    }
}