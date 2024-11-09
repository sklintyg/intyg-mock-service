package se.inera.intyg.intygmockservice.common.dto;

import lombok.Data;

@Data
public class CodeTypeDTO {

    private String code;
    private String codeSystem;
    private String displayName;
}