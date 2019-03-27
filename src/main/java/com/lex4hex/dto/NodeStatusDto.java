package com.lex4hex.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public
class NodeStatusDto {
    Integer owner;
    Integer backup;
}
