package com.simle.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description
 * @ClassName AbstractResponseMessage
 * @Author smile
 * @date 2022.03.05 21:37
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractResponseMessage extends Message {
    private boolean success;
    private String reason;
}
