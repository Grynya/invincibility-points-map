package com.invincibilitypoints.invincibilitypointsmap.service;

import com.invincibilitypoints.invincibilitypointsmap.dto.KeysDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class KeysService {

    @Value("${google_client_id}")
    private String googleClientId;

    @Value("${mapbox_access_token}")
    private String mapboxAccessToken;

    public KeysDto getKeys (){
        return new KeysDto(googleClientId, mapboxAccessToken);
    }
}
