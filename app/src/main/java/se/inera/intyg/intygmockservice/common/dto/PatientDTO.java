package se.inera.intyg.intygmockservice.common.dto;

import lombok.Data;

@Data
public class PatientDTO {

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
