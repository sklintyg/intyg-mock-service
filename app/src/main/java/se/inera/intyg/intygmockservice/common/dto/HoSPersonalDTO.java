package se.inera.intyg.intygmockservice.common.dto;

import java.util.List;
import lombok.Data;

@Data
public class HoSPersonalDTO {

    private PersonalIdDTO personalId;
    private String fullstandigtNamn;
    private String forskrivarkod;
    private List<CodeTypeDTO> befattning;
    private EnhetDTO enhet;

    @Data
    public static class PersonalIdDTO {

        private String root;
        private String extension;
    }

    @Data
    public static class EnhetDTO {

        private HsaIdDTO enhetsId;
        private ArbetsplatskodDTO arbetsplatskod;
        private String enhetsnamn;
        private String postadress;
        private String postnummer;
        private String postort;
        private String telefonnummer;
        private String epost;
        private VardgivareDTO vardgivare;

        @Data
        public static class ArbetsplatskodDTO {

            private String root;
            private String extension;
        }

        @Data
        public static class VardgivareDTO {

            private HsaIdDTO vardgivareId;
            private String vardgivarnamn;
        }

        @Data
        public static class HsaIdDTO {

            private String root;
            private String extension;
        }
    }
}
