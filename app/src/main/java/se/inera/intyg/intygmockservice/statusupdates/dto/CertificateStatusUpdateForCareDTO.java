package se.inera.intyg.intygmockservice.statusupdates.dto;

import lombok.Data;
import se.inera.intyg.intygmockservice.common.dto.IntygDTO;

@Data
public class CertificateStatusUpdateForCareDTO {

    private IntygDTO intyg;
    private Handelse handelse;
    private Fragor skickadeFragor;
    private Fragor mottagnaFragor;
    private HanteratAv hanteratAv;

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