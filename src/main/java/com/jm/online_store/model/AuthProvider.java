package com.jm.online_store.model;

import io.swagger.annotations.ApiModel;

@ApiModel(description =  "Enum провайдеров")
public enum AuthProvider {
    LOCAL,
    FACEBOOK,
    GOOGLE,
    OK,
    VK
}
