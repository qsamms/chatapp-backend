package com.chatappbackend.dto.rooms;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomsRequest {
  @Pattern(regexp = "^(invited|accepted)$", message = "Invalid option")
  private String type;
}
